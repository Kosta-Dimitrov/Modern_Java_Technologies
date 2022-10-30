package bg.sofia.uni.fmi.mjt.airbnb.filter;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.location.Location;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;

public class LocationCriterion implements Criterion{
    private final Location currentLocation;
    private final double maxDistance;

    public LocationCriterion(Location currentLocation, double maxDistance){
        this.currentLocation = currentLocation;
        this.maxDistance = maxDistance > 0? maxDistance : 0;
    }

    @Override
    public boolean check(Bookable bookable) {
        if(bookable == null){
            return false;
        }

        double distanceX = currentLocation.getX() - bookable.getLocation().getX();
        double distanceY = currentLocation.getY() - bookable.getLocation().getY();

        return this.maxDistance >= Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2)) ;
    }
}
