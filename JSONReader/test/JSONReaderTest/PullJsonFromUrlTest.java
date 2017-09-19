/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JSONReaderTest;

/**
 *
 * @author jens.papenhagen
 */
import static org.assertj.core.api.Assertions.*;
import org.junit.Test;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import com.javafxjsonreader.PullJsonFromUrl;
import com.javafxjsonreader.FileHandler;

public class PullJsonFromUrlTest {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(PullJsonFromUrlTest.class);
    private static String fileName = "./test/JSONReaderTest/test.json";

    @Test
    public void pullFromNewUrlTest() throws IOException {
        String newURL = "http://report.pundr.hamburg/rest/carpark/data/2122393/2017-08-07T06:00_2017-08-07T06:10";
        String outputOnline = PullJsonFromUrl.readUrl(newURL);
        String outputOffline = FileHandler.readFile(fileName);

        assertThat(outputOnline).as("The file is not Blank").isNotEmpty();
        assertThat(outputOffline).as("The file is not Blank").isNotEmpty();
        assertThat(outputOnline).isEqualToIgnoringWhitespace(outputOffline).as("checking the return String of the oneline JSON is the same as the local saved one");

    }

}
