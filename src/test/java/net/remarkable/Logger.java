package net.remarkable;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Logger {

    private File logFile;
    private File screenshots;
    private WebDriver driver;

    public Logger(String testName, WebDriver instance) throws IOException {

        driver = instance;

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
        String date = new SimpleDateFormat("yyyy.MM.dd").format(calendar.getTime());

        File namePath = new File("C:\\Tests\\logs\\" + testName);
        if (!namePath.exists()) {
            Boolean np = namePath.mkdir();
            if (np) {
                System.out.println("Created test name directory");
            } else {
                System.out.println("Name directory could not be created");
                System.exit(1);
            }
        }

        File datePath = new File(namePath + "\\" + date);
        if (!datePath.exists()) {
            Boolean dp = datePath.mkdir();
            if (dp) {
                System.out.println("Created date directory");
            } else {
                System.out.println("Date directory could not be created");
                System.exit(1);
            }
        }

        screenshots = new File(datePath + "\\screenshots");
        if (!screenshots.exists()) {
            Boolean ss = screenshots.mkdir();
            if (ss) {
                System.out.println("Created screenshots directory");
            } else {
                System.out.println("Screenshots directory could not be created");
                System.exit(1);
            }
        }

        logFile = new File(datePath + "\\log.txt");

        if (!logFile.exists()) {
            Boolean fc = logFile.createNewFile();
            if (fc) {
                System.out.println("File was created");
            } else {
                System.out.println("File could not be created");
                System.exit(1);
            }
        }
    }

    public void add(String message, Boolean scr) throws IOException {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
        String time = new SimpleDateFormat("HH.mm.ss").format(calendar.getTime());

        FileWriter fw = new FileWriter(logFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);

        if (scr) {
            File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(scrFile, new File(screenshots + "\\" + time + " " + message + ".png"));
        }

        System.out.println(time + " " + message);
        pw.println(time + " " + message);

        pw.close();
    }

    private File[] getScr() {

        FilenameFilter getFiles = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                return lowercaseName.endsWith(".png");
            }
        };
        File[] files = screenshots.listFiles(getFiles);
        if (files.length == 0) {
            return null;
        } else {
            return files;
        }
    }

    public void delScr() {

        File[] fl = getScr();
        if (fl != null) {
            for (File file : fl) {
                Boolean del = file.delete();
                if (!del) {
                    System.out.println("File " + file + " could not be deleted");
                }
            }
        } else {
            System.out.println("No files to delete");
        }
    }

    public File zipScr() throws IOException {

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Europe/Lisbon"));
        String time = new SimpleDateFormat("HH.mm.ss").format(calendar.getTime());

        File[] fl = getScr();

        if (fl != null) {
            File zip = new File(screenshots + "\\" + time + ".zip");
            byte[] buf = new byte[1024];
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));

            for (File file : fl) {
                FileInputStream in = new FileInputStream(file);
                out.putNextEntry(new ZipEntry(file.getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
            delScr();
            return zip;
        } else {
            return null;
        }
    }
}