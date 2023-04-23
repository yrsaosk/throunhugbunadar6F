package hi.vidmot.verkefni.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassengerViewController {

    // UI components
    @FXML
    private Text fxPassengerNumber;
    @FXML
    private AnchorPane fxPassengerAnchorPane;
    @FXML
    private TextField fxFirstNameTextField;
    @FXML
    private TextField fxLastNameTextField;
    @FXML
    private TextField fxPassportTextField;
    @FXML
    private Label fxPhoneLabel;
    @FXML
    private TextField fxPhoneTextField;
    @FXML
    private Label fxEmailLabel;
    @FXML
    private TextField fxEmailTextField;
    @FXML
    private Button fxAddPassengerButton;
    @FXML
    private Text fxContactPassenger;
    @FXML
    private GridPane fxPassengerGridPane;

    // class variables
    boolean isContact = false;
    private BookingController bookingController;
    private int passengerIndex;

    // setter for passenger index
    public void setPassengerIndex(int passengerIndex) {
        this.passengerIndex = passengerIndex;
        System.out.println("passengerIndex: " + passengerIndex);
        fxPassengerNumber.setText(String.valueOf(passengerIndex));
    }

    // setter for booking controller
    public void setBookingController(BookingController controller) {
        bookingController = controller;
    }

    // method to confirm passenger input
    public void confirmPassengerInput(ActionEvent event) throws IOException, SQLException, ParseException {
        // get input fields from the UI
        String firstName = fxFirstNameTextField.getText();
        String lastName = fxLastNameTextField.getText();
        String passport = fxPassportTextField.getText();
        String phone = fxPhoneTextField.getText();
        String email = fxEmailTextField.getText();
        String passengerNumber = fxPassengerNumber.getText();

        // create a custom dialog for input validation
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Villa í innslætti");

        // create a button to close the dialog
        ButtonType closeButton = new ButtonType("Loka", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);

        // check for validation errors
        if (isContact && (firstName.isEmpty() || lastName.isEmpty() || passport.isEmpty() || phone.isEmpty() || email.isEmpty())) {
            dialog.setContentText("Vinsamlegast fylltu út alla reitina.");
        } else if (isContact && !isValidPhoneNumber(phone)) {
            dialog.setContentText("Vinsamlegast lagaðu símanúmerið.");
        } else if (isContact && !isValidEmail(email)) {
            dialog.setContentText("Vinsamlegast lagaðu netfangið.");
        } else if (!isContact && (firstName.isEmpty() || lastName.isEmpty() || passport.isEmpty())) {
            dialog.setContentText("Vinsamlegast fylltu út alla reitina.");
        } else {
            String seatNumber = getSeatNumber();
            // call the bookingController's method to confirm passenger input
            bookingController.passengerInputConfirmed(firstName, lastName, passport, phone, email, isContact, seatNumber);
            return;
        }
        // show the dialog and wait for user input
        dialog.showAndWait();
    }

