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

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class LayoutController implements Initializable {
    
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
        System.out.println("UpdateButtonPressed");
        boolean online = true;
        starter(online);
    }

    @FXML
    private void handleSaveAsCSVButtonAction(ActionEvent event) throws InterruptedException, Exception {
        boolean online = false;
        System.out.println("SaveAsCSVButtonPressed");
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        //get the main Windows
        Stage stage = (Stage) AnchorPane.getScene().getWindow();

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            SaveFile(convertToCSVString(), file);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            starter(false);
        } catch (Exception ex) {
            Logger.getLogger(LayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void starter(boolean online) throws Exception {
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
            if (event.getClickCount() == 2) {
                Feed stat = (Feed) table.getSelectionModel().getSelectedItem();

                if (stat.getInternalId() != null) {
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DialogLayout.fxml"));
                        Parent root1 = (Parent) fxmlLoader.load();
                        //https://stackoverflow.com/questions/14370183/passing-parameters-to-a-controller-when-loading-an-fxml
                        DialogController controller = fxmlLoader.<DialogController>getController();
                        controller.setInID(stat.getInternalId());
                        
                        Stage stage = new Stage();
                        stage.initModality(Modality.APPLICATION_MODAL);
                        stage.initStyle(StageStyle.DECORATED);
                        stage.setResizable(false);
                        stage.setTitle("Park & Ride Parkhaus Checker: " + stat.getInternalId());
                        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));

                        stage.setScene(new Scene(root1));
                        stage.show();
                                     
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

        });

    }

    private List<Feed> getFeed(boolean online) throws Exception {

        List<Feed> liste = null;
        PullJsonFromUrl pullJsonFromUrl = new PullJsonFromUrl(online);
        String jsonFileAsString = pullJsonFromUrl.PullFromUrl(online);
        liste = new PullJsonFromUrl(online).parseLocalJsonFromFile(jsonFileAsString);

        return liste;
    }

    private ObservableList<Feed> convertFeed(List<Feed> liste) {
        ObservableList<Feed> output = FXCollections.observableArrayList();
        output.addAll(liste);

        return output;

    }

    public void SaveFile(String input, File fileName) throws IOException {
        try (FileWriter fileWriter = new FileWriter(fileName, true)) {
            fileWriter.write(input);
            fileWriter.write(System.lineSeparator());
            fileWriter.close();
        }
    }

    public String convertToCSVString() throws Exception {
        boolean online = false;
        String alltogether = "";
        String headerString = "geoId,internalId,id,zip,contactPhone,appelation,contactName,street,longitude,capacityMax,latitude,abbreviation,contactMail,city";
        alltogether = alltogether + headerString + "\n";
        for (Feed feed : getFeed(online)) {
            String bodyString = "" + feed.getGeoId() + "," + feed.getInternalId() + "," + feed.getId() + "," + feed.getZip() + ","
                    + feed.getContactPhone() + "," + feed.getAppelation() + "," + feed.getContactName() + "," + feed.getStreet() + "," + feed.getLongitude() + "," + feed.getCapacityMax()
                    + "," + feed.getLatitude() + "," + feed.getAbbreviation() + "," + feed.getContactMail() + "," + feed.getCity();
            alltogether = alltogether + bodyString + "\n";
        }
        return alltogether;
    }

}
