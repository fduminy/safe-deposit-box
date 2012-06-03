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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

public class PasswordForm extends JPanel {

    private BindingGroup m_bindingGroup;
    private fr.duminy.safe.core.model.Password password;
    private JTextField nameJTextField;
    private JTextField passwordJTextField;

    public PasswordForm(fr.duminy.safe.core.model.Password newPassword) {
        this();
        setPassword(newPassword);
    }

    public PasswordForm() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0 };
        gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
        gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0E-4 };
        setLayout(gridBagLayout);

        JLabel nameLabel = new JLabel("Name:");
        GridBagConstraints labelGbc_0 = new GridBagConstraints();
        labelGbc_0.insets = new Insets(5, 5, 5, 5);
        labelGbc_0.gridx = 0;
        labelGbc_0.gridy = 0;
        add(nameLabel, labelGbc_0);

        nameJTextField = new JTextField();
        GridBagConstraints componentGbc_0 = new GridBagConstraints();
        componentGbc_0.insets = new Insets(5, 0, 5, 5);
        componentGbc_0.fill = GridBagConstraints.HORIZONTAL;
        componentGbc_0.gridx = 1;
        componentGbc_0.gridy = 0;
        add(nameJTextField, componentGbc_0);

        JLabel passwordLabel = new JLabel("Password:");
        GridBagConstraints labelGbc_1 = new GridBagConstraints();
        labelGbc_1.insets = new Insets(5, 5, 5, 5);
        labelGbc_1.gridx = 0;
        labelGbc_1.gridy = 1;
        add(passwordLabel, labelGbc_1);

        passwordJTextField = new JTextField();
        GridBagConstraints componentGbc_1 = new GridBagConstraints();
        componentGbc_1.insets = new Insets(5, 0, 5, 5);
        componentGbc_1.fill = GridBagConstraints.HORIZONTAL;
        componentGbc_1.gridx = 1;
        componentGbc_1.gridy = 1;
        add(passwordJTextField, componentGbc_1);

        if (password != null) {
            m_bindingGroup = initDataBindings();
        }
    }

    protected BindingGroup initDataBindings() {
        BeanProperty<fr.duminy.safe.core.model.Password, java.lang.String> nameProperty = BeanProperty
                .create("name");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty = BeanProperty
                .create("text");
        AutoBinding<fr.duminy.safe.core.model.Password, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding = Bindings
                .createAutoBinding(AutoBinding.UpdateStrategy.READ, password,
                        nameProperty, nameJTextField, textProperty);
        autoBinding.bind();
        //
        BeanProperty<fr.duminy.safe.core.model.Password, java.lang.String> passwordProperty = BeanProperty
                .create("password");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty_1 = BeanProperty
                .create("text");
        AutoBinding<fr.duminy.safe.core.model.Password, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding_1 = Bindings
                .createAutoBinding(AutoBinding.UpdateStrategy.READ, password,
                        passwordProperty, passwordJTextField, textProperty_1);
        autoBinding_1.bind();
        //
        BindingGroup bindingGroup = new BindingGroup();
        bindingGroup.addBinding(autoBinding);
        bindingGroup.addBinding(autoBinding_1);
        //
        return bindingGroup;
    }

    public fr.duminy.safe.core.model.Password getPassword() {
        return password;
    }

    public void setPassword(fr.duminy.safe.core.model.Password newPassword) {
        setPassword(newPassword, true);
    }

    public void setPassword(fr.duminy.safe.core.model.Password newPassword,
            boolean update) {
        password = newPassword;
        if (update) {
            if (m_bindingGroup != null) {
                m_bindingGroup.unbind();
                m_bindingGroup = null;
            }
            if (password != null) {
                m_bindingGroup = initDataBindings();
            }
        }
    }

}
