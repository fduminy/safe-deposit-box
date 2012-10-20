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

import static fr.duminy.safe.swing.MessageKey.ASK_PASSWORD;
import static fr.duminy.safe.swing.MessageKey.LOCKED;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.jdesktop.application.FrameView;

import fr.duminy.safe.core.system.AbstractSystem;
import fr.duminy.safe.core.system.PasswordListener;
import fr.duminy.safe.core.system.Timer;

public class SwingSystem extends AbstractSystem<JComponent> {
	private final SwingApplication application;
	
    public SwingSystem(SwingApplication application) {
    	this.application = application;
        ActionListener l = new ActionListener() {            
            @Override
            void actionPerformed() {
                fireActionPerformed();
            }
        };
        getApplication().getMainFrame().addKeyListener(l);
        getApplication().getMainFrame().addMouseListener(l);
        getApplication().getMainFrame().addMouseMotionListener(l);
        getApplication().getMainFrame().addMouseWheelListener(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timer createTimer(int delay) {
//        return new SwingTimer(delay);
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClipboardContent(String content) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JComponent getComponent() {
        return getMainView().getComponent();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void setComponent(JComponent view) {
        getMainView().setComponent(view);
        
        // workaround to force a refresh
        Dimension d = getMainView().getFrame().getSize();
        d.setSize(d.getWidth() + 1, d.getHeight() + 1);
        getMainView().getFrame().setSize(d);
        d.setSize(d.getWidth() - 1, d.getHeight() - 1);
        getMainView().getFrame().setSize(d);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected JComponent createLockComponent() {
        return new JLabel(Messages.getString(LOCKED));
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getClipboardContent() {
        // TODO Auto-generated method stub
        return null;
    }

    private SwingApplication getApplication() {
        return application;        
    }
    
    private FrameView getMainView() {
        return getApplication().getMainView();        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void askPassword(final PasswordListener passwordListener) {
        SwingUtilities.invokeLater(new Runnable() {            
            @Override
            public void run() {
                String password = JOptionPane.showInputDialog(Messages.getString(ASK_PASSWORD));
                if (password == null) {
                    passwordListener.passwordCancelled();
                } else {
                    passwordListener.passwordEntered(password);
                }
            }
        });
    }
}
