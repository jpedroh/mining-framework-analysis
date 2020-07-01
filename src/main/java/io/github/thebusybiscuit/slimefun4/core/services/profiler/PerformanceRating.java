package io.github.thebusybiscuit.slimefun4.core.services.profiler;

import java.util.function.Predicate;

import org.bukkit.ChatColor;

/**
 * This enum is used to quantify Slimefun's performance impact. This way we can assign a
 * "grade" to each timings report and also use this for metrics collection.
 * 
 * @author TheBusyBiscuit
 * 
 * @see SlimefunProfiler
 *
 */
public enum PerformanceRating implements Predicate<Float> {

    // Thresholds might change in the future!

    UNKNOWN(ChatColor.WHITE, -1),

    GOOD(ChatColor.DARK_GREEN, 10),
    FINE(ChatColor.DARK_GREEN, 20),
    OKAY(ChatColor.GREEN, 30),
    MODERATE(ChatColor.YELLOW, 55),
    SEVERE(ChatColor.RED, 85),
    HURTFUL(ChatColor.DARK_RED, Float.MAX_VALUE);

    private final ChatColor color;
    private final float threshold;

    PerformanceRating(ChatColor color, float threshold) {
        this.color = color;
        this.threshold = threshold;
    }

    @Override
    public boolean test(Float value) {
        return value <= threshold;
    }

    public ChatColor getColor() {
        return color;
    }

}
