package hi.vidmot.verkefni.controller;

import hi.vidmot.verkefni.model.Flight;
import hi.vidmot.verkefni.repository.FlightRepository;
import hi.vidmot.verkefni.repository.FlightRepositoryInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class FlightController implements Initializable {
    @FXML
    public ComboBox fxDepartureAirport;
    @FXML
    public ComboBox fxArrivalAirport;
    @FXML
    private DatePicker fxDepartureDate;
    @FXML
    private DatePicker fxArrivalDate;
    @FXML
    private ComboBox fxNumberOfPassangers;
    @FXML
    private ComboBox fxClass;
    @FXML
    private Button fxSearchButton;
    @FXML
    private Label fxNoDepartingFlightsAvailable;
    @FXML
    private Label fxNoArrivingFlightsAvailable;
    @FXML
    private Button fxClearFlightsListAndReset;
    @FXML
    private Text fxAvailableFlights;
    @FXML
    private VBox fxDepartureFlightVBox;
    @FXML
    private VBox fxArrivalFlightVBox;
    @FXML
    private VBox fxOneWayFlightVBox;
    @FXML
    ToggleGroup oneWayOrReturn = new ToggleGroup();
    @FXML
    private RadioButton fxOneWay = new RadioButton("Önnur leið");
    @FXML
    private RadioButton fxReturnFlight = new RadioButton("Báðar leiðir");
    @FXML
    private Label fxKomudagurLabel = new Label();
    @FXML
    private Text fxBrottferdText;
    @FXML
    private Text fxHeimferdText;
    @FXML
    private Button fxBookFlights;
    @FXML
    ToggleGroup departureToggleGroup = new ToggleGroup();
    @FXML
    ToggleGroup arrivalToggleGroup = new ToggleGroup();
    @FXML
    ToggleGroup oneWayToggleGroup = new ToggleGroup();

// Variables used in the controller
    private boolean showSearchButton[] = {false,false,false,false,false,false};
    private int selectableItems;
    int flightPrice;
    ObservableList<Integer> numberOfPassengers = FXCollections.observableArrayList();
    ObservableList<String> flightClass = FXCollections.observableArrayList();
    List<Flight> departureFlights = FXCollections.observableArrayList();
    List<Flight> arrivalFlights = FXCollections.observableArrayList();
    List<Flight> selectedFlights = FXCollections.observableArrayList();

    // Interface for accessing the flight repository
    protected FlightRepositoryInterface flightRepository;

    // Constructor for the controller (injecting the flight repository)
    public FlightController(FlightRepositoryInterface flightRepository) {
        this.flightRepository = flightRepository;
    }

    // Default constructor
    public FlightController() { }

    // Method called after the .fxml file has been loaded and the elements have been injected
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    /**
 * Sets up the GUI based on the given oneWay boolean flag.
 * 
 * @param oneWay flag indicating whether to display the one-way or return flight options
 */
private void setUpTheGUI(boolean oneWay) {

    // Add event listener for departure radio button group
    departureToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
        try {
            handleRadioButtonAction(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    // Add event listener for arrival radio button group
    arrivalToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
        try {
            handleRadioButtonAction(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    // Add event listener for one-way radio button group
    oneWayToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
        try {
            handleOneWayRadioButtonAction(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    });

    // Set all show search button flags to false
    for (int i = 0; i < showSearchButton.length; i++){
        showSearchButton[i] = false;
    }

    // Hide departure and arrival text fields, clear flights lists, and hide relevant labels
    fxBrottferdText.setVisible(false);
    fxHeimferdText.setVisible(false);
    departureFlights.clear();
    arrivalFlights.clear();
    selectedFlights.clear();
    fxNoDepartingFlightsAvailable.setVisible(false);
    fxClearFlightsListAndReset.setVisible(false);
    fxKomudagurLabel.setVisible(false);
    fxArrivalDate.setVisible(false);

    // Set toggle groups for one-way and return flight options
    fxOneWay.setToggleGroup(oneWayOrReturn);
    fxReturnFlight.setToggleGroup(oneWayOrReturn);

    // Set up the GUI for one-way or return flights, depending on the oneWay flag
    if(oneWay){
        fxOneWay.isSelected();
        selectableItems = 5;
    }
    else{
        fxReturnFlight.isSelected();
        selectableItems = 6;
        fxKomudagurLabel.setVisible(true);
        fxArrivalDate.setVisible(true);
    }

    // Add event listeners for one-way and return flight radio buttons
    fxOneWay.setOnAction(e -> {
        fxKomudagurLabel.setVisible(false);
        fxArrivalDate.setVisible(false);
        selectableItems = 5;
        System.out.println(selectableItems);
        System.out.println("Return date disabled");
    });
    fxReturnFlight.setOnAction(e -> {
        fxKomudagurLabel.setVisible(true);
        fxArrivalDate.setVisible(true);
        selectableItems = 6;
        System.out.println(selectableItems);
        System.out.println("Return date enabled");
    });

    // Hide search button and available flights list
    fxSearchButton.setVisible(false);
    fxAvailableFlights.setVisible(false);

    // Initialize and check connection to the flight repository
    try {
        flightRepository = getFlightRepository();
    } catch (SQLException e) {
        e.printStackTrace();
    }

    try {
        flightRepository.checkConnection();
    } catch (SQLException e) {
        e.printStackTrace();
    }

    // Get UI values
    getUIValues();
}
private void getUIValues() {
    // Get the list of departure airports from the flight repository and add them to the departure airport ComboBox.
    List<String> departureAirports = flightRepository.getDepartureAirports();
    fxDepartureAirport.getItems().addAll(departureAirports);

    // Get the list of arrival airports from the flight repository and add them to the arrival airport ComboBox.
    System.out.println("Getting the Arrival Airport list....");
    List<String> arrivalAirports = flightRepository.getArrivalAirports();
    fxArrivalAirport.getItems().addAll(arrivalAirports);

    // Set the number of passengers and flight class options.
    setNumberOfPassangers();
    setFlightClass();

    // Add event handlers for the ComboBoxes and date pickers to enable the search button if all required fields have been filled.
    fxDepartureAirport.setOnAction(e -> {
        showSearchButton[0] = true;
        checkIfToShowSearchButton();
    });
    fxArrivalAirport.setOnAction(e -> {
        showSearchButton[1] = true;
        checkIfToShowSearchButton();
    });
    fxDepartureDate.setOnAction(e -> {
        showSearchButton[2] = true;
        checkIfToShowSearchButton();
    });
    fxArrivalDate.setOnAction(e -> {
        showSearchButton[3] = true;
        checkIfToShowSearchButton();
    });
    fxNumberOfPassangers.setOnAction(e -> {
        showSearchButton[4] = true;
        checkIfToShowSearchButton();
    });
    fxClass.setOnAction(e -> {
        showSearchButton[5] = true;
        checkIfToShowSearchButton();
    });
}

    public FlightRepositoryInterface getFlightRepository() throws SQLException {
        return new FlightRepository() {
        };
    }

    private void checkIfToShowSearchButton() {
        int counter = 0;
        for (int i = 0; i < showSearchButton.length; i++) {
            if(showSearchButton[i] == true) {
                counter++;
            }
        }
        System.out.println(counter);
        if (counter == selectableItems) {
            fxSearchButton.setVisible(true);
        }
    }

    private void setFlightClass() {
        flightClass.setAll("Business","Economy");
        fxClass.setItems(flightClass);
    }

    private void setNumberOfPassangers() {
        numberOfPassengers.addAll(1,2,3,4,5,6,7,8);
        fxNumberOfPassangers.setItems(numberOfPassengers);
    }

    @FXML
//Enables the book flights button if both departure and arrival radio buttons are selected, otherwise disables it.
    private void handleRadioButtonAction(ActionEvent event) throws IOException {
        if (departureToggleGroup.getSelectedToggle() != null && arrivalToggleGroup.getSelectedToggle() != null) {
            fxBookFlights.setVisible(true);
        }
        else {
            fxBookFlights.setVisible(false);
        }
    }

    @FXML
//Handles the event when a one-way radio button is selected or deselected. If a radio button is selected, the 'Book Flights' button is set to visible, otherwise it is set to invisible.
    private void handleOneWayRadioButtonAction(ActionEvent event) throws IOException {
        if(oneWayToggleGroup.getSelectedToggle() != null) {
            fxBookFlights.setVisible(true);
        } else {
            fxBookFlights.setVisible(false);
        }
    }

    @FXML
//This method handles the action event when the user clicks the "Book" button. It checks if either the one-way or return flight is selected and proceeds accordingly.
    private void onBookClickAction(ActionEvent event) throws IOException, SQLException, ParseException {
        if (fxOneWay.isSelected()) {
            Flight selectedOneWayFlight = (Flight) oneWayToggleGroup.getSelectedToggle().getUserData();
            saveCurrentFlight(selectedOneWayFlight);
            System.out.println("selectedOneWayFlight type: "+ selectedOneWayFlight.getClass());
            openBookingWindow(selectedOneWayFlight);
        }
        else if(fxReturnFlight.isSelected()) {
            Flight selectedDepartureFlight = (Flight) departureToggleGroup.getSelectedToggle().getUserData();
            saveCurrentFlight(selectedDepartureFlight);
            System.out.println("selectedDepartureFlight type: "+ selectedDepartureFlight.getClass());
            Flight selectedArrivalFlight = (Flight) arrivalToggleGroup.getSelectedToggle().getUserData();
            saveCurrentFlight(selectedArrivalFlight);
            System.out.println("selectedArrivalFlight type: "+ selectedArrivalFlight.getClass());
            openBookingWindow(selectedDepartureFlight, selectedArrivalFlight);
        }
    }

//Opens the booking window for the selected flight
    private void openBookingWindow(Flight selectedFlight) throws IOException, SQLException, ParseException {
            // Load the booking view FXML file
    FXMLLoader newLoader = new FXMLLoader(getClass().getResource("/hi/vidmot/verkefni/booking-view.fxml"));
    Parent root = newLoader.load();
    
    // Set the selected flight on the BookingController instance and start the controller
    BookingController bookingController = newLoader.getController();
    bookingController.setFlight(selectedFlight);
    bookingController.start();

    // Replace the current scene with the booking view
    Scene scene = fxDepartureFlightVBox.getScene();
    scene.setRoot(root);

    // Show the new stage
    Stage stage = (Stage) scene.getWindow();
    stage.show();
    }

//Loads the booking view with the selected departure and arrival flights and replaces the current scene with it.
    private void openBookingWindow(Flight departure, Flight arrival) throws IOException, SQLException, ParseException {
        // Load the booking view
        FXMLLoader newLoader = new FXMLLoader(getClass().getResource("/hi/vidmot/verkefni/booking-view.fxml"));
        Parent root = newLoader.load();
        BookingController bookingController = newLoader.getController();
        bookingController.setFlight(departure, arrival);
        bookingController.start();

        // Replace the current scene with the booking view
        Scene scene = fxDepartureFlightVBox.getScene();
        scene.setRoot(root);

        // Show the new stage
        Stage stage = (Stage) scene.getWindow();
        stage.show();
    }

    @FXML
//This method clears all previous flight search results and resets the UI values, except for the one-way or return flight selections. Also sets up the GUI according to the last search performed by the user. 
    private void onClearFlightsListAndResetClick() { //clear all previous search except one way or return flight
        fxHeimferdText.setVisible(false);
        String both = String.valueOf("bothWays");
        String lastSearch = String.valueOf("bothWays");
        if (fxOneWay.isSelected()){
            lastSearch = String.valueOf("oneWay");
        }
        fxClearFlightsListAndReset.setVisible(false);
        fxAvailableFlights.setVisible(false);
        resetUIValues();
        if (lastSearch.equals(both)) {
            setUpTheGUI(false);
        }
        else {
            setUpTheGUI(true);
        }

    }

    @FXML
    public void onFindFlightsClick() throws SQLException, ParseException, IOException {
    // Hide search button and show clear/reset button and available flights section
    fxSearchButton.setVisible(false);
    fxClearFlightsListAndReset.setVisible(true); // Indicates that user can clear previous search
    fxAvailableFlights.setVisible(true);

    // Get departure and arrival airports, departure date, flight class, and number of passengers from UI
    String departureAirport = flightRepository.getAirportCode((String) fxDepartureAirport.getValue());
    String arrivalAirport  = flightRepository.getAirportCode((String) fxArrivalAirport.getValue());
    LocalDate departureDate = fxDepartureDate.getValue();
    Date date = Date.from(departureDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    String flightClass = (String) fxClass.getValue();
    int flyers = (int) fxNumberOfPassangers.getValue();

    // Print search criteria for debugging purposes
    System.out.println(departureAirport + " to " + arrivalAirport + " on " + departureDate + " at " + date);

    // Search for departing flights
    departureFlights = flightRepository.searchFlights(departureAirport, arrivalAirport, date, flightClass, flyers);

    // If no departing flights found, show error message and clear/reset button
    if (departureFlights.size()==0) {
        fxNoDepartingFlightsAvailable.setVisible(true);
        fxClearFlightsListAndReset.setVisible(true);
    }
    // Otherwise, if one-way flight is selected, display one-way flights
    else {
        if(fxOneWay.isSelected()) {
            fxClearFlightsListAndReset.setVisible(true);
            fxNoDepartingFlightsAvailable.setVisible(false);
            displayOneWayFlights();
        }
    }

    // If return flight is selected, search for returning flights
    if(fxReturnFlight.isSelected()) {
        LocalDate arrivalDate = fxArrivalDate.getValue();
        date = Date.from(arrivalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        System.out.println(departureAirport + " to " + arrivalAirport + " on " + arrivalDate + " at " + date + " and returning on " + arrivalDate);
        arrivalFlights = flightRepository.searchFlights(arrivalAirport, departureAirport, date, flightClass, flyers);

        // If no departing flights found, show error message and clear/reset button
        if (departureFlights.size()==0) {
            fxNoDepartingFlightsAvailable.setVisible(true);
            fxClearFlightsListAndReset.setVisible(true);
        }
        // If no arriving flights found, show error message and clear/reset button
        if (arrivalFlights.size()==0) {
            fxNoArrivingFlightsAvailable.setVisible(true);
            fxClearFlightsListAndReset.setVisible(true);
        }
        // Otherwise, display both departing and arriving flights
        else {
            fxBrottferdText.setVisible(true);
            fxHeimferdText.setVisible(true);
            fxNoDepartingFlightsAvailable.setVisible(false);
            displayReturnFlights();
        }
    }
}

    public void displayOneWayFlights() throws IOException {
        // Clear the VBox that displays the one-way flights and populate it with the available flights
	fxOneWayFlightVBox.getChildren().clear();
	for (Flight flight : departureFlights) {
    	// Load the FXML file that defines the flight view
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/hi/vidmot/verkefni/			availableFlightsOneWay-view.fxml"));
    	AnchorPane flightPane = loader.load();
   	 DisplayFlightsController controller = loader.getController();
   	 // Populate the flight view with flight information
   	 controller.fxFlightNumber.setText(flight.getFlightNumber());
	    controller.fxFlightDuration.setText(String.valueOf(flight.getFlightDuration()));
	    controller.fxFlightDepartureAirportCode.setText(flight.getDepartureAirportCode());
	    controller.fxFlightDepartureTime.setText(flight.getDepartureTime().toString());
	    controller.fxFlightArrivalAirportCode.setText(flight.getArrivalAirportCode());
	    controller.fxFlightArrivalTime.setText(flight.getArrivalTime().toString());
	    System.out.println("Class: " + fxClass.getValue());
	    // Set the price of the flight based on the selected class
	    if(fxClass.getValue().equals("Economy")) {
 	       controller.fxFlightPerPersonPrice.setText(String.format("%,d", flight.getEconomyPrice()));
		flightPrice = flight.getEconomyPrice()*(Integer)(fxNumberOfPassangers.getValue());
    }
   	 else if(fxClass.getValue().equals("Business")) {
  	      controller.fxFlightPerPersonPrice.setText(String.format("%,d", flight.getBusinessPrice()));
 	       flightPrice = flight.getBusinessPrice()*(Integer)(fxNumberOfPassangers.getValue());
 	       System.out.println(flightPrice);
	    }
	    controller.fxFlightTotalPrice.setText(String.format("%,d", flightPrice));
	    String airlineName = flight.getAirline();
	    System.out.println(airlineName);
	    // Load the airline logo image and set it in the flight view
  	  String imagePath = "C:\\Users\\snæfríður\\IdeaProjects\\Flugverkefni\\src\\main\\files\\airlines\\" + airlineName + ".png";
 	   Image image = new Image(imagePath);
	    controller.fxAirlaneLogoImage.setImage(image);;
	    // Load the take-off and landing images and set them in the flight view
	    controller.fxTakeOffImage.setImage(new Image("C:\\Users\\snæfríður\\IdeaProjects\\Flugverkefni\\src\\main\\files\\taking_off.jpg"));
 	   controller.fxLandingImage.setImage(new Image("C:\\Users\\snæfríður\\IdeaProjects\\Flugverkefni\\src\\main\\files\\plane_landing.jpg"));
 	   // Set the flight toggle button in the toggle group and add it to the VBox
 	   controller.fxChosenFlight.setToggleGroup(oneWayToggleGroup);
 	   controller.fxChosenFlight.setUserData(flight);
 	   fxOneWayFlightVBox.getChildren().add(flightPane);
	}
private void displayReturnFlights() throws IOException {
    // clear the VBox containers for departure and arrival flights
    fxDepartureFlightVBox.getChildren().clear();
    fxArrivalFlightVBox.getChildren().clear();
    // iterate through each departure flight
    for (Flight flight : departureFlights) {
        // load the FXML view for a flight
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hi/vidmot/verkefni/availableFlightsReturn-view.fxml"));
        AnchorPane flightPane = loader.load();
        // get the controller for the view
        DisplayFlightsController controller = loader.getController();
        // set the flight details in the view's labels
        controller.fxFlightNumber.setText(flight.getFlightNumber());
        controller.fxFlightDuration.setText(String.valueOf(flight.getFlightDuration()));
        controller.fxFlightDepartureAirportCode.setText(flight.getDepartureAirportCode());
        controller.fxFlightDepartureTime.setText(flight.getDepartureTime().toString());
        controller.fxFlightArrivalAirportCode.setText(flight.getArrivalAirportCode());
        controller.fxFlightArrivalTime.setText(flight.getArrivalTime().toString());
        System.out.println("Class: " + fxClass.getValue());
        // check the class selected by the user and set the flight price accordingly
        if(fxClass.getValue().equals("Economy")) {
            controller.fxFlightPerPersonPrice.setText(String.format("%,d", flight.getEconomyPrice()));
            flightPrice = flight.getEconomyPrice()*(Integer)(fxNumberOfPassangers.getValue());
        }
        else if(fxClass.getValue().equals("Business")) {
            controller.fxFlightPerPersonPrice.setText(String.format("%,d", flight.getBusinessPrice()));
            flightPrice = flight.getBusinessPrice()*(Integer)(fxNumberOfPassangers.getValue());
            System.out.println(flightPrice);
        }
        // set the total price of the flight
        controller.fxFlightTotalPrice.setText(String.format("%,d", flightPrice));
        String airlineName = flight.getAirline();
        System.out.println(airlineName);
        // load the image for the airline logo
        String imagePath = "C:\\Users\\snæfríður\\IdeaProjects\\Flugverkefni\\src\\main\\files\\airlines\\" + airlineName + ".png";
        Image image = new Image(imagePath);
        controller.fxAirlaneLogoImage.setImage(image);;
        // load images for take-off and landing
        controller.fxTakeOffImage.setImage(new Image("C:\\Users\\snæfríður\\IdeaProjects\\Flugverkefni\\src\\main\\files\\taking_off.jpg"));
        controller.fxLandingImage.setImage(new Image("C:\\Users\\snæfríður\\IdeaProjects\\Flugverkefni\\src\\main\\files\\plane_landing.jpg"));
        // set the flight toggle button to be part of the departure toggle group and set its user data to the flight object
        controller.fxChosenFlight.setToggleGroup(departureToggleGroup);
        controller.fxChosenFlight.setUserData(flight);
        // add the flight view to the departure VBox container
        fxDepartureFlightVBox.getChildren().add(flightPane);
    }
    // iterate through each arrival flight
    for (Flight flight : arrivalFlights) {
        // load the FXML view for a flight
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/hi/vidmot/verkefni/availableFlightsReturn-view.fxml"));
        AnchorPane flightPane = loader.load

private void saveCurrentFlight(Flight flight) throws SQLException {
    // Call the flightRepository to update the seat availability of the flight with the chosen class and number of passengers
    flightRepository.updateSeats(flight.getId(), fxClass.getValue(), fxNumberOfPassangers.getValue());
    
    // Add the selected flight to the list of selected flights
    selectedFlights.add(flight);
}


    private void resetUIValues() {
        fxBookFlights.setVisible(false);
        fxDepartureAirport.getItems().clear(); //clear comboboxes
        fxArrivalAirport.getItems().clear();
        fxOneWayFlightVBox.getChildren().clear();
        fxArrivalFlightVBox.getChildren().clear();
        fxDepartureFlightVBox.getChildren().clear();
        fxDepartureAirport.setValue(null);
        fxDepartureAirport.setPromptText("Brottfararstaður");
        fxArrivalAirport.setValue(null);
        fxArrivalAirport.setPromptText("Áfangastaður");
        fxClass.setValue(null);
        fxClass.setPromptText("Farrými");
        fxNumberOfPassangers.setValue(null);
        fxNumberOfPassangers.setPromptText("Fjöldi farþega");
        fxDepartureDate.setValue(null);
        fxDepartureDate.setPromptText("Brottfarardagur");
        fxArrivalDate.setValue(null);
        fxArrivalDate.setPromptText("Komudagur");

    }




    public List<Flight> findDepartureFlights(String rey, String bos, Date date) {return null; }
}