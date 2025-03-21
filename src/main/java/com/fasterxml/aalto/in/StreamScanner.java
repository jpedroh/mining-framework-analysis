package com.fasterxml.aalto.in;
import java.io.*;
import javax.xml.stream.XMLStreamException;
import com.fasterxml.aalto.impl.ErrorConsts;
import com.fasterxml.aalto.impl.IoStreamException;
import com.fasterxml.aalto.util.DataUtil;
import com.fasterxml.aalto.util.TextBuilder;

/**
 * Base class for various byte stream based scanners (generally one
 * for each type of encoding supported).
 */
public abstract class StreamScanner extends ByteBasedScanner {
  /**
     * Underlying InputStream to use for reading content.
     */
  protected InputStream _in;

  protected byte[] _inputBuffer;

  public StreamScanner(ReaderConfig cfg, InputStream in, byte[] buffer, int ptr, int last) {
    super(cfg);
    _in = in;
    _inputBuffer = buffer;
    _inputPtr = ptr;
    _inputEnd = last;
  }

  @Override protected void _releaseBuffers() {
    super._releaseBuffers();
    if (_in != null && _inputBuffer != null) {
      _config.freeFullBBuffer(_inputBuffer);
      _inputBuffer = null;
    }
  }

  @Override protected void _closeSource() throws IOException {
    if (_in != null) {
      _in.close();
      _in = null;
    }
  }

  protected abstract int handleEntityInText(boolean inAttr) throws XMLStreamException;

  protected abstract String parsePublicId(byte quoteChar) throws XMLStreamException;

  protected abstract String parseSystemId(byte quoteChar) throws XMLStreamException;

  @Override public final int nextFromProlog(boolean isProlog) throws XMLStreamException {
    if (_tokenIncomplete) {
      skipToken();
    }
    setStartLocation();
    while (true) {
      if (_inputPtr >= _inputEnd) {
        if (!loadMore()) {
          setStartLocation();
          return TOKEN_EOI;
        }
      }
      int c = _inputBuffer[_inputPtr++] & 0xFF;
      if (c == INT_LT) {
        break;
      }
      if (c != INT_SPACE) {
        if (c == INT_LF) {
          markLF();
        } else {
          if (c == INT_CR) {
            if (_inputPtr >= _inputEnd) {
              if (!loadMore()) {
                markLF();
                setStartLocation();
                return TOKEN_EOI;
              }
            }
            if (_inputBuffer[_inputPtr] == BYTE_LF) {
              ++_inputPtr;
            }
            markLF();
          } else {
            if (c != INT_TAB) {
              reportPrologUnexpChar(isProlog, decodeCharForError((byte) c), null);
            }
          }
        }
      }
    }
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed(COMMENT);
    }
    byte b = _inputBuffer[_inputPtr++];
    if (b == BYTE_EXCL) {
      return handlePrologDeclStart(isProlog);
    }
    if (b == BYTE_QMARK) {
      return handlePIStart();
    }
    if (b == BYTE_SLASH || !isProlog) {
      reportPrologUnexpChar(isProlog, decodeCharForError(b), " (unbalanced start/end tags?)");
    }
    return handleStartElement(b);
  }

  @Override public final int nextFromTree() throws XMLStreamException {
    if (_tokenIncomplete) {
      if (skipToken()) {
        return _nextEntity();
      }
    } else {
      if (_currToken == START_ELEMENT) {
        if (_isEmptyTag) {
          --_depth;
          return (_currToken = END_ELEMENT);
        }
      } else {
        if (_currToken == END_ELEMENT) {
          _currElem = _currElem.getParent();
          while (_lastNsDecl != null && _lastNsDecl.getLevel() >= _depth) {
            _lastNsDecl = _lastNsDecl.unbind();
          }
        } else {
          if (_entityPending) {
            _entityPending = false;
            return _nextEntity();
          }
        }
      }
    }
    setStartLocation();
    if (_inputPtr >= _inputEnd) {
      if (!loadMore()) {
        setStartLocation();
        return TOKEN_EOI;
      }
    }
    byte b = _inputBuffer[_inputPtr];
    if (b == BYTE_LT) {
      ++_inputPtr;
      b = (_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne(COMMENT);
      if (b == BYTE_EXCL) {
        return handleCommentOrCdataStart();
      }
      if (b == BYTE_QMARK) {
        return handlePIStart();
      }
      if (b == BYTE_SLASH) {
        return handleEndElement();
      }
      return handleStartElement(b);
    }
    if (b == BYTE_AMP) {
      ++_inputPtr;
      int i = handleEntityInText(false);
      if (i == 0) {
        return (_currToken = ENTITY_REFERENCE);
      }
      _tmpChar = -i;
    } else {
      _tmpChar = (int) b & 0xFF;
    }
    if (_cfgLazyParsing) {
      _tokenIncomplete = true;
    } else {
      finishCharacters();
    }
    return (_currToken = CHARACTERS);
  }

  /**
     * Helper method used to isolate things that need to be (re)set in
     * cases where 
     */
  protected int _nextEntity() {
    _textBuilder.resetWithEmpty();
    return (_currToken = ENTITY_REFERENCE);
  }

  private final int handlePrologDeclStart(boolean isProlog) throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    byte b = _inputBuffer[_inputPtr++];
    if (b == BYTE_HYPHEN) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      b = _inputBuffer[_inputPtr++];
      if (b == BYTE_HYPHEN) {
        if (_cfgLazyParsing) {
          _tokenIncomplete = true;
        } else {
          finishComment();
        }
        return (_currToken = COMMENT);
      }
    } else {
      if (b == BYTE_D) {
        if (isProlog) {
          handleDtdStart();
          if (!_cfgLazyParsing) {
            if (_tokenIncomplete) {
              finishDTD(true);
              _tokenIncomplete = false;
            }
          }
          return DTD;
        }
      }
    }
    _tokenIncomplete = true;
    _currToken = CHARACTERS;
    reportPrologUnexpChar(isProlog, decodeCharForError(b), " (expected \'-\' for COMMENT)");
    return _currToken;
  }

  private final int handleDtdStart() throws XMLStreamException {
    matchAsciiKeyword("DOCTYPE");
    byte b = skipInternalWs(true, "after DOCTYPE keyword, before root name");
    _tokenName = parsePName(b);
    b = skipInternalWs(false, null);
    if (b == BYTE_P) {
      matchAsciiKeyword("PUBLIC");
      b = skipInternalWs(true, null);
      _publicId = parsePublicId(b);
      b = skipInternalWs(true, null);
      _systemId = parseSystemId(b);
      b = skipInternalWs(false, null);
    } else {
      if (b == BYTE_S) {
        matchAsciiKeyword("SYSTEM");
        b = skipInternalWs(true, null);
        _publicId = null;
        _systemId = parseSystemId(b);
        b = skipInternalWs(false, null);
      } else {
        _publicId = _systemId = null;
      }
    }
    if (b == BYTE_GT) {
      _tokenIncomplete = false;
      return (_currToken = DTD);
    }
    if (b != BYTE_LBRACKET) {
      String msg = (_systemId != null) ? " (expected \'[\' for the internal subset, or \'>\' to end DOCTYPE declaration)" : " (expected a \'PUBLIC\' or \'SYSTEM\' keyword, \'[\' for the internal subset, or \'>\' to end DOCTYPE declaration)";
      reportTreeUnexpChar(decodeCharForError(b), msg);
    }
    _tokenIncomplete = true;
    return (_currToken = DTD);
  }

  private final int handleCommentOrCdataStart() throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    byte b = _inputBuffer[_inputPtr++];
    if (b == BYTE_HYPHEN) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      b = _inputBuffer[_inputPtr++];
      if (b != BYTE_HYPHEN) {
        reportTreeUnexpChar(decodeCharForError(b), " (expected \'-\' for COMMENT)");
      }
      if (_cfgLazyParsing) {
        _tokenIncomplete = true;
      } else {
        finishComment();
      }
      return (_currToken = COMMENT);
    }
    if (b == BYTE_LBRACKET) {
      _currToken = CDATA;
      for (int i = 0; i < 6; ++i) {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        b = _inputBuffer[_inputPtr++];
        if (b != (byte) CDATA_STR.charAt(i)) {
          int ch = decodeCharForError(b);
          reportTreeUnexpChar(ch, " (expected \'" + CDATA_STR.charAt(i) + "\' for CDATA section)");
        }
      }
      if (_cfgLazyParsing) {
        _tokenIncomplete = true;
      } else {
        finishCData();
      }
      return CDATA;
    }
    reportTreeUnexpChar(decodeCharForError(b), " (expected either \'-\' for COMMENT or \'[CDATA[\' for CDATA section)");
    return TOKEN_EOI;
  }

  /**
     * Method called after leading '<?' has been parsed; needs to parse
     * target.
     */
  private final int handlePIStart() throws XMLStreamException {
    _currToken = PROCESSING_INSTRUCTION;
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    byte b = _inputBuffer[_inputPtr++];
    _tokenName = parsePName(b);
    {
      String ln = _tokenName.getLocalName();
      if (ln.length() == 3 && ln.equalsIgnoreCase("xml") && _tokenName.getPrefix() == null) {
        reportInputProblem(ErrorConsts.ERR_WF_PI_XML_TARGET);
      }
    }
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    int c = (int) _inputBuffer[_inputPtr++] & 0xFF;
    if (c <= INT_SPACE) {
      while (true) {
        if (c == INT_LF) {
          markLF();
        } else {
          if (c == INT_CR) {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (_inputBuffer[_inputPtr] == BYTE_LF) {
              ++_inputPtr;
            }
            markLF();
          } else {
            if (c != INT_SPACE && c != INT_TAB) {
              throwInvalidSpace(c);
            }
          }
        }
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = (int) _inputBuffer[_inputPtr] & 0xFF;
        if (c > INT_SPACE) {
          break;
        }
        ++_inputPtr;
      }
      if (_cfgLazyParsing) {
        _tokenIncomplete = true;
      } else {
        finishPI();
      }
    } else {
      if (c != INT_QMARK) {
        reportMissingPISpace(decodeCharForError((byte) c));
      }
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      b = _inputBuffer[_inputPtr++];
      if (b != BYTE_GT) {
        reportMissingPISpace(decodeCharForError(b));
      }
      _textBuilder.resetWithEmpty();
      _tokenIncomplete = false;
    }
    return PROCESSING_INSTRUCTION;
  }

  /**
     * @return Code point for the entity that expands to a valid XML
     *    content character.
     */
  protected final int handleCharEntity() throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    byte b = _inputBuffer[_inputPtr++];
    int value = 0;
    if (b == BYTE_x) {
      while (true) {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        b = _inputBuffer[_inputPtr++];
        if (b == BYTE_SEMICOLON) {
          break;
        }
        value = value << 4;
        int c = (int) b;
        if (c <= '9' && c >= '0') {
          value += (c - '0');
        } else {
          if (c >= 'a' && c <= 'f') {
            value += 10 + (c - 'a');
          } else {
            if (c >= 'A' && c <= 'F') {
              value += 10 + (c - 'A');
            } else {
              throwUnexpectedChar(decodeCharForError(b), "; expected a hex digit (0-9a-fA-F)");
            }
          }
        }
        if (value > MAX_UNICODE_CHAR) {
          reportEntityOverflow();
        }
      }
    } else {
      while (b != BYTE_SEMICOLON) {
        int c = (int) b;
        if (c <= '9' && c >= '0') {
          value = (value * 10) + (c - '0');
          if (value > MAX_UNICODE_CHAR) {
            reportEntityOverflow();
          }
        } else {
          throwUnexpectedChar(decodeCharForError(b), "; expected a decimal number");
        }
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        b = _inputBuffer[_inputPtr++];
      }
    }
    verifyXmlChar(value);
    return value;
  }

  /**
     * Parsing of start element requires parsing of the element name
     * (and attribute names), and is thus encoding-specific.
     */
  protected abstract int handleStartElement(byte b) throws XMLStreamException;

  /**
     * Note that this method is currently also shareable for all Ascii-based
     * encodings, and at least between UTF-8 and ISO-Latin1. The reason is
     * that since we already know exact bytes that need to be matched,
     * there's no danger of getting invalid encodings or such.
     * So, for now, let's leave this method here in the base class.
     */
  protected final int handleEndElement() throws XMLStreamException {
    --_depth;
    _currToken = END_ELEMENT;
    _tokenName = _currElem.getName();
    int size = _tokenName.sizeInQuads();
    if ((_inputEnd - _inputPtr) < ((size << 2) + 1)) {
      return handleEndElementSlow(size);
    }
    int ptr = _inputPtr;
    byte[] buf = _inputBuffer;
    --size;
    for (int qix = 0; qix < size; ++qix) {
      int q = (buf[ptr] << 24) | ((buf[ptr + 1] & 0xFF) << 16) | ((buf[ptr + 2] & 0xFF) << 8) | ((buf[ptr + 3] & 0xFF));
      ptr += 4;
      if (q != _tokenName.getQuad(qix)) {
        _inputPtr = ptr;
        reportUnexpectedEndTag(_tokenName.getPrefixedName());
      }
    }
    int lastQ = _tokenName.getQuad(size);
    int q = buf[ptr++] & 0xFF;
    if (q != lastQ) {
      q = (q << 8) | (buf[ptr++] & 0xFF);
      if (q != lastQ) {
        q = (q << 8) | (buf[ptr++] & 0xFF);
        if (q != lastQ) {
          q = (q << 8) | (buf[ptr++] & 0xFF);
          if (q != lastQ) {
            _inputPtr = ptr;
            reportUnexpectedEndTag(_tokenName.getPrefixedName());
          }
        }
      }
    }
    int i2 = _inputBuffer[ptr] & 0xFF;
    _inputPtr = ptr + 1;
    while (i2 <= INT_SPACE) {
      if (i2 == INT_LF) {
        markLF();
      } else {
        if (i2 == INT_CR) {
          byte b = (_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne();
          if (b != BYTE_LF) {
            markLF(_inputPtr - 1);
            i2 = (int) b & 0xFF;
            continue;
          }
          markLF();
        } else {
          if (i2 != INT_SPACE && i2 != INT_TAB) {
            throwInvalidSpace(i2);
          }
        }
      }
      i2 = (int) ((_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne()) & 0xFF;
    }
    if (i2 != INT_GT) {
      throwUnexpectedChar(decodeCharForError((byte) i2), " expected space or closing \'>\'");
    }
    return END_ELEMENT;
  }

  private final int handleEndElementSlow(int size) throws XMLStreamException {
    --size;
    for (int qix = 0; qix < size; ++qix) {
      int q = 0;
      for (int i = 0; i < 4; ++i) {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        q = (q << 8) | (_inputBuffer[_inputPtr++] & 0xFF);
      }
      if (q != _tokenName.getQuad(qix)) {
        reportUnexpectedEndTag(_tokenName.getPrefixedName());
      }
    }
    int lastQ = _tokenName.getQuad(size);
    int q = 0;
    int i = 0;
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      q = (q << 8) | (_inputBuffer[_inputPtr++] & 0xFF);
      if (q == lastQ) {
        break;
      }
      if (++i > 3) {
        reportUnexpectedEndTag(_tokenName.getPrefixedName());
        break;
      }
    }
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    int i2 = _inputBuffer[_inputPtr++];
    while (i2 <= INT_SPACE) {
      if (i2 == INT_LF) {
        markLF();
      } else {
        if (i2 == INT_CR) {
          byte b = (_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne();
          if (b != BYTE_LF) {
            markLF(_inputPtr - 1);
            i2 = (int) b & 0xFF;
            continue;
          }
          markLF();
        } else {
          if (i2 != INT_SPACE && i2 != INT_TAB) {
            throwInvalidSpace(i2);
          }
        }
      }
      i2 = (int) ((_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne()) & 0xFF;
    }
    if (i2 != INT_GT) {
      throwUnexpectedChar(decodeCharForError((byte) i2), " expected space or closing \'>\'");
    }
    return END_ELEMENT;
  }

  /**
     * This method can (for now?) be shared between all Ascii-based
     * encodings, since it only does coarse validity checking -- real
     * checks are done in different method.
     *<p>
     * Some notes about assumption implementation makes:
     *<ul>
     * <li>Well-formed xml content can not end with a name: as such,
     *    end-of-input is an error and we can throw an exception
     *  </li>
     * </ul>
     */
  protected final PName parsePName(byte b) throws XMLStreamException {
    if ((_inputEnd - _inputPtr) < 8) {
      return parsePNameSlow(b);
    }
    int q = b & 0xFF;
    if (q < INT_A) {
      throwUnexpectedChar(q, "; expected a name start character");
    }
    int i2 = _inputBuffer[_inputPtr++] & 0xFF;
    if (i2 < 65) {
      if (i2 < 45 || i2 > 58 || i2 == 47) {
        return findPName(q, 1);
      }
    }
    q = (q << 8) | i2;
    i2 = (int) _inputBuffer[_inputPtr++] & 0xFF;
    if (i2 < 65) {
      if (i2 < 45 || i2 > 58 || i2 == 47) {
        return findPName(q, 2);
      }
    }
    q = (q << 8) | i2;
    i2 = (int) _inputBuffer[_inputPtr++] & 0xFF;
    if (i2 < 65) {
      if (i2 < 45 || i2 > 58 || i2 == 47) {
        return findPName(q, 3);
      }
    }
    q = (q << 8) | i2;
    i2 = (int) _inputBuffer[_inputPtr++] & 0xFF;
    if (i2 < 65) {
      if (i2 < 45 || i2 > 58 || i2 == 47) {
        return findPName(q, 4);
      }
    }
    return parsePNameMedium(i2, q);
  }

  protected PName parsePNameMedium(int i2, int q1) throws XMLStreamException {
    int q2 = i2;
    i2 = _inputBuffer[_inputPtr++] & 0xFF;
    if (i2 < 65) {
      if (i2 < 45 || i2 > 58 || i2 == 47) {
        return findPName(q1, q2, 1);
      }
    }
    q2 = (q2 << 8) | i2;
    i2 = (int) _inputBuffer[_inputPtr++] & 0xFF;
    if (i2 < 65) {
      if (i2 < 45 || i2 > 58 || i2 == 47) {
        return findPName(q1, q2, 2);
      }
    }
    q2 = (q2 << 8) | i2;
    i2 = (int) _inputBuffer[_inputPtr++] & 0xFF;
    if (i2 < 65) {
      if (i2 < 45 || i2 > 58 || i2 == 47) {
        return findPName(q1, q2, 3);
      }
    }
    q2 = (q2 << 8) | i2;
    i2 = (int) _inputBuffer[_inputPtr++] & 0xFF;
    if (i2 < 65) {
      if (i2 < 45 || i2 > 58 || i2 == 47) {
        return findPName(q1, q2, 4);
      }
    }
    int[] quads = _quadBuffer;
    quads[0] = q1;
    quads[1] = q2;
    return parsePNameLong(i2, quads);
  }

  protected final PName parsePNameLong(int q, int[] quads) throws XMLStreamException {
    int qix = 2;
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      int i2 = _inputBuffer[_inputPtr++] & 0xFF;
      if (i2 < 65) {
        if (i2 < 45 || i2 > 58 || i2 == 47) {
          return findPName(q, quads, qix, 1);
        }
      }
      q = (q << 8) | i2;
      i2 = (int) ((_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne()) & 0xFF;
      if (i2 < 65) {
        if (i2 < 45 || i2 > 58 || i2 == 47) {
          return findPName(q, quads, qix, 2);
        }
      }
      q = (q << 8) | i2;
      i2 = (int) ((_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne()) & 0xFF;
      if (i2 < 65) {
        if (i2 < 45 || i2 > 58 || i2 == 47) {
          return findPName(q, quads, qix, 3);
        }
      }
      q = (q << 8) | i2;
      i2 = (int) ((_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne()) & 0xFF;
      if (i2 < 65) {
        if (i2 < 45 || i2 > 58 || i2 == 47) {
          return findPName(q, quads, qix, 4);
        }
      }
      if (qix >= quads.length) {
        _quadBuffer = quads = DataUtil.growArrayBy(quads, quads.length);
      }
      quads[qix] = q;
      ++qix;
      q = i2;
    }
  }

  protected final PName parsePNameSlow(byte b) throws XMLStreamException {
    int q = b & 0xFF;
    if (q < INT_A) {
      throwUnexpectedChar(q, "; expected a name start character");
    }
    int[] quads = _quadBuffer;
    int qix = 0;
    int firstQuad = 0;
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      int i2 = _inputBuffer[_inputPtr++] & 0xFF;
      if (i2 < 65) {
        if (i2 < 45 || i2 > 58 || i2 == 47) {
          return findPName(q, 1, firstQuad, qix, quads);
        }
      }
      q = (q << 8) | i2;
      i2 = (int) ((_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne()) & 0xFF;
      if (i2 < 65) {
        if (i2 < 45 || i2 > 58 || i2 == 47) {
          return findPName(q, 2, firstQuad, qix, quads);
        }
      }
      q = (q << 8) | i2;
      i2 = (int) ((_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne()) & 0xFF;
      if (i2 < 65) {
        if (i2 < 45 || i2 > 58 || i2 == 47) {
          return findPName(q, 3, firstQuad, qix, quads);
        }
      }
      q = (q << 8) | i2;
      i2 = (int) ((_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne()) & 0xFF;
      if (i2 < 65) {
        if (i2 < 45 || i2 > 58 || i2 == 47) {
          return findPName(q, 4, firstQuad, qix, quads);
        }
      }
      if (qix == 0) {
        firstQuad = q;
      } else {
        if (qix == 1) {
          quads[0] = firstQuad;
          quads[1] = q;
        } else {
          if (qix >= quads.length) {
            _quadBuffer = quads = DataUtil.growArrayBy(quads, quads.length);
          }
          quads[qix] = q;
        }
      }
      ++qix;
      q = i2;
    }
  }

  /**
     * Method called to process a sequence of bytes that is likely to
     * be a PName. At this point we encountered an end marker, and
     * may either hit a formerly seen well-formed PName; an as-of-yet
     * unseen well-formed PName; or a non-well-formed sequence (containing
     * one or more non-name chars without any valid end markers).
     *
     * @param onlyQuad Word with 1 to 4 bytes that make up PName
     * @param lastByteCount Number of actual bytes contained in onlyQuad; 0 to 3.
     */
  private final PName findPName(int onlyQuad, int lastByteCount) throws XMLStreamException {
    --_inputPtr;
    int hash = ByteBasedPNameTable.calcHash(onlyQuad);
    PName name = _symbols.findSymbol(hash, onlyQuad, 0);
    if (name == null) {
      _quadBuffer[0] = onlyQuad;
      name = addPName(hash, _quadBuffer, 1, lastByteCount);
    }
    return name;
  }

  /**
     * Method called to process a sequence of bytes that is likely to
     * be a PName. At this point we encountered an end marker, and
     * may either hit a formerly seen well-formed PName; an as-of-yet
     * unseen well-formed PName; or a non-well-formed sequence (containing
     * one or more non-name chars without any valid end markers).
     *
     * @param firstQuad First 1 to 4 bytes of the PName
     * @param secondQuad Word with last 1 to 4 bytes of the PName
     * @param lastByteCount Number of bytes contained in secondQuad; 0 to 3.
     */
  private final PName findPName(int firstQuad, int secondQuad, int lastByteCount) throws XMLStreamException {
    --_inputPtr;
    int hash = ByteBasedPNameTable.calcHash(firstQuad, secondQuad);
    PName name = _symbols.findSymbol(hash, firstQuad, secondQuad);
    if (name == null) {
      _quadBuffer[0] = firstQuad;
      _quadBuffer[1] = secondQuad;
      name = addPName(hash, _quadBuffer, 2, lastByteCount);
    }
    return name;
  }

  /**
     * Method called to process a sequence of bytes that is likely to
     * be a PName. At this point we encountered an end marker, and
     * may either hit a formerly seen well-formed PName; an as-of-yet
     * unseen well-formed PName; or a non-well-formed sequence (containing
     * one or more non-name chars without any valid end markers).
     *
     * @param lastQuad Word with last 0 to 3 bytes of the PName; not included
     *   in the quad array
     * @param quads Array that contains all the quads, except for the
     *    last one, for names with more than 8 bytes (i.e. more than
     *    2 quads)
     * @param qlen Number of quads in the array, except if less than 2
     *    (in which case only firstQuad and lastQuad are used)
     * @param lastByteCount Number of bytes contained in lastQuad; 0 to 3.
     */
  private final PName findPName(int lastQuad, int[] quads, int qlen, int lastByteCount) throws XMLStreamException {
    --_inputPtr;
    if (qlen >= quads.length) {
      _quadBuffer = quads = DataUtil.growArrayBy(quads, quads.length);
    }
    quads[qlen++] = lastQuad;
    int hash = ByteBasedPNameTable.calcHash(quads, qlen);
    PName name = _symbols.findSymbol(hash, quads, qlen);
    if (name == null) {
      name = addPName(hash, quads, qlen, lastByteCount);
    }
    return name;
  }

  /**
     * Method called to process a sequence of bytes that is likely to
     * be a PName. At this point we encountered an end marker, and
     * may either hit a formerly seen well-formed PName; an as-of-yet
     * unseen well-formed PName; or a non-well-formed sequence (containing
     * one or more non-name chars without any valid end markers).
     *
     * @param lastQuad Word with last 0 to 3 bytes of the PName; not included
     *   in the quad array
     * @param lastByteCount Number of bytes contained in lastQuad; 0 to 3.
     * @param firstQuad First 1 to 4 bytes of the PName (4 if length
     *    at least 4 bytes; less only if not). 
     * @param qlen Number of quads in the array, except if less than 2
     *    (in which case only firstQuad and lastQuad are used)
     * @param quads Array that contains all the quads, except for the
     *    last one, for names with more than 8 bytes (i.e. more than
     *    2 quads)
     */
  private final PName findPName(int lastQuad, int lastByteCount, int firstQuad, int qlen, int[] quads) throws XMLStreamException {
    if (qlen <= 1) {
      if (qlen == 0) {
        return findPName(lastQuad, lastByteCount);
      }
      return findPName(firstQuad, lastQuad, lastByteCount);
    }
    return findPName(lastQuad, quads, qlen, lastByteCount);
  }

  /**
     * @return First byte following skipped white space
     */
  protected byte skipInternalWs(boolean reqd, String msg) throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    byte b = _inputBuffer[_inputPtr++];
    int c = b & 0xFF;
    if (c > INT_SPACE) {
      if (!reqd) {
        return b;
      }
      reportTreeUnexpChar(decodeCharForError(b), " (expected white space " + msg + ")");
    }
    do {
      if (b == BYTE_LF) {
        markLF();
      } else {
        if (b == BYTE_CR) {
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          if (_inputBuffer[_inputPtr] == BYTE_LF) {
            ++_inputPtr;
          }
          markLF();
        } else {
          if (b != BYTE_SPACE && b != BYTE_TAB) {
            throwInvalidSpace(b);
          }
        }
      }
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      b = _inputBuffer[_inputPtr++];
    } while((b & 0xFF) <= INT_SPACE);
    return b;
  }

  private final void matchAsciiKeyword(String keyw) throws XMLStreamException {
    for (int i = 1, len = keyw.length(); i < len; ++i) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      byte b = _inputBuffer[_inputPtr++];
      if (b != (byte) keyw.charAt(i)) {
        reportTreeUnexpChar(decodeCharForError(b), " (expected \'" + keyw.charAt(i) + "\' for " + keyw + " keyword)");
      }
    }
  }

  /**
     *<p>
     * Note: consequtive white space is only considered indentation,
     * if the following token seems like a tag (start/end). This so
     * that if a CDATA section follows, it can be coalesced in
     * coalescing mode. Although we could check if coalescing mode is
     * enabled, this should seldom have significant effect either way,
     * so it removes one possible source of problems in coalescing mode.
     *
     * @return -1, if indentation was handled; offset in the output
     *    buffer, if not
     */
  protected final int checkInTreeIndentation(int c) throws XMLStreamException {
    if (c == INT_CR) {
      if (_inputPtr >= _inputEnd && !loadMore()) {
        _textBuilder.resetWithIndentation(0, CHAR_SPACE);
        return -1;
      }
      if (_inputBuffer[_inputPtr] == BYTE_LF) {
        ++_inputPtr;
      }
    }
    markLF();
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    byte b = _inputBuffer[_inputPtr];
    if (b != BYTE_SPACE && b != BYTE_TAB) {
      if (b == BYTE_LT) {
        if ((_inputPtr + 1) < _inputEnd && _inputBuffer[_inputPtr + 1] != BYTE_EXCL) {
          _textBuilder.resetWithIndentation(0, CHAR_SPACE);
          return -1;
        }
      }
      char[] outBuf = _textBuilder.resetWithEmpty();
      outBuf[0] = CHAR_LF;
      _textBuilder.setCurrentLength(1);
      return 1;
    }
    ++_inputPtr;
    int count = 1;
    int max = (b == BYTE_SPACE) ? TextBuilder.MAX_INDENT_SPACES : TextBuilder.MAX_INDENT_TABS;
    while (count <= max) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      byte b2 = _inputBuffer[_inputPtr];
      if (b2 != b) {
        if (b2 == BYTE_LT && (_inputPtr + 1) < _inputEnd && _inputBuffer[_inputPtr + 1] != BYTE_EXCL) {
          _textBuilder.resetWithIndentation(count, (char) b);
          return -1;
        }
        break;
      }
      ++_inputPtr;
      ++count;
    }
    char[] outBuf = _textBuilder.resetWithEmpty();
    outBuf[0] = CHAR_LF;
    char ind = (char) b;
    for (int i = 1; i <= count; ++i) {
      outBuf[i] = ind;
    }
    count += 1;
    _textBuilder.setCurrentLength(count);
    return count;
  }

  /**
     * @return -1, if indentation was handled; offset in the output
     *    buffer, if not
     */
  protected final int checkPrologIndentation(int c) throws XMLStreamException {
    if (c == INT_CR) {
      if (_inputPtr >= _inputEnd && !loadMore()) {
        _textBuilder.resetWithIndentation(0, CHAR_SPACE);
        return -1;
      }
      if (_inputBuffer[_inputPtr] == BYTE_LF) {
        ++_inputPtr;
      }
    }
    markLF();
    if (_inputPtr >= _inputEnd && !loadMore()) {
      _textBuilder.resetWithIndentation(0, CHAR_SPACE);
      return -1;
    }
    byte b = _inputBuffer[_inputPtr];
    if (b != BYTE_SPACE && b != BYTE_TAB) {
      if (b == BYTE_LT) {
        _textBuilder.resetWithIndentation(0, CHAR_SPACE);
        return -1;
      }
      char[] outBuf = _textBuilder.resetWithEmpty();
      outBuf[0] = CHAR_LF;
      _textBuilder.setCurrentLength(1);
      return 1;
    }
    ++_inputPtr;
    int count = 1;
    int max = (b == BYTE_SPACE) ? TextBuilder.MAX_INDENT_SPACES : TextBuilder.MAX_INDENT_TABS;
    while (true) {
      if (_inputPtr >= _inputEnd && !loadMore()) {
        break;
      }
      if (_inputBuffer[_inputPtr] != b) {
        break;
      }
      ++_inputPtr;
      ++count;
      if (count >= max) {
        char[] outBuf = _textBuilder.resetWithEmpty();
        outBuf[0] = CHAR_LF;
        char ind = (char) b;
        for (int i = 1; i <= count; ++i) {
          outBuf[i] = ind;
        }
        count += 1;
        _textBuilder.setCurrentLength(count);
        return count;
      }
    }
    _textBuilder.resetWithIndentation(count, (char) b);
    return -1;
  }

  @Override protected final boolean loadMore() throws XMLStreamException {
    _pastBytesOrChars += _inputEnd;
    _rowStartOffset -= _inputEnd;
    _inputPtr = 0;
    if (_in == null) {
      _inputEnd = 0;
      return false;
    }
    try {
      int count = _in.read(_inputBuffer, 0, _inputBuffer.length);
      if (count < 1) {
        _inputEnd = 0;
        if (count == 0) {
          reportInputProblem("InputStream returned 0 bytes, even when asked to read up to " + _inputBuffer.length);
        }
        return false;
      }
      _inputEnd = count;
      return true;
    } catch (IOException ioe) {
      throw new IoStreamException(ioe);
    }
  }

  protected final byte nextByte(int tt) throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      if (!loadMore()) {
        reportInputProblem("Unexpected end-of-input when trying to parse " + ErrorConsts.tokenTypeDesc(tt));
      }
    }
    return _inputBuffer[_inputPtr++];
  }

  protected final byte nextByte() throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      if (!loadMore()) {
        reportInputProblem("Unexpected end-of-input when trying to parse " + ErrorConsts.tokenTypeDesc(_currToken));
      }
    }
    return _inputBuffer[_inputPtr++];
  }

  protected final byte loadOne() throws XMLStreamException {
    if (!loadMore()) {
      reportInputProblem("Unexpected end-of-input when trying to parse " + ErrorConsts.tokenTypeDesc(_currToken));
    }
    return _inputBuffer[_inputPtr++];
  }

  protected final byte loadOne(int type) throws XMLStreamException {
    if (!loadMore()) {
      reportInputProblem("Unexpected end-of-input when trying to parse " + ErrorConsts.tokenTypeDesc(type));
    }
    return _inputBuffer[_inputPtr++];
  }

  protected final boolean loadAndRetain(int nrOfChars) throws XMLStreamException {
    if (_in == null) {
      return false;
    }
    _pastBytesOrChars += _inputPtr;
    _rowStartOffset -= _inputPtr;
    int remaining = (_inputEnd - _inputPtr);
    System.arraycopy(_inputBuffer, _inputPtr, _inputBuffer, 0, remaining);
    _inputPtr = 0;
    _inputEnd = remaining;
    try {
      do {
        int max = _inputBuffer.length - _inputEnd;
        int count = _in.read(_inputBuffer, _inputEnd, max);
        if (count < 1) {
          if (count == 0) {
            reportInputProblem("InputStream returned 0 bytes, even when asked to read up to " + max);
          }
          return false;
        }
        _inputEnd += count;
      } while(_inputEnd < nrOfChars);
      return true;
    } catch (IOException ioe) {
      throw new IoStreamException(ioe);
    }
  }
}