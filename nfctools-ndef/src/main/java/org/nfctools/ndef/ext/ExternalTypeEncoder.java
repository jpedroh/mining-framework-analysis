package org.nfctools.ndef.ext;
import java.util.HashMap;
import java.util.Map;
import org.nfctools.ndef.NdefConstants;
import org.nfctools.ndef.NdefEncoderException;
import org.nfctools.ndef.NdefException;
import org.nfctools.ndef.NdefMessageEncoder;
import org.nfctools.ndef.NdefRecord;
import org.nfctools.ndef.Record;
import org.nfctools.ndef.wkt.WellKnownRecordConfig;
import org.nfctools.ndef.wkt.encoder.RecordEncoder;

public class ExternalTypeEncoder implements RecordEncoder {
  private Map<Class<?>, ExternalTypeRecordConfig> externalRecordTypes = new HashMap<Class<?>, ExternalTypeRecordConfig>();

  @Override public boolean canEncode(Record record) {
    return record instanceof ExternalTypeRecord;
  }

  @Override public NdefRecord encodeRecord(Record record, NdefMessageEncoder messageEncoder) {
    ExternalTypeRecord externalType = (ExternalTypeRecord) record;

<<<<<<< /usr/src/app/output/grundid/nfctools/15659385c0127435a9d7ed840ff8f444d3d63053/nfctools-ndef/src/main/java/org/nfctools/ndef/ext/ExternalTypeEncoder.java/left.java
    if (!externalType.hasNamespace()) {
      throw new NdefEncoderException("Expected namespace", record);
    }
=======
    ExternalTypeRecordConfig config = externalRecordTypes.get(record.getClass());
>>>>>>> /usr/src/app/output/grundid/nfctools/15659385c0127435a9d7ed840ff8f444d3d63053/nfctools-ndef/src/main/java/org/nfctools/ndef/ext/ExternalTypeEncoder.java/right.java

    byte[] payload;

<<<<<<< /usr/src/app/output/grundid/nfctools/15659385c0127435a9d7ed840ff8f444d3d63053/nfctools-ndef/src/main/java/org/nfctools/ndef/ext/ExternalTypeEncoder.java/left.java
    if (!externalType.hasContent()) {
      throw new NdefEncoderException("Expected content", record);
    }
=======
    if (config != null) {
      payload = config.getContentEncoder().encodeContent(externalType).getBytes(NdefConstants.DEFAULT_CHARSET);
    } else {
      if (externalType instanceof UnsupportedExternalTypeRecord) {
        UnsupportedExternalTypeRecord externalTypeUnsupportedRecord = (UnsupportedExternalTypeRecord) externalType;
        payload = externalTypeUnsupportedRecord.getContent().getBytes(NdefConstants.DEFAULT_CHARSET);
      } else {
        throw new IllegalArgumentException("Unable to encode external type " + externalType.getClass().getName());
      }
    }
>>>>>>> /usr/src/app/output/grundid/nfctools/15659385c0127435a9d7ed840ff8f444d3d63053/nfctools-ndef/src/main/java/org/nfctools/ndef/ext/ExternalTypeEncoder.java/right.java

    byte[] type = externalType.getNamespace().getBytes(NdefConstants.DEFAULT_CHARSET);
    return new NdefRecord(NdefConstants.TNF_EXTERNAL_TYPE, type, record.getId(), payload);
  }

  public void addRecordConfig(ExternalTypeRecordConfig config) {
    externalRecordTypes.put(config.getRecordClass(), config);
  }
}