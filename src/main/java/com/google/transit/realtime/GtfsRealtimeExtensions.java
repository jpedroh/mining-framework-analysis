package com.google.transit.realtime;
import com.google.protobuf.ExtensionRegistry;

/**
 * Support for GTFS-realtime extensions.
 * 
 * @author bdferris
 */
public class GtfsRealtimeExtensions {
  /**
   * Adds all known GTFS-realtime extension messages to the specified extension
   * registry, except LIRR, unless includeLIRR=true. If includeLIRR=true, the
   * LIRR extension will be added to the registry but the MNR extension will
   * not be.
   *
   * LIRR and MNR GTFS-RT extensions both use extension ID 1005. Since MNR has
   * been assigned extension ID 1005 in the Google registry, it makes sense to
   * default to MNR.
   * 
   * @param registry registry to add the extensions to
   * @param includeLIRR if true, include LIRR extension; if false, include MNR
   *                    extension.
   */
  public static void registerExtensions(ExtensionRegistry registry) {
    registry.add(GtfsRealtimeMTARR.mtaRailroadStopTimeUpdate);

<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/96a36bebafe864cf710f1c7d4fb59d6b0f6be102/src/main/java/com/google/transit/realtime/GtfsRealtimeExtensions.java/left.java
    GtfsRealtimeNYCT.registerAllExtensions(registry)
=======
    registry.add(GtfsRealtimeMTARR.carriageDescriptor)
>>>>>>> /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/96a36bebafe864cf710f1c7d4fb59d6b0f6be102/src/main/java/com/google/transit/realtime/GtfsRealtimeExtensions.java/right.java
    ;
    GtfsRealtimeOneBusAway.registerAllExtensions(registry);

<<<<<<< /usr/src/app/output/onebusaway/onebusaway-gtfs-realtime-api/96a36bebafe864cf710f1c7d4fb59d6b0f6be102/src/main/java/com/google/transit/realtime/GtfsRealtimeExtensions.java/left.java
    if (includeLIRR) {
      GtfsRealtimeLIRR.registerAllExtensions(registry);
    } else {
      GtfsRealtimeMNR.registerAllExtensions(registry);
    }
=======
>>>>>>> Unknown file: This is a bug in JDime.

    GtfsRealtimeServiceStatus.registerAllExtensions(registry);
  }
}