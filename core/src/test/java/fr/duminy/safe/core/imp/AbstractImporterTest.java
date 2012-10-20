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
import java.util.Collection;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

import au.com.bytecode.opencsv.CSVWriter;
import fr.duminy.safe.core.CoreTest.FakeCore;
import fr.duminy.safe.core.model.Password;

@RunWith(Theories.class)
abstract public class AbstractImporterTest {
	@DataPoints public static final Importer[] IMPORTERS = new FakeCore().getImporters().toArray(new Importer[0]);
		
	protected static final String[] REQUIRED_COLUMNS = CsvImporter.COLUMN_NAMES; 
	
	protected final static String[] generateCsvRow(String name, String password) {
		return new String[]{name, "", "", "", password, "", "", "", "", ""};		
	}
	
	protected final Collection<Password> importPasswords(Importer importer, String[] columns, String... values) throws Exception {
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
		
		Collection<Password> passwords = doImportPasswords(importer, writer.toString());
		
		assertNotNull(passwords);
		assertEquals((values.length / nbColumns), passwords.size());
		
		return passwords;
	}
	
	abstract protected Collection<Password> doImportPasswords(Importer importer, String csvData) throws Exception;
}
