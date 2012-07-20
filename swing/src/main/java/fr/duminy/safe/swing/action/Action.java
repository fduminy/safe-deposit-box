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
package fr.duminy.safe.swing.action;

import org.jdesktop.swingx.action.ActionFactory;
import org.jdesktop.swingx.action.ActionManager;

public enum Action {
	CREATE_PASSWORD("create-password", "+", true),
	EDIT_PASSWORD("edit-password", "Edit", false),	
	ADD_PASSWORD("add-password", "Add", false),
	CANCEL_EDITION("cancel-edition", "Cancel", false),
	UPDATE_PASSWORD("update-password", "Update", false),
	REMOVE_PASSWORD("remove-password", "-", false),
	EXIT("exit", "Exit", true);
	
	private final String name;
	private final String label;
	private final boolean initEnabled;
	private Action(String name, String label, boolean initEnabled) {
		this.name = name;
		this.label = label;
		this.initEnabled = initEnabled;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}

	public String getLabel() {
		return label;
	}

	public void setEnabled(boolean enabled) {
		ActionManager.getInstance().setEnabled(name, enabled);		
	}

	public javax.swing.Action toSwingAction() {
		return ActionManager.getInstance().getAction(name);
	}

	public static void init() {
   	 	ActionManager manager = ActionManager.getInstance();
    	for (Action a : Action.values()) {
    		javax.swing.Action action = ActionFactory.createTargetableAction(a.getName(), a.getLabel()); 
    		action.setEnabled(a.initEnabled);
    		manager.addAction(action);
    	}
	}
}
