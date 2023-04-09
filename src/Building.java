import java.util.logging.Logger;

public record Building(int level, String name) {

    static Logger logger = Logger.getLogger(Building.class.getName()); // Logger

    public int index(Building[] buildings) {
        for (int i = 0; i < buildings.length; i++) {
            if (this.equals(buildings[i])) {
                return i;
            }
        }
        logger.warning("Building " + name + " not found.");
        return -1;
    }

}
