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
package fr.duminy.safe.swing;

public enum MessageKey {
	PASSWORD_NAME("password.name"), //$NON-NLS-1$
	PASSWORD_VALUE("password.value"), //$NON-NLS-1$
	NAME("name"), //$NON-NLS-1$
	PASSWORD("password"), //$NON-NLS-1$
	PASSWORD_NOT_SAVED_MESSAGE("passwordNotSaved.message"), //$NON-NLS-1$
	PASSWORD_NOT_SAVED_TITLE("passwordNotSaved.title"), //$NON-NLS-1$
	ERROR("error"), //$NON-NLS-1$
	TECHNICAL("technical"), //$NON-NLS-1$
	ASK_PASSWORD("askPassword"), //$NON-NLS-1$
	LOCKED("locked"), //$NON-NLS-1$
	
	ACTION_CREATE("action.create"), //$NON-NLS-1$
	ACTION_EDIT("action.edit"), //$NON-NLS-1$
	ACTION_ADD("action.add"), //$NON-NLS-1$
	ACTION_CANCEL("action.cancel"), //$NON-NLS-1$
	ACTION_UPDATE("action.update"), //$NON-NLS-1$
	ACTION_REMOVE("action.remove"), //$NON-NLS-1$
	ACTION_EXIT("action.exit"), //$NON-NLS-1$
	ACTION_IMPORT("action.import"), //$NON-NLS-1$
	ACTION_ADD_CATEGORY("action.add.category"),
	ACTION_REMOVE_CATEGORY("action.remove.category"),
	ACTION_RENAME_CATEGORY("action.rename.category"),
	
	ACTION_CREATE_DESCRIPTION("action.create.description"), //$NON-NLS-1$
	ACTION_EDIT_DESCRIPTION("action.edit.description"), //$NON-NLS-1$
	ACTION_ADD_DESCRIPTION("action.add.description"), //$NON-NLS-1$
	ACTION_CANCEL_DESCRIPTION("action.cancel.description"), //$NON-NLS-1$
	ACTION_UPDATE_DESCRIPTION("action.update.description"), //$NON-NLS-1$
	ACTION_REMOVE_DESCRIPTION("action.remove.description"), //$NON-NLS-1$
	ACTION_EXIT_DESCRIPTION("action.exit.description"), //$NON-NLS-1$
	ACTION_IMPORT_DESCRIPTION("action.import.description"), //$NON-NLS-1$
	
	ACTION_ADD_CATEGORY_DESCRIPTION("action.add.category.description"),
	ACTION_REMOVE_CATEGORY_DESCRIPTION("action.remove.category.description"),
	ACTION_RENAME_CATEGORY_DESCRIPTION("action.rename.category.description");
	
	public static MessageKey fromKey(String key) {
		for (MessageKey m : values()) {
			if (m.getKey().equals(key)) {
				return m;
			}
		}
		throw new IllegalArgumentException("No MessageKey found for key '" + key + "'");
	}
	
	private final String key;
	private MessageKey(String key) {
		this.key = key;
	}
	
	public final String getKey() {
		return key;
	}
}
