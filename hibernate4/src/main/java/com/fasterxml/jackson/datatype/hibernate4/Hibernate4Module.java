package com.fasterxml.jackson.datatype.hibernate4;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.Module;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.Mapping;

public class Hibernate4Module extends Module {
  public enum Feature {
    FORCE_LAZY_LOADING(false),
    USE_TRANSIENT_ANNOTATION(true),
    SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS(false),
    REQUIRE_EXPLICIT_LAZY_LOADING_MARKER(false),
    REPLACE_PERSISTENT_COLLECTIONS(false)
    ;

    final boolean _defaultState;

    final int _mask;

    /**
         * Method that calculates bit set (flags) of all features that
         * are enabled by default.
         */
    public static int collectDefaults() {
      int flags = 0;
      for (Feature f : values()) {
        if (f.enabledByDefault()) {
          flags |= f.getMask();
        }
      }
      return flags;
    }

    private Feature(boolean defaultState) {
      _defaultState = defaultState;
      _mask = (1 << ordinal());
    }

    public boolean enabledIn(int flags) {
      return (flags & _mask) != 0;
    }

    public boolean enabledByDefault() {
      return _defaultState;
    }

    public int getMask() {
      return _mask;
    }
  }

  protected final static int DEFAULT_FEATURES = Feature.collectDefaults();

  /**
     * Bit flag composed of bits that indicate which
     * {@link Feature}s
     * are enabled.
     */
  protected int _moduleFeatures = DEFAULT_FEATURES;

  /**
     * Hibernate mapping.
     */
  protected final Mapping _mapping;

  protected final SessionFactory _sessionFactory;

  public Hibernate4Module() {
    this(null, null);
  }

  public Hibernate4Module(Mapping mapping) {
    this(mapping, null);
  }

  public Hibernate4Module(SessionFactory sessionFactory) {
    this(null, sessionFactory);
  }

  public Hibernate4Module(Mapping mapping, SessionFactory sessionFactory) {
    _sessionFactory = sessionFactory;
    _mapping = mapping;
  }

  @Override public String getModuleName() {
    return "jackson-datatype-hibernate";
  }

  @Override public Version version() {
    return ModuleVersion.instance.version();
  }

  @Override public void setupModule(SetupContext context) {
    AnnotationIntrospector ai = annotationIntrospector();
    if (ai != null) {
      context.appendAnnotationIntrospector(ai);
    }
    context.addSerializers(new HibernateSerializers(_mapping, _moduleFeatures));
    context.addBeanSerializerModifier(new HibernateSerializerModifier(_moduleFeatures, _sessionFactory));
  }

  /**
     * Method called during {@link #setupModule}, to create {@link com.fasterxml.jackson.databind.AnnotationIntrospector}
     * to register along with module. If null is returned, no introspector is added.
     */
  protected AnnotationIntrospector annotationIntrospector() {
    HibernateAnnotationIntrospector ai = new HibernateAnnotationIntrospector();
    ai.setUseTransient(isEnabled(Feature.USE_TRANSIENT_ANNOTATION));
    return ai;
  }

  public Hibernate4Module enable(Feature f) {
    _moduleFeatures |= f.getMask();
    return this;
  }

  public Hibernate4Module disable(Feature f) {
    _moduleFeatures &= ~f.getMask();
    return this;
  }

  public final boolean isEnabled(Feature f) {
    return (_moduleFeatures & f.getMask()) != 0;
  }

  public Hibernate4Module configure(Feature f, boolean state) {
    if (state) {
      enable(f);
    } else {
      disable(f);
    }
    return this;
  }
}