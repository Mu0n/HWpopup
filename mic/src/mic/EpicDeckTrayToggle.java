package mic;

import static mic.Util.getCurrentPlayer;
import static mic.Util.logToChat;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import com.google.common.collect.Lists;

import VASSAL.build.AbstractConfigurable;
import VASSAL.build.Buildable;
import VASSAL.build.GameModule;
import VASSAL.build.module.Map;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.build.module.map.boardPicker.Board;
import VASSAL.counters.Deck;
import VASSAL.counters.GamePiece;

/**
 * Created by mjuneau on 2017-03-09.
 * epic trays are initially at y=-1000, can be toggled to y=75
 * epic tray counters are initially at y=-210, can be toggled to y=210
 */



public class EpicDeckTrayToggle extends AbstractConfigurable {

    private List<JButton> toggleButtons = Lists.newArrayList();

    private synchronized void epicMaskToggle(int playerId) {
        mic.Util.XWPlayerInfo playerInfo = getCurrentPlayer();
        if (playerInfo.getSide() != playerId) {
            return;
        }

        tempPoCChecksumChecker();

        Map playerMap = getPlayerMap(playerId);
        Board board = playerMap.getBoardByName("Player " + playerId);

        for (GamePiece piece : playerMap.getAllPieces()) {
            if (piece instanceof Deck) {
                Deck deck = (Deck) piece;
                if (deck.getDeckName() != null && deck.getDeckName().contains("Huge")) {
                    if(deck.getPosition().getY() == -1000)
                    {
                        deck.setPosition(new Point((int)deck.getPosition().getX(),75));
                        toggleButtons.get(playerId - 1).setText("Disable Epic");
                        board.setAttribute("image", "player_hand_background.jpg");
                    }
                    else
                    {
                        deck.setPosition(new Point((int)deck.getPosition().getX(),-1000));
                        toggleButtons.get(playerId - 1).setText("Activate Epic");
                        board.setAttribute("image", "observer_hand_background.jpg");
                    }
                    continue;
                }
            } else if (piece instanceof VASSAL.counters.Stack) {
                if (piece.getName() != null && piece.getName().contains("/ 10)")) {
                    if(piece.getPosition().getY() == -210) piece.setPosition(new Point((int)piece.getPosition().getX(),210));
                    else piece.setPosition(new Point((int)piece.getPosition().getX(),-210));
                    continue;
                }
            }
        }

        playerMap.setBoards(Lists.newArrayList(board.copy()));


    }

    private void tempPoCChecksumChecker() {
        try {
            List<String> allFiles = GameModule.getGameModule().getDataArchive().getArchive().getFiles();
            ArrayList<String> filteredList = new ArrayList<String>();

            for(String s : allFiles)
            {
                if(s.contains("images/Dial_Back")) filteredList.add(s);
            }
            for(String s: filteredList){
                //FileInputStream fis = new FileInputStream(new File("s"));
                String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
                fis.close();

                logToChat(md5);
            }
        }
        catch(Exception e){}
    }

    public void addTo(Buildable parent) {
        for (int i = 1; i <= 8; i++) {
            final int playerId = i;

            JButton b = new JButton("Activate Epic");
            b.setAlignmentY(0.0F);
            b.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    epicMaskToggle(playerId);
                }
            });
            toggleButtons.add(b);

            Map playerMap = getPlayerMap(i);
            playerMap.getToolBar().add(b);
        }
    }

    public void removeFrom(Buildable parent) {
        for (int i = 1; i <= 8; i++) {
            getPlayerMap(i).getToolBar().remove(toggleButtons.get(i - 1));
        }
    }

    private Map getPlayerMap(int playerIndex) {
        for (Map loopMap : GameModule.getGameModule().getComponentsOf(Map.class)) {
            if (("Player " + Integer.toString(playerIndex)).equals(loopMap.getMapName())) {
                return loopMap;
            }
        }
        return null;
    }

    // <editor-fold desc="unused vassal hooks">
    @Override
    public String[] getAttributeNames() {
        return new String[]{};
    }

    @Override
    public void setAttribute(String s, Object o) {
        // No-op
    }

    @Override
    public String[] getAttributeDescriptions() {
        return new String[]{};
    }

    @Override
    public Class[] getAttributeTypes() {
        return new Class[]{};
    }

    @Override
    public String getAttributeValueString(String key) {
        return "";
    }

    public Class[] getAllowableConfigureComponents() {
        return new Class[0];
    }

    public HelpFile getHelpFile() {
        return null;
    }
    // </editor-fold>
}
