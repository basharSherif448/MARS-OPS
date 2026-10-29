package colony.facilities;

import colony.core.Result;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ResourceManager {
    private final List<Facility> facilities;   // aggregation: the colony's facilities (shared list)
    private AllocationPolicy policy;

    public ResourceManager(List<Facility> facilities, AllocationPolicy policy) {
        this.facilities = facilities;
        this.policy = policy;
    }

    public AllocationPolicy getPolicy() { return policy; }
    public void setPolicy(AllocationPolicy policy) { this.policy = policy; }

    public int getTotal(ResourceType type) {
        int sum = 0;
        for (Facility f : facilities) if (f.isOperational()) sum += f.getStock(type);
        return sum;
    }

    public Map<ResourceType, Integer> getTotals() {
        Map<ResourceType, Integer> totals = new EnumMap<>(ResourceType.class);
        for (ResourceType t : ResourceType.values()) totals.put(t, getTotal(t));
        return totals;
    }

    public boolean canSupply(Map<ResourceType, Integer> request) { return check(request).success(); }

    private Result check(Map<ResourceType, Integer> request) {
        StringBuilder missing = new StringBuilder();
        for (Map.Entry<ResourceType, Integer> e : request.entrySet()) {
            int available = getTotal(e.getKey());
            if (e.getValue() < 0) return Result.fail("Invalid request: negative quantity of " + e.getKey());
            if (available < e.getValue())
                missing.append(missing.length() > 0 ? "; " : "").append(e.getKey())
                       .append(" required ").append(e.getValue()).append(", available ").append(available);
        }
        return missing.length() == 0 ? Result.ok() : Result.fail("Insufficient resources: " + missing);
    }

    public Result consume(Map<ResourceType, Integer> request) {
        Result check = check(request);
        if (check.isFailure()) return check;
        for (Map.Entry<ResourceType, Integer> e : request.entrySet()) drain(e.getKey(), e.getValue());
        return Result.ok();
    }

    public int consumeUpTo(ResourceType type, int amount) {
        return drain(type, Math.min(amount, getTotal(type)));
    }

    private int drain(ResourceType type, int amount) {
        int remaining = amount;
        for (Facility f : policy.order(facilities)) {
            if (remaining == 0) break;
            if (!f.isOperational()) continue;
            remaining -= f.getStore().removeUpTo(type, remaining);
        }
        return amount - remaining;
    }
}
