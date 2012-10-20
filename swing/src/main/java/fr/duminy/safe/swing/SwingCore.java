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

import static fr.duminy.safe.swing.MessageKey.ERROR;
import static fr.duminy.safe.swing.MessageKey.TECHNICAL;

import javax.swing.Action;
import javax.swing.JFrame;

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
    protected void init(MutablePicoContainer container) {
        super.init(container);
        
        container.addComponent(SwingSystem.class);
    }
    
    public void setApplication(SingleFrameApplication app) {
    	addComponent(app);
    }

    @Override
    protected void displayError(String message, Exception e) {
//        ErrorInfo info = new ErrorInfo(Messages.getString(ERROR), message, null, Messages.getString(TECHNICAL), e, ErrorLevel.FATAL, null);
    	ErrorInfo info = new ErrorInfo(Messages.getString(ERROR), message, null, Messages.getString(TECHNICAL), e, ErrorLevel.SEVERE, null);
        JFrame frame = getComponent(SingleFrameApplication.class).getMainFrame();
        
        JXErrorPane pane = new JXErrorPane();
        
        // set fatal action so that System.exit() won't be called directly
        Action fatalAction = fr.duminy.safe.swing.action.Action.EXIT.toSwingAction();
        pane.getActionMap().put(JXErrorPane.FATAL_ACTION_KEY, fatalAction);
        
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
