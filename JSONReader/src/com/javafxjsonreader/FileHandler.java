/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafxjsonreader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.slf4j.LoggerFactory;

/**
 * Handle Files (read/write/clean)
 * @author jens.papenhagen
 */
public class FileHandler {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(FileHandler.class);

    /**
     * read a file and giveback as String
     *
     * @param fileName
     * @return
     */
    public static String readFile(File fileName) {
        String content = null;
        try {
            content = new String(Files.readAllBytes(fileName.toPath()));
        } catch (IOException ex) {
            LOG.error("read File all Bytes give a IOException" + ex.getMessage());
        }

        return content;
    }

    /**
     * cleanFile open the file an remove all content than the file is 100% empty
     *
     * @param filename
     * @throws IOException
     */
    public static void cleanFile(String filename) throws IOException {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileWriter = new FileWriter(filename, false);
            bufferedWriter = new BufferedWriter(fileWriter);
            //adding emtpy String
            bufferedWriter.write("");
        } catch (IOException ex) {
            LOG.error("FileWriter give a IOException " + ex.getMessage());
        } finally {
            bufferedWriter.close();
            fileWriter.close();
        }
    }

    /**
     * Save a String into a singel File
     *
     * @param input
     * @param fileName
     * @throws IOException
     */
    public static void writeToFile(String input, String fileName) throws IOException {
        //using the FileWriter to make a new file or handle
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName, false);
            fileWriter.write(input);
            //adding a break after the String
            fileWriter.write(System.lineSeparator());
        } catch (IOException ex) {
            LOG.error("FileWriter have an IOException" + ex.toString());
        } finally {
            fileWriter.close();
        }

    }


}
