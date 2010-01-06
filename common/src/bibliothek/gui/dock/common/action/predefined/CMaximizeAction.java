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
package bibliothek.gui.dock.common.action.predefined;

import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.action.CExtendedModeAction;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.state.StateManager;
import bibliothek.gui.dock.support.util.Resources;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * An action that maximizes each {@link CDockable} to which it is added.
 * @author Benjamin Sigg
 */
public class CMaximizeAction extends CExtendedModeAction{
    
    /**
     * {@link KeyStroke} used to go into, or go out from the maximized state.
     */
    private PropertyValue<KeyStroke> keyStrokeMaximizeChange = new PropertyValue<KeyStroke>( CControl.KEY_MAXIMIZE_CHANGE ){
        @Override
        protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
            if( keyStrokeMaximized.getValue() == null )
                setAccelerator( newValue );
        }
    };
    
    
    /**
     * {@link KeyStroke} used on the maximize-action.
     */
    private PropertyValue<KeyStroke> keyStrokeMaximized = new PropertyValue<KeyStroke>( CControl.KEY_GOTO_MAXIMIZED ){
        @Override
        protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
            if( newValue == null )
                setAccelerator( keyStrokeMaximizeChange.getValue() );
            else
                setAccelerator( newValue );
        }
    };
    
    /**
     * Creates a new action
     * @param control the control for which this action will be used
     */
    public CMaximizeAction( CControl control ){
        super( control, ExtendedMode.MAXIMIZED, "maximize", StateManager.ICON_MANAGER_KEY_MAXIMIZE, CControl.KEY_GOTO_MAXIMIZED );
        
        ResourceBundle bundle = Resources.getBundle();
        
        setText( bundle.getString( "maximize.in" ) );
        setTooltip( bundle.getString( "maximize.in.tooltip" ) );
    }
    
    @Override
    protected void setController( DockController controller ) {
        super.setController( controller );
        keyStrokeMaximizeChange.setProperties( controller );
        keyStrokeMaximized.setProperties( controller );
    }
    
    @Override
    protected boolean checkTrigger( KeyEvent event ) {
        return !KeyStroke.getKeyStrokeForEvent( event ).equals( keyStrokeMaximizeChange.getValue());
    }
}
