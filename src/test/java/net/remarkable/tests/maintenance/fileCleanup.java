package net.remarkable.tests.maintenance;

import net.remarkable.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class fileCleanup {

    private Logger log;
    private Long current;

    @Before
    public void setUp() throws Exception {
        log = new Logger("File Cleanup", null);
        current = System.currentTimeMillis();
    }

    @Test
    public void cleanup() throws IOException {

        File path = new File("C:\\Tests\\logs");

        File[] folders = path.listFiles();

        if (folders == null) {

            log.add("Folders is null", false);

        } else if (folders.length != 0) {

            for (File folder : folders) {

                File[] siteFiles = folder.listFiles();

                if (siteFiles == null) {

                    log.add("SiteFiles is null", false);

                } else if (siteFiles.length != 0) {

                    for (File site : siteFiles) {

                        checkDelete(site);

                    }

                } else {

                    log.add("No subfolders found in " + folder, false);
                    checkDelete(folder);

                }

            }
        } else {

            log.add("No folders to check", false);

        }
    }

    private void checkDelete(File file) throws IOException {

        log.add("Checking folder " + file, false);
        Long mod = file.lastModified();
        Long diff = current - mod;

        Integer twoWeeks = 1209600000;

        if (diff >= twoWeeks) {

            log.add(file + " last modified more than 2 weeks ago, deleting folder.", false);
            FileUtils.deleteDirectory(file);

        } else {

            log.add(file + " last modified within last 2 weeks, not deleting.", false);
            
        }

    }

}
