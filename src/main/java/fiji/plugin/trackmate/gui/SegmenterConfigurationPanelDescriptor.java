package fiji.plugin.trackmate.gui;

import java.awt.Component;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import fiji.plugin.trackmate.TrackMate_;
import fiji.plugin.trackmate.detection.ManualSegmenter;
import fiji.plugin.trackmate.detection.SegmenterSettings;
import fiji.plugin.trackmate.detection.SpotSegmenter;

public class SegmenterConfigurationPanelDescriptor <T extends RealType<T> & NativeType<T>> implements WizardPanelDescriptor<T> {

	public static final String DESCRIPTOR = "SegmenterConfigurationPanel";
	private TrackMate_<T> plugin;
	private SegmenterConfigurationPanel<T> configPanel;
	private TrackMateWizard<T> wizard;
	
	/*
	 * METHODS
	 */

	@Override
	public void setWizard(TrackMateWizard<T> wizard) { 
		this.wizard = wizard;
	}

	@Override
	public void setPlugin(TrackMate_<T> plugin) {
		this.plugin = plugin;
		SegmenterSettings<T> settings = plugin.getModel().getSettings().segmenterSettings;
		// Bulletproof null
		if (null == settings) {
			SpotSegmenter<T> segmenter = plugin.getModel().getSettings().segmenter;
			if (null == segmenter) {
				// try to make it right with a default
				segmenter = new ManualSegmenter<T>();
				plugin.getModel().getSettings().segmenter = segmenter;
			}
			settings = segmenter.createDefaultSettings();
		}
		configPanel = settings.createConfigurationPanel();
	}

	@Override
	public Component getComponent() {
		return configPanel;
	}

	@Override
	public String getDescriptorID() {
		return DESCRIPTOR;
	}
	
	@Override
	public String getComponentID() {
		return DESCRIPTOR;
	}

	@Override
	public String getNextDescriptorID() {
		if (plugin.getModel().getSettings().segmenter.getClass() == ManualSegmenter.class) {
			return DisplayerChoiceDescriptor.DESCRIPTOR;
		} else {
			return SegmentationDescriptor.DESCRIPTOR;
		}
	}

	@Override
	public String getPreviousDescriptorID() {
		return SegmenterChoiceDescriptor.DESCRIPTOR;
	}

	@Override
	public void aboutToDisplayPanel() {
		configPanel.setSegmenterSettings(plugin.getModel());
		wizard.setNextButtonEnabled(true);
	}

	@Override
	public void displayingPanel() {	}

	@Override
	public void aboutToHidePanel() {
		plugin.getModel().getSettings().segmenterSettings = configPanel.getSegmenterSettings();
	}

}
