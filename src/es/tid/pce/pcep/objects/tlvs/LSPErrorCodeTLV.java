package es.tid.pce.pcep.objects.tlvs;
import es.tid.pce.pcep.objects.MalformedPCEPObjectException;
import es.tid.pce.pcep.objects.ObjectParameters;
import es.tid.protocol.commons.ByteHandler;

public class LSPErrorCodeTLV extends PCEPTLV {
  protected int errorCode;

  public LSPErrorCodeTLV() {
    this.TLVType = ObjectParameters.PCEP_TLV_TYPE_LSP_ERROR_CODE;
  }

  public LSPErrorCodeTLV(byte[] bytes, int offset) throws MalformedPCEPObjectException {
    super(bytes, offset);
    decode();
  }

  @Override public void encode() {
    log.debug("Encoding LSPErrorCodeTLV TLV");
    int length = 4;
    this.setTLVValueLength(length);
    this.tlv_bytes = new byte[this.getTotalTLVLength()];
    encodeHeader();
    int offset = 4;
    ByteHandler.IntToBuffer(0, offset * 8, 32, errorCode, this.tlv_bytes);
  }

  public void decode() throws MalformedPCEPObjectException {
    log.debug("Decoding LSPErrorCodeTLV TLV");
    int offset = 4;
    errorCode = ByteHandler.easyCopy(0, 31, tlv_bytes[offset], tlv_bytes[offset + 1], tlv_bytes[offset + 2], tlv_bytes[offset + 3]);
  }
}