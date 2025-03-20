package oripa.file;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

/**
 * reads filePath into {@link oripa.file.InitData} object.
 *
 * @author OUCHI Koji
 *
 */
public class InitDataFileReader {
  /**
	 *
	 * @param filePath
	 *            init file location
	 * @return init data in filePath or default init data otherwise
	 */
  public InitData read(final String filePath) {
    InitData initData = new InitData();
    try (var fis = new FileInputStream(filePath); var bis = new BufferedInputStream(fis); var dec = new XMLDecoder(bis)) {
      initData = (InitData) dec.readObject();
    } catch (Exception e) {
    }
    return initData;
  }
}