package diablomfswap;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 *
 * @author Mats
 */
public class MFSwapper {

    private PixelPoint[] availableResolutions;
    private Dimension screenResolution;
    private Robot robot;
    private boolean[][] itemGrid;
    private boolean[][] doubleItemGrid;
    private Dimension rightHandRing;

    public MFSwapper() {
        itemGrid = new boolean[6][10];
        doubleItemGrid = new boolean[6][10];
        availableResolutions = createAvailableResolutions();
        screenResolution = availableResolutions[0].getResolution();
        rightHandRing = new Dimension(-1, -1);
        try {
            robot = new Robot();
        } catch (AWTException e) {
            //Handle exception
        }
    }

    public void editItem(int x, int y, String selectedItem) {
        switch (selectedItem) {
            case "shoulder":
                handleRegularItemSelection(x, y);
                break;
            case "head":
                handleRegularItemSelection(x, y);
                break;
            case "amulet":
                handleUnregularItemSelection(x, y);
                break;
            case "chest":
                handleRegularItemSelection(x, y);
                break;
            case "gloves":
                handleRegularItemSelection(x, y);
                break;
            case "belt":
                handleUnregularItemSelection(x, y);
                break;
            case "bracers":
                handleRegularItemSelection(x, y);
                break;
            case "leftRing":
                handleUnregularItemSelection(x, y);
                break;
            case "pants":
                handleRegularItemSelection(x, y);
                break;
            case "rightRing":
                if (getItemGrid()[y][x]) {
                    itemGrid[y][x] = false;
                    rightHandRing.setSize(-1, -1);
                } else {
                    itemGrid[y][x] = true;
                    rightHandRing.setSize(x, y);
                }
                break;
            case "mainHand":
                handleRegularItemSelection(x, y);
                break;
            case "boots":
                handleRegularItemSelection(x, y);
                break;
            case "offHand":
                handleRegularItemSelection(x, y);
                break;
        }
    }

    public void clearItems() {
        setItemGrid(new boolean[6][10]);
        rightHandRing.setSize(-1, -1);
    }

    public boolean isSelected(int x, int y) {
        return getItemGrid()[y][x];
    }

    public PixelPoint[] getAvailableResolutions() {
        return availableResolutions;
    }

    public void swapGear() {
        Point p = MouseInfo.getPointerInfo().getLocation();
        altTabToDiablo();
        sleep(200);
        robot.keyPress(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_E);
        sleep(200);
        for (int y = 0; y < getItemGrid().length; y++) {
            for (int x = 0; x < getItemGrid()[y].length; x++) {
                if (getItemGrid()[y][x]) {
                    if (!getDoubleItemGrid()[y][x]) {
                        if (y == rightHandRing.height && x == rightHandRing.width) {
                            moveMouse(getAsPixels(x, y));
                            robot.keyPress(KeyEvent.VK_ALT);
                            sleep(20);
                            mouseRightClick();
                            robot.keyRelease(KeyEvent.VK_ALT);

                        } else {
                            moveMouse(getAsPixels(x, y));
                            mouseRightClick();
                        }
                        sleep(10);
                    }
                }
            }
        }
        robot.keyPress(KeyEvent.VK_E);
        robot.keyRelease(KeyEvent.VK_E);
        moveMouse(new Dimension(p.x, p.y));
    }

    public Dimension getRightHandRing() {
        return rightHandRing;
    }

    public void setScreenResolution(Dimension d) {
        this.screenResolution = d;
    }

    /*
     * Private methods
     */
    private void handleRegularItemSelection(int x, int y) {
        if (y + 1 < getItemGrid().length) {
            if (getItemGrid()[y][x]) {
                itemGrid[y][x] = false;
                itemGrid[y + 1][x] = false;
                doubleItemGrid[y + 1][x] = false;
            } else {
                itemGrid[y][x] = true;
                itemGrid[y + 1][x] = true;
                doubleItemGrid[y + 1][x] = true;
            }
        }
    }

    private void handleUnregularItemSelection(int x, int y) {
        if (getItemGrid()[y][x]) {
            itemGrid[y][x] = false;
        } else {
            itemGrid[y][x] = true;
        }
    }

    private void moveMouse(Dimension d) {
        robot.mouseMove(d.width, d.height);
    }

    private void mouseLeftClick() {
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        sleep(10);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    private void mouseRightClick() {
        robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        sleep(10);
        robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }

    private void altTabToDiablo() { //Might fail depending on OS and last used window
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(KeyEvent.VK_TAB);
        sleep(10);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_ALT);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            System.exit(0);
        }
    }

    private Dimension getAsPixels(int x, int y) {
        PixelPoint p = getAsPixelPoint(getScreenResolution());
        int pixelX = (int) (p.getX() + p.getBoxOffset() / 2);
        int pixelY = (int) (p.getY() + p.getBoxOffset() / 2);
        pixelX += x * p.getBoxOffset();
        pixelY += y * p.getBoxOffset();
        return new Dimension(pixelX, pixelY);
    }

    private PixelPoint getAsPixelPoint(Dimension d) {
        for (PixelPoint p : availableResolutions) {
            if (p.getResolution().width == d.width && p.getResolution().height == d.height) {
                return p;
            }
        }
        return null;
    }

    private PixelPoint[] createAvailableResolutions() {
        PixelPoint[] r = {
            new PixelPoint(new Dimension(1920, 1200), 1410, 654, 51),
            new PixelPoint(new Dimension(1920, 1080), 1410, 588, 50),
            new PixelPoint(new Dimension(1773, 1000), 1255, 545, 50),
            new PixelPoint(new Dimension(1680, 1050), 1187, 573, 45),
            new PixelPoint(new Dimension(1600, 900), 1131, 489, 42),
            new PixelPoint(new Dimension(1440, 900), 1020, 489, 39),
            new PixelPoint(new Dimension(1400, 1050), 989, 571, 39),
            new PixelPoint(new Dimension(1366, 768), 967, 418, 35),
            new PixelPoint(new Dimension(1360, 1024), 961, 557, 38),
            new PixelPoint(new Dimension(1360, 768), 959, 417, 37),
            new PixelPoint(new Dimension(1280, 1024), 904, 557, 33),
            new PixelPoint(new Dimension(1280, 960), 905, 521, 34),
            new PixelPoint(new Dimension(1280, 800), 903, 437, 33),
            new PixelPoint(new Dimension(1280, 768), 906, 416, 36),
            new PixelPoint(new Dimension(1280, 720), 903, 392, 34),
            new PixelPoint(new Dimension(1152, 864), 816, 469, 31),
            new PixelPoint(new Dimension(1152, 720), 815, 392, 31),
            new PixelPoint(new Dimension(1152, 648), 815, 350, 31),
            new PixelPoint(new Dimension(1024, 768), 723, 418, 28),
            new PixelPoint(new Dimension(800, 600), 566, 326, 23)
        };
        return r;
    }

    public Dimension getScreenResolution() {
        return screenResolution;
    }

    public boolean[][] getItemGrid() {
        return itemGrid;
    }

    public boolean[][] getDoubleItemGrid() {
        return doubleItemGrid;
    }

    public void setItemGrid(boolean[][] itemGrid) {
        this.itemGrid = itemGrid;
    }

    public void setDoubleItemGrid(boolean[][] doubleItemGrid) {
        this.doubleItemGrid = doubleItemGrid;
    }

    public void setRightHandRing(Dimension rightHandRing) {
        this.rightHandRing = rightHandRing;
    }
}
