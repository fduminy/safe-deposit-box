/**
 * Safe Deposit Box is a software managing your passwords in a safe place.
 *
 * Copyright (C) 2012-2012 Fabien DUMINY (fduminy at jnode dot org)
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

import static fr.duminy.safe.swing.action.Action.ADD_PASSWORD;
import static fr.duminy.safe.swing.action.Action.CREATE_PASSWORD;
import static fr.duminy.safe.swing.action.Action.EXIT;
import static fr.duminy.safe.swing.action.Action.IMPORT;
import static fr.duminy.safe.swing.action.Action.REMOVE_PASSWORD;
import static fr.duminy.safe.swing.action.Action.UPDATE_PASSWORD;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.model.DuplicateNameException;
import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.swing.command.Command;

@SuppressWarnings("serial")
public class MainPanel extends SPanel {
    private static final Logger LOG = LoggerFactory.getLogger(MainPanel.class);
    
	private PasswordListPanel passwordList;
	private PasswordForm passwordForm;
    	
	public MainPanel() throws Exception {
		this(new SwingCore());
	}
	/**
	 * Create the panel.
	 */
	public MainPanel(final SwingCore core) {
		LOG.debug("new instance of MainPanel. callstack", new Exception("callstack"));
		setLayout(new BorderLayout(0, 0));		
		passwordList = new PasswordListPanel(core);
		add(passwordList);
		
		passwordForm = new PasswordForm();
		add(passwordForm, BorderLayout.SOUTH);
		
		JToolBar toolBar = new JToolBar();
		add(toolBar, BorderLayout.NORTH);
		
		JButton addButton = new JButton(CREATE_PASSWORD.toSwingAction());
		addButton.setName("addButton"); //$NON-NLS-1$
		toolBar.add(addButton);
		
		JButton removeButton = new JButton(REMOVE_PASSWORD.toSwingAction());
		removeButton.setName("removeButton"); //$NON-NLS-1$
		toolBar.add(removeButton);

		toolBar.addSeparator();
		
		JButton importButton = new JButton(IMPORT.toSwingAction());
		importButton.setName("importButton"); //$NON-NLS-1$
		toolBar.add(importButton);
		
		toolBar.addSeparator();
		
		JButton exitButton = new JButton(EXIT.toSwingAction());
		exitButton.setName("exitButton"); //$NON-NLS-1$
		toolBar.add(exitButton);
		
		passwordList.addListSelectionListener(new ListSelectionListener() {			
			@Override
			public void valueChanged(ListSelectionEvent e) {				
				if (!e.getValueIsAdjusting()) {
					int pwdIndex = passwordList.getSelectedRow();
					boolean canRemove = (pwdIndex >= 0);
					REMOVE_PASSWORD.setEnabled(canRemove);
					
					if (!passwordForm.isEditing()) {
						Password pwd = null;
						if ((pwdIndex >= 0) && (pwdIndex < core.getPasswords().size())) {
							pwd = core.getPasswords().get(pwdIndex);
						}
						
						passwordForm.viewPassword(pwd);
					}
				}
			}
		});
		
		addCommand(new Command(ADD_PASSWORD) {		
			@Override
			public void run() {
				Password password = passwordForm.getPassword();
				LOG.info("{} {}", ADD_PASSWORD, password); //$NON-NLS-1$
				try {
					passwordList.addPassword(password);
					passwordForm.viewPassword(password);
				} catch (DuplicateNameException dne) {
					core.displayError(dne.getMessage(), dne);
				}
			}
		});
		addCommand(new Command(UPDATE_PASSWORD) {		
			@Override
			public void run() {
				Password password = passwordForm.getPassword();
				LOG.info("{} {}", UPDATE_PASSWORD, password); //$NON-NLS-1$
				passwordForm.viewPassword(password);
				passwordList.updatePassword(password);
			}
		});
	}
	
	public void refresh() {
		passwordList.refresh();
	}
		
	public boolean isEditing() {
		return (passwordForm != null) && passwordForm.isEditing();
	}
	
	@Override
	public void dispose() {
		passwordList = null;
		passwordForm = null;
		
		super.dispose();
	}	
}
