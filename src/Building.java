public enum Building {
    //TODO: Expand to 00_buildings.txt functionality, editing / adding custom buildings
    INFRASTRUCTURE(1, "Infrastructure"),
    AIR_BASE(1, "Air base"),
    ANTI_AIR_BUILDING(1, "Anti-air"),
    RADAR_STATION(1, "Radar station"),
    INDUSTRIAL_COMPLEX(1, "Civilian factory"),
    ARMS_FACTORY(1, "Military factory"),
    DOCKYARD(1, "Naval dockyard"),
    SYNTHETIC_REFINERY(1, "Synthetic refinery"),
    FUEL_SILO(1, "Fuel silo"),
    ROCKET_SITE(1, "Rocket site"),
    NUCLEAR_REACTOR(1, "Nuclear reactor"),
    NAVAL_BASE(0, "Naval base"),
    BUNKER(0, "Land fort"),
    COASTAL_BUNKER(0, "Naval fort"),
    RAIL_WAY(0, "Railway"),
    SUPPLY_NODE(0, "Supply hub");

    private int level;
    private String name;

    Building(int level, String name) {
        this.level = level;
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
