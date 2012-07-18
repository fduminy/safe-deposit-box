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

import static fr.duminy.safe.swing.action.Action.ADD_PASSWORD;
import static fr.duminy.safe.swing.action.Action.CREATE_PASSWORD;
import static fr.duminy.safe.swing.action.Action.REMOVE_PASSWORD;
import static fr.duminy.safe.swing.action.Action.UPDATE_PASSWORD;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.action.TargetManager;
import org.jdesktop.swingx.action.Targetable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.model.DuplicateNameException;
import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.swing.command.Command;
import fr.duminy.safe.swing.command.CommandSupport;

public class MainPanel extends JPanel implements Targetable {
    private static final Logger LOG = LoggerFactory.getLogger(MainPanel.class);
    
	private final CommandSupport support = new CommandSupport();	
	
	private final PasswordListPanel passwordList;
	private final PasswordForm passwordForm;
    	
	public MainPanel() throws Exception {
		this(new SwingCore());
	}
	/**
	 * Create the panel.
	 */
	public MainPanel(final SwingCore core) {
		TargetManager.getInstance().addTarget(this);
		
		setLayout(new BorderLayout(0, 0));		
		passwordList = new PasswordListPanel(core);
		TargetManager.getInstance().addTarget(passwordList);
		add(passwordList);
		
		passwordForm = new PasswordForm();
		TargetManager.getInstance().addTarget(passwordForm);
		add(passwordForm, BorderLayout.SOUTH);
		
		JToolBar toolBar = new JToolBar();
		add(toolBar, BorderLayout.NORTH);
		
		JButton addButton = new JButton(CREATE_PASSWORD.toSwingAction());
		addButton.setName("addButton");
		toolBar.add(addButton);
		
		JButton removeButton = new JButton(REMOVE_PASSWORD.toSwingAction());
		removeButton.setName("removeButton");
		toolBar.add(removeButton);
		
		passwordList.addListSelectionListener(new ListSelectionListener() {			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					boolean canRemove = (passwordList.getSelectedRow() >= 0);
					REMOVE_PASSWORD.setEnabled(canRemove);
					
					Password pwd = core.getPasswords().get(e.getFirstIndex());
					if (!passwordForm.isEditing()) {
						passwordForm.viewPassword(pwd);
					}
				}
			}
		});
		
		support.addCommand(new Command(ADD_PASSWORD) {		
			@Override
			public void run() {
				Password password = passwordForm.getPassword();
				LOG.info("{} {}", ADD_PASSWORD, password);
				try {
					passwordList.addPassword(password);
					passwordForm.viewPassword(password);
				} catch (DuplicateNameException dne) {
					core.displayError(dne.getMessage(), dne);
				}
			}
		});
		support.addCommand(new Command(UPDATE_PASSWORD) {		
			@Override
			public void run() {
				Password password = passwordForm.getPassword();
				LOG.info("{} {}", UPDATE_PASSWORD, password);
				passwordForm.viewPassword(password);
				passwordList.updatePassword(password);
			}
		});
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
	public String[] getCommands() {
		return support.getCommands();
	}	
}
