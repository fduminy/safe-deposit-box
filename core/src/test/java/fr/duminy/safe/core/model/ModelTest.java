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

import static fr.duminy.safe.core.TestUtils.CATEGORY_COMPARATOR;
import static fr.duminy.safe.core.TestUtils.assertThatIsUnmodifiable;
import static fr.duminy.safe.core.TestUtils.password;
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Test;

import fr.duminy.safe.core.CoreException;
import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordFinderResult;

public class ModelTest {
	@Test
    public void testAddPasswordToRootCategory() {
    	testAddPassword(true);
    }

    @Test
    public void testAddPasswordToChildCategory() {
    	testAddPassword(false);
    }

    private void testAddPassword(boolean rootCategory) {
    	final Model model = new Model();
    	final Password password = password("name", "password");
    	
    	final Category category;
    	if (rootCategory) { 
    		category = model.getRootCategory();
    		
        	model.addPassword(password);
    	} else {
    		category = new Category("A Category");
    		model.getRootCategory().add(category);
    		
    		model.addPassword(category, password);
    	}
    	
    	PasswordFinderResult result = Finders.findPassword(model.getRootCategory(), category.getName(), password.getName(), true);
    	
    	assertThat(model).containsExactly(password);
    	if (rootCategory) {
    		assertThat(result.getPasswordsWithPath().get(0).getPath()).isNotNull().containsExactly(model.getRootCategory());
    	} else {
    		assertThat(result.getPasswordsWithPath().get(0).getPath()).isNotNull().containsExactly(model.getRootCategory(), category);
    	}
    }
    
    @Test
    public void testRemovePassword_DifferentName() {
    	testRemovePassword(false, false);
    }

    @Test
    public void testRemovePassword_SamePasswordInstance() {
    	testRemovePassword(true, true);
    }

    @Test
    public void testRemovePassword_DifferentPasswordInstance() {
    	testRemovePassword(true, false);
    }
    
    private void testRemovePassword(boolean sameName, boolean samePasswordInstance) {
    	Model model = new Model();    	
    	assertThat(model).isEmpty();
    	
    	Password p = password("name", "password");
    	model.addPassword(p);
    	assertThat(model).containsExactly(p);
    	
    	Password pwdToRemove = samePasswordInstance ? p : password(sameName ? "name" : "wrongName", "password");
    	model.removePassword(pwdToRemove);
    	if (sameName || samePasswordInstance) {
    		assertThat(model).isEmpty();
    	} else {
    		assertThat(model).containsExactly(p);
    	}
    }
    
    @Test
    public void testGetPasswords() {
    	Model model = new Model();    	
    	assertThat(model).isEmpty();
    	
    	Password p = password("name", "password");
    	model.addPassword(p);
    	assertThat(model).containsExactly(p);
    	assertThatIsUnmodifiable(model.getPasswords());
    }

    @Test
    public void testGetRootCategory() {
    	Model model = new Model();
    	
    	assertThat(model.getRootCategory()).isNotNull();
    	assertThat(model.getRootCategory().getName()).isEqualTo("root");
    	assertThat(model.getRootCategory().getPath()).containsExactly(model.getRootCategory());
    }

    @Test
	public void testAddModel() throws CoreException {
    	// prepare models
    	Model model = new Model();
    	Password p = password("name", "password");
    	model.addPassword(p);
    	
    	Model model2 = new Model();
    	Password p2 = password("name2", "password2");
    	model2.addPassword(p2);
    	
    	// add a model to another
    	model.add(model2);
    	
    	// assertions
    	assertThat(model).containsExactly(p, p2);
    	
    	PasswordFinderResult result = Finders.findPassword(model.getRootCategory(), p2.getName(), true);    	
    	assertThat(Finders.getPasswords(result.getPasswordsWithPath())).containsExactly(p2);
    	assertThat(result.getPasswordsWithPath().get(0).getPath()).usingElementComparator(CATEGORY_COMPARATOR).containsExactly(model.getRootCategory());
	}
}
