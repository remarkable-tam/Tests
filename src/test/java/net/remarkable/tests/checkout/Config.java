package net.remarkable.tests.checkout;

import net.remarkable.Logger;
import net.remarkable.SendMessages;
import org.openqa.selenium.WebDriver;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.junit.Assert;

public class Config {

    private Logger log;
    private SendMessages msg;
    private String cardError;

    public Config(String configName, String testName, WebDriver driver) throws IOException {

        log = new Logger(testName, driver);

        Properties assertCfg = new Properties();
        try {
            InputStream input = new FileInputStream("C:\\Tests\\configs\\" + configName + ".properties");
            assertCfg.load(input);
            cardError = assertCfg.getProperty("cardError");
        } catch (FileNotFoundException e) {
            log.add("Could not find file - C:\\Tests\\configs\\" + configName + ".properties", false);
            Assert.fail("Could not find config file");
        }
        if (cardError == null) {
            log.add("Card error value could not be found in C:\\Tests\\configs\\" + configName + ".properties", false);
            Assert.fail("Could not find card error in config file");
        }

        msg = new SendMessages(configName, testName, log);
    }

    public Logger getLog() {
        return log;
    }

    public SendMessages getMsg() {
        return msg;
    }

    public String getError() {
        return cardError;
    }
}
