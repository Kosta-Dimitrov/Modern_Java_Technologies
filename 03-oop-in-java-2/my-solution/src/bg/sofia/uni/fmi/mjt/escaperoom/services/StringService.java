package bg.sofia.uni.fmi.mjt.escaperoom.services;

public class StringService {
    public static boolean isValid(String string) {
        return !(string == null || string.isEmpty() || string.isBlank());
    }
}
