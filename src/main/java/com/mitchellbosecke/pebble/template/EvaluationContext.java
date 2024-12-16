package com.mitchellbosecke.pebble.template;

import com.mitchellbosecke.pebble.error.PebbleException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Created by mitchell on 2016-11-13.
 */
public interface EvaluationContext {
    public abstract boolean isStrictVariables();

    public abstract Locale getLocale();

    public abstract Object getVariable(String key);
}