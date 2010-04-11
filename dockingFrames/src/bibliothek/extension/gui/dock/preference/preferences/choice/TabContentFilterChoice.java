/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.extension.gui.dock.preference.preferences.choice;

import bibliothek.gui.DockUI;
import bibliothek.gui.dock.station.stack.tab.DefaultTabContentFilter;
import bibliothek.gui.dock.station.stack.tab.TabContentFilter;
import bibliothek.gui.dock.station.stack.tab.DefaultTabContentFilter.Behavior;

/**
 * A set of choices of {@link TabContentFilter}s.
 * @author Benjamin Sigg
 */
public class TabContentFilterChoice extends DefaultChoice<TabContentFilter> {
	/**
	 * Creates a new choice
	 */
	public TabContentFilterChoice(){
		DockUI ui = DockUI.getDefaultDockUI();
		add( "all", ui.getString( "preference.layout.tabcontentfilter.all" ), null );
		add( "icon", ui.getString( "preference.layout.tabcontentfilter.icon" ), new DefaultTabContentFilter( Behavior.ICON_ONLY ) );
		add( "title", ui.getString( "preference.layout.tabcontentfilter.title" ), new DefaultTabContentFilter( Behavior.TEXT_ONLY ) );
		add( "iconOrTitle", ui.getString( "preference.layout.tabcontentfilter.iconOrTitle" ), new DefaultTabContentFilter( Behavior.ALL, Behavior.TEXT_ONLY ) );
		add( "titleOrIcon", ui.getString( "preference.layout.tabcontentfilter.titleOrIcon" ), new DefaultTabContentFilter( Behavior.ALL, Behavior.ICON_ONLY ) );
		
		setDefaultChoice( "all" );
	}
}
