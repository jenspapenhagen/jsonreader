/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.javafxjsonreader;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 *
 * @author jens.papenhagen
 */
public class JavaStart extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        //load the layout form the fxml file
        Parent root = FXMLLoader.load(getClass().getResource("MainLayout.fxml"));

        //add this layout to the scene
        Scene scene = new Scene(root);

        //adding look of the window
        stage.setTitle("Park & Ride JSON Reader");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("resources/icon.png")));
        stage.setResizable(true);

        //show the window
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
