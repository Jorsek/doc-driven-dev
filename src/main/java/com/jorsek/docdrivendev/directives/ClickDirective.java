package com.jorsek.docdrivendev.directives;

/**
 * User: Casey
 * Date: 10/7/13
 * Time: 8:03 PM
 */
public class ClickDirective extends AbstractHasElementDirective implements Directive,HasElementDirective {

    public ClickDirective(String elementFinderXPath) {
        super(elementFinderXPath);
    }
}
