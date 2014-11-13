package frets.main;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * Unit tests to validate this class.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class LocationTest 
{
	protected Fretboard standard;
	String nl = Display.NL;

    @Before
    public void setup() {
		standard = Fretboard.instance.getInstance( Fretboard.STANDARD );
    }
    
    @Test
    public void testCompare() {
    	Location a = new Location ( 6, 5 );
    	Location b = new Location ( 5, 3 );
    	
        assertTrue("Location compare 1", a.compareTo( b ) > 0 );        	
        assertTrue("Location compare 2", b.compareTo( a ) < 0 );        	
        assertTrue("Location compare 3", a.compareTo( a ) ==  0  );        	
    }
    
    @Test
    public void testLocations() {
       	LocationList noLocations = standard.getLocations( null );
        assertTrue("No locations", null == noLocations );        	
       	
       	LocationList emptyLocations = standard.getLocations( Note.plus( Note.GuitarHighE, 1001 ));
        assertTrue("Empty locations", 0 == emptyLocations.size() );        	
       	
       	LocationList locations = standard.getLocations( Note.GuitarG );
       	// System.out.println( "Locations of G=" + locations );
        assertTrue("G locations", 4 == locations.size() );        	
       	
        assertEquals("Location 1", new Location( 0, 15 ), locations.get( 0 ) );        	
        assertEquals("Location 2", new Location( 1, 10 ), locations.get( 1 ) );        	
        assertEquals("Location 3", new Location( 2,  5 ), locations.get( 2 ) );        	
        assertEquals("Location 4", new Location( 3,  0 ), locations.get( 3 ) );        	

       	locations = standard.getLocations( Note.GuitarG, 0, 6 );
       	// System.out.println( "Locations of G[0,6]=" + locations );
        assertTrue("G locations", 2 == locations.size() );        	
        
        // Test location reverting to string.
        Location test = new Location( 0, 5 );
        // System.out.println( "Guitar A=" + Note.GuitarA + ", location=" + test + ", note=" + test.getNote( standard ));
        assertEquals( "Guitar A", Note.GuitarA, test.getNote( standard ));
        test = new Location( 5, 0 );
        // System.out.println( "Guitar E=" + Note.GuitarHighE + ", location=" + test + ", note=" + test.getNote( standard ));
        assertEquals( "Guitar High E", Note.GuitarHighE, test.getNote( standard ));
        test = new Location( -1, 0 );
        // System.out.println( "Location=" + test + ", note=" + test.getNote( standard ));
        assertTrue( "No String", null == test.getNote( standard ));

        test = new Location( 2, Integer.MAX_VALUE );
        // System.out.println( "Location=" + test + ", note=" + test.getNote( standard ));
        assertTrue( "No fret", null == test.getNote( standard ));
        
        LocationList variousLocations = new LocationList(
        		new Location( 1, 10 ),
        		new Location( 2, 5 ),
        		new Location( 3, 0 ));
        assertEquals( "Location filter 1", Fretboard.getLocations( 1 , variousLocations ), Arrays.asList( 10 ));
        assertEquals( "Location filter 2", Fretboard.getLocations( 3 , variousLocations ), Arrays.asList( 0 ));
    }

    @Test
    public void testStringConstructor() {
        // Catch some illegals
        try { Location.parseString( null );
        	assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        try { Location.parseString( "" );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        try { Location.parseString( " " );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        try { Location.parseString( " 1" );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertTrue( "Exception",   Exception.class.isAssignableFrom( e.getClass() ) );
        }
        try { Location.parseString( "1    " );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertTrue( "Exception",   Exception.class.isAssignableFrom( e.getClass() ) );
        }
        try { Location.parseString( "1+2+3" );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertTrue( "Exception",   Exception.class.isAssignableFrom( e.getClass() ) );
        }
        
        // Catch some legals
        assertEquals("Constructor 1", "1-2", Location.parseString( "1-2" ).toString());
        assertEquals("Constructor 2", "0-0", Location.parseString( "0-0" ).toString());
        // assertEquals("Constructor 3", "-1+-3", Location.parseString( "-1+-3" ).toString());
        // assertEquals("Constructor 4", "-1+-2", Location.parseString( "    -1    +     -2        " ).toString());
        assertEquals("Constructor 5", (new Location("1-2")).toString(), Location.parseString( "1-2" ).toString());
    }
 
    @Test
    public void testJson()  {
    	Location expected = Location.parseString( "0-5" );
    	String json = expected.toJSON();   	
    	// System.out.println( "Location=" + expected.toString() + ", json=" + json );
    	Location returned = Location.fromJSON( json );
    	assertEquals( "Location json", expected, returned );
    }

    @Test
    public void testStringFret() {
        Location lowA = new Location( 0, 5 );
        Location highA = new Location( 5, 5 );

        // Test location reverting to String-Fret string.
        assertEquals( "Guitar Low A", "E2-5", lowA.toStringFret( standard ));
        assertEquals( "Guitar High A", "E4-5", highA.toStringFret( standard ));

        // test parsing of string name, fret to note    
        assertEquals( "Guitar Low A parse", lowA, Location.parseStringFret("E2-5", standard));
        assertEquals( "Guitar High A parse", highA, Location.parseStringFret("E4-5", standard));

        // Try non-existent string
        try { 
        	Location.parseStringFret( "E3-5", standard );
     		assertFalse( "Exception not thrown", true );
         } catch ( Throwable e ) {
         	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
         }
    }
}