package diablomfswap;

import java.awt.Dimension;

/**
 *
 * @author Mats
 */
public class Load {

    private String path;
    private Dimension resolution;
    private Dimension rightHandRing;
    private int hotkey;
    private boolean[][] itemGrid;
    private boolean[][] doubleItemGrid;
    
    public Load(String path, Dimension resolution, Dimension rightHandRing, int hotkey, boolean[][] itemGrid, boolean[][] doubleItemGrid) {
        this.path = path;
        this.resolution = resolution;
        this.rightHandRing = rightHandRing;
        this.hotkey = hotkey;
        this.itemGrid = itemGrid;
        this.doubleItemGrid = doubleItemGrid;
    }

    public String getPath() {
        return path;
    }

    public Dimension getResolution() {
        return resolution;
    }

    public Dimension getRightHandRing() {
        return rightHandRing;
    }

    public int getHotkey() {
        return hotkey;
    }

    public boolean[][] getItemGrid() {
        return itemGrid;
    }

    public boolean[][] getDoubleItemGrid() {
        return doubleItemGrid;
    }
}
