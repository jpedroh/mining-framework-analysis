package org.gwtbootstrap3.client;

/*
 * #%L
 * GwtBootstrap3
 * %%
 * Copyright (C) 2013 - 2014 GwtBootstrap3
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * @author Sven Jacobs
 */
public interface GwtBootstrap3ClientBundle extends ClientBundle {

    static final GwtBootstrap3ClientBundle INSTANCE = GWT.create(GwtBootstrap3ClientBundle.class);

<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/3f6b71884670971b10f90b5ce25daedc6dad12ea/gwtbootstrap3/src/main/java/org/gwtbootstrap3/client/GwtBootstrap3ClientBundle.java/left.java
    @Source("resource/js/jquery-1.11.2.min.js")
||||||| /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/3f6b71884670971b10f90b5ce25daedc6dad12ea/gwtbootstrap3/src/main/java/org/gwtbootstrap3/client/GwtBootstrap3ClientBundle.java/base.java
    @Source("resource/js/jquery-1.11.0.min.js")
=======
    @Source("resource/js/jquery-1.11.0.min.cache.js")
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/3f6b71884670971b10f90b5ce25daedc6dad12ea/gwtbootstrap3/src/main/java/org/gwtbootstrap3/client/GwtBootstrap3ClientBundle.java/right.java
    TextResource jQuery();

    @Source("resource/js/bootstrap-3.3.1.min.cache.js")
    TextResource bootstrap();
}
