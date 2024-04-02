package com.spin.cgt.tool;

import com.spin.cgt.excetion.CgtException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class TplZipTool {
    private final static String TPL_CLASS_PATH = "tpl/";
    private final static String TPL_ROOT_RELATIVE_PATH = "/src/main/resources/tpl/";
    private final static String ALGORITHM = "AES";
    private final static SecretKeySpec secretKey = new SecretKeySpec("!@#$%^&*12345678".getBytes(), ALGORITHM);

    public static InputStream unzipTpl(String tplName) {
        ClassLoader classLoader = TplZipTool.class.getClassLoader();
        InputStream zipInputStream = classLoader.getResourceAsStream(TPL_CLASS_PATH + tplName + ".zip");
        if (zipInputStream == null) {
            try {
                String tplZipPath = System.getProperty("user.dir") + TPL_ROOT_RELATIVE_PATH + tplName + ".zip";
                zipInputStream = new FileInputStream(tplZipPath);
            } catch (IOException e) {
                throw new CgtException(e);
            }
        }
        if (zipInputStream == null) {
            throw new CgtException("tpl file not exist!");
        }
        try {
            return decryptAndUnzip(zipInputStream, tplName);
        } finally {
            try {
                zipInputStream.close();
            } catch (IOException e) {
            }
        }
    }

    public static void zipTpl(String tplName) {
        String tplPath = System.getProperty("user.dir") + TPL_ROOT_RELATIVE_PATH + tplName;
        String tplZipPath = tplPath + ".zip";
        File tplFile = new File(tplPath);
        if (!tplFile.exists() || !tplFile.isFile()) {
            throw new CgtException(tplName + " not exist!");
        }
        try (FileOutputStream fos = new FileOutputStream(tplZipPath); ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            // 压缩并加密文件
            try (FileInputStream fis = new FileInputStream(tplFile)) {
                ZipEntry zipEntry = new ZipEntry(tplName);
                zipOut.putNextEntry(zipEntry);
                // 加密文件内容
                encryptStream(fis, zipOut, secretKey);
            }
        } catch (IOException e) {
            throw new CgtException(e);
        }
    }

    private static void encryptStream(InputStream inputStream, OutputStream outputStream, SecretKeySpec secretKey) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] inputBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(inputBuffer)) >= 0) {
                byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                outputStream.write(outputBuffer);
            }

            byte[] outputBuffer = cipher.doFinal();
            outputStream.write(outputBuffer);
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            throw new CgtException("Error encrypting stream: " + e.getMessage());
        }
    }

    private static InputStream decryptAndUnzip(InputStream zipStream, String tplName) {
        try {
            // 创建AES解密器
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            try (ZipInputStream zipInputStream = new ZipInputStream(zipStream); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                // 只解压一个tpl文件
                ZipEntry entry = zipInputStream.getNextEntry();
                while (entry != null) {
                    if (!entry.getName().equals(tplName)) {
                        entry = zipInputStream.getNextEntry();
                        continue;
                    }

                    byte[] inputBuffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zipInputStream.read(inputBuffer)) >= 0) {
                        byte[] outputBuffer = cipher.update(inputBuffer, 0, bytesRead);
                        outputStream.write(outputBuffer);
                    }
                    byte[] outputBuffer = cipher.doFinal();
                    outputStream.write(outputBuffer);
                    String s = new String(outputStream.toByteArray());
                    return new ByteArrayInputStream(outputStream.toByteArray());
                }
            }
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IOException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new CgtException(e);
        }
        throw new CgtException("tpl file not exist!");
    }

    public static void main(String[] args) throws IOException {
//        zipTpl("pre.go.tpl");
//        zipTpl("gen_case_test.go.tpl");
    }
}
