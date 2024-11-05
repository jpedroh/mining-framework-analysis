package org.dasein.cloud;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProviderContext extends ProviderContextCompat implements Serializable {
  static private final Random random = new Random();

  static public class Value<T extends java.lang.Object> {
    public String name;

    public T value;

    public Value(@Nonnull String name, @Nonnull T value) {
      this.name = name;
      this.value = value;
    }

    public byte[][] getKeypair() {
      return (byte[][]) value;
    }

    public Float getFloat() {
      if (value instanceof Float) {
        return (Float) value;
      } else {
        if (value instanceof Number) {
          return ((Number) value).floatValue();
        } else {
          if (value instanceof String) {
            return Float.parseFloat((String) value);
          } else {
            throw new ClassCastException("Not a float: " + value);
          }
        }
      }
    }

    public Integer getInt() {
      if (value instanceof Integer) {
        return (Integer) value;
      } else {
        if (value instanceof Number) {
          return ((Number) value).intValue();
        } else {
          if (value instanceof String) {
            return Integer.parseInt((String) value);
          } else {
            throw new ClassCastException("Not an integer: " + value);
          }
        }
      }
    }

    public byte[] getPassword() {
      if (value instanceof String) {
        try {
          return ((String) value).getBytes("utf-8");
        } catch (UnsupportedEncodingException ignore) {
          return (byte[]) value;
        }
      }
      return (byte[]) value;
    }

    public String getText() {
      if (value instanceof String) {
        return (String) value;
      } else {
        return value.toString();
      }
    }

    static public @Nonnull Value<?> parseValue(@Nonnull ContextRequirements.Field field, @Nonnull String... fromStrings) throws UnsupportedEncodingException {
      switch (field.type) {
        case KEYPAIR:
        if (fromStrings.length != 2) {
          throw new IndexOutOfBoundsException("Should have exactly 2 strings for a keypair value");
        }
        if (fromStrings[0] == null || fromStrings[1] == null) {
          throw new RuntimeException("Keypair values can not be null");
        }
        byte[][] bytes = new byte[2][];
        bytes[0] = fromStrings[0].getBytes("utf-8");
        bytes[1] = fromStrings[1].getBytes("utf-8");
        return new Value<byte[][]>(field.name, bytes);
        case TEXT:
        return new Value<String>(field.name, fromStrings[0]);
        case INTEGER:
        return new Value<Integer>(field.name, Integer.parseInt(fromStrings[0]));
        case FLOAT:
        return new Value<Float>(field.name, Float.parseFloat(fromStrings[0]));
        case PASSWORD:
        return new Value<byte[]>(field.name, fromStrings[0].getBytes("utf-8"));
        default:
        throw new RuntimeException("Unsupported type: " + field.type);
      }
    }
  }

  static public void clear(byte[]... keys) {
    if (keys != null) {
      for (byte[] key : keys) {
        if (key != null) {
          random.nextBytes(key);
        }
      }
    }
  }

  static ProviderContext getContext(@Nonnull Cloud cloud, @Nonnull String accountNumber, @Nullable String regionId, @Nonnull Value<?>... configurationValues) {
    ProviderContext ctx = new ProviderContext(cloud, accountNumber, regionId);
    Properties p = new Properties();
    ctx.configurationValues = new HashMap<String, Object>();
    for (Value<?> v : configurationValues) {
      ctx.configurationValues.put(v.name, v.value);
      if (v.value instanceof String) {
        p.setProperty(v.name, (String) v.value);
      }
    }
    ctx.setCustomProperties(p);
    return ctx;
  }

  @SuppressWarnings(value = { "UnusedDeclaration" }) public static Random getRandom() {
    return random;
  }

  private String accountNumber;

  private Cloud cloud;

  private Map<String, Object> configurationValues;

  private String effectiveAccountNumber;

  private String regionId;

  private ProviderContext(@Nonnull Cloud cloud, @Nonnull String accountNumber, @Nullable String regionId) {
    this.cloud = cloud;
    this.accountNumber = accountNumber;
    this.regionId = regionId;
  }

  public @Nonnull String getAccountNumber() {
    return accountNumber;
  }

  @SuppressWarnings(value = { "deprecation" }) void configureForDeprecatedConnect(@Nonnull CloudProvider p) {
    if (configurationValues == null) {
      configurationValues = new HashMap<String, Object>();
      ContextRequirements r = p.getContextRequirements();
      ContextRequirements.Field access = r.getCompatAccessKeys();
      if (access != null) {
        byte[] key = getAccessPublic();
        if (key != null) {
          configurationValues.put(access.name, new byte[][] { key, getAccessPrivate() });
        }
      }
      ContextRequirements.Field x509 = r.getCompatAccessX509();
      if (x509 != null) {
        byte[] cert = getX509Cert();
        if (cert != null) {
          configurationValues.put(x509.name, new byte[][] { cert, getX509Key() });
        }
      }
      Properties props = getCustomProperties();
      for (ContextRequirements.Field field : r.getConfigurableValues()) {
        if ((access == null || !access.name.equals(field.name)) && (x509 == null || !x509.name.equals(field.name))) {
          if (props.containsKey(field.name)) {
            configurationValues.put(field.name, props.getProperty(field.name));
          }
        }
      }
    }
  }

  public @Nonnull CloudProvider connect() throws CloudException, InternalException {
    return connect(null);
  }

  public @Nonnull CloudProvider connect(@Nullable CloudProvider computeProvider) throws CloudException, InternalException {
    try {
      ProviderContext computeContext = null;
      if (computeProvider != null) {
        computeContext = computeProvider.getContext();
        if (computeContext == null) {
          throw new InternalException("The compute provider has not yet connected to the compute cloud");
        }
      }
      CloudProvider p = cloud.buildProvider();
      p.connect(this, computeProvider, cloud);
      if (computeContext != null) {
        effectiveAccountNumber = computeContext.getAccountNumber();
      }
      return p;
    } catch (InstantiationException e) {
      throw new InternalException(e);
    } catch (IllegalAccessException e) {
      throw new InternalException(e);
    }
  }

  public @Nonnull ProviderContext copy(@Nonnull String havingRegionId) throws InternalException {
    try {
      CloudProvider provider = this.getCloud().buildProvider();
      List<ContextRequirements.Field> fields = provider.getContextRequirements().getConfigurableValues();
      List<Value<Object>> values = new ArrayList<Value<Object>>();
      for (ContextRequirements.Field f : fields) {
        Object value = this.getConfigurationValue(f);
        if (value != null) {
          values.add(new Value<Object>(f.name, value));
        }
      }
      return this.getCloud().createContext(getAccountNumber(), havingRegionId, values.toArray(new Value[values.size()]));
    } catch (IllegalAccessException e) {
      throw new InternalException(e);
    } catch (InstantiationException e) {
      throw new InternalException(e);
    }
  }

  public @Nonnull Cloud getCloud() {
    return cloud;
  }

  public @Nullable Object getConfigurationValue(@Nonnull String field) {
    return configurationValues.get(field);
  }

  public @Nullable Object getConfigurationValue(@Nonnull ContextRequirements.Field field) {
    return getConfigurationValue(field.name);
  }

  public @Nonnull String getEffectiveAccountNumber() {
    if (effectiveAccountNumber == null) {
      return getAccountNumber();
    }
    return effectiveAccountNumber;
  }

  public @Nullable String getRegionId() {
    return regionId;
  }

  @Deprecated public ProviderContext() {
  }

  @Deprecated public ProviderContext(@Nonnull String accountNumber, @Nonnull String inRegionId) {
    this.accountNumber = accountNumber;
    regionId = inRegionId;
  }

  @Override @Deprecated public void setAccountNumber(@Nonnull String accountNumber) {
    if (this.accountNumber == null) {
      this.accountNumber = accountNumber;
    } else {
      throw new RuntimeException("Cannot double-set the account number. Tried " + accountNumber + ", was already " + this.accountNumber);
    }
  }

  @SuppressWarnings(value = { "deprecation" }) @Deprecated public void setCloud(@Nonnull CloudProvider provider) throws InternalException {
    String endpoint = getEndpoint();
    if (endpoint == null) {
      throw new InternalException("The context was not properly configured");
    }
    cloud = Cloud.getInstance(endpoint);
    if (cloud == null) {
      String pname = getProviderName();
      String cname = getCloudName();
      cloud = Cloud.register(pname == null ? provider.getProviderName() : pname, cname == null ? provider.getCloudName() : cname, endpoint, provider.getClass());
    }
  }

  @Deprecated void setEffectiveAccountNumber(@Nonnull String effectiveAccountNumber) {
    if (this.effectiveAccountNumber == null) {
      this.effectiveAccountNumber = effectiveAccountNumber;
    } else {
      throw new RuntimeException("Cannot double-set the effective account number. Tried " + effectiveAccountNumber + ", was already " + this.effectiveAccountNumber);
    }
  }

  @Override @Deprecated public void setRegionId(@Nullable String regionId) {
    if (this.regionId == null) {
      this.regionId = regionId;
    } else {
      throw new RuntimeException("Cannot double-set the region ID. Tried " + regionId + ", was already " + this.regionId);
    }
  }

  private RequestTrackingStrategy strategy;

  public @Nonnull ProviderContext withRequestTracking(@Nonnull RequestTrackingStrategy strategy) {
    this.strategy = strategy;
    return this;
  }

  public @Nullable RequestTrackingStrategy getRequestTrackingStrategy() {
    return this.strategy;
  }
}