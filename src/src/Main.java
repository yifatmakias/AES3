import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.security.SecureRandom;


public class Main {
    public static byte[][] parseKeyFile(File keyFile) {
        byte [][] parsedKeys = new byte[3][16];
        try {
            byte [] allKey = Files.readAllBytes(keyFile.toPath());
            int index = 0;
            for (int i = 0; i < 3 ; i++) {
                for (int j = 0; j < 16 ; j++) {
                    parsedKeys[i][j] = allKey[index];
                    index++;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return parsedKeys;
    }

    public static byte[][] parseFile(File file) {
        byte [][] parsedFile = null;
        try {
            byte [] allFile = Files.readAllBytes(file.toPath());
            int blockCount = allFile.length/16;
            parsedFile = new byte[blockCount][16];

            int index = 0;
            for (int i = 0; i < blockCount ; i++) {
                for (int j = 0; j < 16 ; j++) {
                    parsedFile[i][j] = allFile[index];
                    index++;
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return parsedFile;
    }

    public static void writeToFile(byte [] byteArray, OutputStream outputFile){
        try {
            outputFile.write(byteArray);
            outputFile.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String [] args) {
        byte [][] keys;
        byte [][] blocks;
        if (args.length != 7) {
            System.out.println("Your command is not valid.");
        }
        else {
            if (args[1].equals("-k") && args[3].equals("-i") && args[5].equals("-o")){
                String keyPath = args[2];
                String inputPath = args[4];
                String outputPath = args[6];
                try {
                    File keyFile = new File(keyPath);
                    keys = parseKeyFile(keyFile);
                    File inputFile = new File(inputPath);
                    blocks = parseFile(inputFile);
                    byte [] result = new byte[blocks.length*16];
                    int resIndex = 0;
                    if (args[0].equals("-e")){
                        for (int i = 0; i < blocks.length ; i++) {
                            byte [] c1;
                            byte [] c2;
                            AES aes1 = new AES(keys[0]);
                            c1 = aes1.encrypt(blocks[i]);
                            AES aes2 = new AES(keys[1]);
                            c2 = aes2.encrypt(c1);
                            AES aes3 = new AES(keys[2]);
                            byte [] temp = aes3.encrypt(c2);
                            System.arraycopy(temp, 0, result, resIndex, temp.length);
                            resIndex += 16;
                        }
                        OutputStream outputFile = new FileOutputStream(outputPath);
                        writeToFile(result, outputFile);
                    }
                    else if (args[0].equals("-d")){
                        for (int i = 0; i < blocks.length ; i++) {
                            byte [] c1;
                            byte [] c2;
                            AES aes1 = new AES(keys[2]);
                            c1 = aes1.decrypt(blocks[i]);
                            AES aes2 = new AES(keys[1]);
                            c2 = aes2.decrypt(c1);
                            AES aes3 = new AES(keys[0]);
                            byte [] temp = aes3.decrypt(c2);
                            System.arraycopy(temp, 0, result, resIndex, temp.length);
                            resIndex += 16;
                        }
                        OutputStream outputFile = new FileOutputStream(outputPath);
                        writeToFile(result, outputFile);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Path is not valid.");
                }
            }
            else if(args[0].equals("-b") && args[1].equals("-m") && args[3].equals("-c") && args[5].equals("-o")) {
                String messegePath = args[2];
                String cipherPath = args[4];
                String outputPath = args[6];
                try {
                    File messegeFile = new File(messegePath);
                    File cipherFile = new File(cipherPath);
                    byte [][] messegeBlocks = parseFile(messegeFile);
                    byte [][] cipherBlocks = parseFile(cipherFile);
                    byte [] result = new byte[3*16];
                    SecureRandom random = new SecureRandom();
                    byte [] k1 = new byte[16];
                    byte [] k2 = new byte [16];
                    random.nextBytes(k1);
                    random.nextBytes(k2);
                    System.arraycopy(k1, 0, result, 0, k1.length);
                    System.arraycopy(k2, 0, result, 16, k2.length);
                    AES aes1 = new AES(k1);
                    byte [] temp = aes1.encrypt(messegeBlocks[0]);
                    AES aes2 = new AES(k2);
                    byte [] MTag = aes2.encrypt(temp);
                    AES aes3 = new AES();
                    byte [] k3 = aes3.breakAES(MTag, cipherBlocks[0]);
                    System.arraycopy(k3, 0, result, 32, k2.length);
                    OutputStream outputFile = new FileOutputStream(outputPath);
                    writeToFile(result, outputFile);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
