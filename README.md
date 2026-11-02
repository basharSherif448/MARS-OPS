# MARS-OPS: Autonomous Mars Colony Management & Emergency Simulator

Alexandria University — Faculty of Engineering  
Computer and Communication Engineering — Programming II (Fall 2026)  
Lab 5: MARS-OPS

---

## 1. System Overview and Architecture

**MARS-OPS** is an autonomous Mars colony management and emergency simulation software developed in Java. The system models the complex interactions between human personnel, autonomous robotic units, facilities, resources, time-advanced missions, environmental weather disruptions, and emergency response operations.

The codebase is organized into four collaborating packages:
- `agents`: Defines human personnel (`Human`, `Scientist`, `Engineer`, `MedicalOfficer`) and autonomous robotic units (`Robot`, `ExplorationRover`, `RepairRobot`, `Drone`) under a common `Agent` interface.
- `facilities`: Models the physical infrastructure (`Facility`, `Habitat`, `Laboratory`, `StorageFacility`, `PowerStation`, `CommunicationStation`), storage units (`ResourceStore`), equipment (`Equipment`), and the colony-wide resource coordinator (`ResourceManager`).
- `missions`: Controls the mission lifecycle (`Mission`, `ExplorationMission`, `RepairMission`, `ResearchMission`, `RescueMission`), simulation time advancement (`Simulation`), and colony orchestration (`MarsColony`).
- `events`: Handles environmental weather anomalies (`EnvironmentManager`, `EnvironmentalCondition`) and crisis response (`Emergency`, `EmergencyManager`, `OxygenLeakEmergency`, `FireEmergency`, `MedicalEmergency`, `RoverBreakdownEmergency`).

---

## 2. Object Relationships & UML Design

The design incorporates the four core UML relationship types:

### 1. Association
- **Definition**: Represents objects that interact with or use one another without owning their lifecycles.
- **MARS-OPS Example**: An `Engineer` or `RepairRobot` repairs a `Facility` or `Equipment`. An `Agent` checks whether it can perform a `Mission` (`canPerform(mission)`) or handle an `Emergency` (`canHandle(emergency)`).
- **Java Implementation**: Methods receive parameter references to other classes (e.g., `engineer.repair(facility)`).

### 2. Aggregation
- **Definition**: A whole-part relationship ("has-a") where the parts can exist independently of the whole.
- **MARS-OPS Example**: 
  - `MarsColony` aggregates `List<Agent>` and `List<Facility>`. If a colony is disbanded, crew members and facilities remain independent domain entities.
  - `Facility` aggregates `List<Equipment>`. Equipment can be removed, transferred, or damaged independently of the building.
  - `ResourceManager` aggregates `List<Facility>`, referencing the colony's shared facility collection.
- **Java Implementation**: Collections passed into constructors or added via mutators (`addAgent`, `addFacility`), stored as references.

### 3. Composition
- **Definition**: A strong ownership relationship where a part's lifecycle is strictly tied to the whole. If the whole is destroyed, the part is destroyed.
- **MARS-OPS Example**:
  - Each `Habitat` owns its internal `LifeSupportSystem` (`private final LifeSupportSystem lifeSupport = new LifeSupportSystem();`).
  - Each `Facility` owns its internal `ResourceStore` (`private final ResourceStore store = new ResourceStore(capacity);`).
- **Java Implementation**: The owned object is instantiated directly by the parent during construction and is not shared independently.

### 4. Inheritance & Polymorphism
- **Definition**: An "is-a" relationship between base abstractions and specialized subclasses.
- **MARS-OPS Example**:
  - `Human` and `Robot` implement `Agent`. `Scientist`, `Engineer`, and `MedicalOfficer` extend `Human`. `ExplorationRover`, `RepairRobot`, and `Drone` extend `Robot`.
  - `ExplorationMission`, `RepairMission`, `ResearchMission`, and `RescueMission` extend `Mission`.
  - `OxygenLeakEmergency`, `FireEmergency`, `MedicalEmergency`, and `RoverBreakdownEmergency` extend `Emergency`.
- **Java Implementation**: Polymorphic method dispatch (`mission.canStart()`, `agent.canPerform(mission)`, `agent.canHandle(emergency)`, `emergency.respond()`).

---

## 3. Resource Management & Allocation Policy

### Colony-Wide Totals
- The colony's total resource quantity is the sum of quantities across all currently operational facilities (`Facility.isOperational() == true`).
- Resources in non-operational, damaged, or destroyed facilities do not count towards colony totals.

### Multi-Facility Sourcing & Allocation Policy
- An operation or mission may draw resources from multiple facilities simultaneously.
- **Chosen Allocation Policy**: `StorageFirstPolicy`.
  - **Policy Rationale**: In a research colony, dedicated storage depots (`StorageFacility`) are intended for general supply drawdowns before tapping into specialized habitat or laboratory reserves.
  - **Behavior**: Supply queries order facilities with `isStorage() == true` first, followed by other operational facilities in insertion order.

### All-or-Nothing Consumption
- Before consuming resources, `ResourceManager.canSupply(...)` verifies colony-wide availability.
- If the required quantity cannot be completely satisfied across operational facilities, **zero resources are consumed**, the operation fails immediately with a clear explanatory reason, and resource levels never fall below zero.

---

## 4. Emergency Rules & Responder Requirements

Emergencies do not interrupt active missions and are handled by available agents:

1. **Oxygen Leak (`OxygenLeakEmergency`)**:
   - Affects a specific `Habitat`.
   - **Detection**: Deduces the initial scenario oxygen loss from the affected habitat only (never falls below zero).
   - **Responders**: Requires two responders capable of handling repairs (e.g., `Engineer` and `RepairRobot`).
   - **Resource Cost**: Requires 2 `SPARE_PARTS`.
   - **Resolution**: Repairs habitat life support (`NOMINAL`).
   - **Unresolved Outcome**: If responders or spare parts are unavailable, the emergency ends `UNRESOLVED` and the same oxygen loss is deducted a second time (unresolved leaks cost 2x repaired leaks).
2. **Fire (`FireEmergency`)**:
   - Takes a `Facility` offline (`operational = false`). Requires 2 responders capable of fire suppression and 10 `WATER` units. Restores facility upon resolution.
3. **Rover Breakdown (`RoverBreakdownEmergency`)**:
   - Rover health becomes `DAMAGED` and unavailable. Requires an available repair unit and 2 `SPARE_PARTS`. Restores rover to `OPERATIONAL` upon resolution.
4. **Medical Emergency (`MedicalEmergency`)**:
   - Injured crew member requires an available `MedicalOfficer` and 1 `MEDICAL_SUPPLIES` unit. Restores crew member health to `HEALTHY`.

---

## 5. Environmental Effects & Mid-Mission State Transitions

The colony environment is governed by `EnvironmentManager`:
- **Normal**: Nominal operations.
- **Dust Storm**:
  - Surface exploration missions cannot begin (submission rejected).
  - **Missions in Progress**: If a dust storm begins while an exploration mission is in progress on the surface, the active rover mission fails immediately (`mission.fail("Dust storm caused active surface mission to fail.")`), releasing agents back to the colony.
- **Communication Disruption**:
  - Drones cannot be dispatched or controlled. Communication-dependent missions cannot be initiated.

---

## 6. Mission Lifecycle & State Machine

Each mission transitions through well-defined lifecycle states:
```
[Creation] -> ASSIGNED (upon acceptance by colony)
                  |
                  v (via mission.start() or simulation time step)
              IN_PROGRESS
              /         \
             v           v
       (duration reached) (failure / shortage / storm)
             v           v
         COMPLETED     FAILED
```

- **One Active Mission Limit**: Colony enforces that only one mission can be active (`ASSIGNED` or `IN_PROGRESS`) at a time.
- **Rejection**: Ineligible agents, active mission conflict, dust storms, or resource shortages cause immediate rejection (`FAILED`) with a recorded reason; the mission never becomes active.
- **Agent Availability**: Assigned agents have `available = false` during `IN_PROGRESS` and are automatically released (`available = true`) upon `COMPLETED` or `FAILED`.

---

## 7. Compilation & Execution Instructions

### Prerequisites
- Java Development Kit (JDK 17 or higher recommended, minimum JDK 8)
- Bash shell (Linux / macOS / WSL)

### Compiling All Source Code
From the project root directory:
```bash
javac -d bin $(find src -name "*.java")
```

### Running the Required Demonstration Scenarios (Main Class)
Executes all 5 required demonstration scenarios specified in Section 12:
```bash
java -cp bin missions.Main
```



---

## 8. Summary of Demonstration Scenarios (`missions.Main`)

1. **Scenario 1 (Successful Exploration)**: Demonstrates a `Scientist`, `ExplorationRover`, and core drill equipment completing an exploration mission on the Martian surface with state inspection before and after execution.
2. **Scenario 2 (Mission Rejection)**: Demonstrates immediate failure when an unsuitable agent (an Engineer for exploration) is assigned.
3. **Scenario 3 (Resource Shortage)**: Demonstrates all-or-nothing rejection when a mission requests 10 spare parts while only 2 exist in the colony, preventing negative quantities.
4. **Scenario 4 (Emergency Handling)**: Demonstrates full detection, responder identification, resource consumption, and life support repair for an oxygen leak in `Habitat Alpha`.
5. **Scenario 5 (Environmental Effect)**: Demonstrates that an active `DUST_STORM` condition immediately blocks surface exploration missions.