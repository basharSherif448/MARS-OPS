package facilities;

public class Equipment implements Repairable {
    private final String name;
    private final EquipmentType type;
    private boolean damaged;

    public Equipment(String name, EquipmentType type) { 
        this.name = name; 
        this.type = type; 
    }

    @Override public String getName() { return name; }
    public EquipmentType getType() { return type; }
    @Override public boolean isDamaged() { return damaged; }
    public void damage() { damaged = true; }
    @Override public void repair() { damaged = false; }
    public boolean isUsable() { return !damaged; }
    public boolean isOperational() { return isUsable(); }

    public void setOperational(boolean operational) {
        if (operational) {
            repair();
        } else {
            damage();
        }
    }

    @Override public String toString() { return name + (damaged ? " [DAMAGED]" : ""); }
}