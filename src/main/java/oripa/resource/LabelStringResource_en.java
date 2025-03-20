package oripa.resource;

import java.util.ListResourceBundle;

public class LabelStringResource_en extends ListResourceBundle {

	private static final Object[][] strings = {
			{ StringID.Main.TITLE_ID, "ORIPA " + Version.ORIPA_VERSION },
			{ StringID.Main.FILE_ID, "File" },
			{ StringID.Main.EDIT_ID, "Edit" },
			{ StringID.Main.HELP_ID, "Help" },
			{ StringID.Main.NEW_ID, "New" },
			{ StringID.Main.OPEN_ID, "Open" },
			{ StringID.Main.SAVE_ID, "Save" },
			{ StringID.Main.SAVE_AS_ID, "Save As ..." },
			{ StringID.Main.SAVE_AS_IMAGE_ID, "Save As Image ..." },
<<<<<<< /usr/src/app/output/oripa/oripa/c7a747856cefa97fa45aa389c08f0eabb471efff/src/main/java/oripa/resource/LabelStringResource_en.java/left.java
			{ StringID.Main.EXPORT_FOLD_ID, "Export (FOLD)" },
			{ StringID.Main.EXPORT_DXF_ID, "Export (DXF)" },
			{ StringID.Main.EXPORT_CP_ID, "Export (CP)" },
			{ StringID.Main.EXPORT_SVG_ID, "Export (SVG)" },
||||||| /usr/src/app/output/oripa/oripa/c7a747856cefa97fa45aa389c08f0eabb471efff/src/main/java/oripa/resource/LabelStringResource_en.java/base.java
			{ StringID.Main.EXPORT_DXF_ID, "Export (DXF)" },
=======
	
			{ StringID.Main.EXPORT_FOLD_ID, "Export FOLD" },
			{ StringID.Main.EXPORT_CP_ID, "Export CP" },
			{ StringID.Main.EXPORT_DXF_ID, "Export DXF" },
			{ StringID.Main.EXPORT_SVG_ID, "Export SVG" },

>>>>>>> /usr/src/app/output/oripa/oripa/c7a747856cefa97fa45aa389c08f0eabb471efff/src/main/java/oripa/resource/LabelStringResource_en.java/right.java
			{ StringID.Main.PROPERTY_ID, "Property" },
			{ StringID.Main.EXIT_ID, "Exit" },
			{ StringID.Main.ABOUT_ID, "About" },
			{ StringID.Main.REPEAT_COPY_ID, "Repeat Copy" },
			{ StringID.Main.CIRCLE_COPY_ID, "Circular Copy" },
			{ StringID.Main.UNDO_ID, "Undo" },
			{ StringID.Main.REDO_ID, "Redo" },
<<<<<<< /usr/src/app/output/oripa/oripa/c7a747856cefa97fa45aa389c08f0eabb471efff/src/main/java/oripa/resource/LabelStringResource_en.java/left.java
			{ StringID.Main.SELECT_ALL_ID, "Select all" },
			{ StringID.Main.UNSELECT_ALL_ID, "Unselect all" },
			{ StringID.Main.DELETE_SELECTED_LINES_ID, "Delete selected lines" },
||||||| /usr/src/app/output/oripa/oripa/c7a747856cefa97fa45aa389c08f0eabb471efff/src/main/java/oripa/resource/LabelStringResource_en.java/base.java
			{ StringID.Main.SELECT_ALL_ID, "Select all" },
=======
			{ StringID.Main.ARRAY_COPY_ID, "Array Copy" },
			{ StringID.Main.CIRCLE_COPY_ID, "Circle Copy" },
			{ StringID.Main.UNSELECT_ALL_ID, "Unselect All" },
			{ StringID.Main.DELETE_SELECTED_ID, "Delete Selected Lines" },
>>>>>>> /usr/src/app/output/oripa/oripa/c7a747856cefa97fa45aa389c08f0eabb471efff/src/main/java/oripa/resource/LabelStringResource_en.java/right.java

			{ StringID.UI.ZERO_LINE_WIDTH_ID, "Zero line width" },
			{ StringID.UI.AUX_ID, "Aux" },
			{ StringID.UI.VALLEY_ID, "Valley" },
			{ StringID.UI.MOUNTAIN_ID, "Mountain" },

			{ StringID.UI.INPUT_LINE_ID, "Input Line" },
			{ StringID.UI.SELECT_ID, "Select" },
			{ StringID.UI.DELETE_LINE_ID, "Delete Line" },

			{ StringID.UI.SHOW_GRID_ID, "Show Grid" },
			{ StringID.UI.SHOW_MV_ID, "Show M/V Lines" },
			{ StringID.UI.SHOW_AUX_ID, "Show Aux Lines" },

			{ StringID.UI.CHANGE_LINE_TYPE_FROM_ID, "  from" },
			{ StringID.UI.CHANGE_LINE_TYPE_TO_ID, "to" },

			{ StringID.Main.ORIPA_FILE_ID, "ORIPA file" },
			{ StringID.Main.PICTURE_FILE_ID, "Picture file" },

			// ---------------------------------------------------------
			// Integrated IDs
			{ StringID.COPY_PASTE_ID, "Copy and Paste" },
			{ StringID.CUT_PASTE_ID, "Cut and Paste" },
			{ StringID.EDIT_CONTOUR_ID, "Edit Contour" },
			{ StringID.SELECT_ID, "Select" },
			{ StringID.DELETE_LINE_ID, "Delete Line" },
			{ StringID.CHANGE_LINE_TYPE_ID, "Change Line Type" },
			{ StringID.ADD_VERTEX_ID, "Add Vertex" },
			{ StringID.DELETE_VERTEX_ID, "Delete Vertex" },
			{ StringID.SELECT_ALL_LINE_ID, "Select All" },

			{ StringID.UI.MEASURE_ID, "Measure" },
			{ StringID.UI.FOLD_ID, "Fold..." },
			{ StringID.UI.FULL_ESTIMATION_ID, "Full Estimation" },
			{ StringID.UI.CHECK_WINDOW_ID, "Check Window" },

			{ StringID.UI.GRID_SIZE_CHANGE_ID, "Set" },
			{ StringID.UI.SHOW_VERTICES_ID, "Show Vertices" },
			{ StringID.UI.EDIT_MODE_ID, "Edit Mode" },
			{ StringID.UI.LINE_INPUT_MODE_ID, "Line Input Mode" },
			{ StringID.UI.LENGTH_ID, "Length" },
			{ StringID.UI.ANGLE_ID, "Angle" },
			{ StringID.UI.GRID_DIVIDE_NUM_ID, "Div Num" },

			// ---------------------------------------------------------
			// Default IDs

			{ StringID.Default.FILE_NAME_ID, "NoTitle" }

	};

	@Override
	protected Object[][] getContents() {
		return strings;
	}

}
