package es.tid.pce.pcep.objects.tlvs;
import es.tid.pce.pcep.objects.MalformedPCEPObjectException;
import es.tid.pce.pcep.objects.ObjectParameters;
import es.tid.protocol.commons.ByteHandler;

public class LSPDatabaseVersionTLV extends PCEPTLV {
  private long LSPStateDBVersion;

  public LSPDatabaseVersionTLV() {
    this.TLVType = ObjectParameters.PCEP_TLV_TYPE_LSP_DATABASE_VERSION;
  }

  public LSPDatabaseVersionTLV(byte[] bytes, int offset) throws MalformedPCEPObjectException {
    super(bytes, offset);
    decode();
  }

  @Override public void encode() {
    log.debug("Encoding LSPDatabaseVersionTLV TLV");
    int length = 8;
    this.setTLVValueLength(length);
    this.tlv_bytes = new byte[this.getTotalTLVLength()];
    this.encodeHeader();
    int offset = 4;
    ByteHandler.LongToBuffer(0, offset * 8, 64, LSPStateDBVersion, this.tlv_bytes);
  }

  public void decode() throws MalformedPCEPObjectException {
    log.debug("Decoding LSPDatabaseVersionTLV TLV");
    int offset = 4;
    LSPStateDBVersion = ByteHandler.easyCopyL(0, 63, this.tlv_bytes[offset + 0], this.tlv_bytes[offset + 1], this.tlv_bytes[offset + 2], this.tlv_bytes[offset + 3], this.tlv_bytes[offset + 4], this.tlv_bytes[offset + 5], this.tlv_bytes[offset + 6], this.tlv_bytes[offset + 7]);
    log.debug("Databse Version TLV ID: " + LSPStateDBVersion);
  }

  public long getLSPStateDBVersion() {
    return LSPStateDBVersion;
  }

  public void setLSPStateDBVersion(long lSPStateDBVersion) {
    LSPStateDBVersion = lSPStateDBVersion;
  }
}