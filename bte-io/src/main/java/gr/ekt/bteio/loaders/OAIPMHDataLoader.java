package gr.ekt.bteio.loaders;
import gr.ekt.bte.core.DataLoader;
import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.exceptions.MalformedSourceException;
import gr.ekt.bte.record.MapRecord;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Element;
import se.kb.oai.OAIException;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.RecordsList;
import se.kb.oai.pmh.ResumptionToken;

public class OAIPMHDataLoader implements DataLoader {
  private static Logger logger_;

  private OaiPmhServer server_;

  private String server_address_;

  private ResumptionToken token_;

  private String prefix_;

  private boolean has_more_records_;

  private Map<String, String> field_map_;

  public OAIPMHDataLoader() {
    server_ = null;
    server_address_ = null;
    token_ = null;
    prefix_ = null;
    has_more_records_ = true;
    field_map_ = null;
  }

  public OAIPMHDataLoader(String server_address, String prefix, Map<String, String> field_map) {
    server_address_ = server_address;
    server_ = new OaiPmhServer(server_address_);
    has_more_records_ = true;
    prefix_ = prefix;
    field_map_ = field_map;
  }

  @Override public RecordSet getRecords() throws MalformedSourceException {
    if (server_ == null) {
      throw new MalformedSourceException("Connection with server " + server_address_ + " has not been established");
    }
    RecordSet ret = new RecordSet();
    if (!has_more_records_) {
      return ret;
    }
    try {
      RecordsList records = null;
      if (token_ == null) {
        records = server_.listRecords(prefix_);
      } else {
        records = server_.listRecords(token_);
      }
      List<se.kb.oai.pmh.Record> oai_record_list = records.asList();
      token_ = records.getResumptionToken();
      has_more_records_ = token_ != null;
      for (int i = 0; i < oai_record_list.size(); i++) {
        Record rec = oai2bte(oai_record_list.get(i));
        ret.addRecord(rec);
      }
    } catch (OAIException e) {
      logger_.info("Caught OAIException " + e.getMessage());
      throw new MalformedSourceException(e.getMessage());
    }
    return ret;
  }

  @Override public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
    if (spec.getIdentifier() != null) {
      if (server_ == null) {
        throw new MalformedSourceException("Connection with server " + server_address_ + " has not been established");
      }
      RecordSet ret = new RecordSet();
      try {
        se.kb.oai.pmh.Record oai_record = server_.getRecord(spec.getIdentifier(), prefix_);
        Record rec = oai2bte(oai_record);
        ret.addRecord(rec);
      } catch (OAIException e) {
        logger_.info("Caught OAIException " + e.getMessage());
        throw new MalformedSourceException(e.getMessage());
      }
      return ret;
    }
    return getRecords();
  }

  @Override public boolean hasMoreRecords() {
    return has_more_records_;
  }

  /**
     * @return the server_address_
     */
  public String getServerAddress() {
    return server_address_;
  }

  /**
     * @param server_address_ the server_address_ to set
     */
  public void setServerAddress(String server_address_) {
    this.server_address_ = server_address_;
    server_ = new OaiPmhServer(server_address_);
    has_more_records_ = true;
  }

  /**
     * @return the prefix_
     */
  public String getPrefix() {
    return prefix_;
  }

  /**
     * @param prefix_ the prefix_ to set
     */
  public void setPrefix(String prefix_) {
    this.prefix_ = prefix_;
  }

  private Record oai2bte(se.kb.oai.pmh.Record oai_record) {
    MapRecord rec = new MapRecord();
    Element metadata_element = oai_record.getMetadata();
    for (String field : field_map_.keySet()) {
      String record_key = field_map_.get(field);
      for (Object elem : metadata_element.elements(field)) {
        String field_value = ((Element) elem).getText();
        rec.addValue(record_key, new StringValue(field_value));
      }
    }
    return rec;
  }

  /**
     * @return the field_map_
     */
  public Map<String, String> getFieldMap() {
    return field_map_;
  }

  /**
     * @param field_map_ the field_map_ to set
     */
  public void setFieldMap(Map<String, String> field_map_) {
    this.field_map_ = field_map_;
  }
}