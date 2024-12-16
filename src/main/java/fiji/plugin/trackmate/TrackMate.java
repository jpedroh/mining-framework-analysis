package fiji.plugin.trackmate;

import fiji.plugin.trackmate.detection.ManualDetectorFactory;
import fiji.plugin.trackmate.detection.SpotDetector;
import fiji.plugin.trackmate.detection.SpotDetectorFactory;
import fiji.plugin.trackmate.detection.SpotDetectorFactoryBase;
import fiji.plugin.trackmate.detection.SpotGlobalDetector;
import fiji.plugin.trackmate.detection.SpotGlobalDetectorFactory;
import fiji.plugin.trackmate.features.EdgeFeatureCalculator;
import fiji.plugin.trackmate.features.FeatureFilter;
import fiji.plugin.trackmate.features.SpotFeatureCalculator;
import fiji.plugin.trackmate.features.TrackFeatureCalculator;
import fiji.plugin.trackmate.tracking.SpotTracker;
import fiji.plugin.trackmate.util.TMUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.imagej.ImgPlus;
import net.imglib2.Interval;
import net.imglib2.algorithm.Algorithm;
import net.imglib2.algorithm.Benchmark;
import net.imglib2.algorithm.MultiThreaded;
import net.imglib2.multithreading.SimpleMultiThreading;
import org.scijava.Named;
import org.scijava.util.VersionUtils;


/**
 * <p>
 * The TrackMate_ class runs on the currently active time-lapse image (2D or 3D)
 * and both identifies and tracks bright spots over time.
 * </p>
 *
 * <p>
 * <b>Required input:</b> A 2D or 3D time-lapse image with bright blobs.
 * </p>
 *
 * @author Nicholas Perry
 * @author Johannes Schindelin
 * @author Jean-Yves Tinevez - Institut Pasteur - July 2010 - 2018
 */
@SuppressWarnings("deprecation")
public class TrackMate implements Benchmark , MultiThreaded , Algorithm , Named {
	public static final String PLUGIN_NAME_STR = "TrackMate";

	public static final String PLUGIN_NAME_VERSION = VersionUtils.getVersion( TrackMate.class );

	/**
	 * The model this trackmate will shape.
	 */
	protected final Model model;

	protected final Settings settings;

	protected long processingTime;

	protected String errorMessage;

	protected int numThreads = Runtime.getRuntime().availableProcessors();

	private String name;

	/* CONSTRUCTORS */
	public TrackMate(final Settings settings) {
		this(new Model(), settings);
	}

	public TrackMate(final Model model, final Settings settings) {
		this.model = model;
		this.settings = settings;
		name = (PLUGIN_NAME_STR + "_v") + PLUGIN_NAME_VERSION;
		if (settings.imp != null) {
			name += ("_(" + settings.imp.getTitle().replace(' ', '_')) + ")";
		}
		name += ("_[" + Integer.toHexString(hashCode())) + "]";
	}

	public TrackMate() {
		this(new Model(), new Settings());
	}

	/*
	 * METHODS
	 */

	public Model getModel()
	{
		return model;
	}

	public Settings getSettings()
	{
		return settings;
	}

	/*
	 * PROCESSES
	 */

	/**
	 * Calculate all features for all detected spots.
	 * <p>
	 * Features are calculated for each spot, using their location, and the raw
	 * image. Features to be calculated and analyzers are taken from the
	 * settings field of this object.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, the {@link Logger} of the model will be
	 *            notified.
	 * @return <code>true</code> if the calculation was performed successfully,
	 *         <code>false</code> otherwise.
	 */
	public boolean computeSpotFeatures( final boolean doLogIt )
	{
		final Logger logger = model.getLogger();
		logger.log( "Computing spot features.\n" );
		final SpotFeatureCalculator calculator = new SpotFeatureCalculator( model, settings );
		calculator.setNumThreads( numThreads );
		if ( calculator.checkInput() && calculator.process() )
		{
			if ( doLogIt )
				logger.log( "Computation done in " + calculator.getProcessingTime() + " ms.\n" );
			return true;
		}

		errorMessage = "Spot features calculation failed:\n" + calculator.getErrorMessage();
		return false;
	}

	/**
	 * Calculate all features for all detected spots.
	 * <p>
	 * Features are calculated for each spot, using their location, and the raw
	 * image. Features to be calculated and analyzers are taken from the
	 * settings field of this object.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, the {@link Logger} of the model will be
	 *            notified.
	 * @return <code>true</code> if the calculation was performed successfuly,
	 *         <code>false</code> otherwise.
	 */
	public boolean computeEdgeFeatures( final boolean doLogIt )
	{
		final Logger logger = model.getLogger();
		final EdgeFeatureCalculator calculator = new EdgeFeatureCalculator( model, settings );
		calculator.setNumThreads( numThreads );
		if ( !calculator.checkInput() || !calculator.process() )
		{
			errorMessage = "Edge features calculation failed:\n" + calculator.getErrorMessage();
			return false;
		}
		if ( doLogIt )
		{
			logger.log( "Computation done in " + calculator.getProcessingTime() + " ms.\n" );
		}
		return true;
	}

	/**
	 * Calculate all features for all tracks.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, messages will be sent to the logger.
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean computeTrackFeatures( final boolean doLogIt )
	{
		final Logger logger = model.getLogger();
		final TrackFeatureCalculator calculator = new TrackFeatureCalculator( model, settings );
		calculator.setNumThreads( numThreads );
		if ( calculator.checkInput() && calculator.process() )
		{
			if ( doLogIt )
				logger.log( "Computation done in " + calculator.getProcessingTime() + " ms.\n" );
			return true;
		}

		errorMessage = "Track features calculation failed:\n" + calculator.getErrorMessage();
		return false;
	}

	/**
	 * Execute the tracking part.
	 * <p>
	 * This method links all the selected spots from the thresholding part using
	 * the selected tracking algorithm. This tracking process will generate a
	 * graph (more precisely a {@link org.jgrapht.graph.SimpleWeightedGraph})
	 * made of the spot election for its vertices, and edges representing the
	 * links.
	 * <p>
	 * The {@link ModelChangeListener}s of the model will be notified when the
	 * successful process is over.
	 *
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execTracking()
	{
		final Logger logger = model.getLogger();
		logger.log( "Starting tracking process.\n" );
		final SpotTracker tracker = settings.trackerFactory.create( model.getSpots(), settings.trackerSettings );
		tracker.setNumThreads( numThreads );
		tracker.setLogger( logger );
		if ( tracker.checkInput() && tracker.process() )
		{
			model.setTracks( tracker.getResult(), true );
			return true;
		}

		errorMessage = "Tracking process failed:\n" + tracker.getErrorMessage();
		return false;
	}

	/**
	 * Execute the detection part.
	 * <p>
	 * This method configure the chosen {@link Settings#detectorFactory} with
	 * the source image and the detectr settings and execute the detection
	 * process for all the frames set in the {@link Settings} object of the
	 * target model.
	 *
	 * @return true if the whole detection step has executed correctly.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean execDetection() {
		final Logger logger = model.getLogger();
		logger.log(("Starting detection process using " + (numThreads > 1 ? numThreads + " threads" : "1 thread")) + ".\n");
		final SpotDetectorFactoryBase<?> factory = settings.detectorFactory;
		if (null == factory) {
			errorMessage = "Detector factory is null.\n";
			return false;
		}
		if (null == settings.detectorSettings) {
			errorMessage = "Detector settings is null.\n";
			return false;
		}
		if (factory instanceof ManualDetectorFactory) {
			// Skip detection (don't delete anything) if we received this factory.
			return true;
		}
		/* Prepare interval */
		final ImgPlus img = TMUtils.rawWraps(settings.imp);
		if (!factory.setTarget(img, settings.detectorSettings)) {
			errorMessage = factory.getErrorMessage();
			return false;
		}
		/* Separate frame-by-frame or global detection depending on the factory type. */
		if (factory instanceof SpotGlobalDetectorFactory) {
			return processGlobal(((SpotGlobalDetectorFactory) (factory)), img, logger);
		} else if (factory instanceof SpotDetectorFactory) {
			return processFrameByFrame(((SpotDetectorFactory) (factory)), img, logger);
		}
		errorMessage = "Don't know how to handle detector factory of type: " + factory.getClass();
		return false;
	}

	@SuppressWarnings( "rawtypes" )
	private boolean processGlobal( final SpotGlobalDetectorFactory factory, final ImgPlus img, final Logger logger )
	{
		final Interval interval = TMUtils.getIntervalWithTime( img, settings );

		// To translate spots, later
		final double[] calibration = TMUtils.getSpatialCalibration( settings.imp );

		final SpotGlobalDetector< ? > detector = factory.getDetector( interval );
		if ( detector instanceof MultiThreaded )
		{
			final MultiThreaded md = ( MultiThreaded ) detector;
			md.setNumThreads( numThreads );
		}
		
		// Execute detection
		logger.setStatus( "Detection..." );
		if ( detector.checkInput() && detector.process() )
		{
			final SpotCollection rawSpots = detector.getResult();
			rawSpots.setNumThreads( numThreads );

			/*
			 * Filter out spots not in the ROI.
			 */
			final SpotCollection spots;
			if ( settings.roi != null )
			{
				spots = new SpotCollection();
				spots.setNumThreads( numThreads );
				for ( int frame = settings.tstart; frame <= settings.tend; frame++ )
				{
					for ( final Spot spot : rawSpots.iterable( frame, false ) )
					{
						final List< Spot > spotsThisFrame = new ArrayList<>();
						if ( settings.roi.contains(
								( int ) Math.round( spot.getFeature( Spot.POSITION_X ) / calibration[ 0 ] ),
								( int ) Math.round( spot.getFeature( Spot.POSITION_Y ) / calibration[ 1 ] ) ) )
						{
							spotsThisFrame.add( spot );
						}
						spots.put( frame, spotsThisFrame );
					}
				}
			}
			else
			{
				spots = rawSpots;
			}

			// Add detection feature other than position
			for ( final Spot spot : spots.iterable( false ) )
				spot.putFeature( Spot.POSITION_T, spot.getFeature( Spot.FRAME ) * settings.dt );

			model.setSpots( spots, true );
			logger.setStatus( "" );
			logger.log( "Found " + spots.getNSpots( false ) + " spots.\n" );
		}
		else
		{
			// Fail: exit and report error.
			errorMessage = detector.getErrorMessage();
			return false;
		}

		return true;
	}

	@SuppressWarnings("rawtypes")
	private boolean processFrameByFrame(final SpotDetectorFactory factory, final ImgPlus img, final Logger logger) {
		final Interval interval = TMUtils.getInterval(img, settings);
		final int zindex = TMUtils.findZAxisIndex(img);
		if (!factory.setTarget(img, settings.detectorSettings)) {
			errorMessage = factory.getErrorMessage();
			return false;
		}
		final int numFrames = (settings.tend - settings.tstart) + 1;
		// Final results holder, for all frames
		final SpotCollection spots = new SpotCollection();
		spots.setNumThreads(numThreads);
		// To report progress
		final AtomicInteger spotFound = new AtomicInteger(0);
		final AtomicInteger progress = new AtomicInteger(0);
		// To translate spots, later
		final double[] calibration = TMUtils.getSpatialCalibration(settings.imp);
		/*
		 * Fine tune multi-threading: If we have 10 threads and 15 frames to
		 * process, we process 10 frames at once, and allocate 1 thread per
		 * frame. But if we have 10 threads and 2 frames, we process the 2
		 * frames at once, and allocate 5 threads per frame if we can.
		 */
		final int nSimultaneousFrames = (factory.forbidMultithreading()) ? 1 : Math.min(numThreads, numFrames);
		final int threadsPerFrame = Math.max(1, numThreads / nSimultaneousFrames);
		logger.log(((("Detection processes " + (nSimultaneousFrames > 1 ? nSimultaneousFrames + " frames" : "1 frame")) + " simultaneously and allocates ") + (threadsPerFrame > 1 ? threadsPerFrame + " threads" : "1 thread")) + " per frame.\n");
		final Thread[] threads = SimpleMultiThreading.newThreads(nSimultaneousFrames);
		final AtomicBoolean ok = new AtomicBoolean(true);
		// Prepare the thread array
		final AtomicInteger ai = new AtomicInteger(settings.tstart);
		for (int ithread = 0; ithread < threads.length; ithread++) {
			threads[ithread] = new Thread((("TrackMate spot detection thread " + (1 + ithread)) + "/") + threads.length) {
				private boolean wasInterrupted()
				{
					try
					{
						if ( isInterrupted() )
							return true;
						sleep( 0 );
						return false;
					}
					catch ( final InterruptedException e )
					{
						return true;
					}
				}

				@Override
				public void run() {
					for (int frame = ai.getAndIncrement(); frame <= settings.tend; frame = ai.getAndIncrement()) {
						try {
							// Yield detector for target frame
							final SpotDetector<?> detector = factory.getDetector(interval, frame);
							if (detector instanceof MultiThreaded) {
								final MultiThreaded md = ((MultiThreaded) (detector));
								md.setNumThreads(threadsPerFrame);
							}
							if (wasInterrupted()) {
								return;
							}
							// Execute detection
							if ((ok.get() && detector.checkInput()) && detector.process()) {
								// On success, get results.
								final List<Spot> spotsThisFrame = detector.getResult();
								/*
								 * Special case: if we have a single column
								 * image, then the detectors internally dealt
								 * with a single line image. We need to permute
								 * back the X & Y coordinates if it's the case.
								 */
								if ((img.dimension(0) < 2) && (zindex < 0)) {
									for (final Spot spot : spotsThisFrame) {
										spot.putFeature(Spot.POSITION_Y, spot.getDoublePosition(0));
										spot.putFeature(Spot.POSITION_X, 0.0);
									}
								}
								List<Spot> prunedSpots;
								if (settings.roi != null) {
									prunedSpots = new ArrayList<>();
									for (final Spot spot : spotsThisFrame) {
										if (settings.roi.contains(((int) (Math.round(spot.getFeature(Spot.POSITION_X) / calibration[0]))), ((int) (Math.round(spot.getFeature(Spot.POSITION_Y) / calibration[1]))))) {
											prunedSpots.add(spot);
										}
									}
								} else {
									prunedSpots = spotsThisFrame;
								}
								// Add detection feature other than position
								for (final Spot spot : prunedSpots) {
									// FRAME will be set upon adding to
									// SpotCollection.
									spot.putFeature(Spot.POSITION_T, frame * settings.dt);
								}
								// Store final results for this frame
								spots.put(frame, prunedSpots);
								// Report
								spotFound.addAndGet(prunedSpots.size());
								logger.setProgress(progress.incrementAndGet() / ((double) (numFrames)));
							} else {
								// Fail: exit and report error.
								ok.set(false);
								errorMessage = detector.getErrorMessage();
								return;
							}
						} catch (final java.lang.RuntimeException e) {
							final Throwable cause = e.getCause();
							if ((cause != null) && (cause instanceof InterruptedException)) {
								return;
							}
							throw e;
						}
					}
				}
			};
		}
		logger.setStatus("Detection...");
		logger.setProgress(0);
		try {
			SimpleMultiThreading.startAndJoin(threads);
		} catch (final java.lang.RuntimeException e) {
			ok.set(false);
			if ((e.getCause() != null) && (e.getCause() instanceof InterruptedException)) {
				errorMessage = "Detection workers interrupted.\n";
				for (final Thread thread : threads) {
					thread.interrupt();
				}
				for (final Thread thread : threads) {
					if (thread.isAlive()) {
						try {
							thread.join();
						} catch (final java.lang.InterruptedException e2) {
							// ignore
						}
					}
				}
			} else {
				throw e;
			}
		}
		model.setSpots(spots, true);
		if (ok.get()) {
			logger.log(("Found " + spotFound.get()) + " spots.\n");
		} else {
			logger.error((("Detection failed after " + progress.get()) + " frames:\n") + errorMessage);
			logger.log(("Found " + spotFound.get()) + " spots prior failure.\n");
		}
		logger.setProgress(1);
		logger.setStatus("");
		return ok.get();
	}

	/**
	 * Execute the initial spot filtering part.
	 * <p>
	 * Because of the presence of noise, it is possible that some of the
	 * regional maxima found in the detection step have identified noise, rather
	 * than objects of interest. This can generates a very high number of spots,
	 * which is inconvenient to deal with when it comes to computing their
	 * features, or displaying them.
	 * <p>
	 * Any {@link SpotDetector} is expected to at least compute the
	 * {@link Spot#QUALITY} value for each spot it creates, so it is possible to
	 * set up an initial filtering on this feature, prior to any other
	 * operation.
	 * <p>
	 * This method simply takes all the detected spots, and discard those whose
	 * quality value is below the threshold set by
	 * {@link Settings#initialSpotFilterValue}. The spot field is overwritten,
	 * and discarded spots can't be recalled.
	 * <p>
	 * The {@link ModelChangeListener}s of this model will be notified with a
	 * {@link ModelChangeEvent#SPOTS_COMPUTED} event.
	 *
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execInitialSpotFiltering()
	{
		final Logger logger = model.getLogger();
		logger.log( "Starting initial filtering process.\n" );

		final Double initialSpotFilterValue = settings.initialSpotFilterValue;
		final FeatureFilter featureFilter = new FeatureFilter( Spot.QUALITY, initialSpotFilterValue, true );

		SpotCollection spots = model.getSpots();
		spots.filter( featureFilter );

		spots = spots.crop();

		model.setSpots( spots, true ); // Forget about the previous one
		return true;
	}

	/**
	 * Execute the spot feature filtering part.
	 * <p>
	 * Because of the presence of noise, it is possible that some of the
	 * regional maxima found in the detection step have identified noise, rather
	 * than objects of interest. A filtering operation based on the calculated
	 * features in this step should allow to rule them out.
	 * <p>
	 * This method simply takes all the detected spots, and mark as visible the
	 * spots whose features satisfy all of the filters in the {@link Settings}
	 * object.
	 * <p>
	 * The {@link ModelChangeListener}s of this model will be notified with a
	 * {@link ModelChangeEvent#SPOTS_FILTERED} event.
	 *
	 * @param doLogIt
	 *            if <code>true</code>, will send a message to the model logger.
	 * @return <code>true</code> if the computation completed without errors.
	 */
	public boolean execSpotFiltering( final boolean doLogIt )
	{
		if ( doLogIt )
		{
			final Logger logger = model.getLogger();
			logger.log( "Starting spot filtering process.\n" );
		}
		model.filterSpots( settings.getSpotFilters(), true );
		return true;
	}

	public boolean execTrackFiltering( final boolean doLogIt )
	{
		if ( doLogIt )
		{
			final Logger logger = model.getLogger();
			logger.log( "Starting track filtering process.\n" );
		}

		model.beginUpdate();
		try
		{
			for ( final Integer trackID : model.getTrackModel().trackIDs( false ) )
			{
				boolean trackIsOk = true;
				for ( final FeatureFilter filter : settings.getTrackFilters() )
				{
					final Double tval = filter.value;
					final Double val = model.getFeatureModel().getTrackFeature( trackID, filter.feature );
					if ( null == val )
						continue;

					if ( filter.isAbove )
					{
						if ( val < tval )
						{
							trackIsOk = false;
							break;
						}
					}
					else
					{
						if ( val > tval )
						{
							trackIsOk = false;
							break;
						}
					}
				}
				model.setTrackVisibility( trackID, trackIsOk );
			}
		}
		finally
		{
			model.endUpdate();
		}
		return true;
	}

	@Override
	public String toString()
	{
		return name;
	}

	/*
	 * ALGORITHM METHODS
	 */

	@Override
	public boolean checkInput()
	{
		if ( null == model )
		{
			errorMessage = "The model is null.\n";
			return false;
		}
		if ( null == settings )
		{
			errorMessage = "Settings are null";
			return false;
		}
		if ( !settings.checkValidity() )
		{
			errorMessage = settings.getErrorMessage();
			return false;
		}
		return true;
	}

	@Override
	public String getErrorMessage()
	{
		return errorMessage;
	}

	@Override
	public boolean process()
	{
		if ( !execDetection() ) { return false; }

		if ( !execInitialSpotFiltering() ) { return false; }

		if ( !computeSpotFeatures( true ) ) { return false; }

		if ( !execSpotFiltering( true ) ) { return false; }

		if ( !execTracking() ) { return false; }

		if ( !computeEdgeFeatures( true ) ) { return false; }

		if ( !computeTrackFeatures( true ) ) { return false; }

		if ( !execTrackFiltering( true ) ) { return false; }


		return true;
	}

	@Override
	public int getNumThreads()
	{
		return numThreads;
	}

	@Override
	public void setNumThreads()
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;
	}

	@Override
	public long getProcessingTime()
	{
		return processingTime;
	}

	// --- org.scijava.Named methods ---

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName( final String name )
	{
		this.name = name;
	}
}