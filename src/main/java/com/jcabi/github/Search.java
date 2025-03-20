package com.jcabi.github;
import com.jcabi.aspects.Immutable;
import java.io.IOException;
import java.util.EnumMap;
import javax.validation.constraints.NotNull;

/**
 * Github search.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @since 0.8
 * @see <a href="http://developer.github.com/v3/search/">Search API</a>
 */
@Immutable @SuppressWarnings(value = { "PMD.AvoidDuplicateLiterals" }) public interface Search {
  /**
     * Github we're in.
     *
     * @return Github
     */
  @NotNull(message = "Github is never NULL") Github github();

  /**
     * Search repositories.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Repos
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/search/#search-repositories">Search repositories</a>
     */
  @NotNull(message = "Iterable of repos is never NULL") Iterable<Repo> repos(@NotNull(message = "Search keywords can\'t be NULL") String keywords, @NotNull(message = "Sort field can\'t be NULL") String sort, @NotNull(message = "Sort order can\'t be NULL") Order order) throws IOException;

  /**
     * Search issues.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @param qualifiers The search qualifier
     * @return Issues
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/search/#search-issues">Search issues</a>
     * @checkstyle ParameterNumberCheck (7 lines)
     */
  @NotNull(message = "Iterable of issues is never NULL") Iterable<Issue> issues(@NotNull(message = "Search keywords can\'t be NULL") String keywords, @NotNull(message = "Sort field can\'t be NULL") String sort, @NotNull(message = "Sort order can\'t be NULL") Order order, @NotNull(message = "Search qualifiers can\'t be NULL") EnumMap<Qualifier, String> qualifiers) throws IOException;

  /**
     * Search users.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Users
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/search/#search-users">Search users</a>
     */
  @NotNull(message = "Iterable of users is never NULL") Iterable<User> users(@NotNull(message = "Search keywords can\'t be NULL") String keywords, @NotNull(message = "Sort field can\'t be NULL") String sort, @NotNull(message = "Sort order can\'t be NULL") Order order) throws IOException;

  /**
     * Search code.
     *
     * @param keywords The search keywords
     * @param sort The sort field
     * @param order The sort order
     * @return Contents
     * @throws IOException If there is any I/O problem
     * @see <a href="http://developer.github.com/v3/search/#search-code">Search code</a>
     */
  @NotNull(message = "Iterable of users is never NULL") Iterable<Content> codes(@NotNull(message = "Search keywords can\'t be NULL") String keywords, @NotNull(message = "Sort field can\'t be NULL") String sort, @NotNull(message = "Sort order can\'t be NULL") Order order) throws IOException;

  enum Qualifier implements StringEnum {
    TYPE("type"),
    IN("in"),
    AUTHOR("author"),
    ASSIGNEE("assignee"),
    MENTIONS("mentions"),
    COMMENTER("commenter"),
    INVOLVES("involves"),
    TEAM("team"),
    STATE("state"),
    LABEL("label"),
    NO("no"),
    LANGUAGE("language"),
    IS("is"),
    CREATED("created"),
    UPDATED("updated"),
    MERGED("merged"),
    CLOSED("closed"),
    COMMENTS("comments"),
    USER("user"),
    REPO("repo")
    ;

    /**
         * Search qualifier.
         */
    private final transient String qualifier;

    /**
         * Ctor.
         * @param key Search qualifier
         */
    Qualifier(final String key) {
      this.qualifier = key;
    }

    /**
         * Get search qualifier.
         * @return String
         */
    @Override @NotNull(message = "identifier string is never NULL") public String identifier() {
      return this.qualifier;
    }
  }

  enum Order implements StringEnum {
    ASC("asc"),
    DESC("desc")
    ;

    /**
         * The sort order.
         */
    private final transient String order;

    /**
         * Ctor.
         * @param key The sort order
         */
    Order(final String key) {
      this.order = key;
    }

    /**
         * Get sort order.
         * @return String
         */
    @Override @NotNull(message = "identifier string is never NULL") public String identifier() {
      return this.order;
    }
  }
}