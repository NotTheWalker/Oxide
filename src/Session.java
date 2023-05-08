import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Session {

    //session will be made with a path to the mod's root folder
    //it will be up to the user to ensure their mod is correctly formatted,
    //and if not they will have to provide each file path manually


    public static Resource[] resources() {
        return resources("common/resources/00_resources.txt");
    }
    public static Resource[] resources(String filePath) {

        return null;
    }

    private static String cleanText(String text) {
        text = text.replace("\t", " ");
        text = text.replace("}", " } ");
        text = text.replace("{", " { ");
        text = text.replace("=", " = ");
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
                text.append(removeComments(line));
                line=br.readLine();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return text.toString();
    }

    private static String removeComments(String text) {
        return text.replaceAll("#.*", "");
    }
    private static RegexPattern getPatAbstract(String[] ignore) {
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < ignore.length; i++) {
            s.append(ignore[i]);
            if(i != ignore.length-1) {
                s.append("|");
            }
        }
        return new RegexPattern("\\b([a-z]\\w+)\\b(?<!"+s.toString()+")");
    }
}
