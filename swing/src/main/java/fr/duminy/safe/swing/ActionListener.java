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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

abstract public class ActionListener implements KeyListener, MouseListener, MouseWheelListener, MouseMotionListener {
    
    abstract void actionPerformed();
    
    @Override
    public void mouseDragged(MouseEvent e) {
        actionPerformed();        
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        actionPerformed();        
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        actionPerformed();        
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        actionPerformed();        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        actionPerformed();        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        actionPerformed();        
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        actionPerformed();        
    }

    @Override
    public void mouseExited(MouseEvent e) {
        actionPerformed();        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        actionPerformed();        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        actionPerformed();        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        actionPerformed();        
    }
}
