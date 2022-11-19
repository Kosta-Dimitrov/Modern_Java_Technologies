package bg.sofia.uni.fmi.mjt.smartfridge;

import bg.sofia.uni.fmi.mjt.smartfridge.comparators.ExpirationDateComparator;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.FridgeCapacityExceededException;
import bg.sofia.uni.fmi.mjt.smartfridge.exception.InsufficientQuantityException;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.DefaultIngredient;
import bg.sofia.uni.fmi.mjt.smartfridge.ingredient.Ingredient;
import bg.sofia.uni.fmi.mjt.smartfridge.recipe.Recipe;
import bg.sofia.uni.fmi.mjt.smartfridge.storable.Storable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class SmartFridge implements SmartFridgeAPI {
    private final int totalCapacity;
    private int currentQuantity;
    private Map<String, Queue<Storable>> storage = new HashMap<>();

    public SmartFridge(int totalCapacity) {
        this.totalCapacity = totalCapacity;
        this.currentQuantity = 0;
    }

    @Override
    public <E extends Storable> void store(E item, int quantity) throws FridgeCapacityExceededException {
        if (item == null || quantity <= 0) {
            throw new IllegalArgumentException();
        }
        if (quantity + this.currentQuantity > this.totalCapacity) {
            throw new FridgeCapacityExceededException();
        }

        if (!storage.containsKey(item.getName())) {
            storage.put(item.getName(), new PriorityQueue<>(new ExpirationDateComparator()));
        }

        storage.get(item.getName()).addAll(Collections.nCopies(quantity, item));
        this.currentQuantity += quantity;
    }

    @Override
    public List<? extends Storable> retrieve(String itemName) {
        if (itemName == null || itemName.isEmpty() || itemName.isBlank()) {
            throw new IllegalArgumentException();
        }

        List<Storable> result = new ArrayList<>();

        if (this.storage.containsKey(itemName)) {
            result.addAll(storage.get(itemName));
            this.currentQuantity -= result.size();
            this.storage.remove(itemName);
        }

        return result;
    }

    @Override
    public List<? extends Storable> retrieve(String itemName, int quantity) throws InsufficientQuantityException {
        if (itemName == null || itemName.isEmpty() || itemName.isBlank() || quantity <= 0) {
            throw new IllegalArgumentException();
        }

        List<Storable> result = new ArrayList<>();

        if (this.storage.containsKey(itemName)) {
            if (this.storage.get(itemName).size() < quantity) {
                throw new InsufficientQuantityException();
            }

            for (int i = 0; i < quantity; i++) {
                result.add(this.storage.get(itemName).poll());
            }
        } else {
            throw new InsufficientQuantityException();
        }

        return result;
    }

    @Override
    public int getQuantityOfItem(String itemName) {
        if (itemName == null || itemName.isEmpty() || itemName.isBlank()) {
            throw new IllegalArgumentException();
        }

        if (this.storage.containsKey(itemName)) {
            return this.storage.get(itemName).size();
        }

        return 0;
    }

    @Override
    public Iterator<Ingredient<? extends Storable>> getMissingIngredientsFromRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException();
        }

        List<Ingredient<? extends Storable>> result = new ArrayList<>();

        List<? extends Storable> expired = this.removeExpired();

        for (Ingredient<? extends Storable> ingredient : recipe.getIngredients()) {
            if (!this.storage.containsKey(ingredient.item().getName())) {
                result.add(ingredient);
            }

            int counter = this.getQuantityOfItem(ingredient.item().getName());

            if (counter < ingredient.quantity()) {
                result.add(new DefaultIngredient<>(ingredient.item(), ingredient.quantity() - counter));
            }
        }

        this.addAll(expired);

        return result.iterator();

    }

    @Override
    public List<? extends Storable> removeExpired() {
        List<Storable> result = new ArrayList<>();

        for (Queue<Storable> items : this.storage.values()) {
            while (items.peek().isExpired()) {
                result.add(items.poll());
                this.currentQuantity--;
            }
        }

        return result;
    }

    private Map.Entry<String, Queue<Storable>> getItem(String name) {
        for (Map.Entry<String, Queue<Storable>> kvp : this.storage.entrySet()) {
            if (kvp.getKey().equals(name)) {
                return kvp;
            }
        }

        return null;
    }

    private void addAll(List<? extends Storable> items) {
        for (Storable i : items) {
            if (!storage.containsKey(i.getName())) {
                storage.put(i.getName(), new PriorityQueue<>(new ExpirationDateComparator()));
            }

            storage.get(i.getName()).add(i);
            this.currentQuantity++;
        }
    }
}