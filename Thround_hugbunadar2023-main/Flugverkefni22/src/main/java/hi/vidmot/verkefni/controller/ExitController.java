package hi.vidmot.verkefni.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class ExitController {
    // Define JavaFX controls
    @FXML
    Button fxBookingConfirmationButton;
    @FXML
    AnchorPane fxExitAnchoPane;

    // Handle button click event
    @FXML
    private void bookingConfirmationButtonClick(ActionEvent event) {
        // Close the JavaFX application
        Platform.exit();
    }

    // This method is called after the FXML file is loaded
    public void start() {
    }
}
