package com.graphhopper.jsprit.core.problem.job;
import java.util.*;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.SizeDimension;
import com.graphhopper.jsprit.core.problem.Skills;
import com.graphhopper.jsprit.core.problem.solution.route.activity.JobActivity;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;

/**
 * Abstract base class for all Job implementations.
 * <p>
 * See {@linkplain JobBuilder} for detailed instruction how to implement your
 * Job.
 * </p>
 * <p>
 * Created by schroeder on 14.07.14.
 * </p>
 *
 * @author schroeder
 * @author balage
 * @see JobBuilder
 */
public abstract class AbstractJob implements Job {
  public abstract static class JobBuilder<T extends AbstractJob, B extends JobBuilder<T, B>> {
    protected SizeDimension.Builder capacityBuilder = SizeDimension.Builder.newInstance();

    protected Skills.Builder skillBuilder = Skills.Builder.newInstance();

    protected String id;

    protected String name = "no-name";

    protected int priority = 2;

    public JobBuilder(String id) {
      if (id == null) {
        throw new IllegalArgumentException("id must not be null");
      }
      this.id = id;
    }

    /**
         * Adds capacity dimension.
         *
         * @param dimensionIndex
         *            the dimension index of the capacity value
         * @param dimensionValue
         *            the capacity value
         * @return the builder
         * @throws IllegalArgumentException
         *             if dimensionValue < 0
         */
    @SuppressWarnings(value = { "unchecked" }) public B addSizeDimension(int dimensionIndex, int dimensionValue) {
      if (dimensionValue < 0) {
        throw new IllegalArgumentException("capacity value cannot be negative");
      }
      capacityBuilder.addDimension(dimensionIndex, dimensionValue);
      return (B) this;
    }

    @SuppressWarnings(value = { "unchecked" }) public B addRequiredSkill(String skill) {
      skillBuilder.addSkill(skill);
      return (B) this;
    }

    @SuppressWarnings(value = { "unchecked" }) public B setName(String name) {
      this.name = name;
      return (B) this;
    }

    @SuppressWarnings(value = { "unchecked" }) public B addAllRequiredSkills(Skills skills) {
      for (String s : skills.values()) {
        skillBuilder.addSkill(s);
      }
      return (B) this;
    }

    @SuppressWarnings(value = { "unchecked" }) public B addAllSizeDimensions(SizeDimension size) {
      for (int i = 0; i < size.getNuOfDimensions(); i++) {
        capacityBuilder.addDimension(i, size.get(i));
      }
      return (B) this;
    }

    /**
         * Set priority to service. Only 1 = high priority, 2 = medium and 3 =
         * low are allowed.
         * <p>
         * Default is 2 = medium.
         *
         * @param priority
         * @return builder
         */
    @SuppressWarnings(value = { "unchecked" }) public B setPriority(int priority) {
      if (priority < 1 || priority > 3) {
        throw new IllegalArgumentException("incorrect priority. only 1 = high, 2 = medium and 3 = low is allowed");
      }
      this.priority = priority;
      return (B) this;
    }

    /**
         * Builds the job.
         *
         * <p>
         * You never has to override this method. Override the
         * {@linkplain #validate()} and {@linkplain #createInstance()} methods
         * instead. (See for detailed implementation guidlines at
         * {@linkplain JobBuilder}!)
         * </p>
         *
         * @return {@link T} The new implementation of the corresponding Job.
         *
         * @see JobBuilder
         *
         * @author balage
         */
    public final T build() {
      validate();
      T job = createInstance();
      job.createActivities(this);
      return job;
    }

    protected abstract void validate();

    protected abstract T createInstance();

    public SizeDimension getCapacity() {
      return capacityBuilder.build();
    }

    public Skills getSkills() {
      return skillBuilder.build();
    }

    public String getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public int getPriority() {
      return priority;
    }
  }

  private int index;

  private String id;

  private Skills skills;

  private String name;

  private int priority;

  protected List<Location> allLocations;

  private JobActivityList activityList;

  protected Set<TimeWindow> allTimeWindows;

  private Capacity sizeAtStart;

  private Capacity sizeAtEnd;

  /**
     * Builder based constructor.
     *
     * @param builder
     *            The builder instance.
     *
     * @see JobBuilder
     */
  protected AbstractJob(JobBuilder<?, ?> builder) {
    super();
    activityList = new SequentialJobActivityList(this);
    id = builder.getId();
    skills = builder.getSkills();
    name = builder.getName();
    priority = builder.getPriority();
  }

  @Override public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

  private void addLocation(Location location) {
    if (location != null) {
      allLocations.add(location);
    }
  }

  @Override public List<Location> getAllLocations() {
    return allLocations;
  }

  protected void prepareCaches() {
    allLocations = new ArrayList<>();
    allTimeWindows = new LinkedHashSet<>();
    activityList.getAll().stream().forEach((ja) -> {
      addLocation(ja.getLocation());
      addTimeWindows(ja.getTimeWindows());
    });
    sizeAtStart = calcSizeAt(true);
    sizeAtEnd = calcSizeAt(false);
  }

  private Capacity calcSizeAt(boolean start) {
    Capacity size = Capacity.EMPTY;
    for (JobActivity act : activityList.getAll()) {
      size = size.add(act.getSize());
    }
    if (start) {
      return size.getNegativeDimensions().abs();
    } else {
      return size.getPositiveDimensions();
    }
  }

  private void addTimeWindows(Collection<TimeWindow> timeWindows) {
    if (timeWindows != null && !timeWindows.isEmpty()) {
      allTimeWindows.addAll(timeWindows);
    }
  }

  public Capacity getSizeAtStart() {
    return sizeAtStart;
  }

  public Capacity getSizeAtEnd() {
    return sizeAtEnd;
  }

  /**
     * Creates the activities.
     *
     * <p>
     * This functions contract specifies that the implementation has to call
     * {@linkplain #prepareCaches()} function at the end, after all activities
     * are added.
     * </p>
     */
  protected abstract void createActivities(JobBuilder<? extends AbstractJob, ?> jobBuilder);

  @Override public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  /**
     * Two shipments are equal if they have the same id.
     *
     * @return true if shipments are equal (have the same id)
     */
  @Override public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AbstractJob other = (AbstractJob) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else {
      if (!id.equals(other.id)) {
        return false;
      }
    }
    return true;
  }

  protected void setActivities(JobActivityList list) {
    activityList = list;
    prepareCaches();
  }

  @Override public JobActivityList getActivityList() {
    return activityList;
  }

  @Override public Set<TimeWindow> getTimeWindows() {
    return allTimeWindows;
  }

  @Override public String getId() {
    return id;
  }

  @Override public Skills getRequiredSkills() {
    return skills;
  }

  @Override public String getName() {
    return name;
  }

  @Override public int getPriority() {
    return priority;
  }
}