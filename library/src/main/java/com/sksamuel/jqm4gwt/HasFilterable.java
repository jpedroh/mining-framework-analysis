package com.sksamuel.jqm4gwt;

import com.google.gwt.dom.client.Element;

/**
<<<<<<< /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasFilterable.java/left.java
 * <br> Filterable functionality support.
 * <br> See <a href="http://demos.jquerymobile.com/1.4.5/filterable/">Filterable</a>
 * <br> See <a href="http://api.jquerymobile.com/filterable/">Filterable API</a>
||||||| /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasFilterable.java/base.java
 * <p/> Filterable functionality support.
 * <p/> See <a href="http://demos.jquerymobile.com/1.4.5/filterable/">Filterable</a>
 * <p/> See <a href="http://api.jquerymobile.com/filterable/">Filterable API</a>
=======
 *  Filterable functionality support.
 *  See <a href="http://demos.jquerymobile.com/1.4.5/filterable/">Filterable</a>
 *  See <a href="http://api.jquerymobile.com/filterable/">Filterable API</a>
>>>>>>> /usr/src/app/output/jqm4gwt/jqm4gwt/5d69eb0c67f1964835f082cfaffec4da5ca60067/library/src/main/java/com/sksamuel/jqm4gwt/HasFilterable.java/right.java
 *
 * @author SlavaP
 *
 */
public interface HasFilterable {
    void refreshFilter();
    void doBeforeFilter(String filter);

    /**
     * @return - must return true if the element is to be filtered,
     * and it must return false if the element is to be shown.
     * null - means default filtering should be used.
     */
    Boolean doFiltering(Element elt, Integer index, String searchValue);
}
