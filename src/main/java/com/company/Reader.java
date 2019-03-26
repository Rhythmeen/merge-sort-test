package com.company;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Reader extends FileHandler {

    private Scanner sc;

    public Reader(String fileName) throws FileNotFoundException {
        super(fileName);
        this.sc = new Scanner(file);
    }

    public List<String> readLines(int n) {
        List<String> lines = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            String line;
            if (!sc.hasNext()) {
                break;
            }
            line = sc.nextLine();
            lines.add(line);
        }

        if (lines.isEmpty()) {
            close();
        }

        return lines;
    }

    public void close() {
        sc.close();
        this.isClosed = true;
    }

}
