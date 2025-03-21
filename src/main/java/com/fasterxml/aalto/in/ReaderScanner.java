package com.fasterxml.aalto.in;
import java.io.*;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamLocation2;
import com.fasterxml.aalto.impl.ErrorConsts;
import com.fasterxml.aalto.impl.IoStreamException;
import com.fasterxml.aalto.impl.LocationImpl;
import com.fasterxml.aalto.util.DataUtil;
import com.fasterxml.aalto.util.TextBuilder;
import com.fasterxml.aalto.util.XmlCharTypes;
import com.fasterxml.aalto.util.XmlChars;
import com.fasterxml.aalto.util.XmlConsts;

/**
 * This is the concrete scanner implementation used when input comes
 * as a {@link java.io.Reader}. In general using this scanner is quite
 * a bit less optimal than that of {@link java.io.InputStream} based
 * scanner. Nonetheless, it is included for completeness, since Stax
 * interface allows passing Readers as input sources.
 */
public final class ReaderScanner extends XmlScanner {
  /**
     * Although java chars are basically UTF-16 in memory, the closest
     * match for char types is Latin1.
     */
  private final static XmlCharTypes sCharTypes = InputCharTypes.getLatin1CharTypes();

  /**
     * Underlying InputStream to use for reading content.
     */
  protected Reader _in;

  protected char[] _inputBuffer;

  protected int _inputPtr;

  protected int _inputEnd;

  /**
     * Storage location for a single character that can not be pushed
     * back (for example, multi-byte char)
     */
  protected int mTmpChar = INT_NULL;

  /**
     * For now, symbol table contains prefixed names. In future it is
     * possible that they may be split into prefixes and local names?
     */
  protected final CharBasedPNameTable _symbols;

  public ReaderScanner(ReaderConfig cfg, Reader r, char[] buffer, int ptr, int last) {
    super(cfg);
    _in = r;
    _inputBuffer = buffer;
    _inputPtr = ptr;
    _inputEnd = last;
    _pastBytesOrChars = 0;
    _rowStartOffset = 0;
    _symbols = cfg.getCBSymbols();
  }

  public ReaderScanner(ReaderConfig cfg, Reader r) {
    super(cfg);
    _in = r;
    _inputBuffer = cfg.allocFullCBuffer(ReaderConfig.DEFAULT_CHAR_BUFFER_LEN);
    _inputPtr = _inputEnd = 0;
    _pastBytesOrChars = 0;
    _rowStartOffset = 0;
    _symbols = cfg.getCBSymbols();
  }

  @Override protected void _releaseBuffers() {
    super._releaseBuffers();
    if (_symbols.maybeDirty()) {
      _config.updateCBSymbols(_symbols);
    }
    if (_in != null) {
      if (_inputBuffer != null) {
        _config.freeFullCBuffer(_inputBuffer);
        _inputBuffer = null;
      }
    }
  }

  @Override protected void _closeSource() throws IOException {
    if (_in != null) {
      _in.close();
      _in = null;
    }
  }

  @Override protected final void finishToken() throws XMLStreamException {
    _tokenIncomplete = false;
    switch (_currToken) {
      case PROCESSING_INSTRUCTION:
      finishPI();
      break;
      case CHARACTERS:
      finishCharacters();
      break;
      case COMMENT:
      finishComment();
      break;
      case SPACE:
      finishSpace();
      break;
      case DTD:
      finishDTD(true);
      break;
      case CDATA:
      finishCData();
      break;
      default:
      ErrorConsts.throwInternalError();
    }
  }

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
      if (c == '<') {
        break;
      }
      if (c != ' ') {
        if (c == '\n') {
          markLF();
        } else {
          if (c == '\r') {
            if (_inputPtr >= _inputEnd) {
              if (!loadMore()) {
                markLF();
                setStartLocation();
                return TOKEN_EOI;
              }
            }
            if (_inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          } else {
            if (c != '\t') {
              reportPrologUnexpChar(isProlog, c, null);
            }
          }
        }
      }
    }
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed(COMMENT);
    }
    char c = _inputBuffer[_inputPtr++];
    if (c == '!') {
      return handlePrologDeclStart(isProlog);
    }
    if (c == '?') {
      return handlePIStart();
    }
    if (c == '/' || !isProlog) {
      reportPrologUnexpChar(isProlog, c, " (unbalanced start/end tags?)");
    }
    return handleStartElement(c);
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
    char c = _inputBuffer[_inputPtr];
    if (c == '<') {
      ++_inputPtr;
      c = (_inputPtr < _inputEnd) ? _inputBuffer[_inputPtr++] : loadOne(COMMENT);
      if (c == '!') {
        return handleCommentOrCdataStart();
      }
      if (c == '?') {
        return handlePIStart();
      }
      if (c == '/') {
        return handleEndElement();
      }
      return handleStartElement(c);
    }
    if (c == '&') {
      ++_inputPtr;
      int i = handleEntityInText(false);
      if (i == 0) {
        return (_currToken = ENTITY_REFERENCE);
      }
      mTmpChar = -i;
    } else {
      mTmpChar = c;
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

  protected final int handlePrologDeclStart(boolean isProlog) throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    char c = _inputBuffer[_inputPtr++];
    if (c == '-') {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr++];
      if (c == '-') {
        if (_cfgLazyParsing) {
          _tokenIncomplete = true;
        } else {
          finishComment();
        }
        return (_currToken = COMMENT);
      }
    } else {
      if (c == 'D') {
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
    reportPrologUnexpChar(isProlog, c, " (expected \'-\' for COMMENT)");
    return _currToken;
  }

  private final int handleDtdStart() throws XMLStreamException {
    matchAsciiKeyword("DOCTYPE");
    char c = skipInternalWs(true, "after DOCTYPE keyword, before root name");
    _tokenName = parsePName(c);
    c = skipInternalWs(false, null);
    if (c == 'P') {
      matchAsciiKeyword("PUBLIC");
      c = skipInternalWs(true, null);
      _publicId = parsePublicId(c);
      c = skipInternalWs(true, null);
      _systemId = parseSystemId(c);
      c = skipInternalWs(false, null);
    } else {
      if (c == 'S') {
        matchAsciiKeyword("SYSTEM");
        c = skipInternalWs(true, null);
        _publicId = null;
        _systemId = parseSystemId(c);
        c = skipInternalWs(false, null);
      } else {
        _publicId = _systemId = null;
      }
    }
    if (c == '>') {
      _tokenIncomplete = false;
      return (_currToken = DTD);
    }
    if (c != '[') {
      String msg = (_systemId != null) ? " (expected \'[\' for the internal subset, or \'>\' to end DOCTYPE declaration)" : " (expected a \'PUBLIC\' or \'SYSTEM\' keyword, \'[\' for the internal subset, or \'>\' to end DOCTYPE declaration)";
      reportTreeUnexpChar(c, msg);
    }
    _tokenIncomplete = true;
    return (_currToken = DTD);
  }

  protected final int handleCommentOrCdataStart() throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    char c = _inputBuffer[_inputPtr++];
    if (c == '-') {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr++];
      if (c != '-') {
        reportTreeUnexpChar(c, " (expected \'-\' for COMMENT)");
      }
      if (_cfgLazyParsing) {
        _tokenIncomplete = true;
      } else {
        finishComment();
      }
      return (_currToken = COMMENT);
    }
    if (c == '[') {
      _currToken = CDATA;
      for (int i = 0; i < 6; ++i) {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr++];
        if (c != CDATA_STR.charAt(i)) {
          reportTreeUnexpChar(c, " (expected \'" + CDATA_STR.charAt(i) + "\' for CDATA section)");
        }
      }
      if (_cfgLazyParsing) {
        _tokenIncomplete = true;
      } else {
        finishCData();
      }
      return CDATA;
    }
    reportTreeUnexpChar(c, " (expected either \'-\' for COMMENT or \'[CDATA[\' for CDATA section)");
    return TOKEN_EOI;
  }

  protected final int handlePIStart() throws XMLStreamException {
    _currToken = PROCESSING_INSTRUCTION;
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    char c = _inputBuffer[_inputPtr++];
    _tokenName = parsePName(c);
    {
      String ln = _tokenName.getLocalName();
      if (ln.length() == 3 && ln.equalsIgnoreCase("xml") && _tokenName.getPrefix() == null) {
        reportInputProblem(ErrorConsts.ERR_WF_PI_XML_TARGET);
      }
    }
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    c = _inputBuffer[_inputPtr++];
    if (c <= INT_SPACE) {
      while (true) {
        if (c == '\n') {
          markLF();
        } else {
          if (c == '\r') {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (_inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          } else {
            if (c != ' ' && c != '\t') {
              throwInvalidSpace(c);
            }
          }
        }
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr];
        if (c > 0x0020) {
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
        reportMissingPISpace(c);
      }
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr++];
      if (c != '>') {
        reportMissingPISpace(c);
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
    char c = _inputBuffer[_inputPtr++];
    int value = 0;
    if (c == 'x') {
      while (true) {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr++];
        if (c == ';') {
          break;
        }
        value = value << 4;
        if (c <= '9' && c >= '0') {
          value += (c - '0');
        } else {
          if (c >= 'a' && c <= 'f') {
            value += 10 + (c - 'a');
          } else {
            if (c >= 'A' && c <= 'F') {
              value += 10 + (c - 'A');
            } else {
              throwUnexpectedChar(c, "; expected a hex digit (0-9a-fA-F)");
            }
          }
        }
        if (value > MAX_UNICODE_CHAR) {
          reportEntityOverflow();
        }
      }
    } else {
      while (c != ';') {
        if (c <= '9' && c >= '0') {
          value = (value * 10) + (c - '0');
          if (value > MAX_UNICODE_CHAR) {
            reportEntityOverflow();
          }
        } else {
          throwUnexpectedChar(c, "; expected a decimal number");
        }
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr++];
      }
    }
    if (value >= 0xD800) {
      if (value < 0xE000) {
        reportInvalidXmlChar(value);
      }
      if (value == 0xFFFE || value == 0xFFFF) {
        reportInvalidXmlChar(value);
      }
    } else {
      if (value < 32) {
        if (value != INT_LF && value != INT_CR && value != INT_TAB) {
          if (!_xml11 || value == 0) {
            reportInvalidXmlChar(value);
          }
        }
      }
    }
    return value;
  }

  protected final int handleStartElement(char c) throws XMLStreamException {
    _currToken = START_ELEMENT;
    _currNsCount = 0;
    PName elemName = parsePName(c);
    String prefix = elemName.getPrefix();
    boolean allBound;
    if (prefix == null) {
      allBound = true;
    } else {
      elemName = bindName(elemName, prefix);
      allBound = elemName.isBound();
    }
    _tokenName = elemName;
    _currElem = new ElementScope(elemName, _currElem);
    int attrPtr = 0;
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr++];
      if (c <= INT_SPACE) {
        do {
          if (c == INT_LF) {
            markLF();
          } else {
            if (c == INT_CR) {
              if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
              }
              if (_inputBuffer[_inputPtr] == '\n') {
                ++_inputPtr;
              }
              markLF();
            } else {
              if (c != ' ' && c != '\t') {
                throwInvalidSpace(c);
              }
            }
          }
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          c = _inputBuffer[_inputPtr++];
        } while(c <= INT_SPACE);
      } else {
        if (c != INT_SLASH && c != INT_GT) {
          throwUnexpectedChar(c, " expected space, or \'>\' or \"/>\"");
        }
      }
      if (c == INT_SLASH) {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr++];
        if (c != '>') {
          throwUnexpectedChar(c, " expected \'>\'");
        }
        _isEmptyTag = true;
        break;
      } else {
        if (c == '>') {
          _isEmptyTag = false;
          break;
        } else {
          if (c == '<') {
            reportInputProblem("Unexpected \'<\' character in element (missing closing \'>\'?)");
          }
        }
      }
      PName attrName = parsePName(c);
      prefix = attrName.getPrefix();
      boolean isNsDecl;
      if (prefix == null) {
        isNsDecl = (attrName.getLocalName() == "xmlns");
      } else {
        if (prefix == "xmlns") {
          isNsDecl = true;
        } else {
          attrName = bindName(attrName, prefix);
          if (allBound) {
            allBound = attrName.isBound();
          }
          isNsDecl = false;
        }
      }
      while (true) {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr++];
        if (c > INT_SPACE) {
          break;
        }
        if (c == '\n') {
          markLF();
        } else {
          if (c == '\r') {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (_inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          } else {
            if (c != ' ' && c != '\t') {
              throwInvalidSpace(c);
            }
          }
        }
      }
      if (c != '=') {
        throwUnexpectedChar(c, " expected \'=\'");
      }
      while (true) {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr++];
        if (c > INT_SPACE) {
          break;
        }
        if (c == '\n') {
          markLF();
        } else {
          if (c == '\r') {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (_inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          } else {
            if (c != ' ' && c != '\t') {
              throwInvalidSpace(c);
            }
          }
        }
      }
      if (c != '\"' && c != '\'') {
        throwUnexpectedChar(c, " Expected a quote");
      }
      if (isNsDecl) {
        handleNsDeclaration(attrName, c);
        ++_currNsCount;
      } else {
        attrPtr = collectValue(attrPtr, c, attrName);
      }
    }
    {
      int act = _attrCollector.finishLastValue(attrPtr);
      if (act < 0) {
        act = _attrCollector.getCount();
        reportInputProblem(_attrCollector.getErrorMsg());
      }
      _attrCount = act;
    }
    ++_depth;
    if (!allBound) {
      if (!elemName.isBound()) {
        reportUnboundPrefix(_tokenName, false);
      }
      for (int i = 0, len = _attrCount; i < len; ++i) {
        PName attrName = _attrCollector.getName(i);
        if (!attrName.isBound()) {
          reportUnboundPrefix(attrName, true);
        }
      }
    }
    return START_ELEMENT;
  }

  /**
     * This method implements the tight loop for parsing attribute
     * values. It's off-lined from the main start element method to
     * simplify main method, which makes code more maintainable
     * and possibly easier for JIT/HotSpot to optimize.
     */
  private final int collectValue(int attrPtr, char quoteChar, PName attrName) throws XMLStreamException {
    char[] attrBuffer = _attrCollector.startNewValue(attrName, attrPtr);
    final int[] TYPES = sCharTypes.ATTR_CHARS;
    value_loop:
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
        }
        if (attrPtr >= attrBuffer.length) {
          attrBuffer = _attrCollector.valueBufferFull();
        }
        int max = _inputEnd;
        {
          int max2 = ptr + (attrBuffer.length - attrPtr);
          if (max2 < max) {
            max = max2;
          }
        }
        while (ptr < max) {
          c = _inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
          attrBuffer[attrPtr++] = c;
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          if (_inputBuffer[_inputPtr] == '\n') {
            ++_inputPtr;
          }
          case XmlCharTypes.CT_WS_LF:
          markLF();
          case XmlCharTypes.CT_WS_TAB:
          c = ' ';
          break;
          case XmlCharTypes.CT_LT:
          throwUnexpectedChar(c, "\'<\' not allowed in attribute value");
          case XmlCharTypes.CT_AMP:
          {
            int d = handleEntityInText(false);
            if (d == 0) {
              reportUnexpandedEntityInAttr(attrName, false);
            }
            if ((d >> 16) != 0) {
              d -= 0x10000;
              attrBuffer[attrPtr++] = (char) (0xD800 | (d >> 10));
              d = 0xDC00 | (d & 0x3FF);
              if (attrPtr >= attrBuffer.length) {
                attrBuffer = _attrCollector.valueBufferFull();
              }
            }
            c = (char) d;
          }
          break;
          case XmlCharTypes.CT_ATTR_QUOTE:
          if (c == quoteChar) {
            break value_loop;
          }
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            char d = checkSurrogate(c);
            attrBuffer[attrPtr++] = c;
            if (attrPtr >= attrBuffer.length) {
              attrBuffer = _attrCollector.valueBufferFull();
            }
            c = d;
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
      attrBuffer[attrPtr++] = c;
    }
    return attrPtr;
  }

  /**
     * Method called from the main START_ELEMENT handling loop, to
     * parse namespace URI values.
     */
  private void handleNsDeclaration(PName name, char quoteChar) throws XMLStreamException {
    int attrPtr = 0;
    char[] attrBuffer = _nameBuffer;
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      char c = _inputBuffer[_inputPtr++];
      if (c == quoteChar) {
        break;
      }
      if (c == '&') {
        int d = handleEntityInText(false);
        if (d == 0) {
          reportUnexpandedEntityInAttr(name, true);
        }
        if ((d >> 16) != 0) {
          if (attrPtr >= attrBuffer.length) {
            _nameBuffer = attrBuffer = DataUtil.growArrayBy(attrBuffer, attrBuffer.length);
          }
          d -= 0x10000;
          attrBuffer[attrPtr++] = (char) (0xD800 | (d >> 10));
          d = 0xDC00 | (d & 0x3FF);
        }
        c = (char) d;
      } else {
        if (c == '<') {
          throwUnexpectedChar(c, "\'<\' not allowed in attribute value");
        } else {
          if (c < INT_SPACE) {
            if (c == '\n') {
              markLF();
            } else {
              if (c == '\r') {
                if (_inputPtr >= _inputEnd) {
                  loadMoreGuaranteed();
                }
                if (_inputBuffer[_inputPtr] == '\n') {
                  ++_inputPtr;
                }
                markLF();
                c = '\n';
              } else {
                if (c != '\t') {
                  throwInvalidSpace(c);
                }
              }
            }
          }
        }
      }
      if (attrPtr >= attrBuffer.length) {
        _nameBuffer = attrBuffer = DataUtil.growArrayBy(attrBuffer, attrBuffer.length);
      }
      attrBuffer[attrPtr++] = c;
    }
    if (attrPtr == 0) {
      bindNs(name, "");
    } else {
      String uri = _config.canonicalizeURI(attrBuffer, attrPtr);
      bindNs(name, uri);
    }
  }

  protected final int handleEndElement() throws XMLStreamException {
    --_depth;
    _currToken = END_ELEMENT;
    _tokenName = _currElem.getName();
    String pname = _tokenName.getPrefixedName();
    char c;
    int i = 0;
    int len = pname.length();
    do {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr++];
      if (c != pname.charAt(i)) {
        reportUnexpectedEndTag(pname);
      }
    } while(++i < len);
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    c = _inputBuffer[_inputPtr++];
    if (c <= ' ') {
      c = skipInternalWs(false, null);
    } else {
      if (c != '>') {
        if (c == ':' || XmlChars.is10NameChar(c)) {
          reportUnexpectedEndTag(pname);
        }
      }
    }
    if (c != '>') {
      throwUnexpectedChar(c, " expected space or closing \'>\'");
    }
    return END_ELEMENT;
  }

  protected final int handleEntityInText(boolean inAttr) throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    char c = _inputBuffer[_inputPtr++];
    if (c == '#') {
      return handleCharEntity();
    }
    String start;
    if (c == 'a') {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr++];
      if (c == 'm') {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr++];
        if (c == 'p') {
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          c = _inputBuffer[_inputPtr++];
          if (c == ';') {
            return INT_AMP;
          }
          start = "amp";
        } else {
          start = "am";
        }
      } else {
        if (c == 'p') {
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          c = _inputBuffer[_inputPtr++];
          if (c == 'o') {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            c = _inputBuffer[_inputPtr++];
            if (c == 's') {
              if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
              }
              c = _inputBuffer[_inputPtr++];
              if (c == ';') {
                return INT_APOS;
              }
              start = "apos";
            } else {
              start = "apo";
            }
          } else {
            start = "ap";
          }
        } else {
          start = "a";
        }
      }
    } else {
      if (c == 'l') {
        if (_inputPtr >= _inputEnd) {
          loadMoreGuaranteed();
        }
        c = _inputBuffer[_inputPtr++];
        if (c == 't') {
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          c = _inputBuffer[_inputPtr++];
          if (c == ';') {
            return INT_LT;
          }
          start = "lt";
        } else {
          start = "l";
        }
      } else {
        if (c == 'g') {
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          c = _inputBuffer[_inputPtr++];
          if (c == 't') {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            c = _inputBuffer[_inputPtr++];
            if (c == ';') {
              return INT_GT;
            }
            start = "gt";
          } else {
            start = "g";
          }
        } else {
          if (c == 'q') {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            c = _inputBuffer[_inputPtr++];
            if (c == 'u') {
              if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
              }
              c = _inputBuffer[_inputPtr++];
              if (c == 'o') {
                if (_inputPtr >= _inputEnd) {
                  loadMoreGuaranteed();
                }
                c = _inputBuffer[_inputPtr++];
                if (c == 't') {
                  if (_inputPtr >= _inputEnd) {
                    loadMoreGuaranteed();
                  }
                  c = _inputBuffer[_inputPtr++];
                  if (c == ';') {
                    return INT_QUOTE;
                  }
                  start = "quot";
                } else {
                  start = "quo";
                }
              } else {
                start = "qu";
              }
            } else {
              start = "q";
            }
          } else {
            start = "";
          }
        }
      }
    }
    final int[] TYPES = sCharTypes.NAME_CHARS;
    char[] cbuf = _nameBuffer;
    int cix = 0;
    for (int len = start.length(); cix < len; ++cix) {
      cbuf[cix] = start.charAt(cix);
    }
    while (c != ';') {
      boolean ok;
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_NAME_NONE:
          case XmlCharTypes.CT_NAME_COLON:
          case XmlCharTypes.CT_NAME_NONFIRST:
          ok = (cix > 0);
          break;
          case XmlCharTypes.CT_NAME_ANY:
          ok = true;
          break;
          default:
          ok = false;
          break;
        }
      } else {
        if (c < 0xE000) {
          int value = decodeSurrogate(c);
          if (cix >= cbuf.length) {
            _nameBuffer = cbuf = DataUtil.growArrayBy(cbuf, cbuf.length);
          }
          cbuf[cix++] = c;
          c = _inputBuffer[_inputPtr - 1];
          ok = (cix == 0) ? XmlChars.is10NameStartChar(value) : XmlChars.is10NameChar(value);
        } else {
          if (c >= 0xFFFE) {
            c = handleInvalidXmlChar(c);
            ok = false;
          } else {
            ok = true;
          }
        }
      }
      if (!ok) {
        reportInvalidNameChar(c, cix);
      }
      if (cix >= cbuf.length) {
        _nameBuffer = cbuf = DataUtil.growArrayBy(cbuf, cbuf.length);
      }
      cbuf[cix++] = c;
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr++];
    }
    String pname = new String(cbuf, 0, cix);
    _tokenName = new PNameC(pname, null, pname, 0);
    if (_config.willExpandEntities()) {
      reportInputProblem("General entity reference (&" + pname + ";) encountered in entity expanding mode: operation not (yet) implemented");
    }
    if (inAttr) {
      reportInputProblem("General entity reference (&" + pname + ";) encountered in attribute value, in non-entity-expanding mode: no way to handle it");
    }
    return 0;
  }

  @Override protected final void finishComment() throws XMLStreamException {
    final int[] TYPES = sCharTypes.OTHER_CHARS;
    final char[] inputBuffer = _inputBuffer;
    char[] outputBuffer = _textBuilder.resetWithEmpty();
    int outPtr = 0;
    main_loop:
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
        }
        if (outPtr >= outputBuffer.length) {
          outputBuffer = _textBuilder.finishCurrentSegment();
          outPtr = 0;
        }
        int max = _inputEnd;
        {
          int max2 = ptr + (outputBuffer.length - outPtr);
          if (max2 < max) {
            max = max2;
          }
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
          outputBuffer[outPtr++] = c;
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          }
          c = '\n';
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_HYPHEN:
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          if (_inputBuffer[_inputPtr] == '-') {
            ++_inputPtr;
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (_inputBuffer[_inputPtr++] != '>') {
              reportDoubleHyphenInComments();
            }
            break main_loop;
          }
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            char d = checkSurrogate(c);
            outputBuffer[outPtr++] = c;
            if (outPtr >= outputBuffer.length) {
              outputBuffer = _textBuilder.finishCurrentSegment();
              outPtr = 0;
            }
            c = d;
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
      outputBuffer[outPtr++] = c;
    }
    _textBuilder.setCurrentLength(outPtr);
  }

  @Override protected final void finishPI() throws XMLStreamException {
    final int[] TYPES = sCharTypes.OTHER_CHARS;
    final char[] inputBuffer = _inputBuffer;
    char[] outputBuffer = _textBuilder.resetWithEmpty();
    int outPtr = 0;
    main_loop:
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
        }
        if (outPtr >= outputBuffer.length) {
          outputBuffer = _textBuilder.finishCurrentSegment();
          outPtr = 0;
        }
        int max = _inputEnd;
        {
          int max2 = ptr + (outputBuffer.length - outPtr);
          if (max2 < max) {
            max = max2;
          }
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
          outputBuffer[outPtr++] = c;
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (inputBuffer[_inputPtr] == CHAR_LF) {
              ++_inputPtr;
            }
            markLF();
            c = '\n';
          }
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_QMARK:
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          if (_inputBuffer[_inputPtr] == '>') {
            ++_inputPtr;
            break main_loop;
          }
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            char d = checkSurrogate(c);
            outputBuffer[outPtr++] = c;
            if (outPtr >= outputBuffer.length) {
              outputBuffer = _textBuilder.finishCurrentSegment();
              outPtr = 0;
            }
            c = d;
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
      outputBuffer[outPtr++] = c;
    }
    _textBuilder.setCurrentLength(outPtr);
  }

  @Override protected final void finishDTD(boolean copyContents) throws XMLStreamException {
    char[] outputBuffer = copyContents ? _textBuilder.resetWithEmpty() : null;
    int outPtr = 0;
    final int[] TYPES = sCharTypes.DTD_CHARS;
    boolean inDecl = false;
    int quoteChar = 0;
    main_loop:
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
        }
        int max = _inputEnd;
        if (outputBuffer != null) {
          if (outPtr >= outputBuffer.length) {
            outputBuffer = _textBuilder.finishCurrentSegment();
            outPtr = 0;
          }
          {
            int max2 = ptr + (outputBuffer.length - outPtr);
            if (max2 < max) {
              max = max2;
            }
          }
        }
        while (ptr < max) {
          c = _inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
          if (outputBuffer != null) {
            outputBuffer[outPtr++] = c;
          }
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (_inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          }
          c = '\n';
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_DTD_QUOTE:
          if (quoteChar == 0) {
            quoteChar = c;
          } else {
            if (quoteChar == c) {
              quoteChar = 0;
            }
          }
          break;
          case XmlCharTypes.CT_DTD_LT:
          if (!inDecl) {
            inDecl = true;
          }
          break;
          case XmlCharTypes.CT_DTD_GT:
          if (quoteChar == 0) {
            inDecl = false;
          }
          break;
          case XmlCharTypes.CT_DTD_RBRACKET:
          if (!inDecl && quoteChar == 0) {
            break main_loop;
          }
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            char d = checkSurrogate(c);
            if (outputBuffer != null) {
              outputBuffer[outPtr++] = c;
              if (outPtr >= outputBuffer.length) {
                outputBuffer = _textBuilder.finishCurrentSegment();
                outPtr = 0;
              }
            }
            c = d;
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
      if (outputBuffer != null) {
        outputBuffer[outPtr++] = c;
      }
    }
    if (outputBuffer != null) {
      _textBuilder.setCurrentLength(outPtr);
    }
    char c = skipInternalWs(false, null);
    if (c != '>') {
      throwUnexpectedChar(c, " expected \'>\' after the internal subset");
    }
  }

  @Override protected final void finishCData() throws XMLStreamException {
    final int[] TYPES = sCharTypes.OTHER_CHARS;
    final char[] inputBuffer = _inputBuffer;
    char[] outputBuffer = _textBuilder.resetWithEmpty();
    int outPtr = 0;
    main_loop:
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
        }
        if (outPtr >= outputBuffer.length) {
          outputBuffer = _textBuilder.finishCurrentSegment();
          outPtr = 0;
        }
        int max = _inputEnd;
        {
          int max2 = ptr + (outputBuffer.length - outPtr);
          if (max2 < max) {
            max = max2;
          }
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
          outputBuffer[outPtr++] = c;
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          }
          c = '\n';
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_RBRACKET:
          int count = 0;
          char d;
          do {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            d = _inputBuffer[_inputPtr];
            if (d != ']') {
              break;
            }
            ++_inputPtr;
            ++count;
          } while(true);
          boolean ok = (d == '>' && count >= 1);
          if (ok) {
            --count;
          }
          for ( ; count > 0; --count) {
            outputBuffer[outPtr++] = ']';
            if (outPtr >= outputBuffer.length) {
              outputBuffer = _textBuilder.finishCurrentSegment();
              outPtr = 0;
            }
          }
          if (ok) {
            ++_inputPtr;
            break main_loop;
          }
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            char d = checkSurrogate(c);
            outputBuffer[outPtr++] = c;
            if (outPtr >= outputBuffer.length) {
              outputBuffer = _textBuilder.finishCurrentSegment();
              outPtr = 0;
            }
            c = d;
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
      outputBuffer[outPtr++] = c;
    }
    _textBuilder.setCurrentLength(outPtr);
    if (_cfgCoalescing && !_entityPending) {
      finishCoalescedText();
    }
  }

  @Override protected final void finishCharacters() throws XMLStreamException {
    int outPtr;
    char[] outputBuffer;
    {
      int c = mTmpChar;
      if (c < 0) {
        c = -c;
        outputBuffer = _textBuilder.resetWithEmpty();
        outPtr = 0;
        if ((c >> 16) != 0) {
          c -= 0x10000;
          outputBuffer[outPtr++] = (char) (0xD800 | (c >> 10));
          c = 0xDC00 | (c & 0x3FF);
        }
        outputBuffer[outPtr++] = (char) c;
      } else {
        if (c == INT_CR || c == INT_LF) {
          ++_inputPtr;
          outPtr = checkInTreeIndentation((char) c);
          if (outPtr < 0) {
            return;
          }
          outputBuffer = _textBuilder.getBufferWithoutReset();
        } else {
          outputBuffer = _textBuilder.resetWithEmpty();
          outPtr = 0;
        }
      }
    }
    final int[] TYPES = sCharTypes.TEXT_CHARS;
    final char[] inputBuffer = _inputBuffer;
    main_loop:
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
        }
        if (outPtr >= outputBuffer.length) {
          outputBuffer = _textBuilder.finishCurrentSegment();
          outPtr = 0;
        }
        int max = _inputEnd;
        {
          int max2 = ptr + (outputBuffer.length - outPtr);
          if (max2 < max) {
            max = max2;
          }
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
          outputBuffer[outPtr++] = c;
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            int ptr = _inputPtr;
            if (ptr >= _inputEnd) {
              loadMoreGuaranteed();
              ptr = _inputPtr;
            }
            if (inputBuffer[ptr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          }
          c = '\n';
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_LT:
          --_inputPtr;
          break main_loop;
          case XmlCharTypes.CT_AMP:
          {
            int d = handleEntityInText(false);
            if (d == 0) {
              _entityPending = true;
              break main_loop;
            }
            if ((d >> 16) != 0) {
              d -= 0x10000;
              outputBuffer[outPtr++] = (char) (0xD800 | (d >> 10));
              if (outPtr >= outputBuffer.length) {
                outputBuffer = _textBuilder.finishCurrentSegment();
                outPtr = 0;
              }
              d = (0xDC00 | (d & 0x3FF));
            }
            c = (char) d;
          }
          break;
          case XmlCharTypes.CT_RBRACKET:
          {
            int count = 1;
            while (true) {
              if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
              }
              c = inputBuffer[_inputPtr];
              if (c != ']') {
                break;
              }
              ++_inputPtr;
              ++count;
            }
            if (c == '>' && count > 1) {
              reportIllegalCDataEnd();
            }
            while (count > 1) {
              outputBuffer[outPtr++] = ']';
              if (outPtr >= outputBuffer.length) {
                outputBuffer = _textBuilder.finishCurrentSegment();
                outPtr = 0;
              }
              --count;
            }
          }
          c = ']';
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            char d = checkSurrogate(c);
            outputBuffer[outPtr++] = c;
            if (outPtr >= outputBuffer.length) {
              outputBuffer = _textBuilder.finishCurrentSegment();
              outPtr = 0;
            }
            c = d;
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
      outputBuffer[outPtr++] = c;
    }
    _textBuilder.setCurrentLength(outPtr);
    if (_cfgCoalescing && !_entityPending) {
      finishCoalescedText();
    }
  }

  @Override protected final void finishSpace() throws XMLStreamException {
    char tmp = (char) mTmpChar;
    char[] outputBuffer;
    int outPtr;
    if (tmp == '\r' || tmp == '\n') {
      outPtr = checkPrologIndentation(tmp);
      if (outPtr < 0) {
        return;
      }
      outputBuffer = _textBuilder.getBufferWithoutReset();
    } else {
      outputBuffer = _textBuilder.resetWithEmpty();
      outputBuffer[0] = tmp;
      outPtr = 1;
    }
    int ptr = _inputPtr;
    while (true) {
      if (ptr >= _inputEnd) {
        if (!loadMore()) {
          break;
        }
        ptr = _inputPtr;
      }
      char c = _inputBuffer[ptr];
      if (c > INT_SPACE) {
        break;
      }
      ++ptr;
      if (c == INT_LF) {
        markLF(ptr);
      } else {
        if (c == INT_CR) {
          if (ptr >= _inputEnd) {
            if (!loadMore()) {
              if (outPtr >= outputBuffer.length) {
                outputBuffer = _textBuilder.finishCurrentSegment();
                outPtr = 0;
              }
              outputBuffer[outPtr++] = '\n';
              break;
            }
            ptr = _inputPtr;
          }
          if (_inputBuffer[ptr] == '\n') {
            ++ptr;
          }
          markLF(ptr);
          c = '\n';
        } else {
          if (c != ' ' && c != '\t') {
            _inputPtr = ptr;
            throwInvalidSpace(c);
          }
        }
      }
      if (outPtr >= outputBuffer.length) {
        outputBuffer = _textBuilder.finishCurrentSegment();
        outPtr = 0;
      }
      outputBuffer[outPtr++] = c;
    }
    _inputPtr = ptr;
    _textBuilder.setCurrentLength(outPtr);
  }

  /**
     * Method that gets called after a primary text segment (of type
     * CHARACTERS or CDATA, not applicable to SPACE) has been read in
     * text buffer. Method has to see if the following event would
     * be textual as well, and if so, read it (and any other following
     * textual segments).
     */
  protected final void finishCoalescedText() throws XMLStreamException {
    while (true) {
      if (_inputPtr >= _inputEnd) {
        if (!loadMore()) {
          return;
        }
      }
      if (_inputBuffer[_inputPtr] == '<') {
        if ((_inputPtr + 3) >= _inputEnd) {
          if (!loadAndRetain(3)) {
            return;
          }
        }
        if (_inputBuffer[_inputPtr + 1] != '!' || _inputBuffer[_inputPtr + 2] != '[') {
          return;
        }
        _inputPtr += 3;
        for (int i = 0; i < 6; ++i) {
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          char c = _inputBuffer[_inputPtr++];
          if (c != CDATA_STR.charAt(i)) {
            reportTreeUnexpChar(c, " (expected \'" + CDATA_STR.charAt(i) + "\' for CDATA section)");
          }
        }
        finishCoalescedCData();
      } else {
        finishCoalescedCharacters();
        if (_entityPending) {
          break;
        }
      }
    }
  }

  protected final void finishCoalescedCData() throws XMLStreamException {
    final int[] TYPES = sCharTypes.OTHER_CHARS;
    final char[] inputBuffer = _inputBuffer;
    char[] outputBuffer = _textBuilder.getBufferWithoutReset();
    int outPtr = _textBuilder.getCurrentLength();
    main_loop:
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
        }
        if (outPtr >= outputBuffer.length) {
          outputBuffer = _textBuilder.finishCurrentSegment();
          outPtr = 0;
        }
        int max = _inputEnd;
        {
          int max2 = ptr + (outputBuffer.length - outPtr);
          if (max2 < max) {
            max = max2;
          }
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
          outputBuffer[outPtr++] = c;
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          }
          c = '\n';
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_RBRACKET:
          int count = 0;
          char d;
          do {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            d = _inputBuffer[_inputPtr];
            if (d != ']') {
              break;
            }
            ++_inputPtr;
            ++count;
          } while(true);
          boolean ok = (d == '>' && count >= 1);
          if (ok) {
            --count;
          }
          for ( ; count > 0; --count) {
            outputBuffer[outPtr++] = ']';
            if (outPtr >= outputBuffer.length) {
              outputBuffer = _textBuilder.finishCurrentSegment();
              outPtr = 0;
            }
          }
          if (ok) {
            ++_inputPtr;
            break main_loop;
          }
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            char d = checkSurrogate(c);
            outputBuffer[outPtr++] = c;
            if (outPtr >= outputBuffer.length) {
              outputBuffer = _textBuilder.finishCurrentSegment();
              outPtr = 0;
            }
            c = d;
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
      outputBuffer[outPtr++] = c;
    }
    _textBuilder.setCurrentLength(outPtr);
  }

  protected final void finishCoalescedCharacters() throws XMLStreamException {
    final int[] TYPES = sCharTypes.TEXT_CHARS;
    final char[] inputBuffer = _inputBuffer;
    char[] outputBuffer = _textBuilder.getBufferWithoutReset();
    int outPtr = _textBuilder.getCurrentLength();
    main_loop:
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        if (ptr >= _inputEnd) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
        }
        if (outPtr >= outputBuffer.length) {
          outputBuffer = _textBuilder.finishCurrentSegment();
          outPtr = 0;
        }
        int max = _inputEnd;
        {
          int max2 = ptr + (outputBuffer.length - outPtr);
          if (max2 < max) {
            max = max2;
          }
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
          outputBuffer[outPtr++] = c;
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            int ptr = _inputPtr;
            if (ptr >= _inputEnd) {
              loadMoreGuaranteed();
              ptr = _inputPtr;
            }
            if (inputBuffer[ptr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          }
          c = '\n';
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_LT:
          --_inputPtr;
          break main_loop;
          case XmlCharTypes.CT_AMP:
          {
            int d = handleEntityInText(false);
            if (d == 0) {
              _entityPending = true;
              break main_loop;
            }
            if ((d >> 16) != 0) {
              d -= 0x10000;
              outputBuffer[outPtr++] = (char) (0xD800 | (d >> 10));
              if (outPtr >= outputBuffer.length) {
                outputBuffer = _textBuilder.finishCurrentSegment();
                outPtr = 0;
              }
              d = (0xDC00 | (d & 0x3FF));
            }
            c = (char) d;
          }
          break;
          case XmlCharTypes.CT_RBRACKET:
          {
            int count = 1;
            while (true) {
              if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
              }
              c = inputBuffer[_inputPtr];
              if (c != ']') {
                break;
              }
              ++_inputPtr;
              ++count;
            }
            if (c == '>' && count > 1) {
              reportIllegalCDataEnd();
            }
            while (count > 1) {
              outputBuffer[outPtr++] = ']';
              if (outPtr >= outputBuffer.length) {
                outputBuffer = _textBuilder.finishCurrentSegment();
                outPtr = 0;
              }
              --count;
            }
          }
          c = ']';
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            char d = checkSurrogate(c);
            outputBuffer[outPtr++] = c;
            if (outPtr >= outputBuffer.length) {
              outputBuffer = _textBuilder.finishCurrentSegment();
              outPtr = 0;
            }
            c = d;
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
      outputBuffer[outPtr++] = c;
    }
    _textBuilder.setCurrentLength(outPtr);
  }

  /**
     * Method that gets called after a primary text segment (of type
     * CHARACTERS or CDATA, not applicable to SPACE) has been skipped.
     * Method has to see if the following event would
     * be textual as well, and if so, skip it (and any other following
     * textual segments).
     *
     * @return True if we encountered an unexpandable entity
     */
  @Override protected final boolean skipCoalescedText() throws XMLStreamException {
    while (true) {
      if (_inputPtr >= _inputEnd) {
        if (!loadMore()) {
          return false;
        }
      }
      if (_inputBuffer[_inputPtr] == '<') {
        if ((_inputPtr + 3) >= _inputEnd) {
          if (!loadAndRetain(3)) {
            return false;
          }
        }
        if (_inputBuffer[_inputPtr + 1] != '!' || _inputBuffer[_inputPtr + 2] != '[') {
          return false;
        }
        _inputPtr += 3;
        for (int i = 0; i < 6; ++i) {
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          char c = _inputBuffer[_inputPtr++];
          if (c != CDATA_STR.charAt(i)) {
            reportTreeUnexpChar(c, " (expected \'" + CDATA_STR.charAt(i) + "\' for CDATA section)");
          }
        }
        skipCData();
      } else {
        if (skipCharacters()) {
          return true;
        }
      }
    }
  }

  @Override protected final void skipComment() throws XMLStreamException {
    final int[] TYPES = sCharTypes.OTHER_CHARS;
    final char[] inputBuffer = _inputBuffer;
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        int max = _inputEnd;
        if (ptr >= max) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
          max = _inputEnd;
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          }
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_HYPHEN:
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          if (_inputBuffer[_inputPtr] == '-') {
            ++_inputPtr;
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (_inputBuffer[_inputPtr++] != '>') {
              reportDoubleHyphenInComments();
            }
            return;
          }
          break;
        }
      }
    }
  }

  @Override protected final void skipPI() throws XMLStreamException {
    final int[] TYPES = sCharTypes.OTHER_CHARS;
    final char[] inputBuffer = _inputBuffer;
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        int max = _inputEnd;
        if (ptr >= max) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
          max = _inputEnd;
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (inputBuffer[_inputPtr] == CHAR_LF) {
              ++_inputPtr;
            }
            markLF();
          }
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_QMARK:
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          if (_inputBuffer[_inputPtr] == '>') {
            ++_inputPtr;
            return;
          }
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            checkSurrogate(c);
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
    }
  }

  @Override protected final boolean skipCharacters() throws XMLStreamException {
    final int[] TYPES = sCharTypes.TEXT_CHARS;
    final char[] inputBuffer = _inputBuffer;
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        int max = _inputEnd;
        if (ptr >= max) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
          max = _inputEnd;
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (inputBuffer[_inputPtr] == CHAR_LF) {
              ++_inputPtr;
            }
            markLF();
          }
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_LT:
          --_inputPtr;
          return false;
          case XmlCharTypes.CT_AMP:
          {
            int d = handleEntityInText(false);
            if (d == 0) {
              return true;
            }
          }
          break;
          case XmlCharTypes.CT_RBRACKET:
          {
            int count = 1;
            while (true) {
              if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
              }
              c = inputBuffer[_inputPtr];
              if (c != ']') {
                break;
              }
              ++_inputPtr;
              ++count;
            }
            if (c == '>' && count > 1) {
              reportIllegalCDataEnd();
            }
          }
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            checkSurrogate(c);
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
    }
  }

  @Override protected final void skipCData() throws XMLStreamException {
    final int[] TYPES = sCharTypes.OTHER_CHARS;
    final char[] inputBuffer = _inputBuffer;
    while (true) {
      char c;
      ascii_loop:
      while (true) {
        int ptr = _inputPtr;
        int max = _inputEnd;
        if (ptr >= max) {
          loadMoreGuaranteed();
          ptr = _inputPtr;
          max = _inputEnd;
        }
        while (ptr < max) {
          c = inputBuffer[ptr++];
          if (c <= 0xFF) {
            if (TYPES[c] != 0) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          } else {
            if (c >= 0xD800) {
              _inputPtr = ptr;
              break ascii_loop;
            }
          }
        }
        _inputPtr = ptr;
      }
      if (c <= 0xFF) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            int ptr = _inputPtr;
            if (ptr >= _inputEnd) {
              loadMoreGuaranteed();
              ptr = _inputPtr;
            }
            if (inputBuffer[ptr] == CHAR_LF) {
              ++ptr;
              ++_inputPtr;
            }
            markLF(ptr);
          }
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_RBRACKET:
          {
            int count = 0;
            do {
              if (_inputPtr >= _inputEnd) {
                loadMoreGuaranteed();
              }
              ++count;
              c = _inputBuffer[_inputPtr++];
            } while(c == ']');
            if (c == '>') {
              if (count > 1) {
                return;
              }
            } else {
              --_inputPtr;
            }
          }
          break;
        }
      } else {
        if (c >= 0xD800) {
          if (c < 0xE000) {
            checkSurrogate(c);
          } else {
            if (c >= 0xFFFE) {
              c = handleInvalidXmlChar(c);
            }
          }
        }
      }
    }
  }

  @Override protected final void skipSpace() throws XMLStreamException {
    int ptr = _inputPtr;
    while (true) {
      if (ptr >= _inputEnd) {
        if (!loadMore()) {
          break;
        }
        ptr = _inputPtr;
      }
      char c = _inputBuffer[ptr];
      if (c > ' ') {
        break;
      }
      ++ptr;
      if (c == '\n') {
        markLF(ptr);
      } else {
        if (c == '\r') {
          if (ptr >= _inputEnd) {
            if (!loadMore()) {
              break;
            }
            ptr = _inputPtr;
          }
          if (_inputBuffer[ptr] == '\n') {
            ++ptr;
          }
          markLF(ptr);
        } else {
          if (c != ' ' && c != '\t') {
            _inputPtr = ptr;
            throwInvalidSpace(c);
          }
        }
      }
    }
    _inputPtr = ptr;
  }

  /**
     * @return First byte following skipped white space
     */
  protected char skipInternalWs(boolean reqd, String msg) throws XMLStreamException {
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    char c = _inputBuffer[_inputPtr++];
    if (c > INT_SPACE) {
      if (!reqd) {
        return c;
      }
      reportTreeUnexpChar(c, " (expected white space " + msg + ")");
    }
    do {
      if (c == '\n') {
        markLF();
      } else {
        if (c == '\r') {
          if (_inputPtr >= _inputEnd) {
            loadMoreGuaranteed();
          }
          if (_inputBuffer[_inputPtr] == '\n') {
            ++_inputPtr;
          }
          markLF();
        } else {
          if (c != ' ' && c != '\t') {
            throwInvalidSpace(c);
          }
        }
      }
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr++];
    } while(c <= INT_SPACE);
    return c;
  }

  private final void matchAsciiKeyword(String keyw) throws XMLStreamException {
    for (int i = 1, len = keyw.length(); i < len; ++i) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      char c = _inputBuffer[_inputPtr++];
      if (c != keyw.charAt(i)) {
        reportTreeUnexpChar(c, " (expected \'" + keyw.charAt(i) + "\' for " + keyw + " keyword)");
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
  protected final int checkInTreeIndentation(char c) throws XMLStreamException {
    if (c == '\r') {
      if (_inputPtr >= _inputEnd && !loadMore()) {
        _textBuilder.resetWithIndentation(0, CHAR_SPACE);
        return -1;
      }
      if (_inputBuffer[_inputPtr] == '\n') {
        ++_inputPtr;
      }
    }
    markLF();
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    c = _inputBuffer[_inputPtr];
    if (c != ' ' && c != '\t') {
      if (c == '<') {
        if ((_inputPtr + 1) < _inputEnd && _inputBuffer[_inputPtr + 1] != '!') {
          _textBuilder.resetWithIndentation(0, ' ');
          return -1;
        }
      }
      char[] outputBuffer = _textBuilder.resetWithEmpty();
      outputBuffer[0] = '\n';
      _textBuilder.setCurrentLength(1);
      return 1;
    }
    ++_inputPtr;
    int count = 1;
    int max = (c == ' ') ? TextBuilder.MAX_INDENT_SPACES : TextBuilder.MAX_INDENT_TABS;
    while (count <= max) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      char c2 = _inputBuffer[_inputPtr];
      if (c2 != c) {
        if (c2 == '<' && (_inputPtr + 1) < _inputEnd && _inputBuffer[_inputPtr + 1] != '!') {
          _textBuilder.resetWithIndentation(count, c);
          return -1;
        }
        break;
      }
      ++_inputPtr;
      ++count;
    }
    char[] outputBuffer = _textBuilder.resetWithEmpty();
    outputBuffer[0] = '\n';
    for (int i = 1; i <= count; ++i) {
      outputBuffer[i] = c;
    }
    count += 1;
    _textBuilder.setCurrentLength(count);
    return count;
  }

  /**
     * @return -1, if indentation was handled; offset in the output
     *    buffer, if not
     */
  protected final int checkPrologIndentation(char c) throws XMLStreamException {
    if (c == '\r') {
      if (_inputPtr >= _inputEnd && !loadMore()) {
        _textBuilder.resetWithIndentation(0, CHAR_SPACE);
        return -1;
      }
      if (_inputBuffer[_inputPtr] == '\n') {
        ++_inputPtr;
      }
    }
    markLF();
    if (_inputPtr >= _inputEnd && !loadMore()) {
      _textBuilder.resetWithIndentation(0, CHAR_SPACE);
      return -1;
    }
    c = _inputBuffer[_inputPtr];
    if (c != ' ' && c != '\t') {
      if (c == '<') {
        _textBuilder.resetWithIndentation(0, CHAR_SPACE);
        return -1;
      }
      char[] outputBuffer = _textBuilder.resetWithEmpty();
      outputBuffer[0] = '\n';
      _textBuilder.setCurrentLength(1);
      return 1;
    }
    ++_inputPtr;
    int count = 1;
    int max = (c == ' ') ? TextBuilder.MAX_INDENT_SPACES : TextBuilder.MAX_INDENT_TABS;
    while (true) {
      if (_inputPtr >= _inputEnd && !loadMore()) {
        break;
      }
      if (_inputBuffer[_inputPtr] != c) {
        break;
      }
      ++_inputPtr;
      ++count;
      if (count >= max) {
        char[] outputBuffer = _textBuilder.resetWithEmpty();
        outputBuffer[0] = '\n';
        for (int i = 1; i <= count; ++i) {
          outputBuffer[i] = c;
        }
        count += 1;
        _textBuilder.setCurrentLength(count);
        return count;
      }
    }
    _textBuilder.resetWithIndentation(count, c);
    return -1;
  }

  protected PName parsePName(char c) throws XMLStreamException {
    char[] nameBuffer = _nameBuffer;
    if (c < INT_A) {
      throwUnexpectedChar(c, "; expected a name start character");
    }
    nameBuffer[0] = c;
    int hash = (int) c;
    int ptr = 1;
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      c = _inputBuffer[_inputPtr];
      int d = (int) c;
      if (d < 65) {
        if (d < 45 || d > 58 || d == 47) {
          PName n = _symbols.findSymbol(nameBuffer, 0, ptr, hash);
          if (n == null) {
            n = addPName(nameBuffer, ptr, hash);
          }
          return n;
        }
      }
      ++_inputPtr;
      if (ptr >= nameBuffer.length) {
        _nameBuffer = nameBuffer = DataUtil.growArrayBy(nameBuffer, nameBuffer.length);
      }
      nameBuffer[ptr++] = c;
      hash = (hash * 31) + d;
    }
  }

  protected final PName addPName(char[] nameBuffer, int nameLen, int hash) throws XMLStreamException {
    char c = nameBuffer[0];
    int namePtr = 1;
    int last_colon = -1;
    if (c < 0xD800 || c >= 0xE000) {
      if (!XmlChars.is10NameStartChar(c)) {
        reportInvalidNameChar(c, 0);
      }
    } else {
      if (nameLen == 1) {
        reportInvalidFirstSurrogate(c);
      }
      checkSurrogateNameChar(c, nameBuffer[1], 0);
      ++namePtr;
    }
    for ( ; namePtr < nameLen; ++namePtr) {
      c = nameBuffer[namePtr];
      if (c < 0xD800 || c >= 0xE000) {
        if (c == ':') {
          if (last_colon >= 0) {
            reportMultipleColonsInName();
          }
          last_colon = namePtr;
        } else {
          if (!XmlChars.is10NameChar(c)) {
            reportInvalidNameChar(c, namePtr);
          }
        }
      } else {
        if ((namePtr + 1) >= nameLen) {
          reportInvalidFirstSurrogate(c);
        }
        checkSurrogateNameChar(c, nameBuffer[namePtr + 1], namePtr);
      }
    }
    return _symbols.addSymbol(nameBuffer, 0, nameLen, hash);
  }

  protected String parsePublicId(char quoteChar) throws XMLStreamException {
    char[] outputBuffer = _nameBuffer;
    int outPtr = 0;
    final int[] TYPES = XmlCharTypes.PUBID_CHARS;
    boolean addSpace = false;
    main_loop:
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      char c = _inputBuffer[_inputPtr++];
      if (c == quoteChar) {
        break main_loop;
      }
      if ((c > 0xFF) || TYPES[c] != XmlCharTypes.PUBID_OK) {
        throwUnexpectedChar(c, " in public identifier");
      }
      if (c <= INT_SPACE) {
        addSpace = true;
        continue;
      }
      if (addSpace) {
        if (outPtr >= outputBuffer.length) {
          outputBuffer = _textBuilder.finishCurrentSegment();
          outPtr = 0;
        }
        outputBuffer[outPtr++] = ' ';
        addSpace = false;
      }
      if (outPtr >= outputBuffer.length) {
        _nameBuffer = outputBuffer = DataUtil.growArrayBy(outputBuffer, outputBuffer.length);
        outPtr = 0;
      }
      outputBuffer[outPtr++] = c;
    }
    return new String(outputBuffer, 0, outPtr);
  }

  protected String parseSystemId(char quoteChar) throws XMLStreamException {
    char[] outputBuffer = _nameBuffer;
    int outPtr = 0;
    final int[] TYPES = sCharTypes.ATTR_CHARS;
    main_loop:
    while (true) {
      if (_inputPtr >= _inputEnd) {
        loadMoreGuaranteed();
      }
      char c = _inputBuffer[_inputPtr++];
      if (TYPES[c] != 0) {
        switch (TYPES[c]) {
          case XmlCharTypes.CT_INVALID:
          c = handleInvalidXmlChar(c);
          case XmlCharTypes.CT_WS_CR:
          {
            if (_inputPtr >= _inputEnd) {
              loadMoreGuaranteed();
            }
            if (_inputBuffer[_inputPtr] == '\n') {
              ++_inputPtr;
            }
            markLF();
          }
          c = '\n';
          break;
          case XmlCharTypes.CT_WS_LF:
          markLF();
          break;
          case XmlCharTypes.CT_ATTR_QUOTE:
          if (c == quoteChar) {
            break main_loop;
          }
        }
      }
      if (outPtr >= outputBuffer.length) {
        _nameBuffer = outputBuffer = DataUtil.growArrayBy(outputBuffer, outputBuffer.length);
        outPtr = 0;
      }
      outputBuffer[outPtr++] = c;
    }
    return new String(outputBuffer, 0, outPtr);
  }

  /**
     * This method is called to verify that a surrogate
     * pair found describes a legal surrogate pair (ie. expands
     * to a legal XML char)
     */
  private char checkSurrogate(char firstChar) throws XMLStreamException {
    if (firstChar >= 0xDC00) {
      reportInvalidFirstSurrogate(firstChar);
    }
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    char sec = _inputBuffer[_inputPtr++];
    if (sec < 0xDC00 || sec >= 0xE000) {
      reportInvalidSecondSurrogate(sec);
    }
    int val = ((firstChar - 0xD800) << 10) + 0x10000;
    if (val > XmlConsts.MAX_UNICODE_CHAR) {
      reportInvalidXmlChar(val);
    }
    return sec;
  }

  private int checkSurrogateNameChar(char firstChar, char sec, int index) throws XMLStreamException {
    if (firstChar >= 0xDC00) {
      reportInvalidFirstSurrogate(firstChar);
    }
    if (sec < 0xDC00 || sec >= 0xE000) {
      reportInvalidSecondSurrogate(sec);
    }
    int val = ((firstChar - 0xD800) << 10) + 0x10000;
    if (val > XmlConsts.MAX_UNICODE_CHAR) {
      reportInvalidXmlChar(val);
    }
    if (true) {
      reportInvalidNameChar(val, index);
    }
    return val;
  }

  /**
     * This method is similar to <code>checkSurrogate</code>, but
     * returns the actual character code encoded by the surrogate
     * pair. This is needed if further validation rules (such as name
     * charactert checks) are to be done.
     */
  private int decodeSurrogate(char firstChar) throws XMLStreamException {
    if (firstChar >= 0xDC00) {
      reportInvalidFirstSurrogate(firstChar);
    }
    if (_inputPtr >= _inputEnd) {
      loadMoreGuaranteed();
    }
    char sec = _inputBuffer[_inputPtr++];
    if (sec < 0xDC00 || sec >= 0xE000) {
      reportInvalidSecondSurrogate(sec);
    }
    int val = ((firstChar - 0xD800) << 10) + 0x10000;
    if (val > XmlConsts.MAX_UNICODE_CHAR) {
      reportInvalidXmlChar(val);
    }
    return val;
  }

  private void reportInvalidFirstSurrogate(char ch) throws XMLStreamException {
    reportInputProblem("Invalid surrogate character (code 0x" + Integer.toHexString((int) ch) + "): can not start a surrogate pair");
  }

  private void reportInvalidSecondSurrogate(char ch) throws XMLStreamException {
    reportInputProblem("Invalid surrogate character (code " + Integer.toHexString((int) ch) + "): is not legal as the second part of a surrogate pair");
  }

  @Override public XMLStreamLocation2 getCurrentLocation() {
    return LocationImpl.fromZeroBased(_config.getPublicId(), _config.getSystemId(), _pastBytesOrChars + _inputPtr, _currRow, _inputPtr - _rowStartOffset);
  }

  @Override public int getCurrentColumnNr() {
    return _inputPtr - _rowStartOffset;
  }

  @Override public long getStartingByteOffset() {
    return -1L;
  }

  @Override public long getStartingCharOffset() {
    return _startRawOffset;
  }

  @Override public long getEndingByteOffset() throws XMLStreamException {
    return -1L;
  }

  @Override public long getEndingCharOffset() throws XMLStreamException {
    if (_tokenIncomplete) {
      finishToken();
    }
    return _pastBytesOrChars + _inputPtr;
  }

  protected final void markLF(int offset) {
    _rowStartOffset = offset;
    ++_currRow;
  }

  protected final void markLF() {
    _rowStartOffset = _inputPtr;
    ++_currRow;
  }

  protected final void setStartLocation() {
    _startRawOffset = _pastBytesOrChars + _inputPtr;
    _startRow = _currRow;
    _startColumn = _inputPtr - _rowStartOffset;
  }

  @Override protected final boolean loadMore() throws XMLStreamException {
    if (_in == null) {
      _inputEnd = 0;
      return false;
    }
    _pastBytesOrChars += _inputEnd;
    _rowStartOffset -= _inputEnd;
    _inputPtr = 0;
    try {
      int count = _in.read(_inputBuffer, 0, _inputBuffer.length);
      if (count < 1) {
        _inputEnd = 0;
        if (count == 0) {
          reportInputProblem("Reader returned 0 bytes, even when asked to read up to " + _inputBuffer.length);
        }
        return false;
      }
      _inputEnd = count;
      return true;
    } catch (IOException ioe) {
      throw new IoStreamException(ioe);
    }
  }

  protected final char loadOne() throws XMLStreamException {
    if (!loadMore()) {
      reportInputProblem("Unexpected end-of-input when trying to parse " + ErrorConsts.tokenTypeDesc(_currToken));
    }
    return _inputBuffer[_inputPtr++];
  }

  protected final char loadOne(int type) throws XMLStreamException {
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
            reportInputProblem("Reader returned 0 bytes, even when asked to read up to " + max);
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