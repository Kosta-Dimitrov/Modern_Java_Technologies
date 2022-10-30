package bg.sofia.uni.fmi.mjt.airbnb.filter;

import bg.sofia.uni.fmi.mjt.airbnb.accommodation.Bookable;
import com.sun.jdi.connect.Connector;

public class PriceCriterion  implements Criterion{

    private final double minPrice;
    private final double maxPrice;

    public PriceCriterion(double minPrice, double maxPrice){
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    @Override
    public boolean check(Bookable bookable) {
        if(bookable == null){
            return false;
        }

        if(this.minPrice <= 0 || this.maxPrice <= 0 || this.minPrice > this.maxPrice){
            return false;
        }

        return this.maxPrice >= bookable.getPricePerNight() && this.minPrice <= bookable.getPricePerNight();
    }
}
