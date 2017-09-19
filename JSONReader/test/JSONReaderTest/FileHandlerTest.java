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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jens.papenhagen
 */
public class FileHandlerTest {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(FileHandlerTest.class);
    private static String fileName = "./test/JSONReaderTest/testfile.txt";
    private static String testString = "123456";

    @Test
    public void testFileRead() {
        String output = FileHandler.readFile(fileName);

        assertThat(output).isNotBlank().as("The file is not Blank");
        assertThat(output).isEqualTo(testString).as("checking the return String of the file is the same as the testString");
    }

    @Test
    public void testCleanFile() {
        try {
            FileHandler.cleanFile(fileName);
        } catch (IOException ex) {
            Logger.getLogger(FileHandlerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        assertThat(fileName).isBlank().as("The file is Blank");
    }

    @Test
    public void testWriteFile() throws IOException {
        //write the deafult test String to the file
        FileHandler.writeToFile(testString, fileName);

        String output = FileHandler.readFile(fileName);
        assertThat(output).isNotBlank().as("The file is not Blank");
        assertThat(output).isEqualTo(testString).as("checking the return String of the file is the same as the testString");

    }

}
