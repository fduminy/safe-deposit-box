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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Enumeration;
import java.util.HashSet;

import org.junit.Test;

public class MessagesTest {
	@Test
	public void checkKeyUnicity() {
		HashSet<String> keys = new HashSet<String>();
		for (MessageKey k : MessageKey.values()) {
			assertFalse(keys.contains(k.getKey()));
			keys.add(k.getKey());
		}
	}
	
	@Test
	public void checkBundleKeysMatchesEnum() {
		Enumeration<String> bundleKeys = Messages.getKeys();
		while (bundleKeys.hasMoreElements()) {
			String bundleKey = bundleKeys.nextElement();
			
			boolean found = false;
			for (MessageKey k : MessageKey.values()) {
				if (k.getKey().equals(bundleKey)) {
					found = true;
					break;
				}
			}
			
			assertTrue(found);
		}
	}
	
	@Test
	public void checkEnumMatchesBundleKeys() {
		for (MessageKey k : MessageKey.values()) {
			boolean found = false;
			Enumeration<String> bundleKeys = Messages.getKeys();
			while (bundleKeys.hasMoreElements()) {
				String bundleKey = bundleKeys.nextElement();
				if (k.getKey().equals(bundleKey)) {
					found = true;
					break;
				}
			}
			
			assertTrue(found);
		}
	}
}
