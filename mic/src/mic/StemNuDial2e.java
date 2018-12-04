package mic;

import VASSAL.build.module.documentation.HelpFile;
import VASSAL.command.Command;
import VASSAL.counters.Decorator;
import VASSAL.counters.EditablePiece;
import VASSAL.counters.GamePiece;
import VASSAL.counters.KeyCommand;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mic on 04/12/2018.
 *
 * New style of dial:
 *
 * 1) looks like the open face 2nd edition dial instead of the 1st edition
 * 2) no more additional graphics due to the masked/hidden side - a simple eye-with-slash icon will be used to indicate the hidden mode of the dial (icon at center)
 * 3) when in reveal mode, the selected move (at the top) will be copied, larger, in the center and rotations can't be done at all
 * 4) when in hidden mode, dial rotation commands will only tweak the rotation of the faceplate for the owner of the dial, not for any other player
 * 5) The pilot name in text (white text over black background) above the dial; the ship name in text (same colors) under the dial, no more icon gfx to manage
 * 6) a player can't cheat anymore by swapping mask gfx by a transparent empty png
 * 7) the open face dial has to be kept in OTA2 - no mistakes are allowed because patches can't happen, unless a download all is forced in the content checker
 */

public class StemNuDial2e extends Decorator implements EditablePiece {
    public static final String ID = "stemnudial2e";


    public StemNuDial2e(GamePiece piece)
        {setInner(piece); }

    @Override
    public void mySetState(String newState) {

    }
    @Override
    public String myGetState() {
        return "";
    }
    @Override
    public String myGetType() {
        return ID;
    }
    @Override
    protected KeyCommand[] myGetKeyCommands() {
        return new KeyCommand[0];
    }
    @Override
    public Command myKeyEvent(KeyStroke stroke) {
        return null;
    }

    public String getDescription() {
        return "Custom StemNuDial (mic.StemNuDial2e)";
    }

    public void mySetType(String type) {

    }

    public HelpFile getHelpFile() {
        return null;
    }

    public void draw(Graphics g, int x, int y, Component obs, double zoom) {
        this.piece.draw(g, x, y, obs, zoom);
    }

    public Rectangle boundingBox() {
        return this.piece.boundingBox();
    }

    public Shape getShape() {
        return this.piece.getShape();
    }

    public String getName() {
        return this.piece.getName();
    }

    public static class DialGenerateCommand extends Command {

        protected void executeCommand() {

        }

        protected Command myUndoCommand() {
            return null;
        }
    }
}
