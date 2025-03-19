package storm.lrb.bolt;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storm.lrb.TopologyControl;
import storm.lrb.model.Accident;
import storm.lrb.model.AccidentImmutable;
import storm.lrb.tools.TupleHelpers;
import backtype.storm.Config;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import de.hub.cs.dbis.lrb.types.PositionReport;
import de.hub.cs.dbis.lrb.types.SegmentIdentifier;

/**
 * This bolt registers every stopped vehicle. If an accident was detected it emits accident information for further
 * processing.
 * 
 * Each AccidentDetectionBolt is responsible to check one assigned xway.
 * 
 * The description in the LRB paper isn't very helpful as the accident section doesn't describe accidents
 * completely:<blockquote> An accident occurs when two vehicles are "stopped" at the same position at the same time. A
 * vehicle is stopped when it reports the same position in 4 consecutive position reports. Once an accident occurs in a
 * given segment, traffic proceeds in that segment at a reduced speed determined by the traffic spacing model.
 * </blockquote> Below in the streaming description for accidents it gets clear what an accident involves: <blockquote>A
 * stream processing system should detect an accident on a given segment whenever two or more vehicles are stopped in
 * that segment at the same lane and position</blockquote>
 */
public class AccidentDetectionBolt extends BaseRichBolt {
  private static final long serialVersionUID = 5537727428628598519L;

  private static final Logger LOG = LoggerFactory.getLogger(AccidentDetectionBolt.class);

  public static final Fields FIELDS_OUTGOING = new Fields(TopologyControl.POS_REPORT_FIELD_NAME, TopologyControl.SEGMENT_FIELD_NAME, TopologyControl.ACCIDENT_INFO_FIELD_NAME);

  public static final Fields FIELDS_INCOMING = new Fields(TopologyControl.POS_REPORT_FIELD_NAME);

  /**
	 * Holds information about which car has been detected to be stopped how many times (>1) at each segment (identified
	 * by its id). An accident is defined to have occurred after two vehicles has been detected to be stopped in 4
	 * consecutive position reports at the same position.
	 * 
	 * {segment id} x ({vehicle id} x {vehicle stop count})
	 */
  private final Map<Integer, Map<Integer, Integer>> stopInformationPerPosition = new HashMap<Integer, Map<Integer, Integer>>();

  /**
	 * Due to the fact that the {@link Accident} class manages a lot of information, it is necessary to reference it in
	 * a proper collection. This one holds {@code Position x (Lane x Accicent)}.
	 */
  private final Map<Integer, Map<Integer, Accident>> accidentsPerPosition = new HashMap<Integer, Map<Integer, Accident>>();

  private OutputCollector collector;

  public AccidentDetectionBolt() {
  }

  @Override public void prepare(@SuppressWarnings(value = { "rawtypes" }) Map conf, TopologyContext context, OutputCollector collector) {
    this.collector = collector;
  }

  @Override public void execute(Tuple tuple) {
    if (TupleHelpers.isTickTuple(tuple)) {
      LOG.debug("emit all accidents");
      this.emitCurrentAccidents();
      return;
    }
    PositionReport report = (PositionReport) tuple.getValueByField(TopologyControl.POS_REPORT_FIELD_NAME);
    if (report == null) {
      LOG.warn("report is null, ackknowledging tuple and skipping");
    } else {
      if (report.getSpeed() == 0) {
        this.recordStoppedCar(report);
      } else {
        Map<Integer, Integer> vehicleStopInformationMap = this.stopInformationPerPosition.get(report.getPosition());
        if (vehicleStopInformationMap.containsKey(report.getVid())) {
          LOG.debug("car is moving again; position report: %s", report);
          this.checkIfAccidentIsOver(report);
        }
      }
    }
    this.collector.ack(tuple);
  }

  /**
	 * Only invoke if the accident at the position denoted by {@code report} can be cleared.
	 * 
	 * @param report
	 */
  private void checkIfAccidentIsOver(PositionReport report) {
    Integer accidentPosition = report.getPosition();
    Map<Integer, Integer> vehicleStopInformationMap = this.stopInformationPerPosition.get(accidentPosition);
    Set<Integer> stoppedCarsAtPosition = vehicleStopInformationMap.keySet();
    if (stoppedCarsAtPosition.size() == 2) {
      Map<Integer, Accident> laneAccidentMap = this.accidentsPerPosition.get(accidentPosition);
      Accident accidentinfo = laneAccidentMap.get(report.getLane());
      accidentinfo.setOver(report.getTime());
      LOG.info("accident is over: %s", accidentinfo);
      this.emitAccidentAtPosition(accidentPosition);
      laneAccidentMap.remove(report.getLane());
      if (laneAccidentMap.isEmpty()) {
        this.accidentsPerPosition.remove(accidentPosition);
      }
    }
  }

  private void recordStoppedCar(PositionReport report) {
    int position = report.getPosition();
    Map<Integer, Integer> vehicleStopMap = this.stopInformationPerPosition.get(position);
    if (vehicleStopMap == null) {
      vehicleStopMap = new HashMap<Integer, Integer>();
      this.stopInformationPerPosition.put(position, vehicleStopMap);
    }
    Integer vehicleStopCount = vehicleStopMap.get(report.getVid());
    if (vehicleStopCount == null) {
      vehicleStopCount = 1;
      vehicleStopMap.put(report.getVid(), vehicleStopCount);
    } else {
      vehicleStopCount += 1;
    }
    vehicleStopMap.put(report.getVid(), vehicleStopCount);
    if (vehicleStopCount >= 4) {
      this.updateAccident(report);
    }
  }

  private void updateAccident(PositionReport report) {
    Map<Integer, Integer> vehicleStopMap = this.stopInformationPerPosition.get(report.getPosition());
    Set<Integer> accidentVehicleIdentifiers = new HashSet<Integer>();
    for (Integer vehicleIdentifier : vehicleStopMap.keySet()) {
      Integer vehicleStopCount = vehicleStopMap.get(vehicleIdentifier);
      if (vehicleStopCount >= 4) {
        accidentVehicleIdentifiers.add(vehicleIdentifier);
      }
    }
    if (accidentVehicleIdentifiers.size() >= 2) {
      Map<Integer, Accident> laneAccidentMap = this.accidentsPerPosition.get(report.getPosition());
      if (laneAccidentMap == null) {
        laneAccidentMap = new HashMap<Integer, Accident>();
        this.accidentsPerPosition.put(report.getPosition(), laneAccidentMap);
      }
      Accident laneAccident = laneAccidentMap.get(report.getLane());
      if (laneAccident == null) {
        laneAccident = new Accident(report);
        laneAccidentMap.put(report.getLane().intValue(), laneAccident);
        LOG.debug("emitting new accident: %s", laneAccident);
        laneAccident.getInvolvedCars().addAll(accidentVehicleIdentifiers);
      } else {
        LOG.debug("update accident: %s", laneAccident);
        laneAccident.getInvolvedCars().add(report.getVid());
      }
    }
  }

  private void emitCurrentAccidents() {
    for (Map<Integer, Accident> laneAccidentMap : this.accidentsPerPosition.values()) {
      for (Accident accident : laneAccidentMap.values()) {
        this.emitAccident(accident);
      }
    }
  }

  private void emitAccident(Accident accident) {
    Set<SegmentIdentifier> segmensts = accident.getInvolvedSegs();
    for (SegmentIdentifier xsd : segmensts) {
      AccidentImmutable acc = new AccidentImmutable(accident);
      this.collector.emit(TopologyControl.ACCIDENT_INFO_STREAM_ID, new Values(acc));
    }
  }

  /**
	 * emit newly detected accident at {@code position}
	 * 
	 * @param position
	 */
  private void emitAccidentAtPosition(Integer position) {
    LOG.debug("emmitting new or over accident on position %s", position);
    if (this.accidentsPerPosition.isEmpty()) {
      return;
    }
    Map<Integer, Accident> laneAccidentMap = this.accidentsPerPosition.get(position);
    if (laneAccidentMap == null) {
      return;
    }
    for (Accident accident : laneAccidentMap.values()) {
      this.emitAccident(accident);
    }
  }

  @Override public void declareOutputFields(OutputFieldsDeclarer declarer) {
    declarer.declareStream(TopologyControl.ACCIDENT_INFO_STREAM_ID, FIELDS_OUTGOING);
  }

  @Override public Map<String, Object> getComponentConfiguration() {
    Map<String, Object> conf = new HashMap<String, Object>();
    conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 60);
    return conf;
  }

  @Override public String toString() {
    return "AccidentDetectionBolt \n [stoppedCarsPerXSegDir=" + this.stopInformationPerPosition + ",\n allAccidentPositions=" + this.accidentsPerPosition + "]";
  }

  public Map<Integer, Map<Integer, Accident>> getAccidentsPerPosition() {
    return Collections.unmodifiableMap(this.accidentsPerPosition);
  }

  public Map<Integer, Map<Integer, Integer>> getStopInformationPerPosition() {
    return Collections.unmodifiableMap(this.stopInformationPerPosition);
  }
}