import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    public static void main(String[] args) {

        String osInfo = String.format("OS: %s, Version: %s",
                System.getProperty("os.name"),
                System.getProperty("os.version"));
// System.out.println(osInfo);

//        System.out.println(" ");

        int cpuCores = Runtime.getRuntime().availableProcessors();
//        System.out.println("Number of Threads: " + cpuCores);

        Runtime runtime = Runtime.getRuntime();
//        System.out.println("Free Memory: " + (runtime.freeMemory() / 1024 / 1024) + " GB");

//        System.out.println(" ");

//        System.out.println(System.getenv("PROCESSOR_IDENTIFIER"));

        Logger logger = Logger.getLogger("Log");
        try {
            FileHandler fh = new FileHandler("PCInfo.txt", true);
            logger.addHandler(fh);
            fh.setFormatter(new SimpleFormatter());
            logger.info(osInfo);
            logger.info("Number of Threads: " + cpuCores);
            logger.info("Free Memory: " + (runtime.freeMemory() / 1024 / 1024) + " GB");
            logger.info(System.getenv("PROCESSOR_IDENTIFIER"));

        } catch (SecurityException | IOException e) {
            logger.log(Level.SEVERE, "Произошла ошибка при работе с FileHandler.", e);
        }



        String hostname = "";
        int port = 0;
        String username = "";
        String password = "";

        FTPClient ftp = new FTPClient();

        try {
            ftp.connect(hostname, port);
            int replyCode = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(replyCode)) {
                return;
            }

            boolean loggedIn = ftp.login(username, password);
            if (!loggedIn) {
                return;
            }

            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);


            String localFilePath = "./Log.txt";
            String remoteDirectory = "/";

            File localFile = new File(localFilePath);
            if (!localFile.exists()) {
                return;
            }

            try (FileInputStream inputStream = new FileInputStream(localFilePath)) {
                boolean uploaded = ftp.storeFile(remoteDirectory + "PCInfo.txt", inputStream);
                if (uploaded) {
                    System.out.println("File Uploaded Successfully");
                } else {
                    System.out.println("File Not Uploaded");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ftp.isConnected()) {
                    ftp.logout();
                    ftp.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
