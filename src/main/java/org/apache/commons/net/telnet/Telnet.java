package org.apache.commons.net.telnet;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.commons.net.SocketClient;

class Telnet extends SocketClient {
  static final boolean debug = false;

  static final boolean debugoptions = false;

  static final byte[] COMMAND_DO = { (byte) TelnetCommand.IAC, (byte) TelnetCommand.DO };

  static final byte[] COMMAND_DONT = { (byte) TelnetCommand.IAC, (byte) TelnetCommand.DONT };

  static final byte[] COMMAND_WILL = { (byte) TelnetCommand.IAC, (byte) TelnetCommand.WILL };

  static final byte[] COMMAND_WONT = { (byte) TelnetCommand.IAC, (byte) TelnetCommand.WONT };

  static final byte[] COMMAND_SB = { (byte) TelnetCommand.IAC, (byte) TelnetCommand.SB };

  static final byte[] COMMAND_SE = { (byte) TelnetCommand.IAC, (byte) TelnetCommand.SE };

  static final int WILL_MASK = 0x01;

  static final int DO_MASK = 0x02;

  static final int REQUESTED_WILL_MASK = 0x04;

  static final int REQUESTED_DO_MASK = 0x08;

  static final int DEFAULT_PORT = 23;

  /**
     * Terminal type option
     */
  protected static final int TERMINAL_TYPE = 24;

  /**
     * Send (for subnegotiation)
     */
  protected static final int TERMINAL_TYPE_SEND = 1;

  /**
     * Is (for subnegotiation)
     */
  protected static final int TERMINAL_TYPE_IS = 0;

  /**
     * Is sequence (for subnegotiation)
     */
  static final byte[] COMMAND_IS = { (byte) TERMINAL_TYPE, (byte) TERMINAL_TYPE_IS };

  /**
     * AYT sequence
     */
  static final byte[] COMMAND_AYT = { (byte) TelnetCommand.IAC, (byte) TelnetCommand.AYT };

  private final int[] doResponse;

  private final int[] willResponse;

  private final int[] options;

  /**
     * Terminal type
     */
  private String terminalType;

  /**
     * Array of option handlers
     */
  private final TelnetOptionHandler[] optionHandlers;

  /**
     * monitor to wait for AYT
     */
  private final Object aytMonitor = new Object();

  /**
     * flag for AYT
     */
  private volatile boolean aytFlag = true;

  /**
     * The stream on which to spy
     */
  private volatile OutputStream spyStream;

  /**
     * The notification handler
     */
  private TelnetNotificationHandler notifhand;

  /**
     * Empty Constructor
     */
  Telnet() {
    setDefaultPort(DEFAULT_PORT);
    doResponse = new int[TelnetOption.MAX_OPTION_VALUE + 1];
    willResponse = new int[TelnetOption.MAX_OPTION_VALUE + 1];
    options = new int[TelnetOption.MAX_OPTION_VALUE + 1];
    optionHandlers = new TelnetOptionHandler[TelnetOption.MAX_OPTION_VALUE + 1];
  }

  /**
     * This constructor lets you specify the terminal type.
     *
     * @param termtype - terminal type to be negotiated (ej. VT100)
     */
  Telnet(final String termtype) {
    setDefaultPort(DEFAULT_PORT);
    doResponse = new int[TelnetOption.MAX_OPTION_VALUE + 1];
    willResponse = new int[TelnetOption.MAX_OPTION_VALUE + 1];
    options = new int[TelnetOption.MAX_OPTION_VALUE + 1];
    terminalType = termtype;
    optionHandlers = new TelnetOptionHandler[TelnetOption.MAX_OPTION_VALUE + 1];
  }

  /**
     * Called upon connection.
     *
     * @throws IOException - Exception in I/O.
     */
  @Override protected void _connectAction_() throws IOException {
    for (int ii = 0; ii < TelnetOption.MAX_OPTION_VALUE + 1; ii++) {
      doResponse[ii] = 0;
      willResponse[ii] = 0;
      options[ii] = 0;
      if (optionHandlers[ii] != null) {
        optionHandlers[ii].setDo(false);
        optionHandlers[ii].setWill(false);
      }
    }
    super._connectAction_();
    _input_ = new BufferedInputStream(_input_);
    _output_ = new BufferedOutputStream(_output_);
    for (int ii = 0; ii < TelnetOption.MAX_OPTION_VALUE + 1; ii++) {
      if (optionHandlers[ii] != null) {
        if (optionHandlers[ii].getInitLocal()) {
          requestWill(optionHandlers[ii].getOptionCode());
        }
        if (optionHandlers[ii].getInitRemote()) {
          requestDo(optionHandlers[ii].getOptionCode());
        }
      }
    }
  }

  /**
     * Registers an OutputStream for spying what's going on in
     * the Telnet session.
     *
     * @param spystream - OutputStream on which session activity
     * will be echoed.
     */
  void _registerSpyStream(final OutputStream spystream) {
    spyStream = spystream;
  }

  /**
     * Sends an Are You There sequence and waits for the result.
     *
     * @param timeout - Time to wait for a response (millis.)
     * @throws IOException - Exception in I/O.
     * @throws IllegalArgumentException - Illegal argument
     * @throws InterruptedException - Interrupted during wait.
     * @return true if AYT received a response, false otherwise
     **/
  final boolean _sendAYT(final long timeout) throws IOException, IllegalArgumentException, InterruptedException {
    boolean retValue = false;
    synchronized (aytMonitor) {
      synchronized (this) {
        aytFlag = false;
        _output_.write(COMMAND_AYT);
        _output_.flush();
      }
      aytMonitor.wait(timeout);
      if (!aytFlag) {
        aytFlag = true;
      } else {
        retValue = true;
      }
    }
    return retValue;
  }

  /**
     * Sends a command, automatically adds IAC prefix and flushes the output.
     *
     * @param cmd - command data to be sent
     * @throws IOException - Exception in I/O.
     * @since 3.0
     */
  final synchronized void _sendCommand(final byte cmd) throws IOException {
    _output_.write(TelnetCommand.IAC);
    _output_.write(cmd);
    _output_.flush();
  }

  /**
     * Manages subnegotiation for Terminal Type.
     *
     * @param subn - subnegotiation data to be sent
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void _sendSubnegotiation(final int[] subn) throws IOException {
    if (debug) {
      System.err.println("SEND SUBNEGOTIATION: ");
      if (subn != null) {
        System.err.println(Arrays.toString(subn));
      }
    }
    if (subn != null) {
      _output_.write(COMMAND_SB);
      for (final int element : subn) {
        final byte b = (byte) element;
        if (b == (byte) TelnetCommand.IAC) {
          _output_.write(b);
        }
        _output_.write(b);
      }
      _output_.write(COMMAND_SE);
      _output_.flush();
    }
  }

  /**
     * Stops spying this Telnet.
     *
     */
  void _stopSpyStream() {
    spyStream = null;
  }

  /**
     * Registers a new TelnetOptionHandler for this telnet  to use.
     *
     * @param opthand - option handler to be registered.
     * @throws InvalidTelnetOptionException - The option code is invalid.
     * @throws IOException on error
     **/
  void addOptionHandler(final TelnetOptionHandler opthand) throws InvalidTelnetOptionException, IOException {
    final int optcode = opthand.getOptionCode();
    if (!TelnetOption.isValidOption(optcode)) {
      throw new InvalidTelnetOptionException("Invalid Option Code", optcode);
    }
    if (optionHandlers[optcode] != null) {
      throw new InvalidTelnetOptionException("Already registered option", optcode);
    }
    optionHandlers[optcode] = opthand;
    if (isConnected()) {
      if (opthand.getInitLocal()) {
        requestWill(optcode);
      }
      if (opthand.getInitRemote()) {
        requestDo(optcode);
      }
    }
  }

  /**
     * Unregisters a  TelnetOptionHandler.
     *
     * @param optcode - Code of the option to be unregistered.
     * @throws InvalidTelnetOptionException - The option code is invalid.
     * @throws IOException on error
     **/
  void deleteOptionHandler(final int optcode) throws InvalidTelnetOptionException, IOException {
    if (!TelnetOption.isValidOption(optcode)) {
      throw new InvalidTelnetOptionException("Invalid Option Code", optcode);
    }
    if (optionHandlers[optcode] == null) {
      throw new InvalidTelnetOptionException("Unregistered option", optcode);
    }
    final TelnetOptionHandler opthand = optionHandlers[optcode];
    optionHandlers[optcode] = null;
    if (opthand.getWill()) {
      requestWont(optcode);
    }
    if (opthand.getDo()) {
      requestDont(optcode);
    }
  }

  /**
     * Processes the response of an AYT
     */
  final synchronized void processAYTResponse() {
    if (!aytFlag) {
      synchronized (aytMonitor) {
        aytFlag = true;
        aytMonitor.notifyAll();
      }
    }
  }

  /**
     * Processes a COMMAND.
     *
     * @param command - option code to be set.
     **/
  void processCommand(final int command) {
    if (debugoptions) {
      System.err.println("RECEIVED COMMAND: " + command);
    }
    if (notifhand != null) {
      notifhand.receivedNegotiation(TelnetNotificationHandler.RECEIVED_COMMAND, command);
    }
  }

  /**
     * Processes a DO request.
     *
     * @param option - option code to be set.
     * @throws IOException - Exception in I/O.
     **/
  void processDo(final int option) throws IOException {
    if (debugoptions) {
      System.err.println("RECEIVED DO: " + TelnetOption.getOption(option));
    }
    if (notifhand != null) {
      notifhand.receivedNegotiation(TelnetNotificationHandler.RECEIVED_DO, option);
    }
    boolean acceptNewState = false;
    if (optionHandlers[option] != null) {
      acceptNewState = optionHandlers[option].getAcceptLocal();
    } else {
      if (option == TERMINAL_TYPE && terminalType != null && !terminalType.isEmpty()) {
        acceptNewState = true;
      }
    }
    if (willResponse[option] > 0) {
      --willResponse[option];
      if (willResponse[option] > 0 && stateIsWill(option)) {
        --willResponse[option];
      }
    }
    if (willResponse[option] == 0) {
      if (requestedWont(option)) {
        switch (option) {
          default:
          break;
        }
        if (acceptNewState) {
          setWantWill(option);
          sendWill(option);
        } else {
          ++willResponse[option];
          sendWont(option);
        }
      } else {
        switch (option) {
          default:
          break;
        }
      }
    }
    setWill(option);
  }

  /**
     * Processes a DONT request.
     *
     * @param option - option code to be set.
     * @throws IOException - Exception in I/O.
     **/
  void processDont(final int option) throws IOException {
    if (debugoptions) {
      System.err.println("RECEIVED DONT: " + TelnetOption.getOption(option));
    }
    if (notifhand != null) {
      notifhand.receivedNegotiation(TelnetNotificationHandler.RECEIVED_DONT, option);
    }
    if (willResponse[option] > 0) {
      --willResponse[option];
      if (willResponse[option] > 0 && stateIsWont(option)) {
        --willResponse[option];
      }
    }
    if (willResponse[option] == 0 && requestedWill(option)) {
      switch (option) {
        default:
        break;
      }
      if (stateIsWill(option) || requestedWill(option)) {
        sendWont(option);
      }
      setWantWont(option);
    }
    setWont(option);
  }

  /**
     * Processes a suboption negotiation.
     *
     * @param suboption - subnegotiation data received
     * @param suboptionLength - length of data received
     * @throws IOException - Exception in I/O.
     **/
  void processSuboption(final int[] suboption, final int suboptionLength) throws IOException {
    if (debug) {
      System.err.println("PROCESS SUBOPTION.");
    }
    if (suboptionLength > 0) {
      if (optionHandlers[suboption[0]] != null) {
        final int[] responseSuboption = optionHandlers[suboption[0]].answerSubnegotiation(suboption, suboptionLength);
        _sendSubnegotiation(responseSuboption);
      } else {
        if (suboptionLength > 1) {
          if (debug) {
            for (int ii = 0; ii < suboptionLength; ii++) {
              System.err.println("SUB[" + ii + "]: " + suboption[ii]);
            }
          }
          if (suboption[0] == TERMINAL_TYPE && suboption[1] == TERMINAL_TYPE_SEND) {
            sendTerminalType();
          }
        }
      }
    }
  }

  /**
     * Processes a WILL request.
     *
     * @param option - option code to be set.
     * @throws IOException - Exception in I/O.
     **/
  void processWill(final int option) throws IOException {
    if (debugoptions) {
      System.err.println("RECEIVED WILL: " + TelnetOption.getOption(option));
    }
    if (notifhand != null) {
      notifhand.receivedNegotiation(TelnetNotificationHandler.RECEIVED_WILL, option);
    }
    boolean acceptNewState = false;
    if (optionHandlers[option] != null) {
      acceptNewState = optionHandlers[option].getAcceptRemote();
    }
    if (doResponse[option] > 0) {
      --doResponse[option];
      if (doResponse[option] > 0 && stateIsDo(option)) {
        --doResponse[option];
      }
    }
    if (doResponse[option] == 0 && requestedDont(option)) {
      switch (option) {
        default:
        break;
      }
      if (acceptNewState) {
        setWantDo(option);
        sendDo(option);
      } else {
        ++doResponse[option];
        sendDont(option);
      }
    }
    setDo(option);
  }

  /**
     * Processes a WONT request.
     *
     * @param option - option code to be set.
     * @throws IOException - Exception in I/O.
     **/
  void processWont(final int option) throws IOException {
    if (debugoptions) {
      System.err.println("RECEIVED WONT: " + TelnetOption.getOption(option));
    }
    if (notifhand != null) {
      notifhand.receivedNegotiation(TelnetNotificationHandler.RECEIVED_WONT, option);
    }
    if (doResponse[option] > 0) {
      --doResponse[option];
      if (doResponse[option] > 0 && stateIsDont(option)) {
        --doResponse[option];
      }
    }
    if (doResponse[option] == 0 && requestedDo(option)) {
      switch (option) {
        default:
        break;
      }
      if (stateIsDo(option) || requestedDo(option)) {
        sendDont(option);
      }
      setWantDont(option);
    }
    setDont(option);
  }

  /**
     * Registers a notification handler to which will be sent
     * notifications of received telnet option negotiation commands.
     *
     * @param notifhand - TelnetNotificationHandler to be registered
     */
  public void registerNotifHandler(final TelnetNotificationHandler notifhand) {
    this.notifhand = notifhand;
  }

  /**
     * Requests a DO.
     *
     * @param option - Option code.
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void requestDo(final int option) throws IOException {
    if (doResponse[option] == 0 && stateIsDo(option) || requestedDo(option)) {
      return;
    }
    setWantDo(option);
    ++doResponse[option];
    sendDo(option);
  }

  /**
     * Requests a DONT.
     *
     * @param option - Option code.
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void requestDont(final int option) throws IOException {
    if (doResponse[option] == 0 && stateIsDont(option) || requestedDont(option)) {
      return;
    }
    setWantDont(option);
    ++doResponse[option];
    sendDont(option);
  }

  /**
     * Looks for the state of the option.
     *
     * @return returns true if a do has been reuqested
     *
     * @param option - option code to be looked up.
     */
  boolean requestedDo(final int option) {
    return (options[option] & REQUESTED_DO_MASK) != 0;
  }

  /**
     * Looks for the state of the option.
     *
     * @return returns true if a dont has been reuqested
     *
     * @param option - option code to be looked up.
     */
  boolean requestedDont(final int option) {
    return !requestedDo(option);
  }

  /**
     * Looks for the state of the option.
     *
     * @return returns true if a will has been reuqested
     *
     * @param option - option code to be looked up.
     */
  boolean requestedWill(final int option) {
    return (options[option] & REQUESTED_WILL_MASK) != 0;
  }

  /**
     * Looks for the state of the option.
     *
     * @return returns true if a wont has been reuqested
     *
     * @param option - option code to be looked up.
     */
  boolean requestedWont(final int option) {
    return !requestedWill(option);
  }

  /**
     * Requests a WILL.
     *
     * @param option - Option code.
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void requestWill(final int option) throws IOException {
    if (willResponse[option] == 0 && stateIsWill(option) || requestedWill(option)) {
      return;
    }
    setWantWill(option);
    ++doResponse[option];
    sendWill(option);
  }

  /**
     * Requests a WONT.
     *
     * @param option - Option code.
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void requestWont(final int option) throws IOException {
    if (willResponse[option] == 0 && stateIsWont(option) || requestedWont(option)) {
      return;
    }
    setWantWont(option);
    ++doResponse[option];
    sendWont(option);
  }

  /**
     * Sends a byte.
     *
     * @param b - byte to send
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void sendByte(final int b) throws IOException {
    _output_.write(b);
    spyWrite(b);
  }

  /**
     * Sends a DO.
     *
     * @param option - Option code.
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void sendDo(final int option) throws IOException {
    if (debug || debugoptions) {
      System.err.println("DO: " + TelnetOption.getOption(option));
    }
    _output_.write(COMMAND_DO);
    _output_.write(option);
    _output_.flush();
  }

  /**
     * Sends a DONT.
     *
     * @param option - Option code.
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void sendDont(final int option) throws IOException {
    if (debug || debugoptions) {
      System.err.println("DONT: " + TelnetOption.getOption(option));
    }
    _output_.write(COMMAND_DONT);
    _output_.write(option);
    _output_.flush();
  }

  /**
     * Sends terminal type information.
     *
     * @throws IOException - Exception in I/O.
     */
  final synchronized void sendTerminalType() throws IOException {
    if (debug) {
      System.err.println("SEND TERMINAL-TYPE: " + terminalType);
    }
    if (terminalType != null) {
      _output_.write(COMMAND_SB);
      _output_.write(COMMAND_IS);
      _output_.write(terminalType.getBytes(getCharset()));
      _output_.write(COMMAND_SE);
      _output_.flush();
    }
  }

  /**
     * Sends a WILL.
     *
     * @param option - Option code.
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void sendWill(final int option) throws IOException {
    if (debug || debugoptions) {
      System.err.println("WILL: " + TelnetOption.getOption(option));
    }
    _output_.write(COMMAND_WILL);
    _output_.write(option);
    _output_.flush();
  }

  /**
     * Sends a WONT.
     *
     * @param option - Option code.
     * @throws IOException - Exception in I/O.
     **/
  final synchronized void sendWont(final int option) throws IOException {
    if (debug || debugoptions) {
      System.err.println("WONT: " + TelnetOption.getOption(option));
    }
    _output_.write(COMMAND_WONT);
    _output_.write(option);
    _output_.flush();
  }

  /**
     * Sets the state of the option.
     *
     * @param option - option code to be set.
     * @throws IOException
     */
  void setDo(final int option) throws IOException {
    options[option] |= DO_MASK;
    if (requestedDo(option) && (optionHandlers[option] != null)) {
      optionHandlers[option].setDo(true);
      final int[] subneg = optionHandlers[option].startSubnegotiationRemote();
      if (subneg != null) {
        _sendSubnegotiation(subneg);
      }
    }
  }

  /**
     * Sets the state of the option.
     *
     * @param option - option code to be set.
     */
  void setDont(final int option) {
    options[option] &= ~DO_MASK;
    if (optionHandlers[option] != null) {
      optionHandlers[option].setDo(false);
    }
  }

  /**
     * Sets the state of the option.
     *
     * @param option - option code to be set.
     */
  void setWantDo(final int option) {
    options[option] |= REQUESTED_DO_MASK;
  }

  /**
     * Sets the state of the option.
     *
     * @param option - option code to be set.
     */
  void setWantDont(final int option) {
    options[option] &= ~REQUESTED_DO_MASK;
  }

  /**
     * Sets the state of the option.
     *
     * @param option - option code to be set.
     */
  void setWantWill(final int option) {
    options[option] |= REQUESTED_WILL_MASK;
  }

  /**
     * Sets the state of the option.
     *
     * @param option - option code to be set.
     */
  void setWantWont(final int option) {
    options[option] &= ~REQUESTED_WILL_MASK;
  }

  /**
     * Sets the state of the option.
     *
     * @param option - option code to be set.
     * @throws IOException
     */
  void setWill(final int option) throws IOException {
    options[option] |= WILL_MASK;
    if (requestedWill(option) && (optionHandlers[option] != null)) {
      optionHandlers[option].setWill(true);
      final int[] subneg = optionHandlers[option].startSubnegotiationLocal();
      if (subneg != null) {
        _sendSubnegotiation(subneg);
      }
    }
  }

  /**
     * Sets the state of the option.
     *
     * @param option - option code to be set.
     */
  void setWont(final int option) {
    options[option] &= ~WILL_MASK;
    if (optionHandlers[option] != null) {
      optionHandlers[option].setWill(false);
    }
  }

  /**
     * Sends a read char on the spy stream.
     *
     * @param ch - character read from the session
     */
  void spyRead(final int ch) {
    final OutputStream spy = spyStream;
    if (spy != null) {
      try {
        if (ch != '\r') {
          if (ch == '\n') {
            spy.write('\r');
          }
          spy.write(ch);
          spy.flush();
        }
      } catch (final IOException e) {
        spyStream = null;
      }
    }
  }

  /**
     * Sends a written char on the spy stream.
     *
     * @param ch - character written to the session
     */
  void spyWrite(final int ch) {
    if (!(stateIsDo(TelnetOption.ECHO) && requestedDo(TelnetOption.ECHO))) {
      final OutputStream spy = spyStream;
      if (spy != null) {
        try {
          spy.write(ch);
          spy.flush();
        } catch (final IOException e) {
          spyStream = null;
        }
      }
    }
  }

  /**
     * Looks for the state of the option.
     *
     * @return returns true if a do has been acknowledged
     *
     * @param option - option code to be looked up.
     */
  boolean stateIsDo(final int option) {
    return (options[option] & DO_MASK) != 0;
  }

  /**
     * Looks for the state of the option.
     *
     * @return returns true if a dont has been acknowledged
     *
     * @param option - option code to be looked up.
     */
  boolean stateIsDont(final int option) {
    return !stateIsDo(option);
  }

  /**
     * Looks for the state of the option.
     *
     * @return returns true if a will has been acknowledged
     *
     * @param option - option code to be looked up.
     */
  boolean stateIsWill(final int option) {
    return (options[option] & WILL_MASK) != 0;
  }

  /**
     * Looks for the state of the option.
     *
     * @return returns true if a wont has been acknowledged
     *
     * @param option - option code to be looked up.
     */
  boolean stateIsWont(final int option) {
    return !stateIsWill(option);
  }

  /**
     * Unregisters the current notification handler.
     *
     */
  public void unregisterNotifHandler() {
    this.notifhand = null;
  }
}