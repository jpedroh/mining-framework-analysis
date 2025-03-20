package com.jcabi.github.mock;
import com.jcabi.aspects.Immutable;
import com.jcabi.aspects.Loggable;
import com.jcabi.github.DeployKey;
import com.jcabi.github.Repo;
import java.io.IOException;
import javax.json.JsonObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.xembly.Directives;

/**
 * Mock Github deploy key.
 * @author Alexander Sinyagin (sinyagin.alexander@gmail.com)
 * @version $Id$
 */
@Immutable @Loggable(value = Loggable.DEBUG) @ToString @EqualsAndHashCode(of = { "storage", "owner", "key" }) public final class MkDeployKey implements DeployKey {
  /**
     * Storage.
     */
  private final transient MkStorage storage;

  /**
     * Repository.
     */
  private final transient Repo owner;

  /**
     * Id.
     */
  private final transient int key;

  /**
     * Public ctor.
     * @param stg Storage
     * @param number Id
     * @param repo Repository
     */
  MkDeployKey(final MkStorage stg, final int number, final Repo repo) {
    this.storage = stg;
    this.key = number;
    this.owner = repo;
  }

  @Override public int number() {
    return this.key;
  }

  @Override public void edit(final String title, final String value) throws IOException {
    this.storage.apply(new Directives().xpath(this.xpath()).attr("key", value).attr("title", title));
  }

  @Override public JsonObject json() throws IOException {
    return new JsonNode(this.storage.xml().nodes(this.xpath()).get(0)).json();
  }

  @Override public void remove() throws IOException {
    this.storage.apply(new Directives().xpath(this.xpath()).strict(1).remove());
  }

  /**
     * XPath of this element in XML tree.
     * @return XPath
     */
  private String xpath() {
    return String.format("/github/repos/repo[@coords=\'%s\']/deploykeys/deploykey[id=\'%d\']", this.owner.coordinates(), this.key);
  }
}