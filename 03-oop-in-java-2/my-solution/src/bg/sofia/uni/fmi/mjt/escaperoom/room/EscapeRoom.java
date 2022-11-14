package bg.sofia.uni.fmi.mjt.escaperoom.room;

import bg.sofia.uni.fmi.mjt.escaperoom.rating.Ratable;

import java.util.Objects;

public class EscapeRoom implements Ratable {
    private final String name;
    private final Theme theme;
    private final Difficulty difficulty;
    private final int maxTimeToEscape;
    private final double priceToPlay;
    private final int maxReviewsCount;
    private Review[] reviews;
    private int reviewIndex;
    private double rating;
    private int ratingCount;

    public EscapeRoom(String name, Theme theme, Difficulty difficulty, int maxTimeToEscape, double priceToPlay,
                      int maxReviewsCount) {
        this.name = name;
        this.theme = theme;
        this.difficulty = difficulty;
        this.maxTimeToEscape = maxTimeToEscape;
        this.priceToPlay = priceToPlay;
        this.maxReviewsCount = maxReviewsCount;
        this.reviews = new Review[maxReviewsCount];
        this.reviewIndex = 0;
        this.ratingCount = 0;
        this.rating = 0;
    }

    @Override
    public double getRating() {
        double sum = 0;

        for (Review review : this.reviews) {
            sum += review.rating();
        }

        return this.ratingCount > 0 ? sum / this.ratingCount : 0;
    }

    public String getName() {
        return this.name;
    }

    public Review[] getReviews() {
        int reviewsCount = Math.min(this.ratingCount, maxReviewsCount);
        Review[] result = new Review[reviewsCount];

        int firstReview = (this.ratingCount < maxReviewsCount) ? 0 : this.reviewIndex;

        for (int i = 0; i < reviewsCount; i++) {
            result[i] = reviews[(firstReview + i) % maxReviewsCount];
        }

        return result;
    }

    public void addReview(Review review) {
        this.reviews[this.reviewIndex++] = review;

        if (this.reviewIndex == maxReviewsCount) {
            this.reviewIndex = 0;
        }

        this.rating += review.rating();
        this.ratingCount++;
    }

    public int getMaxTimeToEscape() {
        return this.maxTimeToEscape;
    }

    /**
     * Returns the difficulty of the escape room.
     */
    public Difficulty getDifficulty() {
        return difficulty;
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
        EscapeRoom escapeRoom = (EscapeRoom) o;
        return this.name.equalsIgnoreCase(escapeRoom.name);
    }
}
