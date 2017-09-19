/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafxjsonreader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class LayoutController implements Initializable {

    private final static org.slf4j.Logger LOG = LoggerFactory.getLogger(LayoutController.class);

    public List<Feed> newJSON;

    @FXML
    private TableView table;
    @FXML
    private TableColumn<Feed, Integer> id;
    @FXML
    private TableColumn<Feed, Integer> internalId;
    @FXML
    private TableColumn<Feed, String> appelation;
    @FXML
    private TableColumn<Feed, String> abbreviation;
    @FXML
    private TableColumn<Feed, Integer> capacityMax;
    @FXML
    private TableColumn<Feed, Double> longitude;
    @FXML
    private TableColumn<Feed, Double> latitude;
    @FXML
    private TableColumn<Feed, String> street;
    @FXML
    private TableColumn<Feed, Integer> zip;
    @FXML
    private TableColumn<Feed, String> city;
    @FXML
    private TableColumn<Feed, Integer> geoId;
    @FXML
    private TableColumn<Feed, String> contactName;
    @FXML
    private TableColumn<Feed, String> contactPhone;
    @FXML
    private TableColumn<Feed, String> contactMail;
    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private void handleUpdateButtonAction(ActionEvent event) throws InterruptedException, Exception {
        LOG.info("UpdateButtonPressed");
        boolean online = true;
        fillUIformJSON(online);
    }

    @FXML
    private void handleSaveAsCSVButtonAction(ActionEvent event) throws InterruptedException, Exception {
        boolean online = false;
        LOG.info("SaveAsCSVButtonPressed");
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //get the main Windows
        Stage stage = (Stage) AnchorPane.getScene().getWindow();

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            FileHandler.writeToFile(buildingUpCSVString(), file.getAbsolutePath());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            fillUIformJSON(false);
        } catch (Exception ex) {
            Logger.getLogger(LayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void fillUIformJSON(boolean online) throws Exception {
        //get the new Feed
        ObservableList<Feed> liste = convertFeed(getFeed(online));

        //feed the list
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        internalId.setCellValueFactory(new PropertyValueFactory<>("internalId"));
        appelation.setCellValueFactory(new PropertyValueFactory<>("appelation"));
        abbreviation.setCellValueFactory(new PropertyValueFactory<>("abbreviation"));
        capacityMax.setCellValueFactory(new PropertyValueFactory<>("capacityMax"));
        longitude.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        latitude.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        street.setCellValueFactory(new PropertyValueFactory<>("street"));
        zip.setCellValueFactory(new PropertyValueFactory<>("zip"));
        city.setCellValueFactory(new PropertyValueFactory<>("city"));
        geoId.setCellValueFactory(new PropertyValueFactory<>("geoId"));
        contactName.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        contactPhone.setCellValueFactory(new PropertyValueFactory<>("contactPhone"));
        contactMail.setCellValueFactory(new PropertyValueFactory<>("contactMail"));

        table.setItems(liste);

        table.setOnMouseClicked(event -> {
            //ount the click an only handl doubleclicks
            if (event.getClickCount() == 2) {
                //get the selectet status
                Feed stat = (Feed) table.getSelectionModel().getSelectedItem();

                //check if the InternalId is set
                if (stat.getInternalId() != null) {
                    try {
                        //load the layout from the fxml file
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DialogLayout.fxml"));

                        //getting the InternalId form the main window
                        //https://stackoverflow.com/questions/14370183/passing-parameters-to-a-controller-when-loading-an-fxml
                        Parent root1 = (Parent) fxmlLoader.load();

                        DialogController controller = fxmlLoader.<DialogController>getController();
                        //set the InternalId form the main window to this child window
                        controller.setInID(stat.getInternalId());

                        //adding look of the window
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.DECORATED);
                        stage.setTitle("Park & Ride Parkhaus Checker: " + stat.getInternalId());
                        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
                        stage.setResizable(true);

                        //show the window
                        stage.setScene(new Scene(root1));
                        stage.show();
                    } catch (IOException ex) {
                        LOG.error("FXMLLoader have an IOException" + ex.toString());
                    }

                }

            }

        });

    }

    /**
     * getting a new overview List of all the parkinglots form old oder form
     * uptodate file
     *
     * @param online
     * @return
     * @throws Exception
     */
    private List<Feed> getFeed(boolean online) throws Exception {
        List<Feed> liste = null;
        PullJsonFromUrl pullJsonFromUrl = new PullJsonFromUrl(online);
        String jsonFileAsString = pullJsonFromUrl.pullOverview(online);
        liste = new PullJsonFromUrl(online).parseLocalJsonFromFile(jsonFileAsString);

        return liste;
    }

    /**
     * Convert a List of Status into an ObservableList
     *
     * @param liste
     * @return ObservableList
     */
    private ObservableList<Feed> convertFeed(List<Feed> liste) {
        ObservableList<Feed> output = FXCollections.observableArrayList();
        output.addAll(liste);

        return output;

    }


    /**
     * building the a CSV ( Comma-separated values file)
     *
     * @return the new build String
     * @throws Exception
     */
    public String buildingUpCSVString() throws Exception {
        boolean online = false;
        //empty string
        String alltogether = "";
        //the header of the CSV file
        String headerString = "geoId,internalId,id,zip,contactPhone,appelation,contactName,street,longitude,capacityMax,latitude,abbreviation,contactMail,city";
        //adding the header on top
        alltogether = alltogether + headerString + System.lineSeparator();
        //for every Status build a new line in the CSV and ending with a break
        for (Feed feed : getFeed(online)) {
            String bodyString = "" + feed.getGeoId() + "," + feed.getInternalId() + ","
                    + feed.getId() + "," + feed.getZip() + ","
                    + feed.getContactPhone() + "," + feed.getAppelation() + ","
                    + feed.getContactName() + "," + feed.getStreet() + ","
                    + feed.getLongitude() + "," + feed.getCapacityMax()
                    + "," + feed.getLatitude() + "," + feed.getAbbreviation()
                    + "," + feed.getContactMail() + "," + feed.getCity();
            alltogether = alltogether + bodyString + System.lineSeparator();
        }
        //give back the buildup String
        return alltogether;
    }

}
