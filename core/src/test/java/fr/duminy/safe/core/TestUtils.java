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

import static fr.duminy.safe.core.Utils.array;
import static fr.duminy.safe.core.Utils.newArray;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.junit.Assert;

import fr.duminy.safe.core.TestDataUtils.Node;
import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.finder.PasswordFinder.PasswordWithPath;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Password;

public class TestUtils {
    private TestUtils() {        
    }
        
    public static <T> int compare(List<T> path1,
			List<T> path2, Comparator<T> comparator) {
    	int result = 0;
		if (path1.size() != path2.size()) {
			result = path1.size() - path2.size();
		} else {
			for (int i = 0; i < path1.size(); i++) {
				T category1 = path1.get(i);
				T category2 = path2.get(i);
				result = comparator.compare(category1, category2);
				if (result != 0) {
					break;
				}
			}
		}
		return result;
	}			
    
    private static final class PasswordComparator implements Comparator<Password> {
		@Override
		public int compare(Password o1, Password o2) {
			int result = o1.getName().compareTo(o2.getName());
			if (result == 0) {
				 result = o1.getPassword().compareTo(o2.getPassword());
			}
			return result;
		}			
	};
    public static final PasswordComparator PASSWORD_COMPARATOR = new PasswordComparator();

    private static final class PasswordWithPathComparator implements Comparator<PasswordWithPath> {
		@Override
		public int compare(PasswordWithPath o1, PasswordWithPath o2) {
			int result = PASSWORD_COMPARATOR.compare(o1.getPassword(), o2.getPassword());
			if (result == 0) {
				List<Category> path1 = o1.getPath();
				List<Category> path2 = o2.getPath();
				result = TestUtils.compare(path1, path2, CATEGORY_COMPARATOR);
			}
			return result;
		}
	};
    public static final PasswordWithPathComparator PASSWORD_WITH_PATH_COMPARATOR = new PasswordWithPathComparator();
    
    private static final class CategoryComparator implements Comparator<Category>  {
		@Override
		public int compare(Category o1, Category o2) {
			return o1.getName().compareTo(o2.getName());
		}			
	};
	public static final CategoryComparator CATEGORY_COMPARATOR = new CategoryComparator();
	    
	public static Collection<PasswordWithPath> allPasswordsWithPath(Category category) {
		return Finders.findPassword(category, null, null, true).getPasswordsWithPath();		
	}

	public static Collection<Password> allPasswords(Category category) {
		return Finders.findPassword(category, null, null, true).getPasswords();		
	}
	
	public static <T> T[] join(Class<T> clazz, T[]... arrays) {
		int size = 0;
		for (T[] array : arrays) {
			size += array.length;
		}
		T[] result = newArray(clazz, size);
		int i = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, result, i, array.length);
			i += array.length;
		}
		return result;
	}
	
	public static Category[] join(Collection<Category> list, Category... items) {
		return join(Category.class, array(list, Category.class), items);
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
    
    public static class CategoryFactory {
        public final Category category(Node node) {
        	return category(node.getCategoryName());
        }
        
        public Category category(String name) {
    		return new Category(name);
    	}
    }
    
    public static Password password(String name) {
    	return password(name, name + "_pwd");
    }
    
    public static Password password(String name, String password) {
		return new Password(name, password);
	}
	
    public static Category category(Node node) {
    	return category(node.getCategoryName());
    }
    
    public static Category category(String name) {
		return new Category(name);
	}

	public static Category[] remove(Category[] categories, Category... categoriesToRemove) {
		List<Category> list = new ArrayList<Category>(Arrays.asList(categories));
		list.removeAll(Arrays.asList(categoriesToRemove));
    	return array(list);
    }
        
    /**
     * TODO use FEST assert 2.9-SNAPSHOT or more, supporting this kid of assertion.
     * @param list
     */
    public static <T> void assertThatIsUnmodifiable(List<T> list) {
    	if (list != null) {
	    	try {
	    		list.clear();
	    		Assert.fail("list must not be modifiable");
	    	} catch(UnsupportedOperationException uoe) {
	    		return;
	    	}
    	}
    }

	public static <T> T[] transform(T[] items, Transformer<T> transformer, MutableInteger transformationCounter) {
		if (transformer != null) {
			for (int i = 0; i < items.length; i++) {
				items[i] = transformer.transform(items[i], transformationCounter);
			}
		}
		
		return items;
	}
}
