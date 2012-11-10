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

import static fr.duminy.safe.core.TestUtils.PASSWORD_COMPARATOR;
import static fr.duminy.safe.core.TestUtils.category;
import static fr.duminy.safe.core.TestUtils.password;
import static fr.duminy.safe.core.assertions.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.assertThat;

import java.io.IOException;
import java.io.StringReader;

import org.junit.experimental.theories.Theory;

import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Model;

/**
 * @author fabien
 */
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
		Model model = doImportPasswords(importer, csvData);
		
		assertThat(model).isNotNull();
		
		assertThat(model.getPasswords()).isNotNull().hasSize(2);
		assertThat(model.getPasswords()).usingElementComparator(PASSWORD_COMPARATOR).containsExactly(
				password("title1", "pass1"), password("title2", "pass2"));
		
		Category root = category("root");
		assertThat(model.getRootCategory()).isNotNull();
		assertThat(model.getRootCategory()).isEqualsTo(root);
		
		Category expected = category("messagerie");
		Category actual = Finders.findCategory(model.getRootCategory(), expected.getName()).getFoundCategories().get(0); 
		assertThat(actual).isEqualsTo(expected);		
		assertThat(actual).hasPath(root, expected);
	}
	
	private void testImport_SingleLine(Importer importer, String[] columns) throws Exception {
		Model model = importPasswords(importer, columns, generateCsvRow("name1", "password1"));
		
		assertThat(model.getPasswords()).isNotNull().usingElementComparator(PASSWORD_COMPARATOR).containsExactly(
				password("name1", "password1"));
	}
	
	protected Model doImportPasswords(Importer importer, String csvData) throws IOException {
		return importer.read(new StringReader(csvData));
	}
}

