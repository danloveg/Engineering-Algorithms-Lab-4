package rsa;

/*******************************************************************************
 *
 * FileEncryptor.java
 *
 * This program implements the RSA algorithm to encrypt a small file.
 *
 * RSA is a public key encryption system which makes use of two keys, a public
 * one and a private one. These become exponents for encryption and decryption.
 *
 * The modulus value is determined by two probabilistically prime numbers of a
 * given (large) size. The public exponent may then be selected, and a private
 * exponent calculated.
 *
 * The BigIntegers class provides native support for implementing encryption
 * algorithms in Java.
 ******************************************************************************/

import java.io.*;
import java.util.Random;
import java.math.BigInteger;
import java.util.Base64;

public class FileEncryptor {

    public static void main(String[] args) {
        int KEY_SIZE = 128;
        int CERTAINTY = 20;
        File file;
        String fileContents;

        // Get a file from the command line arguments
        try {
            file = getFile(args, KEY_SIZE);
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

        System.out.println(String.format("Encrypting file with a modulus of %d bits.\n", KEY_SIZE));

        // ---------------------------------------------------------------------
        // Calculate keys and modulus
        // ---------------------------------------------------------------------
        long start = System.currentTimeMillis();
        BigInteger one = new BigInteger("1");

        // Generate two likely primes of KEY_SIZE
        // The modulus for RSA is determined as the product of p and q.
        BigInteger p = new BigInteger(KEY_SIZE, CERTAINTY, new Random());
        BigInteger q = new BigInteger(KEY_SIZE, CERTAINTY, new Random());
        BigInteger n = p.multiply(q);

        // PHI(n) represents the Euler totient function, which is calculated as:
        // PHI(n) = (p-1)*(q-1)
        // It is used for determining public and private exponents
        BigInteger phi = p.subtract(one).multiply(q.subtract(one));

        // Select a public exponent (repeat until GCD condition is met).
        BigInteger e = new BigInteger(32, 4, new Random());
        BigInteger gcd = phi.gcd(e);
        while (!gcd.equals(one)) {
            e = new BigInteger(32, 4, new Random());
            gcd = phi.gcd(e);
        }

        // Calculate the private exponent.
        BigInteger d = e.modInverse(phi);
        System.out.println(String.format("Took %d milliseconds to calculate n, e, and d.\n",
            System.currentTimeMillis() - start));


        // ---------------------------------------------------------------------
        // Encrypt the message
        // ---------------------------------------------------------------------
        start = System.currentTimeMillis();
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] fileContentsAsBytes = fileContents.getBytes();
        BigInteger fileContentsAsBigInt = new BigInteger(fileContentsAsBytes);
        BigInteger encrypted = fileContentsAsBigInt.modPow(e, n);
        String base64EncryptedFileContents = encoder.encodeToString(encrypted.toByteArray());

        System.out.println(String.format("Took %d milliseconds to encrypt the file",
            System.currentTimeMillis() - start));
        System.out.println("Original file contents: " + fileContents);
        System.out.println("Encrypted file contents (Base 64): " + base64EncryptedFileContents + "\n");


        // ---------------------------------------------------------------------
        // Overwrite initial file and create new file to store keys and modulus
        // ---------------------------------------------------------------------
        try {
            FileWriter encryptedFileWriter = new FileWriter(file, false);
            encryptedFileWriter.write(base64EncryptedFileContents);
            encryptedFileWriter.close();
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
            return;
        }

        // Write RSA information to file encryptionInfo.txt
        try {
            String fileDirectory = file.getAbsoluteFile().getParent();
            File rsaInfoFile = new File(fileDirectory, "encryptionInfo.txt");
            FileWriter rsaInfoWriter = new FileWriter(rsaInfoFile, false);
            StringBuilder rsaInfoBuilder = new StringBuilder();
            rsaInfoBuilder.append(String.format("e=" + e + "\n"));
            rsaInfoBuilder.append(String.format("d=" + d + "\n"));
            rsaInfoBuilder.append(String.format("n=" + n));
            System.out.println("Encryption information:\n" + rsaInfoBuilder.toString());
            rsaInfoWriter.write(rsaInfoBuilder.toString());
            rsaInfoWriter.close();
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
            return;
        }
    }

    /*
     * Get a valid file from the command line arguments.
     */
    public static File getFile(String[] args, int KEY_SIZE) throws RuntimeException {
        if (args.length != 1) {
            throw new RuntimeException("ERROR: Must supply an argument for a file name.");
        }

        String filename = args[0];
        File file = new File(filename);

        // If file does not exist or is not a file or is a directory, throw exception
        if (!file.exists() || !file.isFile() || file.isDirectory()) {
            throw new RuntimeException(String.format("ERROR: %s is not a valid file.", filename));
        }

        int maxLength = (KEY_SIZE / 8); // File cannot be greater than modulus
        if (file.length() > maxLength) {
            throw new RuntimeException(String.format("ERROR: File is too big. Max # of bytes for KEY_SIZE %d is %d", KEY_SIZE, maxLength));
        }

        return file;
    }

    /*
     * Return contents of file
     */
    public static String readFileContents(File file) throws IOException {
        byte[] data;
        try (FileInputStream inputStream = new FileInputStream(file)) {
            data = new byte[(int) file.length()];
            inputStream.read(data);
        }

        return new String(data, "UTF-8");
    }
}
