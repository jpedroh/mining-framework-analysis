package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import java.io.IOException;
import javax.validation.constraints.NotNull;

/**
 * Github contents.
 *
 * @author Andres Candal (andres.candal@rollasolution.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/repos/contents/">Contents API</a>
 */
@Immutable public interface Contents {
  /**
     * Owner of them.
     * @return Repo
     */
  @NotNull(message = "repository is never NULL") Repo repo();

  /**
     * Get the Readme file of the default branch (usually master).
     *
     * @return The Content of the readme file.
     * @see <a href="http://http://developer.github.com/v3/repos/contents/#get-the-readme">Get the README</a>
     */
  @NotNull(message = "Content is never NULL") Content readme();

  /**
     * Create new file.
     * @param path The content path
     * @param message The commit message
     * @param content File content, Base64 encoded
     * @return Content just created
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/contents/#create-a-file">Create a file</a>
     */
  @NotNull(message = "Content is never NULL") Content create(@NotNull(message = "path is never NULL") String path, @NotNull(message = "message is never NULL") String message, @NotNull(message = "content is never NULL") String content) throws IOException;

  /**
     * Removes a file.
     * @param path The content path
     * @param message The commit message
     * @param sha Blob SHA of file to be deleted
     * @return Commit referring to this operation
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/repos/contents/#delete-a-file">Delete a file</a>
     */
  @NotNull(message = "Content is never NULL") Commit remove(@NotNull(message = "path is never NULL") String path, @NotNull(message = "message is never NULL") String message, @NotNull(message = "sha is never NULL") String sha) throws IOException;
}