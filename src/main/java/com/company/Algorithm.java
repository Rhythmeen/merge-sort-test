package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Algorithm {

    public static void mergeListOfFiles(List<String> files, String outputName, int size, boolean reversed, boolean numeric, int batchSize) throws IOException {
        String leftName = null;
        String rightName = null;
        try {
            if (size < 2) {
                FileHandler.copy(files.get(0), outputName);
                return;
            }
            leftName = FileHandler.createUniqueTemp();
            rightName = FileHandler.createUniqueTemp();

            int mid = size / 2;
            List<String> l = files.subList(0, mid);
            List<String> r = files.subList(mid, size);

            mergeListOfFiles(l, leftName, l.size(), reversed, numeric, batchSize);
            mergeListOfFiles(r, rightName, r.size(), reversed, numeric, batchSize);

            mergeFiles(leftName, rightName, outputName, reversed, numeric, batchSize);
            FileHandler.delete(leftName);
            FileHandler.delete(rightName);
        } finally {
            if (leftName != null) {
                FileHandler.delete(leftName);
            }
            if (rightName != null) {
                FileHandler.delete(rightName);
            }
        }
    }

    private static void mergeFiles(String in1, String in2, String out, boolean reversed, boolean numeric, int batchSize) throws IOException {
        Writer w = new Writer(out);
        Reader r1 = new Reader(in1);
        Reader r2 = new Reader(in2);
        List<String> batch1;
        List<String> batch2;
        List<String> batchToWrite;

        do {
            batch1 = fixBatch(r1.readLines(batchSize), reversed, numeric);
            batch2 = fixBatch(r2.readLines(batchSize), reversed, numeric);
            batchToWrite = new ArrayList<>(2 * batchSize);

            mergeLists(batchToWrite, batch1, batch2, reversed, numeric);
            w.writeLines(batchToWrite);

        } while (!r1.isClosed() && !r2.isClosed());

        while (!r1.isClosed()) {
            w.writeLines(r1.readLines(batchSize));
        }

        while (!r2.isClosed()) {
            w.writeLines(r2.readLines(batchSize));
        }

        w.close();
    }

    private static void mergeLists(List<String> a, List<String> l, List<String> r, boolean reversed, boolean numeric) {
        int i = 0, j = 0;
        while (i < l.size() && j < r.size()) {
            if (compareStrings(l.get(i), r.get(j), reversed, numeric) <= 0) {
                a.add(l.get(i));
                i++;
            } else {
                a.add(r.get(j));
                j++;
            }
        }

        while (i < l.size()) {
            a.add(l.get(i));
            i++;
        }

        while (j < r.size()) {
            a.add(r.get(j));
            j++;
        }
    }

    private static List<String> fixBatch(List<String> batch, boolean reversed, boolean numeric) {
        List<String> filteredByType = filterByType(batch, numeric);
        return filterByOrder(filteredByType, reversed, numeric);
    }

    private static boolean isNumeric(String entry) {
        return entry.matches("-?\\d+");
    }

    private static List<String> filterByType(List<String> batch, boolean numeric) {
        List<String> filtered = new ArrayList<>();
        for (String entry : batch) {
            if (numeric & isNumeric(entry)) {
                filtered.add(entry);
            }
        }
        return filtered;
    }

    private static List<String> filterByOrder(List<String> batch, boolean reversed, boolean numeric) {
        if (batch.size() < 2) {
            return batch;
        }
        List<String> filtered = new ArrayList<>();

        String prev = batch.get(0);
        filtered.add(prev);
        for (int i = 1; i < batch.size(); i++) {
            String next = batch.get(i);
            if (compareStrings(next, prev, reversed, numeric) >= 0) {
                filtered.add(next);
                prev = next;
            }
        }

        return filtered;
    }

    private static int compareStrings(String a, String b, boolean reversed, boolean numeric) {
        if (reversed) {
            return compareStrings(b, a, false, numeric);
        }
        if (numeric) {
            return Integer.parseInt(a) - Integer.parseInt(b);
        }
        return a.compareTo(b);
    }

}
