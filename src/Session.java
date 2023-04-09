import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Session {

    //session will be made with a path to the mod's root folder
    //it will be up to the user to ensure their mod is correctly formatted,
    //and if not they will have to provide each file path manually

    //the following features represent VERSION ONE of the program, with more features to come in later versions
    //session will have certain default tasks it will run in order to parse the mod, such as:
    // - parsing resources (common/resources/00_resources.txt)
    // - parsing buildings (common/buildings/00_buildings.txt)
    // - parsing terrain (common/terrain/00_terrain.txt)
    // - parsing states (history/states/*.txt)
    // - parsing provinces via bitmap parsing (map/provinces.bmp)
    // - display the map as part of a GUI
    // - assign an alias RGB value to each province to show which state owns it
    // - parsing countries (common/countries/*.txt) (unsure how realistic this is)

    //session will have to support various actions, such as:
    // - adding a new state
    // - adding a new province
    // - adding a new resource, building, or terrain
    // - editing a province or state
    // - deleting a province or state
    // - correctly updating all files to reflect the changes

    //VERSION TWO may include:
    // - parsing and manipulating all remaining map features (rivers, lakes, railways, weather, etc.)
    // - manipulating starting units
    // - localisation support
    // - limited graphical asset support (such as adding a new flag or unit icon)
    // - repairing a mod (such as fixing a broken state/province link)
    // - possibly incorporating other modding tools like MapGen by u/Jamestom999

    //VERSION THREE is a long way off, but may include:
    // - full graphical asset support (such as adding a new unit model)
    // - comprehensive editors for other game systems like focus trees, tech trees, characters, etc.
    // - custom systems (vague, but some existing mods have these and I would like to support and possibly use their implementations)

    private static final String lBrace = "{";
    private static final String rBrace = "}";
    private static final RegexPattern PAT_RESOURCES_ABSTRACT = new RegexPattern("\\b([a-z]\\w+)\\b(?<!resources|icon_frame|cic|convoys)");

    public static Resource[] resources() {
        return resources("common/resources/00_resources.txt");
    }
    public static Resource[] resources(String filePath) {
        String text = fileToText(filePath);
        text = cleanText(text);
        List<String> allMatches = new ArrayList<>();
        List<RegexPattern> allPatterns = new ArrayList<>();
        List<Resource> allResources = new ArrayList<>();
        Matcher m = PAT_RESOURCES_ABSTRACT.getPattern().matcher(text);
        while (m.find()) {
            allMatches.add(m.group());
        }
        for(String s : allMatches) {
            allPatterns.add(new RegexPattern("(?<="+s+"\\s=\\s)\\{\\s*([^}]*)"));
        }
        for(int i = 0; i < allPatterns.size(); i++) {
            Matcher x = allPatterns.get(i).getPattern().matcher(text);
            while(x.find()) {
                String[] splitMatch = x.group().split(" ");
                allResources.add(new Resource(allMatches.get(i), Integer.parseInt(splitMatch[3]), Float.parseFloat(splitMatch[6]), Float.parseFloat(splitMatch[9])));
            }
        }
        Resource[] resources = new Resource[allResources.size()];
        resources = allResources.toArray(resources);
        return resources;
    }

    private static String cleanText(String text) {
        text = text.replaceAll("\\t", " ");
        text = text.replaceAll(rBrace, " "+rBrace+" ");
        text = text.replaceAll(lBrace, " "+lBrace+" ");
        text = text.replaceAll("=", " = ");
        return text.trim().replaceAll("\\s+", " ");
    }

    private static String fileToText(String filePath) {
        Path pathToFile = Paths.get(filePath);
        StringBuilder text = new StringBuilder();
        try(BufferedReader br = Files.newBufferedReader(pathToFile)) {
            String line = br.readLine();
            while (line!=null) {
                if(!line.startsWith("#")) {
                    continue;
                }
                text.append(line);
                line=br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return text.toString();
    }
}
