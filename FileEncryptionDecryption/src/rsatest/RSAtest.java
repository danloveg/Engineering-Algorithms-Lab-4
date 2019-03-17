package rsatest;

/*******************************************************************************
 *
 * RSAtest.java
 *
 * This program uses the FileEncryptor and FileDecryptor to first encrypt a real
 * text file, and decrypt that file.
 ******************************************************************************/

import rsa.FileEncryptor;
import rsa.FileDecryptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RSAtest {
    public static void main(String[] args) {
        String filename = "src/textfiles/sample.txt";
        String[] sampleArgs = {filename};

        File file = new File(filename);
        String initialContents = readFileContents(file);

        // Encrypt file with encryptor class
        FileEncryptor.main(sampleArgs);
        System.out.println("\n---------------------------------------------------\n");

        // Decrypt the file with decryptor class
        FileDecryptor.main(sampleArgs);

        String finalContents = readFileContents(file);

        if (initialContents.equals(finalContents)) {
            System.out.println("\n> PASS: File was encrypted and decrypted correctly.");
        } else {
            System.out.println("\n> FAIL: File was not encrypted or decrypted correctly.");
        }
    }

    public static String readFileContents(File file) {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            byte[] data = new byte[(int) file.length()];
            inputStream.read(data);
            inputStream.close();

            return new String(data, "UTF-8");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return "";
        }
    }
}