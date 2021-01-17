package diablomfswap;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

/**
 *
 * @author Mats
 */
public class Gui extends JFrame {

    private MFSwapper mfs = new MFSwapper();
    private IO IO;
    private Listener al;
    private KeyboardListener kl;
    private JButton[][] itemGridButtons;
    private ArrayList<JButton> selectItemGridButtons;
    private String selectedItem;
    private JLabel editHotkeyLabel;
    private int gearHotkey;
    private JComboBox<String> jc;

    public Gui() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle(Str.TITLE);
        init();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(kl);
        setFrameResolution();
        setLayout(new GridLayout(2, 1));
        add(new NorthPanel());
        add(new SouthPanel());
        loadMemory();
    }

    private void init() {
        IO = new IO();
        al = new Listener();
        kl = new KeyboardListener();
        selectedItem = "shoulder";
        selectItemGridButtons = new ArrayList<>();
        gearHotkey = KeyEvent.VK_ENTER;
    }

    private void setFrameResolution() {
        setSize(new Dimension(492, 611));
        setResizable(false);
    }

    private void loadMemory() {
        String lastUsedConfigPath = IO.loadMemory();
        if (!lastUsedConfigPath.equals("")) {
            Load l = null;
            try {
                l = IO.load(lastUsedConfigPath);
                if (l != null) {
                    mfs.setScreenResolution(l.getResolution());
                    mfs.setRightHandRing(l.getRightHandRing());
                    mfs.setItemGrid(l.getItemGrid());
                    mfs.setDoubleItemGrid(l.getDoubleItemGrid());
                    updateHotkeyLabel(l.getHotkey());
                    jc.setSelectedItem(l.getResolution().width + " x " + l.getResolution().height);
                } else {
                    IO.showIOError();
                }
            } catch (NumberFormatException e) {
                IO.showIOError();
            }
            checkHighlightingButtons();
        }
    }

    private void checkHighlightingButtons() {
        for (int y = 0; y < itemGridButtons.length; y++) {
            for (int x = 0; x < itemGridButtons[y].length; x++) {
                if (mfs.isSelected(x, y)) {
                    itemGridButtons[y][x].setBorderPainted(true);
                    if (mfs.getRightHandRing().height == y && mfs.getRightHandRing().width == x) {
                        itemGridButtons[y][x].setBorder(BorderFactory.createLineBorder(Color.RED));
                    }
                } else {
                    itemGridButtons[y][x].setBorderPainted(false);
                    itemGridButtons[y][x].setBorder(BorderFactory.createLineBorder(Color.GREEN));
                }
            }
        }
    }

    private void updateHotkeyLabel(int keyCode) {
        gearHotkey = keyCode;
        editHotkeyLabel.setText("Swap hotkey: " + KeyEvent.getKeyText(keyCode));
    }

    private class NorthPanel extends JPanel {

        public NorthPanel() {
            setLayout(new GridLayout(1, 2));
            add(new NorthPanel.WestPanel());
            add(new NorthPanel.EastPanel());
        }

        private class WestPanel extends JPanel {

            public WestPanel() {
                setLayout(new GridLayout(2, 1));

                add(new NorthPanel.WestPanel.NorthWestPanel());
                add(new NorthPanel.WestPanel.SouthWestPanel());
            }

            private class NorthWestPanel extends JPanel {

                public NorthWestPanel() {
                    setLayout(new GridLayout(5, 2));

                    jc = new JComboBox<>();
                    fillComboBox(jc);
                    add(new JLabel(Str.RESOLUTION));
                    add(jc);
                    jc.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String selected = (String) ((JComboBox) e.getSource()).getSelectedItem();
                            mfs.setScreenResolution(getAsDimension(selected));
                        }
                    });

                    editHotkeyLabel = new JLabel(Str.SWAP_HOTKEY + KeyEvent.getKeyText(gearHotkey));
                    add(editHotkeyLabel);

                    JButton editHotkey = new JButton(Str.EDIT);
                    editHotkey.addActionListener(al);
                    add(editHotkey);

                    JButton save = new JButton(Str.SAVE);
                    save.addActionListener(al);
                    add(save);

                    JButton load = new JButton(Str.LOAD);
                    load.addActionListener(al);
                    add(load);
                }

                private void fillComboBox(JComboBox jc) {
                    PixelPoint[] aR = mfs.getAvailableResolutions();
                    for (int i = 0; i < aR.length; i++) {
                        jc.addItem((int) aR[i].getResolution().getWidth()
                                + " x "
                                + (int) aR[i].getResolution().getHeight());
                    }
                }

                private Dimension getAsDimension(String res) {
                    String[] resolution = res.split("[x]");
                    return new Dimension(Integer.parseInt(resolution[0].trim()),
                            Integer.parseInt(resolution[1].trim()));
                }
            }

            private class SouthWestPanel extends JPanel {

                public SouthWestPanel() {
                    setLayout(new GridBagLayout());
                    GridBagConstraints c = new GridBagConstraints();
                    c.weightx = 1;
                    c.weighty = 1;
                    c.fill = GridBagConstraints.HORIZONTAL;

                    JButton clear = new JButton("Clear all");
                    clear.addActionListener(al);
                    c.anchor = GridBagConstraints.LAST_LINE_START;
                    c.gridx = 0;
                    c.gridy = 0;
                    add(clear, c);

                    JButton swap = new JButton("Swap gear");
                    swap.addActionListener(al);
                    c.anchor = GridBagConstraints.LAST_LINE_START;
                    c.gridx = 1;
                    add(swap, c);
                }

                public void paintComponent(Graphics g) {
                    g.drawImage(IO.getMs(), 0, 0, null);
                    g.drawOval(120, 0, 30, 30);
                    g.drawOval(200, 0, 30, 30);
                    g.drawArc(123, 50, 100, 50, 0, -180);
                    g.drawRect(160, 80, 10, 20);
                }
            }
        }

        private class EastPanel extends JPanel {

            public EastPanel() {
                setLayout(null);

                /*
                 * Shoulder0
                 */
                JButton shoulder = new JButton();
                setButtonLayout(shoulder);
                shoulder.setBorderPainted(true);
                shoulder.setLocation(27, 20);
                shoulder.setSize(53, 61);
                shoulder.setActionCommand("shoulder");
                selectItemGridButtons.add(shoulder);
                shoulder.addActionListener(al);
                add(shoulder);
                /*
                 * Head1
                 */
                JButton head = new JButton();
                setButtonLayout(head);
                head.setLocation(93, 6);
                head.setSize(52, 43);
                head.addActionListener(al);
                head.setActionCommand("head");
                selectItemGridButtons.add(head);
                add(head);

                /*
                 * Amulet2
                 */
                JButton amulet = new JButton();
                setButtonLayout(amulet);
                amulet.setLocation(160, 35);
                amulet.setSize(43, 36);
                amulet.addActionListener(al);
                amulet.setActionCommand("amulet");
                selectItemGridButtons.add(amulet);
                add(amulet);

                /*
                 * Chest3
                 */
                JButton chest = new JButton();
                setButtonLayout(chest);
                chest.setLocation(86, 53);
                chest.setSize(66, 77);
                chest.addActionListener(al);
                chest.setActionCommand("chest");
                selectItemGridButtons.add(chest);
                add(chest);

                /*
                 * Gloves4
                 */
                JButton gloves = new JButton();
                setButtonLayout(gloves);
                gloves.setLocation(6, 88);
                gloves.setSize(53, 61);
                gloves.addActionListener(al);
                gloves.setActionCommand("gloves");
                selectItemGridButtons.add(gloves);
                add(gloves);

                /*
                 * Belt5
                 */
                JButton belt = new JButton();
                setButtonLayout(belt);
                belt.setLocation(86, 133);
                belt.setSize(66, 22);
                belt.addActionListener(al);
                belt.setActionCommand("belt");
                selectItemGridButtons.add(belt);
                add(belt);

                /*
                 * Bracers6
                 */
                JButton bracers = new JButton();
                setButtonLayout(bracers);
                bracers.setLocation(180, 88);
                bracers.setSize(53, 61);
                bracers.addActionListener(al);
                bracers.setActionCommand("bracers");
                selectItemGridButtons.add(bracers);
                add(bracers);

                /*
                 * Left ring7
                 */
                JButton leftRing = new JButton();
                setButtonLayout(leftRing);
                leftRing.setLocation(16, 157);
                leftRing.setSize(33, 28);
                leftRing.addActionListener(al);
                leftRing.setActionCommand("leftRing");
                selectItemGridButtons.add(leftRing);
                add(leftRing);

                /*
                 * Pants8
                 */
                JButton pants = new JButton();
                setButtonLayout(pants);
                pants.setLocation(94, 159);
                pants.setSize(53, 61);
                pants.addActionListener(al);
                pants.setActionCommand("pants");
                selectItemGridButtons.add(pants);
                add(pants);

                /*
                 * Right ring9
                 */
                JButton rightRing = new JButton();
                setButtonLayout(rightRing);
                rightRing.setLocation(189, 156);
                rightRing.setSize(33, 28);
                rightRing.addActionListener(al);
                rightRing.setActionCommand("rightRing");
                selectItemGridButtons.add(rightRing);
                add(rightRing);

                /*
                 * Main hand10
                 */
                JButton mainHand = new JButton();
                setButtonLayout(mainHand);
                mainHand.setLocation(7, 192);
                mainHand.setSize(54, 88);
                mainHand.addActionListener(al);
                mainHand.setActionCommand("mainHand");
                selectItemGridButtons.add(mainHand);
                add(mainHand);

                /*
                 * Boots11
                 */
                JButton boots = new JButton();
                setButtonLayout(boots);
                boots.setLocation(94, 223);
                boots.setSize(53, 61);
                boots.addActionListener(al);
                boots.setActionCommand("boots");
                selectItemGridButtons.add(boots);
                add(boots);

                /*
                 * Off hand12
                 */
                JButton offHand = new JButton();
                setButtonLayout(offHand);
                offHand.setLocation(180, 192);
                offHand.setSize(54, 88);
                offHand.addActionListener(al);
                offHand.setActionCommand("offHand");
                selectItemGridButtons.add(offHand);
                add(offHand);
            }

            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(IO.getSelectItemGrid(), 0, 0, null);
                repaint();
            }

            private void setButtonLayout(JButton j) {
                j.setOpaque(false);
                j.setContentAreaFilled(false);
                j.setBorder(BorderFactory.createLineBorder(Color.GREEN));
                j.setBorderPainted(false);
            }
        }
    }

    private class SouthPanel extends JPanel {

        public SouthPanel() {
            itemGridButtons = new JButton[6][10];
            setLayout(new GridLayout(6, 10));
            for (int y = 0; y < 6; y++) {
                for (int x = 0; x < 10; x++) {
                    itemGridButtons[y][x] = new JButton();

                    itemGridButtons[y][x].setActionCommand(x + "," + y);
                    itemGridButtons[y][x].addActionListener(al);

                    itemGridButtons[y][x].setOpaque(false);
                    itemGridButtons[y][x].setContentAreaFilled(false);
                    itemGridButtons[y][x].setBorder(BorderFactory.createLineBorder(Color.GREEN));
                    itemGridButtons[y][x].setBorderPainted(false);
                    add(itemGridButtons[y][x]);
                }
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(IO.getDiabloGrid(), 0, 0, null);
            repaint();
        }
    }

    private class Listener implements ActionListener {

        private EditFrame ef;

        public Listener() {
            ef = new EditFrame();
            ef.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {
                }

                @Override
                public void windowClosing(WindowEvent e) {
                    ef.setEnabled(false);
                    ef.setVisible(false);
                    kl.enable();
                }

                @Override
                public void windowClosed(WindowEvent e) {
                }

                @Override
                public void windowIconified(WindowEvent e) {
                }

                @Override
                public void windowDeiconified(WindowEvent e) {
                }

                @Override
                public void windowActivated(WindowEvent e) {
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                }
            });
            ef.setVisible(false);
            ef.setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            String action = e.getActionCommand();
            if (action.charAt(1) == ',') {
                int x = -1;
                int y = -1;
                try {
                    x = Integer.parseInt(String.valueOf(action.charAt(0)));
                    y = Integer.parseInt(String.valueOf(action.charAt(2)));
                } catch (NumberFormatException en) {
                    //Handle exception
                }
                if (x != -1 && y != -1) {
                    mfs.editItem(x, y, selectedItem);
                    checkHighlightingButtons();
                }
            } else {
                switch (action) {
                    case "Clear all":
                        mfs.clearItems();
                        checkHighlightingButtons();
                        break;
                    case "Swap gear":
                        mfs.swapGear();
                        break;
                    case "Edit":
                        viewEditFrame();
                        break;
                    case "Save":
                        save();
                        break;
                    case "Load":
                        load();
                        break;
                    case "shoulder":
                        handleSelection(action, 0);
                        break;
                    case "head":
                        handleSelection(action, 1);
                        break;
                    case "amulet":
                        handleSelection(action, 2);
                        break;
                    case "chest":
                        handleSelection(action, 3);
                        break;
                    case "gloves":
                        handleSelection(action, 4);
                        break;
                    case "belt":
                        handleSelection(action, 5);
                        break;
                    case "bracers":
                        handleSelection(action, 6);
                        break;
                    case "leftRing":
                        handleSelection(action, 7);
                        break;
                    case "pants":
                        handleSelection(action, 8);
                        break;
                    case "rightRing":
                        handleSelection(action, 9);
                        break;
                    case "mainHand":
                        handleSelection(action, 10);
                        break;
                    case "boots":
                        handleSelection(action, 11);
                        break;
                    case "offHand":
                        handleSelection(action, 12);
                        break;
                }
            }
        }

        private void handleSelection(String action, int index) {
            selectItemGridButtons.get(index).setBorderPainted(true);
            selectedItem = action;
            for (int i = 0; i < selectItemGridButtons.size(); i++) {
                if (i != index) {
                    selectItemGridButtons.get(i).setBorderPainted(false);
                }
            }
        }

        private void viewEditFrame() {
            ef.setEnabled(true);
            ef.setVisible(true);
            kl.disable();
        }

        private void save() {
            File diablo = IO.checkDiabloFolder();
            if (diablo != null) {
                JFileChooser fc = new JFileChooser(diablo) {
                    @Override
                    public void approveSelection() {
                        File f = getSelectedFile();
                        if (f.exists()) {
                            int result = JOptionPane.showConfirmDialog(this, "This file already exists, are you sure you want to overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
                            switch (result) {
                                case JOptionPane.YES_OPTION:
                                    super.approveSelection();
                                    return;
                                case JOptionPane.NO_OPTION:
                                    return;
                                case JOptionPane.CANCEL_OPTION:
                                    return;
                                default:
                                    return;
                            }
                        }
                        super.approveSelection();
                    }
                };
                int r = fc.showDialog(null, "Save");
                if (r == JFileChooser.APPROVE_OPTION) {
                    if (!IO.save(fc.getSelectedFile().getPath(),
                            mfs.getScreenResolution(), mfs.getRightHandRing(),
                            gearHotkey, mfs.getItemGrid(), mfs.getDoubleItemGrid())) {
                        IO.showIOError();
                    }
                }
            } else {
                IO.showIOError();
            }
        }

        private void load() {
            File diablo = IO.checkDiabloFolder();
            JFileChooser fc = new JFileChooser(diablo);
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int r = fc.showDialog(null, "Load");
            if (r == JFileChooser.APPROVE_OPTION) {
                Load l = null;
                try {
                    l = IO.load(fc.getSelectedFile().getPath());
                    if (l != null) {
                        mfs.setScreenResolution(l.getResolution());
                        mfs.setRightHandRing(l.getRightHandRing());
                        mfs.setItemGrid(l.getItemGrid());
                        mfs.setDoubleItemGrid(l.getDoubleItemGrid());
                        updateHotkeyLabel(l.getHotkey());
                        jc.setSelectedItem(l.getResolution().width + " x " + l.getResolution().height);
                    } else {
                        IO.showIOError();
                    }
                } catch (NumberFormatException e) {
                    IO.showIOError();
                }
                checkHighlightingButtons();
            }
        }

        private class EditFrame extends JFrame {

            public EditFrame() {
                setSize(200, 100);
                TextArea t = new TextArea("Press desired key");
                t.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                        kl.enable();
                        setVisible(false);
                        setEnabled(false);
                        updateHotkeyLabel(e.getKeyCode());

                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }
                });
                add(t);
                t.requestFocus();
            }
        }
    }

    private class KeyboardListener implements KeyEventDispatcher {

        private boolean disabled;

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == gearHotkey) {
                    if (!disabled) {
                        mfs.swapGear();
                    }
                }
            }
            return false;
        }

        public void disable() {
            disabled = true;
        }

        public void enable() {
            disabled = false;
        }
    }
}
