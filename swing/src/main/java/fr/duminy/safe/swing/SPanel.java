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

import java.awt.Component;

import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.action.TargetManager;
import org.jdesktop.swingx.action.Targetable;
import org.picocontainer.Disposable;

import fr.duminy.safe.swing.command.Command;
import fr.duminy.safe.swing.command.CommandSupport;

@SuppressWarnings("serial")
public class SPanel extends JXPanel implements Targetable, Disposable {
	private CommandSupport support = new CommandSupport();
	
	public SPanel() {
		TargetManager.getInstance().addTarget(this);
	}
	
	@Override
	public final boolean doCommand(Object command, Object value) {
		return support.doCommand(command, value);
	}
	@Override
	public final boolean hasCommand(Object command) {
		return support.hasCommand(command);
	}
	@Override
	public final String[] getCommands() {
		return support.getCommands();
	}
	
	@Override
	public void dispose() {
		if (support != null) {
			for (Component c : getComponents()) {
				if (c instanceof Disposable) {
					((Disposable) c).dispose();
				}
			}
			removeAll();
			support.removeCommands();
			TargetManager.getInstance().removeTarget(this);
			support = null;
		}
	}

	public final void addCommand(Command command) {
		support.addCommand(command);
	}
}
