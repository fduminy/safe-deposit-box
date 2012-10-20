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

import static fr.duminy.safe.swing.FormState.CREATE;
import static fr.duminy.safe.swing.FormState.READ;
import static fr.duminy.safe.swing.FormState.UPDATE;
import static fr.duminy.safe.swing.action.Action.CANCEL_EDITION;
import static fr.duminy.safe.swing.action.Action.CREATE_PASSWORD;
import static fr.duminy.safe.swing.action.Action.EDIT_PASSWORD;

import java.awt.BorderLayout;

import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.swing.command.Command;

@SuppressWarnings("serial")
public class PasswordForm extends SPanel {
	private final PasswordFormButtons passwordFormButtons;
	private final PasswordFormFields passwordFormFields;
	private FormState state = READ;
	
	public PasswordForm() {
		setLayout(new BorderLayout(0, 0));
		
		passwordFormButtons = new PasswordFormButtons();
		add(passwordFormButtons, BorderLayout.SOUTH);
		
		passwordFormFields = new PasswordFormFields();
		add(passwordFormFields, BorderLayout.CENTER);
				
		addCommand(new Command(CREATE_PASSWORD) {
			public void run() {
				changeState(CREATE, createEmptyPassword());
			}
		});
		addCommand(new Command(CANCEL_EDITION) {
			public void run() {
				changeState(READ, null);
			}
		});
		addCommand(new Command(EDIT_PASSWORD) {
			public void run() {
				changeState(UPDATE, getPassword());
			}
		});
		
		changeState(READ, null);
	}
	
	private Password createEmptyPassword() {
		return new Password("", "");		 //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void viewPassword(Password password) {
		changeState(READ, password);
	}

	public Password getPassword() {
		return passwordFormFields.getPassword();
	}

	private void changeState(FormState state, Password password) {
		this.state = state;
		passwordFormButtons.changeState(state, password);
		passwordFormFields.changeState(state, password);
	}
	
	public boolean isEditing() {
		return state.isEditing();
	}
}
