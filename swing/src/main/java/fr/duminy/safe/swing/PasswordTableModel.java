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

import static fr.duminy.safe.swing.MessageKey.NAME;
import static fr.duminy.safe.swing.MessageKey.PASSWORD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.duminy.safe.core.model.Password;

@SuppressWarnings("serial")
public class PasswordTableModel extends AbstractTableModel {
	private List<Password> passwords;
	
	public PasswordTableModel(List<Password> passwords) {
		setPasswords(passwords);
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	
	@Override
	public String getColumnName(int column) {
		return (column == 0) ? Messages.getString(NAME) : Messages.getString(PASSWORD);
	}
	
	@Override
	public int getRowCount() {
		return getPasswords().size();
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Password pwd = getPasswords().get(rowIndex); 
		return (columnIndex == 0) ? pwd.getName() : pwd.getPassword();
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}
	
	public void setPasswords(List<Password> passwords) {
		this.passwords = (passwords == null) ? new ArrayList<Password>() : passwords;
		fireTableDataChanged();
	}

	public List<Password> getPasswords() {
		return passwords;
	}

	public void addPassword(Password password) {
		getPasswords().add(password);
		int row = getPasswords().size() - 1;
		fireTableRowsInserted(row, row);
	}

	public void removePassword(int rowIndex) {
		getPasswords().remove(rowIndex);
		fireTableRowsDeleted(rowIndex, rowIndex);
	}

	public void removePasswords(int[] rowIndexes) {
		Arrays.sort(rowIndexes);

		for (int i = rowIndexes.length - 1; i >= 0; i--) {
			getPasswords().remove(rowIndexes[i]);
		}

		fireTableRowsDeleted(rowIndexes[0], rowIndexes[rowIndexes.length - 1]);
	}
}
