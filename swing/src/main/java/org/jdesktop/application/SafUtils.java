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
package org.jdesktop.application;

import static org.jdesktop.application.Application.create;

import javax.swing.SwingUtilities;

public class SafUtils {
    public static void launchIt(final Class<? extends Application> applicationClass, final String[] args) {
		Runnable doCreateAndShowGUI = new Runnable() {
			public void run() {
				try {
					Application application = create(applicationClass);
					application.initialize(args);
					application.startup();
					application.waitForReady();
				} catch (Exception e) {
					String msg = String
							.format("Application %s failed to launch",
									applicationClass);
//					logger.log(Level.SEVERE, msg, e);
					throw new Error(msg, e);
				}
			}
		};
    	SwingUtilities.invokeLater(doCreateAndShowGUI);
    }
}
