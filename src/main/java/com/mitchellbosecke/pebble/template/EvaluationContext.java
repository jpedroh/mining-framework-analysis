package com.mitchellbosecke.pebble.template;

import com.github.benmanes.caffeine.cache.Cache;
import java.util.Locale;


/**
 * Created by mitchell on 2016-11-13.
 */
public interface EvaluationContext {
    public abstract boolean isStrictVariables();

    public abstract Locale getLocale();
}