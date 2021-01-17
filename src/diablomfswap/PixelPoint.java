package diablomfswap;

import java.awt.Dimension;

/**
 *
 * @author Mats
 */

public class PixelPoint {
    private Dimension resolution;
    private int x, y;
    private int boxOffset;
    
    public PixelPoint(Dimension resolution, int x, int y, int boxOffset) {
        this.resolution = resolution;
        this.x = x;
        this. y = y;
        this.boxOffset = boxOffset;
    }

    public Dimension getResolution() {
        return resolution;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getBoxOffset() {
        return boxOffset;
    }
}
