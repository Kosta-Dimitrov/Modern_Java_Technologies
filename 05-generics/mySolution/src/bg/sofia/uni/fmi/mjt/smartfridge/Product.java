package bg.sofia.uni.fmi.mjt.smartfridge;

import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

public record Product(Storable storable, int quantity) {
}
