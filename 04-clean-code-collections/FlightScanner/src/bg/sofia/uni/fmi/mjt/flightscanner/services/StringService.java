package bg.sofia.uni.fmi.mjt.flightscanner.services;

public class StringService {
    public static boolean isValid(String str) {
        return str != null && !str.isBlank() && !str.isEmpty();
    }
}
