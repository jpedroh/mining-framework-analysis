package org.fluentlenium.core.wait;

import com.google.common.base.Predicate;
import org.fluentlenium.core.Fluent;
import org.openqa.selenium.By;
import static org.fluentlenium.core.wait.WaitMessage.equalToMessage;
import static org.fluentlenium.core.wait.WaitMessage.greatherThanMessage;
import static org.fluentlenium.core.wait.WaitMessage.greatherThanOrEqualToMessage;
import static org.fluentlenium.core.wait.WaitMessage.lessThanMessage;
import static org.fluentlenium.core.wait.WaitMessage.lessThanOrEqualToMessage;
import static org.fluentlenium.core.wait.WaitMessage.notEqualToMessage;
import static org.fluentlenium.core.wait.FluentWaitMessages.equalToMessage;
import static org.fluentlenium.core.wait.FluentWaitMessages.greatherThanMessage;
import static org.fluentlenium.core.wait.FluentWaitMessages.greatherThanOrEqualToMessage;
import static org.fluentlenium.core.wait.FluentWaitMessages.lessThanMessage;
import static org.fluentlenium.core.wait.FluentWaitMessages.lessThanOrEqualToMessage;
import static org.fluentlenium.core.wait.FluentWaitMessages.notEqualToMessage;

public class FluentSizeBuilder {

<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
    private By locator;
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
    private String selector;
=======
    private AbstractWaitElementMatcher parent;
    private String selection;
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
    private FluentWait wait;

<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
    public FluentSizeBuilder(Search search, FluentWait fluentWait, By locator, List<Filter> filters) {
        this.locator = locator;
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
    public FluentSizeBuilder(Search search, FluentWait fluentWait, String selector, List<Filter> filters) {
        this.selector = selector;
=======
    public FluentSizeBuilder(AbstractWaitElementMatcher parent, FluentWait fluentWait, String selection) {
        this.parent = parent;
        this.selection = selection;
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
        this.wait = fluentWait;
    }

    /**
     * Equals
     *
     * @param size size value
     */
    public void equalTo(final int size) {
        Predicate<Fluent> isPresent = new com.google.common.base.Predicate<Fluent>() {
            public boolean apply(Fluent fluent) {
                return getSize() == size;
            }
        };
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
        FluentWaitMatcher.until(wait, isPresent, filters, equalToMessage(locator, size));
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
        FluentWaitMatcher.until(wait, isPresent, filters, equalToMessage(selector, size));
=======
        parent.until(wait, isPresent, equalToMessage(selection, size));
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
    }

    /**
     * Not equals
     *
     * @param size size value
     */
    public void notEqualTo(final int size) {
        Predicate<Fluent> isPresent = new com.google.common.base.Predicate<Fluent>() {
            public boolean apply(Fluent fluent) {
                return getSize() != size;
            }
        };
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
        FluentWaitMatcher.until(wait, isPresent, filters, notEqualToMessage(locator, size));
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
        FluentWaitMatcher.until(wait, isPresent, filters, notEqualToMessage(selector, size));
=======
        parent.until(wait, isPresent, notEqualToMessage(selection, size));
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
    }

    /**
     * Less than
     *
     * @param size size value
     */
    public void lessThan(final int size) {
        Predicate<Fluent> isPresent = new com.google.common.base.Predicate<Fluent>() {
            public boolean apply(Fluent fluent) {
                return getSize() < size;
            }
        };
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
        FluentWaitMatcher.until(wait, isPresent, filters, lessThanMessage(locator, size));
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
        FluentWaitMatcher.until(wait, isPresent, filters, lessThanMessage(selector, size));
=======
        parent.until(wait, isPresent, lessThanMessage(selection, size));
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
    }

    /**
     * Less than or equals
     *
     * @param size size value
     */
    public void lessThanOrEqualTo(final int size) {
        Predicate<Fluent> isPresent = new com.google.common.base.Predicate<Fluent>() {
            public boolean apply(Fluent fluent) {
                return getSize() <= size;
            }
        };
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
        FluentWaitMatcher.until(wait, isPresent, filters, lessThanOrEqualToMessage(locator, size));
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
        FluentWaitMatcher.until(wait, isPresent, filters, lessThanOrEqualToMessage(selector, size));
=======
        parent.until(wait, isPresent, lessThanOrEqualToMessage(selection, size));
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
    }

    /**
     * Greater than
     *
     * @param size size value
     */
    public void greaterThan(final int size) {
        Predicate<Fluent> isPresent = new com.google.common.base.Predicate<Fluent>() {
            public boolean apply(Fluent fluent) {
                return getSize() > size;
            }
        };
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
        FluentWaitMatcher.until(wait, isPresent, filters, greatherThanMessage(locator, size));
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
        FluentWaitMatcher.until(wait, isPresent, filters, greatherThanMessage(selector, size));
=======
        parent.until(wait, isPresent, greatherThanMessage(selection, size));
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
    }

    /**
     * Greater than or equals
     *
     * @param size size value
     */
    public void greaterThanOrEqualTo(final int size) {
        Predicate<Fluent> isPresent = new com.google.common.base.Predicate<Fluent>() {
            public boolean apply(Fluent fluent) {
                return getSize() >= size;
            }
        };
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
        FluentWaitMatcher.until(wait, isPresent, filters, greatherThanOrEqualToMessage(locator, size));
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
        FluentWaitMatcher.until(wait, isPresent, filters, greatherThanOrEqualToMessage(selector, size));
=======
        parent.until(wait, isPresent, greatherThanOrEqualToMessage(selection, size));
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
    }

    private int getSize() {
<<<<<<< /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/left.java
        if (filters.size() > 0) {
            return search.find(locator, (Filter[]) filters.toArray(new Filter[filters.size()])).size();
        } else {
            return search.find(locator).size();
        }
||||||| /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/base.java
        if (filters.size() > 0) {
            return search.find(selector, (Filter[]) filters.toArray(new Filter[filters.size()])).size();
        } else {
            return search.find(selector).size();
        }
=======
        return parent.find().size();
>>>>>>> /usr/src/app/output/fluentlenium/fluentlenium/48293f0369c2f9a76452e4c21bb376cc60377f69/fluentlenium-core/src/main/java/org/fluentlenium/core/wait/FluentSizeBuilder.java/right.java
    }
}
