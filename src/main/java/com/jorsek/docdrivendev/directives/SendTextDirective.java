package com.jorsek.docdrivendev.directives;

/**
 * User: Casey
 * Date: 10/7/13
 * Time: 11:10 PM
 */
public class SendTextDirective extends AbstractHasElementDirective implements Directive,HasElementDirective {
    private String textPayload;

    public SendTextDirective(String elementFinderXPath, String textPayload) {
        super(elementFinderXPath);

        this.textPayload = textPayload;
    }

    public String getPayload(){
        return textPayload;
    }
}
