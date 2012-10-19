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

import static fr.duminy.safe.swing.MessageKey.PASSWORD_NAME;
import static fr.duminy.safe.swing.MessageKey.PASSWORD_VALUE;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.duminy.safe.core.model.Password;

@SuppressWarnings("serial")
public class PasswordFormFields extends JPanel {
    private JTextField nameTextField;
    private JTextField passwordTextField;

    public PasswordFormFields() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0E-4 };
        setLayout(gridBagLayout);

        JLabel nameLabel = new JLabel(Messages.getString(PASSWORD_NAME));
        GridBagConstraints labelGbc_0 = new GridBagConstraints();
        labelGbc_0.insets = new Insets(5, 5, 5, 5);
        labelGbc_0.gridx = 0;
        labelGbc_0.gridy = 0;
        add(nameLabel, labelGbc_0);

        nameTextField = new JTextField();
        nameTextField.setName("nameTextField"); //$NON-NLS-1$
        GridBagConstraints gbc_nameTextField = new GridBagConstraints();
        gbc_nameTextField.insets = new Insets(5, 0, 5, 5);
        gbc_nameTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_nameTextField.gridx = 1;
        gbc_nameTextField.gridy = 0;
        add(nameTextField, gbc_nameTextField);

        JLabel passwordLabel = new JLabel(Messages.getString(PASSWORD_VALUE));
        GridBagConstraints labelGbc_1 = new GridBagConstraints();
        labelGbc_1.insets = new Insets(5, 5, 5, 5);
        labelGbc_1.gridx = 0;
        labelGbc_1.gridy = 1;
        add(passwordLabel, labelGbc_1);

        passwordTextField = new JTextField();
        passwordTextField.setName("passwordTextField"); //$NON-NLS-1$
        GridBagConstraints gbc_passwordTextField = new GridBagConstraints();
        gbc_passwordTextField.insets = new Insets(5, 0, 5, 5);
        gbc_passwordTextField.fill = GridBagConstraints.HORIZONTAL;
        gbc_passwordTextField.gridx = 1;
        gbc_passwordTextField.gridy = 1;
        add(passwordTextField, gbc_passwordTextField);
    }

    public Password getPassword() {
        return new Password(nameTextField.getText(), passwordTextField.getText());
    }

	public void changeState(FormState state, Password password) {
		nameTextField.setText((password == null) ? "" : password.getName()); //$NON-NLS-1$
		passwordTextField.setText((password == null) ? "" : password.getPassword()); //$NON-NLS-1$
		
		boolean editing = state.isEditing();
	    nameTextField.setEditable(editing);
	    passwordTextField.setEditable(editing);
	}

}
