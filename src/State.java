import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

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

    //region Regex patterns
    //TODO: Convert this to a standardized pattern
    private static final Pattern patID = compile("/[\\n\\r].*id\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patName = compile("/[\\n\\r].*name\\s*=\\s*\"([^\\s#\\n\\r]*)\"/");
    private static final Pattern patManpower = compile("/[\\n\\r].*manpower\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patStateCat = compile("/[\\n\\r].*state_category\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patProvinces = compile("/provinces\\s*=\\s*\\{\\s*([^}]*)\\t/m");

    private static final Pattern patImpassable = compile("/[\\n\\r].*impassable\\s*=\\s*([^\\s#\\n\\r]*)/");
    //TODO: should inherit from resource config (pane?) or file
    private static final Pattern patSteel = compile("/[\\n\\r].*steel\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patAluminium = compile("/[\\n\\r].*aluminium\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patChromium = compile("/[\\n\\r].*chromium\\s*=\\s*([^\\s#\\n\\r]*)/");
    private static final Pattern patTungsten = compile("/[\\n\\r].*tungsten\\s*=\\s*([^\\s#\\n\\r]*)/");
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

    public State state(String attributes) {
        String[] attributesArr = attributes.split(",");
        return new State(
                Integer.parseInt(attributesArr[0]),
                attributesArr[1],
                StateCat.valueOf(attributesArr[2]),
                Integer.parseInt(attributesArr[3])
                );

    }

    private State[] statesFromFile(String filePath) {
        Path pathToFile = Paths.get(filePath);
        State[] states;
        List<State> stateList = new ArrayList<>();
        try(BufferedReader br = Files.newBufferedReader(pathToFile)) {
            String line = br.readLine();
            while (line!=null) {
                State stateFromLine = state(line);
                stateList.add(stateFromLine);
                line=br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        states = (State[]) stateList.toArray();
        return null;
    }

    private State[] appendStateArray(State[] states, State state) {
        State[] states1 = new State[states.length+1];
        System.arraycopy(states, 0, states1, 0, states.length);
        states1[states.length] = state;
        return states1;
    }

}
