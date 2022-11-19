import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class State {
    //region Mandatory state data
    private final int id;
    private String name;
    private int manpower;
    private StateCat stateCat;
    private List<Province> provinceList;
    //endregion

    //region Optional state data
    private boolean impassable;
    private HashMap<Resource, Integer> resourceHashMap;
    private double local_supplies;
    private double buildings_max_level_factor;
    //endregion

    //region History state data
    private String owner;
    private String controller;
    private HashMap<Integer, Integer> victoryPoints;
    private BuildingMap stateBuildings;
    private HashMap<Integer, BuildingMap> provincialBuildings;
    //endregion

    //TODO: Convert this to a standardized pattern
    private static final Pattern patID = Pattern.compile("/[\\n\\r].*id\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patName = Pattern.compile("/[\\n\\r].*name\\s*=\\s*\"([^\\s#\\n\\r]*)\"/");
    private static final Pattern patManpower = Pattern.compile("/[\\n\\r].*manpower\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patStateCat = Pattern.compile("/[\\n\\r].*state_category\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patProvinces = Pattern.compile("/provinces\\s*=\\s*\\{\\s*([^}]*)\\t/m");

    private static final Pattern patImpassable = Pattern.compile("/[\\n\\r].*impassable\\s*=\\s*([^\\s#\\n\\r]*)/");
    //TODO: should inherit from resource config (pane?) or file
    private static final Pattern patSteel = Pattern.compile("/[\\n\\r].*steel\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patAluminium = Pattern.compile("/[\\n\\r].*aluminium\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patChromium = Pattern.compile("/[\\n\\r].*chromium\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patTungsten = Pattern.compile("/[\\n\\r].*tungsten\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patRubber = Pattern.compile("/[\\n\\r].*rubber\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patOil = Pattern.compile("/[\\n\\r].*oil\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patLocalSupplies = Pattern.compile("/[\\n\\r].*local_supplies\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patBuildingsMLF = Pattern.compile("/[\\n\\r].*buildings_max_level_factor\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern pat = Pattern.compile("");

    //private static final Pattern pat = Pattern.compile("");

    public State(int id, String name, int manpower, StateCat stateCat, List<Province> provinceList) {
        this.id = id;
        this.name = name;
        this.manpower = manpower;
        this.stateCat = stateCat;
        this.provinceList = provinceList;
    }
}
