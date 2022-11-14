package bg.sofia.uni.fmi.mjt.escaperoom;

import bg.sofia.uni.fmi.mjt.escaperoom.exception.PlatformCapacityExceededException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.RoomNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.exception.TeamNotFoundException;
import bg.sofia.uni.fmi.mjt.escaperoom.room.EscapeRoom;
import bg.sofia.uni.fmi.mjt.escaperoom.room.Review;
import bg.sofia.uni.fmi.mjt.escaperoom.services.StringService;
import bg.sofia.uni.fmi.mjt.escaperoom.team.Team;

public class EscapeRoomPlatform implements EscapeRoomAdminAPI, EscapeRoomPortalAPI {
    private final Team[] teams;
    private final int maxCapacity;
    private EscapeRoom[] escapeRooms;
    private int currentNumberOfEscapeRooms;

    public EscapeRoomPlatform(Team[] teams, int maxCapacity) {
        this.teams = teams;
        this.maxCapacity = maxCapacity;
        this.escapeRooms = new EscapeRoom[maxCapacity];
        this.currentNumberOfEscapeRooms = 0;
    }

    @Override
    public void addEscapeRoom(EscapeRoom room)
        throws PlatformCapacityExceededException, IllegalArgumentException, RoomAlreadyExistsException {
        if (room == null) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < this.maxCapacity; i++) {
            if (escapeRooms[i] != null && this.escapeRooms[i].equals(room)) {
                throw new RoomAlreadyExistsException();
            }
        }

        if (this.currentNumberOfEscapeRooms + 1 == this.maxCapacity) {
            throw new PlatformCapacityExceededException();
        }

        for (int i = 0; i < this.maxCapacity; i++) {
            if (this.escapeRooms[i] == null) {
                this.escapeRooms[i] = room;
                this.currentNumberOfEscapeRooms++;
                break;
            }
        }
    }

    @Override
    public void removeEscapeRoom(String roomName) throws RoomNotFoundException, IllegalArgumentException {
        if (!StringService.isValid(roomName)) {
            throw new IllegalArgumentException();
        }

        boolean isFound = false;

        for (int i = 0; i < maxCapacity; i++) {
            if (escapeRooms[i] != null && escapeRooms[i].getName().equals(roomName)) {
                escapeRooms[i] = null;
                isFound = true;
                break;
            }
        }

        if (isFound) {
            currentNumberOfEscapeRooms--;
        }

        throw new RoomNotFoundException();
    }

    @Override
    public EscapeRoom[] getAllEscapeRooms() {
        EscapeRoom[] result = new EscapeRoom[this.currentNumberOfEscapeRooms];
        int currentIndex = 0;

        for (int i = 0; i < currentNumberOfEscapeRooms; i++) {
            if (this.escapeRooms[i] != null) {
                result[currentIndex] = this.escapeRooms[i];
                currentIndex++;
            }
        }

        return result;
    }

    @Override
    public void registerAchievement(String roomName, String teamName, int escapeTime)
        throws RoomNotFoundException, TeamNotFoundException {
        EscapeRoom room = this.getEscapeRoomByName(roomName);
        Team team = this.getTeam(teamName);

        if (escapeTime <= 0 || escapeTime > room.getMaxTimeToEscape()) {
            throw new IllegalArgumentException();
        }

        int bonusPoints = 0;
        double speed = escapeTime / (double) room.getMaxTimeToEscape();
        if (speed <= 0.5) {
            bonusPoints = 2;
        } else if (speed <= 0.75) {
            bonusPoints = 1;
        }

        team.updateRating(room.getDifficulty().getRank() + bonusPoints);
    }

    @Override
    public EscapeRoom getEscapeRoomByName(String roomName) throws RoomNotFoundException {
        if (!StringService.isValid(roomName)) {
            throw new IllegalArgumentException();
        }

        for (EscapeRoom room : this.escapeRooms) {
            if (room != null && room.getName().equals(roomName)) {
                return room;
            }
        }
        throw new RoomNotFoundException();
    }

    @Override
    public void reviewEscapeRoom(String roomName, Review review)
        throws RoomNotFoundException, IllegalArgumentException {
        if (review == null) {
            throw new IllegalArgumentException();
        }

        EscapeRoom room = this.getEscapeRoomByName(roomName);

        room.addReview(review);
    }

    @Override
    public Review[] getReviews(String roomName) throws RoomNotFoundException, IllegalArgumentException {
        EscapeRoom room = this.getEscapeRoomByName(roomName);

        if (room == null) {
            throw new RoomNotFoundException();
        }

        return room.getReviews();
    }

    @Override
    public Team getTopTeamByRating() {
        double maxRating = 0;
        Team maxRatingTeam = null;

        for (Team team : this.teams) {
            if (team != null && team.getRating() > maxRating) {
                maxRatingTeam = team;
                maxRating = team.getRating();
            }
        }

        return maxRatingTeam;
    }

    private Team getTeam(String name) throws TeamNotFoundException {
        if (!StringService.isValid(name)) {
            throw new IllegalArgumentException();
        }

        for (Team team : this.teams) {
            if (team != null && team.getName().equals(name)) {
                return team;
            }
        }

        throw new TeamNotFoundException();
    }
}
