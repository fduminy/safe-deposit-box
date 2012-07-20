/**
 * Safe Deposit Box is a software managing your passwords in a safe place.
 *
 * Copyright (C) 2012 Fabien DUMINY (fduminy at jnode dot org)
 *
 * Safe Deposit Box is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Safe Deposit Box is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */
package fr.duminy.safe.swing;

import static fr.duminy.safe.swing.action.Action.EXIT;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.View;
import org.jdesktop.swingx.action.TargetManager;
import org.jdesktop.swingx.action.Targetable;

import fr.duminy.safe.swing.action.Action;
import fr.duminy.safe.swing.command.Command;
import fr.duminy.safe.swing.command.CommandSupport;


public class SwingApplication extends SingleFrameApplication implements Targetable {
    public static void main(String[] args) {
        Application.launch(SwingApplication.class, args);
    }
    
    private CommandSupport support = new CommandSupport();
    private SwingCore core;
    
    @Override
    protected void initialize(String[] args) {
        super.initialize(args);
        try {
            core = new SwingCore();
            
            core.start();            
        } catch (Exception e) {
            throw new Error(e);
        }
        
        TargetManager.getInstance().addTarget(this);        
		support.addCommand(new Command(EXIT) {		
			@Override
			public void run() {
				exit();
			}
		});		
		
		addExitListener(new ExitListener() {			
			@Override
			public void willExit(EventObject e) {
			}
			
			@Override
			public boolean canExit(EventObject e) {
				boolean result = true;
				
		    	MainPanel mainPanel = (MainPanel) getMainView().getComponent();
		    	if (mainPanel.isEditing()) {
		    		JOptionPane jop = new JOptionPane("Password is being edited.\nPlease save it before exiting.", JOptionPane.WARNING_MESSAGE);
		    		JDialog dlg = jop.createDialog("Password not saved");
		    		show(dlg);
		    		result = false;
		    	}
		    	
		    	return result;
			}
		});
    }
    
    @Override
    public void exit(EventObject e) {
    	super.exit(e);
        
        try {
            if (core != null) {
                core.stop();
                core = null;
            }
        } catch (Exception ex) {
        	//TODO use Core.reportError 
            throw new Error(ex);
        }
    }
    
    @Override 
    protected void startup() {
        Action.init();
        
        View view = getMainView();
        view.setComponent(createMainComponent());
        view.setMenuBar(createMenuBar());
        show(view);
    }
           
    private JMenuBar createMenuBar() {
        return new JMenuBar();
    }

    private JComponent createMainComponent() {
    	return new MainPanel(core);
    }

	@Override
	public boolean doCommand(Object command, Object value) {
		return support.doCommand(command, value);
	}

	@Override
	public boolean hasCommand(Object command) {
		return support.hasCommand(command);
	}

	@Override
	public Object[] getCommands() {
		return support.getCommands();
	}
}
