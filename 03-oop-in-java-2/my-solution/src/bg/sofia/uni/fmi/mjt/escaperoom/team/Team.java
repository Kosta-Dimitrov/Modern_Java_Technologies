package bg.sofia.uni.fmi.mjt.escaperoom.team;

import bg.sofia.uni.fmi.mjt.escaperoom.rating.Ratable;

import java.util.Objects;

public class Team implements Ratable {

    private final String name;
    private final TeamMember[] members;
    private int rating;

    private Team(String name, TeamMember[] members) {
        this.name = name;
        this.members = members;
        this.rating = 0;
    }

    public static Team of(String name, TeamMember[] members) {

        return new Team(name, members);
    }

    /**
     * Updates the team rating by adding the specified points to it.
     *
     * @param points the points to be added to the team rating.
     * @throws IllegalArgumentException if the points are negative.
     */
    public void updateRating(int points) throws IllegalArgumentException {

        if (points < 0) {
            throw new IllegalArgumentException();
        }
        this.rating += points;
    }

    /**
     * Returns the team name.
     */
    public String getName() {
        return this.name;
    }

    @Override
    public double getRating() {
        return this.rating;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Team other = (Team) o;
        return Objects.equals(this.name, other.name);
    }
}
