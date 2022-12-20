package bg.sofia.uni.fmi.mjt.mail.services;

public class StringService {
    public static boolean isValidString(String string) {
        return string != null && !string.isBlank() && !string.isEmpty();
    }

    public static boolean areAllStringsValid(String... args) {
        for (String s : args) {
            if (!isValidString(s)) {
                return false;
            }
        }
        return true;
    }
}
