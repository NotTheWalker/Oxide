import java.util.logging.Logger;

public class StateCat {

    Logger logger = Logger.getLogger(StateCat.class.getName()); // Logger
    private StateCatType stateCatType;
    private String name;

    public StateCat(StateCatType stateCatType) {
        this.stateCatType = stateCatType;
        this.name = stateCatType.name();
    }

    public StateCat(String name) {
        name = name.toUpperCase();
        this.name = name;
        try {
            this.stateCatType = StateCatType.valueOf(name);
        } catch (IllegalArgumentException iae) {
            logger.warning("StateCatType not found for name: " + name);
            this.stateCatType = StateCatType.NONE;
        }
    }

    public StateCatType getStateCatType() {
        return stateCatType;
    }

    public void setStateCatType(StateCatType stateCatType) {
        this.stateCatType = stateCatType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
