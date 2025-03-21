package org.urish.gwtit.titanium;
import org.urish.gwtit.client.EventCallback;
import com.google.gwt.core.client.JavaScriptObject;

/**
 * The top level titanium module.
 * <p>
 * Notes: Titanium provides a number of built-in objects in the Javascript that
 * are not part of the Titanium namespace. Specifically, the following are
 * available.
 * 
 * #### JSON
 * 
 * Titanium has a built-in JSON parser with two main functions: `parse` and
 * `stringify`. `JSON.parse` will safely evaluate a string encoded as Javascript
 * into a Javascript object. `JSON.stringify` will encode a Javascript object
 * into a string.
 * 
 * #### Timers
 * 
 * Titanium has built-in support for one-shot and repeating timers with two main
 * functions: `setTimeout` and `setInterval`. `setTimeout` takes 2 arguments:
 * function and timeout in milliseconds after which the function should be
 * executed and returns a timer handle that can be used to cancel a pending
 * timer with `clearTimeout`. `setInterval` takes 2 arguments: function and
 * timeout in milliseconds for how often the function should be executed until
 * cancelled and returns a timer handle that can be used to cancel a timer with
 * `clearInterval`.
 * 
 * #### Alert
 * 
 * Titanium has a built-in convenience function `alert` which can be used as a
 * shortcut to [[Titanium.UI.createAlertDialog]] for creating a message box.
 * Note that unlike a web browser-based version of `alert`, the method is
 * asynchronous. However, only one alert dialog will be visible and modal at a
 * time.
 * 
 * #### Locale
 * 
 * The macro `L` can also be used which aliases the method
 * [[Titanium.Locale.getString]].
 * 
 * #### String formatting
 * 
 * The following are built-in functions available on the `String` class which
 * are Titanium specific and will aid in formatting strings into a
 * locale-specific string.
 * 
 * `String.format`: format a generic string using the [IEEE printf
 * specification]
 * (http://www.opengroup.org/onlinepubs/009695399/functions/printf.html).
 * 
 * `String.formatDate`: format a date into a locale specific date format.
 * Optionally pass a second argument (string) as either "short" (default),
 * "medium" or "long" for controlling the date format.
 * 
 * `String.formatTime`: format a date into a locale specific time format.
 * 
 * `String.formatDecimal`: format a number into a locale specific decimal
 * format.
 * 
 * `String.formatCurrency`: format a number into a locale specific currency
 * format.
 * 
 * @platforms android, iphone, ipad
 * @since 0.1
 */
public class Titanium extends org.urish.gwtit.titanium.Module {
  protected Titanium() {
  }

  /**
	 * @return The user-agent string used by titanium
	 */
  public static native String getUserAgent();

  public static native void setUserAgent(String value);

  /**
	 * @return The version of titanium that is executing
	 */
  public static native float getVersion();

  public static native void setVersion(float value);

  public static native org.urish.gwtit.titanium.Blob createBlob();

  public static native org.urish.gwtit.titanium.BlobStream createBlobStream();

  public static native org.urish.gwtit.titanium.BufferStream createBufferStream();

  public static native org.urish.gwtit.titanium.IOStream createIOStream();

  public static native org.urish.gwtit.titanium.Module createModule();

  public static native org.urish.gwtit.titanium.Proxy createProxy();

  public static native org.urish.gwtit.titanium.Accelerometer createAccelerometer();

  public static native org.urish.gwtit.titanium.Analytics createAnalytics();

  public static native org.urish.gwtit.titanium.Android createAndroid();

  public static native org.urish.gwtit.titanium.API createAPI();

  public static native org.urish.gwtit.titanium.App createApp();

  public static native org.urish.gwtit.titanium.Codec createCodec();

  public static native org.urish.gwtit.titanium.Contacts createContacts();

  public static native org.urish.gwtit.titanium.Database createDatabase();

  public static native org.urish.gwtit.titanium.Facebook createFacebook();

  public static native org.urish.gwtit.titanium.Filesystem createFilesystem();

  public static native org.urish.gwtit.titanium.Geolocation createGeolocation();

  public static native org.urish.gwtit.titanium.Gesture createGesture();

  public static native org.urish.gwtit.titanium.Locale createLocale();

  public static native org.urish.gwtit.titanium.Map createMap();

  public static native org.urish.gwtit.titanium.Media createMedia();

  public static native org.urish.gwtit.titanium.Network createNetwork();

  public static native org.urish.gwtit.titanium.Platform createPlatform();

  public static native org.urish.gwtit.titanium.Stream createStream();

  public static native org.urish.gwtit.titanium.UI createUI();

  public static native org.urish.gwtit.titanium.Utils createUtils();

  public static native org.urish.gwtit.titanium.XML createXML();

  public static native org.urish.gwtit.titanium.Yahoo createYahoo();

  /**
	 * A filename to include as if the javascript code was written in place.
	 * this is similar to a c `#include` function.
	 * 
	 * @param name
	 *            filename to include
	 */
  public static native void include(String name);

  /**
	 * Creates a new buffer based on the params
	 * 
	 * @param params
	 *            creation arguments
	 * @return The new buffer.
	 */
  public static native org.urish.gwtit.titanium.Buffer createBuffer(org.urish.gwtit.titanium.CreateBufferArgs params);

  /**
	 * Add an event listener for the instance to receive triggered events
	 * 
	 * @param name
	 *            name of the event
	 * @param callback
	 *            callback function to invoke when the event is fired
	 */
  public static native void addEventListener(String name, EventCallback<JavaScriptObject> callback);

  /**
	 * Remove a previously added event listener
	 * 
	 * @param name
	 *            name of the event
	 * @param callbac
	 *            callback function passed in addEventListener
	 */
  public static native void removeEventListener(String name, EventCallback<JavaScriptObject> callbac);

  /**
	 * Fire a synthesized event to the views listener
	 * 
	 * @param name
	 *            name of the event.
	 * @param event
	 *            event object
	 */
  public static native void fireEvent(String name, Object event);
}