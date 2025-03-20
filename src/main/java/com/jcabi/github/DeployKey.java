package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import java.io.IOException;

/**
 * Github deploy key.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/keys/">Deploy Keys API</a>
 * @todo #231 Deploy key object should be able to edit a deploy key. Let's
 *  create a test for for this method, declare it here, implement it in
 *  RtDeployKey and MkDeployKey, and add an integration test for it. See
 *  http://developer.github.com/v3/repos/keys/#edit. When done, remove this
 *  puzzle.
 * @todo #231 Deploy key object should be able to remove a deploy key. Let's
 *  create a test for for this method, declare it here, implement it in
 *  RtDeployKey and MkDeployKey, and add an integration test for it. See
 *  http://developer.github.com/v3/repos/keys/#delete. When done, remove this
 *  puzzle.
 * @todo #356:1hr There should be get() method implemented for reading the value
 *  of title and key. Implement method at RtDeployKey, MkDeployKey and write an
 *  integration test for it. See http://developer.github.com/v3/repos/keys/#get
 *  When done remove this puzzle and finish #356 (write test for edit method)
 */
@Immutable public interface DeployKey extends JsonReadable {
  /**
     * Get id of a deploy key.
     * @return Id
     */
  int number();

  /**
     * Edits a key.
     * @see <a href="http://developer.github.com/v3/repos/keys/#edit">Deploy keys API</a>
     * @param title New title
     * @param value New value
     * @throws IOException if any I/O problem occurs86
     */
  void edit(String title, String value) throws IOException;

  /**
     * Delete a deploy key.
     * @throws java.io.IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/keys/#delete">Remove a deploy key</a>
     */
  void remove() throws IOException;
}