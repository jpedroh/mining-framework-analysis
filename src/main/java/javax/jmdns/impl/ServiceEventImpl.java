package javax.jmdns.impl;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;

/**
 *
 */
public class ServiceEventImpl extends ServiceEvent {
  /**
     *
     */
  private static final long serialVersionUID = 7107973622016897488L;

  /**
     * The type name of the service.
     */
  private final String _type;

  /**
     * The instance name of the service. Or null, if the event was fired to a service type listener.
     */
  private final String _name;

  /**
     * The service info record, or null if the service could be be resolved. This is also null, if the event was fired to a service type listener.
     */
  private final ServiceInfo _info;

  /**
     * Creates a new instance.
     * 
     * @param jmDNS
     *            the JmDNS instance which originated the event.
     * @param type
     *            the type name of the service.
     * @param name
     *            the instance name of the service.
     * @param info
     *            the service info record, or null if the service could be be resolved.
     */
  public ServiceEventImpl(JmDNSImpl jmDNS, String type, String name, ServiceInfo info) {
    super(jmDNS);
    this._type = type;
    this._name = name;
    this._info = info;
  }

  @Override public JmDNS getDNS() {
    return (JmDNS) getSource();
  }

  @Override public String getType() {
    return _type;
  }

  @Override public String getName() {
    return _name;
  }

  @Override public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append('[').append(this.getClass().getSimpleName()).append('@').append(System.identityHashCode(this)).append("\n\tname: \'").append(this.getName()).append("\' type: \'").append(this.getType()).append("\' info: \'").append(this.getInfo()).append("\']");
    return sb.toString();
  }

  @Override public ServiceInfo getInfo() {
    return _info;
  }

  @Override public ServiceEventImpl clone() {
    ServiceInfoImpl newInfo = new ServiceInfoImpl(this.getInfo());
    return new ServiceEventImpl((JmDNSImpl) this.getDNS(), this.getType(), this.getName(), newInfo);
  }
}