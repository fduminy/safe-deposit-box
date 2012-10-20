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
import static fr.duminy.safe.swing.MessageKey.ACTION_ADD;
import static fr.duminy.safe.swing.MessageKey.ACTION_CANCEL;
import static fr.duminy.safe.swing.MessageKey.ACTION_EDIT;
import static fr.duminy.safe.swing.MessageKey.ACTION_UPDATE;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.swing.JButton;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import fr.duminy.safe.core.model.Password;
import fr.duminy.safe.swing.action.Action;

@RunWith(Theories.class)
public class PasswordFormButtonsTest {
	@DataPoint
	public static final Password NO_PASSWORD = null;
	
	@DataPoint
	public static final Password PASSWORD = new Password("name", "pass");
	
	private PasswordFormButtons pfb;
	
	@BeforeClass
	public static void setUpClass() {
		Action.init();
	}
	
	@Before
	public void setUp() {
		pfb = new PasswordFormButtons();
	}

	@After
	public void tearDown() {
		pfb = null;
	}
	
	@Test
	public void testInitialState() {
		assertState("initial state", null, ACTION_CANCEL);
	}

	@Theory
	public void testCreateState(Password pwd) {
		pfb.changeState(CREATE, pwd);
		assertState("create", (pwd == null) ? null : ACTION_ADD, ACTION_CANCEL);
	}

	@Theory
	public void testReadState(Password pwd) {
		pfb.changeState(READ, pwd);
		assertState("read", (pwd == null) ? null : ACTION_EDIT, null);
	}

	@Theory
	public void testUpdateState(Password pwd) {
		pfb.changeState(UPDATE, pwd);
		assertState("update", (pwd == null) ? null : ACTION_UPDATE, ACTION_CANCEL);
	}
	
	private void assertState(String message, MessageKey mainLabelKey, MessageKey cancelLabelKey) {
		StringBuilder sb = new StringBuilder(message).append(" state\n");
		
		JButton mainButton = pfb.getMainButton();
		boolean ok = assertState(sb, mainButton, mainLabelKey);
		
		JButton cancelButton = pfb.getCancelButton();		
		ok &= assertState(sb, cancelButton, cancelLabelKey);
		
		assertTrue(sb.toString(), ok);
	}

	private boolean assertState(StringBuilder sb, JButton button, MessageKey labelKey) {
		boolean ok = true;
		boolean expectedVisible = (labelKey != null);
		boolean actualVisible = Arrays.asList(pfb.getComponents()).contains(button);
		if (actualVisible != expectedVisible) {			
			sb.append(button.getName()).append(": expectedVisible=").append(expectedVisible);
			sb.append(" actualVisible=").append(actualVisible).append('\n');
			ok = false;
		}
		
		if (ok && actualVisible) {
			String label = Messages.getString(labelKey);
			if (!label.equals(button.getText())) {
				sb.append(button.getName()).append(": expectedLabel='").append(label).append("'");
				sb.append(" actualLabel='").append(button.getText()).append("'").append('\n');
				ok = false;
			}
		}
		
		return ok;
	}
}
