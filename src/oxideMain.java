import javax.swing.*;
import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

public class oxideMain {
    public static Logger logger = Logger.getLogger(oxideMain.class.getName());
    public static void main(String[] args) {
        //oxideMain is the central landing pad of this program
        //its job is to create a session in which the user can edit a mod
        //oxideMain just selects the mod (via a directory chooser) and then passes the mod to the session
        //which then handles the rest of the program
        //oxideMain also handles settings, configurations, presets, and preferences
        boolean isRunning = true;
        logger.info("Start of test");
        testAllStatesToXML();
        logger.info("End of test");
        String[] options = {"New", "Existing"};
        int newOrExisting = JOptionPane.showOptionDialog(
                null,
                "Create a new mod or select an existing mod?",
                "Mod Manager",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
        if (newOrExisting == 0) {
            String modName = JOptionPane.showInputDialog("Enter the name of your mod:");
            String modPath = JOptionPane.showInputDialog("Enter the path to your mod:");
        }
    }

    public static void testAllStatesToXML() {
        File[] fileList = new File("gameFiles/history/states").listFiles();
        assert fileList != null;
        for (File file : fileList) {
            if (file.isFile()) {
                Parser parser = new Parser(file);
            }
        }
    }

}