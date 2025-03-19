package javax.jmdns.impl;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.impl.DNSRecord.Pointer;
import javax.jmdns.impl.DNSRecord.Service;
import javax.jmdns.impl.DNSRecord.Text;
import javax.jmdns.impl.constants.DNSRecordClass;
import javax.jmdns.impl.constants.DNSRecordType;
import javax.jmdns.impl.constants.DNSState;
import javax.jmdns.impl.tasks.DNSTask;
import javax.jmdns.impl.util.ByteWrangler;

/**
 * JmDNS service information.
 *
 * @author Arthur van Hoff, Jeff Sonstein, Werner Randelshofer, Victor Toni
 */
public class ServiceInfoImpl extends ServiceInfo implements DNSListener, DNSStatefulObject {
  private static Logger logger = LoggerFactory.getLogger(ServiceInfoImpl.class.getName());

  private String _domain;

  private String _protocol;

  private String _application;

  private String _name;

  private String _subtype;

  private String _server;

  private int _port;

  private int _weight;

  private int _priority;

  private byte[] _text;

  private Map<String, byte[]> _props;

  private final Set<Inet4Address> _ipv4Addresses;

  private final Set<Inet6Address> _ipv6Addresses;

  /**
     * Flag to track if an InetAddress was set at least once
     */
  private boolean _inetAddressSet = false;

  private transient String _key;

  private boolean _persistent;

  private boolean _needTextAnnouncing;

  private final ServiceInfoState _state;

  private Delegate _delegate;

  public static interface Delegate {
    public void textValueUpdated(ServiceInfo target, byte[] value);
  }

  private final static class ServiceInfoState extends DNSStatefulObject.DefaultImplementation {
    private static final long serialVersionUID = 1104131034952196820L;

    private final ServiceInfoImpl _info;

    /**
         * @param info
         */
    public ServiceInfoState(ServiceInfoImpl info) {
      super();
      _info = info;
    }

    @Override protected void setTask(DNSTask task) {
      super.setTask(task);
      if ((this._task == null) && _info.needTextAnnouncing()) {
        this.lock();
        try {
          if ((this._task == null) && _info.needTextAnnouncing()) {
            if (this._state.isAnnounced()) {
              this.setState(DNSState.ANNOUNCING_1);
              if (this.getDns() != null) {
                this.getDns().startAnnouncer();
              }
            }
            _info.setNeedTextAnnouncing(false);
          }
        }  finally {
          this.unlock();
        }
      }
    }

    @Override public void setDns(JmDNSImpl dns) {
      super.setDns(dns);
    }
  }

  /**
     * @param type
     * @param name
     * @param subtype
     * @param port
     * @param weight
     * @param priority
     * @param persistent
     * @param text
     * @see javax.jmdns.ServiceInfo#create(String, String, int, int, int, String)
     */
  public ServiceInfoImpl(String type, String name, String subtype, int port, int weight, int priority, boolean persistent, String text) {
    this(ServiceInfoImpl.decodeQualifiedNameMap(type, name, subtype), port, weight, priority, persistent, (byte[]) null);
    try {
      this._text = ByteWrangler.encodeText(text);
    } catch (final IOException e) {
      throw new RuntimeException("Unexpected exception: " + e);
    }
    _server = text;
  }

  /**
     * @param type
     * @param name
     * @param subtype
     * @param port
     * @param weight
     * @param priority
     * @param persistent
     * @param props
     * @see javax.jmdns.ServiceInfo#create(String, String, int, int, int, Map)
     */
  public ServiceInfoImpl(String type, String name, String subtype, int port, int weight, int priority, boolean persistent, Map<String, ?> props) {
    this(ServiceInfoImpl.decodeQualifiedNameMap(type, name, subtype), port, weight, priority, persistent, ByteWrangler.textFromProperties(props));
  }

  /**
     * @param type
     * @param name
     * @param subtype
     * @param port
     * @param weight
     * @param priority
     * @param persistent
     * @param text
     * @see javax.jmdns.ServiceInfo#create(String, String, int, int, int, byte[])
     */
  public ServiceInfoImpl(String type, String name, String subtype, int port, int weight, int priority, boolean persistent, byte[] text) {
    this(ServiceInfoImpl.decodeQualifiedNameMap(type, name, subtype), port, weight, priority, persistent, text);
  }

  public ServiceInfoImpl(Map<Fields, String> qualifiedNameMap, int port, int weight, int priority, boolean persistent, Map<String, ?> props) {
    this(qualifiedNameMap, port, weight, priority, persistent, ByteWrangler.textFromProperties(props));
  }

  ServiceInfoImpl(Map<Fields, String> qualifiedNameMap, int port, int weight, int priority, boolean persistent, String text) {
    this(qualifiedNameMap, port, weight, priority, persistent, (byte[]) null);
    try {
      this._text = ByteWrangler.encodeText(text);
    } catch (final IOException e) {
      throw new RuntimeException("Unexpected exception: " + e);
    }
    _server = text;
  }

  ServiceInfoImpl(Map<Fields, String> qualifiedNameMap, int port, int weight, int priority, boolean persistent, byte[] text) {
    Map<Fields, String> map = ServiceInfoImpl.checkQualifiedNameMap(qualifiedNameMap);
    this._domain = map.get(Fields.Domain);
    this._protocol = map.get(Fields.Protocol);
    this._application = map.get(Fields.Application);
    this._name = map.get(Fields.Instance);
    this._subtype = map.get(Fields.Subtype);
    this._port = port;
    this._weight = weight;
    this._priority = priority;
    this._text = text;
    this.setNeedTextAnnouncing(false);
    this._state = new ServiceInfoState(this);
    this._persistent = persistent;
    this._ipv4Addresses = Collections.synchronizedSet(new LinkedHashSet<Inet4Address>());
    this._ipv6Addresses = Collections.synchronizedSet(new LinkedHashSet<Inet6Address>());
  }

  /**
     * During recovery we need to duplicate service info to reregister them
     *
     * @param info
     */
  ServiceInfoImpl(ServiceInfo info) {
    this._ipv4Addresses = Collections.synchronizedSet(new LinkedHashSet<Inet4Address>());
    this._ipv6Addresses = Collections.synchronizedSet(new LinkedHashSet<Inet6Address>());
    if (info != null) {
      this._domain = info.getDomain();
      this._protocol = info.getProtocol();
      this._application = info.getApplication();
      this._name = info.getName();
      this._subtype = info.getSubtype();
      this._port = info.getPort();
      this._weight = info.getWeight();
      this._priority = info.getPriority();
      this._text = info.getTextBytes();
      this._persistent = info.isPersistent();
      Inet6Address[] ipv6Addresses = info.getInet6Addresses();
      for (Inet6Address address : ipv6Addresses) {
        this._ipv6Addresses.add(address);
      }
      Inet4Address[] ipv4Addresses = info.getInet4Addresses();
      for (Inet4Address address : ipv4Addresses) {
        this._ipv4Addresses.add(address);
      }
    }
    this._state = new ServiceInfoState(this);
  }

  public static Map<Fields, String> decodeQualifiedNameMap(String type, String name, String subtype) {
    Map<Fields, String> qualifiedNameMap = decodeQualifiedNameMapForType(type);
    qualifiedNameMap.put(Fields.Instance, name);
    qualifiedNameMap.put(Fields.Subtype, subtype);
    return checkQualifiedNameMap(qualifiedNameMap);
  }

  public static Map<Fields, String> decodeQualifiedNameMapForType(String type) {
    int index;
    String casePreservedType = type;
    String aType = type.toLowerCase();
    String application = aType;
    String protocol = "";
    String subtype = "";
    String name = "";
    String domain = "";
    if (aType.contains("in-addr.arpa") || aType.contains("ip6.arpa")) {
      index = (aType.contains("in-addr.arpa") ? aType.indexOf("in-addr.arpa") : aType.indexOf("ip6.arpa"));
      name = removeSeparators(casePreservedType.substring(0, index));
      domain = casePreservedType.substring(index);
      application = "";
    } else {
      if ((!aType.contains("_")) && aType.contains(".")) {
        index = aType.indexOf('.');
        name = removeSeparators(casePreservedType.substring(0, index));
        domain = removeSeparators(casePreservedType.substring(index));
        application = "";
      } else {
        if (!aType.startsWith("_") || aType.startsWith("_services")) {
          index = aType.indexOf("._");
          if (index > 0) {
            name = casePreservedType.substring(0, index);
            if (index + 1 < aType.length()) {
              aType = aType.substring(index + 1);
              casePreservedType = casePreservedType.substring(index + 1);
            }
          }
        }
        index = aType.lastIndexOf("._");
        if (index > 0) {
          int start = index + 2;
          int end = aType.indexOf('.', start);
          protocol = casePreservedType.substring(start, end);
        }
        if (protocol.length() > 0) {
          index = aType.indexOf("_" + protocol.toLowerCase() + ".");
          int start = index + protocol.length() + 2;
          int end = aType.length() - (aType.endsWith(".") ? 1 : 0);
          if (end > start) {
            domain = casePreservedType.substring(start, end);
          }
          if (index > 0) {
            application = casePreservedType.substring(0, index - 1);
          } else {
            application = "";
          }
        }
        index = application.toLowerCase().indexOf("._sub");
        if (index > 0) {
          int start = index + 5;
          subtype = removeSeparators(application.substring(0, index));
          application = application.substring(start);
        }
      }
    }
    final Map<Fields, String> qualifiedNameMap = new HashMap<Fields, String>(5);
    qualifiedNameMap.put(Fields.Domain, removeSeparators(domain));
    qualifiedNameMap.put(Fields.Protocol, protocol);
    qualifiedNameMap.put(Fields.Application, removeSeparators(application));
    qualifiedNameMap.put(Fields.Instance, name);
    qualifiedNameMap.put(Fields.Subtype, subtype);
    return qualifiedNameMap;
  }

  protected static Map<Fields, String> checkQualifiedNameMap(Map<Fields, String> qualifiedNameMap) {
    Map<Fields, String> checkedQualifiedNameMap = new HashMap<Fields, String>(5);
    String domain = (qualifiedNameMap.containsKey(Fields.Domain) ? qualifiedNameMap.get(Fields.Domain) : "local");
    if ((domain == null) || (domain.length() == 0)) {
      domain = "local";
    }
    domain = removeSeparators(domain);
    checkedQualifiedNameMap.put(Fields.Domain, domain);
    String protocol = (qualifiedNameMap.containsKey(Fields.Protocol) ? qualifiedNameMap.get(Fields.Protocol) : "tcp");
    if ((protocol == null) || (protocol.length() == 0)) {
      protocol = "tcp";
    }
    protocol = removeSeparators(protocol);
    checkedQualifiedNameMap.put(Fields.Protocol, protocol);
    String application = (qualifiedNameMap.containsKey(Fields.Application) ? qualifiedNameMap.get(Fields.Application) : "");
    if ((application == null) || (application.length() == 0)) {
      application = "";
    }
    application = removeSeparators(application);
    checkedQualifiedNameMap.put(Fields.Application, application);
    String instance = (qualifiedNameMap.containsKey(Fields.Instance) ? qualifiedNameMap.get(Fields.Instance) : "");
    if ((instance == null) || (instance.length() == 0)) {
      instance = "";
    }
    instance = removeSeparators(instance);
    checkedQualifiedNameMap.put(Fields.Instance, instance);
    String subtype = (qualifiedNameMap.containsKey(Fields.Subtype) ? qualifiedNameMap.get(Fields.Subtype) : "");
    if ((subtype == null) || (subtype.length() == 0)) {
      subtype = "";
    }
    subtype = removeSeparators(subtype);
    checkedQualifiedNameMap.put(Fields.Subtype, subtype);
    return checkedQualifiedNameMap;
  }

  private static String removeSeparators(String name) {
    if (name == null) {
      return "";
    }
    String newName = name.trim();
    if (newName.startsWith(".")) {
      newName = newName.substring(1);
    }
    if (newName.startsWith("_")) {
      newName = newName.substring(1);
    }
    if (newName.endsWith(".")) {
      newName = newName.substring(0, newName.length() - 1);
    }
    return newName;
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getType() {
    String domain = this.getDomain();
    String protocol = this.getProtocol();
    String application = this.getApplication();
    return (application.length() > 0 ? "_" + application + "." : "") + (protocol.length() > 0 ? "_" + protocol + "." : "") + domain + ".";
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getTypeWithSubtype() {
    String subtype = this.getSubtype();
    return (subtype.length() > 0 ? "_" + subtype + "._sub." : "") + this.getType();
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getName() {
    return (_name != null ? _name : "");
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getKey() {
    if (this._key == null) {
      this._key = this.getQualifiedName().toLowerCase();
    }
    return this._key;
  }

  /**
     * Sets the service instance name.
     *
     * @param name
     *            unqualified service instance name, such as <code>foobar</code>
     */
  void setName(String name) {
    this._name = name;
    this._key = null;
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getQualifiedName() {
    String domain = this.getDomain();
    String protocol = this.getProtocol();
    String application = this.getApplication();
    String instance = this.getName();
    return (instance.length() > 0 ? instance + "." : "") + (application.length() > 0 ? "_" + application + "." : "") + (protocol.length() > 0 ? "_" + protocol + "." : "") + domain + ".";
  }

  /**
     * @see javax.jmdns.ServiceInfo#getServer()
     */
  @Override public String getServer() {
    return (_server != null ? _server : "");
  }

  /**
     * @param server
     *            the server to set
     */
  void setServer(String server) {
    this._server = server;
  }

  /**
     * {@inheritDoc}
     */
  @Deprecated @Override public String getHostAddress() {
    String[] names = this.getHostAddresses();
    return (names.length > 0 ? names[0] : "");
  }

  /**
     * {@inheritDoc}
     */
  @Override public String[] getHostAddresses() {
    Inet4Address[] ip4Aaddresses = this.getInet4Addresses();
    Inet6Address[] ip6Aaddresses = this.getInet6Addresses();
    String[] names = new String[ip4Aaddresses.length + ip6Aaddresses.length];
    for (int i = 0; i < ip4Aaddresses.length; i++) {
      names[i] = ip4Aaddresses[i].getHostAddress();
    }
    for (int i = 0; i < ip6Aaddresses.length; i++) {
      names[i + ip4Aaddresses.length] = "[" + ip6Aaddresses[i].getHostAddress() + "]";
    }
    return names;
  }

  /**
     * @param addr
     *            the addr to add
     */
  void addAddress(Inet4Address addr) {
    _ipv4Addresses.add(addr);
  }

  /**
     * @param addr
     *            the addr to add
     */
  void addAddress(Inet6Address addr) {
    _ipv6Addresses.add(addr);
  }

  /**
     * {@inheritDoc}
     */
  @Deprecated @Override public InetAddress getAddress() {
    return this.getInetAddress();
  }

  /**
     * {@inheritDoc}
     */
  @Deprecated @Override public InetAddress getInetAddress() {
    InetAddress[] addresses = this.getInetAddresses();
    return (addresses.length > 0 ? addresses[0] : null);
  }

  /**
     * {@inheritDoc}
     */
  @Deprecated @Override public Inet4Address getInet4Address() {
    Inet4Address[] addresses = this.getInet4Addresses();
    return (addresses.length > 0 ? addresses[0] : null);
  }

  /**
     * {@inheritDoc}
     */
  @Deprecated @Override public Inet6Address getInet6Address() {
    Inet6Address[] addresses = this.getInet6Addresses();
    return (addresses.length > 0 ? addresses[0] : null);
  }

  @Override public InetAddress[] getInetAddresses() {
    List<InetAddress> aList = new ArrayList<InetAddress>(_ipv4Addresses.size() + _ipv6Addresses.size());
    aList.addAll(_ipv4Addresses);
    aList.addAll(_ipv6Addresses);
    return aList.toArray(new InetAddress[aList.size()]);
  }

  @Override public Inet4Address[] getInet4Addresses() {
    return _ipv4Addresses.toArray(new Inet4Address[_ipv4Addresses.size()]);
  }

  @Override public Inet6Address[] getInet6Addresses() {
    return _ipv6Addresses.toArray(new Inet6Address[_ipv6Addresses.size()]);
  }

  /**
     * @see javax.jmdns.ServiceInfo#getPort()
     */
  @Override public int getPort() {
    return _port;
  }

  /**
     * @see javax.jmdns.ServiceInfo#getPriority()
     */
  @Override public int getPriority() {
    return _priority;
  }

  /**
     * @see javax.jmdns.ServiceInfo#getWeight()
     */
  @Override public int getWeight() {
    return _weight;
  }

  /**
     * @see javax.jmdns.ServiceInfo#getTextBytes()
     */
  @Override public byte[] getTextBytes() {
    return (this._text != null && this._text.length > 0 ? this._text : ByteWrangler.EMPTY_TXT);
  }

  /**
     * {@inheritDoc}
     */
  @Deprecated @Override public String getTextString() {
    Map<String, byte[]> properties = this.getProperties();
    for (final Map.Entry<String, byte[]> entry : properties.entrySet()) {
      byte[] value = entry.getValue();
      if ((value != null) && (value.length > 0)) {
        final String 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        val = ByteWrangler.readUTF(value)
=======
        str = ByteWrangler.readUTF(value, 0, value.length)
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ;
        return entry.getKey() + "=" + 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        val
=======
        str
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ;
      }
      return entry.getKey();
    }
    return "";
  }

  @Deprecated @Override public String getURL() {
    return this.getURL("http");
  }

  @Override public String[] getURLs() {
    return this.getURLs("http");
  }

  @Deprecated @Override public String getURL(String protocol) {
    String[] urls = this.getURLs(protocol);
    return (urls.length > 0 ? urls[0] : protocol + "://null:" + getPort());
  }

  @Override public String[] getURLs(String protocol) {
    InetAddress[] addresses = this.getInetAddresses();
    List<String> urls = new ArrayList<String>(addresses.length);
    for (InetAddress address : addresses) {
      String hostAddress = address.getHostAddress();
      if (address instanceof Inet6Address) {
        hostAddress = "[" + hostAddress + "]";
      }
      String url = protocol + "://" + hostAddress + ":" + getPort();
      String path = getPropertyString("path");
      if (path != null) {
        if (path.indexOf("://") >= 0) {
          url = path;
        } else {
          url += path.startsWith("/") ? path : "/" + path;
        }
      }
      urls.add(url);
    }
    return urls.toArray(new String[urls.size()]);
  }

  /**
     * {@inheritDoc}
     */
  @Override public synchronized byte[] getPropertyBytes(String name) {
    return this.getProperties().get(name);
  }

  /**
     * {@inheritDoc}
     */
  @Override public synchronized String getPropertyString(String name) {
    byte data[] = this.getProperties().get(name);
    if (data == null) {
      return null;
    }
    if (data == ByteWrangler.NO_VALUE) {
      return "true";
    }
    return ByteWrangler.readUTF(data, 0, data.length);
  }

  /**
     * {@inheritDoc}
     */
  @Override public Enumeration<String> getPropertyNames() {
    Map<String, byte[]> properties = this.getProperties();
    Collection<String> names = (properties != null ? properties.keySet() : Collections.<String>emptySet());
    return new Vector<String>(names).elements();
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getApplication() {
    return (_application != null ? _application : "");
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getDomain() {
    return (_domain != null ? _domain : "local");
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getProtocol() {
    return (_protocol != null ? _protocol : "tcp");
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getSubtype() {
    return (_subtype != null ? _subtype : "");
  }

  /**
     * {@inheritDoc}
     */
  @Override public Map<Fields, String> getQualifiedNameMap() {
    Map<Fields, String> map = new HashMap<Fields, String>(5);
    map.put(Fields.Domain, this.getDomain());
    map.put(Fields.Protocol, this.getProtocol());
    map.put(Fields.Application, this.getApplication());
    map.put(Fields.Instance, this.getName());
    map.put(Fields.Subtype, this.getSubtype());
    return map;
  }

  synchronized Map<String, byte[]> getProperties() {
    if ((_props == null) && (this.getTextBytes() != null)) {
      final Map<String, byte[]> properties = new Hashtable<String, byte[]>();
      try {
        ByteWrangler.readProperties(properties, this.getTextBytes());
      } catch (final Exception exception) {
        logger.warn("Malformed TXT Field ", exception);
      }
      this._props = properties;
    }
    return (_props != null ? _props : Collections.<String, byte[]>emptyMap());
  }

  /**
     * JmDNS callback to update a DNS record.
     *
     * @param dnsCache
     * @param now
     * @param dnsEntry
     */
  @Override public void updateRecord(final DNSCache dnsCache, final long now, 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
  final DNSEntry dnsEntry
=======
  final DNSEntry entry
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
  ) {
    if (!(
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
    dnsEntry
=======
    entry
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
     instanceof DNSRecord)) {
      logger.trace("DNSEntry is not of type \'DNSRecord\' but of type {}", null == 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      dnsEntry
=======
      entry
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
       ? "null" : 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      dnsEntry
=======
      entry
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      .getClass().getSimpleName());
      return;
    }
    final DNSRecord record = (DNSRecord) dnsEntry;
    boolean serviceChanged = false;
    final DNSRecord rec = (DNSRecord) entry;
    if (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
    record
=======
    rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
    .isExpired(now)) {
      serviceChanged = handleExpiredRecord(
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      );
    } else {
      serviceChanged = handleUpdateRecord(dnsCache, now, 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      );
    }
    if (serviceChanged) {
      final JmDNSImpl dns = this.getDns();
      if (dns != null) {
        if (this.hasData()) {
          ServiceEvent event = new ServiceEventImpl(dns, this.getType(), this.getName(), this.clone());
          dns.handleServiceResolved(event);
        } else {
          if (_inetAddressSet && !this.hasInetAddress()) {
            logger.trace("No InetAddress for this ServiceInfo, will delete: {}", this);
            ServiceEvent event = new ServiceEventImpl(dns, this.getType(), this.getName(), this);
            dns.handleServiceRemoved(event);
            _inetAddressSet = false;
            dns.removeServiceInfoFromCache(this);
          }
        }
      } else {
        logger.debug("JmDNS not available.");
      }
    }
    synchronized (this) {
      this.notifyAll();
    }
  }

  /**
     * Handles expired records insofar that it removes their content from this service.
     *
     * Implementation note:<br/>
     * Currently only expired A and AAAA records are handled.
     *
     * @param record to check for data to be removed
     * @return <code>true</code> if data from the expired record could be removed from this service, <code>false</code> otherwise
     */
  private boolean handleExpiredRecord(
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
  final DNSRecord record
=======
  final DNSRecord rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
  ) {
    switch (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
    record
=======
    rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
    .getRecordType()) {
      case TYPE_A:
      if (rec.getName().equalsIgnoreCase(this.getServer())) {
        final DNSRecord.Address address = (DNSRecord.Address) rec;
        final Inet4Address inet4Address = (Inet4Address) address.getAddress();
        if (_ipv4Addresses.remove(inet4Address)) {
          logger.trace("Removed expired IPv4: {}", inet4Address);
          return true;
        }
      }
      break;
      case TYPE_AAAA:
      if (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      .getName().equalsIgnoreCase(this.getServer())) {
        final DNSRecord.Address address = (DNSRecord.Address) 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        record
=======
        rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ;
        final Inet6Address inet6Address = (Inet6Address) address.getAddress();
        if (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        DNSRecordType.TYPE_A.equals(record.getRecordType())
=======
        _ipv6Addresses.remove(inet6Address)
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ) {

<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
          final Inet4Address inet4Address = (Inet4Address) address.getAddress();
=======
          logger.trace("Removed expired IPv6: {}", inet6Address);
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java


<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
          if (_ipv4Addresses.remove(inet4Address)) {
            logger.debug("Removed expired IPv4: {}", inet4Address);
            return true;
          } else {
            logger.debug("Expired IPv4 not in this service: {}", inet4Address);
          }
=======
          return true;
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        } else {
          final Inet6Address inet6Address = (Inet6Address) address.getAddress();
          if (_ipv6Addresses.remove(inet6Address)) {
            logger.debug("Removed expired IPv6: {}", inet6Address);
            return true;
          } else {
            logger.debug("Expired IPv6 not in this service: {}", inet6Address);
          }
        }
      }
      break;
      default:
      logger.trace("Unhandled expired record: {}", 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      );
      break;
    }
    return false;
  }

  /**
     * Adds data of {@link DNSRecord} to the internal service representation.
     * 
     * @param dnsCache
     * @param now
     * @param record to get data from
     * @return
     */
  private boolean handleUpdateRecord(final DNSCache dnsCache, final long now, 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
  final DNSRecord record
=======
  final DNSRecord rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
  ) {
    boolean serviceUpdated = false;
    switch (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
    record
=======
    rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
    .getRecordType()) {
      case TYPE_A:
      if (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      .getName().equalsIgnoreCase(this.getServer())) {
        final DNSRecord.Address address = (DNSRecord.Address) 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        record
=======
        rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ;
        if (address.getAddress() instanceof Inet4Address) {
          final Inet4Address inet4Address = (Inet4Address) address.getAddress();
          if (_ipv4Addresses.add(inet4Address)) {

<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
            serviceUpdated
=======
            _inetAddressSet
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
             = true;
          }
        }
        serviceUpdated = true;
      }
      break;
      case TYPE_AAAA:
      if (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      .getName().equalsIgnoreCase(this.getServer())) {
        final DNSRecord.Address address = (DNSRecord.Address) 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        record
=======
        rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ;
        if (address.getAddress() instanceof Inet6Address) {
          final Inet6Address inet6Address = (Inet6Address) address.getAddress();
          if (_ipv6Addresses.add(inet6Address)) {

<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
            serviceUpdated
=======
            _inetAddressSet
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
             = true;
          }
        }
        serviceUpdated = true;
      }
      break;
      case TYPE_SRV:
      if (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      .getName().equalsIgnoreCase(this.getQualifiedName())) {
        final DNSRecord.Service srv = (DNSRecord.Service) 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        record
=======
        rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ;
        final boolean serverChanged = (_server == null) || !_server.equalsIgnoreCase(srv.getServer());
        _server = srv.getServer();
        _port = srv.getPort();
        _weight = srv.getWeight();
        _priority = srv.getPriority();
        if (serverChanged) {
          _ipv4Addresses.clear();
          _ipv6Addresses.clear();
          for (final DNSEntry entry : dnsCache.getDNSEntryList(_server, DNSRecordType.TYPE_A, DNSRecordClass.CLASS_IN)) {
            this.updateRecord(dnsCache, now, entry);
          }
          for (final DNSEntry entry : dnsCache.getDNSEntryList(_server, DNSRecordType.TYPE_AAAA, DNSRecordClass.CLASS_IN)) {
            this.updateRecord(dnsCache, now, entry);
          }
        } else {
          serviceUpdated = true;
        }
      }
      break;
      case TYPE_TXT:
      if (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      .getName().equalsIgnoreCase(this.getQualifiedName())) {
        DNSRecord.Text txt = (DNSRecord.Text) 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        record
=======
        rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ;
        _text = txt.getText();
        _props = null;
        serviceUpdated = true;
      }
      break;
      case TYPE_PTR:
      if ((this.getSubtype().length() == 0) && (
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
      record
=======
      rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      .getSubtype().length() != 0)) {
        _subtype = 
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        record
=======
        rec
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        .getSubtype();
        serviceUpdated = true;
      }
      break;
      default:
      break;
    }
    return serviceUpdated;
  }

  /**
     * Returns true if the service info is filled with data.
     *
     * @return <code>true</code> if the service info has data, <code>false</code> otherwise.
     */
  @Override public synchronized boolean hasData() {
    return this.getServer() != null && this.hasInetAddress() && this.getTextBytes() != null && this.getTextBytes().length > 0;
  }

  private final boolean hasInetAddress() {
    return _ipv4Addresses.size() > 0 || _ipv6Addresses.size() > 0;
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean advanceState(DNSTask task) {
    return _state.advanceState(task);
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean revertState() {
    return _state.revertState();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean cancelState() {
    return _state.cancelState();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean closeState() {
    return this._state.closeState();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean recoverState() {
    return this._state.recoverState();
  }

  /**
     * {@inheritDoc}
     */
  @Override public void removeAssociationWithTask(DNSTask task) {
    _state.removeAssociationWithTask(task);
  }

  /**
     * {@inheritDoc}
     */
  @Override public void associateWithTask(DNSTask task, DNSState state) {
    _state.associateWithTask(task, state);
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isAssociatedWithTask(DNSTask task, DNSState state) {
    return _state.isAssociatedWithTask(task, state);
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isProbing() {
    return _state.isProbing();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isAnnouncing() {
    return _state.isAnnouncing();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isAnnounced() {
    return _state.isAnnounced();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isCanceling() {
    return this._state.isCanceling();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isCanceled() {
    return _state.isCanceled();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isClosing() {
    return _state.isClosing();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isClosed() {
    return _state.isClosed();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean waitForAnnounced(long timeout) {
    return _state.waitForAnnounced(timeout);
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean waitForCanceled(long timeout) {
    return _state.waitForCanceled(timeout);
  }

  /**
     * {@inheritDoc}
     */
  @Override public int hashCode() {
    return getQualifiedName().hashCode();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean equals(Object obj) {
    return (obj instanceof ServiceInfoImpl) && getQualifiedName().equals(((ServiceInfoImpl) obj).getQualifiedName());
  }

  /**
     * {@inheritDoc}
     */
  @Override public String getNiceTextString() {
    final StringBuilder sb = new StringBuilder();
    for (int i = 0, len = this.getTextBytes().length; i < len; i++) {
      if (i >= 200) {
        sb.append("...");
        break;
      }
      int ch = getTextBytes()[i] & 0xFF;
      if ((ch < ' ') || (ch > 127)) {
        sb.append("\\0");
        sb.append(Integer.toString(ch, 8));
      } else {
        sb.append((char) ch);
      }
    }
    return sb.toString();
  }

  @Override public ServiceInfoImpl clone() {
    ServiceInfoImpl serviceInfo = new ServiceInfoImpl(this.getQualifiedNameMap(), _port, _weight, _priority, _persistent, _text);
    Inet6Address[] ipv6Addresses = this.getInet6Addresses();
    for (Inet6Address address : ipv6Addresses) {
      serviceInfo._ipv6Addresses.add(address);
    }
    Inet4Address[] ipv4Addresses = this.getInet4Addresses();
    for (Inet4Address address : ipv4Addresses) {
      serviceInfo._ipv4Addresses.add(address);
    }
    return serviceInfo;
  }

  /**
     * {@inheritDoc}
     */
  @Override public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append('[').append(this.getClass().getSimpleName()).append('@').append(System.identityHashCode(this));

<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
    sb.append(" name: \'");
=======
>>>>>>> Unknown file: This is a bug in JDime.

    if (0 < this.getName().length()) {
      sb.append(this.getName()).append('.');
    }
    sb.
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
    append(this.getTypeWithSubtype())
=======
    append(" name: \'").append((this.getName().length() > 0 ? this.getName() + "." : "") + this.getTypeWithSubtype())
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
    ;
    sb.append("\' address: \'");
    InetAddress[] addresses = this.getInetAddresses();
    if (addresses.length > 0) {
      for (InetAddress address : addresses) {
        sb.append(address).
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
        append(':').append(this.getPort()).append(' ')
=======
        append(':').append(this.getPort())
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
        ;

<<<<<<< Unknown file: This is a bug in JDime.
=======
        sb.append(' ');
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
      }
    } else {

<<<<<<< Unknown file: This is a bug in JDime.
=======
      sb.append("(null):");
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java

      sb.append("(null):").append(this.getPort());
    }
    sb.append("\' status: \'").append(_state.toString());
    sb.append(this.isPersistent() ? "\' is persistent," : "\',");

<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
    if (this.hasData()) {
      sb.append(" has data");
    } else {
      sb.append(" has NO data");
    }
=======
    sb.append(" has ").append(this.hasData() ? "" : "NO ").append("data");
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java

    if (this.getTextBytes().length > 0) {
      final Map<String, byte[]> properties = this.getProperties();
      if (!properties.isEmpty()) {

<<<<<<< Unknown file: This is a bug in JDime.
=======
        sb.append("\n");
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java

        for (final Map.Entry<String, byte[]> entry : properties.entrySet()) {

<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
          final String value = ByteWrangler.readUTF(entry.getValue());
=======
          final byte[] value = entry.getValue();
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java

          final String str = ByteWrangler.readUTF(value, 0, value.length);
          sb.append(
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
          "\n\t"
=======
          '\t'
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
          ).append(entry.getKey()).
<<<<<<< /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/left.java
          append(": ").append(value)
=======
          append(": ").append(str).append('\n')
>>>>>>> /usr/src/app/output/openhab/jmdns/2c7d1a7405d9d43873f3a78f5373648ef759207e/src/main/java/javax/jmdns/impl/ServiceInfoImpl.java/right.java
          ;
        }
      } else {
        sb.append(", empty");
      }
    }
    sb.append(']');
    return sb.toString();
  }

  /**
     * Create a series of answer that correspond with the give service info.
     *
     * @param recordClass
     *            record class of the query
     * @param unique
     * @param ttl
     * @param localHost
     * @return collection of answers
     */
  public Collection<DNSRecord> answers(DNSRecordClass recordClass, boolean unique, int ttl, HostInfo localHost) {
    List<DNSRecord> list = new ArrayList<DNSRecord>();
    if ((recordClass == DNSRecordClass.CLASS_ANY) || (recordClass == DNSRecordClass.CLASS_IN)) {
      if (this.getSubtype().length() > 0) {
        list.add(new Pointer(this.getTypeWithSubtype(), DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE, ttl, this.getQualifiedName()));
      }
      list.add(new Pointer(this.getType(), DNSRecordClass.CLASS_IN, DNSRecordClass.NOT_UNIQUE, ttl, this.getQualifiedName()));
      list.add(new Service(this.getQualifiedName(), DNSRecordClass.CLASS_IN, unique, ttl, _priority, _weight, _port, localHost.getName()));
      list.add(new Text(this.getQualifiedName(), DNSRecordClass.CLASS_IN, unique, ttl, this.getTextBytes()));
    }
    return list;
  }

  /**
     * {@inheritDoc}
     */
  @Override public void setText(byte[] text) throws IllegalStateException {
    synchronized (this) {
      this._text = text;
      this._props = null;
      this.setNeedTextAnnouncing(true);
    }
  }

  /**
     * {@inheritDoc}
     */
  @Override public void setText(Map<String, ?> props) throws IllegalStateException {
    this.setText(ByteWrangler.textFromProperties(props));
  }

  /**
     * This is used internally by the framework
     *
     * @param text
     */
  void _setText(byte[] text) {
    this._text = text;
    this._props = null;
  }

  public void setDns(JmDNSImpl dns) {
    this._state.setDns(dns);
  }

  /**
     * {@inheritDoc}
     */
  @Override public JmDNSImpl getDns() {
    return this._state.getDns();
  }

  /**
     * {@inheritDoc}
     */
  @Override public boolean isPersistent() {
    return _persistent;
  }

  /**
     * @param needTextAnnouncing
     *            the needTextAnnouncing to set
     */
  public void setNeedTextAnnouncing(boolean needTextAnnouncing) {
    this._needTextAnnouncing = needTextAnnouncing;
    if (this._needTextAnnouncing) {
      _state.setTask(null);
    }
  }

  /**
     * @return the needTextAnnouncing
     */
  public boolean needTextAnnouncing() {
    return _needTextAnnouncing;
  }

  /**
     * @return the delegate
     */
  Delegate getDelegate() {
    return this._delegate;
  }

  /**
     * @param delegate
     *            the delegate to set
     */
  void setDelegate(Delegate delegate) {
    this._delegate = delegate;
  }

  @Override public boolean hasSameAddresses(ServiceInfo other) {
    if (other == null) {
      return false;
    }
    if (other instanceof ServiceInfoImpl) {
      ServiceInfoImpl otherImpl = (ServiceInfoImpl) other;
      return _ipv4Addresses.size() == otherImpl._ipv4Addresses.size() && _ipv6Addresses.size() == otherImpl._ipv6Addresses.size() && _ipv4Addresses.equals(otherImpl._ipv4Addresses) && _ipv6Addresses.equals(otherImpl._ipv6Addresses);
    } else {
      InetAddress[] addresses = getInetAddresses();
      InetAddress[] otherAddresses = other.getInetAddresses();
      return addresses.length == otherAddresses.length && new HashSet<InetAddress>(Arrays.asList(addresses)).equals(new HashSet<InetAddress>(Arrays.asList(otherAddresses)));
    }
  }
}