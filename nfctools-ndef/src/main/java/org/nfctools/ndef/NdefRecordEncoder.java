package org.nfctools.ndef;
import java.util.ArrayList;
import java.util.List;
import org.nfctools.ndef.auri.AbsoluteUriRecordEncoder;
import org.nfctools.ndef.empty.EmptyRecordEncoder;
import org.nfctools.ndef.ext.ExternalTypeEncoder;
import org.nfctools.ndef.ext.ExternalTypeRecordConfig;
import org.nfctools.ndef.mime.MimeRecordEncoder;
import org.nfctools.ndef.unknown.UnknownRecordEncoder;
import org.nfctools.ndef.unknown.unsupported.UnsupportedRecordEncoder;
import org.nfctools.ndef.wkt.WellKnownRecordConfig;
import org.nfctools.ndef.wkt.WellKnownRecordEncoder;
import org.nfctools.ndef.wkt.encoder.RecordEncoder;

public class NdefRecordEncoder {
  private List<RecordEncoder> knownRecordEncoders = new ArrayList<RecordEncoder>();

  private WellKnownRecordEncoder wellKnownRecordEncoder = new WellKnownRecordEncoder();

  private ExternalTypeEncoder externalTypeEncoder = new ExternalTypeEncoder();

  public NdefRecordEncoder() {
    knownRecordEncoders.add(wellKnownRecordEncoder);
    knownRecordEncoders.add(new MimeRecordEncoder());
    knownRecordEncoders.add(new AbsoluteUriRecordEncoder());
    knownRecordEncoders.add(externalTypeEncoder);
    knownRecordEncoders.add(new EmptyRecordEncoder());
    knownRecordEncoders.add(new UnknownRecordEncoder());
    knownRecordEncoders.add(new UnsupportedRecordEncoder());
  }

  public NdefRecord encode(Record record, NdefMessageEncoder messageEncoder) {
    for (RecordEncoder encoder : knownRecordEncoders) {
      if (encoder.canEncode(record)) {
        return encoder.encodeRecord(record, messageEncoder);
      }
    }
    throw new IllegalArgumentException("Unsupported record [" + record.getClass().getName() + "]");
  }

  public void registerWellKnownRecordConfig(WellKnownRecordConfig recordconfig) {
    wellKnownRecordEncoder.addRecordConfig(recordconfig);
  }

  public void registerExternalTypeRecordConfig(ExternalTypeRecordConfig recordconfig) {
    externalTypeEncoder.addRecordConfig(recordconfig);
  }
}