package net.remarkable.tests.checkout;

import net.remarkable.Logger;
import net.remarkable.SendMessages;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MossBrosTest {

    private static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        FirefoxProfile profile = new FirefoxProfile();
        profile.setPreference("browser.private.browsing.autostart", true);
        driver = new FirefoxDriver(profile);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    @Test
    public void mossHire() throws IOException, MessagingException {

        String configName = "MossBrosHire";
        String testName = "Moss Bros Hire Checkout";

        Config setUp = new Config(configName, testName, driver);
        Logger log = setUp.getLog();
        SendMessages msg = setUp.getMsg();
        String cardError = setUp.getError();

        log.add("Starting test", false);

		WebDriverWait wait = new WebDriverWait(driver, 20);

        try {

            driver.get("http://www.mossbroshire.co.uk/");
            log.add("Go to site", true);

            driver.findElement(By.cssSelector(("#weddingPromo > div > a"))).click();
            log.add("Start planning", true);

            // Event Details
            driver.findElement(By.id("FunctionName")).sendKeys("Checkout Tester");
            log.add("Enter function name", true);

            Select numberInGroup = new Select(driver.findElement(By.id("NumberInGroup")));
            numberInGroup.selectByVisibleText("1");
            log.add("Select number in group", true);

            //Wait for Your Account to open
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("collapseAccount"))));

            driver.findElements(By.className("material-checkbox--for-panel")).get(1).click();
            log.add("Login to existing account", true);

            driver.findElement(By.id("LoginEmailAddress")).sendKeys("checkouttester@remarkable.net");
            log.add("Enter username", true);

            driver.findElement(By.id("LoginPassword")).sendKeys("password");
            log.add("Enter password", true);

            driver.findElements(By.name("ForceRegister")).get(1).click();
            log.add("Login", true);

            driver.findElement(By.cssSelector("#collapseYourDetails > div > div:nth-child(2) > div.form-group > div > button")).click();
            log.add("Continue", true);

            //Wait for Date of Event to open
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("collapseTwo"))));

            driver.findElements(By.className("ui-state-optimal")).get(1).click();
            log.add("Select recommended day", true);

            //Wait for Collection Plan to open
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("collapseThree"))));

            driver.findElement(By.id("LookupStore")).sendKeys("NG15 0HT");
            log.add("Enter postcode for store lookup", true);

            driver.findElements(By.className("btnLookupStore")).get(0).click();
            log.add("Find my store", true);

            Select storeLookup = new Select(driver.findElement(By.id("LookupStoreDropdowncollection")));
            List<WebElement> list = storeLookup.getOptions();
            for (WebElement item : list) {
                if (item.getText().contains("Mansfield")) {
                    storeLookup.selectByVisibleText(item.getText());
                }
            }
            log.add("Select address", true);

            //Wait for Date of Delivery to open
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("collapseFour"))));

            driver.findElements(By.className("ui-state-optimal")).get(1).click();
            log.add("Select recommended day", true);

            //Wait for tooltip to be clickable and click
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("div.material-box__content > h3 > label")))).click();
            log.add("Click store delivery", true);

            //Wait for Return Plans to open
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("collapseSix"))));

            driver.findElements(By.className("material-checkbox--for-panel")).get(2).click();
            log.add("Return to store", true);

            driver.findElements(By.className("js-next-section-click")).get(3).click();
            log.add("Continue", true);

            //Wait for Date of Return to open
            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.id("collapseFive"))));

            driver.findElements(By.className("ui-state-optimal")).get(1).click();
            log.add("Select recommended day", true);

            driver.findElement(By.cssSelector(".event-proccess-next > div > div > input")).click();
            log.add("Continue", true);

            //Select Outfit
            Select chooseOutfit = new Select(driver.findElement(By.name("outfitID")));
            chooseOutfit.selectByVisibleText("CheckoutTester");
            log.add("Select outfit", true);

            //Choose sizes
            driver.findElements(By.className("btn-block")).get(1).click();
            log.add("Continue", true);

            wait.until(ExpectedConditions.urlContains("confirmation"));

            //Confirmation
            driver.findElements(By.className("material-checkbox")).get(0).click();
            log.add("Accept Terms", true);

            driver.findElements(By.className("btn-block")).get(2).click();
            log.add("Place order", true);

            //Payment
            driver.findElement(By.id("CardNumber")).sendKeys("5555555555555555");
            log.add("Enter card number", true);

            Select expiryMonth = new Select(driver.findElement(By.name("ExpMonth")));
            expiryMonth.selectByVisibleText("05");
            log.add("Select expiry month", true);

            Select expiryYear = new Select(driver.findElement(By.name("ExpYear")));
            expiryYear.selectByVisibleText("2019");
            log.add("Select expiry year", true);

            driver.findElement(By.id("CV2")).sendKeys("123");
            log.add("Enter security number", true);

            driver.findElement(By.id("buttonSubmit")).click();
            log.add("Pay now", true);

            String errorText = driver.findElement(By.className("jumbotron")).getText();
            Boolean checkExpected = errorText.contains(cardError);

            if (!checkExpected) {
                log.add("Expected error not found", true);
                msg.send("<b>Expected text could not be found.<br /><br />Expected:</b> " + cardError + "<br /><br /><b>Returned:</b> " + errorText);
                Assert.fail("Expected text could not be found");
            } else {
                log.delScr();
                log.add("Expected card error was found - screenshots deleted", false);
            }
        } catch (Exception e) {
            String eS = e.toString();
            log.add("Exception found", true);
            log.add(eS, false);

            msg.send("<b>An exception was found</b><br /><br /> " + eS);
            Assert.fail("An exception was found");
        }

        log.add("Test finished\n", false);
    }

    @Test
    public void mossBros() throws IOException, MessagingException {

        String configName = "MossBros";
        String testName = "Moss Bros Checkout";

        Config setUp = new Config(configName, testName, driver);
        Logger log = setUp.getLog();
        SendMessages msg = setUp.getMsg();
        String cardError = setUp.getError();

		WebDriverWait wait = new WebDriverWait(driver, 20);

        log.add("Starting test", false);

        try {

            String[] products = { "965460815", "965460807", "965462213", "965462204", "965284601", "965284813",
                    "965461322", "965461501", "965461414", "965461809", "965326003", "965570214", "965326018",
                    "965461219", "965569719", "965461263", "965570119", "965461204", "965461291", "965326071",
                    "965569909", "965326002", "965570798", "965326009", "965570509", "965284109", "965570137",
                    "965570534", "965446515", "965284009", "965434715", "965284118", "965446709", "965434815",
                    "965569918", "965446415", "965446307", "965435715", "965570004", "965570209", "965569801",
                    "965461619", "965570377", "965569709", "965284001", "965435817", "965457098", "965489709",
                    "965446621", "965570991", "965569704", "965569891", "965570819", "965570414", "965461292",
                    "965570418", "965570201", "965570413" };

            driver.get("http://www.moss.co.uk/");
            log.add("Go to site", true);
            driver.findElement(By.id("search-query")).clear();

            log.add("Trying product with ID " + products[0], false);
            driver.findElement(By.id("search-query")).sendKeys(products[0]);
            log.add("Send keys to search", true);
            driver.findElement(By.id("search-query")).submit();

            //Attempt to add to bag
            log.add("Add to Bag", true);
            driver.findElement(By.className("addToBag")).click();

            wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.className("basketStatus"))));

            // Checks if product can not be found. Iterate through array until a product is found.
            if (driver.findElement(By.cssSelector(".basketStatus > span")).getText()
                    .contains("Sorry. We have no stock of that item at the moment.")) {
                for (int i = 1; i < products.length; i++) {
                    driver.findElement(By.id("search-query")).clear();

                    log.add("Trying product with ID " + products[i], false);
                    driver.findElement(By.id("search-query")).sendKeys(products[i]);
                    log.add("Send keys to search", true);
                    driver.findElement(By.id("search-query")).submit();

                    log.add("Add to Bag", true);
                    driver.findElement(By.className("addToBag")).click();

                    // Check if product is found, exit loop if present
                    if (!(driver.findElement(By.cssSelector(".basketStatus > span")).getText()
                            .contains("Sorry. We have no stock of that item at the moment."))) {
                        break;
                    } else if (i == (products.length - 1)) {
                        log.add("No more products left in array\n\nTried: " + Arrays.toString(products), false);
                        msg.send("No more products left in array<br /><br />Tried products: " + Arrays.toString(products));
                        Assert.fail("No more products left in array");
                    }
                }
            }

            WebElement payNow = driver.findElements(By.cssSelector("div.addBagConfirmation > div > a.orangeButton")).get(0);

            // Pay now
            log.add("Pay Now", true);
            payNow.click();

            // Checkout
            log.add("Go to Checkout", true);
            driver.findElements(By.cssSelector(".viewBasketContainer > a")).get(1).click();

            // Login
            driver.findElements(By.name("Email")).get(0).clear();
            driver.findElements(By.name("Email")).get(0).sendKeys("checkouttester@remarkable.net");
            log.add("Enter username", true);
            driver.findElements(By.name("Password")).get(0).clear();
            driver.findElements(By.name("Password")).get(0).sendKeys("checkouttester");
            log.add("Enter password", true);
            driver.findElement(By.xpath("//*[@id='LoginForm']/div/div/input")).click();

            // Go to Payment
            log.add("Continue", true);
            driver.findElement(By.className("btn-topRight")).click();


            // Enter payment details and submit
            driver.findElement(By.id("card.cardNumber")).clear();
            driver.findElement(By.id("card.cardNumber")).sendKeys("5555555555555555");
            log.add("Enter card number", true);

            driver.findElement(By.id("card.cardHolderName")).clear();
            driver.findElement(By.id("card.cardHolderName")).sendKeys("Checkout Tester");
            log.add("Enter card holder", true);

            Select expiryMonth = new Select(driver.findElement(By.id("card.expiryMonth")));
            expiryMonth.selectByVisibleText("05");
            log.add("Select expiry month", true);
            Select expiryYear = new Select(driver.findElement(By.id("card.expiryYear")));
            expiryYear.selectByVisibleText("2017");
            log.add("Select expiry year", true);

            driver.findElement(By.id("card.cvcCode")).clear();
            driver.findElement(By.id("card.cvcCode")).sendKeys("123");
            log.add("Enter security code", true);

            driver.findElement(By.className("paySubmit")).click();

			String errorText = driver.findElement(By.id("errorFrameValidationErrors")).getText();
            Boolean checkExpected = errorText.contains(cardError);

            if (!checkExpected) {
                log.add("Expected error not found", true);
                msg.send("<b>Expected text could not be found.<br /><br />Expected:</b> " + cardError + "<br /><br /><b>Returned:</b> " + errorText);
                Assert.fail("Expected text could not be found");
            } else {
                log.delScr();
                log.add("Expected card error was found - screenshots deleted", false);
            }
        } catch (Exception e) {
            String eS = e.toString();
            log.add("Exception found", true);
            log.add(eS, false);

            msg.send("<b>An exception was found</b><br /><br /> " + eS);
            Assert.fail("An exception was found");
        }

        log.add("Test finished\n", false);
    }

    @AfterClass
    public static void tearDown() {
        driver.close();
    }


}
