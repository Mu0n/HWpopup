package mic;

import VASSAL.build.GameModule;
import VASSAL.build.widget.PieceSlot;
import VASSAL.counters.GamePiece;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static mic.Util.logToChat;

/*
 * created by mjuneau on 6/8/18
 * This class dynamically generates GamePieces during AutoSquadSpawn
 */
public class GamePieceGenerator2e
{
    private static final String SMALL_STEM_SHIP_SLOT_NAME = "ship -- Stem2e Small Ship";
    private static final String MEDIUM_STEM_SHIP_SLOT_NAME = "ship -- Stem2e Medium Ship";
    private static final String LARGE_STEM_SHIP_SLOT_NAME = "ship -- Stem2e Large Ship";

    private static final String SMALL_STEM_SHIP_SINGLE_TURRET_SLOT_NAME = "ship -- Stem2e Small Single Turret Ship";
    private static final String MEDIUM_STEM_SHIP_SINGLE_TURRET_SLOT_NAME = "ship -- Stem2e Medium Single Turret Ship";
    private static final String LARGE_STEM_SHIP_SINGLE_TURRET_SLOT_NAME = "ship -- Stem2e Large Single Turret Ship";

    private static final String MEDIUM_STEM_SHIP_DOUBLE_TURRET_SLOT_NAME = "ship -- Stem2e Medium Double Turret Ship";
    private static final String LARGE_STEM_SHIP_DOUBLE_TURRET_SLOT_NAME = "ship -- Stem2e Large Double Turret Ship";

    private static final String SHIP_BASE_SIZE_SMALL = "Small";
    private static final String SHIP_BASE_SIZE_MEDIUM = "Medium";
    private static final String SHIP_BASE_SIZE_LARGE = "Large";

    // generate a ship GamePiece
    public static GamePiece generateShip(VassalXWSPilotPieces2e ship)
    {
                // generate the piece from the stem ships
        GamePiece newShip = null;
      //  boolean shipContainsMobileArc = containsMobileArc(shipData);
        if(ship.getShipData().getSize().contentEquals(SHIP_BASE_SIZE_SMALL)) {
            newShip = Util.newPiece(getPieceSlotByName(SMALL_STEM_SHIP_SLOT_NAME));
        }else if(ship.getShipData().getSize().contentEquals(SHIP_BASE_SIZE_MEDIUM)) {
            newShip = Util.newPiece(getPieceSlotByName(MEDIUM_STEM_SHIP_SLOT_NAME));
        }else if(ship.getShipData().getSize().contentEquals(SHIP_BASE_SIZE_LARGE))
        {
            //TO DO deal with mobilearc detection
            /*
            if(containsMobileArc(shipData))
            {
                //newShip = Util.newPiece(getPieceSlotByName(LARGE_STEM_SHIP_MOBILE_ARC_SLOT_NAME));
            }else {
                newShip = Util.newPiece(getPieceSlotByName(LARGE_STEM_SHIP_SLOT_NAME));
            }
            */
            newShip = Util.newPiece(getPieceSlotByName(LARGE_STEM_SHIP_SLOT_NAME));
        }

        // determine if the ship needs bomb drop
        //boolean needsBombCapability = determineIfShipNeedsBombCapability(ship, allShips);

        // execute the command to build the ship piece
        /*
        ShipGenerateCommand(String shipXws,   GamePiece piece, String faction, String xwsPilot,
        boolean needsBombCapability, Boolean hasDualBase,
            String dualBaseToggleMenuText, String base1ReportIdentifier, String base2ReportIdentifier) {
        */
        StemShip2e.ShipGenerateCommand myShipGen = new StemShip2e.ShipGenerateCommand(
                ship, ship.getShipData().getName(),
                newShip, ship.getShipData().getFaction(), ship.getPilotData().getXWS2(),false,
                false, "","","");

        myShipGen.execute();

        // add the stats to the piece
        newShip = setShipProperties(newShip,ship);
        return newShip;
    }

    public static GamePiece setShipProperties(GamePiece piece,VassalXWSPilotPieces2e ship ) {
        //GamePiece piece = Util.newPiece(this.ship);

        int initiativeModifier = 0;
        int chargeModifier = 0;
        int shieldsModifier = 0;
        int hullModifier = 0;
        int forceModifier = 0;

/*
        for (VassalXWSPilotPieces2e.Upgrade upgrade : upgrades) {

            MasterUpgradeData.UpgradeGrants doubleSideCardStats = DoubleSideCardPriorityPicker.getDoubleSideCardStats(upgrade.getXwsName());
            ArrayList<MasterUpgradeData.UpgradeGrants> grants = new ArrayList<MasterUpgradeData.UpgradeGrants>();
            if(grants!=null)
            {
                if (doubleSideCardStats != null) {
                    grants.add(doubleSideCardStats);
                } else {
                    ArrayList<MasterUpgradeData.UpgradeGrants> newGrants = new ArrayList<MasterUpgradeData.UpgradeGrants>();
                    try{
                        newGrants = upgrade.getUpgradeData().getGrants();
                    }catch(Exception e){

                    }
                    if(newGrants !=null) grants.addAll(newGrants);
                }
            }


            for (MasterUpgradeData.UpgradeGrants modifier : grants) {
                if (modifier.isStatsModifier()) {
                    String name = modifier.getName();
                    int value = modifier.getValue();

                    if (name.equals("hull")) hullModifier += value;
                    else if (name.equals("shields")) shieldsModifier += value;
                    else if (name.equals("initiative")) initiativeModifier += value;
                    else if (name.equals("force")) forceModifier += value;
                    else if (name.equals("charge")) chargeModifier += value;
                }
            }
        }*/

        if (ship.getShipData() != null)
        {
            int hull = ship.getShipData().getHull();
            int shields = ship.getShipData().getShields();
            int initiative = ship.getPilotData().getInitiative();

            //TO DO overrides, ugh
            /*
            if (pilotData != null && pilotData.getShipOverrides() != null)
            {
                MasterPilotData.ShipOverrides shipOverrides = pilotData.getShipOverrides();
                hull = shipOverrides.getHull();
                shields = shipOverrides.getShields();
            }
            */

            piece.setProperty("Initiative", initiative + initiativeModifier);
            piece.setProperty("Hull Rating", hull + hullModifier);
            piece.setProperty("Shield Rating", shields + shieldsModifier);


            if (ship.getShipData().getCharge() > 0) {
                int charge = ship.getShipData().getCharge();
                piece.setProperty("Charge Rating", charge + chargeModifier);
            }
            if (ship.getShipData().getForce() > 0) {
                int force = ship.getShipData().getForce();
                piece.setProperty("Force Rating", force + forceModifier);
            }

        }

        if (ship.getShipData().getName() != null) {
            piece.setProperty("Pilot Name", getDisplayPilotName(ship, ship.getShipNumber()));
        }

        return piece;
    }



    private static boolean containsMobileArc(MasterShipData.ShipData shipData)
    {
        boolean foundMobileArc = false;
        List<String>arcs = shipData.getFiringArcs();
        Iterator<String> i = arcs.iterator();
        String arc = null;
        while(i.hasNext() && !foundMobileArc)
        {
            arc = i.next();
            if(arc.equals("Mobile"))
            {
                foundMobileArc = true;
            }
        }

        return foundMobileArc;
    }


    //TO DO not sure where the bomb information will be polled from
    private static boolean determineIfShipNeedsBombCapability(VassalXWSPilotPieces2e ship, List<XWS2Pilots> allPilots)
    {
        boolean needsBomb = false;
        // if the pilot has a bomb slot
        /*
        MasterPilotData.PilotData pilotData = ship.getPilotData();
        List<String> slots = pilotData.getSlots();
        Iterator<String> slotIterator = slots.iterator();
        String slotName = null;
        while(slotIterator.hasNext() && !needsBomb)
        {
            slotName = slotIterator.next();
            if(slotName.equalsIgnoreCase("Bomb"))
            {
                needsBomb = true;
            }
        }

        // if an upgrade has a grant of bomb slot
        if(!needsBomb)
        {
            List<VassalXWSPilotPieces2e.Upgrade> upgrades = ship.getUpgrades();
            Iterator<VassalXWSPilotPieces2e.Upgrade> upgradeIterator = upgrades.iterator();
            VassalXWSPilotPieces2e.Upgrade upgrade = null;
            Iterator<MasterUpgradeData.UpgradeGrants> grantIterator = null;
            while(upgradeIterator.hasNext() && !needsBomb)
            {
                upgrade = upgradeIterator.next();
                ArrayList<MasterUpgradeData.UpgradeGrants> upgradeGrants;
                try {
                    upgradeGrants = upgrade.getUpgradeData().getGrants();
                }catch(Exception e){
                    logToChat("found the grants null exception.");
                    return false;
                }
                grantIterator = upgradeGrants.iterator();
                MasterUpgradeData.UpgradeGrants grant = null;
                while(grantIterator.hasNext() && !needsBomb)
                {
                    grant = grantIterator.next();
                    if(grant.getType().equalsIgnoreCase("slot") && grant.getName().equalsIgnoreCase("Bomb"))
                    {
                        needsBomb = true;
                    }
                }
            }
        }
*/
        return needsBomb;
    }

    private static PieceSlot getPieceSlotByName(String name)
    {

        List<PieceSlot> pieceSlots = GameModule.getGameModule().getAllDescendantComponentsOf(PieceSlot.class);
        PieceSlot targetPieceSlot = null;
        boolean found = false;

        PieceSlot pieceSlot = null;
        Iterator<PieceSlot> slotIterator = pieceSlots.iterator();
        while(slotIterator.hasNext() && !found)
        {
            pieceSlot = slotIterator.next();

            if (pieceSlot.getConfigureName().startsWith(name)) {
                targetPieceSlot = pieceSlot;
                found = true;
            }
        }
        return targetPieceSlot;
    }

    public static GamePiece generateDial(VassalXWSPilotPieces2e ship)
    {
        PieceSlot rebelDialSlot = null;
        PieceSlot imperialDialSlot = null;
        PieceSlot scumDialSlot = null;
        PieceSlot firstOrderDialSlot = null;
        PieceSlot resistanceDialSlot = null;

        // find the 3 slots for the auto-gen dials
        List<PieceSlot> pieceSlots = GameModule.getGameModule().getAllDescendantComponentsOf(PieceSlot.class);

        for (PieceSlot pieceSlot : pieceSlots) {
            String slotName = pieceSlot.getConfigureName();
            if (slotName.startsWith("Rebel Stem2e Dial") && rebelDialSlot == null) {
                rebelDialSlot = pieceSlot;
                continue;
            } else if (slotName.startsWith("Imperial Stem2e Dial") && imperialDialSlot == null) {
                imperialDialSlot = pieceSlot;
                continue;
            } else if (slotName.startsWith("Scum Stem2e Dial") && scumDialSlot == null) {
                scumDialSlot = pieceSlot;
                continue;
            } else if (slotName.startsWith("Resistance Stem2e Dial") && resistanceDialSlot == null) {
                resistanceDialSlot = pieceSlot;
                continue;
            } else if (slotName.startsWith("FirstOrder Stem2e Dial") && firstOrderDialSlot == null) {
                firstOrderDialSlot = pieceSlot;
                continue;
            }
        }

        String faction = ship.getShipData().getFaction();
        // grab the correct dial for the faction
        GamePiece dial = null;
        if(faction.contentEquals("Rebel Alliance")) {
            dial = Util.newPiece(rebelDialSlot);
        }else if(faction.contentEquals("Resistance")){
            dial = Util.newPiece(resistanceDialSlot);
        } if(faction.contentEquals("Galactic Empire")) {
            dial = Util.newPiece(imperialDialSlot);
        }else if(faction.contentEquals("First Order")){
        dial = Util.newPiece(firstOrderDialSlot);
        }else if(faction.contentEquals("Scum and Villainy")) {
            dial = Util.newPiece(scumDialSlot);
        }


        // execute the command
        StemDial2e.DialGenerateCommand myDialGen = new StemDial2e.DialGenerateCommand(ship.getShipData().getDial(), ship.getShipData().getName(), dial, faction);

        myDialGen.execute();

        //is this even needed
        //dial.setProperty("ShipXwsId",Canonicalizer.getCleanedName(shipTag));
        dial.setProperty("Pilot Name", getDisplayShipName(ship));
        dial.setProperty("Craft ID #", getDisplayPilotName(ship, ship.getShipNumber()));

        return dial;
    }

    public static GamePiece generateUpgrade(VassalXWSPilotPieces2e.Upgrade upgrade)
    {

        GamePiece newUpgrade = Util.newPiece(upgrade.getPieceSlot());
        boolean isDualSided = (upgrade.getUpgradeData().getDualCard() != null);
        StemUpgrade2e.UpgradeGenerateCommand myUpgradeGen = new StemUpgrade2e.UpgradeGenerateCommand(newUpgrade, upgrade, isDualSided);

        myUpgradeGen.execute();

        return newUpgrade;
    }

    public static GamePiece generateCondition(VassalXWSPilotPieces2e.Condition condition)
    {

        GamePiece newCondition = Util.newPiece(condition.getPieceSlot());

        // build the condition card
        StemCondition.ConditionGenerateCommand myConditionGen = new StemCondition.ConditionGenerateCommand(condition.getConditionData().getXws(), newCondition, condition.getConditionData().getName());
        myConditionGen.execute();

        return newCondition;
    }

    public static GamePiece generateConditionToken(VassalXWSPilotPieces2e.Condition condition)
    {
        // get the pieceslot for the StemConditionToken
        List<PieceSlot> pieceSlots = GameModule.getGameModule().getAllDescendantComponentsOf(PieceSlot.class);
        PieceSlot stemConditionTokenPieceSlot = null;
        for (PieceSlot pieceSlot : pieceSlots)
        {
            String slotName = pieceSlot.getConfigureName();
            if(slotName.equals("Stem Condition Token")) {

                stemConditionTokenPieceSlot = pieceSlot;
                break;
            }

        }

        // get a copy of the stem token game piece
        GamePiece conditionTokenPiece = Util.newPiece(stemConditionTokenPieceSlot);



        // build the condition card
        StemConditionToken.TokenGenerateCommand myTokenGen = new StemConditionToken.TokenGenerateCommand(condition.getConditionData().getXws(), conditionTokenPiece);
        myTokenGen.execute();

        return conditionTokenPiece;
    }

    public static GamePiece generatePilot(VassalXWSPilotPieces2e ship, List<XWS2Pilots> allShips) {

        GamePiece newPilot = Util.newPiece(ship.getPilotCard());
        if (ship.getShipNumber() != null && ship.getShipNumber() > 0) {
            newPilot.setProperty("Pilot ID #", ship.getShipNumber());
        } else {
            newPilot.setProperty("Pilot ID #", "");
        }

        // this is a stem card = fill it in

        XWS2Pilots shipData = XWS2Pilots.getSpecificShipFromPilotXWS2(ship.getPilotData().getXWS2(), allShips);
        XWS2Pilots.Pilot2e pilotData = XWS2Pilots.getSpecificPilot(ship.getPilotData().getXWS2(), allShips);
        //    newPilot.setProperty("Ship Type",shipData.getName());
        //    newPilot.setProperty("Pilot Name",pilotData.getName());

        StemPilot2e.PilotGenerateCommand myShipGen = new StemPilot2e.PilotGenerateCommand(newPilot, shipData, pilotData);

        myShipGen.execute();

        return newPilot;
    }

    private static String getDisplayPilotName(VassalXWSPilotPieces2e ship, Integer shipNumber )
    {
        String pilotName = "";

        if (ship.getPilotData() != null) {
            pilotName = Acronymizer.acronymizer(
                    ship.getPilotData().getName(),
                    ship.getPilotData().isUnique(),
                    ship.getShipData().hasSmallBase());
        }

        if (shipNumber != null && shipNumber > 0) {
            pilotName += " " + shipNumber;
        }
        return pilotName;
    }

    private static String getDisplayShipName(VassalXWSPilotPieces2e ship) {
        String shipName = "";

        if (ship.getPilotData() != null) {
            shipName = Acronymizer.acronymizer(
                    ship.getShipData().getName(),
                    ship.getPilotData().isUnique(),
                    ship.getShipData().hasSmallBase());
        }

        return shipName;
    }
}