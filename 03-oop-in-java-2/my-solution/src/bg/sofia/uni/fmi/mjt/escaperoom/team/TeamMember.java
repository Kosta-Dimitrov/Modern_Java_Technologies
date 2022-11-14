package bg.sofia.uni.fmi.mjt.escaperoom.team;

import bg.sofia.uni.fmi.mjt.escaperoom.services.StringService;

import java.time.LocalDateTime;

public record TeamMember(String name, LocalDateTime birthday) {
    public TeamMember {
        if(!StringService.isValid(name)) {
            throw new IllegalArgumentException();
        }
    }
}
