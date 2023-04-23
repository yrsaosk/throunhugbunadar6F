package hi.vidmot.verkefni.controller;

import hi.vidmot.verkefni.model.Booking;
import hi.vidmot.verkefni.model.Flight;
import hi.vidmot.verkefni.model.Passenger;
import hi.vidmot.verkefni.repository.BookingRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class BookingController {

    // FXML elements
    @FXML
    private StackPane fxPassengerInfoStackPane;

    // Flight information
    private Flight departureFlight;
    private Flight arrivalFlight;
    private int numberOfPassangers;
 
    private int passengerIndex = 0;
    private int numberOfLegs = 0;
    
    BookingRepository bookingRepository;

    List<Passenger> passengers = FXCollections.observableArrayList();

    Booking booking;

    int bookingId[] = new int [2];

    // Constructor
    public BookingController() { }

    // Method to start the booking process
    public void start() throws IOException, SQLException, ParseException {
        bookingRepository = new BookingRepository();
        // If it is a one-way flight
        if (numberOfLegs == 1) {
            bookingId[0] = departureFlight.getId();
        }
        // If it is a round trip flight
        else {
            bookingId[0] = departureFlight.getId();
            bookingId[1] = arrivalFlight.getId();
        }
        numberOfPassangers = departureFlight.getNumberOfPassangers();
        System.out.println(numberOfPassangers);
        showPassengerView(numberOfPassangers);
    }

    // Method to show the passenger view
    @FXML
    public void showPassengerView(int numberOfPassengers) throws IOException, SQLException, ParseException {
        fxPassengerInfoStackPane.getChildren().clear();
        for (int i = 1; i <= numberOfPassengers; i++) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/hi/vidmot/verkefni/passenger-view.fxml"));
            Parent passengerView = loader.load();
            PassengerViewController controller = loader.getController();
            if (i == 1) {
                controller.isContact = true;
                controller.setContact(true);
                controller.showFields(true);
            } else {
                controller.setContact(false);
                controller.showFields(false);
            }
            controller.setBookingController(this);
            controller.setPassengerIndex(i);
            fxPassengerInfoStackPane.getChildren().add(passengerView);
        }
        switchToPassengerView(0);
    }

    // Method to switch to the passenger view
    public void switchToPassengerView(int passengerIndex) throws IOException, SQLException, ParseException {
        // If there are still more passengers to add
        if(passengerIndex < numberOfPassangers) {
            fxPassengerInfoStackPane.getChildren().forEach(node -> node.setVisible(false));
            fxPassengerInfoStackPane.getChildren().get(passengerIndex).setVisible(true);
        }
        // If all passengers have been added
        else {
            // Create a new booking object with the list of passengers and add it to the database
            for(int i = 0; i < numberOfLegs; i++) {
                booking = new Booking(passengers);
                bookingRepository.addBooking(bookingId[i
