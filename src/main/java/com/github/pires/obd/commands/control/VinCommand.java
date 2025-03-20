package com.github.pires.obd.commands.control;
import com.github.pires.obd.commands.PersistentCommand;
import com.github.pires.obd.enums.AvailableCommandNames;

/**
 * <p>VinCommand class.</p>
 *
 * @author pires
 * @version $Id: $Id
 */
public class VinCommand extends PersistentCommand {
  String vin = "";

  /**
     * Default ctor.
     */
  public VinCommand() {
    super("09 02");
  }

  /**
     * Copy ctor.
     *
     * @param other a {@link com.github.pires.obd.commands.control.VinCommand} object.
     */
  public VinCommand(VinCommand other) {
    super(other);
  }

  /** {@inheritDoc} */
  @Override protected void performCalculations() {
    final String result = getResult();
    String workingData;
    if (result.contains(":")) {
      workingData = result.replaceAll(".:", "").substring(9);
    } else {
      workingData = result.replaceAll("49020.", "");
    }
    String hexToString = convertHexToString(workingData);
    vin = hexToString.replaceAll("[\u0000-\u001f]", "");
  }

  /** {@inheritDoc} */
  @Override public String getFormattedResult() {
    return String.valueOf(vin);
  }

  /** {@inheritDoc} */
  @Override public String getName() {
    return AvailableCommandNames.VIN.getValue();
  }

  /** {@inheritDoc} */
  @Override public String getCalculatedResult() {
    return String.valueOf(vin);
  }

  /** {@inheritDoc} */
  @Override protected void fillBuffer() {
  }

  public String convertHexToString(String hex) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < hex.length() - 1; i += 2) {
      String output = hex.substring(i, (i + 2));
      int decimal = Integer.parseInt(output, 16);
      sb.append((char) decimal);
    }
    return sb.toString();
  }
}