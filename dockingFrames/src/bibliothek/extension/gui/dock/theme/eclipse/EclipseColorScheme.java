/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse;

import javax.swing.UIManager;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.color.DefaultColorScheme;

/**
 * A {@link ColorScheme} used by the {@link EclipseTheme}.
 * @author Benjamin Sigg
 */
public class EclipseColorScheme extends DefaultColorScheme {
    /**
     * Creates the new color scheme
     */
    public EclipseColorScheme(){
        updateUI();
    }
    
    @Override
    public boolean updateUI(){
        setColor( "stack.tab.border",                   UIManager.getColor( "Panel.background" ) );
        setColor( "stack.tab.border.selected",          RexSystemColor.getInactiveColorGradient() );
        setColor( "stack.tab.border.selected.focused",  RexSystemColor.getActiveColorGradient() );
        setColor( "stack.tab.border.selected.focuslost",RexSystemColor.getInactiveColor() );
        
        setColor( "stack.tab.top",                      UIManager.getColor( "Panel.background" ) );
        setColor( "stack.tab.tob.selected",             RexSystemColor.getInactiveColor() );
        setColor( "stack.tab.top.selected.focused",     RexSystemColor.getActiveColor() );
        setColor( "stack.tab.top.selected.focuslost",   RexSystemColor.getInactiveColor() );
        
        setColor( "stack.tab.bottom",                   UIManager.getColor( "Panel.background" ) );
        setColor( "stack.tab.bottom.selected",          RexSystemColor.getInactiveColorGradient() );
        setColor( "stack.tab.bottom.selected.focused",  RexSystemColor.getActiveColorGradient() );
        setColor( "stack.tab.bottom.selected.focuslost",RexSystemColor.getInactiveColor() );
        
        setColor( "stack.tab.text",                     UIManager.getColor( "Panel.foreground" ) );
        setColor( "stack.tab.text.selected",            RexSystemColor.getInactiveTextColor() );
        setColor( "stack.tab.text.selected.focused",    RexSystemColor.getActiveTextColor() );
        setColor( "stack.tab.text.selected.focuslost",  RexSystemColor.getInactiveTextColor() );
    
        return true;
    }
}
