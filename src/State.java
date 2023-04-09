import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class State {

    Logger logger = Logger.getLogger(StateCat.class.getName());
    private static final String lBrace = "{";
    private static final String rBrace = "}";

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

    //region Regex patterns
    //TODO: Convert this to a standardized pattern
    private final RegexPattern PAT_ID = new RegexPattern("(?<=id\\s=\\s)\\d+");
    private final RegexPattern PAT_NAME = new RegexPattern("(?<=name\\s=\\s)([^\\s\\r]*)");
    private final RegexPattern PAT_MANPOWER = new RegexPattern("(?<=manpower\\s=\\s)\\d+");
    private final RegexPattern PAT_STATE_CATEGORY = new RegexPattern("(?<=state_category\\s=\\s)([^\\s\\r]*)");
    private final RegexPattern PAT_PROVINCE_ABSTRACT = new RegexPattern("(?<=provinces\\s=\\s)\\{\\s*([^}]*)");
    private final RegexPattern PAT_IMPASSABLE = new RegexPattern("/(?<=impassable\\s=\\s)([^\\s\\r]*)/");
    //TODO: should inherit from resource config (pane?) or file
    private final RegexPattern patSteel = new RegexPattern("/(?<=steel\\s=\\s)([^\\s\\r]*)/");
    private final RegexPattern patAluminium = new RegexPattern("/(?<=aluminium\\s=\\s)([^\\s\\r]*)/");
    private final RegexPattern patChromium = new RegexPattern("/(?<=chromium\\s=\\s)([^\\s\\r]*)/");
    private final RegexPattern patTungsten = new RegexPattern("/(?<=tungsten\\s=\\s)([^\\s\\r]*)/");
    private static final Pattern patRubber = compile("/[\\n\\r].*rubber\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patOil = compile("/[\\n\\r].*oil\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patLocalSupplies = compile("/[\\n\\r].*local_supplies\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patBuildingsMLF = compile("/[\\n\\r].*buildings_max_level_factor\\s*=\\s*([^\\s#\\n\\r]*)/");

    //TODO: Absolutely 100% needs testing due to multiple start dates
    private static final Pattern patOwner = compile("/[\\n\\r].*owner\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patController = compile("/[\\n\\r].*controller\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patVictoryPoints = compile("/[^#]victory_points\\s*=\\s*\\{\\s*([^}\\t]*)\\s/g");

    //private static final Pattern pat = Pattern.compile("");
    //endregion

    public State(int id, String name, StateCat stateCat, int manpower, List<Province> provinceList) {
        this.id = id;
        this.name = name;
        this.stateCat = stateCat;
        this.manpower = manpower;
        this.provinceList = provinceList;
    }

    public State(int id, String name, StateCat stateCat, int manpower) {
        this.id = id;
        this.name = name;
        this.stateCat = stateCat;
        this.manpower = manpower;
    }

    private String cleanText(String text) {
        text = text.replaceAll("\\t", " ");
        text = text.replaceAll(rBrace, " "+rBrace+" ");
        text = text.replaceAll(lBrace, " "+lBrace+" ");
        text = text.replaceAll("=", " = ");
        return text.trim().replaceAll("\\s+", " ");
    }

    private String fileToText(String filePath) {
        Path pathToFile = Paths.get(filePath);
        StringBuilder text = new StringBuilder();
        try(BufferedReader br = Files.newBufferedReader(pathToFile)) {
            String line = br.readLine();
            while (line!=null) {
                text.append(line);
                line=br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return text.toString();
    }

}
