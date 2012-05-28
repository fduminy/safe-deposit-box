/**
 * Safe is a software managing your passwords in safe place.
 *
 * Copyright (C) 2012 Fabien DUMINY (fduminy at jnode dot org)
 *
 * Safe is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * Safe is distributed in the hope that it will be useful,
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

import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JMenuBar;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.View;


public class SwingApplication extends SingleFrameApplication {
    public static void main(String[] args) {
        Application.launch(SwingApplication.class, args);
    }
    
    private SwingCore core;
    
    @Override
    protected void initialize(String[] args) {
        super.initialize(args);
        try {
            core = new SwingCore();
            core.start();            
        } catch (Exception e) {
            throw new Error(e);
        }
    }
    
    @Override
    protected void shutdown() {
        super.shutdown();
        
        try {
            if (core != null) {
                core.stop();
                core = null;
            }
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }
    
    @Override 
    protected void startup() {
        View view = getMainView();
        view.setComponent(createMainComponent());
        view.setMenuBar(createMenuBar());
        show(view);
    }

    private JMenuBar createMenuBar() {
        return new JMenuBar();
    }

    private JComponent createMainComponent() {
        PasswordList v;
        try {
            v = new PasswordList();
            v.setPasswords(core.getPasswords());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return v;
    }
}
