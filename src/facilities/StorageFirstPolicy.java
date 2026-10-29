package colony.facilities;

import java.util.ArrayList;
import java.util.List;

public class StorageFirstPolicy implements AllocationPolicy {
    @Override public List<Facility> order(List<Facility> facilities) {
        List<Facility> ordered = new ArrayList<>();
        for (Facility f : facilities) if (f.isStorage()) ordered.add(f);
        for (Facility f : facilities) if (!f.isStorage()) ordered.add(f);
        return ordered;
    }
    @Override public String describe() { return "StorageFirstPolicy (storage facilities first, then insertion order)"; }
}
