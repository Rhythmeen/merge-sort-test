package com.company;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileHandler {

    protected File file;
    protected boolean isClosed;

    public FileHandler(String fileName) {
        this.file = new File(fileName);
    }

    public static void copy(String from, String to) throws IOException {
        File fromFile = new File(from);
        File toFile = new File(to);

        if (!toFile.exists()) {
            toFile.createNewFile();
        }

        try (FileChannel in = new FileInputStream(fromFile).getChannel();
             FileChannel out = new FileOutputStream(toFile).getChannel() ) {
            out.transferFrom( in, 0, in.size() );
        }
    }

    public static boolean delete(String fileName) {
        File file = new File(fileName);
        return file.delete();
    }

    public static String createUniqueTemp() throws IOException {
        File file = File.createTempFile("merge_", null, null);
        return file.getAbsolutePath();
    }

    public static boolean exists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static boolean empty(String fileName) {
        File file = new File(fileName);
        return file.length() == 0;
    }

    public static boolean isCreatable(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            return false;
        }
        file.delete();
        return true;
    }

    public boolean isClosed() {
        return isClosed;
    }

}
