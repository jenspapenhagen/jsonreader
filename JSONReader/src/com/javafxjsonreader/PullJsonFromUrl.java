/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafxjsonreader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.scene.control.TableColumn;
import org.slf4j.LoggerFactory;

/**
 * Handling all the JSONs pulling and write to file
 *
 * @author jens.papenhagen
 */
public final class PullJsonFromUrl {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(PullJsonFromUrl.class);

    private static String singelparkinglotfile = "./src/com/javafxjsonreader/resources/singelparkinglot.txt";
    private static String overviewfile = "./src/com/javafxjsonreader/resources/overview.txt";

    private final ClassLoader classLoader = getClass().getClassLoader();
    
    public static TableColumn<Integer, Number> intColumn;
    public static TableColumn<Integer, String> appelationColumn;
    public static List<String> nameColumn = Arrays.asList();
    public static List<Integer> maxCapactiyList = Arrays.asList();

    PullJsonFromUrl() throws Exception {
    }

    PullJsonFromUrl(String newURL) throws Exception {
         pullFromNewUrl(newURL);
    }

    PullJsonFromUrl(boolean online) throws Exception {
        pullOverview(online);
    }

    /**
     * pull JSON form URL
     *
     * @param newURL
     * @return the JSON as String
     * @throws Exception
     */
    public String pullFromNewUrl(String newURL) throws Exception {
        //check the new URL if null use default URL
        if (newURL == null) {
            newURL = "http://report.pundr.hamburg/rest/carpark/data/2122393/2017-08-07T06:00_2017-08-14T06:00";
            LOG.info("default URL get used");
        }
        LOG.info("This URL get used" + newURL);
        //get the JSON file as String
        String DownloadedFile = readUrl(newURL);
        //write to file
        writeToSingelParklotFile(DownloadedFile);

        //read the singelparkinglotfile and giveback as String        
        String output = FileHandler.readFile(singelparkinglotfile);

        return output;
    }

    /**
     * pull the complet Overview
     *
     * @param online
     * @return
     * @throws Exception
     */
    public String pullOverview(boolean online) throws Exception {
        //check if online and save the new overview in the overview file
        if (online) {
            String DownloadedFile = readUrl("http://report.pundr.hamburg/rest/carpark/");
            writeToOverviewFile(DownloadedFile);
        }
        //read from the overview file and giveback as String        
        String output = FileHandler.readFile(overviewfile);

        return output;
    }

    /**
     * read the JSON file from given URL
     * this methode is set public for the test
     *
     * @param urlString
     * @return
     */
    public static String readUrl(String urlString) throws IOException {
        //starting the BufferedReader
        BufferedReader reader = null;
        try {
            //get the URL
            URL url = new URL(urlString);
            //check the connection 
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            //only send the HEAD not the complett HTTP GET request
            huc.setRequestMethod("HEAD");
            int responseCode = huc.getResponseCode();
            //check if the connection is working. Output the Web Error Code is not
            if (responseCode != 200) {
                LOG.error("This URL can not get solve. Web Error Code: " + responseCode);
            }
            //getting the InoutStream from the URL
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            //staring a Stringbuilder as buffer
            StringBuilder buffer = new StringBuilder();
            //fill the buffer
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }
            //giveback the filled buffer
            return buffer.toString();

        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        } finally {
            reader.close();

        }
        return "ERROR";
    }

    /**
     * write String to overview file
     *
     * @param input
     */
    private void writeToOverviewFile(String input) {
        try {
            FileHandler.cleanFile(classLoader.getResource(overviewfile).getFile());
            FileHandler.writeToFile(input, classLoader.getResource(overviewfile).getFile());
        } catch (IOException ex) {
            LOG.error("FileHandler give a IOException " + ex.getMessage());
        }

    }

    /**
     * write String to the singel parkinglot file
     *
     * @param input
     */
    private void writeToSingelParklotFile(String input) {
        try {
            FileHandler.cleanFile(classLoader.getResource(singelparkinglotfile).getFile() );
            FileHandler.writeToFile(input, classLoader.getResource(singelparkinglotfile).getFile());
        } catch (IOException ex) {
            LOG.error("FileHandler give a IOException " + ex.getMessage());
        }

    }

    /**
     * parsing the JSON file into a List of Feed
     *
     * @param Fileinput
     * @return
     */
    public List<Feed> parseLocalJsonFromFile(String Fileinput) {
        Gson gson = new Gson();
        Type founderListType = new TypeToken<ArrayList<Feed>>() {
        }.getType();
        List<Feed> founderList = gson.fromJson(Fileinput, founderListType);

        return founderList;
    }

    /**
     * parsing the JSON file into a List of Status
     *
     * @param Fileinput
     * @return
     */
    public List<Status> parseLocalStatusJsonFromFile(String Fileinput) {
        Gson gson = new Gson();
        Type founderListType = new TypeToken<ArrayList<Status>>() {
        }.getType();
        List<Status> founderList = gson.fromJson(Fileinput, founderListType);

        return founderList;
    }

}
