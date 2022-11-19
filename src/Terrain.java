public enum Terrain {
    //TODO: Expand to 00_terrain.txt functionality, editing / adding custom terrain types
    FOREST, HILLS, MOUNTAIN, PLAINS, URBAN, JUNGLE, MARSH, DESERT, OCEAN, LAKES,
    WATER_FJORDS, WATER_SHALLOW_SEA, WATER_DEEP_OCEAN,
    UNKNOWN;


    public boolean isTerrainNaval(Terrain ter) {
        return switch(ter) {
            case OCEAN, WATER_FJORDS, WATER_SHALLOW_SEA, WATER_DEEP_OCEAN -> true;
            default -> false;
        };
    }

    public static Terrain parseTerrain(String input) {
        return switch(input) {
            case "forest" -> Terrain.FOREST;
            case "hills" -> Terrain.HILLS;
            case "mountain" -> Terrain.MOUNTAIN;
            case "plains" -> Terrain.PLAINS;
            case "urban" -> Terrain.URBAN;
            case "jungle" -> Terrain.JUNGLE;
            case "marsh" -> Terrain.MARSH;
            case "desert" -> Terrain.DESERT;
            case "ocean" -> Terrain.OCEAN;
            case "lakes" -> Terrain.LAKES;
            case "water_fjords" -> Terrain.WATER_FJORDS;
            case "water_shallow_sea" -> Terrain.WATER_SHALLOW_SEA;
            case "water_deep_ocean" -> Terrain.WATER_DEEP_OCEAN;
            default -> Terrain.UNKNOWN;
        };
    }
}
