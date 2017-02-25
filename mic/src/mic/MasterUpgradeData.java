package mic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by guido on 11/02/17.
 */
public class MasterUpgradeData extends ArrayList<MasterUpgradeData.UpgradeData> {

  private static Map<String, List<UpgradeData>> loadedData = null;

  public static List<UpgradeData> getUpgradeData(String upgradeXwsId) {
      if (loadedData == null) {
          loadData();
      }
      return loadedData.get(upgradeXwsId);
  }

  private static void loadData() {
      loadedData = Maps.newHashMap();
      MasterUpgradeData data = Util.loadClasspathJson("upgrades.json", MasterUpgradeData.class);

      for(UpgradeData upgrade : data) {
          String key = upgrade.getXws();
          List<UpgradeData> upgradeData = loadedData.get(key);
          if (upgradeData == null)
          {
              upgradeData = new ArrayList<UpgradeData>();
          }
          upgradeData.add(upgrade);
          loadedData.put(key, upgradeData);
      }
  }

  public static class UpgradeData {

      @JsonProperty("name")
      private String name;

      @JsonProperty("points")
      private String points = "0";

      @JsonProperty("xws")
      private String xws;

      @JsonProperty("grants")
      private ArrayList<UpgradeGrants> grants = Lists.newArrayList();

      @JsonProperty("conditions")
      private List<String> conditions = Lists.newArrayList();

      public List<String> getConditions() {
          return conditions;
      }

      public ArrayList<UpgradeGrants> getGrants() {
          return grants;
      }

      public String getXws() {
          return xws;
      }

      public String getName() {
          return name;
      }

      public int getPoints() {
          try {
              return Integer.parseInt(points);
          } catch (Exception e) {
              return 0;
          }
      }
  }

  public static class UpgradeGrants {

      @JsonProperty("name")
      private String name;

      @JsonProperty("type")
      private String type;

      @JsonProperty("value")
      private int value;

      public String getName() {
          return name;
      }

      public String getType() {
          return type;
      }

      public int getValue() {
          return value;
      }

      public boolean isStatsModifier() {
          return type.equals("stats");
      }
  };
}
