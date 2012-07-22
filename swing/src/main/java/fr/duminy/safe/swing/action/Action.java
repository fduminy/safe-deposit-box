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

import static fr.duminy.safe.swing.MessageKey.ACTION_ADD;
import static fr.duminy.safe.swing.MessageKey.ACTION_CANCEL;
import static fr.duminy.safe.swing.MessageKey.ACTION_CREATE;
import static fr.duminy.safe.swing.MessageKey.ACTION_EDIT;
import static fr.duminy.safe.swing.MessageKey.ACTION_EXIT;
import static fr.duminy.safe.swing.MessageKey.ACTION_IMPORT;
import static fr.duminy.safe.swing.MessageKey.ACTION_REMOVE;
import static fr.duminy.safe.swing.MessageKey.ACTION_UPDATE;

import org.jdesktop.swingx.action.ActionFactory;
import org.jdesktop.swingx.action.ActionManager;

import fr.duminy.safe.swing.MessageKey;
import fr.duminy.safe.swing.Messages;

public enum Action {
	CREATE_PASSWORD("create-password", ACTION_CREATE, true), //$NON-NLS-1$
	EDIT_PASSWORD("edit-password", ACTION_EDIT, false),	 //$NON-NLS-1$
	ADD_PASSWORD("add-password", ACTION_ADD, false), //$NON-NLS-1$
	CANCEL_EDITION("cancel-edition", ACTION_CANCEL, false), //$NON-NLS-1$
	UPDATE_PASSWORD("update-password", ACTION_UPDATE, false), //$NON-NLS-1$
	REMOVE_PASSWORD("remove-password", ACTION_REMOVE, false), //$NON-NLS-1$
	EXIT("exit", ACTION_EXIT, true), //$NON-NLS-1$
	IMPORT("import", ACTION_IMPORT, true); //$NON-NLS-1$
	
	private final String name;
	private final MessageKey labelKey;
	private final boolean initEnabled;
	private Action(String name, MessageKey labelKey, boolean initEnabled) {
		this.name = name;
		this.labelKey = labelKey;
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
		return Messages.getString(labelKey);
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
