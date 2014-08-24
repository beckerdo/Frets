package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;

import frets.util.FilenameRegExFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Unit tests to validate this class.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class SimplePropertiesTest 
{
    @Test
    public void testNullMock() 
    {
    	SimpleProperties<MockProperties> mock = MockProperties.instance.getInstance( "");
    	assertNull( "Null instance", mock );
    }
    
    @Test
    public void testMock1() 
    {
    	SimpleProperties<MockProperties> mock = MockProperties.instance.getInstance( "Mock properties name 1" );
    	assertEquals( "Name", "Mock properties name 1",  mock.getMetaName() );
    	assertEquals( "Description", "Mock properties description 1",  mock.getMetaDescription() );
    	assertEquals( "Location", "src\\test\\resources\\frets\\main\\mock1.properties",  mock.getMetaLocation() );
    }
    
    @Test
    public void testMock2() 
    {
    	SimpleProperties<MockProperties> mock = MockProperties.instance.getInstance( "Mock properties name 2" );
    	assertEquals( "Name", "Mock properties name 2",  mock.getMetaName() );
    	assertEquals( "Description", "Mock properties description 2",  mock.getMetaDescription() );
    	assertEquals( "Location", "src\\test\\resources\\frets\\main\\mock2.properties",  mock.getMetaLocation() );
    }
    

    /** A simple class that implements the SimpleProperties interface. */
    public static class MockProperties implements SimpleProperties<MockProperties> {
    	protected String metaName;
    	protected String metaDescription;
    	protected String metaLocation;
    	protected static Map<String,MockProperties> propertiesMap; // static collection for all these objects.
    	protected static MockProperties instance = new MockProperties();
    	
    	public String getMetaName() {
    		return metaName;
    	}
    	public String getMetaDescription() {
    		return metaDescription;
    	}
    	public String getMetaLocation() {
    		return metaLocation;
    	}
    	
    	/** Opens a file at the given name, and reads all the properties into an object. */
    	public MockProperties readFromFile( String fileName ) throws IOException {
    		MockProperties mock = new MockProperties();
    		
    		// Read properties file. 
    		Properties properties = new Properties();
    		// File test = new File( fileName );
    		// System.out.println( "File \"" + fileName + "\" exists=" + test.exists() + ", abspath=" + test.getAbsolutePath());		
    		properties.load(new FileInputStream( fileName ));

    		mock.metaLocation = fileName;
    		
    		// Convert from properties to object
    		for ( Iterator<Object> it = properties.keySet().iterator(); it.hasNext(); ) {
    			String key = (String) it.next();
    			String value = properties.getProperty( key );
    			if ( "name".equals( key ) ) {
    				mock.metaName = value;
    			} else if ( "description".equals( key ) ) {
    				mock.metaDescription = value;
    			} else {
    				throw new IllegalArgumentException( "Class=\"" + MockProperties.class.getName() + "\" key \"" + key + "\" not handled, value=\"" + value + "\"." );
    			} 
    		}
    		return mock;
    	}

    	/** Opens a path at the given name, attempts to read files from there.
    	 *  Use optional filter as a java.io.FilenameFilter. */
    	public List<MockProperties> readFromPath( String pathName, String filterString ) throws IOException {
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
    		
    		List<MockProperties> list = new LinkedList<MockProperties>();    		
    		for ( File file : files ) {
               list.add(  readFromFile(file.getPath()) );			
    		}
    		return list;
    	}

    	/** Returns an object fretboard that has been loaded from a
    	 *  central repository or properties list. */
    	public MockProperties getInstance( String shortName ) {
    		if ( null == propertiesMap ) {
    			// Lazy instantiation
    			try {
    				propertiesMap = loadProperties("src/test/resources/frets/main/", "mock.*.properties");
    			} catch (IOException e) {
    				System.out.println(e);
    			}
    		}
    		return propertiesMap.get( shortName );
    	}

    	/** Performs the loading of configured objects from the given location. */
    	public Map<String,MockProperties> loadProperties(String pathName, String filterString) throws IOException {
    		   Map<String,MockProperties> propertiesMap = new HashMap<String,MockProperties>();
    		   List<MockProperties> propertiesList = readFromPath(pathName,filterString);
    		   for (MockProperties mockProperties : propertiesList) {
    			   propertiesMap.put(mockProperties.getMetaName(),mockProperties);
    		   }
    		   return propertiesMap;	       		
    	}
    } // MockProperties
}