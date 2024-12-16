/*******************************************************************************
 * This file is part of Pebble.
 *
 * Copyright (c) 2014 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.mitchellbosecke.pebble.loader;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import java.io.Reader;


/**
 * Interface used to find templates for Pebble. Different implementations can
 * use different techniques for finding templates such as looking on the
 * classpath, looking in a database, using a servlet context, etc.
 *
 * @author mbosecke
 *
 */
public interface Loader<T> {
    /**
     * The reader which will be used by Pebble to read the contents of the
     * template.
     *
     * @param templateName
     *            Name of the template
     * @return A reader object
     * @throws LoaderException
     *             If template can not be found
     */
    public abstract Reader getReader(T cacheKey) throws LoaderException;

    /**
     * A method for end users to change the charset used by the loader.
     *
     * @param charset
     *            Character set used by the loader when building a reader object
     */
    public void setCharset(String charset);

    /**
     * Optional prefix to help find templates, ex "/WEB-INF/templates/" or
     * "database_schema."
     *
     * @param prefix
     *            Prefix to help find templates
     */
    public void setPrefix(String prefix);

    /**
     * Optional suffix to help find templates, ex ".html", ".peb"
     *
     * @param suffix
     *            Suffix to attach to template names
     */
    public void setSuffix(String suffix);

    /**
     * Resolves the given {@code relativePath} based on the given
     * {@code anchorPath}.
     *
     * <p>
     * A path is considered as relative when it starts either with '..' or '.'
     * and followed either by a '/' or '\\' otherwise the assumption is that the
     * provided path is an absolute path.
     *
     * @param relativePath
     * 		the relative path which should be resolved.
     * @param anchorPath
     * 		the anchor path based on which the relative path should be
     * 		resolved on.
     * @return the resolved path or {@code null} when the path could not be resolved.
     */
    public abstract String resolveRelativePath(String relativePath, String anchorPath);

    /**
     * This method resolves the given template name to a unique object which can
     * be used as the key within the {@link PebbleEngine#getTemplateCache()}.
     * The returned object will be passed with {@link #getReader(Object)}.
     *
     * <p>
     * The resolve method can eventually add information to the cache key from
     * the context (e.g. user session information, servlet request etc.).
     *
     * <p>
     * As a concrete example if the loader loads a template created by a user
     * form the database the template name itself is not uniquely identify the
     * template. The identification of the template requires also the user which
     * created the template. Hence for the key the user id and the template name
     * should be used. So the cache key is enhanced by some contextual
     * information.
     *
     * <p>
     * The implementor of the method can add as many additional contextual
     * information to the returned object. However the following things needs to
     * be considered:
     * <ul>
     * <li>This method will be called on each
     * {@link PebbleEngine#getTemplate(String)}. Hence the implementation needs
     * to be fast and eventually use some caching for the lookup process.</li>
     * <li>The returned object is used within a cache and hence needs to
     * implement {@link Object#equals(Object)} and {@link Object#hashCode()}.</li>
     * <li>The object is kept in memory and hence it should not be to memory
     * heavy.</li>
     * </ul>
     *
     * <p>
     * Depending on this implementation the
     * {@link PebbleEngine#getTemplateCache()} should be tuned in a way it can
     * operate optimal. E.g. when the number of potential templates is infinite
     * the cache should evict some templates at some point in time otherwise the
     * stability of the memory is not given anymore.
     *
     * @param templateName
     * 		
     * @return 
     */
    public abstract T createCacheKey(String templateName);
}