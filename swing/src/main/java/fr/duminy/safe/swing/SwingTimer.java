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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import fr.duminy.safe.core.system.Timer;

public class SwingTimer implements Timer {
    private final javax.swing.Timer timer;
    private Runnable runnable;
    
    public SwingTimer(int delay) {
        timer = new javax.swing.Timer(delay, new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
    }

    @Override
    public void start() {
        timer.start();        
    }

    @Override
    public void stop() {
        timer.stop();
    }

    @Override
    public void setAction(final Runnable runnable) {
        this.runnable = runnable;
    }
}
