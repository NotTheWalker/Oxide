import java.awt.Color;
import java.util.List;

public class Province {
    private final int index;
    private Color rgb;
    private TileType tileType;
    private boolean coastal;
    private Terrain terrain;
    private int continent;
    private List<Pixel> pixelList;

    public Province(int index, Color rgb, TileType tileType, boolean coastal, Terrain terrain, int continent) {
        this.index = index;
        this.rgb = rgb;
        this.tileType = tileType;
        this.coastal = coastal;
        this.terrain = terrain;
        this.continent = continent;
    }

    public int getIndex() {
        return index;
    }

    public Color getRgb() {
        return rgb;
    }

    public void setRgb(Color rgb) {
        this.rgb = rgb;
    }

    public TileType getTileType() {
        return tileType;
    }

    public void setTileType(TileType tileType) {
        this.tileType = tileType;
    }

    public boolean isCoastal() {
        return coastal;
    }

    public void setCoastal(boolean coastal) {
        this.coastal = coastal;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public int getContinent() {
        return continent;
    }

    public void setContinent(int continent) {
        this.continent = continent;
    }

    public List<Pixel> getPixelList() {
        return pixelList;
    }

    public void setPixelList(List<Pixel> pixelList) {
        this.pixelList = pixelList;
    }

    public void addPixel(Pixel pixel) {
        this.pixelList.add(pixel);
    }

    public Pixel centerPixel() {
        int avgX = 0, avgY = 0;
        if(pixelList.size()<1) {
            return new Pixel(0, 0);
        }
        for (Pixel i : pixelList) {
            avgX+=i.getX();
            avgY+=i.getY();
        }
        avgX/=pixelList.size();
        avgY/=pixelList.size();
        return new Pixel(avgX, avgY);
    }
}
