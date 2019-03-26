package com.company;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Writer extends FileHandler {

    private BufferedWriter writer;

    public Writer(String fileName) throws FileNotFoundException {
        super(fileName);
        this.writer = new BufferedWriter((new PrintWriter(file)));
    }

    public void writeLines(List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }

    public void close() throws IOException {
        writer.close();
        this.isClosed = true;
    }

}
