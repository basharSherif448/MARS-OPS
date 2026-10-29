package colony.facilities;

import java.util.EnumMap;
import java.util.Map;

public class ResourceStore {
    private final Map<ResourceType, Integer> amounts = new EnumMap<>(ResourceType.class);
    private final int capacity;

    public ResourceStore(int capacity) { this.capacity = capacity; }

    public int get(ResourceType type) { return amounts.getOrDefault(type, 0); }
    public int getCapacity() { return capacity; }

    public int totalUnits() {
        int sum = 0;
        for (int v : amounts.values()) sum += v;
        return sum;
    }

    public int add(ResourceType type, int amount) {
        if (amount < 0) throw new IllegalArgumentException("amount < 0");
        int added = Math.min(amount, capacity - totalUnits());
        amounts.merge(type, added, Integer::sum);
        return added;
    }

    public void remove(ResourceType type, int amount) {
        if (amount < 0 || amount > get(type))
            throw new IllegalArgumentException("Cannot remove " + amount + " " + type + " (have " + get(type) + ")");
        amounts.put(type, get(type) - amount);
    }

    public int removeUpTo(ResourceType type, int amount) {
        int removed = Math.min(Math.max(amount, 0), get(type));
        amounts.put(type, get(type) - removed);
        return removed;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        for (ResourceType t : ResourceType.values())
            if (get(t) > 0) sb.append(sb.length() > 0 ? ", " : "").append(t).append('=').append(get(t));
        return sb.length() == 0 ? "empty" : sb.toString();
    }
}
