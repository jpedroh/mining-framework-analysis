/*
 * Copyright (c) 2014 Villu Ruusmann
 *
 * This file is part of Openscoring
 *
 * Openscoring is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Openscoring is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Openscoring.  If not, see <http://www.gnu.org/licenses/>.
 */
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

	private Function<Principal, ConcurrentMap<String, Model>> initializer = new Function<Principal, ConcurrentMap<String, Model>>(){

		@Override
		public ConcurrentMap<String, Model> apply(Principal principal){
			return new ConcurrentHashMap<>();
		}
	};


	public ModelRegistry(){
<<<<<<< /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/left.java
||||||| /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/base.java
		Config modelRegistryConfig = config.getConfig("modelRegistry");

		LoadingModelEvaluatorBuilder modelEvaluatorBuilder = new LoadingModelEvaluatorBuilder();

		String modelEvaluatorFactoryClassName = modelRegistryConfig.getString("modelEvaluatorFactoryClass");
		if(modelEvaluatorFactoryClassName != null){
			Class<? extends ModelEvaluatorFactory> modelEvaluatorFactoryClazz = loadClass(ModelEvaluatorFactory.class, modelEvaluatorFactoryClassName);

			modelEvaluatorBuilder.setModelEvaluatorFactory(newInstance(modelEvaluatorFactoryClazz));
		}

		String valueFactoryFactoryClassName = modelRegistryConfig.getString("valueFactoryFactoryClass");
		if(valueFactoryFactoryClassName != null){
			Class<? extends ValueFactoryFactory> valueFactoryFactoryClazz = loadClass(ValueFactoryFactory.class, valueFactoryFactoryClassName);

			modelEvaluatorBuilder.setValueFactoryFactory(newInstance(valueFactoryFactoryClazz));
		}

		FieldMapper resultMapper = new FieldMapper(){

			@Override
			public FieldName apply(FieldName name){

				// A "phantom" default target field
				if(name == null){
					return ModelResource.DEFAULT_NAME;
				}

				return name;
			}
		};

		modelEvaluatorBuilder.setResultMapper(resultMapper);

		boolean validate = modelRegistryConfig.getBoolean("validate");

		if(validate){
			Schema schema;

			try {
				schema = JAXBUtil.getSchema();
			} catch(SAXException | IOException e){
				throw new RuntimeException(e);
			}

			modelEvaluatorBuilder
				.setSchema(schema)
				.setValidationEventHandler(new SimpleValidationEventHandler());
		}

		boolean locatable = modelRegistryConfig.getBoolean("locatable");

		modelEvaluatorBuilder.setLocatable(locatable);

		VisitorBattery visitors = new VisitorBattery();

		List<String> visitorClassNames = modelRegistryConfig.getStringList("visitorClasses");
		for(String visitorClassName : visitorClassNames){
			Class<?> clazz = loadClass(Object.class, visitorClassName);

			if((Visitor.class).isAssignableFrom(clazz)){
				Class<? extends Visitor> visitorClazz = clazz.asSubclass(Visitor.class);

				visitors.add(visitorClazz);
			} else

			if((VisitorBattery.class).isAssignableFrom(clazz)){
				Class<? extends VisitorBattery> visitorBatteryClazz = clazz.asSubclass(VisitorBattery.class);

				VisitorBattery visitorBattery = newInstance(visitorBatteryClazz);

				visitors.addAll(visitorBattery);
			} else

			{
				throw new IllegalArgumentException(new ClassCastException(clazz.toString()));
			}
		}

		modelEvaluatorBuilder.setVisitors(visitors);

		this.modelEvaluatorBuilder = modelEvaluatorBuilder;
=======
		Config modelRegistryConfig = config.getConfig("modelRegistry");

		LoadingModelEvaluatorBuilder modelEvaluatorBuilder = new LoadingModelEvaluatorBuilder();

		String modelEvaluatorFactoryClassName = modelRegistryConfig.getString("modelEvaluatorFactoryClass");
		if(modelEvaluatorFactoryClassName != null){
			Class<? extends ModelEvaluatorFactory> modelEvaluatorFactoryClazz = loadClass(ModelEvaluatorFactory.class, modelEvaluatorFactoryClassName);

			modelEvaluatorBuilder.setModelEvaluatorFactory(newInstance(modelEvaluatorFactoryClazz));
		}

		String valueFactoryFactoryClassName = modelRegistryConfig.getString("valueFactoryFactoryClass");
		if(valueFactoryFactoryClassName != null){
			Class<? extends ValueFactoryFactory> valueFactoryFactoryClazz = loadClass(ValueFactoryFactory.class, valueFactoryFactoryClassName);

			modelEvaluatorBuilder.setValueFactoryFactory(newInstance(valueFactoryFactoryClazz));
		}

		// Jackson does not support the JSON serialization of <code>null</code> map keys
		ResultMapper resultMapper = new ResultMapper(){

			@Override
			public FieldName apply(FieldName name){

				// A "phantom" default target field
				if(name == null){
					return ModelResource.DEFAULT_NAME;
				}

				return name;
			}
		};

		modelEvaluatorBuilder.setResultMapper(resultMapper);

		boolean validate = modelRegistryConfig.getBoolean("validate");

		if(validate){
			Schema schema;

			try {
				schema = JAXBUtil.getSchema();
			} catch(SAXException | IOException e){
				throw new RuntimeException(e);
			}

			modelEvaluatorBuilder
				.setSchema(schema)
				.setValidationEventHandler(new SimpleValidationEventHandler());
		}

		boolean locatable = modelRegistryConfig.getBoolean("locatable");

		modelEvaluatorBuilder.setLocatable(locatable);

		VisitorBattery visitors = new VisitorBattery();

		List<String> visitorClassNames = modelRegistryConfig.getStringList("visitorClasses");
		for(String visitorClassName : visitorClassNames){
			Class<?> clazz = loadClass(Object.class, visitorClassName);

			if((Visitor.class).isAssignableFrom(clazz)){
				Class<? extends Visitor> visitorClazz = clazz.asSubclass(Visitor.class);

				visitors.add(visitorClazz);
			} else

			if((VisitorBattery.class).isAssignableFrom(clazz)){
				Class<? extends VisitorBattery> visitorBatteryClazz = clazz.asSubclass(VisitorBattery.class);

				VisitorBattery visitorBattery = newInstance(visitorBatteryClazz);

				visitors.addAll(visitorBattery);
			} else

			{
				throw new IllegalArgumentException(new ClassCastException(clazz.toString()));
			}
		}

		modelEvaluatorBuilder.setVisitors(visitors);

		this.modelEvaluatorBuilder = modelEvaluatorBuilder;
>>>>>>> /usr/src/app/output/jpmml/openscoring/9aca0c7a8409f3888295b16cc03a74d8def3ed21/openscoring-service/src/main/java/org/openscoring/service/ModelRegistry.java/right.java
	}

	public Map<String, Model> getModels(Principal owner){
		return this.models.computeIfAbsent(owner, getInitializer());
	}

	public Model get(ModelRef modelRef){
		return get(modelRef, false);
	}

	public Model get(ModelRef modelRef, boolean touch){
		Map<String, Model> models = getModels(modelRef.getOwner());

		Model model = models.get(modelRef.getId());
		if(model != null && touch){
			model.putProperty(Model.PROPERTY_ACCESSED_TIMESTAMP, new Date());
		}

		return model;
	}

	public boolean put(ModelRef modelRef, Model model){
		Map<String, Model> models = getModels(modelRef.getOwner());

		Model oldModel = models.putIfAbsent(modelRef.getId(), Objects.requireNonNull(model));

		return (oldModel == null);
	}

	public boolean replace(ModelRef modelRef, Model oldModel, Model model){
		Map<String, Model> models = getModels(modelRef.getOwner());

		return models.replace(modelRef.getId(), oldModel, Objects.requireNonNull(model));
	}

	public boolean remove(ModelRef modelRef, Model model){
		Map<String, Model> models = getModels(modelRef.getOwner());

		return models.remove(modelRef.getId(), model);
	}

	public Function<Principal, ConcurrentMap<String, Model>> getInitializer(){
		return this.initializer;
	}

	public void setInitializer(Function<Principal, ConcurrentMap<String, Model>> initializer){
		this.initializer = Objects.requireNonNull(initializer);
	}
}
