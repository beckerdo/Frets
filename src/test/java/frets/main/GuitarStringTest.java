package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static frets.main.Display.Hand;
import static frets.main.Display.VAlign;
import static frets.main.Display.NotPlayedLocation;
import static frets.main.Display.Orientation;


/**
 * Unit tests to validate this class.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class GuitarStringTest 
{
	String nl = Display.NL;

    @Before
    public void setup()
    {
    }
    
    @Test
    public void testGettersSetters()
    {
    	GuitarString test = new GuitarString( Note.E );
        assertEquals("Open note", Note.E, test.getOpenNote() );        	
        assertTrue("Octave fret", 12 == test.getOctaveFret() );        	
        assertTrue("Max fret", 18 == test.getMaxFret() );        	

        int maxFret = 20;
        Note highNote = (new GuitarString( Note.E, 12, maxFret )).getHighNote();
        assertEquals("High note", Note.plus( Note.E, maxFret ), highNote );        	
    }

    @Test
    public void testEquals()
    {
    	GuitarString test1 = new GuitarString( Note.A );
    	GuitarString test2 = new GuitarString( Note.A );
    	GuitarString test3 = new GuitarString( Note.G );
    	GuitarString test4 = new GuitarString( Note.GuitarLowE );
    	
        assertEquals("Identity", test1, test1 );        	
        assertTrue("Symmetry 1", test1.equals( test2 ));        	
        assertTrue("Symmetry 2", test2.equals( test1 ));        	
        assertFalse("Symmetry 3", test1.equals( test3 ));        	
        assertFalse("Symmetry 4", test2.equals( test4 ));        	
    }

    @Test
    public void testComparable()
    {
    	GuitarString lowE = new GuitarString( Note.GuitarLowE );
    	GuitarString highE = new GuitarString( Note.GuitarHighE );
    	GuitarString G1 = new GuitarString( Note.G );
    	GuitarString G2 = new GuitarString( Note.G );
    	
        assertTrue("Less", 0 > lowE.compareTo(highE));        	
        assertTrue("Greater", 0 < highE.compareTo(lowE));        	
        assertTrue("Equal", 0 == G1.compareTo(G2));        	
        
        GuitarString lowB = new GuitarString( Note.parse( "B2" ));
        assertTrue("Less B", 0 > lowB.compareTo(highE));        	
    }

    @Test
    public void testFrets()
    {
    	GuitarString typicalA = new GuitarString( Note.GuitarA, 12, 18 );

    	Note lessThan = Note.GuitarLowE;
        assertTrue("Less", GuitarString.NOFRET == typicalA.getFret( lessThan ));
        
        Note open = Note.GuitarA;
        assertTrue("Open",  0 == typicalA.getFret( open ));
        
        Note onString = Note.plus( Note.GuitarA, Interval.half );
        assertTrue("On string 1",  1 == typicalA.getFret( onString ));

        onString = Note.plus( Note.GuitarA, Interval.whole );
        assertTrue("On string 2",  2 == typicalA.getFret( onString ));

        onString = Note.plus( Note.GuitarA, Interval.ninth );
        assertTrue("On string 3",  14 == typicalA.getFret( onString ));

        onString = Note.plus( Note.GuitarA, 18 );
        assertTrue("Equal max",  GuitarString.NOFRET == typicalA.getFret( onString ));
        
    	Note greaterThan = Note.GuitarHighE;
        assertTrue("Greater", GuitarString.NOFRET == typicalA.getFret( greaterThan ));
    
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoteLow()
    {
    	GuitarString typicalG = new GuitarString( Note.GuitarG, 12, 18 );
    	typicalG.getNote( -5 );
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoteHigh()
    {
    	GuitarString typicalG = new GuitarString( Note.GuitarG, 12, 18 );
    	typicalG.getNote( 20 );
    }
    
    @Test
    public void testNotes()
    {
    	GuitarString typicalG = new GuitarString( Note.GuitarG, 12, 18 );
    	
    	assertEquals( "A", Note.plus( Note.GuitarG, Interval.whole ), typicalG.getNote( 2 ) );

    	assertEquals( "A#", Note.plus( Note.GuitarG, Interval.minorThird ), typicalG.getNote( 3 ) );

    	assertEquals( "B", Note.plus( Note.GuitarG, Interval.third ), typicalG.getNote( 4 ) );

    	// System.out.println( "G plus fret 7=" + typicalG.getNote( 7 ) );
    	assertEquals( "D", Note.plus( Note.GuitarG, Interval.fifth ), typicalG.getNote( 7 ) );
    }

    @Test
    public void testLocations()
    {
    	List<Integer> locations = Arrays.asList( 0, 3, 5, 7 );
    	List<Integer> smallerLocations = Arrays.asList( 0, 3 );
    	NoteList notes = new NoteList( 
    			Note.GuitarG,
    			Note.plus( Note.GuitarG, Interval.minorThird ),
    			Note.plus( Note.GuitarG, Interval.fourth ),
    			Note.plus( Note.GuitarG, Interval.fifth )
        	);
    	NoteList smallerNotes = new NoteList( 
    			Note.GuitarG,
    			Note.plus( Note.GuitarG, Interval.minorThird )
        	);
    	
    	GuitarString typicalG = new GuitarString( Note.GuitarG, 0, 18 );
    	
    	assertTrue( "Locations this range",  2 == typicalG.getCountThisRange( locations, 0, 5 ));    	
    	assertEquals( "Locations to notes", typicalG.getNoteList( locations ), notes );
    	assertEquals( "Locations to notes ranged", typicalG.getNoteList( locations, 0, 5 ), smallerNotes );
    	assertEquals( "Notes to locations", typicalG.getLocations( notes ), locations );
    	assertEquals( "Notes to locations reanged", typicalG.getLocations( notes, 0, 5 ), smallerLocations );
    }

    @Test
    public void testStringLocationHori()
    {
    	GuitarString test = new GuitarString( Note.GuitarD );
    	List<Integer> locations = Arrays.asList( 0, 2 );
    	
    	// Example: open plus whole on 0 to 5 righty is "o|| |o| | |"
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.PLAIN;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.fretSpace = 1;
    	displayOpts.headSpace = 1;
    	// System.out.println( test.toString(notes, 0, 5, displayOpts ));    	
        assertEquals("String 1", "o|| |o| | |", test.toString(locations, 0, 5, displayOpts ));        	
    	displayOpts.fretSpace = 2;
        assertEquals("String 2", "o||  | o|  |  |",	test.toString(locations, 0, 5, displayOpts ));        	
    	
        // Example: open plus whole on 0 to 5 lefty is "| | |o| ||o"
    	displayOpts.hand = Hand.LEFT;
    	displayOpts.fretSpace = 3;
        assertEquals("String 7", "|   |   |o  |   ||o",	test.toString(locations, 0, 5, displayOpts ));        	
    	displayOpts.fretSpace = 4;
        assertEquals("String 8", "|    |    |o   |    ||o",	test.toString(locations, 0, 5,  displayOpts ));        	
    }

    @Test
    public void testStringHori()
    {
    	GuitarString test = new GuitarString( Note.GuitarD );
    	NoteList notes = new NoteList( 
			Note.GuitarD,
			Note.plus( Note.GuitarD, Interval.whole )
    	);
    	
    	// Example: open plus whole on 0 to 5 righty is "o|| |o| | |"
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.PLAIN;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.fretSpace = 1;
    	displayOpts.headSpace = 1;
    	// System.out.println( test.toString(notes, 0, 5, displayOpts ));    	
        assertEquals("String 1", "o|| |o| | |", test.toString(notes, 0, 5, displayOpts ));        	
    	displayOpts.fretSpace = 2;
        assertEquals("String 2", "o||  | o|  |  |",	test.toString(notes, 0, 5, displayOpts ));        	
    	displayOpts.fretSpace = 3;
        assertEquals("String 3", "o||   |  o|   |   |",	test.toString(notes, 0, 5, displayOpts ));        	
    	displayOpts.fretSpace = 4;
        assertEquals("String 4", "o||    |   o|    |    |",	test.toString(notes, 0, 5, displayOpts ));        	

        // Example: open plus whole on 0 to 5 lefty is "| | |o| ||o"
    	displayOpts.hand = Hand.LEFT;
    	displayOpts.fretSpace = 1;
        assertEquals("String 5", "| | |o| ||o", test.toString(notes, 0, 5, displayOpts ));        	
    	displayOpts.fretSpace = 2;
        assertEquals("String 6", "|  |  |o |  ||o",	test.toString(notes, 0, 5, displayOpts ));        	
    	displayOpts.fretSpace = 3;
        assertEquals("String 7", "|   |   |o  |   ||o",	test.toString(notes, 0, 5, displayOpts ));        	
    	displayOpts.fretSpace = 4;
        assertEquals("String 8", "|    |    |o   |    ||o",	test.toString(notes, 0, 5,  displayOpts ));        	
    }

    @Test
    public void testStringHoriNoteName()
    {
    	GuitarString test = new GuitarString( Note.GuitarB );
    	NoteList notes = new NoteList( 
			Note.GuitarB,
			Note.plus( Note.GuitarB, Interval.whole )
    	);
    	
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.headSpace = 1;
    	displayOpts.fretSpace = 1;
    	
    	// Example: open plus whole on 0 to 5 righty is " B|| |C#| | |"
    	// System.out.println( "B C# righty=" + test.toString(notes, 0, 5, displayOpts ));    	
        assertEquals("String hori note right", "B ||  |C#|  |  |", test.toString(notes, 0, 5, displayOpts ));        	

        // Example: open plus whole on 0 to 5 lefty is "| | |C#| ||B "
    	displayOpts.hand = Hand.LEFT;
    	// System.out.println( "B C# lefty=" + test.toString(notes, 0, 5, displayOpts ));    	
        assertEquals("String hori note left", "|  |  |C#|  ||B ", test.toString(notes, 0, 5, displayOpts ));        	
    }

    @Test
    public void testStringHoriCompact()
    {
    	GuitarString test = new GuitarString( Note.GuitarB );
    	NoteList notes = new NoteList( 
    		Note.GuitarB,
			Note.plus( Note.GuitarB, Interval.whole )
    	);
    	
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.nutString = "|";
    	displayOpts.fretString = "";
    	// Example: open plus whole on 0 to 5 righty is " B|| |C#| | |"
    	// System.out.println( "B C# compact=" + test.toString(notes, 0, 5, displayOpts ));    	
        assertEquals("String hori compact", "B |  C#    ", test.toString(notes, 0, 5, displayOpts ));        	
    }

    @Test
    public void testStringHoriString()
    {
    	GuitarString test = new GuitarString( Note.GuitarB );
    	NoteList notes = new NoteList( 
    		Note.GuitarB,
    		Note.plus( Note.GuitarB, Interval.whole ),
    		Note.plus( Note.GuitarB, Interval.fourth )
    	);
    	
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.nutString = "|";
    	displayOpts.fretString = "";
    	displayOpts.stringString = "-";
    	// System.out.println( "B C# E compact string=" + test.toString(notes, 0, 6, displayOpts ));    	
        assertEquals("String hori compact string", "B-|--C#----E-", test.toString(notes, 0, 6, displayOpts ));        	
    }

    @Test
    public void testStringHoriNotPlayed()
    {
    	GuitarString test = new GuitarString( Note.GuitarB );
    	NoteList notes = new NoteList(); 
    	
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.PLAIN;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.nutString = "|";
    	displayOpts.fretString = "";
    	displayOpts.stringString = "-";
    	displayOpts.notPlayed = EnumSet.of( NotPlayedLocation.HEAD, NotPlayedLocation.FIRST );
    	displayOpts.notPlayedString= "x";
    	displayOpts.fretSpace = 1;
    	// System.out.println( "String hori not played=" + test.toString(notes, 0, 5, displayOpts ));    	
        assertEquals("String hori compact not played open", "x|----", test.toString(notes, 0, 5, displayOpts ));        	
    	// System.out.println( "String hori not played=" + test.toString(notes, 1, 5, displayOpts ));    	
        assertEquals("String hori compact not played first", "x---", test.toString(notes, 1, 5, displayOpts ));        	
    	displayOpts.infoType = Display.InfoType.NAME;
    	// System.out.println( "String hori not played=" + test.toString(notes, 0, 5, displayOpts ));    	
        assertEquals("String hori compact not played open", "x-|--------", test.toString(notes, 0, 5, displayOpts ));        	
    	// System.out.println( "String hori not played=" + test.toString(notes, 1, 5, displayOpts ));    	
        assertEquals("String hori compact not played first", "x-------", test.toString(notes, 1, 5, displayOpts ));        	
    }

    @Test
    public void testStringVert()
    {
    	GuitarString test = new GuitarString( Note.GuitarLowE );
    	NoteList notes = new NoteList( 
			Note.GuitarLowE,
			Note.plus( Note.GuitarLowE, Interval.third ),
			Note.plus( Note.GuitarLowE, Interval.fourth )
    	);
    	
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.PLAIN;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.VERTICAL;
    	displayOpts.fretString = "-";
    	displayOpts.nutString = "=";
    	displayOpts.spaceString = " ";
    	
    	String space = displayOpts.spaceString + nl + displayOpts.fretString + nl;
    	String note = displayOpts.plainNoteString + nl + displayOpts.fretString + nl;
    	// System.out.println(  "E G# A plain:" + nl + test.toString(notes, 0, 6, displayOpts ));
        assertEquals("String vert plain no space", 
        	displayOpts.plainNoteString + nl + "=" + nl + space + space + space + note + note,    
        	test.toString(notes, 0, 6, displayOpts ));        	

    	displayOpts.fretString = null;
    	displayOpts.spaceString = " ";
    	space = displayOpts.spaceString + nl;
    	note = displayOpts.plainNoteString + nl;
    	// System.out.println(  "E G# A plain:" + nl + test.toString(notes, 0, 6, displayOpts ));
        assertEquals("String vert plain space", 
        	displayOpts.plainNoteString + nl + "=" + nl + space + space + space + note + note,    
        	test.toString(notes, 0, 6, displayOpts ));        	

        displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.fretString = "--";
    	displayOpts.nutString = "==";
    	displayOpts.headAlign = VAlign.TOP;
    	displayOpts.fretAlign = VAlign.TOP;
    	displayOpts.spaceString = " ";

    	space = displayOpts.spaceString + displayOpts.spaceString + nl + displayOpts.fretString + nl;
    	String g = "G#" + nl + displayOpts.fretString + nl;
    	String a = "A " + nl + displayOpts.fretString + nl;
    	// System.out.println( "E G# A notes:" + nl + test.toString(notes, 0, 6, displayOpts ));
        assertEquals("String vert named space", 
            	"E " + nl + displayOpts.nutString + nl + space + space + space + g + a,    
            	test.toString(notes, 0, 6, displayOpts ));        	
    }

    @Test
    public void testStringVertCompact()
    {
    	GuitarString test = new GuitarString( Note.GuitarLowE );
    	NoteList notes = new NoteList( 
			Note.GuitarLowE,
			Note.plus( Note.GuitarLowE, Interval.third ),
			Note.plus( Note.GuitarLowE, Interval.fourth )
    	);
    	
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.PLAIN;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.VERTICAL;
    	displayOpts.fretString = "";
    	displayOpts.nutString = "=";

    	// System.out.println( "E G# A compact h:" + nl + test.toString(notes, 0, 6, displayOpts ));
    	// System.out.println( "E G# A compact:" + nl + test.toString(notes, 0, 6, displayOpts ));
    	String space = displayOpts.spaceString + nl;
    	String note = displayOpts.plainNoteString + nl;
        assertEquals("String vert plain compact", 
        	displayOpts.plainNoteString + nl + "=" + nl + space + space + space + note + note,    
        	test.toString(notes, 0, 6, displayOpts ));        	

    	displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.headAlign = VAlign.TOP;
    	displayOpts.fretAlign = VAlign.TOP;
    	displayOpts.nutString = "==";
        // System.out.println( "E G# A compact name h:" + nl + test.toString(notes, 0, 6, displayOpts ));
        // System.out.println( "E G# A compact:" + nl + test.toString(notes, 0, 6, displayOpts ));
    	space = "  " + nl;
    	String g = "G#" + nl;
    	String a = "A " + nl;
        assertEquals("String vert named compact", 
        	"E " + nl + "==" + nl + space + space + space + g + a,    
        	test.toString(notes, 0, 6, displayOpts ));        	
    }

    @Test
    public void testStringVertString()
    {
    	GuitarString test = new GuitarString( Note.GuitarLowE );
    	NoteList notes = new NoteList( 
			Note.GuitarLowE,
			Note.plus( Note.GuitarLowE, Interval.third ),
			Note.plus( Note.GuitarLowE, Interval.fourth )
    	);
    	
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.PLAIN;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.VERTICAL;
    	displayOpts.headAlign = VAlign.TOP;
    	displayOpts.fretAlign = VAlign.TOP;
    	displayOpts.fretString = "";
    	displayOpts.nutString = "=";
    	displayOpts.stringString = "|";

    	// System.out.println( "E G A compact h:" + nl + test.toString(notes, 0, 6, displayOpts ));
    	String space = "|" + nl;
    	String note = displayOpts.plainNoteString + nl;
        assertEquals("String vert plain string", 
        	displayOpts.plainNoteString + nl + "=" + nl + space + space + space + note + note,    
        	test.toString(notes, 0, 6, displayOpts ));        	

        // Mistake. Appends string after A note name, rather than space.
    	displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.nutString = "==";
    	displayOpts.stringString = "| ";
        // System.out.println( "E G# A vert named strings:" + nl + test.toString(notes, 0, 6, displayOpts ));
    	space = "| " + nl;
    	String g = "G#" + nl;
    	String a = "A " + nl;
        assertEquals("String vert named string", 
        	"E " + nl + "==" + nl + space + space + space + g + a,    
        	test.toString(notes, 0, 6, displayOpts ));
        
    	test = new GuitarString( Note.GuitarB );
    	notes = new NoteList( 
    		Note.GuitarB,
    		Note.plus( Note.GuitarB, Interval.whole ),
    		Note.plus( Note.GuitarB, Interval.fourth )
    	);
        // System.out.println( "B C# E vert named strings:" + nl + test.toString(notes, 0, 6, displayOpts ));
        assertEquals("B C# E String vert named string", 
            	"B " + nl + "==" + nl + space + "C#" + nl + space + space + "E " + nl,    
            	test.toString(notes, 0, 6, displayOpts ));
            

    }

    @Test
    public void testStringVertNotPlayed()
    {
    	GuitarString test = new GuitarString( Note.GuitarLowE );
    	NoteList notes = new NoteList( 
    	);
    	
    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.PLAIN;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.VERTICAL;
    	displayOpts.fretString = "";
    	displayOpts.nutString = "=";
    	displayOpts.stringString = "|";
    	displayOpts.notPlayed = EnumSet.of( NotPlayedLocation.HEAD, NotPlayedLocation.FIRST );
    	displayOpts.notPlayedString =  "x";

    	String space = "|" + nl;
    	String notPlayed = "x" + nl;
    	
        // System.out.println( "String vert plain string not played open:" + test.toString(notes, 0, 4, displayOpts ));
        assertEquals( "String vert plain string not played", 
        	notPlayed + "=" + nl + space + space + space,    
        	test.toString(notes, 0, 4, displayOpts ));        	

        // System.out.println( "String vert plain string not played third:" + nl + test.toString(notes, 3, 7, displayOpts ));
        assertEquals( "String vert plain string not played", 
        	notPlayed + space + space + space,    
        	test.toString(notes, 3, 7, displayOpts ));        	

    	displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.headAlign = VAlign.TOP;
    	displayOpts.fretAlign = VAlign.TOP;
    	displayOpts.nutString = "==";
    	displayOpts.stringString = "| ";
    	space = "| " + nl;
    	notPlayed = "x " + nl;
    	
        // System.out.println( "String vert named string not played open:" + test.toString(notes, 0, 4, displayOpts ));
        assertEquals( "String vert plain string not played", 
        	notPlayed + "==" + nl + space + space + space,    
        	test.toString(notes, 0, 4, displayOpts ));        	

    	space = "| " + nl + "| " + nl;
    	notPlayed = "x " + nl + "| " + nl;
    	displayOpts.notPlayedString = "x ";
    	displayOpts.headSpace = 2;
    	displayOpts.fretSpace = 2;
    	
        // System.out.println( "String vert named string not played range:" + nl + test.toString(notes, 3, 8, displayOpts ));
        assertEquals( "String vert plain string not played range", 
        	notPlayed + space + space + space + space,    
        	test.toString(notes, 3, 8, displayOpts ));        	
   }
}