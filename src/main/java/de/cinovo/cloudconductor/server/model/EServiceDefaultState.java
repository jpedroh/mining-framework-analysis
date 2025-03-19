package de.cinovo.cloudconductor.server.model;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import de.cinovo.cloudconductor.api.ServiceState;
import de.taimos.dao.IEntity;

/**
 * Copyright 2013 Cinovo AG<br>
 * <br>
 * 
 * @author psigloch
 * 
 */
@Entity @Table(name = "servicedefaultstate", schema = "cloudconductor") public class EServiceDefaultState implements IEntity<Long> {
  private static final long serialVersionUID = 1L;

  private Long id;

  private EService service;

  private ETemplate template;

  private ServiceState state = ServiceState.STOPPED;

  @Override @Id @GeneratedValue(strategy = GenerationType.IDENTITY) public Long getId() {
    return this.id;
  }

  /**
	 * @param id the id to set
	 */
  public void setId(long id) {
    this.id = id;
  }

  /**
	 * @return the service
	 */
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "serviceid") public EService getService() {
    return this.service;
  }

  /**
	 * @param service the service to set
	 */
  public void setService(EService service) {
    this.service = service;
  }

  /**
	 * @return the host
	 */
  @ManyToOne(optional = false, fetch = FetchType.LAZY) @JoinColumn(name = "templateid") public ETemplate getTemplate() {
    return this.template;
  }

  /**
	 * @param template the template to set
	 */
  public void setTemplate(ETemplate template) {
    this.template = template;
  }

  /**
	 * @return the state
	 */
  public ServiceState getState() {
    return this.state;
  }

  /**
	 * @param state the state to set
	 */
  public void setState(ServiceState state) {
    this.state = state;
  }

  @Override public boolean equals(Object obj) {
    if (!(obj instanceof EServiceDefaultState)) {
      return false;
    }
    EServiceDefaultState other = (EServiceDefaultState) obj;
    if ((this.getId() != null) && (other.getId() != null)) {
      return this.getId().equals(other.getId());
    }
    boolean result = this.service.getId().equals(other.service.getId());
    if (result) {
      result = this.template.getId().equals(other.template.getId());
    }
    return result;
  }

  @Override public int hashCode() {
    int val = (this.getId() == null) ? 0 : this.getId().hashCode();
    int parent = (this.service == null) ? 0 : this.service.hashCode();
    int parent2 = (this.template == null) ? 0 : this.template.hashCode();
    return val * (parent + parent2);
  }
}