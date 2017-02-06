package mic;

import VASSAL.build.*;
import VASSAL.build.module.*;
import VASSAL.build.module.Map;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.build.widget.PieceSlot;
import VASSAL.build.widget.PieceSlotHack;
import VASSAL.command.*;
import VASSAL.counters.GamePiece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;



/**
 * HWpopup index class for the VASSAL tutorial
 */
public class HWpopup extends AbstractConfigurable implements CommandEncoder,
        GameComponent {

    private int index = 0;
    private int minChange = 0;
    private int maxChange = 0;
    private Random rand = new Random();
    private JButton experimentalButton; // Used to perform new and aggressive tests
    private JButton helloWorldButton; // Button that pops a java alert, displays a simple message

    public void addToIndex(int change) {
        index += change;
    }

    public int getIndex() {
        return index;
    }

    public int newIncrement() {
        return (int) (rand.nextFloat() * (maxChange - minChange + 1))
                + minChange;
    }

    private void helloWorldButtonPressed() {
        //Put simple alert/debug messages to be displayed here in a basic Java popup alert
        try {
            GamePiece pieces[] = Map.activeMap.getPieces();
            JOptionPane.showMessageDialog(null,"Hello World, there are " + Integer.toString(pieces.length) + " pieces on the board right now.",  "Feedback",
                    JOptionPane.ERROR_MESSAGE);
        } catch(Exception e){
            JOptionPane.showMessageDialog(null,"Hello World, there are no pieces on the board right now.",  "Feedback",
                    JOptionPane.ERROR_MESSAGE);
        }

    }


    private void experimentalButtonPressed() {

        //
        /* This is the failed piece of code to fetch the collection of PieceSlot (shown as the Pieces window
        in the x-wing module. Dude who helped me in the forums didn't realize he made me use a protected method
        */
        //PieceSlot slot = findSlot("0");
        //GamePiece piece = slot.getExpandedPiece();


        // This is java's way to do a simple "Hello World" popup alert. Modify at will for debug and tests
        /*JOptionPane.showMessageDialog(null,"Hello World",  "Feedback",
                JOptionPane.ERROR_MESSAGE);*/


        /*
        Force an already present piece to move around to a specific location on the map
        Usefulness: to make sure a loaded squad won't stack a bunch of cards/ships in the same place
        */
        GamePiece pieces[] = Map.activeMap.getPieces();
        pieces[0].setPosition(new Point(100, 100));

        /*
        We will need to read a text file into java. quick test done here
        */
        /*String everything;


            BufferedReader br = new BufferedReader(new FileReader("file.txt"));
            StringBuilder sb = new StringBuilder();
            String line = null;
            line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            everything = sb.toString();
            br.close();
*/


    }


    //method given to me in the vassal/module design forum, unfortunately it uses a protected method in the class
    private PieceSlot findSlot(String myGpId) {
        PieceSlot mySlot = null;
        for (PieceSlot slot : GameModule.getGameModule().getAllDescendantComponentsOf(PieceSlot.class)) {
            if (slot.getGpId().equals(myGpId)) {
                mySlot = slot;
                break;
            }
        }
        return mySlot;
    }

    // version using the dervived class in order to use the protected method remade public
    private PieceSlotHack findSlotHack(String myGpId) {
        PieceSlotHack mySlot = null;
        for (PieceSlotHack slot : GameModule.getGameModule().getAllDescendantComponentsOf(PieceSlotHack.class)) {
            if (slot.getGpId().equals(myGpId)) {
                mySlot = slot;
                break;
            }
        }
        return mySlot;
    }

    public static final String MIN = "min";
    public static final String MAX = "max";

    public void setAttribute(String key, Object value) {
        if (MIN.equals(key)) {
            if (value instanceof String) {
                minChange = Integer.parseInt((String) value);
            } else if (value instanceof Integer) {
                minChange = ((Integer) value).intValue();
            }
        } else if (MAX.equals(key)) {
            if (value instanceof String) {
                maxChange = Integer.parseInt((String) value);
            } else if (value instanceof Integer) {
                maxChange = ((Integer) value).intValue();
            }
        }
    }

    public String[] getAttributeNames() {
        return new String[]{MIN, MAX};
    }

    public String[] getAttributeDescriptions() {
        return new String[]{"Minimum increment", "Maximum increment"};
    }

    public Class[] getAttributeTypes() {
        return new Class[]{Integer.class, Integer.class};
    }

    public String getAttributeValueString(String key) {
        if (MIN.equals(key)) {
            return "" + minChange;
        } else if (MAX.equals(key)) {
            return "" + maxChange;
        } else {
            return null;
        }
    }

    public void addTo(Buildable parent) {
        GameModule mod = (GameModule) parent;

        mod.addCommandEncoder(this);
        mod.getGameState().addGameComponent(this);

        experimentalButton = new JButton("Test Button");
        experimentalButton.setAlignmentY(0.0F);
        experimentalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                experimentalButtonPressed();
            }
        });
        mod.getToolBar().add(experimentalButton);

        helloWorldButton = new JButton("Alert popup");
        helloWorldButton.setAlignmentY(0.0F);
        helloWorldButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                helloWorldButtonPressed();
            }
        });
        mod.getToolBar().add(helloWorldButton);
    }

    public void removeFrom(Buildable parent) {
        GameModule mod = (GameModule) parent;

        mod.removeCommandEncoder(this);
        mod.getGameState().removeGameComponent(this);

        mod.getToolBar().remove(experimentalButton);
        mod.getToolBar().remove(helloWorldButton);
    }

    public VASSAL.build.module.documentation.HelpFile getHelpFile() {
        return null;
    }

    public Class[] getAllowableConfigureComponents() {
        return new Class[0];
    }

    public void setup(boolean gameStarting) {
        if (!gameStarting) {
            index = 0;
        }
    }

    public Command getRestoreCommand() {
        return new Incr(this, index);
    }
    public static final String COMMAND_PREFIX = "TENSION:";

    public String encode(Command c) {
        if (c instanceof Incr) {
            return COMMAND_PREFIX + ((Incr) c).getChange();
        } else {
            return null;
        }
    }

    public Command decode(String s) {
        if (s.startsWith(COMMAND_PREFIX)) {
            return new Incr(this,
                    Integer.parseInt(s.substring(COMMAND_PREFIX.length())));
        } else {
            return null;
        }
    }

    public static class Incr extends Command {

        private HWpopup target;
        private int change;

        public Incr(HWpopup target, int change) {
            this.target = target;
            this.change = change;
        }

        protected void executeCommand() {
            target.addToIndex(change);
        }

        protected Command myUndoCommand() {
            return new Incr(target, -change);
        }

        public int getChange() {
            return change;
        }
    }
}
