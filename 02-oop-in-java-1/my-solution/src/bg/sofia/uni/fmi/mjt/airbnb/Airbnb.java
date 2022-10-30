package bg.sofia.uni.fmi.mjt.airbnb;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import bg.sofia.uni.fmi.mjt.airbnb.filter.Criterion;

public class Airbnb  implements  AirbnbAPI{
    private final Bookable[] accommodations;
    public Airbnb(Bookable[] accomodations){
        this.accommodations = accomodations;
    }

    @Override
    public Bookable findAccommodationById(String id) {
        if(id == null || id.isBlank()){
            return null;
        }

        for(int i = 0; i < accommodations.length; i++){
            if(accommodations[i].getId().equalsIgnoreCase(id)){
                return accommodations[i];
            }
        }

        return null;
    }

    @Override
    public double estimateTotalRevenue() {
        double totalSum = 0;

        for(int i = 0; i < accommodations.length; i++){
            totalSum += accommodations[i].getTotalPriceOfStay();
        }

        return totalSum;
    }

    @Override
    public long countBookings() {
        long totalBookings = 0;

        for(int i = 0; i < accommodations.length; i++){
            if(accommodations[i].isBooked()){
                totalBookings++;
            }
        }

        return totalBookings;
    }

    @Override
    public Bookable[] filterAccommodations(Criterion... criteria) {

        if(criteria.length == 0){
            return this.accommodations;
        }

        if(this.accommodations.length == 0){
            return null;
        }

        boolean[] filtered = new boolean[this.accommodations.length];
        int numberOfFiltered = 0;
        boolean isFiltered;

        for(int i = 0; i < this.accommodations.length; i++){
            isFiltered = true;
            for(int j = 0; j < criteria.length; j++){

                if(!criteria[j].check(this.accommodations[i])){

                    isFiltered = false;
                    break;
                }
            }

            if(isFiltered){
                filtered[i] = true;
                numberOfFiltered++;
            }
        }

        if(numberOfFiltered == 0){
            return null;
        }

        Bookable[] result = new Bookable[numberOfFiltered];
        int counter = 0;

        for(int i = 0; i < filtered.length; i++){
            if(filtered[i]) {

                result[counter] = this.accommodations[i];
                counter++;
            }
        }

        return result;
    }
}
