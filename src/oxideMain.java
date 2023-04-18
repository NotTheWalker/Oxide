import javax.swing.*;
import java.util.Map;

public class oxideMain {
    public static void main(String[] args) {
        //oxideMain is the central landing pad of this program
        //its job is to create a session in which the user can edit a mod
        //oxideMain just selects the mod (via a directory chooser) and then passes the mod to the session
        //which then handles the rest of the program
        //oxideMain also handles settings, configurations, presets, and preferences
        boolean isRunning = true;
        Parser parser = new Parser("common/buildings/00_buildings.txt");
        Map<String, ValueInstance> map = parser.getParsedData();
        System.out.println(map);
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

}