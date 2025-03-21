package org.openscoring.service;
import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import org.jpmml.evaluator.ResultMapper;

public class ModelRegistry {
  private ConcurrentMap<Principal, ConcurrentMap<String, Model>> models = new ConcurrentHashMap<>();

  private Function<Principal, ConcurrentMap<String, Model>> initializer = new Function<Principal, ConcurrentMap<String, Model>>() {
    @Override public ConcurrentMap<String, Model> apply(Principal principal) {
      return new ConcurrentHashMap<>();
    }
  };

  public ModelRegistry() {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    Config modelRegistryConfig = config.getConfig("modelRegistry");
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    LoadingModelEvaluatorBuilder modelEvaluatorBuilder = new LoadingModelEvaluatorBuilder();
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    String modelEvaluatorFactoryClassName = modelRegistryConfig.getString("modelEvaluatorFactoryClass");
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (modelEvaluatorFactoryClassName != null) {
      Class<? extends ModelEvaluatorFactory> modelEvaluatorFactoryClazz = loadClass(ModelEvaluatorFactory.class, modelEvaluatorFactoryClassName);
      modelEvaluatorBuilder.setModelEvaluatorFactory(newInstance(modelEvaluatorFactoryClazz));
    }
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    String valueFactoryFactoryClassName = modelRegistryConfig.getString("valueFactoryFactoryClass");
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (valueFactoryFactoryClassName != null) {
      Class<? extends ValueFactoryFactory> valueFactoryFactoryClazz = loadClass(ValueFactoryFactory.class, valueFactoryFactoryClassName);
      modelEvaluatorBuilder.setValueFactoryFactory(newInstance(valueFactoryFactoryClazz));
    }
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    ResultMapper resultMapper = new ResultMapper() {
      @Override public FieldName apply(FieldName name) {
        if (name == null) {
          return ModelResource.DEFAULT_NAME;
        }
        return name;
      }
    };
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    modelEvaluatorBuilder.setResultMapper(resultMapper);
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    boolean validate = modelRegistryConfig.getBoolean("validate");
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    if (validate) {
      Schema schema;
      try {
        schema = JAXBUtil.getSchema();
      } catch (SAXException | IOException e) {
        throw new RuntimeException(e);
      }
      modelEvaluatorBuilder.setSchema(schema).setValidationEventHandler(new SimpleValidationEventHandler());
    }
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    boolean locatable = modelRegistryConfig.getBoolean("locatable");
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    modelEvaluatorBuilder.setLocatable(locatable);
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    VisitorBattery visitors = new VisitorBattery();
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    List<String> visitorClassNames = modelRegistryConfig.getStringList("visitorClasses");
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java

    for (String visitorClassName : visitorClassNames) {
      Class<?> clazz = loadClass(Object.class, visitorClassName);
      if ((Visitor.class).isAssignableFrom(clazz)) {
        Class<? extends Visitor> visitorClazz = clazz.asSubclass(Visitor.class);
        visitors.add(visitorClazz);
      } else {
        if ((VisitorBattery.class).isAssignableFrom(clazz)) {
          Class<? extends VisitorBattery> visitorBatteryClazz = clazz.asSubclass(VisitorBattery.class);
          VisitorBattery visitorBattery = newInstance(visitorBatteryClazz);
          visitors.addAll(visitorBattery);
        } else {
          throw new IllegalArgumentException(new ClassCastException(clazz.toString()));
        }
      }
    }

<<<<<<< Unknown file: This is a bug in JDime.
=======
    modelEvaluatorBuilder.setVisitors(visitors);
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java


<<<<<<< Unknown file: This is a bug in JDime.
=======
    this.modelEvaluatorBuilder = modelEvaluatorBuilder;
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java
  }

  public Map<String, Model> getModels(Principal owner) {
    return this.models.computeIfAbsent(owner, getInitializer());
  }

  public Model get(ModelRef modelRef) {
    return get(modelRef, false);
  }

  public Model get(ModelRef modelRef, boolean touch) {
    Map<String, Model> models = getModels(modelRef.getOwner());
    Model model = models.get(modelRef.getId());
    if (model != null && touch) {
      model.putProperty(Model.PROPERTY_ACCESSED_TIMESTAMP, new Date());
    }
    return model;
  }

  public boolean put(ModelRef modelRef, Model model) {
    Map<String, Model> models = getModels(modelRef.getOwner());
    Model oldModel = models.putIfAbsent(modelRef.getId(), Objects.requireNonNull(model));
    return (oldModel == null);
  }

  public boolean replace(ModelRef modelRef, Model oldModel, Model model) {
    Map<String, Model> models = getModels(modelRef.getOwner());
    return models.replace(modelRef.getId(), oldModel, Objects.requireNonNull(model));
  }

  public boolean remove(ModelRef modelRef, Model model) {
    Map<String, Model> models = getModels(modelRef.getOwner());
    return models.remove(modelRef.getId(), model);
  }

  public Function<Principal, ConcurrentMap<String, Model>> getInitializer() {
    return this.initializer;
  }

  public void setInitializer(Function<Principal, ConcurrentMap<String, Model>> initializer) {
    this.initializer = Objects.requireNonNull(initializer);
  }
}