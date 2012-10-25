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
package fr.duminy.safe.core.model;

import org.junit.Test;

import fr.duminy.safe.core.TestClass;

public class NamedListTest {

	@Test
	public void testAdd() {
		NamedList<TestClass> list = new NamedList<TestClass>("TestClass");
		list.add(new TestClass("ABC"));
	}
	
	@Test(expected = DuplicateNameException.class)
	public void testAddDuplicate() {
		NamedList<TestClass> list = new NamedList<TestClass>("TestClass");
		String name = "ABC";
		list.add(0, new TestClass(name));
		list.add(1, new TestClass(name));
	}

	@Test
	public void testRemove() {
		NamedList<TestClass> list = createList("ABC", "DEF");
		list.remove(1);
		list.remove(0);
	}
	
	@Test
	public void testSet() {
		NamedList<TestClass> list = createList("ABC", "DEF");
		list.set(1, new TestClass("UVW"));
	}
	
	@Test(expected = DuplicateNameException.class)
	public void testSetDuplicate() {
		String abc = "ABC";
		NamedList<TestClass> list = createList(abc, "DEF");
		list.set(1, new TestClass(abc));
	}
	
	@Test
	public void testSetReplaceWithSameName() {
		String abc = "ABC";
		NamedList<TestClass> list = createList(abc, "DEF");
		list.set(0, new TestClass(abc));
	}
	
	private NamedList<TestClass> createList(String... names) {
		NamedList<TestClass> list = new NamedList<TestClass>("TestClass");
		for (String name : names) {
			list.add(new TestClass(name));
		}
		return list;
	}
}
