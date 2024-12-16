package ol.source;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsType;
import ol.Attribution;
import ol.Constant;
import ol.LogoOptions;
import ol.proj.Projection;


/**
 * Abstract base class; normally only used for creating subclasses and not
 * instantiated in apps. Base class for {@link ol.layer.Layer} sources.
 *
 * A generic `change` event is triggered when the state of the source changes.
 *
 * @author Tino Desjardins
 *
 */
@JsType(isNative = true)
public class Source extends java.lang.Object {
    /**
     * Get the attributions of the source.
     * 
     * @return {Array.<ol.Attribution>} Attributions.
     */
    public native Attribution[] getAttributions();

    /**
     * Get the logo of the source.
     *
     * @return {@link LogoOptions}
     */
    public native LogoOptions getLogo();

    /**
     * Get the projection of the source.
     * 
     * @return {ol.proj.Projection} Projection.
     */
    public native Projection getProjection();

    /**
     * @return {Array.<number>|undefined} Resolutions.
     */
    public native double[] getResolutions();

    /**
     * Get the state of the source: one of 'undefined', 'loading', 'ready' or
     * 'error'.
     * 
     * @return {ol.source.State} State.
     */
    @JsMethod(name = "getState")
    public native String getStateString();

@JsOverlay
public final State getState() {
	return Constant.of(State.class, getStateString());
}

    /**
     * Refreshes the source and finally dispatches a 'change' event.
     */
    public native void refresh();

    /**
     * Set the attributions of the source.
     * 
     * @param attributions
     *            Attributions.
     */
    public native void setAttributions(Attribution[] attributions);

    /**
     * Set the projection of the source.
     * 
     * @param projection
     *            Projection.
     */
    public native void setProjection(Projection projection);
}