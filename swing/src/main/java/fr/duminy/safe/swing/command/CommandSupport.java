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

import java.util.HashMap;
import java.util.Map;

import org.jdesktop.swingx.action.Targetable;

public class CommandSupport implements Targetable {
	private final Map<String, Command> commands = new HashMap<String, Command>(); 
	
	public void addCommand(Command command) {
		commands.put(command.getName(), command);
	}
	
	@Override
	public boolean doCommand(Object command, Object value) {
		boolean result = false;
		Command cmd = commands.get(convertCommandtoString(command));
		if (cmd != null) {
			cmd.run();
			result = true;
		}
		return result;
	}
	@Override
	public boolean hasCommand(Object command) {
		return commands.containsKey(convertCommandtoString(command));
	}
	@Override
	public String[] getCommands() {
		return commands.keySet().toArray(new String[commands.size()]);
	}
	
	private final String convertCommandtoString(Object command) {
		return String.valueOf(command);
	}
}
