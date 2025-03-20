package com.linkedin.camus.etl.kafka.common;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.avro.generic.IndexedRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.joda.time.DateTime;

import com.linkedin.camus.coders.MessageEncoder;
import com.linkedin.camus.etl.kafka.CamusJob;
import com.linkedin.camus.events.records.EventHeader;
import com.linkedin.camus.events.records.Guid;
import com.linkedin.camus.events.records.TrackingMonitoringEvent;

@JsonIgnoreProperties({"trackingCount", "lastKey", "eventCount", "RANDOM"})
public class EtlCounts {
    private transient static final Random RANDOM = new Random();
    private static final String TOPIC = "topic";
    private static final String SERVER = "server";
    private static final String SERVICE = "service";
    private static final String GRANULARITY = "granularity";
    private static final String COUNTS = "counts";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";
    private static final String FIRST_TIMESTAMP = "firstTimestamp";
    private static final String LAST_TIMESTAMP = "lastTimestamp";
    private static final String ERROR_COUNT = "errorCount";
    private static final String START = "start";
    private static final String COUNT = "count";
    private String topic;
<<<<<<< /usr/src/app/output/linkedin/camus/afe16bd0816776894fd39f6605651d8b566312b0/camus-etl-kafka/src/main/java/com/linkedin/camus/etl/kafka/common/EtlCounts.java/left.java
    private EtlKey lastKey;
||||||| /usr/src/app/output/linkedin/camus/afe16bd0816776894fd39f6605651d8b566312b0/camus-etl-kafka/src/main/java/com/linkedin/camus/etl/kafka/common/EtlCounts.java/base.java
    private EtlKey lastKey;
=======
    private transient EtlKey lastKey;
>>>>>>> /usr/src/app/output/linkedin/camus/afe16bd0816776894fd39f6605651d8b566312b0/camus-etl-kafka/src/main/java/com/linkedin/camus/etl/kafka/common/EtlCounts.java/right.java
<<<<<<< /usr/src/app/output/linkedin/camus/afe16bd0816776894fd39f6605651d8b566312b0/camus-etl-kafka/src/main/java/com/linkedin/camus/etl/kafka/common/EtlCounts.java/left.java
    private int eventCount = 0;
||||||| /usr/src/app/output/linkedin/camus/afe16bd0816776894fd39f6605651d8b566312b0/camus-etl-kafka/src/main/java/com/linkedin/camus/etl/kafka/common/EtlCounts.java/base.java
    private int eventCount = 0;
=======
    private transient int eventCount = 0;
>>>>>>> /usr/src/app/output/linkedin/camus/afe16bd0816776894fd39f6605651d8b566312b0/camus-etl-kafka/src/main/java/com/linkedin/camus/etl/kafka/common/EtlCounts.java/right.java
    private static Logger log = Logger.getLogger(EtlCounts.class);
    public void incrementMonitorCount(EtlKey key) {
    	long monitorPartition = DateUtils.getPartition(granularity,
    			key.getTime());
    	Source source = new Source(key.getServer(), key.getService(),
    			monitorPartition);
    	if(counts.containsKey(source.toString()))
    	{
    		Source countSource = counts.get(source.toString());
    		countSource.setCount(countSource.getCount() + 1);
    		counts.put(countSource.toString(), countSource);
    	}
    	else
    	{
    		source.setCount(1);
    		counts.put(source.toString(), source);
    	}

    	if (key.getTime() > lastTimestamp) {
    		lastTimestamp = key.getTime();
    	}

    	if (key.getTime() < firstTimestamp) {
    		firstTimestamp = key.getTime();
    	}

    	lastKey = new EtlKey(key);
    	eventCount++;
    }
    public int loadStreamingCountsFromDir(FileSystem fs, Path path, boolean loadCounts)
            throws IOException {
        FileStatus[] statuses = fs.listStatus(path, new PrefixFilter(COUNTS));

        if (statuses.length == 0) {
            log.info("No old counts found!");
        }

        for (FileStatus status : statuses) {
            InputStream stream = new BufferedInputStream(fs.open(status.getPath()));
            loadStreamingCountsFromStream(stream, loadCounts);
        }

        return statuses.length;
    }
    private void extractCounts(JsonParser jp, boolean loadCounts) throws IOException {
        while (jp.nextToken() != JsonToken.END_ARRAY) {
            if (loadCounts) {
                long count = 0;
                long start = 0;
                String server = "esv4-tarotaz01";
                String service = "azkaban";

                while (jp.nextToken() != JsonToken.END_OBJECT) {
                    jp.nextToken();
                    String fieldName = jp.getCurrentName();
                    if (COUNT.equals(fieldName)) {
                        count = jp.getLongValue();
                    } else if (START.equals(fieldName)) {
                        start = jp.getLongValue();
                    } else if (SERVICE.equals(fieldName)) {
                        service = jp.getText();
                    } else if (SERVER.equals(fieldName)) {
                        server = jp.getText();
                    }
                }

                Source source = new Source(server, service, start);
                Long totalCount = trackingCount.get(source);
                if (totalCount == null) {
                    totalCount = 0l;
                }
                totalCount += count;
                trackingCount.put(source, totalCount);
            }
        }
    }
    public long getStartTime() {
    	return startTime;
    }
    public long getEndTime() {
    	return endTime;
    }
    public EtlKey getLastKey() {
    	return lastKey;
    }
    public int getEventCount() {
    	return eventCount;
    }
    public long getErrorCount() {
    	return errorCount;
    }
    public long getFirstTimestamp() {
    	return firstTimestamp;
    }
    public long getLastTimestamp() {
    	return lastTimestamp;
    }
    public String getTopic() {
    	return topic;
    }
    public void writeCountsToHDFS(FileSystem fs, Path path) throws IOException {
        Map<String, Object> countFile = new HashMap<String, Object>();

        ArrayList<Map<String, Object>> countList = new ArrayList<Map<String, Object>>();
        for (Map.Entry<Source, Long> countEntry : trackingCount.entrySet()) {
            HashMap<String, Object> map = new HashMap<String, Object>();

            Source source = countEntry.getKey();

            map.put(START, source.getPartition());
            map.put(SERVER, source.getServer());
            map.put(SERVICE, source.getService());
            map.put(COUNT, countEntry.getValue());

            countList.add(map);
        }

        countFile.put(TOPIC, topic);
        countFile.put(MONITOR_GRANULARITY, monitorGranularity);
        countFile.put(COUNTS, countList);
        countFile.put(START_TIME, startTime);
        countFile.put(END_TIME, endTime);
        countFile.put(FIRST_TIMESTAMP, firstTimestamp);
        countFile.put(LAST_TIMESTAMP, lastTimestamp);
        countFile.put(ERROR_COUNT, errorCount);

        Path countOutput = path;
        log.info("Writing count to file " + path);
        OutputStream outputStream = new BufferedOutputStream(fs.create(path));

        ObjectMapper m = new ObjectMapper();
        m.writeValue(outputStream, countFile);

        outputStream.close();
        log.info("Finished writing to file " + countOutput);
    }
    public void postTrackingCountToKafka(Configuration conf , String tier, String brokerList) {
    	MessageEncoder<IndexedRecord, byte[]> encoder;
    	try {
    		encoder = (MessageEncoder<IndexedRecord, byte[]>) Class.forName(
    				conf.get(CamusJob.CAMUS_MESSAGE_ENCODER_CLASS))
    				.newInstance();

    		Properties props = new Properties();
    		for (Entry<String, String> entry : conf) {
    			props.put(entry.getKey(), entry.getValue());
    		}

    		encoder.init(props, "TrackingMonitoringEvent");
    	} catch (Exception e1) {
    		throw new RuntimeException(e1);
    	}

    	ArrayList<byte[]> monitorSet = new ArrayList<byte[]>();
    	long timestamp = new DateTime().getMillis();
    	int counts = 0;
    	for (Map.Entry<String, Source> singleCount : this.getCounts().entrySet()) {
    		Source countEntry = singleCount.getValue();
    		long partition = countEntry.getStart();
    		String serverName = countEntry.getServer();
    		String serviceName = countEntry.getService();
    		long count = countEntry.getCount();
    		EventHeader header = new EventHeader();
    		
    		Guid guid = new Guid();
    		byte[] bytes = new byte[16];
    		RANDOM.nextBytes(bytes);
    		guid.bytes(bytes);

    		header.memberId = -1;
    		header.server = serverName;
    		header.service = serviceName;
    		header.time = timestamp;
    		header.guid = guid;
    		
    		TrackingMonitoringEvent trackingRecord = new TrackingMonitoringEvent();
    		trackingRecord.header = header;
    		trackingRecord.beginTimestamp = partition;
    		trackingRecord.endTimestamp = partition + granularity;
    		trackingRecord.count = count;
    		trackingRecord.tier = tier;
    		trackingRecord.eventType = topic;

    		byte[] message = encoder.toBytes(trackingRecord);
    		monitorSet.add(message);

    		if (monitorSet.size() >= 2000) {
    			counts += monitorSet.size();
    			produceCount(brokerList, monitorSet);
    			monitorSet.clear();
    		}
    	}

    	if (monitorSet.size() > 0) {
    		counts += monitorSet.size();
    		produceCount(brokerList, monitorSet);
    	}

    	log.info(topic + " sent " + counts + " counts");
    }
    private void produceCount(List<URI> brokerURI, ArrayList<Message> monitorSet) {
        // Shuffle the broker
        Collections.shuffle(brokerURI);

        SyncProducer basicProducer = null;
        for (URI uri : brokerURI) {
            Properties props = new Properties();
            props.put("host", uri.getHost());
            props.put("port", String.valueOf(uri.getPort()));
            props.put("buffer.size", String.valueOf(512 * 1024));
            log.info("Host " + uri.getHost() + " port " + props.get("port"));

            try {
                SyncProducerConfig config = new SyncProducerConfig(props);

                basicProducer = new SyncProducer(config);

                ByteBufferMessageSet byteBuffer = new ByteBufferMessageSet(monitorSet);
                basicProducer.send("TrackingMonitoringEvent", byteBuffer);

                break;
            } catch (Exception e) {
                e.printStackTrace();
                log.error(topic + " issue sending tracking to " + uri);
                continue;
            } finally {
                if (basicProducer != null) {
                    basicProducer.close();
                }
            }
        }
    }
	private long startTime;
	private long granularity;
	private long errorCount;
	private long endTime;
	private long lastTimestamp;
	private long firstTimestamp;
	private HashMap<String, Source> counts;
	//private final transient HashMap<NewSource, Long> trackingCount = new HashMap<NewSource, Long>();
	public EtlCounts()
	{}
	public EtlCounts(String topic, long granularity, long currentTime)
	{
		this.topic = topic;
		this.granularity = granularity;
		this.startTime = currentTime;
		this.counts = new HashMap<String, Source>();
	}
	public EtlCounts(String topic, long granularity)
	{
		this(topic, granularity, System.currentTimeMillis());
	}
	public HashMap<String, Source> getCounts() {
		return counts;
	}
	public long getGranularity() {
		return granularity;
	}
	public void setCounts(HashMap<String, Source> counts) {
		this.counts = counts;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public void setErrorCount(long errorCount) {
		this.errorCount = errorCount;
	}
	public void setFirstTimestamp(long firstTimestamp) {
		this.firstTimestamp = firstTimestamp;
	}
	public void setGranularity(long granularity) {
		this.granularity = granularity;
	}
	public void setLastTimestamp(long lastTimestamp) {
		this.lastTimestamp = lastTimestamp;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}
	public void setLastKey(EtlKey lastKey) {
		this.lastKey = lastKey;
	}
	public void writeCountsToHDFS(ArrayList<Map<String,Object>> allCountObject, FileSystem fs, Path path) throws IOException {
		Map<String, Object> countFile = new HashMap<String, Object>();
		countFile.put(TOPIC, topic);
		countFile.put(GRANULARITY, granularity);
		countFile.put(COUNTS, counts);
		countFile.put(START_TIME, startTime);
		countFile.put(END_TIME, endTime);
		countFile.put(FIRST_TIMESTAMP, firstTimestamp);
		countFile.put(LAST_TIMESTAMP, lastTimestamp);
		countFile.put(ERROR_COUNT, errorCount);	
		allCountObject.add(countFile);
	}
	private void produceCount(String brokerList, ArrayList<byte[]> monitorSet) {
		// Shuffle the broker

		Properties props = new Properties();
		props.put("metadata.broker.list", brokerList);
		props.put("producer.type", "async");
		props.put("request.required.acks", "1");
		props.put("request.timeout.ms", "30000");
		log.debug("Broker list: " + brokerList);
		Producer producer = new Producer(new ProducerConfig(props));
		try {
			for (byte[] message : monitorSet) {
				KeyedMessage keyedMessage = new KeyedMessage(
						"TrackingMonitoringEvent", message);
				producer.send(keyedMessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error(topic + " issue sending tracking to "
                    + brokerList.toString());
		} finally {
			if (producer != null) {
				producer.close();
			}
		}

	}
	
}
