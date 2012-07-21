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

import java.awt.BorderLayout;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.action.Targetable;

import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.swing.action.Action;
import fr.duminy.safe.swing.command.Command;
import fr.duminy.safe.swing.command.CommandSupport;

public class PasswordListPanel extends JXPanel implements Targetable {
	private final CommandSupport support = new CommandSupport();	
	private final JXTable passwordList;
	private final SwingCore core;

	public PasswordListPanel() throws Exception {
		this(new SwingCore());
	}
	/**
	 * Create the panel.
	 */
	public PasswordListPanel(final SwingCore core) {
		this.core = core;
		
		setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		add(scrollPane);
		passwordList = new JXTable(new PasswordTableModel(core));
		passwordList.setName("passwordList"); //$NON-NLS-1$
		scrollPane.setViewportView(passwordList);
		
		support.addCommand(new Command(Action.REMOVE_PASSWORD) {
			public void run() {
				int row = passwordList.getSelectedRow();
				Password pwd = core.getPasswords().get(row);
				core.removePassword(pwd);
				((PasswordTableModel) passwordList.getModel()).fireTableRowsDeleted(row, row);
				row = (row > 0) ? (row - 1) : 0;
				passwordList.getSelectionModel().setSelectionInterval(row , row);
			}
		});
	}
	
	public void addListSelectionListener(ListSelectionListener listener) {
		passwordList.getSelectionModel().addListSelectionListener(listener);
	}
	public int getSelectedRow() {
		return passwordList.getSelectedRow();
	}
	
	public void addPassword(Password password) {
		int row = core.getPasswords().size();
		core.addPassword(password);
		((PasswordTableModel) passwordList.getModel()).fireTableRowsInserted(row, row);
		passwordList.getSelectionModel().setSelectionInterval(row , row);
	}

	public void updatePassword(Password password) {
		int row = core.getPasswords().indexOf(password);
		((PasswordTableModel) passwordList.getModel()).fireTableRowsUpdated(row, row);
		passwordList.getSelectionModel().setSelectionInterval(row , row);
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
