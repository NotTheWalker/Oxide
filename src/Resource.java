public enum Resource {
    //TODO: Expand to 00_resources.txt functionality, editing / adding custom resources
    STEEL("steel"),
    ALUMINIUM("aluminium"),
    CHROMIUM("chromium"),
    TUNGSTEN("tungsten"),
    RUBBER("rubber"),
    OIL("oil"),
    CUSTOM1("custom1"),
    CUSTOM2("custom2"),
    CUSTOM3("custom3"),
    CUSTOM4("custom4"),
    CUSTOM5("custom5"),
    CUSTOM6("custom6");

    private String name;
    Resource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
