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

import static fr.duminy.safe.swing.FormState.READ;

import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.swing.action.Action;

@SuppressWarnings("serial")
public class PasswordFormButtons extends JPanel {
    private static final Logger LOG = LoggerFactory.getLogger(PasswordFormButtons.class);
    
	private final JButton mainButton;
	private final JButton cancelButton;
	
	/**
	 * Create the panel.
	 */
	public PasswordFormButtons() {
		setLayout(new GridLayout(1, 0, 0, 0));
		
		mainButton = new JButton("Main Action"); //$NON-NLS-1$
		mainButton.setName("mainButton"); //$NON-NLS-1$
		
		cancelButton = new JButton(Action.CANCEL_EDITION.toSwingAction()); //$NON-NLS-1$
		cancelButton.setName("cancelButton"); //$NON-NLS-1$

		changeState(null, null);
	}

	public void changeState(FormState state, Password password) {
		removeAll();
		
		if (!READ.equals(state)) {
			add(cancelButton);	
		}

		if (password != null) {
			add(mainButton);
		}
		
		javax.swing.Action mainAction = mainButton.getAction();
		if (mainAction != null) {
			mainAction.setEnabled(false);
		}
		
		if (state != null) {
			mainAction = state.getMainAction().toSwingAction();
			mainAction.setEnabled(true);
			mainButton.setAction(mainAction);
		}
		
		logState(state, password);		
	}
	
	private void logState(FormState state, Password password) {
		if (LOG.isDebugEnabled()) {
//			LOG.debug("changeState: callstack:", new Exception("callstack"));
			LOG.debug("changeState: state={} password={} components:", state, password); //$NON-NLS-1$
			for (Component c : getComponents()) {
				LOG.debug("c={} name={} visible={}", new Object[]{c.getClass().getName(), c.getName(), c.isVisible()});	 //$NON-NLS-1$
			}
			LOG.debug("--- end of components ---"); //$NON-NLS-1$
		}
	}

	public JButton getMainButton() {
		return mainButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}
}
