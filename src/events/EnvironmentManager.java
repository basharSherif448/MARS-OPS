package events;

import missions.Mission;
import missions.ExplorationMission;

import java.util.ArrayList;
import java.util.List;

public class EnvironmentManager {
    private List<EnvironmentalCondition> conditions;

    public EnvironmentManager() {
        this.conditions = new ArrayList<>();
    }

    public void addCondition(EnvironmentalCondition condition) {
        if (condition != null) {
            this.conditions.add(condition);
        }
    }

    public void update() {
        update(1);
    }

    // FIX: Simulation.advanceTime(units) calls update(units) - overload added so the
    // storm really lasts "units" time units instead of 1.
    public void update(int units) {
        List<EnvironmentalCondition> copy = new ArrayList<>(conditions);
        for (EnvironmentalCondition condition : copy) {
            condition.update(units);
            if (!condition.isActive()) {
                conditions.remove(condition);
            }
        }
    }

    public boolean blocks(Mission mission) {
        if (mission == null) return false;

        for (EnvironmentalCondition condition : conditions) {
            if (condition.isActive()) {
                if (condition.getType() == EnvironmentType.DUST_STORM) {
                    if (mission instanceof ExplorationMission) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<EnvironmentalCondition> getActiveConditions() {
        List<EnvironmentalCondition> activeList = new ArrayList<>();
        for (EnvironmentalCondition c : conditions) {
            if (c.isActive()) {
                activeList.add(c);
            }
        }
        return activeList;
    }
}
