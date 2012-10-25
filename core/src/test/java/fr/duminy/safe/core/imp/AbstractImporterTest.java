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
package fr.duminy.safe.core.imp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.StringWriter;
import java.util.Comparator;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import au.com.bytecode.opencsv.CSVWriter;
import fr.duminy.safe.core.CoreTest.FakeCore;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Model;
import fr.duminy.safe.core.model.Password;

@RunWith(Theories.class)
abstract public class AbstractImporterTest {
	@DataPoints public static final Importer[] IMPORTERS = new FakeCore().getImporters().toArray(new Importer[0]);
		
	protected static final String[] REQUIRED_COLUMNS = CsvImporter.COLUMN_NAMES;
	
	protected static final Comparator<Password> PASSWORD_COMPARATOR = new Comparator<Password>() {
		@Override
		public int compare(Password o1, Password o2) {
			int result = o1.getName().compareTo(o2.getName());
			if (result == 0) {
				 result = o1.getPassword().compareTo(o2.getPassword());
			}
			return result;
		}			
	};

	protected static final Comparator<Category> CATEGORY_COMPARATOR = new Comparator<Category>() {
		@Override
		public int compare(Category o1, Category o2) {
			return o1.getName().compareTo(o2.getName());
		}			
	};
	
	protected final static String[] generateCsvRow(String name, String password) {
		return new String[]{name, "", "", "", password, "", "", "", "", ""};		
	}
	
	protected final Model importPasswords(Importer importer, String[] columns, String... values) throws Exception {
		int nbColumns = (columns == null) ? REQUIRED_COLUMNS.length : columns.length;
		
		StringWriter writer = new StringWriter();
		CSVWriter csvWriter = new CSVWriter(writer);

		if (columns != null) {
			csvWriter.writeNext(columns);
		}
		
		int i = 0;
		while (i < values.length) {
			String[] line = new String[Math.min(values.length - i, nbColumns)];
			System.arraycopy(values, i, line, 0, line.length);
			
			csvWriter.writeNext(line);
			
			i += line.length;
		}
		
		csvWriter.close();
		
		Model model = doImportPasswords(importer, writer.toString());
		
		assertNotNull(model);
		assertNotNull(model.getPasswords());
		assertEquals((values.length / nbColumns), model.getPasswords().size());
		
		return model;
	}
	
	abstract protected Model doImportPasswords(Importer importer, String csvData) throws Exception;
}
