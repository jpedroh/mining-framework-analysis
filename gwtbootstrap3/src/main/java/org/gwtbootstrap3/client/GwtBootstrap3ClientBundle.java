package org.gwtbootstrap3.client;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

/**
 * @author Sven Jacobs
 */
public interface GwtBootstrap3ClientBundle extends ClientBundle {
  static final GwtBootstrap3ClientBundle INSTANCE = GWT.create(GwtBootstrap3ClientBundle.class);

  @Source(value = 
<<<<<<< /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/3f6b71884670971b10f90b5ce25daedc6dad12ea/gwtbootstrap3/src/main/java/org/gwtbootstrap3/client/GwtBootstrap3ClientBundle.java/left.java
  "resource/js/jquery-1.11.2.min.js"
=======
  "resource/js/jquery-1.11.0.min.cache.js"
>>>>>>> /usr/src/app/output/gwtbootstrap3/gwtbootstrap3/3f6b71884670971b10f90b5ce25daedc6dad12ea/gwtbootstrap3/src/main/java/org/gwtbootstrap3/client/GwtBootstrap3ClientBundle.java/right.java
  ) TextResource jQuery();

  @Source(value = "resource/js/bootstrap-3.3.1.min.cache.js") TextResource bootstrap();
}