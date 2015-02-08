package frets.main;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/** 
 * An interface that describes an object that may be
 * created/instantiated from properties/files.
 */
public interface SimpleProperties<T> {
	// Basic meta data
	public String getMetaName();
	public String getMetaDescription();
	public String getMetaLocation();
	
	/** Opens a file at the given name, and reads all the properties into an object. */
	public T readFromFile( String fileName ) throws IOException;

	/** Opens a path at the given name, attempts to read files from there.
	 *  Use optional filter as a java.io.FilenameFilter. */
	public List<T> readFromPath( String pathName, String filterString ) throws IOException;

	/** Returns an object fretboard that has been loaded from a
	 *  central repository or properties list. */
	public T getInstance( String shortName );

	/** Performs the loading of configured objects from the given location. */
	public Map<String,T> loadProperties(String pathName, String filterString) throws IOException;

	// Property related meta-data in an instance
	// protected String metaName;
	// protected String metaDescription;
	// protected String metaLocation; //url or file	
	// protected static Map<String,<T>> propertyMap; // static collection for all these objects.
}