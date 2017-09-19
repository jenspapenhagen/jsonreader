/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafxjsonreader;

import com.google.gson.Gson;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTimePicker;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FXML Controller class for the singelparkinglot Dialog
 *
 * @author jens.papenhagen
 */
public class SingelParkingLotController implements Initializable {

    private final static Logger LOG = LoggerFactory.getLogger(SingelParkingLotController.class);

    public List<Status> newJSON;

    @FXML
    private TableView table;
    @FXML
    private TableColumn<Status, Integer> id;
    @FXML
    private TableColumn<Status, String> internalCarparkId;
    @FXML
    private TableColumn<Status, String> stamp;
    @FXML
    private TableColumn<Status, Integer> maxSlots;
    @FXML
    private TableColumn<Status, Integer> freeSlots;
    @FXML
    private TableColumn<Status, Integer> reservedSlots;
    @FXML
    private JFXDatePicker dateA;
    @FXML
    private JFXDatePicker dateE;
    @FXML
    private JFXTimePicker timeA;
    @FXML
    private JFXTimePicker timeE;
    @FXML
    private TextField parkhausid;
    @FXML
    private AnchorPane AnchorPane;

    @FXML
    private void handleUpdateButtonAction(ActionEvent event) throws InterruptedException, Exception {
        LOG.info("UpdateButtonPressed");

        String newURL = newURL();
        LOG.info("This URL " + newURL);
        fillUIformJSON();
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) throws InterruptedException, Exception {
        LOG.info("SaveButtonPressed");
        String newURl = newURL();

        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //get the main Windows
        Stage stage = (Stage) AnchorPane.getScene().getWindow();

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        Gson gson = new Gson();
        Object output = gson.toJson(newURl);

        if (file != null) {
             FileHandler.writeToFile(output.toString(), file.getAbsolutePath());
        }
    }

    @FXML
    private void handleSaveAsCSVButtonAction(ActionEvent event) throws InterruptedException, Exception {
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

    public void setInID(String user_id) {
        parkhausid.setText(user_id);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //set the timezone to Berlin Europa
        ZoneId zone1 = ZoneId.of("Europe/Berlin");
        //fill with default values
        dateA.setValue(LocalDate.now(zone1));
        dateE.setValue(LocalDate.now(zone1));
        timeA.setValue(LocalTime.now().minusHours(1));
        timeE.setValue(LocalTime.now(zone1));


    }

    /**
     * tansfer the data form the JSON file to the UI
     * @return
     * @throws Exception 
     */
    private List<Status> fillUIformJSON() throws Exception {
        //make a statuslist
        List<Status> statusliste = null;
        //give the URL
        PullJsonFromUrl pullJsonFromUrl = new PullJsonFromUrl(newURL());
        String jsonFileAsString = pullJsonFromUrl.pullFromNewUrl(newURL());
        statusliste = pullJsonFromUrl.parseLocalStatusJsonFromFile(jsonFileAsString);

        //built the ObservableList for the UI
        ObservableList<Status> liste = convertFeed(statusliste);
        //fill the list from the JSON File
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        internalCarparkId.setCellValueFactory(new PropertyValueFactory<>("internalCarparkId"));
        stamp.setCellValueFactory(new PropertyValueFactory<>("stamp"));
        maxSlots.setCellValueFactory(new PropertyValueFactory<>("maxSlots"));
        freeSlots.setCellValueFactory(new PropertyValueFactory<>("freeSlots"));
        reservedSlots.setCellValueFactory(new PropertyValueFactory<>("reservedSlots"));

        //move this ObservableList into the TableView
        table.setItems(liste);

        //giveback the Statuslist
        return (List<Status>) statusliste;

    }

    /**
     * take the infos form the UI to buildup a URL
     *
     * @return
     */
    private String newURL() {
        //handle User Input with an error message
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Fehler im Datum ");
        alert.setHeaderText("Probleme beim Datum");

        //the date in the timerange have to be form A to E 
        if (dateA.getValue().isAfter(dateE.getValue())) {
            alert.setContentText("Anfangs Datum ist kleiner als das Enddatum");

            //get the main Windows for Postion and Style
            Stage stage = (Stage) AnchorPane.getScene().getWindow();

            alert.initOwner(stage);
            alert.initStyle(stage.getStyle());

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                //force set the dateA to yesterday and  dateE to today
                dateA.setValue(LocalDate.now().minusDays(1));
                dateE.setValue(LocalDate.now());
            }
        }

        //the dates are on the same date but the start time have to be after the end time
        if (dateA.getValue().isEqual(dateE.getValue()) && timeA.getValue().isAfter(timeE.getValue())) {
            //change error message
            alert.setContentText("Anfangs Zeit liegt nich vor der Endzeit");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                //force set the start time one hour behind now and the enddate to now, to give back some working timerange
                timeA.setValue(LocalTime.now().minusHours(2));
                timeE.setValue(LocalTime.now().minusHours(1));
            }
        }

        return buildURLString();
    }

    /**
     * buildingup the URL as String
     *
     * @return
     */
    private String buildURLString() {
        //building the URL in a String
        String URL = "http://report.pundr.hamburg/rest/carpark/data/" + parkhausid.getText() + "/" + dateA.getValue() + "T" + timeA.getValue() + "_" + dateE.getValue() + "T" + timeE.getValue();

        return URL;
    }

    /**
     * Convert a List of Status into an ObservableList
     *
     * @param liste
     * @return ObservableList
     */
    private ObservableList<Status> convertFeed(List<Status> liste) {
        ObservableList<Status> output = FXCollections.observableArrayList();
        output.addAll(liste);

        return output;
    }

    
    /**
     * building the a CSV ( Comma-separated values file)
     *
     * @return the new build String
     * @throws Exception
     */
    private String buildingUpCSVString() throws Exception {
        //empty string
        String alltogether = "";
        //the header of the CSV file
        String headerString = "id,internalCarparkId,stamp,maxSlots,freeSlots";
        //adding the header on top
        alltogether = alltogether + headerString + System.lineSeparator();
        //for every Status build a new line in the CSV and ending with a break
        for (Status stat : fillUIformJSON()) {
            String bodyString = "" + stat.getId() + "," + stat.getInternalCarparkId() + "," + stat.getStamp() + "," + stat.getMaxSlots() + "," + stat.getFreeSlots();
            alltogether = alltogether + bodyString + System.lineSeparator();
        }

        //give back the buildup String
        return alltogether;
    }

}
