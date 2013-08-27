package org.owasp.wikitestcases.utils;

import org.w3c.dom.Document;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathExpressionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XPathSearch {

    private final static Logger logger = LoggerFactory.getLogger(XPathSearch.class);

    public static String document(Document doc, String expression) {
        String results = "";
        // this is XXE-able :)
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        try {
            XPathExpression expr = xpath.compile(expression);
            results = expr.evaluate(doc);
        }
        catch (XPathExpressionException e) {
            logger.info(e.getMessage());
        }
        finally {
            return results;
        }
    }
}