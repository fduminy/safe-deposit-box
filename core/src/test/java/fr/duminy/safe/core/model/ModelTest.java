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

import static fr.duminy.safe.core.TestUtils.password;
import static org.fest.assertions.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import fr.duminy.safe.core.CoreException;

public class ModelTest {
	@Test
    public void testAddPasswordToRootCategory() {
    	testAddPassword(true);
    }

    @Test
    public void testAddPassword() {
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
    	
    	final Visitor visitor = new Visitor(category, password);
    	model.getRootCategory().accept(visitor);
    	
    	assertThat(model.getPasswords()).containsExactly(password);
    	assertThat(visitor.getFoundPassword()).isNotNull().isEqualTo(password);
    	if (rootCategory) {
    		assertThat(visitor.getPath()).isNotNull().containsExactly(model.getRootCategory());
    	} else {
    		assertThat(visitor.getPath()).isNotNull().containsExactly(model.getRootCategory(), category);
    	}
    }
    
    @Test
    public void testRemovePassword() {
    	Model model = new Model();    	
    	assertThat(model.getPasswords()).isNotNull().isEmpty();
    	
    	Password p = password("name", "password");
    	model.addPassword(p);
    	assertThat(model.getPasswords()).hasSize(1).containsExactly(p);
    	
    	model.removePassword(p);
    	assertThat(model.getPasswords()).isEmpty();
    }

    @Test
    public void testGetPasswords() {
    	Model model = new Model();    	
    	assertThat(model.getPasswords()).isNotNull().isEmpty();
    	
    	Password p = password("name", "password");
    	model.addPassword(p);
    	assertThat(model.getPasswords()).hasSize(1).containsExactly(p);
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
    	Model model = new Model();
    	Password p = password("name", "password");
    	model.addPassword(p);
    	
    	Model model2 = new Model();
    	Password p2 = password("name2", "password2");
    	model2.addPassword(p2);
    	
    	model.add(model2);
    	
    	Visitor visitor = new Visitor(model2.getRootCategory(), p2);
    	model.getRootCategory().accept(visitor);
    	
    	assertThat(model.getPasswords()).containsExactly(p, p2);
    	assertThat(visitor.getFoundPassword()).isEqualTo(p2);
    	assertThat(visitor.getPath()).containsExactly(model.getRootCategory(), model2.getRootCategory());
	}
    
    private static class Visitor implements CategoryVisitor {
    	private final Category categoryToSearch;
	    private final Password passwordToSearch;
	    
	    private List<Category> path;
		private Category currentCategory;
		private Password foundPassword;
		
		public Visitor(Category categoryToSearch, Password passwordToSearch) {
			super();
			this.categoryToSearch = categoryToSearch;
			this.passwordToSearch = passwordToSearch;
		}

		@Override
		public void visit(Password p) {
			if (currentCategory == categoryToSearch) {
				path = currentCategory.getPath();
				if (p == passwordToSearch) {
					foundPassword = p;
				}
			}
		}
		
		@Override
		public void visit(Category category) {
			currentCategory = category;				
		}
		
		public List<Category> getPath() {
			return path;
		}
		
		public Password getFoundPassword() {
			return foundPassword;
		}
	}
}
