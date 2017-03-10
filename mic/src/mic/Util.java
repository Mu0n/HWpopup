package mic;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import VASSAL.build.GameModule;
import VASSAL.build.module.Chatter;
import VASSAL.build.module.GlobalOptions;
import VASSAL.build.module.PlayerRoster;
import VASSAL.build.widget.PieceSlot;
import VASSAL.command.Command;
import VASSAL.counters.GamePiece;
import VASSAL.counters.PieceCloner;

/**
 * Created by amatheny on 2/9/17.
 */
public class Util {

    private static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static <T> T loadRemoteJson(String url, Class<T> type) {
        try {
            InputStream inputStream = new BufferedInputStream(new URL(url).openStream());
            return mapper.readValue(inputStream, type);
        } catch (Exception e) {
            System.out.println("Unhandled error parsing remote json: \n" + e.toString());
            logToChat("Unhandled error parsing remote json: \n" + e.toString());
            return null;
        }
    }

    public static <T> T loadClasspathJson(String filename, Class<T> type) {
        try {
            InputStream inputStream = GameModule.getGameModule().getDataArchive().getInputStream(filename);
            if (inputStream == null) {
                logToChat("couldn't load " + filename);
            }
            return mapper.readValue(inputStream, type);
        } catch (Exception e) {
            System.out.println("Unhandled error parsing classpath json: \n" + e.toString());
            logToChat("Unhandled error parsing classpath json: \n" + e.toString());
            return null;
        }
    }

    public static List<String> none = Lists.newArrayList();


    public static void logToChat(String msg, Object... args) {
        if (args != null && args.length > 0) {
            msg = String.format(msg, args);
        }
        Command c = new Chatter.DisplayText(GameModule.getGameModule().getChatter(), msg);
        c.execute();
        GameModule.getGameModule().sendAndLog(c);
    }

    public static GamePiece newPiece(PieceSlot slot) {
        return PieceCloner.getInstance().clonePiece(slot.getPiece());
    }

    public static XWPlayerInfo getCurrentPlayer() {
        String name = GlobalOptions.getInstance().getPlayerId();
        String userId = GameModule.getGameModule().getUserId();
        String sideStr = PlayerRoster.getMySide();

        return new XWPlayerInfo(parsePlayerSide(sideStr), name, userId);
    }

    private static int parsePlayerSide(String sideStr) {
        if (sideStr == null || sideStr.length() == 0) {
            return XWPlayerInfo.UNKNOWN;
        }

        if (sideStr.contains("observer")) {
            return XWPlayerInfo.OBSERVER_SIDE;
        }

        try {
            sideStr = sideStr.charAt(sideStr.length() - 1) + "";
            return Integer.parseInt(sideStr);
        } catch (Exception e) {
            return XWPlayerInfo.UNKNOWN;
        }
    }


    public static class XWPlayerInfo {
        public static int OBSERVER_SIDE = 66;
        public static int UNKNOWN = -1;

        private int side;
        private String name;
        private String id;

        protected XWPlayerInfo(int side, String name, String id) {
            this.side = side;
            this.name = name;
            this.id = id;
        }

        public int getSide() {
            return side;
        }

        public String getName() {
            return name;
        }

        public String getId() {
            return id;
        }

        @Override
        public String toString() {
            return String.format("side=%s, id=%s, name=%s", this.side, this.id, this.name);
        }
    }
}
