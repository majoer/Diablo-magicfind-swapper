package diablomfswap;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * @author Mats
 */
public class IO {
    
    private URL diabloGridName, selectItemGridName, msName;
    private final String fileCheck = "DiabloMFSwapperMS", fileMemoryCheck = "DiabloMFSwapperMSMemory", diabloFoldername = "DiabloIIIMFSwapper";
    private Image diabloGrid, selectItemGrid, ms;
    private String error = "";

    public IO() {
        diabloGridName = getClass().getResource("res/diabloIIIGrid.png");
        selectItemGridName = getClass().getResource("res/selectItemGrid.png");
        msName = getClass().getResource("res/MS.png");
        
        diabloGrid = Toolkit.getDefaultToolkit().createImage(diabloGridName);
        selectItemGrid = Toolkit.getDefaultToolkit().createImage(selectItemGridName);
        ms = Toolkit.getDefaultToolkit().createImage(msName);
    }

    public Image getDiabloGrid() {
        return diabloGrid;
    }

    public Image getSelectItemGrid() {
        return selectItemGrid;
    }

    public Image getMs() {
        return ms;
    }

    public synchronized boolean save(String path, Dimension resolution, Dimension rightHandRing, int hotkey, boolean[][] itemGrid, boolean[][] doubleItemGrid) {
        String ext = getExt(path);
        if (ext.equals("")) {
            path += ".properties";
            ext = getExt(path);
        }
        if (ext.equalsIgnoreCase(".properties")) {
            Properties p = new Properties();
            p.setProperty("fileCheck", fileCheck);
            p.setProperty("resolution", resolution.width + " x " + resolution.height);
            p.setProperty("rightHandRing", rightHandRing.width + " x " + rightHandRing.height);
            p.setProperty("hotkey", "" + hotkey);
            for (int y = 0; y < itemGrid.length; y++) {
                for (int x = 0; x < itemGrid[y].length; x++) {
                    p.setProperty("i" + x + y, "" + itemGrid[y][x]);
                }
            }
            for (int y = 0; y < doubleItemGrid.length; y++) {
                for (int x = 0; x < doubleItemGrid[y].length; x++) {
                    p.setProperty("d" + x + y, "" + doubleItemGrid[y][x]);
                }
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(new File(path));
                p.store(fos, null);
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException ex) {
                    error += "\nSave: Could not close the file stream; exiting JVM";
                    showIOError();
                    System.exit(0);
                }
                error += "\nSave: Could not create the file: " + path;
                return false;
            }
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                error += "\nSave: Could not close the file stream; exiting JVM";
                showIOError();
                System.exit(0);
            }
            return true;
        } else {
            return false;
        }
    }

    public synchronized Load load(String path) throws NumberFormatException {
        Properties p = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(path));
            p.load(fis);
            fis.close();
        } catch (IOException e) {
            error += "\nLoad: Could not read the file: " + path;
            try {
                if (fis != null) {
                    fis.close();
                }
                return null;
            } catch (IOException ex) {
                ex.printStackTrace();
                error += "\nLoad: Could not close the file stream; exiting JVM";
                showIOError();
                System.exit(0);
            }
            return null;
        }
        if (!p.getProperty("fileCheck").equals(fileCheck)) {
            error += "\nLoad: The file check did not pass.\nPlease ensure that you selected a compatible .properties file, created by this program";
            return null;
        }
        Dimension resolution = getAsDimension(p.getProperty("resolution"));
        Dimension rightHandRing = getAsDimension(p.getProperty("rightHandRing"));
        int hotkey = Integer.parseInt(p.getProperty("hotkey"));
        boolean[][] itemGrid = new boolean[6][10];
        boolean[][] doubleItemGrid = new boolean[6][10];
        for (int y = 0; y < itemGrid.length; y++) {
            for (int x = 0; x < itemGrid[y].length; x++) {
                itemGrid[y][x] = Boolean.parseBoolean(p.getProperty("i" + x + y));
            }
        }
        for (int y = 0; y < doubleItemGrid.length; y++) {
            for (int x = 0; x < doubleItemGrid[y].length; x++) {
                doubleItemGrid[y][x] = Boolean.parseBoolean(p.getProperty("d" + x + y));
            }
        }
        updateMemory(path);
        return new Load(path, resolution, rightHandRing, hotkey, itemGrid, doubleItemGrid);
    }

    public void showIOError() {
        showMessageDialog(null, "Something unexpected happened while doing input/output operation. :(" + error);
        error = "";
    }

    public File checkDiabloFolder() {
        File diablo = new File(System.getProperty( "user.home" ) + File.separator + "My Documents", diabloFoldername);
        if (!diablo.exists()) {
            if (diablo.mkdirs()) {
                showMessageDialog(null, "Created a new folder 'DiabloIIIMFSwapper' in your documents folder\n"
                        + "You should not edit the name or content of this folder");
            } else {
                error = "\nCreate: Could not create folder in documents.";
                showIOError();
                return null;
            }
        }
        return diablo;
    }

    public String loadMemory() {
        File diablo = checkDiabloFolder();
        Properties p = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(diablo.getAbsolutePath() + File.separator + "memory.properties");
            p.load(fis);
            fis.close();
        } catch (IOException e) {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                error = "\nUpdate: File stream could not be closed; exiting JVM";
                showIOError();
                System.exit(0);
            }
        }
        if (p.getProperty("fileCheck") != null) {
            if (p.getProperty("fileCheck").equals(fileMemoryCheck)) {
                return p.getProperty("lastUsed");
            }
        }
        return "";
    }

    private void updateMemory(String path) {
        File diablo = checkDiabloFolder();
        Properties p = new Properties();
        p.setProperty("fileCheck", fileMemoryCheck);
        p.setProperty("lastUsed", path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(diablo.getAbsolutePath() + File.separator + "memory.properties");
            p.store(fos, null);
            fos.close();
        } catch (IOException e) {
            error = "\nUpdate: Memory could not be updated";
            showIOError();
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                error = "\nUpdate: File stream could not be closed; exiting JVM";
                showIOError();
                System.exit(0);
            }
        }
    }

    private Dimension getAsDimension(String s) {
        String[] resolution = s.split("[x]");
        return new Dimension(Integer.parseInt(resolution[0].trim()),
                Integer.parseInt(resolution[1].trim()));
    }

    private String getExt(String s) {
        int index = s.lastIndexOf(".");
        if (index != -1) {
            return s.substring(index);
        }
        return "";
    }
}
