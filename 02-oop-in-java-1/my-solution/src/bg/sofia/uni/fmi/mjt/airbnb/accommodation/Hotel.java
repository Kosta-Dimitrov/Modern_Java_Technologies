package bg.sofia.uni.fmi.mjt.airbnb.accommodation;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

import java.time.Duration;
import java.time.LocalDateTime;

public class Hotel implements  Bookable{

    private final static  String HOTEL_PREFIX = "HOT";

    private static int counter = 0;
    private int Id;
    private final double pricePerNight;
    private final Location location;
    private LocalDateTime bookedUntil;
    private LocalDateTime bookedFrom;

    private boolean isBooked;

    public Hotel(Location location, double pricePerNight){
        this.location = location;
        this.pricePerNight = pricePerNight > 0? pricePerNight : 0;
        Id = counter;
        counter++;
        this.isBooked = false;
    }

    @Override
    public String getId() {
        return HOTEL_PREFIX + "-" + Id;
    }

    @Override
    public Location getLocation() {
        return this.location;
    }

    @Override
    public boolean isBooked() {
        return this.isBooked;
    }

    @Override
    public boolean book(LocalDateTime checkIn, LocalDateTime checkOut) {
        if(checkIn == null ||
                checkOut == null ||
                this.isBooked ||
                checkIn.isAfter(checkOut)||
                checkIn.isBefore(LocalDateTime.now())) {
            return false;
        }

        long daysBetween = Duration.between(checkIn, checkOut).toDays();

        if(daysBetween == 0){
            return false;
        }

        this.bookedFrom = checkIn;
        this.bookedUntil = checkOut;
        this.isBooked = true;

        return true;
    }

    @Override
    public double getTotalPriceOfStay() {
        if(isBooked()){
            return Duration.between(bookedFrom, bookedUntil).toDays() * this.pricePerNight;
        }
        return 0;
    }

    @Override
    public double getPricePerNight() {
        return this.pricePerNight;
    }
}
