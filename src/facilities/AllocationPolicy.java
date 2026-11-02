package facilities;

import java.util.List;

public interface AllocationPolicy {
    List<Facility> order(List<Facility> facilities);
    String describe();
}
