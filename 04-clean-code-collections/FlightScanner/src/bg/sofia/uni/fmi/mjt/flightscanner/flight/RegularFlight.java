package bg.sofia.uni.fmi.mjt.flightscanner.flight;

import bg.sofia.uni.fmi.mjt.flightscanner.airport.Airport;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.FlightCapacityExceededException;
import bg.sofia.uni.fmi.mjt.flightscanner.exception.InvalidFlightException;
import bg.sofia.uni.fmi.mjt.flightscanner.passenger.Passenger;
import bg.sofia.uni.fmi.mjt.flightscanner.services.StringService;

import java.util.*;

public class RegularFlight implements Flight {

    private final String flightId;
    private final Airport from;
    private final Airport to;
    private final int totalCapacity;
    private Set<Passenger> passengers;


    private RegularFlight(String flightId, Airport from, Airport to, int totalCapacity) {
        if (!StringService.isValid(flightId) ||
            from == null ||
            to == null ||
            totalCapacity < 0) {
            throw new IllegalArgumentException();
        }

        if (from.equals(to)) {
            throw new InvalidFlightException();
        }

        this.flightId = flightId;
        this.to = to;
        this.from = from;
        this.totalCapacity = totalCapacity;
        this.passengers = new HashSet<>(totalCapacity);
    }

    public static RegularFlight of(String flightId, Airport from, Airport to, int totalCapacity) {
        return new RegularFlight(flightId, from, to, totalCapacity);
    }

    public String getFlightId() {
        return this.flightId;
    }

    @Override
    public Airport getFrom() {
        return this.from;
    }

    @Override
    public Airport getTo() {
        return this.to;
    }

    @Override
    public void addPassenger(Passenger passenger) throws FlightCapacityExceededException {
        if (this.getFreeSeatsCount() == 0) {
            throw new FlightCapacityExceededException();
        }

        this.passengers.add(passenger);
    }

    @Override
    public void addPassengers(Collection<Passenger> passengers) throws FlightCapacityExceededException {
        if (passengers == null) {
            throw new IllegalArgumentException();
        }
        
        if (this.getFreeSeatsCount() < passengers.size()) {
            throw new FlightCapacityExceededException();
        }

        this.passengers.addAll(passengers);
    }

    @Override
    public Collection<Passenger> getAllPassengers() {
        return Collections.unmodifiableCollection(this.passengers);
    }

    @Override
    public int getFreeSeatsCount() {
        return this.totalCapacity - this.passengers.size();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return Objects.equals(this.flightId, ((RegularFlight) obj).flightId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.flightId);
    }
}
