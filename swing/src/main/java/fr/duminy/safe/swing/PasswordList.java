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
package fr.duminy.safe.swing;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.swingx.JXSearchPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.sort.RowFilters;

import fr.duminy.safe.core.model.Password;

@SuppressWarnings("serial")
public class PasswordList extends JXTitledPanel {
    private final JXTable table;
    
    /**
     * Create the panel.
     * @throws IOException 
     */
    public PasswordList() throws IOException {
        table = new JXTable();
        //JXFindBar findBar = new JXFindBar(new TableSearchable(table));
        
        JXSearchPanel findBar = new JXSearchPanel();        
        
        setLayout(new BorderLayout());
        add(findBar, BorderLayout.NORTH);        
        add(new JScrollPane(table), BorderLayout.CENTER);
                
        setTitle("Passwords");
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TableModel m = table.getModel();
                    PasswordForm form = new PasswordForm();
                    form.setPassword(new Password(""));
                    add(form, BorderLayout.SOUTH);
                }
            }
        });
    }
    
    public void setPasswords(final List<Password> passwords) {
        TableModel m = new AbstractTableModel() {
            @Override
            public String getColumnName(int column) {
                return (column == 0) ? "Name" : "Password";
            }
            @Override
            public int getRowCount() {
                return passwords.size();
            }

            @Override
            public int getColumnCount() {
                return 2;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                Password p = passwords.get(rowIndex); 
                return (columnIndex == 0) ? p.getName() : p.getPassword();
            }
        };
        table.setModel(m);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(m);
        table.setRowSorter(sorter);
        //Pattern pattern = findBar.getPattern();
        sorter.setRowFilter(RowFilters.regexFilter(Pattern.compile(".*"), 0));
    }
}
