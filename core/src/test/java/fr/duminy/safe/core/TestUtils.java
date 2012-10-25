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
package fr.duminy.safe.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.Collection;

import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Password;

public class TestUtils {
    private TestUtils() {        
    }
        
    public static void assertArrayNotEquals(String message, byte[] expected,
            byte[] actual) {
        boolean equal;
        try {
            assertArrayEquals(message, expected, actual);
            equal = true;
        } catch (AssertionError e) {
            equal = false;
        }
        assertTrue(message, !equal);
    }
    
    public static byte[] createTestData(int size) {
        byte[] data = new byte[size];
        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) i;
        }
        return data;
    }
    	
    public static Password password(String name, String password) {
		return new Password(name, password);
	}
	
    public static Category category(String name) {
		return new Category(name);
	}
    
    @SuppressWarnings("unchecked")
	public static <T> T[] array(Collection<T> values) {    	
    	T[] a = (T[]) Array.newInstance(values.isEmpty() ? Object.class : values.iterator().next().getClass(), values.size());
    	return values.toArray(a);    	
    }
}
