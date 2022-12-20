package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.services.StringService;

import static bg.sofia.uni.fmi.mjt.mail.constants.Constants.RATING_PRIORITY_MAX_VALUE;
import static bg.sofia.uni.fmi.mjt.mail.constants.Constants.RATING_PRIORITY_MIN_VALUE;

public record Rule(String folderPath, String definition, int priority) {
    public Rule {
        if (StringService.isValidString(folderPath)) {
            throw new IllegalArgumentException("Folder cannot be null or blank");
        }

        if (!StringService.isValidString(definition)) {
            throw new IllegalArgumentException("Not valid definition for rule");
        }

        if (priority < RATING_PRIORITY_MIN_VALUE || priority > RATING_PRIORITY_MAX_VALUE) {
            throw new IllegalArgumentException("Not valid rating for rule");
        }
    }
}
