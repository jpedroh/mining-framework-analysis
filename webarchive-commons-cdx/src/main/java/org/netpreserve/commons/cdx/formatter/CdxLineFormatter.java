package org.netpreserve.commons.cdx.formatter;
import java.io.IOException;
import java.io.Writer;
import org.netpreserve.commons.cdx.CdxFormat;
import org.netpreserve.commons.cdx.cdxrecord.CdxLineFormat;
import org.netpreserve.commons.cdx.cdxrecord.CdxLineRecordKey;
import org.netpreserve.commons.cdx.CdxRecord;
import org.netpreserve.commons.cdx.CdxRecordKey;
import org.netpreserve.commons.cdx.FieldName;
import org.netpreserve.commons.cdx.json.NullValue;
import org.netpreserve.commons.cdx.json.NumberValue;
import org.netpreserve.commons.cdx.json.StringValue;
import org.netpreserve.commons.cdx.json.Value;
import org.netpreserve.commons.uri.Uri;

/**
 *
 */
public class CdxLineFormatter implements CdxFormatter {
  @Override public void format(final Writer out, final CdxRecord record, final CdxFormat outputFormat) throws IOException {
    CdxLineFormat format = (CdxLineFormat) outputFormat;
    CdxRecordKey key = record.getKey();
    if (key instanceof CdxLineRecordKey) {
      out.write(((CdxLineRecordKey) key).getUnparsed());
    } else {
      out.write(key.getUriKey().getValue());
      out.write(' ');
      out.write(key.getTimeStamp().getValue().toFormattedString(format.getKeyDateFormat()));
    }
    for (int i = 2; i < format.getLength(); i++) {
      FieldName fieldName = format.getField(i);
      Value value = record.get(fieldName);
      if (value == NullValue.NULL) {
        if (fieldName == FieldName.FILENAME) {
          Uri locator = record.get(FieldName.RESOURCE_REF).getValue();
          if ("warcfile".equals(locator.getScheme())) {
            value = StringValue.valueOf(locator.getPath());
          }
        } else {
          if (fieldName == FieldName.OFFSET) {
            Uri locator = record.get(FieldName.RESOURCE_REF).getValue();
            if ("warcfile".equals(locator.getScheme())) {
              value = NumberValue.valueOf(locator.getFragment());
            }
          }
        }
      }
      out.append(' ');
      if (value == NullValue.NULL) {
        out.write('-');
      } else {
        String v = value.toString();
        if (v.contains(" ")) {
          v = v.substring(0, v.indexOf(' '));
        }
        out.write(v);
      }
    }
  }
}