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
import java.io.FileWriter;
import java.io.IOException;
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

/**
 * FXML Controller class
 *
 * @author jens.papenhagen
 */
public class DialogController implements Initializable {

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
        System.out.println("UpdateButtonPressed");

        String newURL = newURl();
        System.out.println("" + newURL);
        starter(newURL);
    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) throws InterruptedException, Exception {
        String newURl = newURl();
        System.out.println("SaveButtonPressed");
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //get the main Windows
        Stage stage = (Stage) AnchorPane.getScene().getWindow();

        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        Gson gson = new Gson();
        Object output = gson.toJson(getFeed(newURl));

        if (file != null) {
            SaveFile(output.toString(), file);
        }
    }

    @FXML
    private void handleSaveAsCSVButtonAction(ActionEvent event) throws InterruptedException, Exception {
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

    public void setInID(String user_id) {
        parkhausid.setText(user_id);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ZoneId zone1 = ZoneId.of("Europe/Berlin");
        dateA.setValue(LocalDate.now(zone1));
        dateE.setValue(LocalDate.now(zone1));
        timeA.setValue(LocalTime.now());
        timeE.setValue(LocalTime.now(zone1));
    }

    public void starter(String URL) throws Exception {
        if (URL.isEmpty() | URL == null) {
            URL = "http://report.pundr.hamburg/rest/carpark/data/1122145/2017-08-25T05:00_2017-08-25T05:50";
        }

        ObservableList<Status> liste = convertFeed(getFeed(URL));

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        internalCarparkId.setCellValueFactory(new PropertyValueFactory<>("internalCarparkId"));
        stamp.setCellValueFactory(new PropertyValueFactory<>("stamp"));
        maxSlots.setCellValueFactory(new PropertyValueFactory<>("maxSlots"));
        freeSlots.setCellValueFactory(new PropertyValueFactory<>("freeSlots"));
        reservedSlots.setCellValueFactory(new PropertyValueFactory<>("reservedSlots"));

        table.setItems(liste);
    }

    private String newURl() {

        //Handle User Input
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Fehler im Datum ");
        alert.setHeaderText("Probleme beim Datum");

        if (dateA.getValue().isAfter(dateE.getValue())) {
            alert.setContentText("Anfangs Datum ist kleiner als das Enddatum");
            //get the main Windows
            Stage stage = (Stage) AnchorPane.getScene().getWindow();
            alert.initOwner(stage);
            alert.initStyle(stage.getStyle());
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                dateA.setValue(LocalDate.now().minusDays(1)); //set dateA a to yesterday
                dateE.setValue(LocalDate.now()); // set dateE to today
            }
        }

        if (dateA.getValue().isEqual(dateE.getValue()) && timeA.getValue().isAfter(timeE.getValue())) {
            alert.setContentText("Anfangs Zeit liegt nich vor der Endzeit");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                timeA.setValue(timeE.getValue());
            }
        }

        String newURL = "http://report.pundr.hamburg/rest/carpark/data/" + parkhausid.getText() + "/" + dateA.getValue() + "T" + timeA.getValue() + "_" + dateE.getValue() + "T" + timeE.getValue();
        return newURL;
    }

    public List<Status> getFeed(String newURL) throws Exception {
        if (newURL.isEmpty() | newURL == null) {
            newURL = "http://report.pundr.hamburg/rest/carpark/data/1122145/2017-08-25T05:00_2017-08-25T05:50";
        }

        List<Status> liste = null;
        PullJsonFromUrl pullJsonFromUrl = new PullJsonFromUrl(newURL);

        String jsonFileAsString = pullJsonFromUrl.PullFromNewUrl(newURL);
        liste = pullJsonFromUrl.parseLocalStatusJsonFromFile(jsonFileAsString);

        return (List<Status>) liste;
    }

    private ObservableList<Status> convertFeed(List<Status> liste) {
        ObservableList<Status> output = FXCollections.observableArrayList();
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
        String newURl = newURl();

        String alltogether = "";
        String headerString = "id,internalCarparkId,stamp,maxSlots,freeSlots";
        alltogether = alltogether + headerString + "\n";
        for (Status stat : getFeed(newURl)) {
            String bodyString = "" + stat.getId() + "," + stat.getInternalCarparkId() + "," + stat.getStamp() + "," + stat.getMaxSlots() + "," + stat.getFreeSlots();
            alltogether = alltogether + bodyString + "\n";
        }

        return alltogether;
    }

}
