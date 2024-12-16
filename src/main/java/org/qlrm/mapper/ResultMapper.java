package org.qlrm.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


public abstract class ResultMapper {
    @java.lang.SuppressWarnings("unchecked")
    protected <T> T createInstance(final Constructor<?> ctor, final Object[] args) {
        try {
            return ((T) (ctor.newInstance(args)));
        } catch (java.lang.IllegalArgumentException e) {
            StringBuilder sb = new StringBuilder("no constructor taking:\n");
            for (Object object : args) {
                sb.append("\t").append(object != null ? object.getClass().getName() : null).append("\n");
            }
            throw new RuntimeException(sb.toString(), e);
        } catch (java.lang.InstantiationException | java.lang.IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getCause());
        }
    }
}