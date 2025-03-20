package com.amazonaws.services.kinesis.clientlibrary.types;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import lombok.Getter;

/**
 * Container for the parameters to the IRecordProcessor's
 * {@link com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor#processRecords(
 * ProcessRecordsInput processRecordsInput) processRecords} method.
 */
public class ProcessRecordsInput {
  @Getter private Instant cacheEntryTime;

  @Getter private Instant cacheExitTime;

  private List<Record> records;

  private IRecordProcessorCheckpointer checkpointer;

  private Long millisBehindLatest;

  /**
     * Default constructor.
     */
  public ProcessRecordsInput() {
  }

  /**
     * Get records.
     *
     * @return Data records to be processed
     */
  public List<Record> getRecords() {
    return records;
  }

  /**
     * Set records.
     *
     * @param records Data records to be processed
     * @return A reference to this updated object so that method calls can be chained together.
     */
  public ProcessRecordsInput withRecords(List<Record> records) {
    this.records = records;
    return this;
  }

  /**
     * Get Checkpointer.
     *
     * @return RecordProcessor should use this instance to checkpoint their progress.
     */
  public IRecordProcessorCheckpointer getCheckpointer() {
    return checkpointer;
  }

  /**
     * Set Checkpointer.
     *
     * @param checkpointer RecordProcessor should use this instance to checkpoint their progress.
     * @return A reference to this updated object so that method calls can be chained together.
     */
  public ProcessRecordsInput withCheckpointer(IRecordProcessorCheckpointer checkpointer) {
    this.checkpointer = checkpointer;
    return this;
  }

  /**
     * Get milliseconds behind latest.
     *
     * @return The number of milliseconds this batch of records is from the tip of the stream,
     *         indicating how far behind current time the record processor is.
     */
  public Long getMillisBehindLatest() {
    return millisBehindLatest;
  }

  /**
     * Set milliseconds behind latest.
     *
     * @param millisBehindLatest The number of milliseconds this batch of records is from the tip of the stream,
     *         indicating how far behind current time the record processor is.
     * @return A reference to this updated object so that method calls can be chained together.
     */
  public ProcessRecordsInput withMillisBehindLatest(Long millisBehindLatest) {
    this.millisBehindLatest = millisBehindLatest;
    return this;
  }

  public ProcessRecordsInput withCacheEntryTime(Instant cacheEntryTime) {
    this.cacheEntryTime = cacheEntryTime;
    return this;
  }

  public ProcessRecordsInput withCacheExitTime(Instant cacheExitTime) {
    this.cacheExitTime = cacheExitTime;
    return this;
  }

  public Duration getTimeSpentInCache() {
    if (cacheEntryTime == null || cacheExitTime == null) {
      return Duration.ZERO;
    }
    return Duration.between(cacheEntryTime, cacheExitTime);
  }
}