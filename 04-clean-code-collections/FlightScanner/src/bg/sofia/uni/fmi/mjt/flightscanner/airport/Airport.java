package bg.sofia.uni.fmi.mjt.flightscanner.airport;

import bg.sofia.uni.fmi.mjt.flightscanner.services.StringService;

public record Airport(String id) {
    public Airport {
        if (!StringService.isValid(id)) {
            throw new IllegalArgumentException();
        }
    }
}
