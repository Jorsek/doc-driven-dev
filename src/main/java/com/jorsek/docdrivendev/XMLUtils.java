package com.jorsek.docdrivendev;

import net.sf.saxon.lib.NamespaceConstant;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

/**
 * User: Casey
 * Date: 10/7/13
 * Time: 8:26 PM
 */
public class XMLUtils {


    public static XPathExpression getExpr(String xpath) throws XPathExpressionException {
        System.setProperty("javax.xml.xpath.XPathFactory:"+ NamespaceConstant.OBJECT_MODEL_SAXON, "net.sf.saxon.xpath.XPathFactoryImpl");
        try {
            XPathFactory xpf = XPathFactory.newInstance(NamespaceConstant.OBJECT_MODEL_SAXON);

            XPathExpression expr = xpf.newXPath().compile(xpath);

            return expr;
        } catch (XPathFactoryConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        throw new RuntimeException("Could not compile XPath expression: "+ xpath);
    }
}
