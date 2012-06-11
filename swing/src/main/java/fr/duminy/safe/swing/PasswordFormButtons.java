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

import static fr.duminy.safe.swing.FormState.READ;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import fr.duminy.safe.core.model.Password;

public class PasswordFormButtons extends JPanel {
	private final JButton mainButton;
	private final JButton cancelButton;
	
	/**
	 * Create the panel.
	 */
	public PasswordFormButtons() {
		setLayout(new GridLayout(1, 0, 0, 0));
		
		mainButton = new JButton("Main Action");
		mainButton.setName("mainButton");
		add(mainButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setName("cancelButton");
		add(cancelButton);
	}

	public void changeState(FormState state, Password password) {	
		if (READ.equals(state)) {
			remove(cancelButton);
		} else {
			add(cancelButton);	
		}

		if (password == null) {
			remove(mainButton);	
		} else {
			add(mainButton);
		}
		
		javax.swing.Action mainAction = mainButton.getAction();
		if (mainAction != null) {
			mainAction.setEnabled(false);
		}
		
		mainAction = state.getMainAction().toSwingAction();
		mainAction.setEnabled(true);
		mainButton.setAction(mainAction);
	}
}
