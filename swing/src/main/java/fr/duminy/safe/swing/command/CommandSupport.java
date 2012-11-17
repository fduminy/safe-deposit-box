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
package fr.duminy.safe.swing.command;

import java.util.HashMap;
import java.util.Map;

import org.jdesktop.swingx.action.Targetable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.duminy.safe.core.Core;

public class CommandSupport implements Targetable {
    private static final Logger LOG = LoggerFactory.getLogger(CommandSupport.class);
	
    private final Core core;
	private final Map<String, Command> commands = new HashMap<String, Command>(); 
	
	public CommandSupport(Core core) {
		if (core == null) {
			throw new Error("core is null");
		}
		this.core = core;
	}
	
	public void addCommand(Command command) {
		LOG.debug("adding command '{}'", command.getName());
		commands.put(command.getName(), command);
	}
	
	@Override
	public boolean doCommand(Object command, Object value) {
		boolean result = false;
		String cmdName = convertCommandtoString(command);
		Command cmd = commands.get(cmdName);
		if (cmd != null) {
			LOG.debug("executing command '{}'", cmdName);
			try {
				cmd.run();
			} catch (Exception e) {
				core.reportError(e.getMessage(), e);
			}
			result = true;
		} else {
			LOG.debug("no command for name '{}'", cmdName);
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
	
	public void removeCommands() {
		LOG.debug("clearing commands {}", commands.keySet());
		commands.clear();
	}
	
	private final String convertCommandtoString(Object command) {
		return String.valueOf(command);
	}
}
