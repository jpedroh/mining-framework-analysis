/*
 * #%L
 * BigDataViewer core classes with minimal dependencies
 * %%
 * Copyright (C) 2012 - 2016 Tobias Pietzsch, Stephan Saalfeld, Stephan Preibisch,
 * Jean-Yves Tinevez, HongKee Moon, Johannes Schindelin, Curtis Rueden, John Bogovic
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package bdv.viewer;

import bdv.BigDataViewerActions;
import bdv.cache.CacheControl;
import bdv.tools.bookmarks.bookmark.Bookmark;
import bdv.tools.bookmarks.bookmark.DynamicBookmark;
import bdv.util.Affine3DHelpers;
import bdv.util.InvokeOnEDT;
import bdv.util.Prefs;
import bdv.viewer.animate.AbstractTransformAnimator;
import bdv.viewer.animate.MessageOverlayAnimator;
import bdv.viewer.animate.OverlayAnimator;
import bdv.viewer.animate.RotationAnimator;
import bdv.viewer.animate.TextOverlayAnimator.TextPosition;
import bdv.viewer.animate.TextOverlayAnimator;
import bdv.viewer.overlay.MultiBoxOverlayRenderer;
import bdv.viewer.overlay.ScaleBarOverlayRenderer;
import bdv.viewer.overlay.SourceInfoOverlayRenderer;
import bdv.viewer.render.MultiResolutionRenderer;
import bdv.viewer.render.TransformAwareBufferedImageOverlayRenderer;
import bdv.viewer.state.SourceGroup;
import bdv.viewer.state.SourceState;
import bdv.viewer.state.ViewerState;
import bdv.viewer.state.XmlIoViewerState;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.imglib2.Positionable;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.InteractiveDisplayCanvasComponent;
import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.PainterThread;
import net.imglib2.ui.TransformEventHandler;
import net.imglib2.ui.TransformListener;
import net.imglib2.util.LinAlgHelpers;
import org.jdom2.Element;
import static bdv.viewer.VisibilityAndGrouping.Event.CURRENT_SOURCE_CHANGED;
import static bdv.viewer.VisibilityAndGrouping.Event.DISPLAY_MODE_CHANGED;
import static bdv.viewer.VisibilityAndGrouping.Event.GROUP_ACTIVITY_CHANGED;
import static bdv.viewer.VisibilityAndGrouping.Event.GROUP_NAME_CHANGED;
import static bdv.viewer.VisibilityAndGrouping.Event.NUM_GROUPS_CHANGED;
import static bdv.viewer.VisibilityAndGrouping.Event.NUM_SOURCES_CHANGED;
import static bdv.viewer.VisibilityAndGrouping.Event.SOURCE_ACTVITY_CHANGED;
import static bdv.viewer.VisibilityAndGrouping.Event.VISIBILITY_CHANGED;


/**
 * A JPanel for viewing multiple of {@link Source}s. The panel contains a
 * {@link InteractiveDisplayCanvasComponent canvas} and a time slider (if there
 * are multiple time-points). Maintains a {@link ViewerState render state}, the
 * renderer, and basic navigation help overlays. It has it's own
 * {@link PainterThread} for painting, which is started on construction (use
 * {@link #stop() to stop the PainterThread}.
 *
 * @author Tobias Pietzsch &lt;tobias.pietzsch@gmail.com&gt;
 */
public class ViewerPanel extends JPanel implements OverlayRenderer , TransformListener<AffineTransform3D> , PainterThread.Paintable , VisibilityAndGrouping.UpdateListener , RequestRepaint {
	private static final long serialVersionUID = 1L;

	/**
	 * Currently rendered state (visible sources, transformation, timepoint,
	 * etc.) A copy can be obtained by {@link #getState()}.
	 */
	protected final ViewerState state;

	/**
	 * Renders the current state for the {@link #display}.
	 */
	protected final MultiResolutionRenderer imageRenderer;

	/**
	 * TODO
	 */
	protected final TransformAwareBufferedImageOverlayRenderer renderTarget;

	// TODO: move to specialized class
	/**
	 * Overlay navigation boxes.
	 */
	// TODO: move to specialized class
	protected final MultiBoxOverlayRenderer multiBoxOverlayRenderer;

	// TODO: move to specialized class
	/**
	 * Overlay current source name and current timepoint.
	 */
	// TODO: move to specialized class
	protected final SourceInfoOverlayRenderer sourceInfoOverlayRenderer;

	/**
	 * TODO
	 */
	protected final ScaleBarOverlayRenderer scaleBarOverlayRenderer;

	/**
	 * Transformation set by the interactive viewer.
	 */
	protected final AffineTransform3D viewerTransform;

	/**
	 * Canvas used for displaying the rendered {@link #renderTarget image} and
	 * overlays.
	 */
	protected final InteractiveDisplayCanvasComponent< AffineTransform3D > display;

	protected final JSlider timeSlider;

	/**
	 * A {@link ThreadGroup} for (only) the threads used by this
	 * {@link ViewerPanel}, that is, {@link #painterThread} and
	 * {@link #renderingExecutorService}.
	 */
	protected ThreadGroup threadGroup;

	/**
	 * Thread that triggers repainting of the display.
	 */
	protected final PainterThread painterThread;

	/**
	 * The {@link ExecutorService} used for rendereing.
	 */
	protected final ExecutorService renderingExecutorService;

	/**
	 * Keeps track of the current mouse coordinates, which are used to provide
	 * the current global position (see {@link #getGlobalMouseCoordinates(RealPositionable)}).
	 */
	protected final MouseCoordinateListener mouseCoordinates;

	/**
	 * Manages visibility and currentness of sources and groups, as well as
	 * grouping of sources, and display mode.
	 */
	protected final VisibilityAndGrouping visibilityAndGrouping;

	/**
	 * These listeners will be notified about changes to the
	 * {@link #viewerTransform}. This is done <em>before</em> calling
	 * {@link #requestRepaint()} so listeners have the chance to interfere.
	 */
	protected final CopyOnWriteArrayList< TransformListener< AffineTransform3D > > transformListeners;

	/**
	 * These listeners will be notified about changes to the
	 * {@link #viewerTransform} that was used to render the current image. This
	 * is intended for example for {@link OverlayRenderer}s that need to exactly
	 * match the transform of their overlaid content to the transform of the
	 * image.
	 */
	protected final CopyOnWriteArrayList< TransformListener< AffineTransform3D > > lastRenderTransformListeners;

	/**
	 * These listeners will be notified about changes to the current timepoint
	 * {@link ViewerState#getCurrentTimepoint()}. This is done <em>before</em>
	 * calling {@link #requestRepaint()} so listeners have the chance to
	 * interfere.
	 */
	protected final CopyOnWriteArrayList< TimePointListener > timePointListeners;

	protected final CopyOnWriteArrayList< InterpolationModeListener > interpolationModeListeners;

	/**
	 * Current animator for viewer transform, or null. This is for example used
	 * to make smooth transitions when {@link #align(AlignPlane) aligning to
	 * orthogonal planes}.
	 */
	protected AbstractTransformAnimator currentAnimator = null;

	/**
	 * A list of currently incomplete (see {@link OverlayAnimator#isComplete()})
	 * animators. Initially, this contains a {@link TextOverlayAnimator} showing
	 * the "press F1 for help" message.
	 */
	protected final ArrayList<OverlayAnimator> overlayAnimators;

	/**
	 * Fade-out overlay of recent messages. See {@link #showMessage(String)}.
	 */
	protected final MessageOverlayAnimator msgOverlay;

	protected final ViewerOptions.Values options;

	protected final JButton previousKeyframeButton;

	protected final JButton addKeyframeButton;

	protected final JButton nextKeyframeButton;

	protected final List<ActiveBookmarkChangedListener> activeBookmarkChangedListeners = new ArrayList<>();

	protected final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

	protected final Timer playTimer = new Timer(0, this::onPlayTimerTick);

	protected final ActionMap actionMap;

	public ViewerPanel(final List<SourceAndConverter<?>> sources, final int numTimePoints, final CacheControl cacheControl, final ActionMap actionMap) {
		this(sources, numTimePoints, cacheControl, ViewerOptions.options(), actionMap);
	}

	/**
	 *
	 *
	 * @param sources
	 * 		the {@link SourceAndConverter sources} to display.
	 * @param numTimepoints
	 * 		number of available timepoints.
	 * @param cacheControl
	 * 		to control IO budgeting and fetcher queue.
	 * @param optional
	 * 		optional parameters. See {@link ViewerOptions#options()}.
	 * @param optional
	 * 		optional parameters. See {@link ViewerOptions#options()}.
	 */
	public ViewerPanel(final List<SourceAndConverter<?>> sources, final int numTimepoints, final CacheControl cacheControl, final ViewerOptions optional, final ActionMap actionMap) {
		super(new BorderLayout(), false);
		setPreferredSize(new Dimension(600, 500));
		options = optional.values;
		this.actionMap = actionMap;
		final int numGroups = options.getNumSourceGroups();
		final ArrayList<SourceGroup> groups = new ArrayList<>(numGroups);
		for (int i = 0; i < numGroups; ++i) {
			groups.add(new SourceGroup("group " + Integer.toString(i + 1)));
		}
		state = new ViewerState(sources, groups, numTimepoints);
		for (int i = Math.min(numGroups, sources.size()) - 1; i >= 0; --i) {
			state.getSourceGroups().get(i).addSource(i);
		}
		if (!sources.isEmpty()) {
			state.setCurrentSource(0);
		}
		multiBoxOverlayRenderer = new MultiBoxOverlayRenderer();
		sourceInfoOverlayRenderer = new SourceInfoOverlayRenderer();
		scaleBarOverlayRenderer = (Prefs.showScaleBar()) ? new ScaleBarOverlayRenderer() : null;
		threadGroup = new ThreadGroup(this.toString());
		painterThread = new PainterThread(threadGroup, this);
		viewerTransform = new AffineTransform3D();
		renderTarget = new TransformAwareBufferedImageOverlayRenderer();
		renderTarget.setCanvasSize(options.getWidth(), options.getHeight());
		renderingExecutorService = Executors.newFixedThreadPool(options.getNumRenderingThreads(), new RenderThreadFactory());
		imageRenderer = new MultiResolutionRenderer(renderTarget, painterThread, options.getScreenScales(), options.getTargetRenderNanos(), options.isDoubleBuffered(), options.getNumRenderingThreads(), renderingExecutorService, options.isUseVolatileIfAvailable(), options.getAccumulateProjectorFactory(), cacheControl);
		mouseCoordinates = new MouseCoordinateListener();
		display = new InteractiveDisplayCanvasComponent<>(options.getWidth(), options.getHeight(), options.getTransformEventHandlerFactory());
		add(display, BorderLayout.CENTER);
		display.addTransformListener(this);
		display.addOverlayRenderer(renderTarget);
		display.addOverlayRenderer(this);
		display.addHandler(mouseCoordinates);
		display.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				requestRepaint();
				display.removeComponentListener(this);
			}
		});
		JPanel sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));
		sliderPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		previousKeyframeButton = new JButton("<<");
		previousKeyframeButton.setToolTipText("Go to previous keyframe");
		previousKeyframeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Action action = actionMap.get(BigDataViewerActions.PREVIOUS_KEYFRAME);
				if (action != null) {
					action.actionPerformed(e);
				}
			}
		});
		sliderPanel.add(previousKeyframeButton);
		sliderPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		addKeyframeButton = new JButton("+");
		addKeyframeButton.setToolTipText("Add a new keyframe");
		addKeyframeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Action action = actionMap.get(BigDataViewerActions.ADD_KEYFRAME);
				if (action != null) {
					action.actionPerformed(e);
				}
			}
		});
		sliderPanel.add(addKeyframeButton);
		sliderPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		nextKeyframeButton = new JButton(">>");
		nextKeyframeButton.setToolTipText("Go to next keyframe");
		nextKeyframeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final Action action = actionMap.get(BigDataViewerActions.NEXT_KEYFRAME);
				if (action != null) {
					action.actionPerformed(e);
				}
			}
		});
		sliderPanel.add(nextKeyframeButton);
		sliderPanel.add(Box.createRigidArea(new Dimension(5, 5)));
		sliderPlay = new JPlaySlider();
		sliderPlay.setPreferredSize(new Dimension(200, 50));
		sliderPlay.setMinimumSize(new Dimension(200, 50));
		sliderPlay.setMaximumSize(new Dimension(200, 50));
		sliderPlay.setAlignmentX(Component.LEFT_ALIGNMENT);
		sliderPanel.add(sliderPlay);
		sliderPlay.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				// if ( !e.getSource().equals( sliderPlay ) )
				// return;
				if (sliderPlay.getValue() == 0) {
					stopPlayExecuter();
					return;
				}
				final int periode = 1000 / (1 * Math.abs(sliderPlay.getValue()));
				playTimer.setDelay(periode);
				playTimer.start();
			}
		});
		sliderPlay.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				stopPlayExecuter();
			}
		});
		rigidArea = Box.createRigidArea(new Dimension(5, 5));
		sliderPanel.add(rigidArea);
		timeKeyframePanel = new JPanel();
		timeKeyframePanel.setBorder(new EmptyBorder(5, 0, 5, 0));
		sliderPanel.add(timeKeyframePanel);
		timeKeyframePanel.setLayout(new BoxLayout(timeKeyframePanel, BoxLayout.Y_AXIS));
		timeSlider = new JSlider(0, numTimepoints - 1, 0);
		timeSlider.setMinimumSize(new Dimension(36, 26));
		timeSlider.setMaximumSize(new Dimension(32767, 26));
		timeKeyframePanel.add(timeSlider);
		keyframePanel = new JKeyFramePanel(timeSlider);
		timeKeyframePanel.add(keyframePanel);
		timeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent e) {
				if (e.getSource().equals(timeSlider)) {
					setTimepoint(timeSlider.getValue());
				}
			}
		});
		add(display, BorderLayout.CENTER);
		if (numTimepoints > 1) {
			add(sliderPanel, BorderLayout.SOUTH);
		}
		visibilityAndGrouping = new VisibilityAndGrouping(state);
		visibilityAndGrouping.addUpdateListener(this);
		transformListeners = new CopyOnWriteArrayList<>();
		lastRenderTransformListeners = new CopyOnWriteArrayList<>();
		timePointListeners = new CopyOnWriteArrayList<>();
		interpolationModeListeners = new CopyOnWriteArrayList<>();
		msgOverlay = options.getMsgOverlay();
		overlayAnimators = new ArrayList<>();
		overlayAnimators.add(msgOverlay);
		overlayAnimators.add(new TextOverlayAnimator("Press <F1> for help.", 3000, TextPosition.CENTER));
		display.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				requestRepaint();
				display.removeComponentListener(this);
			}
		});
		painterThread.start();
	}

	public void stopPlayExecuter() {
		playTimer.stop();
	}

	private void onPlayTimerTick(ActionEvent event) {
		final int changeValue = Integer.signum(sliderPlay.getValue());
		final int newTimepoint = state.getCurrentTimepoint() + changeValue;
		final int numTimepoints = state.getNumTimepoints();
		
		if(newTimepoint<0){
			  setTimepoint(0);
			  stopPlayExecuter();
			  sliderPlay.setValue(0);
		  }
		  else if(newTimepoint > numTimepoints - 1){
			  setTimepoint(numTimepoints -1);
			  stopPlayExecuter();
			  sliderPlay.setValue(0);
		  }
		  else{
			  setTimepoint(newTimepoint);
		  }
	}

	public void setKeyframeButtonEnable(boolean enable){
		previousKeyframeButton.setEnabled(enable);
		addKeyframeButton.setEnabled(enable);
		nextKeyframeButton.setEnabled(enable);
	}

	public KeyFramePopupMenu getKeyFramePopupMenu(){
		return this.keyframePanel.getKeyFramePopupMenuPopupMenu();
	}

	public void addSource( final SourceAndConverter< ? > sourceAndConverter )
	{
		synchronized ( visibilityAndGrouping )
		{
			state.addSource( sourceAndConverter );
			visibilityAndGrouping.update( NUM_SOURCES_CHANGED );
		}
		requestRepaint();
	}

	public void removeSource( final Source< ? > source )
	{
		synchronized ( visibilityAndGrouping )
		{
			state.removeSource( source );
			visibilityAndGrouping.update( NUM_SOURCES_CHANGED );
		}
		requestRepaint();
	}

	public void addGroup( final SourceGroup group )
	{
		synchronized ( visibilityAndGrouping )
		{
			state.addGroup( group );
			visibilityAndGrouping.update( NUM_GROUPS_CHANGED );
		}
		requestRepaint();
	}

	public void removeGroup(final SourceGroup group) {
		synchronized(visibilityAndGrouping) {
			state.removeGroup(group);
			visibilityAndGrouping.update(NUM_GROUPS_CHANGED);
		}
		requestRepaint();
	}

	/**
	 * Set {@code gPos} to the display coordinates at gPos transformed into the
	 * global coordinate system.
	 *
	 * @param gPos
	 *            is set to the corresponding global coordinates.
	 */
	public < P extends RealLocalizable & RealPositionable > void displayToGlobalCoordinates( final double[] gPos )
	{
		assert gPos.length >= 3;

		viewerTransform.applyInverse( gPos, gPos );
	}

	/**
	 * Set {@code gPos} to the display coordinates at gPos transformed into the
	 * global coordinate system.
	 *
	 * @param gPos
	 *            is set to the corresponding global coordinates.
	 */
	public < P extends RealLocalizable & RealPositionable > void displayToGlobalCoordinates( final P gPos )
	{
		assert gPos.numDimensions() >= 3;

		viewerTransform.applyInverse( gPos, gPos );
	}

	/**
	 * Set {@code gPos} to the display coordinates (x,y,0)<sup>T</sup> transformed into the
	 * global coordinate system.
	 *
	 * @param gPos
	 *            is set to the global coordinates at display (x,y,0)<sup>T</sup>.
	 */
	public void displayToGlobalCoordinates( final double x, final double y, final RealPositionable gPos )
	{
		assert gPos.numDimensions() >= 3;
		final RealPoint lPos = new RealPoint( 3 );
		lPos.setPosition( x, 0 );
		lPos.setPosition( y, 1 );
		viewerTransform.applyInverse( gPos, lPos );
	}

	/**
	 * Set {@code gPos} to the current mouse coordinates transformed into the
	 * global coordinate system.
	 *
	 * @param gPos
	 *            is set to the current global coordinates.
	 */
	public void getGlobalMouseCoordinates( final RealPositionable gPos )
	{
		assert gPos.numDimensions() == 3;
		final RealPoint lPos = new RealPoint( 3 );
		mouseCoordinates.getMouseCoordinates( lPos );
		viewerTransform.applyInverse( gPos, lPos );
	}

	/**
	 * TODO
	 * @param p
	 */
	public synchronized void getMouseCoordinates( final Positionable p )
	{
		assert p.numDimensions() == 2;
		mouseCoordinates.getMouseCoordinates( p );
	}

	@Override
	public void paint()
	{
		imageRenderer.paint( state );

		display.repaint();

		synchronized ( this )
		{
			if ( currentAnimator != null )
			{
				final TransformEventHandler< AffineTransform3D > handler = display.getTransformEventHandler();
				final AffineTransform3D transform = currentAnimator.getCurrent( System.currentTimeMillis() );
				handler.setTransform( transform );
				transformChanged( transform );
				if ( currentAnimator.isComplete() )
					currentAnimator = null;
			}
		}
	}

	/**
	 * Repaint as soon as possible.
	 */
	@Override
	public void requestRepaint()
	{
		imageRenderer.requestRepaint();
	}

	@Override
	public void drawOverlays(final Graphics g) {
		boolean requiresRepaint = false;
		if (Prefs.showMultibox()) {
			multiBoxOverlayRenderer.setViewerState(state);
			multiBoxOverlayRenderer.updateVirtualScreenSize(display.getWidth(), display.getHeight());
			multiBoxOverlayRenderer.paint(((Graphics2D) (g)));
			requiresRepaint = multiBoxOverlayRenderer.isHighlightInProgress();
		}
		if (Prefs.showTextOverlay()) {
			sourceInfoOverlayRenderer.setViewerState(state);
			sourceInfoOverlayRenderer.paint(((Graphics2D) (g)));
			final RealPoint gPos = new RealPoint(3);
			getGlobalMouseCoordinates(gPos);
			final String mousePosGlobalString = String.format("(%6.1f,%6.1f,%6.1f)", gPos.getDoublePosition(0), gPos.getDoublePosition(1), gPos.getDoublePosition(2));
			g.setFont(new Font("Monospaced", Font.PLAIN, 12));
			g.setColor(Color.white);
			g.drawString(mousePosGlobalString, ((int) (g.getClipBounds().getWidth())) - 170, 25);
		}
		if (Prefs.showScaleBar()) {
			scaleBarOverlayRenderer.setViewerState(state);
			scaleBarOverlayRenderer.paint(((Graphics2D) (g)));
		}
		final long currentTimeMillis = System.currentTimeMillis();
		final ArrayList<OverlayAnimator> overlayAnimatorsToRemove = new ArrayList<>();
		for (final OverlayAnimator animator : overlayAnimators) {
			animator.paint(((Graphics2D) (g)), currentTimeMillis);
			requiresRepaint |= animator.requiresRepaint();
			if (animator.isComplete()) {
				overlayAnimatorsToRemove.add(animator);
			}
		}
		overlayAnimators.removeAll(overlayAnimatorsToRemove);
		if (requiresRepaint) {
			display.repaint();
		}
	}

	@Override
	public synchronized void transformChanged( final AffineTransform3D transform )
	{
		viewerTransform.set( transform );
		state.setViewerTransform( transform );
		for ( final TransformListener< AffineTransform3D > l : transformListeners )
			l.transformChanged( viewerTransform );
		requestRepaint();
	}

	@Override
	public void visibilityChanged( final VisibilityAndGrouping.Event e )
	{
		switch ( e.id )
		{
		case CURRENT_SOURCE_CHANGED:
			multiBoxOverlayRenderer.highlight( visibilityAndGrouping.getCurrentSource() );
			display.repaint();
			break;
		case DISPLAY_MODE_CHANGED:
			showMessage( visibilityAndGrouping.getDisplayMode().getName() );
			display.repaint();
			break;
		case GROUP_NAME_CHANGED:
			display.repaint();
			break;
		case SOURCE_ACTVITY_CHANGED:
			// TODO multiBoxOverlayRenderer.highlight() all sources that became visible
			break;
		case GROUP_ACTIVITY_CHANGED:
			// TODO multiBoxOverlayRenderer.highlight() all sources that became visible
			break;
		case VISIBILITY_CHANGED:
			requestRepaint();
			break;
		}
	}

	private final static double c = Math.cos( Math.PI / 4 );

	private Component rigidArea;

	private JPlaySlider sliderPlay;

	private Component rigidArea_1;

	private JPanel timeKeyframePanel;

	private JKeyFramePanel keyframePanel;

	/**
	 * The planes which can be aligned with the viewer coordinate system: XY,
	 * ZY, and XZ plane.
	 */
	public static enum AlignPlane {

		XY("XY", 2, new double[]{ 1, 0, 0, 0 }),
		ZY("ZY", 0, new double[]{ c, 0, -c, 0 }),
		XZ("XZ", 1, new double[]{ c, c, 0, 0 });
		private final String name;

		public String getName() {
			return name;
		}

		/**
		 * rotation from the xy-plane aligned coordinate system to this plane.
		 */
		private final double[] qAlign;

		/**
		 * Axis index. The plane spanned by the remaining two axes will be
		 * transformed to the same plane by the computed rotation and the
		 * "rotation part" of the affine source transform.
		 *
		 * @see Affine3DHelpers#extractApproximateRotationAffine(AffineTransform3D, double[], int)
		 */
		private final int coerceAffineDimension;

		private AlignPlane(final String name, final int coerceAffineDimension, final double[] qAlign) {
			this.name = name;
			this.coerceAffineDimension = coerceAffineDimension;
			this.qAlign = qAlign;
		}
	}

	/**
	 * Align the XY, ZY, or XZ plane of the local coordinate system of the
	 * currently active source with the viewer coordinate system.
	 *
	 * @param plane
	 *            to which plane to align.
	 */
	protected synchronized void align( final AlignPlane plane )
	{
		final SourceState< ? > source = state.getSources().get( state.getCurrentSource() );
		final AffineTransform3D sourceTransform = new AffineTransform3D();
		source.getSpimSource().getSourceTransform( state.getCurrentTimepoint(), 0, sourceTransform );

		final double[] qSource = new double[ 4 ];
		Affine3DHelpers.extractRotationAnisotropic( sourceTransform, qSource );

		final double[] qTmpSource = new double[ 4 ];
		Affine3DHelpers.extractApproximateRotationAffine( sourceTransform, qSource, plane.coerceAffineDimension );
		LinAlgHelpers.quaternionMultiply( qSource, plane.qAlign, qTmpSource );

		final double[] qTarget = new double[ 4 ];
		LinAlgHelpers.quaternionInvert( qTmpSource, qTarget );

		final AffineTransform3D transform = display.getTransformEventHandler().getTransform();
		double centerX;
		double centerY;
		if ( mouseCoordinates.isMouseInsidePanel() )
		{
			centerX = mouseCoordinates.getX();
			centerY = mouseCoordinates.getY();
		}
		else
		{
			centerY = getHeight() / 2.0;
			centerX = getWidth() / 2.0;
		}
		currentAnimator = new RotationAnimator( transform, centerX, centerY, qTarget, 300 );
		currentAnimator.setTime( System.currentTimeMillis() );
		transformChanged( transform );
	}

	public synchronized void setTransformAnimator( final AbstractTransformAnimator animator )
	{
		currentAnimator = animator;
		currentAnimator.setTime( System.currentTimeMillis() );
		requestRepaint();
	}

	/**
	 * Switch to next interpolation mode. (Currently, there are two
	 * interpolation modes: nearest-neighbor and N-linear.)
	 */
	public synchronized void toggleInterpolation() {
		final int i = state.getInterpolation().ordinal();
		final int n = Interpolation.values().length;
		final Interpolation mode = Interpolation.values()[(i + 1) % n];
		setInterpolation(mode);
	}

	/**
	 * Set the {@link Interpolation} mode.
	 */
	public synchronized void setInterpolation(final Interpolation mode) {
		final Interpolation interpolation = state.getInterpolation();
		if (mode != interpolation) {
			state.setInterpolation(mode);
			showMessage(mode.getName());
			for (final InterpolationModeListener l : interpolationModeListeners) {
				l.interpolationModeChanged(state.getInterpolation());
			}
			requestRepaint();
		}
	}

	/**
	 * Set the {@link DisplayMode}.
	 */
	public synchronized void setDisplayMode( final DisplayMode displayMode )
	{
		visibilityAndGrouping.setDisplayMode( displayMode );
	}

	/**
	 * Set the viewer transform.
	 */
	public synchronized void setCurrentViewerTransform( final AffineTransform3D viewerTransform )
	{
		display.getTransformEventHandler().setTransform( viewerTransform );
		transformChanged( viewerTransform );
	}

	/**
	 * Show the specified time-point.
	 *
	 * @param timepoint
	 *            time-point index.
	 */
	public synchronized void setTimepoint(final int timepoint) {
		if (state.getCurrentTimepoint() != timepoint) {
			state.setCurrentTimepoint(timepoint);
			timeSlider.setValue(timepoint);
			for (final TimePointListener l : timePointListeners) {
				l.timePointChanged(timepoint);
			}
			requestRepaint();
		}
	}

	/**
	 * Show the next time-point.
	 */
	public synchronized void nextTimePoint() {
		if (state.getNumTimepoints() > 1) {
			timeSlider.setValue(timeSlider.getValue() + 1);
		}
	}

	/**
	 * Show the previous time-point.
	 */
	public synchronized void previousTimePoint() {
		if (state.getNumTimepoints() > 1) {
			timeSlider.setValue(timeSlider.getValue() - 1);
		}
	}

	/**
	 * Set the number of available timepoints. If {@code numTimepoints == 1}
	 * this will hide the time slider, otherwise show it. If the currently
	 * displayed timepoint would be out of range with the new number of
	 * timepoints, the current timepoint is set to {@code numTimepoints - 1}.
	 *
	 * @param numTimepoints
	 *            number of available timepoints. Must be {@code >= 1}.
	 */
	public void setNumTimepoints( final int numTimepoints )
	{
		try
		{
			InvokeOnEDT.invokeAndWait( () -> setNumTimepointsSynchronized( numTimepoints ) );
		}
		catch ( InvocationTargetException | InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	private synchronized void setNumTimepointsSynchronized(final int numTimepoints) {
		if ((numTimepoints < 1) || (state.getNumTimepoints() == numTimepoints)) {
			return;
		} else if ((numTimepoints == 1) && (state.getNumTimepoints() > 1)) {
			remove(timeSlider);
		} else if ((numTimepoints > 1) && (state.getNumTimepoints() == 1)) {
			add(timeSlider, BorderLayout.SOUTH);
		}
		state.setNumTimepoints(numTimepoints);
		if (state.getCurrentTimepoint() >= numTimepoints) {
			final int timepoint = numTimepoints - 1;
			state.setCurrentTimepoint(timepoint);
			for (final TimePointListener l : timePointListeners) {
				l.timePointChanged(timepoint);
			}
		}
		timeSlider.setModel(new DefaultBoundedRangeModel(state.getCurrentTimepoint(), 0, 0, numTimepoints - 1));
		revalidate();
		requestRepaint();
	}

	/**
	 * Get the currently active bookmark.
	 */
	public synchronized Bookmark getActiveBookmark(){
		return state.getActiveBookmark();
	}

	/**
	 * Set the active bookmark
	 * @param bookmark
	 * 			the active bookmark
	 */
	public synchronized void setActiveBookmark(final Bookmark bookmark) {
		Bookmark previousBookmark = this.state.getActiveBookmark();
		this.state.setActiveBookmark(bookmark);
		
		final boolean enableKeyframeButtons = bookmark instanceof DynamicBookmark;
		setKeyframeButtonEnable(enableKeyframeButtons);
	       
	       if (bookmark instanceof DynamicBookmark) {
	           keyframePanel.setDynamicBookmarks((DynamicBookmark) bookmark);
	       } else {
	       	keyframePanel.setDynamicBookmarks(null);
	       }
	       
	       for(ActiveBookmarkChangedListener l: this.activeBookmarkChangedListeners){
	       	l.activeBookmarkChanged(previousBookmark, bookmark);
	       }
	       
		display.repaint();
	}

	public void addActiveBookmarkChangedListener(ActiveBookmarkChangedListener listener){
		activeBookmarkChangedListeners.add(listener);
	}

	public void removeActiveBookmarkChangedListener(ActiveBookmarkChangedListener listener){
		activeBookmarkChangedListeners.remove(listener);
	}

	/**
	 * Get a copy of the current {@link ViewerState}.
	 *
	 * @return a copy of the current {@link ViewerState}.
	 */
	public ViewerState getState()
	{
		return state.copy();
	}

	/**
	 * Get the viewer canvas.
	 *
	 * @return the viewer canvas.
	 */
	public InteractiveDisplayCanvasComponent< AffineTransform3D > getDisplay()
	{
		return display;
	}

	/**
	 * Display the specified message in a text overlay for a short time.
	 *
	 * @param msg
	 *            String to display. Should be just one line of text.
	 */
	public void showMessage( final String msg )
	{
		msgOverlay.add( msg );
		display.repaint();
	}

	/**
	 * Add a new {@link OverlayAnimator} to the list of animators. The animation
	 * is immediately started. The new {@link OverlayAnimator} will remain in
	 * the list of animators until it {@link OverlayAnimator#isComplete()}.å
	 *
	 * @param animator
	 *            animator to add.
	 */
	public void addOverlayAnimator( final OverlayAnimator animator )
	{
		overlayAnimators.add( animator );
		display.repaint();
	}

	/**
	 * Add a {@link InterpolationModeListener} to notify when the interpolation
	 * mode is changed. Listeners will be notified <em>before</em> calling
	 * {@link #requestRepaint()} so they have the chance to interfere.
	 *
	 * @param listener
	 *            the interpolation mode listener to add.
	 */
	public void addInterpolationModeListener( final InterpolationModeListener listener )
	{
		interpolationModeListeners.add( listener );
	}

	/**
	 * Remove a {@link InterpolationModeListener}.
	 *
	 * @param listener
	 *            the interpolation mode listener to remove.
	 */
	public void removeInterpolationModeListener( final InterpolationModeListener listener )
	{
		interpolationModeListeners.remove( listener );
	}

	/**
	 * Add a {@link TransformListener} to notify about viewer transformation
	 * changes. Listeners will be notified when a new image has been painted
	 * with the viewer transform used to render that image.
	 *
	 * This happens immediately after that image is painted onto the screen,
	 * before any overlays are painted.
	 *
	 * @param listener
	 *            the transform listener to add.
	 */
	public void addRenderTransformListener( final TransformListener< AffineTransform3D > listener )
	{
		renderTarget.addTransformListener( listener );
	}

	/**
	 * Add a {@link TransformListener} to notify about viewer transformation
	 * changes. Listeners will be notified when a new image has been painted
	 * with the viewer transform used to render that image.
	 *
	 * This happens immediately after that image is painted onto the screen,
	 * before any overlays are painted.
	 *
	 * @param listener
	 *            the transform listener to add.
	 * @param index
	 *            position in the list of listeners at which to insert this one.
	 */
	public void addRenderTransformListener( final TransformListener< AffineTransform3D > listener, final int index )
	{
		renderTarget.addTransformListener( listener, index );
	}

	/**
	 * Add a {@link TransformListener} to notify about viewer transformation
	 * changes. Listeners will be notified <em>before</em> calling
	 * {@link #requestRepaint()} so they have the chance to interfere.
	 *
	 * @param listener
	 *            the transform listener to add.
	 */
	public void addTransformListener( final TransformListener< AffineTransform3D > listener )
	{
		addTransformListener( listener, Integer.MAX_VALUE );
	}

	/**
	 * Add a {@link TransformListener} to notify about viewer transformation
	 * changes. Listeners will be notified <em>before</em> calling
	 * {@link #requestRepaint()} so they have the chance to interfere.
	 *
	 * @param listener
	 *            the transform listener to add.
	 * @param index
	 *            position in the list of listeners at which to insert this one.
	 */
	public void addTransformListener( final TransformListener< AffineTransform3D > listener, final int index )
	{
		synchronized ( transformListeners )
		{
			final int s = transformListeners.size();
			transformListeners.add( index < 0 ? 0 : index > s ? s : index, listener );
			listener.transformChanged( viewerTransform );
		}
	}

	/**
	 * Remove a {@link TransformListener}.
	 *
	 * @param listener
	 *            the transform listener to remove.
	 */
	public void removeTransformListener( final TransformListener< AffineTransform3D > listener )
	{
		synchronized ( transformListeners )
		{
			transformListeners.remove( listener );
		}
		renderTarget.removeTransformListener( listener );
	}

	/**
	 * Add a {@link TimePointListener} to notify about time-point
	 * changes. Listeners will be notified <em>before</em> calling
	 * {@link #requestRepaint()} so they have the chance to interfere.
	 *
	 * @param listener
	 *            the listener to add.
	 */
	public void addTimePointListener( final TimePointListener listener )
	{
		addTimePointListener( listener, Integer.MAX_VALUE );
	}

	/**
	 * Add a {@link TimePointListener} to notify about time-point
	 * changes. Listeners will be notified <em>before</em> calling
	 * {@link #requestRepaint()} so they have the chance to interfere.
	 *
	 * @param listener
	 *            the listener to add.
	 * @param index
	 *            position in the list of listeners at which to insert this one.
	 */
	public void addTimePointListener( final TimePointListener listener, final int index )
	{
		synchronized ( timePointListeners )
		{
			final int s = timePointListeners.size();
			timePointListeners.add( index < 0 ? 0 : index > s ? s : index, listener );
			listener.timePointChanged( state.getCurrentTimepoint() );
		}
	}

	/**
	 * Remove a {@link TimePointListener}.
	 *
	 * @param listener
	 *            the listener to remove.
	 */
	public void removeTimePointListener( final TimePointListener listener )
	{
		synchronized ( timePointListeners )
		{
			timePointListeners.remove( listener );
		}
	}

	protected class MouseCoordinateListener implements MouseMotionListener , MouseListener {
		private int x;

		private int y;

		private boolean isInside;

		public synchronized void getMouseCoordinates(final Positionable p) {
			p.setPosition(x, 0);
			p.setPosition(y, 1);
		}

		@Override
		public synchronized void mouseDragged(final MouseEvent e) {
			x = e.getX();
			y = e.getY();
		}

		@Override
		public synchronized void mouseMoved(final MouseEvent e) {
			x = e.getX();
			y = e.getY();
			display.repaint();// TODO: only when overlays are visible

		}

		public synchronized int getX() {
			return x;
		}

		public synchronized int getY() {
			return y;
		}

		public synchronized boolean isMouseInsidePanel() {
			return isInside;
		}

		@Override
		public synchronized void mouseEntered(final MouseEvent e) {
			isInside = true;
		}

		@Override
		public synchronized void mouseExited(final MouseEvent e) {
			isInside = false;
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
		}

		@Override
		public void mousePressed(final MouseEvent e) {
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
		}
	}

	public synchronized Element stateToXml()
	{
		return new XmlIoViewerState().toXml( state );
	}

	public synchronized void stateFromXml( final Element parent )
	{
		final XmlIoViewerState io = new XmlIoViewerState();
		io.restoreFromXml( parent.getChild( io.getTagName() ), state );
	}

	/**
	 * does nothing.
	 */
	@Override
	public void setCanvasSize( final int width, final int height )
	{}

	/**
	 * Returns the {@link VisibilityAndGrouping} that can be used to modify
	 * visibility and currentness of sources and groups, as well as grouping of
	 * sources, and display mode.
	 */
	public VisibilityAndGrouping getVisibilityAndGrouping()
	{
		return visibilityAndGrouping;
	}

	public ViewerOptions.Values getOptionValues()
	{
		return options;
	}

	public SourceInfoOverlayRenderer getSourceInfoOverlayRenderer()
	{
		return sourceInfoOverlayRenderer;
	}

	/**
	 * Stop the {@link #painterThread} and shutdown rendering {@link ExecutorService}.
	 */
	public void stop()
	{
		painterThread.interrupt();
		try
		{
			painterThread.join( 0 );
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
		renderingExecutorService.shutdown();
		state.kill();
		imageRenderer.kill();
	}

	protected static final AtomicInteger panelNumber = new AtomicInteger( 1 );

	protected class RenderThreadFactory implements ThreadFactory {
		private final String threadNameFormat = String.format("bdv-panel-%d-thread-%%d", panelNumber.getAndIncrement());

		private final AtomicInteger threadNumber = new AtomicInteger(1);

		@Override
		public Thread newThread(final Runnable r) {
			final Thread t = new Thread(threadGroup, r, String.format(threadNameFormat, threadNumber.getAndIncrement()), 0);
			if (t.isDaemon()) {
				t.setDaemon(false);
			}
			if (t.getPriority() != Thread.NORM_PRIORITY) {
				t.setPriority(Thread.NORM_PRIORITY);
			}
			return t;
		}
	}
}