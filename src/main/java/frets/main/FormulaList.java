package frets.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import frets.util.FilenameRegExFilter;

/**
 * Information about chords and their data.
 * Formula data can be found by name or formula.
 *
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class FormulaList {
	protected static Map<String,Formula> propertiesMap; // static collection for all these objects.
	
	/** Returns an object loaded from  repository or properties list. */
	public static Formula get( String shortName ) {
		if ( null == propertiesMap ) {
			// Lazy instantiation
			try {
				propertiesMap = new HashMap<String,Formula>();
				populateFromFile(propertiesMap, "src/main/resources/frets/main/chord.properties" );
				populateFromFile(propertiesMap, "src/main/resources/frets/main/scale.properties" );
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return (Formula)propertiesMap.get( shortName );
	}


	/** Load a collection of this class. 
	 * If #.id has a value, then the properties #.name, etc are read in. 
	 * @param fileName
	 * @throws IOException
	 */
	public static void populateFromFile( Map<String,Formula> propertiesMap, String fileName ) throws IOException {
		// Read properties file. 
		Properties properties = new Properties();
		properties.load( new FileInputStream( fileName ) );

		// Convert from properties to Display bean
		int id = 0;
		while ( null != (String) properties.get( Integer.toString( id ) + ".id" ) ) {
			// Value for key "#.id" exists. Populate a bean from this id.
			Formula formula = new Formula();
			formula.setName( (String) properties.get( Integer.toString( id ) + ".name"  ));
			formula.setNameVerbose( (String) properties.get( Integer.toString( id ) + ".nameVerbose"  ));
			String equation = (String) properties.get( Integer.toString( id ) + ".formula"  );
			if (( null != equation ) && ( 0 < equation.length() )) {
				equation = equation.replace(";", "-");
				formula.setFormula( equation );
			}
			
			propertiesMap.put( formula.getName(), formula );
			propertiesMap.put( formula.getFormula(), formula );
			id++;
		}
	}
	
	/** Opens a path at the given name, attempts to read files from there.
	 *  Use optional filter as a java.io.FilenameFilter. */
	public static void populateFromFilter( Map<String,Formula> propertiesMap, String pathName, String filterString ) throws IOException {
		File dir = new File( pathName );
		if (!dir.isDirectory() )
			throw new IOException( "Path must be directory, path=" + pathName );
		
		File [] files = null;
		if (null != filterString && (filterString.length() > 0)) {
			FilenameFilter filter = new FilenameRegExFilter( filterString );
			files = dir.listFiles( filter );
		} else {
			files = dir.listFiles();   			
		}
		
		for ( File file : files ) {
           populateFromFile( propertiesMap, file.getPath() );
		}
	}
}