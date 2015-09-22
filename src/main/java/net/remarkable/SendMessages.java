package net.remarkable;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.TimeZone;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

public class SendMessages {

    private String configName;
    private String testName;
    private Logger log;

    public SendMessages(String config, String name, Logger logger) {
        configName = config;
        testName = name;
        log = logger;
    }


    /**
     * Sends emails and SMS (using email to SMS) using JavaMail.
     *
     * Config for email setup is read from:
     * C:\Tests\tests\configs\EmailSettings.properties
     * Required settings: requiresAuth, tlsEnabled, smtp, mtpPort, username, password
     *
     * Emails and numbers used to send are read from:
     * C:\Tests\tests\configs\[Test Name Without Spaces].properties
     * Required settings: comma separated emails and phone numbers using keys email and number - can be empty
     *
     * Config for SMS setup is read from:
     * C:\Tests\tests\configs\SMSSettings.properties
     * Required settings: user, token, domain, subject
     * SMS messages will send Saturday, Sunday and weekdays between 6pm and 8am
     *
     * @throws IOException
     */
    public void send(String e) throws IOException, MessagingException {

        log.add("Starting messaging process", false);

        // Get Email Config
        Properties config = new Properties();
        InputStream email;
        try {
            email = new FileInputStream("C:\\Tests\\configs\\EmailSettings.properties");

            config.load(email);

            final String username = config.getProperty("username");
            final String password = config.getProperty("password");

            // SMTP Settings
            Properties props = new Properties();
            props.put("mail.smtp.auth", config.getProperty("requiresAuth"));
            props.put("mail.smtp.starttls.enable", config.getProperty("tlsEnabled"));
            props.put("mail.smtp.host", config.getProperty("smtp"));
            props.put("mail.smtp.port", config.getProperty("smtpPort"));

            Session session = Session.getInstance(props, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });

            // TODO Email debugger
            session.setDebug(false);

            if (e.contains("No more products left in array")) {
                log.add("Sending only to me", false);

                // Create a message
                Message msg = new MimeMessage(session);

                // Set the from and to address
                InternetAddress addressFrom = new InternetAddress(username);
                msg.setFrom(addressFrom);

                msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse("tammy.lewis@remarkable.net"));

                // Set subject
                msg.setSubject("Test \"" + testName + "\" has failed.");

                msg.setContent(e, "text/html; charset=utf-8");

                Transport.send(msg);
                log.add("Email sent", false);

            } else {

                try {

                    InputStream input = new FileInputStream("C:\\Tests\\configs\\" + configName + ".properties");
                    config.load(input);

                    // Get emails from config - only send if not null
                    String emails = config.getProperty("email");
                    if (emails != null) {

                        log.add("Sending to emails: " + emails, false);

                        // Create a message
                        Message msg = new MimeMessage(session);

                        // Set the from and to address
                        InternetAddress addressFrom = new InternetAddress(username);
                        msg.setFrom(addressFrom);

                        msg.addRecipients(Message.RecipientType.TO, InternetAddress.parse(emails));

                        // Set subject
                        msg.setSubject("Test \"" + testName + "\" has failed.");

                        File zipFile = log.zipScr();
                        Boolean zipSize = zipFile.length() < 25000000;

                        if (zipFile != null) {
                            log.add("Size of ZIP file is " + zipFile.length(), false);
                            if (zipSize) {
                                log.add("Zip file attached", false);

                                // Message
                                BodyPart messageBody = new MimeBodyPart();
                                messageBody.setContent(e, "text/html; charset=utf-8");
                                Multipart multipart = new MimeMultipart();
                                multipart.addBodyPart(messageBody);

                                //Attachment
                                messageBody = new MimeBodyPart();
                                DataSource source = new FileDataSource(zipFile);
                                messageBody.setDataHandler(new DataHandler(source));
                                messageBody.setFileName(zipFile.getName());
                                multipart.addBodyPart(messageBody);

                                //Set content
                                msg.setContent(multipart);
                            } else {
                                log.add("Zip file not attached", false);
                                msg.setContent("Zip file could not be attached.\n\n" + e, "text/html; charset=utf-8");
                            }
                        } else {
                            msg.setContent(e, "text/html; charset=utf-8");
                        }

                        Transport.send(msg);
                        log.add("Emails sent", false);

                    } else {
                        log.add("No emails found", false);
                    }

                } catch (FileNotFoundException listFnf) {
                    log.add("Could not find file - C:\\Tests\\configs\\" + configName + ".properties", false);
                }
            }

            // Get numbers from config - only try if not null
            String numbers = config.getProperty("number");
            if (numbers != null) {

                log.add("Numbers found: " + numbers, false);

                // Get SMS Config
                try {
                    InputStream sms = new FileInputStream("C:\\Tests\\configs\\SMSSettings.properties");
                    config.load(sms);
                    String smsUser = config.getProperty("user");
                    String smsToken = config.getProperty("token");
                    String smsDomain = config.getProperty("domain");
                    String smsSubject = config.getProperty("subject");

                    // Get data to check if SMS is necessary
                    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
                    SimpleDateFormat hourSdf = new SimpleDateFormat("HH");
                    SimpleDateFormat daySdf = new SimpleDateFormat("u");

                    // Get hour and convert to int
                    Integer hour = Integer.parseInt(hourSdf.format(calendar.getTime()));

                    // Get day and convert to int
                    Integer day = Integer.parseInt(daySdf.format(calendar.getTime()));

                    log.add("Day is " + day + ", hour is " + hour, false);

                    // Check whether to send SMS
                    if (day == 6 || day == 7 || hour >= 18 || hour <= 8) {

                        log.add("SMS conditions met", false);

                        Message smsMsg = new MimeMessage(session);
                        InternetAddress smsFrom = new InternetAddress(username);
                        smsMsg.setFrom(smsFrom);
                        if (numbers.contains(",")) {
                            String[] numbersArr = numbers.split(",");
                            Integer nLength = numbersArr.length;

                            for (int i = 1; i < nLength; i++) {
                                String emailToAdd = numbersArr[i] + smsDomain;
                                smsMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailToAdd));
                            }
                        } else {
                            String emailToAdd = numbers + smsDomain;
                            smsMsg.addRecipient(Message.RecipientType.TO, new InternetAddress(emailToAdd));
                        }

                        smsMsg.setSubject(smsSubject);
                        smsMsg.setContent(smsUser + "\n" + smsToken + "\n" + "Test \"" + testName
                                + "\" has failed. Check emails for more information.", "text/plain");

                        Transport.send(smsMsg);
                        log.add("SMS messages sent", false);
                    } else {
                        log.add("SMS conditions not met - no messages sent", false);
                    }
                } catch (FileNotFoundException ssFnf) {
                    log.add("Could not find file - C:\\Tests\\configs\\SMSSettings.properties", false);
                }

            }

        } catch (FileNotFoundException esFnf) {
            log.add("Could not find file - C:\\Tests\\configs\\EmailSettings.properties", false);
        }
    }
}
