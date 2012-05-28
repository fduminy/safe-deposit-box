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

import javax.swing.JFrame;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorLevel;
import org.jdesktop.swingx.error.ErrorReporter;
import org.picocontainer.MutablePicoContainer;

import fr.duminy.safe.core.Core;

public class SwingCore extends Core {
    public SwingCore() throws Exception {
        super();
    }

    @Override
    protected void init(MutablePicoContainer container) throws Exception {
        super.init(container);
        
        container.addComponent(SwingSystem.class);
    }

    @Override
    protected void displayError(String message, Exception e) {
        ErrorInfo info = new ErrorInfo("Error", message, null, "Technical", e, ErrorLevel.FATAL, null);
        JFrame frame = ((SingleFrameApplication) Application.getInstance()).getMainFrame();
        
        JXErrorPane pane = new JXErrorPane();
        pane.setErrorInfo(info);
        pane.setErrorReporter(new ErrorReporter() {            
            @Override
            public void reportError(ErrorInfo info) throws NullPointerException {
                // TODO report the error
            }
        });
        JXErrorPane.showDialog(frame, pane);
    }
}
