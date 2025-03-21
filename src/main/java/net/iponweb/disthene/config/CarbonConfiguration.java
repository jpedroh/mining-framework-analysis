package net.iponweb.disthene.config;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Andrei Ivanov
 */
public class CarbonConfiguration {
  private String bind;

  private int port;

  private List<Rollup> rollups = new ArrayList<>();

  private Rollup baseRollup;

  private int aggregatorDelay;

  private List<String> authorizedTenants = new ArrayList<>();

  private boolean 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
  aggregateBaseRollup
=======
  allowAll = true
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
  ;

  public String getBind() {
    return bind;
  }

  public void setBind(String bind) {
    this.bind = bind;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public int getAggregatorDelay() {
    return aggregatorDelay;
  }

  public void setAggregatorDelay(int aggregatorDelay) {
    this.aggregatorDelay = aggregatorDelay;
  }

  public boolean getAggregateBaseRollup() {
    return aggregateBaseRollup;
  }

  public void setAggregateBaseRollup(boolean aggregateBaseRollup) {
    this.aggregateBaseRollup = aggregateBaseRollup;
  }

  public List<Rollup> getRollups() {
    return rollups;
  }

  public void setRollups(List<Rollup> rollups) {
    baseRollup = rollups.get(0);
    this.rollups = rollups.subList(1, rollups.size());
  }

  public Rollup getBaseRollup() {
    return baseRollup;
  }

  public List<String> getAuthorizedTenants() {
    return authorizedTenants;
  }

  public void setAuthorizedTenants(List<String> authorizedTenants) {
    this.authorizedTenants = Objects.requireNonNullElseGet(authorizedTenants, ArrayList::new);
  }

  public boolean isAllowAll() {
    return allowAll;
  }

  public void setAllowAll(boolean allowAll) {
    this.allowAll = allowAll;
  }

  @Override public String toString() {
    return 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    "CarbonConfiguration{" + "bind=\'" + bind + '\''
=======
    "CarbonConfiguration{" + "bind=\'" + bind + '\'' + ", port=" + port
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    ", port="
=======
    ", rollups="
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    port
=======
    rollups
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    ", rollups="
=======
    ", baseRollup="
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    rollups
=======
    baseRollup
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    ", baseRollup="
=======
    ", aggregatorDelay="
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    baseRollup
=======
    aggregatorDelay
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    ", aggregatorDelay="
=======
    ", authorizedTenants="
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    aggregatorDelay
=======
    authorizedTenants
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    ", aggregateBaseRollup="
=======
    ", allowAll="
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + 
<<<<<<< /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/left.java
    aggregateBaseRollup
=======
    allowAll
>>>>>>> /usr/src/app/output/einsamhauer/disthene/8c23ce22f44dfa88b1093a590f68cddbb7856da7/src/main/java/net/iponweb/disthene/config/CarbonConfiguration.java/right.java
     + '}';
  }
}