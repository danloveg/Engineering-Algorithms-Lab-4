package rsa;

/*******************************************************************************
 *
 * FileDecryptor.java
 *
 * This program implements the RSA algorithm to decrypt a small file.
 ******************************************************************************/

import java.io.*;
import java.math.BigInteger;
import java.util.Base64;
import java.util.regex.*;

public class FileDecryptor {

    public static void main(String[] args) {
        Matcher matcher;
        File file;
        String fileContents;
        Pattern pattern_n = Pattern.compile("n=(\\d+)");
        Pattern pattern_d = Pattern.compile("d=(\\d+)");
        BigInteger d, n;

        // Get a file from the command line arguments
        try {
            file = getFile(args);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return;
        }

        // Read the contents of the file
        try {
            fileContents = readFileContents(file);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("Decrypting file.\n");

        // ---------------------------------------------------------------------
        // Get encryption information from encryptionInfo.txt
        // ---------------------------------------------------------------------
        try {
            String encryptionFileDirectory = file.getAbsoluteFile().getParent();
            File rsaInfoFile = new File(encryptionFileDirectory, "encryptionInfo.txt");
            String encryptionFileContents = readFileContents(rsaInfoFile);
            matcher = pattern_n.matcher(encryptionFileContents);
            matcher.find();
            n = new BigInteger(matcher.group(1));
            matcher = pattern_d.matcher(encryptionFileContents);
            matcher.find();
            d = new BigInteger(matcher.group(1));
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
            return;
        }


        // ---------------------------------------------------------------------
        // Decrypt the message
        // ---------------------------------------------------------------------
        long start = System.currentTimeMillis();
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] fileContentsAsBytes = fileContents.getBytes();
        byte[] encryptedBytesDecoded = decoder.decode(fileContentsAsBytes); 
        BigInteger encryptedFileContentsAsBigInt = new BigInteger(encryptedBytesDecoded);
        BigInteger decryptedFileContentsAsBigInt = encryptedFileContentsAsBigInt.modPow(d, n);
        String decryptedFileContents = new String(decryptedFileContentsAsBigInt.toByteArray());

        System.out.println(String.format("Took %d milliseconds to decrypt the file",
            System.currentTimeMillis() - start));
        System.out.println("Encrypted file contents (Base 64): " + fileContents);
        System.out.println("Decrypted file contents: " + decryptedFileContents);


        // ---------------------------------------------------------------------
        // Overwrite file with decrypted contents
        // ---------------------------------------------------------------------
        try {
            FileWriter decryptedFileWriter = new FileWriter(file, false);
            decryptedFileWriter.write(decryptedFileContents);
            decryptedFileWriter.close();
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
            return;
        }
    }

    /*
     * Get a valid file from the command line arguments.
     */
    public static File getFile(String[] args) throws RuntimeException {
        if (args.length != 1) {
            throw new RuntimeException("ERROR: Must supply an argument for a file name.");
        }

        String filename = args[0];
        File file = new File(filename);

        // If file does not exist or is not a file or is a directory, throw exception
        if (!file.exists() || !file.isFile() || file.isDirectory()) {
            throw new RuntimeException(String.format("ERROR: %s is not a valid file.", filename));
        }

        if (file.length() > 245) {
            throw new RuntimeException(String.format("ERROR: File is too big. RSA is inefficient for files with size greater than 245 bytes."));
        }

        return file;
    }

    /*
     * Return contents of file
     */
    public static String readFileContents(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        inputStream.read(data);
        inputStream.close();

        return new String(data, "UTF-8");
    }
}
