package com.company;

import java.io.IOException;
import java.util.*;

import static com.company.Algorithm.mergeListOfFiles;

public class Main {

    public static void main(String[] args) {

        Map<String, Object> params = extractParams(args);
        Boolean reversed = (Boolean) params.get("reversed");
        Boolean numeric = (Boolean) params.get("numeric");
        String out = (String) params.get("out");
        List<String> in = (List<String>) params.get("in");
        Integer batchSize = (Integer) params.get("batch_size");

        if (!FileHandler.exists(out) && !FileHandler.isCreatable(out)) {
            System.out.println("Error: output file does not exist and can't be created.");
            printUsage();
            System.exit(1);
        }

        String tempFilePath = null;
        try {
            tempFilePath = FileHandler.createUniqueTemp();
        } catch (IOException e) {
            System.out.println("Error: temporary files can't be created.");
            System.exit(1);
        } finally {
            FileHandler.delete(tempFilePath);
        }

        System.out.println(String.format("%s files to process", in.size()));

        List<String> badFiles = new ArrayList<>();
        Set<String> goodFiles = new HashSet<>();
        for (String fileName : in) {
            if (FileHandler.empty(fileName)) {
                badFiles.add(fileName);
            }
            goodFiles.add(fileName);
        }

        if (!badFiles.isEmpty()) {
            System.out.println(String.format("%s empty or non-existent files, skipped:", badFiles.size()));
            for (String badFile : badFiles) {
                System.out.println(badFile);
            }
        }

        in.removeAll(badFiles);
        if (goodFiles.isEmpty()) {
            System.out.println("All files skipped, terminate.");
            System.exit(1);
        }

        try {
            mergeListOfFiles(new ArrayList<>(goodFiles), out, in.size(), reversed, numeric, batchSize);
        } catch (IOException e) {
            System.out.println("Something went wrong during working with files, see stack trace:");
            e.printStackTrace();
        }

    }

    private static Map<String, Object> extractParams(String[] args) {
        List<String> argumets = new ArrayList<>(Arrays.asList(args));
        Map<String, Object> params = new HashMap<>();

        if (argumets.contains("-h")) {
            printUsage();
            System.exit(1);
        }

        params.put("numeric", argumets.remove("-i"));
        params.put("reversed", argumets.remove("-d"));

        params.put("batch_size", 1000);
        int batchSizeIndex = argumets.indexOf("-b");
        if (batchSizeIndex >= 0) {
            try {
                params.put("batch_size", Integer.parseInt(argumets.remove(batchSizeIndex + 1)));
                argumets.remove("-b");
            } catch (Exception e) {
                System.out.println("ERROR parsing batch size");
                printUsage();
                System.exit(1);
            }
        }

        List<String> fileNames = new ArrayList<>();
        for (String arg: argumets) {
            if (!arg.startsWith("-")) {
                fileNames.add(arg);
            }
        }

        if (fileNames.size() < 2) {
            System.out.println("ERROR: Specify exactly one output and at least one input file paths");
            printUsage();
            System.exit(1);
        }

        params.put("out", fileNames.remove(0));
        params.put("in", fileNames);

        return params;

    }

    private static void printUsage() {
        System.out.println("Usage: merge -a | -d  -i | -s -b 500 out.txt in1.txt in2.txt");
        System.out.println("-a | -d: ascending | descending order (optional, default - ascending)");
        System.out.println("-i | -s: integer | string input data type (optional, default - string");
        System.out.println("-b: batch size - how many lines of file read per iteration (optional, default 1000)");
        System.out.println("out.txt - output file path, required exactly one");
        System.out.println("in.txt - output file path, required one or more");
    }

}
