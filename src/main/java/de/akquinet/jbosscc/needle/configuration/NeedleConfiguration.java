package de.akquinet.jbosscc.needle.configuration;

import de.akquinet.jbosscc.needle.injection.InjectionProvider;
import de.akquinet.jbosscc.needle.injection.InjectionProviderInstancesSupplier;
import de.akquinet.jbosscc.needle.mock.MockProvider;
import java.lang.annotation.Annotation;
import java.util.Set;


public final class NeedleConfiguration {
    private Set<Class<Annotation>> customInjectionAnnotations;

    private Set<Class<InjectionProvider<?>>> customInjectionProviderClasses;

    private String persistenceunitName;

    private String hibernateCfgFilename;

    private Class<? extends MockProvider> mockProviderClass;

    public static final String CUSTOM_INSTANCES_SUPPLIER_CLASSES_KEY = "custom.instances.supplier.classes";

    private String dbOperationClassName;

    private String jdbcUrl;

    private String jdbcDriver;

    private String jdbcUser;

    private String jdbcPassword;

    /**
     * Returns the configured custom {@link Annotation} classes for default mock
     * injections.
     * 
     * @return a {@link Set} of {@link Annotation} classes
     */
    public Set<Class<Annotation>> getCustomInjectionAnnotations() {
        return customInjectionAnnotations;
    }

    public void setCustomInjectionAnnotations(final Set<Class<Annotation>> customInjectionAnnotations) {
        this.customInjectionAnnotations = customInjectionAnnotations;
    }

    /**
     * Returns the configured custom {@link InjectionProvider} classes.
     * 
     * @return a {@link Set} of {@link InjectionProvider} classes
     */
    public Set<Class<InjectionProvider<?>>> getCustomInjectionProviderClasses() {
        return customInjectionProviderClasses;
    }

    public void setCustomInjectionProviderClasses(final Set<Class<InjectionProvider<?>>> customInjectionProviderClasses) {
        this.customInjectionProviderClasses = customInjectionProviderClasses;
    }

    /**
     * Returns the configured database operation class name.
     * 
     * @return database operation class name or null
     */
    public String getDBOperationClassName() {
        return dbOperationClassName;
    }

    public void setDBOperationClassName(final String dbOperationClassName) {
        this.dbOperationClassName = dbOperationClassName;
    }

    public Set<Class<InjectionProviderInstancesSupplier>> getCustomInjectionProviderInstancesSupplierClasses() {
        return customInjectionProviderInstancesSupplierClasses;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(final String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

    public void setJdbcDriver(final String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    public String getJdbcUser() {
        return jdbcUser;
    }

    public void setJdbcUser(final String jdbcUser) {
        this.jdbcUser = jdbcUser;
    }

    public String getJdbcPassword() {
        return jdbcPassword;
    }

    public void setJdbcPassword(final String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    /**
     * Returns the configured mock provider class
     *
     * @return mock provider class name or null
     */
    public Class<? extends MockProvider> getMockProviderClass() {
        return mockProviderClass;
    }

    public void setMockProviderClass(final Class<? extends MockProvider> mockProviderClass) {
        this.mockProviderClass = mockProviderClass;
    }

    /**
     * Returns the configured jpa persistence unit name.
     * 
     * @return jpa persistence unit name
     */
    public String getPersistenceunitName() {
        return persistenceunitName;
    }

    public void setPersistenceunitName(final String persistenceunitName) {
        this.persistenceunitName = persistenceunitName;
    }

    /**
     * Returns the name of the configured hibernate.cfg file
     * 
     * @return name of hibernate.cfg file
     */
    public String getHibernateCfgFilename() {
        return hibernateCfgFilename;
    }

    public void setHibernateCfgFilename(final String hibernateCfgFilename) {
        this.hibernateCfgFilename = hibernateCfgFilename;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("\nPU_NAME=").append(getPersistenceunitName());
        builder.append("\nCFG_FILE=").append(getHibernateCfgFilename());
        builder.append("\nDB_OPERATION=").append(getDBOperationClassName());
        builder.append("\nMOCK_PROVIDER=").append(mockProviderClass != null ? mockProviderClass.getName() : null);
        return builder.toString();
    }
}