import java.util.HashMap;

public class BuildingMap {
    private HashMap<Building, Integer> buildingIntegerHashMap;
    private boolean provincial;

    public BuildingMap(HashMap<Building, Integer> buildingIntegerHashMap, boolean provincial) {
        this.buildingIntegerHashMap = buildingIntegerHashMap;
        this.provincial=provincial;
    }

    public HashMap<Building, Integer> getBuildingIntegerHashMap() {
        return buildingIntegerHashMap;
    }

    public void setBuildingIntegerHashMap(HashMap<Building, Integer> buildingIntegerHashMap) {
        this.buildingIntegerHashMap = buildingIntegerHashMap;
    }

    public void addBuilding(Building building, Integer level) {
        HashMap<Building, Integer> currentBuildingMap = this.buildingIntegerHashMap;
        currentBuildingMap.put(building, level);
        this.buildingIntegerHashMap = currentBuildingMap;
    }

    public boolean isProvincial() {
        return provincial;
    }

    public void setProvincial(boolean provincial) {
        this.provincial = provincial;
    }

}
