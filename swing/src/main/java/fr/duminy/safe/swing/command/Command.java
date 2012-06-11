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
package fr.duminy.safe.swing.command;

import fr.duminy.safe.swing.action.Action;

abstract public class Command {
	private final Action action;
	
	public Command(Action action) {
		this.action = action;
	}
	
	public final String getName() {
		return action.getName();
	}
	
	abstract public void run();
	
	@Override
	public String toString() {
		return "Command[" + getName() + "]";
	}
}
