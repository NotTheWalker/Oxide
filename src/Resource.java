public record Resource(String name, int iconframe_index, float cic, float convoys) {

    private static final String[] defaultResources = {"oil", "steel", "rubber", "chromium", "tungsten", "aluminium"};

    public Resource defaultResource(String name, int iconframe_index) {
        return new Resource(name, iconframe_index, (float) 0.125, (float) 0.1);
    }

    public String[] getDefaultResources() {
        return defaultResources;
    }

    public Resource getDefaultResource(String name) {
        for (int i = 0; i < defaultResources.length; i++) {
            if (defaultResources[i].equals(name)) {
                return defaultResource(name, i);
            }
        }
        return null;
    }
}
