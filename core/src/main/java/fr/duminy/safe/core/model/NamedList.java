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

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NamedList<T extends Named> extends AbstractList<T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7792630680780973576L;

	private final String elementName;
	
	private final Set<String> names = new HashSet<String>();
	private final List<T> list = new ArrayList<T>();
	
	public NamedList(String elementName) {
		this.elementName = elementName;
	}
	
	@Override
    public void add(int index, T element) {
    	checkDuplicateName(element, Integer.MIN_VALUE);
        list.add(index, element);
        
        names.add(element.getName());
    }
	
	@Override
	public T set(int index, T element) {
		checkDuplicateName(element, index);
		T e = list.set(index, element);
		
		if (e != null) {
			names.remove(e.getName());
		}
    	names.add(element.getName());
    	return e;
	}

	@Override
	public T remove(int index) {
		T o = list.remove(index);
		names.remove(o.getName());
		return o; 
	}

	@Override
	public T get(int index) {
		return list.get(index);
	}
	
	@Override
	public int size() {
		return list.size();
	}
	
	public boolean contains(String name) {
		return names.contains(name);
	}

	private void checkDuplicateName(T element, int excludeIndex) {
		if (contains(element.getName())) {
			if (excludeIndex >= 0) {
				T e = list.get(excludeIndex);
				if ((e != null) && !e.getName().equals(element.getName())) {
					throw new DuplicateNameException(elementName, element.getName());
				}
			} else {
				throw new DuplicateNameException(elementName, element.getName());
			}
        }
	}
}
