import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

public class FileEncryptor {
    public static void main(String[] args) {
        File file;
        String fileContents;

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

        System.out.println("ECE3790 Winter 2019, Lab 4");
        System.out.println("Using RSA to Encrypt and Decrypt a Small File\n");
        System.out.println(String.format("Contents of file to encrypt: %s", fileContents));
    }

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

    public static String readFileContents(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        inputStream.read(data);
        inputStream.close();

        return new String(data, "UTF-8");
    }
}