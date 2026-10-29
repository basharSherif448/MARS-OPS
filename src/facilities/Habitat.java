package colony.facilities;


public class Habitat extends Facility {
    private final LifeSupportSystem lifeSupport = new LifeSupportSystem();  // composition

    public Habitat(String name, int capacity) { super(name, capacity); }

    @Override public String details() { return "life support: " + lifeSupport; }

    public LifeSupportSystem getLifeSupport() { return lifeSupport; }
    public int getOxygen() { return getStock(ResourceType.OXYGEN); }

    public int loseOxygen(int amount) { return getStore().removeUpTo(ResourceType.OXYGEN, amount); }
}
