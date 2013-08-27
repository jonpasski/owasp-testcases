package org.owasp.wikitestcases;

import java.io.File;
import java.io.IOException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException; // catching unsupported features

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.owasp.wikitestcases.utils.XPathSearch;

public class DocumentBuilderFactoryTest extends BaseTest {

    private static String externalEntity;
    private DocumentBuilderFactory factory;
    public static final String DOES_NOT_EXIST = "external-entity-doesnotexist.xml";
    public String DISALLOW_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    public String EXTERNAL_FEATURE = "http://xml.org/sax/features/external-general-entities";


    @Before
    public void loadExternalEntity() {
        factory = DocumentBuilderFactory.newInstance();
        try {
            externalEntity = getClass().getClassLoader().getResource(DOES_NOT_EXIST).getFile();
        }
        catch (NullPointerException e) {
            logger.info("Could not load " + DOES_NOT_EXIST + " file, aborting test.");
            Assert.assertTrue(false);
        }
    }

    @Test
    public void disallowDoctypeDecl() {

        try {
            factory.setFeature(EXTERNAL_FEATURE, true);  // force allow external entities
            factory.setFeature(DISALLOW_FEATURE, true);  // testing disallow
            String output = parseDoc(factory);

            // Shouldn't get here!
            logger.info("Doc: " + output);
            Assert.assertTrue(false);
        }
        catch (ParserConfigurationException e) {
            // This should catch a failed setFeature feature
            logger.info("ParserConfigurationException was thrown. The feature '" +
                        DISALLOW_FEATURE +
                        "' is probably not supported by your XML processor.");
            Assert.assertTrue(false);
        }
        catch (SAXException e) {
            // On Apache, this should be thrown
            logger.info("Test is correct, ignore Fatal Error message: " + e.getMessage());
        }
        catch (IOException e) {
            // XXE that points to a file that doesn't exist
            logger.info("IOException occurred, XXE may still possible: " + e.getMessage());
            Assert.assertTrue(false);
        }
    }

    @Test
    public void disallowExternalGeneralEntities() {

        try {
            factory.setFeature(DISALLOW_FEATURE, false);  // force allow doctype
            factory.setFeature(EXTERNAL_FEATURE, false);  // testing disabling external
            String output = parseDoc(factory);

            // This feature won't throw a fatal error / exception
            if (output.contains(new String("/etc/doesnotexist"))) {
                Assert.assertTrue(false);
            }
        }
        catch (ParserConfigurationException e) {
            // This should catch a failed setFeature feature
            logger.info("ParserConfigurationException was thrown. The feature '" +
                        EXTERNAL_FEATURE +
                        "' is probably not supported by your XML processor.");
            Assert.assertTrue(false);
        }
        catch (SAXException e) {
            logger.info("SAXException: " + e.getMessage());
            Assert.assertTrue(false);
        }
        catch (IOException e) {
            // XXE that points to a file that doesn't exist
            logger.info("IOException occurred, XXE still possible: " + e.getMessage());
            Assert.assertTrue(false);
        }
    }

    private static String parseDoc(DocumentBuilderFactory factory) 
        throws IOException, ParserConfigurationException, SAXException {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(externalEntity));
            return XPathSearch.document(doc, "/foo/text()");
    }

}