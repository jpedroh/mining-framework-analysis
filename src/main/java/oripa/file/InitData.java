package oripa.file;

public class InitData {
  private String lastUsedFile = "";

  private String[] MRUFiles = new String[0];

  private boolean zeroLineWidth = false;

  private boolean mvLineVisible = true;

  private boolean auxLineVisible = true;

  private boolean vertexVisible = true;

  public InitData() {
  }

  public void setMRUFiles(final String[] s) {
    MRUFiles = s;
  }

  public String[] getMRUFiles() {
    return MRUFiles;
  }

  public void setLastUsedFile(final String s) {
    lastUsedFile = s;
  }

  public String getLastUsedFile() {
    return lastUsedFile;
  }

  public void setZeroLineWidth(final boolean zeroLineWidth) {
    this.zeroLineWidth = zeroLineWidth;
  }

  public boolean isZeroLineWidth() {
    return zeroLineWidth;
  }

  /**
	 * @return mvLineVisible
	 */
  public boolean isMvLineVisible() {
    return mvLineVisible;
  }

  /**
	 * @param mvLineVisible
	 *            Sets mvLineVisible
	 */
  public void setMvLineVisible(final boolean mvLineVisible) {
    this.mvLineVisible = mvLineVisible;
  }

  /**
	 * @return auxLineVisible
	 */
  public boolean isAuxLineVisible() {
    return auxLineVisible;
  }

  /**
	 * @param auxLineVisible
	 *            Sets auxLineVisible
	 */
  public void setAuxLineVisible(final boolean auxLineVisible) {
    this.auxLineVisible = auxLineVisible;
  }

  /**
	 * @return vertexVisible
	 */
  public boolean isVertexVisible() {
    return vertexVisible;
  }

  /**
	 * @param vertexVisible
	 *            Sets vertexVisible
	 */
  public void setVertexVisible(final boolean vertexVisible) {
    this.vertexVisible = vertexVisible;
  }
}