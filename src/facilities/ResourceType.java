package facilities;

public enum ResourceType { OXYGEN, WATER, FOOD, FUEL, BATTERY_POWER, MEDICAL_SUPPLIES, SPARE_PARTS;

    public static ResourceType fromName(String name) {
        if (name == null) {
            return null;
        }
        String normalizedName = name.replace("_", "").replace(" ", "");
        for (ResourceType type : values()) {
            String normalizedType = type.name().replace("_", "");
            if (normalizedType.equalsIgnoreCase(normalizedName)) {
                return type;
            }
        }
        return null;
    } 
                         
}
