package colony.facilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Facility implements Location, Repairable {
    private final String name;
    private FacilityStatus status = FacilityStatus.OPERATIONAL;
    private final ResourceStore store;                       // composition: dies with the facility
    private final List<Equipment> equipment = new ArrayList<>(); // aggregation: equipment exists independently

    protected Facility(String name, int capacity) {
        this.name = name;
        this.store = new ResourceStore(capacity);
    }

    @Override public String getName() { return name; }
    public FacilityStatus getStatus() { return status; }
    public void setStatus(FacilityStatus status) { this.status = status; }
    public boolean isOperational() { return status == FacilityStatus.OPERATIONAL; }
    public ResourceStore getStore() { return store; }
    public int getStock(ResourceType type) { return store.get(type); }

    public void addEquipment(Equipment e) { equipment.add(e); }
    public List<Equipment> getEquipment() { return Collections.unmodifiableList(equipment); }
    public boolean hasUsableEquipment(EquipmentType type) {
        for (Equipment e : equipment) if (e.getType() == type && e.isUsable()) return true;
        return false;
    }

    public boolean providesCommunication() { return false; }
    public boolean isStorage() { return false; }
    public String details() { return ""; }

    @Override public boolean isDamaged() { return status == FacilityStatus.UNAVAILABLE; }
    @Override public void repair() { if (status == FacilityStatus.UNAVAILABLE) status = FacilityStatus.OPERATIONAL; }

    @Override public String toString() { return name; }
}
