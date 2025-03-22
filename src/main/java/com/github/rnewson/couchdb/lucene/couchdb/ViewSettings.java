package com.github.rnewson.couchdb.lucene.couchdb;

import net.sf.json.JSONObject;

import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.mozilla.javascript.NativeObject;

import com.github.rnewson.couchdb.lucene.util.Constants;

/**
 * Copyright 2010 Robert Newson
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

public final class ViewSettings {

    public static ViewSettings getDefaultSettings() {
<<<<<<< /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/left.java
        return new ViewSettings(Constants.DEFAULT_FIELD, "analyzed", "no", "string", "1.0", "no", null);
||||||| /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/base.java
        return new ViewSettings(Constants.DEFAULT_FIELD, "analyzed", "no", "string", null);
=======
        return new ViewSettings(Constants.DEFAULT_FIELD, "analyzed", "no", "string", "1.0", null);
>>>>>>> /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/right.java
    }

    private final Index index;
    private final Store store;
    private final String field;
    private final FieldType type;
    private final float boost;
    private final TermVector termvector;
    public ViewSettings(final JSONObject json) {
        this(json, getDefaultSettings());
    }
    public ViewSettings(final JSONObject json, final ViewSettings defaults) {
<<<<<<< /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/left.java
        this(json.optString("field", null), json.optString("index", null), json.optString("store", null), json.optString("type", null), json.optString("boost", null), json.optString("termvector", null), defaults);
||||||| /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/base.java
        this(json.optString("field", null), json.optString("index", null), json.optString("store", null), json.optString("type", null), defaults);
=======
        this(json.optString("field", null), json.optString("index", null), json.optString("store", null), json.optString("type", null), json.optString("boost", null), defaults);
>>>>>>> /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/right.java
    }
    public ViewSettings(final NativeObject obj) {
        this(obj, getDefaultSettings());
    }
    public ViewSettings(final NativeObject obj, final ViewSettings defaults) {
<<<<<<< /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/left.java
        this(get(obj, "field"), get(obj, "index"), get(obj, "store"), get(obj, "type"), get(obj, "boost"), get(obj, "termvector"), defaults);
||||||| /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/base.java
        this(get(obj, "field"), get(obj, "index"), get(obj, "store"), get(obj, "type"), defaults);
=======
        this(get(obj, "field"), get(obj, "index"), get(obj, "store"), get(obj, "type"), get(obj, "boost"), defaults);
>>>>>>> /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/right.java
    }
<<<<<<< /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/left.java
    private ViewSettings(final String field, final String index, final String store, final String type, final String boost, final String termvector, final ViewSettings defaults) {
||||||| /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/base.java
    private ViewSettings(final String field, final String index, final String store, final String type, final ViewSettings defaults) {
=======
    private ViewSettings(final String field, final String index, final String store, final String type, final String boost, final ViewSettings defaults) {
>>>>>>> /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/right.java
        this.field = field != null ? field : defaults.getField();
        this.index = index != null ? Index.valueOf(index.toUpperCase()) : defaults.getIndex();
        this.store = store != null ? Store.valueOf(store.toUpperCase()) : defaults.getStore();
        this.type = type != null ? FieldType.valueOf(type.toUpperCase()) : defaults.getFieldType();
<<<<<<< /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/left.java
        this.boost = boost != null ? Float.valueOf(boost) : defaults.getBoost();
        this.termvector = termvector != null? TermVector.valueOf(termvector.toUpperCase()) : defaults.getTermVector();
||||||| /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/base.java
=======
        this.boost = boost != null ? Float.valueOf(boost) : defaults.getBoost();
>>>>>>> /usr/src/app/output/rnewson/couchdb-lucene/a26642d5ab84f52d51eca92ce9ee594420eb719e/src/main/java/com/github/rnewson/couchdb/lucene/couchdb/ViewSettings.java/right.java
    }
    public float getBoost() {
        return boost;
    }

    public Index getIndex() {
        return index;
    }

    public Store getStore() {
        return store;
    }

    public String getField() {
        return field;
    }

    public FieldType getFieldType() {
        return type;
    }

    public TermVector getTermVector()
    {
        return termvector;
    }

    private static String get(final NativeObject obj, final String key) {
        return obj == null ? null : obj.has(key, null) ? obj.get(key, null).toString() : null;
    }

}
