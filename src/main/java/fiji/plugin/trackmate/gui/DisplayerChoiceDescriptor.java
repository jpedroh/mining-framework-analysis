package fiji.plugin.trackmate.gui;

import java.awt.Component;

import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

import fiji.plugin.trackmate.TrackMate_;
import fiji.plugin.trackmate.detection.ManualSegmenter;
import fiji.plugin.trackmate.visualization.TrackMateModelView;

public class DisplayerChoiceDescriptor<T extends RealType<T> & NativeType<T>> implements WizardPanelDescriptor<T> {

	public static final String DESCRIPTOR = "DisplayerChoice";
	private TrackMate_<T> plugin;
	private ListChooserPanel<TrackMateModelView<T>> component;
	private TrackMateWizard<T> wizard;
	
	/*
	 * METHODS
	 */
	
	@Override
	public void setWizard(TrackMateWizard<T> wizard) {
		this.wizard = wizard;
	}

	@Override
	public Component getComponent() {
		return component;
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
		return LaunchDisplayerDescriptor.DESCRIPTOR;
	}

	@Override
	public String getPreviousDescriptorID() {
		if (plugin.getModel().getSettings().segmenter.getClass() == ManualSegmenter.class) {
			return SegmenterConfigurationPanelDescriptor.DESCRIPTOR;
		} else {
			return InitFilterPanel.DESCRIPTOR;
		}
	}

	@Override
	public void aboutToDisplayPanel() {	}

	@Override
	public void displayingPanel() {
		wizard.setNextButtonEnabled(true);
	}

	@Override
	public void aboutToHidePanel() {
		TrackMateModelView<T> displayer = component.getChoice();
		wizard.setDisplayer(displayer);
	}

	@Override
	public void setPlugin(TrackMate_<T> plugin) {
		this.plugin = plugin;
		this.component = new ListChooserPanel<TrackMateModelView<T>>(plugin.getAvailableTrackMateModelViews(), "displayer");
	}



}
