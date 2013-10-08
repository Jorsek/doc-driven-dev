package com.jorsek.docdrivendev.directives;

import javax.xml.xpath.XPathExpression;

/**
 * User: Casey
 * Date: 10/7/13
 * Time: 8:11 PM
 */
public abstract class AbstractHasElementDirective {


    private String elementFinderXPath;

    public AbstractHasElementDirective(String elementFinderXPath){

        this.elementFinderXPath = elementFinderXPath;

    }


    public String getElementFinderXPath() {
        return elementFinderXPath;
    }

}
