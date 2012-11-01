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

import static org.fest.assertions.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.duminy.safe.core.TestClass;

public class NamedListTest {

	@Test
	public void testAdd() {
		NamedList<TestClass> list = new NamedList<TestClass>("TestClass");
		TestClass tc = new TestClass("ABC");
		
		list.add(tc);
		
		assertThat(list).containsExactly(tc);
	}
	
	@Test
	public void testAddDuplicate() {
		NamedList<TestClass> list = new NamedList<TestClass>("TestClass");
		String name = "ABC";
		
		list.add(0, new TestClass(name));		
		
		try {
			list.add(1, new TestClass(name));
		} catch (Throwable t) {
			assertThat(t).isInstanceOf(DuplicateNameException.class);
		}
	}

    @Test
    public void testRemove_DifferentName() {
    	testRemove(false, false);
    }

    @Test
    public void testRemove_SameInstance() {
    	testRemove(true, true);
    }

    @Test
    public void testRemove_DifferentInstance() {
    	testRemove(true, false);
    }
    
    private void testRemove(boolean sameName, boolean sameTestClassInstance) {
    	List<TestClass> createdTestClasses = new ArrayList<TestClass>();
		NamedList<TestClass> list = createList(createdTestClasses, "ABC", "DEF");
		TestClass tc1 = createdTestClasses.get(0);
		TestClass tc2 = createdTestClasses.get(1);
		
    	TestClass tcToRemove = sameTestClassInstance ? tc1 : testClass(sameName ? tc1.getName() : "wrongName");
		list.remove(tcToRemove);

    	if (sameName || sameTestClassInstance) {
    		assertThat(list).containsExactly(tc2);
    	} else {
    		assertThat(list).containsExactly(tc1, tc2);
    	}
    }
	
	@Test
	public void testSet() {
		NamedList<TestClass> list = createList(null, "ABC", "DEF");
		list.set(1, new TestClass("UVW"));
	}
	
	@Test(expected = DuplicateNameException.class)
	public void testSetDuplicate() {
		String abc = "ABC";
		NamedList<TestClass> list = createList(null, abc, "DEF");
		list.set(1, new TestClass(abc));
	}
	
	@Test
	public void testSetReplaceWithSameName() {
		String abc = "ABC";
		NamedList<TestClass> list = createList(null, abc, "DEF");
		list.set(0, new TestClass(abc));
	}
	
	private NamedList<TestClass> createList(List<TestClass> createdTestClasses, String... names) {
		NamedList<TestClass> list = new NamedList<TestClass>("TestClass");
		for (String name : names) {
			TestClass tc = testClass(name); 
			list.add(tc);
			
			if (createdTestClasses != null) {
				createdTestClasses.add(tc);
			}
		}
		return list;
	}
	
	private TestClass testClass(String name) {
		return new TestClass(name);
	}
}
