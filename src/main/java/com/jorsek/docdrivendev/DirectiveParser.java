package com.jorsek.docdrivendev;

import com.jorsek.docdrivendev.directives.ClickDirective;
import com.jorsek.docdrivendev.directives.Directive;
import com.jorsek.docdrivendev.directives.SendTextDirective;
import com.jorsek.docdrivendev.directives.VerifyMessageDirective;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Casey
 * Date: 10/7/13
 * Time: 7:55 PM
 */
public class DirectiveParser {


    public static Map<String,Action> actionFinders = new HashMap<String, Action>();


    static {

        actionFinders.put("not(empty(preceding-sibling::node()[matches(., '(Click|Select)')]))", Action.CLICK);
        actionFinders.put("@testval", Action.SEND_TEXT);

    }

    private static String genSeekUIByRegex(String regex){
        return "";
    }

    public List<Directive> parse(Element searchIn){

        if(searchIn.getTagName().equals("cmd"))
            return parseUIControl(searchIn);
        else if(searchIn.getTagName().equals("result"))
            return parseResult(searchIn);

        return new ArrayList<Directive>();
    }

    private List<Directive> parseResult(Element searchIn) {
        List<Directive> directives = new ArrayList<Directive>();

        try {
            NodeList messages = (NodeList) XMLUtils.getExpr(".//msgph").evaluate(searchIn, XPathConstants.NODESET);
            for(int i = 0; i < messages.getLength();i++){
                directives.add(new VerifyMessageDirective(messages.item(i).getTextContent().trim()));
            }
        } catch (XPathExpressionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return directives;
    }

    private List<Directive> parseUIControl(Element searchIn) {
        List<Directive> directives = new ArrayList<Directive>();

        /* First we get all the uicontrols because that is the root of a user action */
        try {

            NodeList uiControls = (NodeList) XMLUtils.getExpr((".//uicontrol")).evaluate(searchIn, XPathConstants.NODESET);

            Action foundAction;
            Element uiControl;
            for(int i = 0; i < uiControls.getLength();i++){
                uiControl = (Element)uiControls.item(i);
                foundAction = attemptToFindAction(uiControl);

                switch(foundAction){

                    case CLICK:

                        directives.add(generateClickDirective(uiControl));
                        break;
                    case SEND_TEXT:

                        directives.add(generateSendTextDirective(uiControl));
                        break;
                    default:

                        break;
                }
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return directives;
    }

    private Directive generateSendTextDirective(Element uiControl) {
        String controlID = uiControl.getAttribute("id");

        String xpathFinder = "//input[(//label[normalize-space(text()) = '"+uiControl.getTextContent().trim()+"']/@for = ./@name) or @id = '"+controlID+"']";
        return new SendTextDirective(xpathFinder, uiControl.getAttribute("testval"));

    }

    private String createElementFinderXPath(Element uiControl) {

        String startClause = "//*[";
        List<String> clauses = new ArrayList<String>();

        if(uiControl.hasAttribute("id")){
            clauses.add("@id = '"+uiControl.getAttribute("id")+"']");
        }

        String text = uiControl.getTextContent().trim();
        if(!text.isEmpty())
            clauses.add("normalize-space(text()) = '"+text+"'");

        return "//*["+ StringUtils.join(clauses.toArray(new String[clauses.size()]), " and ")+"][last()]";
    }

    private ClickDirective generateClickDirective(Element uiControl) {
        return new ClickDirective(createElementFinderXPath(uiControl));
    }

    private Action attemptToFindAction(Element uiControl) throws XPathExpressionException {
        for(String xPath : actionFinders.keySet()){
            if((Boolean)XMLUtils.getExpr(xPath).evaluate(uiControl, XPathConstants.BOOLEAN)) {
                return actionFinders.get(xPath);
            }
        }

        return Action.CLICK;
    }
}
