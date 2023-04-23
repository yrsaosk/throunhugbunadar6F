package hi.vidmot.verkefni.model;

import java.util.ArrayList;
import java.util.List;

//This class represents a booking in the system.
public class Booking {
    int bookingId; //Unique ID of this booking.
    int flightId; //The ID of the flight this booking is for.
    List<Passenger> passenger = new ArrayList<>(); //Passengers associated with this booking.
    List<Booking> booking = new ArrayList<>(); //All bookings in the system.
// Constructor for creating a new booking with a list of passengers.
public Booking(List<Passenger> passenger) {
    this.flightId = flightId;
}

String flightNumber; // The flight number associated with this booking.
String contactName; // The name of the person who made this booking.
int bookingNumber; // The unique number assigned to this booking.
int numberOfPassangersBooked; // The number of passengers associated with this booking.
boolean isContact; // True if the person who made this booking is one of the passengers.

// Constructor for creating a new booking with a flight number, contact name, and booking number.
public Booking(String flightNumber, String contactName, int bookingNumber) {
    this.flightNumber = flightNumber;
    this.contactName = contactName;
    this.bookingNumber = bookingNumber;
}

// Empty constructor for creating an empty booking object.
public Booking() {

}

// Constructor for creating a new booking with a flight number, contact name, booking number, and number of passengers.
public Booking(String flightNumber, String contactName, int bookingNumber, int numberOfPassangersBooked) {
}

// Returns true if the person who made this booking is one of the passengers.
public boolean isContact() {
    return isContact;
}

// Returns the flight number associated with this booking.
public String getFlightNumber() {
    return flightNumber;
}

// Returns a string representation of this booking object.
@Override
public String toString() {
    return "Booking{" +
            "bookingId=" + bookingId +
            "flightId=" + flightId +
            ", passenger=" + passenger +
            ", booking=" + booking +
            ", flightNumber='" + flightNumber + '\'' +
            ", contactName='" + contactName + '\'' +
            ", bookingNumber=" + bookingNumber +
            ", numberOfPassangersBooked=" + numberOfPassangersBooked +
            ", isContact=" + isContact +
            '}';
}

}
