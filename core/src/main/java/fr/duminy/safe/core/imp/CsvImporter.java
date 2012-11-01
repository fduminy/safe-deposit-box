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

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;
import fr.duminy.safe.core.Utils;
import fr.duminy.safe.core.finder.Finders;
import fr.duminy.safe.core.model.Category;
import fr.duminy.safe.core.model.Model;
import fr.duminy.safe.core.model.Password;

public class CsvImporter implements Importer {
    private static final Logger LOG = LoggerFactory.getLogger(CsvImporter.class);
    
    public static String TITLE = "TITLE";
    public static String PASSWORD = "PASSWORD";
    public static String TYPE = "TYPE";
    
    public static String[] REQUIRED_COLUMN_NAMES = {TITLE, PASSWORD};
    public static String[] COLUMN_NAMES = {TITLE, "CATEGORY", "EMAIL", "USERNAME", PASSWORD, "URL", "NOTES", "OTHER", "DATE_EXPIRES", TYPE};
    
    private static final FileFilter FILTER = new FileNameExtensionFilter("Secure Data Manager CSV (*.csv)", "csv");
    
	@Override
	public FileFilter getFileFilter() {
		return FILTER;
	}

	@Override
	public Model read(Reader reader) throws IOException {
		CSVReader csvReader = null;
		Model model = new Model();
		
		try {
		    csvReader = new CSVReader(reader);
		    String[] nextLine;
		    String[] headers = null;
		    boolean readHeaders = true;
		    Category category = null;
		    
		    while ((nextLine = csvReader.readNext()) != null) {
		    	if (readHeaders) {
			    	readHeaders = false;
			    	
		    		boolean allOk = true;
			    	for (int i = 0; i < COLUMN_NAMES.length; i++) {
				    	boolean columnFound = false;
				    	for (int j = 0; j < nextLine.length; j++) {
				    		if (nextLine[j].equals(COLUMN_NAMES[i])) {
				    			columnFound = true;
				    			break;
				    		}
				    	}
				    	
				    	allOk &= columnFound;
			    	}
			    	
			    	if (allOk) {
				    	headers = nextLine;				    	
				    	continue;
			    	} else {
			    		headers = COLUMN_NAMES;
			    	}
		    	}
		    	
	    		Map<String, String> properties = new HashMap<String, String>();
	    		for (int i = 0; i < nextLine.length; i++) {
	    			String property = headers[i].trim().toUpperCase();
	    			String value = nextLine[i];
	    			LOG.debug("line: property={} value={}", property, value);
	    			
	    			properties.put(property, value);
	    		}
	    		
	    		if (isCategory(properties)) {
	    			String name = properties.get(TITLE);
	    			category = Finders.findCategory(model.getRootCategory(), name).getFoundCategory();
	    			if (category == null) {
	    				LOG.debug("adding category {}", category);
	    				category = new Category(name);
	    				model.getRootCategory().add(category);
	    			}
	    		} else if (isBlank(properties, REQUIRED_COLUMN_NAMES)) {
	    			LOG.debug("skipping line");
	    		} else {
	    			LOG.debug("adding line");
	    			model.addPassword(category, createPassword(properties));
	    		}
		    }
		} finally {
			if (csvReader != null) {
				csvReader.close();
			}
			reader.close();
		}
		
		return model;
	}

	private boolean isBlank(Map<String, String> properties,
			String[] requiredColumns) {
		boolean blank = false;
		for (String requiredProperty : requiredColumns) {
			blank |= Utils.isBlank(properties.get(requiredProperty)); 
		}
		return blank;
	}

	private boolean isCategory(Map<String, String> properties) {
		boolean category = true;
		for (String property : COLUMN_NAMES) {
			if (TITLE.equals(property) || TYPE.equals(property)) {
				category = !Utils.isBlank(properties.get(property));
			} else {
				category = Utils.isBlank(properties.get(property));
			}
			
			if (!category) {
				break;
			}
		}
		return category;
	}
	
	private Password createPassword(Map<String, String> properties) {		
		//TODO support these properties : TITLE,CATEGORY,EMAIL,USERNAME,PASSWORD,URL,NOTES,OTHER,DATE_EXPIRES,TYPE
		return new Password(properties.get(TITLE), properties.get(PASSWORD));
	}
}
