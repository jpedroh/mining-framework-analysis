package org.jboss.msc.service;
import java.util.ArrayList;
import static java.lang.Thread.holdsLock;

/**
 * A single service registration.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
final class ServiceRegistrationImpl implements Dependency {
  /**
     * The service container which contains this registration.
     */
  private final ServiceContainerImpl container;

  /**
     * The name of this registration.
     */
  private final ServiceName name;

  /**
     * The set of dependents on this registration.
     */
  private final IdentityHashSet<Dependent> dependents = new IdentityHashSet<Dependent>(0);

  /**
     * The current instance.
     */
  private ServiceControllerImpl<?> instance;

  /**
     * The number of dependent instances which place a demand-to-start on this registration.  If this value is >0,
     * propagate a demand to the instance, if any.
     */
  private int demandedByCount;

  ServiceRegistrationImpl(final ServiceContainerImpl container, final ServiceName name) {
    this.container = container;
    this.name = name;
  }

  /**
     * Returns the dependents set.
     *
     * @return the dependents set
     */
  IdentityHashSet<Dependent> getDependents() {
    return dependents;
  }

  /**
     * Add a dependent to this controller.
     *
     * @param dependent the dependent to add
     */
  @Override public void addDependent(final Dependent dependent) {
    assert !holdsLock(this);
    assert !holdsLock(dependent);
    final ServiceControllerImpl<?> instance;
    final ArrayList<Runnable> tasks = new ArrayList<Runnable>();
    synchronized (this) {
      synchronized (dependents) {
        if (dependents.contains(dependent)) {
          throw new IllegalStateException("Dependent already exists on this registration");
        }
      }
      instance = this.instance;
      if (instance == null) {
        dependent.immediateDependencyUnavailable(name);
        synchronized (dependents) {
          dependents.add(dependent);
        }
        return;
      }
      synchronized (instance) {
        final boolean leavingRestState = instance.isStableRestState();
        synchronized (dependents) {
          dependents.add(dependent);
        }
        if (!instance.isInstallationCommitted()) {
          dependent.immediateDependencyUnavailable(name);
          return;
        }
        instance.newDependent(name, dependent);
        instance.addAsyncTasks(tasks.size() + 1);
        instance.updateStabilityState(leavingRestState);
      }
    }
    instance.doExecute(tasks);
    tasks.clear();
    synchronized (this) {
      synchronized (instance) {
        final boolean leavingRestState = instance.isStableRestState();
        instance.decrementAsyncTasks();
        instance.transition(tasks);
        instance.addAsyncTasks(tasks.size());
        instance.updateStabilityState(leavingRestState);
      }
    }
    instance.doExecute(tasks);
  }

  /**
     * Remove a dependent from this controller.
     *
     * @param dependent the dependent to remove
     */
  @Override public void removeDependent(final Dependent dependent) {
    assert !holdsLock(this);
    assert !holdsLock(dependent);
    synchronized (dependents) {
      dependents.remove(dependent);
    }
  }

  /**
     * Set the instance.
     *
     * @param instance the new instance
     * @throws DuplicateServiceException if there is already an instance
     */
  void setInstance(final ServiceControllerImpl<?> instance) throws DuplicateServiceException {
    assert instance != null;
    assert !holdsLock(this);
    assert !holdsLock(instance);
    synchronized (this) {
      if (this.instance != null) {
        throw new DuplicateServiceException(String.format("Service %s is already registered", name.getCanonicalName()));
      }
      this.instance = instance;
      if (demandedByCount > 0) {
        instance.addDemands(demandedByCount);
      }
    }
  }

  void clearInstance(final ServiceControllerImpl<?> oldInstance) {
    assert !holdsLock(this);
    synchronized (this) {
      final ServiceControllerImpl<?> instance = this.instance;
      if (instance != oldInstance) {
        return;
      }
      this.instance = null;
    }
  }

  ServiceContainerImpl getContainer() {
    return container;
  }

  @Override public void dependentStopped() {
    assert !holdsLock(this);
    final ServiceControllerImpl<?> instance;
    final ArrayList<Runnable> tasks;
    synchronized (this) {
      instance = this.instance;
      if (instance == null) {
        return;
      }
      synchronized (instance) {
        tasks = instance.dependentStopped();
      }
    }
    instance.doExecute(tasks);
  }

  @Override public Object getValue() throws IllegalStateException {
    synchronized (this) {
      final ServiceControllerImpl<?> instance = this.instance;
      if (instance == null) {
        throw new IllegalStateException("Service is not installed");
      } else {
        return instance.getValue();
      }
    }
  }

  @Override public ServiceName getName() {
    return name;
  }

  public ServiceControllerImpl<?> getDependencyController() {
    return getInstance();
  }

  @Override public void dependentStarted() {
    assert !holdsLock(this);
    synchronized (this) {
      if (instance != null) {
        instance.dependentStarted();
      }
    }
  }

  @Override public void addDemand() {
    assert !holdsLock(this);
    final ServiceControllerImpl<?> instance;
    synchronized (this) {
      demandedByCount++;
      instance = this.instance;
    }
    if (instance != null) {
      instance.addDemand();
    }
  }

  @Override public void removeDemand() {
    assert !holdsLock(this);
    final ServiceControllerImpl<?> instance;
    synchronized (this) {
      demandedByCount--;
      instance = this.instance;
    }
    if (instance != null) {
      instance.removeDemand();
    }
  }

  ServiceControllerImpl<?> getInstance() {
    synchronized (this) {
      return instance;
    }
  }
}