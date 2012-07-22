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

import static fr.duminy.safe.swing.MessageKey.PASSWORD_NOT_SAVED_MESSAGE;
import static fr.duminy.safe.swing.MessageKey.PASSWORD_NOT_SAVED_TITLE;
import static fr.duminy.safe.swing.action.Action.EXIT;
import static fr.duminy.safe.swing.action.Action.IMPORT;

import java.awt.Component;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.plaf.FileChooserUI;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.View;
import org.jdesktop.swingx.action.TargetManager;
import org.jdesktop.swingx.action.Targetable;

import fr.duminy.safe.core.imp.Importer;
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
		support.addCommand(new Command(IMPORT) {		
			@SuppressWarnings("serial")
			@Override
			public void run() {
			    JFileChooser fileChooser = new JFileChooser();
			    fileChooser.setMultiSelectionEnabled(true);
			    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			    JComboBox cbxImporters = new JComboBox(new Vector<Importer>(core.getImporters()));
			    cbxImporters.setRenderer(new DefaultListCellRenderer() {
			    	@Override
			    	public Component getListCellRendererComponent(
			    			JList list, Object value, int index,
			    			boolean isSelected, boolean cellHasFocus) {
			    		Importer importer = (Importer) value;
			    		super.getListCellRendererComponent(list, importer.getName(), index, isSelected,
			    				cellHasFocus);
			    		return this;
			    	}
				});
			    fileChooser.setAccessory(cbxImporters);			    
			    initNamesForReleaseTests(fileChooser); 
			    int result = fileChooser.showOpenDialog(getMainFrame());
			    if (result == JFileChooser.APPROVE_OPTION) {
			    	Importer importer = (Importer) cbxImporters.getSelectedItem();
			    	for (File file : fileChooser.getSelectedFiles()) {
			    		try {
							core.importPasswords(importer, new FileReader(file));
						} catch (Exception e) {
							core.displayError("failed to import from file " + file.getName() + " cause:" + e.getMessage(), e);
						}
			    	}
			    	getMainPanel().refresh();
			    }
			}
		});		
		
		addExitListener(new ExitListener() {			
			@Override
			public void willExit(EventObject e) {
			}
			
			@Override
			public boolean canExit(EventObject e) {
				boolean result = true;
				
		    	MainPanel mainPanel = getMainPanel();
		    	if (mainPanel.isEditing()) {
		    		JOptionPane jop = new JOptionPane(Messages.getString(PASSWORD_NOT_SAVED_MESSAGE), JOptionPane.WARNING_MESSAGE);
		    		JDialog dlg = jop.createDialog(Messages.getString(PASSWORD_NOT_SAVED_TITLE));
		    		show(dlg);
		    		result = false;
		    	}
		    	
		    	return result;
			}
		});
    }
    
    private void initNamesForReleaseTests(JFileChooser jfc) {
    	FileChooserUI ui = jfc.getUI();

    	JTextField fileTextField = null;
    	for (Field field : ui.getClass().getDeclaredFields()) {
    		fileTextField = getFieldValue(ui, field, JTextField.class, fileTextField);
    	}
    	if (fileTextField == null) {
    		throw new Error("Can't find JTextField in JFileChooser UI (" + ui.getClass().getName() + ")");
    	}
    	fileTextField.setName("fileTextField");
    	
    	String errorMessage = "Can't get approve button in JFileChooser UI (" + ui.getClass().getName() + ")";
    	
    	Field field = null;    	
		try {
	    	field = ui.getClass().getDeclaredField("approveButton");
		} catch (Exception e) {
			throw new Error(errorMessage, e);
		}
		
    	JButton approveButton = null;
		boolean accessible = field.isAccessible();
		try {
			field.setAccessible(true);
			approveButton = (JButton) field.get(ui);
		} catch (Exception e) {
			throw new Error(errorMessage, e);
		} finally {
			field.setAccessible(accessible);
		}
    	if (approveButton == null) {
    		throw new Error(errorMessage);
    	}
    	approveButton.setName("approveButton");
    }
    
    private <T> T getFieldValue(final FileChooserUI ui, final Field field, final Class<T> wantedFieldClass, final T fieldValue) {
    	T value = fieldValue;
		if (wantedFieldClass.isAssignableFrom(field.getType())) {
			if (value != null) {
				throw new Error("There is more than one " + wantedFieldClass.getSimpleName() + " in JFileChooser UI (" + ui.getClass().getName() + ")");
			}
			boolean accessible = field.isAccessible();
			try {
				field.setAccessible(true);
				value = wantedFieldClass.cast(field.get(ui));
			} catch (Exception e) {
				throw new Error("Can't get " + wantedFieldClass.getSimpleName(), e);
			} finally {
				field.setAccessible(accessible);
			}
		}
		return value;
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

    private MainPanel getMainPanel() {
    	return (MainPanel) getMainView().getComponent();
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
