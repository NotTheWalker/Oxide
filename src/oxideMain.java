import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class oxideMain {
    public static void main(String[] args) {
        String DELIMITER = ",";
        BufferedReader lineCounter = null;
        int lines = 0;
        try {
            lineCounter = new BufferedReader(new FileReader("definitions_example.csv"));
            while (lineCounter.readLine() != null) lines++;
            lineCounter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[][] definitions = new String[lines][8];
        try (BufferedReader br = new BufferedReader(new FileReader("definitions_example.csv"))) {
            String currentLine;
            int i=0;
            while ((currentLine = br.readLine()) != null) {
                String[] brokenLine = currentLine.split(DELIMITER);
                definitions[i] = brokenLine;
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Province[] allProvinces = new Province[lines];
        for(int i=1; i<lines; i++) {
            int index = Integer.parseInt(definitions[i][0]);
            Color tileColor = new Color(
                    Integer.parseInt(definitions[i][1]),
                    Integer.parseInt(definitions[i][2]),
                    Integer.parseInt(definitions[i][3])
            );
            TileType tileType = switch(definitions[i][4]) {
                case "sea" -> TileType.SEA;
                case "lake" -> TileType.LAKE;
                default -> TileType.LAND;
            };
            boolean coastal = Boolean.parseBoolean(definitions[i][5]);
            Terrain terrain = Terrain.parseTerrain(definitions[i][6]);
            int continent = Integer.parseInt(definitions[i][7]);
            allProvinces[i] = new Province(index, tileColor, tileType, coastal, terrain, continent);
        }
        BufferedImage provinceMap = null;
        try {
            provinceMap = ImageIO.read(new File("provincemap_example.bmp"));
            PrintWriter pixelColourMap = new PrintWriter("pixelColourMap_test.csv");
            writePixelMapCSV(provinceMap, pixelColourMap);
        } catch (IOException ignored) {
        }
        //region Execution and waiting for Rscript
        String pixelFinderPID = null;
        try {
            Runtime.getRuntime().exec("Rscript src/pixelFinder.R");
            Thread.sleep(1000); //must allow R script to write its PID
            Scanner sc = new Scanner(new File("pixelFinderPID.txt"));
            while (sc.hasNextLine()){
                pixelFinderPID = sc.nextLine();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long delayStart = System.currentTimeMillis();
        long delayEnd;
        long delay = 5000;
        do {
            delayEnd = System.currentTimeMillis();
            while (delayEnd - delay < delayStart) {
                delayEnd = System.currentTimeMillis();
            }
            delayStart=delayEnd;
        } while (isStillAlive(pixelFinderPID));
        //endregion

        File stateFolder = new File("statefiles_example");
        File[] stateFileNames = stateFolder.listFiles();
        try {
            String print = txtToString(stateFileNames[0].toString());
            System.out.println(print);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String txtToString(String stringPath) throws IOException {
        Path filePath = Path.of(stringPath);
        return Files.readString(filePath);
    }

    public static void writePixelMapCSV(BufferedImage pixelMap, PrintWriter dest) {
        dest.write("" + "x,y,Red,Green,Blue\n");
        for(int y = 0; y < pixelMap.getHeight(); y++) {
            for(int x = 0; x < pixelMap.getWidth(); x++) {
                StringBuilder data = new StringBuilder("");
                int pixelColour = pixelMap.getRGB(x, y);
                data.append(x);
                data.append(',');
                data.append(y);
                data.append(',');
                data.append((pixelColour>>16)&0xff);
                data.append(',');
                data.append((pixelColour>>8)&0xff);
                data.append(',');
                data.append((pixelColour)&0xff);
                data.append('\n');
                dest.write(data.toString());
            }
        }
        dest.close();
    }

    public static boolean isStillAlive(String PID) {
        String OS = System.getProperty("os.name").toLowerCase();
        String command = null;
        if (OS.contains("win")) {
            System.out.println("Check alive Windows mode. Pid: ["+PID+"]");
            command = "cmd /c tasklist /FI \"PID eq " + PID + "\"";
        } else if (OS.contains("nix") || OS.contains("nux") || OS.contains("mac")) {
            System.out.println("Check alive Linux/Unix mode. Pid: ["+PID+"]");
            command = "ps -p " + PID;
        } else {
            System.out.println("Unsupported OS: Check alive for Pid: ["+PID+"] return false");
            return false;
        }
        return isProcessIdRunning(PID, command); // call generic implementation
    }

    private static boolean isProcessIdRunning(String PID, String command) {
        System.out.println("Command ["+command+"]");
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);

            InputStreamReader isReader = new InputStreamReader(pr.getInputStream());
            BufferedReader bReader = new BufferedReader(isReader);
            String strLine = null;
            while ((strLine= bReader.readLine()) != null) {
                if (strLine.contains(" " + PID + " ")) {
                    return true;
                }
            }

            return false;
        } catch (Exception ex) {
            System.out.println("Got exception using system command ["+command+"]."+ex);
            return true;
        }
    }

}