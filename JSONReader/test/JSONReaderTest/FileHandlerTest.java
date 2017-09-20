/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSONReaderTest;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import com.javafxjsonreader.FileHandler;
import java.io.File;
import java.io.IOException;
import org.junit.Before;

/**
 *
 * @author jens.papenhagen
 */
public class FileHandlerTest {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(FileHandlerTest.class);
    private static String fileName = "./test/JSONReaderTest/testfile.txt";
    private static String testString = "123456";

    @Before
    public void fillTheFile() throws IOException{
        LOG.info("File: " + testString + " is new written.");
        FileHandler.writeToFile(testString, fileName);
    }
    
    @Test
    public void testFileRead() {
        String output = FileHandler.readFile(fileName);

        assertThat(output).as("The file is not Blank").isNotEmpty();
        assertThat(output).isEqualToIgnoringWhitespace(testString).as("checking the return String of the file is the same as the testString");
    }

    @Test
    public void testCleanFile() {
        try {
            FileHandler.cleanFile(fileName);
             
        } catch (IOException ex) {
            LOG.error(ex.getMessage());
        }
        String output = FileHandler.readFile(fileName);
        assertThat(output).as("The file is Blank").isEmpty();
    }

    @Test
    public void testWriteFile() throws IOException {
        //write the deafult test String to the file
        FileHandler.writeToFile(testString, fileName);

        String output = FileHandler.readFile(fileName);
        assertThat(output).as("The file is not Blank").isNotEmpty();
        assertThat(output).isEqualToIgnoringWhitespace(testString).as("checking the return String of the file is the same as the testString");

    }

}
