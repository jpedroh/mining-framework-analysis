package org.assertj.core.util;

/**
 * Class used for testing <code>{@link Introspection}</code>.
 * 
 * @author Joel Costigliola
 */
public class Employee implements Comparable<Employee>, Doctor {
  private final int age;

  public int getAge() {
    return age;
  }

  private final String company = "google";

  String getCompany() {
    return company;
  }

  private boolean firstJob;

  boolean isFirstJob() {
    return firstJob;
  }

  @SuppressWarnings(value = { "unused" }) private final double salary;

  public Employee(double salary, int age) {
    super();
    this.salary = salary;
    this.age = age;
  }

  @Override public int compareTo(Employee other) {
    return age - other.age;
  }
}