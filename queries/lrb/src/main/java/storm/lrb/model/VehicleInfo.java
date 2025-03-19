package storm.lrb.model;
import java.io.Serializable;
import org.apache.commons.collections.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.hub.cs.dbis.lrb.types.PositionReport;
import de.hub.cs.dbis.lrb.types.SegmentIdentifier;

/**
 * Object representing a vehicle
 */
public class VehicleInfo implements Serializable {
  private static final long serialVersionUID = 1L;

  private static final Logger LOG = LoggerFactory.getLogger(VehicleInfo.class);

  private Integer vid;

  private int calculatedToll = 0;

  private final int day = 70;

  private PositionReport posreport;

  /**
	 * TODO load toll history of vehicle keeps toll history as DAy,XWAY,Tolls
	 */
  private final MultiKeyMap tollHistory = new MultiKeyMap();

  /**
	 * Time of positionreport which was last processed
	 */
  private long lastSendToll = -1;

  public VehicleInfo(Integer vid) {
    this.vid = vid;
  }

  protected VehicleInfo() {
  }

  public VehicleInfo(PositionReport pos) {
    this.posreport = pos;
    this.vid = pos.getVid();
  }

  /**
	 * Update vehicle information with new position report
	 * 
	 * @param pos
	 *            position report
	 */
  public void updateInfo(PositionReport pos) {
    this.posreport = pos;
  }

  public int getToll() {
    return this.calculatedToll;
  }

  public void setToll(int toll) {
    this.calculatedToll = toll;
  }

  public SegmentIdentifier getSegmentIdentifier() {
    return new SegmentIdentifier(this.posreport);
  }

  public long getLastReportTime() {
    return this.lastSendToll;
  }

  public Integer getXway() {
    return this.posreport.getXWay();
  }

  /**
	 * can be used as a placeholder for reacting to position reports which do not cause emission of toll notifications
	 * 
	 * @param why
	 *            reason to fill blank space
	 * @return notification
	 */
  public String getTollNotification(double lav, int toll, int nov) {
    this.setToll(toll);
    if (this.posreport.getTime() == this.lastSendToll) {
      return "";
    }
    String notification = "";
    this.lastSendToll = this.posreport.getTime();
    return notification;
  }

  @Override public String toString() {
    return "VehicleInfo [vid=" + this.vid + ", calculatedToll=" + this.calculatedToll + ", xway=" + this.getXway() + ", day=" + this.day + ", time=" + this.getLastReportTime() + ", tollHistory=" + this.tollHistory + ", segDir=" + this.getSegmentIdentifier() + "]";
  }

  @Override public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (this.vid != null ? this.vid.hashCode() : 0);
    return hash;
  }

  @Override public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final VehicleInfo other = (VehicleInfo) obj;
    if (this.vid != other.vid && (this.vid == null || !this.vid.equals(other.vid))) {
      return false;
    }
    return true;
  }
}