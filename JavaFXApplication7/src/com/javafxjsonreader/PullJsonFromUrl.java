/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafxjsonreader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TableColumn;

/**
 *
 * @author jens.papenhagen
 */
public final class PullJsonFromUrl {

    public static TableColumn<Integer, Number> intColumn;
    public static TableColumn<Integer, String> appelationColumn;
    public static List<String> nameColumn = Arrays.asList();
    public static List<Integer> maxCapactiyList = Arrays.asList();

    public PullJsonFromUrl(boolean online) throws Exception {
        String output = PullFromUrl(online);
    }

    PullJsonFromUrl(String newURL) throws Exception {
        PullFromNewUrl(newURL);
    }

    PullJsonFromUrl() throws Exception {
    }

    public String PullFromNewUrl(String newURL) throws Exception {

        String DownloadedFile = readUrl(newURL);
        writeToSingelRangeFile(DownloadedFile);

        String output = readFile("./src/com/javafxjsonreader/singelrage.txt");

        return output;
    }

    public String PullFromUrl(boolean online) throws Exception {
        if (online) {
            String DownloadedFile = readUrl("http://report.pundr.hamburg/rest/carpark/");
            writeToFeedFile(DownloadedFile);
        }

        String output = readFile("./src/com/javafxjsonreader/filename.txt");

        return output;
    }

    public static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();

            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            return buffer.toString();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

    }

    public static void writeToFeedFile(String input) throws FileNotFoundException, IOException {
        String path = "./src/com/javafxjsonreader/filename.txt";
        cleanFile(path);
        writeToFile(input, path);
    }

    public static void writeToSingelRangeFile(String input) throws FileNotFoundException, IOException {
        String path = "./src/com/javafxjsonreader/singelrage.txt";
        cleanFile(path);
        writeToFile(input, path);
    }

    public static void writeToFile(String input, String filename) throws FileNotFoundException {
        try (PrintWriter out = new PrintWriter(filename)) {
            out.println(input);
        }
    }

    public static void cleanFile(String filename) throws FileNotFoundException, IOException {
        FileWriter fw = new FileWriter(new File(filename), false);
        try (BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("");
            bw.close();
        }
        
        fw.close();
    }

    public static String readFile(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));

        return content;
    }

    public List<Feed> parseLocalJsonFromFile(String Fileinput) {
        Gson gson = new Gson();
        Type founderListType = new TypeToken<ArrayList<Feed>>() {
        }.getType();
        List<Feed> founderList = gson.fromJson(Fileinput, founderListType);

        return founderList;
    }

    public List<Status> parseLocalStatusJsonFromFile(String Fileinput) {
        Gson gson = new Gson();
        Type founderListType = new TypeToken<ArrayList<Status>>() {
        }.getType();
        List<Status> founderList = gson.fromJson(Fileinput, founderListType);

        return founderList;
    }

}
