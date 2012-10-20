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

import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

import org.junit.experimental.theories.Theory;

import fr.duminy.safe.core.model.Password;

public class ImporterTest extends AbstractImporterTest {
	@Theory
	public void testImport_EmptyFile_NoHeaders(Importer importer) throws Exception {
		importPasswords(importer, null, "");
	}
	
	@Theory
	public void testImport_EmptyFile_Headers(Importer importer) throws Exception {
		importPasswords(importer, REQUIRED_COLUMNS);
	}
	
	@Theory
	public void testImport_SingleLine_NoHeaders(Importer importer) throws Exception {
		testImport_SingleLine(importer, null);
	}
	
	@Theory
	public void testImport_SingleLine_Headers(Importer importer) throws Exception {
		testImport_SingleLine(importer, REQUIRED_COLUMNS);
	}
	
	@Theory
	public void testImport_Bug1(Importer importer) throws Exception {
		String csvData = "messagerie,,,,,,,,,\"Web Site\"\n" +
		"title1,,,username1,pass1,www.site1.com,,,,\"Web Site\"\n" +
		"title2,,,username2,pass2,www.site2.com,,,,\"Web Site\"";
		Collection<Password> passwords = doImportPasswords(importer, csvData);		
		assertNotNull(passwords);
		assertEquals(2, passwords.size());		
		
		Iterator<Password> it = passwords.iterator();
		Password p = it.next();
		assertEquals("title1", p.getName());
		assertEquals("pass1", p.getPassword());
		
		p = it.next();
		assertEquals("title2", p.getName());
		assertEquals("pass2", p.getPassword());		
	}

	private void testImport_SingleLine(Importer importer, String[] columns) throws Exception {
		Collection<Password> passwords = importPasswords(importer, columns, generateCsvRow("name1", "password1"));
		Password p = passwords.iterator().next();
		assertEquals("name1", p.getName());
		assertEquals("password1", p.getPassword());
	}
	
	protected Collection<Password> doImportPasswords(Importer importer, String csvData) throws IOException {
		return importer.read(new StringReader(csvData));
	}
}

