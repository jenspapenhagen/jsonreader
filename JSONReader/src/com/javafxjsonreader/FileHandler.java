/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafxjsonreader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jens.papenhagen
 */
public class FileHandler {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(FileHandler.class);

    /**
     * read a file and giveback as String
     * @param fileName
     * @return 
     */
    public static String readFile(String fileName) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (IOException ex) {
            LOG.error("read File all Bytes give a IOException" + ex.getMessage());
        }

        return content;
    }

    /**
     * write a String to a File, nothting more
     * @param input
     * @param filename 
     */
    public static void writeToFile(String input, String filename) {
        try (final PrintWriter out = new PrintWriter(filename)) {
            out.println(input);
        } catch (FileNotFoundException ex) {

        }
    }

    /**
     * cleanFile 
     * open the file an remove all content
     * than the file is 100% empty
     * @param filename
     * @throws IOException 
     */
    public static void cleanFile(String filename) throws IOException {
        FileWriter fw = new FileWriter(new File(filename), false);
        try (final BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("");
            bw.close();
        } catch (IOException ex) {
            LOG.error("FileWriter give a IOException " + ex.getMessage());
        } finally {
            fw.close();
        }
    }

    /**
     * Save a String into a singel File
     *
     * @param input
     * @param fileName
     * @throws IOException
     */
    public static void saveToFile(String input, File fileName) throws IOException {
        //using the FileWriter to make a new file or handle
        try (final FileWriter fileWriter = new FileWriter(fileName, true)) {
            fileWriter.write(input);
            //adding a break after the String
            fileWriter.write(System.lineSeparator());
            fileWriter.close();
        } catch (IOException ex) {
            LOG.error("FileWriter have an IOException" + ex.toString());
        }
    }

}
