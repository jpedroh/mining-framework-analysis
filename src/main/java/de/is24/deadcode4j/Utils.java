package de.is24.deadcode4j;

import com.google.common.base.Function;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;
import static com.google.common.collect.Sets.newHashSet;


/**
 * Provides convenience methods.
 *
 * @since 1.2.0
 */
public final class Utils {
    private Utils() {
        super();
    }

    /**
     * Returns <i>groupId:artifactId:version</i> for the specified artifact.
     *
     * @since 1.6
     */
    @Nonnull
    public static String getKeyFor(@Nonnull Artifact artifact) {
        return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
    }

    /**
     * Returns <i>groupId:artifactId</i> for the specified project.
     *
     * @since 1.2.0
     */
    @Nonnull
    public static String getKeyFor(@Nonnull MavenProject project) {
        return project.getGroupId() + ":" + project.getArtifactId();
    }

    /**
     * Returns a <code>Function</code> transforming a <code>MavenProject</code> into it's
     * {@link #getKeyFor(org.apache.maven.project.MavenProject) key representation}.
     *
     * @see #getKeyFor(org.apache.maven.project.MavenProject)
     * @since 1.4
     */
    @Nonnull
    public static Function<MavenProject, String> toKey() {
        return new Function<MavenProject, String>() {
            @Override
            public String apply(@Nullable
            MavenProject input) {
                return input == null ? null : getKeyFor(input);
            }
        };
    }

    /**
     * Returns a map's value for the specified key or the given default value if the value is <code>null</code>.
     *
     * @since 1.2.0
     */
    public static <K, V> V getValueOrDefault(Map<K, V> map, K key, V defaultValue) {
        V value = map.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * Retrieves an existing <code>Set</code> being mapped by the specified key or puts a new one into the map.
     *
     * @since 1.4
     */
    public static <K, V> Set<V> getOrAddMappedSet(Map<K, Set<V>> map, K key) {
        Set<V> values = map.get(key);
        if (values == null) {
            values = newHashSet();
            map.put(key, values);
        }
        return values;
    }
}