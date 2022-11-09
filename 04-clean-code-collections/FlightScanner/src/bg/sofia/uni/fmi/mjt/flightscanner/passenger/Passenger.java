package bg.sofia.uni.fmi.mjt.flightscanner.passenger;

import bg.sofia.uni.fmi.mjt.flightscanner.services.StringService;

public record Passenger(String id, String name, Gender gender) {
    public Passenger {
        if (!StringService.isValid(id) || !StringService.isValid(name)) {
            throw new IllegalArgumentException();
        }
    }
}
