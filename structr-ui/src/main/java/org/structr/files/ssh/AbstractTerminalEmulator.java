package org.structr.files.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 *
 * @author Christian Morgner
 */
public abstract class AbstractTerminalEmulator extends Thread implements TerminalEmulator {

	protected final StringBuilder lineBuffer      = new StringBuilder();
	protected TerminalHandler rootTerminalHandler = null;
	protected TerminalHandler terminalHandler     = null;
	protected Reader reader                       = null;
	protected Writer writer                       = null;
	protected boolean running                     = false;
	protected boolean echo                        = true;
	protected int cursorPosition                  = 0;
	protected int lineLength                      = 0;
	protected int commandBufferIndex              = 0;

	public AbstractTerminalEmulator(final InputStream in, final OutputStream out, final TerminalHandler rootTerminalHandler) {

		this.rootTerminalHandler = rootTerminalHandler;
		this.terminalHandler     = rootTerminalHandler;
		this.reader              = new InputStreamReader(in);
		this.writer              = new OutputStreamWriter(out);
	}

	@Override
	public void stopEmulator() {
		this.running = false;
	}

	@Override
	public void setTerminalHandler(final TerminalHandler handler) throws IOException {
		this.terminalHandler = handler;
	}

	@Override
	public void restoreRootTerminalHandler() throws IOException {

		this.terminalHandler = rootTerminalHandler;
		setEcho(true);
	}

	@Override
	public void handleCursorUp() throws IOException {

		final List<String> commandHistory = terminalHandler.getCommandHistory();
		if (commandHistory != null && echo) {

			final int commandBufferSize = commandHistory.size();

			if (commandBufferIndex >= 0 && commandBufferIndex < commandBufferSize) {

				displaySelectedCommand(commandHistory.get(commandBufferSize - commandBufferIndex - 1));

				if (commandBufferIndex < commandBufferSize - 1) {
					commandBufferIndex++;
				}
			}
		}
	}

	@Override
	public void handleCursorDown() throws IOException {

		final List<String> commandHistory = terminalHandler.getCommandHistory();
		if (commandHistory != null && echo) {

			if (commandBufferIndex > 0) {

				final int commandBufferSize = commandHistory.size();

				if (commandBufferIndex >= 0 && commandBufferIndex <= commandBufferSize) {

					commandBufferIndex--;
					displaySelectedCommand(commandHistory.get(commandBufferSize - commandBufferIndex - 1));
				}

			} else {

				displaySelectedCommand("");
			}
		}
	}

	@Override
	public void handleCtrlKey(final int key) throws IOException {

		// 0 is Ctrl-A, 1 is Ctrl-B, etc..
		switch (key) {

			case 3:
				terminalHandler.handleCtrlC();
				break;

			case 4:

				if (lineLength == 0) {
					terminalHandler.handleLogoutRequest();
				}
				break;
		}
	}

	@Override
	public void run() {

		running = true;

		while (running) {

			try {

				int c = reader.read();
				switch (c) {

					case 13:
						handleNewline();
						break;

					case 27:
						// escape sequence
						c = reader.read();
						switch (c) {

							case 91:
								// cursor keys
								c = reader.read();
								switch (c) {

									case 50:
										// insert
										c = reader.read();
										switch (c) {

											case 126:

												handleInsert();
												break;
										}
										break;

									case 51:
										// delete
										c = reader.read();
										switch (c) {

											case 126:

												handleDelete();
												break;
										}
										break;

									case 53:
										// page up
										c = reader.read();
										switch (c) {

											case 126:

												handlePageUp();
												break;
										}
										break;

									case 54:
										// page down
										c = reader.read();
										switch (c) {

											case 126:

												handlePageDown();
												break;
										}
										break;

									case 65:
										// up
										handleCursorUp();
										break;

									case 66:
										// down
										handleCursorDown();
										break;

									case 67:

										handleCursorRight();
										break;

									case 68:

										handleCursorLeft();
										break;

									case 70:

										handleEnd();
										break;

									case 72:

										handleHome();
										break;
								}
								break;

						}
						break;

					case 127:

						handleBackspace();
						break;

					default:

						if (c < 27) {

							handleCtrlKey(c);

						} else {

							// read unicode character
							handleCharacter(c);
						}
						break;
				}

				writer.flush();

			} catch (Throwable t) {
				t.printStackTrace();
			}
		}

		terminalHandler.handleExit();
	}

	@Override
	public void print(final String text) throws IOException {
		writer.write(text);
		writer.flush();
	}

	@Override
	public void println(final String text) throws IOException {
		writer.write(text);
		println();
		writer.flush();
	}

	@Override
	public void clearLineBuffer() {
		lineBuffer.setLength(0);
		cursorPosition = 0;
		lineLength = 0;
	}

	@Override
	public void setEcho(final boolean echo) {
		this.echo = echo;
	}

	// ----- protected methods -----
	protected void handleLineInternal(final String line) throws IOException {

		terminalHandler.handleLine(line);
		commandBufferIndex = 0;
	}

	// ----- private methods -----
	private void displaySelectedCommand(final String selectedCommand) throws IOException {

		lineBuffer.setLength(0);
		lineBuffer.append(selectedCommand);
		lineLength = lineBuffer.length();

		int loopCount = cursorPosition;
		for (int i=0; i<loopCount; i++) {
			handleCursorLeft();
		}

		writer.write(27);
		writer.write('[');
		writer.write('K');

		print(selectedCommand);

		cursorPosition = lineLength;
	}
}
