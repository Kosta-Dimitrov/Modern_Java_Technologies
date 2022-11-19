package bg.sofia.uni.fmi.mjt.smartfridge.comparators;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

import java.util.Comparator;

public class ExpirationDateComparator implements Comparator<Storable> {
    public int compare(Storable s1, Storable s2) {
        return s1.getExpiration().compareTo(s2.getExpiration());
    }
}
