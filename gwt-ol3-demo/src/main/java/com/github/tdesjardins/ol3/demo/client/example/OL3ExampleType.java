package com.github.tdesjardins.ol3.demo.client.example;

/**
 * Provided example types.
 *
 * @author Tino Desjardins
 *
 */
public enum OL3ExampleType {

<<<<<<< /usr/src/app/output/tdesjardins/gwt-ol3-playground/89bea69f8eea1dfa5e8cc75c0dc1a941e6306aa4/gwt-ol3-demo/src/main/java/com/github/tdesjardins/ol3/demo/client/example/OL3ExampleType.java/left.java
		AnimationExample(new AnimationExample()),
		ArcGISExample(new ArcGISExample()),
		GeoJSONExample(new GeoJSONExample()),
		GraticuleExample(new GraticuleExample()),
		ImageExample(new StaticImageExample()),
		MapEventsExample(new MapEventsExample()),
		MapGuideExample(new MapGuideExample()),
		MarkerExample(new MarkerExample()),
		MeasureExample(new MeasureExample()),
		OsmExample(new OsmExample()),
		OverlayExample(new OverlayExample()),
		SelectFeatureExample(new SelectFeaturesExample()),
		TileExample(new TileExample()),
		TileWmsExample(new TileWmsExample()),
		WmsExample(new WmsExample()),
		WmtsExample(new WmtsExample()),
		XyzExample(new XyzExample());
||||||| /usr/src/app/output/tdesjardins/gwt-ol3-playground/89bea69f8eea1dfa5e8cc75c0dc1a941e6306aa4/gwt-ol3-demo/src/main/java/com/github/tdesjardins/ol3/demo/client/example/OL3ExampleType.java/base.java
    AnimationExample(new AnimationExample()),
    GeoJSONExample(new GeoJSONExample()),
    GraticuleExample(new GraticuleExample()),
    ImageExample(new StaticImageExample()),
    MapEventsExample(new MapEventsExample()),
    MapGuideExample(new MapGuideExample()),
    MarkerExample(new MarkerExample()),
    MeasureExample(new MeasureExample()),
    OsmExample(new OsmExample()),
    OverlayExample(new OverlayExample()),
    SelectFeatureExample(new SelectFeaturesExample()),
    TileExample(new TileExample()),
    WmsExample(new WmsExample()),
    WmtsExample(new WmtsExample()),
    XyzExample(new XyzExample());
=======
    AnimationExample(new AnimationExample()),
    ArcGISExample(new ArcGISExample()),
    GeoJSONExample(new GeoJSONExample()),
    GraticuleExample(new GraticuleExample()),
    ImageExample(new StaticImageExample()),
    MapEventsExample(new MapEventsExample()),
    MapGuideExample(new MapGuideExample()),
    MarkerExample(new MarkerExample()),
    MeasureExample(new MeasureExample()),
    OsmExample(new OsmExample()),
    OverlayExample(new OverlayExample()),
    SelectFeatureExample(new SelectFeaturesExample()),
    TileExample(new TileExample()),
    WmsExample(new WmsExample()),
    WmtsExample(new WmtsExample()),
    XyzExample(new XyzExample());
>>>>>>> /usr/src/app/output/tdesjardins/gwt-ol3-playground/89bea69f8eea1dfa5e8cc75c0dc1a941e6306aa4/gwt-ol3-demo/src/main/java/com/github/tdesjardins/ol3/demo/client/example/OL3ExampleType.java/right.java

	private transient Example example;

	OL3ExampleType(Example example) {
		this.example = example;
	}

	public Example getExample() {
		return this.example;
	}

}
