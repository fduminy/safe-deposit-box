/**
 * Safe Deposit Box is a software managing your passwords in safe place.
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
package fr.duminy.safe.core.system;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class AbstractSystem<V> implements System {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSystem.class);
    
    private final List<ClipboardListener> clipboardListeners = new ArrayList<ClipboardListener>();
    private final List<UserActionListener> userActionListeners = new ArrayList<UserActionListener>();
    
    private V view;
    private V lockView;

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isScreenLocked() {
        return (view != null);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void lockScreen() {
        if (!isScreenLocked()) {
            view = getComponent();
            LOG.debug("lockScreen: view={}", view);
            
            if (lockView == null) {
                lockView = createLockComponent();
            }
            setComponent(lockView);
            LOG.debug("lockScreen: lockView={}, component={}", lockView, getComponent());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void unlockScreen() {
        if (isScreenLocked()) {
            LOG.debug("unlockScreen: view={}", view);
            setComponent(view);            
            view = null;
        }
    }
    
    abstract protected V getComponent();
    abstract protected void setComponent(V view);
    abstract protected V createLockComponent();
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void addClipboardListener(ClipboardListener l) {
        clipboardListeners.add(l);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeClipboardListener(ClipboardListener l) {
        clipboardListeners.remove(l);        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void addUserActionListener(UserActionListener l) {
        userActionListeners.add(l);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public final void removeUserActionListener(UserActionListener l) {
        userActionListeners.remove(l);        
    }
    
    protected final void fireClipboardChanged() {
        for (ClipboardListener l : clipboardListeners) {
            l.clipboardChanged(this);
        }
    }
    
    protected final void fireActionPerformed() {
        for (UserActionListener l : new ArrayList<UserActionListener>(userActionListeners)) {
            l.actionPerformed();
        }
    }
}
