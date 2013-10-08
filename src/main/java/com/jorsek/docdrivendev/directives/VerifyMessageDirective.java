package com.jorsek.docdrivendev.directives;

/**
 * User: Casey
 * Date: 10/7/13
 * Time: 11:42 PM
 */
public class VerifyMessageDirective implements Directive {
    private final String messageToFind;

    public VerifyMessageDirective(String messageToFind){
        this.messageToFind = messageToFind;
    }

    public String getMessageToFind(){
        return messageToFind;
    }
}
