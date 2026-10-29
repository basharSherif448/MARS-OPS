package facilities;

public class StorageFacility extends Facility {
    public StorageFacility(String name, int capacity) { super(name, capacity); }
    @Override public boolean isStorage() { return true; }
}
