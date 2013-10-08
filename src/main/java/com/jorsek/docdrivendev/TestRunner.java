
package com.jorsek.docdrivendev;

import com.jorsek.docdrivendev.directives.*;
import junit.framework.TestCase;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;

// There's a whole lotta WebElement .... findElement .. By here that should be unnecessary
// I've just learned about driver.findelement("locator").sendKeys("Keys.Enter") or ("Keys.Tab")
// ... so let's get rid of some of it
// Also, you'll see heavy reliance on xpath rather than DOM. It's not because I am comfortable in 
// xpath -- I'm not -- it's because these pages are <div> all the way down.





public class TestRunner extends TestCase {

    public static Document documentation;



    public static void loadDocument() throws ParserConfigurationException, IOException, SAXException {

        InputStream in = null;
        try{
            in = TestRunner.class.getResourceAsStream("ChangePassword.dita");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            DocumentBuilder builder = factory.newDocumentBuilder();

            /* Ignore dtd, our files are already parsed by easyDITA */
            builder.setEntityResolver(new EntityResolver() {

                @Override
                public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
                    if (systemId.endsWith(".dtd")) {
                        StringReader stringInput = new StringReader(" ");
                        return new InputSource(stringInput);
                    }
                    else {
                        return null; // use default behavior
                    }
                }
            });
            documentation = builder.parse(in);

        }finally {
            if(in != null)
                in.close();
        }
    }



    public static NodeList parseSteps() throws XPathExpressionException {

        return (NodeList) XMLUtils.getExpr("//*[contains(@class, ' task/cmd ')]").evaluate(documentation, XPathConstants.NODESET);
    }



    @Test
    public void testTwitter() throws InterruptedException {


        try {
            loadDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // Create a new instance of the Firefox driver
        // Notice that the remainder of the code relies on the interface, 
        // not the implementation.
        WebDriver driver = new FirefoxDriver();

        // And now use this to visit Twitter
        driver.get("https://twitter.com");
        // Alternatively the same thing can be done like this
        // driver.navigate().to("https://twitter.com");
        // should open the sign-in page

        // Could be slow ...
        WebElement waitForPageLoad = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'submit btn primary-btn flex-table-btn js-submit']")));


        // Are you signed in auto? No?
        WebElement signInNeeded = driver.findElement(By.xpath("//button[@class = 'submit btn primary-btn flex-table-btn js-submit']"));
        // This is OK for confirming the page, but could be dangerous for input -- there are a couple


        if (signInNeeded != null) {


            signIn(driver);

        } else {

            // We're signed into the account and on our Home (feed) page, so change the password
            changePassword(driver);
        }


    };


    private static void signIn (WebDriver driver) throws InterruptedException {

        //  System.out.println("arrived at the sign-in page - no auto-sign-in");

        // Could be slow ... look for the sign-in button because other stuff changes
        // <button class="submit btn primary-btn flex-table-btn js-submit" type="submit">
        WebElement waitForPageLoad = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@class = 'submit btn primary-btn flex-table-btn js-submit']")));



        // Find the User or Email box and type the Email
        WebElement emailInput = driver.findElement(By.xpath("//label[@for = 'signin-email']"));
        emailInput.sendKeys("LavaconTester");

        // Find the password box, type it
        WebElement passwordInput = driver.findElement(By.xpath("//label[@for = 'signin-password']"));
        passwordInput.sendKeys("easyDITA2");

        // Find the Sign in button, click it
        WebElement signInButton = driver.findElement(By.xpath("//button[@class = 'submit btn primary-btn flex-table-btn js-submit']"));
        signInButton.click();

        // Wait for page load?

        // change the password
        changePassword(driver);

        Thread.sleep(4000);
        driver.close();
    }

    private static void changePassword (WebDriver driver) throws InterruptedException {

        DirectiveParser directiveParser = new DirectiveParser();

        try {
            NodeList steps = parseSteps();
            System.out.println(steps.getLength() + " steps found");
            for(int i = 0 ; i < steps.getLength();i++) {
                processDirectives(driver, directiveParser.parse((Element)steps.item(i)));
            }

        } catch (XPathExpressionException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


//        //   System.out.println("changing password");
//
//        // Find the gear icon, and click it
//        WebElement settingsIcon = driver.findElement(By.id("user-dropdown-toggle"));
//        settingsIcon.click();
//
//        // Find the settings, click it
//        // <a class="js-nav" data-nav="settings" href="/settings/account"
//        WebElement settingsSelection = driver.findElement(By.xpath("//a[@href= '/settings/account' ]"));
//        settingsSelection.click();
//
//
//        // Wait for page load? Seems to be needed
//        WebElement waitForPageLoad = (new WebDriverWait(driver, 5))
//                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@href = '/settings/password']")));
//
//
//        // Select Password in list, click it
//        // <a class="list-link js-nav" data-nav="password" href="/settings/password">
//        WebElement passwordSelection = driver.findElement(By.xpath("//a[@href = '/settings/password']"));
//        passwordSelection.click();
//
//        // Wait for it for the dialog
//        WebElement waitForDialogLoad = (new WebDriverWait(driver, 5))
//                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@id = 'current_password']")));
//
//        // Find the Current Password box, type it in
//        // <input id="current_password" type="password" name="current_password">
//        WebElement currentPassword = driver.findElement(By.xpath("//input[@id = 'current_password']"));
//        currentPassword.click();
//
//        // What's the easy way to increment the password for repeated use of this?
//        // NOT NEEDED. Twitter let's you change to the same string
//
//
//        // type the current password into the box [easyDITA2]
//        currentPassword.sendKeys("easyDITA2");
//
//
//        // Find (or Tab to?) the New Password box and re-type the same password
//        // <input id="user_password" type="password" name="user_password">
//        WebElement newPassword = driver.findElement(By.xpath("//input[@id = 'user_password']"));
//        newPassword.sendKeys("easyDITA2");
//
//
//        // Find (or Tab to?) the Verify Password box
//        // <input id="user_password_confirmation" type="password" name="user_password_confirmation">
//        WebElement confirmNewPassword = driver.findElement(By.xpath("//input[@id = 'user_password_confirmation']"));
//        confirmNewPassword.sendKeys("easyDITA2");
//
//
//        // Find the Save Changes button
//        // <button id="settings_save" class="btn primary-btn" type="submit" disabled="disabled">
//        WebElement saveNewSettings = driver.findElement(By.xpath("//button[@id = 'settings_save']"));
//        saveNewSettings.click();
//
//        // Wait for page?
//        //   <h1 class="heading">Woo hoo! Your password has been changed!</h1>
//        WebElement waitForWooHooPageLoad = (new WebDriverWait(driver, 10))
//                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//h1[@class = 'heading']")));
//
//
//
//        // In the new page, find Go to the homepage
//        // <a class=" js-nav" href="/"> Ö </a>
//
//        WebElement goToHomepage = driver.findElement(By.xpath("//a[@class = ' js-nav']"));
//        goToHomepage.click();

    }

    private static void processDirectives(WebDriver driver, List<Directive> directives) throws InterruptedException {

        for(Directive directive : directives){
            processDirective(driver, directive);

        }

    }

    private static void processDirective(WebDriver driver, Directive direc) throws InterruptedException {

        Thread.sleep(1000);
        if(direc instanceof HasElementDirective) {
            HasElementDirective directive = (HasElementDirective)direc;

            By byXPath = By.xpath(directive.getElementFinderXPath());
            WebElement e = (new WebDriverWait(driver, 5)).until(ExpectedConditions.elementToBeClickable(byXPath));

            System.out.println("TEXT: " + e.getText());
            System.out.println("Tag: " + e.getTagName());
            if(directive instanceof ClickDirective){

                Locatable hoverItem = (Locatable) e;
                Mouse mouse = ((HasInputDevices) driver).getMouse();
                mouse.mouseMove(hoverItem.getCoordinates());
                Thread.sleep(500);
                e.click();

            } else if (directive instanceof SendTextDirective){
                e.sendKeys(((SendTextDirective) directive).getPayload());
            }
        }else if(direc instanceof VerifyMessageDirective){
            WebElement e = null;
            try {
                String message = ((VerifyMessageDirective) direc).getMessageToFind();
                e = (new WebDriverWait(driver, 5)).until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(., '" + message + "')]")));

            }finally{
                    assertNotNull(e);
            }
        }
    }


}
       
 
