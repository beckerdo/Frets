package frets.main;

import static frets.main.Display.NL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

import frets.main.Display.Hand;
import frets.main.Display.Orientation;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests to validate this class.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class LocationListTest 
{
    @Before
    public void setup()
    {
    }
    
    @Test
    public void testEquals()
    {
    	LocationList empty1 = new LocationList();
    	LocationList empty2 = new LocationList();
    	Location a = new Location ( 6, 5 );
    	Location b = new Location ( 5, 3 );
    	Location c = new Location ( 4, 1 );
    	LocationList la = new LocationList( a, b, c  );
    	List<Location> list = new LinkedList<Location>();
    	list.add( a );
    	list.add( b );
    	list.add( c );
    	LocationList lb = new LocationList( list );
    	LocationList lc = new LocationList( la );
    	
        assertEquals("Identity 1", empty1, empty1 );        	
        assertTrue("Identity 2", la.equals( la ));        	
        assertTrue("Symmetry 1", empty1.equals( empty2 ));        	
        assertTrue("Symmetry 2", empty2.equals( empty1 ));        	
        assertTrue("Symmetry 3", la.equals( lb ));        	
        assertTrue("Symmetry 4", lb.equals( la ) );        	
        assertTrue("Transitivity", la.equals(lb) && lb.equals( lc ) && la.equals( lc ) );        	
    }

    @Test
    public void testComparable()
    {
    	LocationList empty = new LocationList();
    	Location a = new Location ( 6, 5 );
    	Location b = new Location ( 5, 3 );
    	Location c = new Location ( 4, 1 );
    	Location d = new Location ( 3, 5 );
    	Location e = new Location ( 2, 3 );
    	LocationList la = new LocationList( a, b, c );
    	LocationList lb = new LocationList( a, b, c, d );
    	LocationList lc = new LocationList( a, b, c, d, e );
    	
        assertTrue("Empty 1", empty.compareTo( la ) < 0 );        	
        assertTrue("Empty 2", la.compareTo( empty ) > 0 );        	
        assertTrue("Identity", la.compareTo( la ) == 0 );        	
        assertTrue("Same length 1", lb.compareTo( lc ) < 0 );        	
        assertTrue("Same length 2", lc.compareTo( lb ) > 0 );        	
        assertTrue("Different length 1", la.compareTo( lb ) < 0 );        	
        assertTrue("Different length 2", lb.compareTo( la ) > 0 );        	
    }

    @Test
    public void testListMethods()
    {
    	// Test some of the crud methods.
    	LocationList la = new LocationList();
    	assertTrue("Length 1", 0 == la.size());
    	
    	Location a = new Location ( 6, 5 );
    	Location b = new Location ( 5, 3 );
    	Location c = new Location ( 4, 1 );
    	Location d = new Location ( 3, 5 );

    	la.addAll( Arrays.asList( new Location[]{ a, b, c } ) );
    	assertTrue("Length 2", 3 == la.size());
    	LocationList lb = new LocationList( la );
    	
    	la.add( d );
    	assertTrue("Length 3", 4 == la.size());
    	
    	la.remove( 2 );
    	assertTrue("Length 4", 3 == la.size());    	
    	lb.remove( c );
    	assertTrue("Length 5", 2 == lb.size());
    	
    	assertTrue( "Contains", la.contains( d ));

    	lb.clear();
    	assertTrue("Length 6", 0 == lb.size());
    	assertTrue("IsEmpty", lb.isEmpty());    	
    }

    @Test
    public void testLocationSorting()
    {
    	Location a = new Location ( 6, 5 );
    	Location b = new Location ( 5, 3 );
    	Location c = new Location ( 4, 1 );
    	Location d = new Location ( 3, 5 );
    	Location e = new Location ( 2, 3 );
    	LocationList bMajor = new LocationList( c, e, d, a, b );

    	Collections.sort( bMajor );

    	// System.out.println( "Sorted B major=" + bMajor );
    	assertEquals("First", e, bMajor.get( 0 ));
    	assertEquals("Last", a, bMajor.get( bMajor.size() - 1 ));
    }

    @Test
    public void testListSorting()
    {
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD ); 
    	NoteList cRootsPos0 = new NoteList( 
    			Note.plus( Note.GuitarB, Interval.half ),
    			Note.plus( Note.GuitarA, Interval.wholehalf )
        	);
       	List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations( cRootsPos0 );
       	int varCount = Fretboard.getPermutationCount( cRootsPos0Vars );
    	// System.out.println( "Sorted C root location list variations=" + varCount );
    	assertTrue("Sorted list count", varCount == 10 );
       	
    	LocationList locations = Fretboard.getPermutation( cRootsPos0Vars, 3 );
    	LocationList expected = new LocationList( new Location( 0, 8 ), new Location( 3, 5 ) );
    	// System.out.println( "Sorted C root location list=" + cRootsPos0Vars );
    	assertEquals("Sorted variation list", expected, locations );
    }

    @Test
    public void testLocationSets()
    {
    	LocationList bigSpan = new LocationList( new Location( 2, 1 ), new Location( 1, 6 ) );
    	LocationList arpeggio = new LocationList( new Location( 2, 1 ), new Location( 2, 6 ) );

        assertTrue("Unique strings true", bigSpan.uniqueStrings());
        assertFalse("Unique strings false", arpeggio.uniqueStrings());
        assertTrue("Fret span", bigSpan.fretSpan() == 5 );

        assertEquals( "String set",  bigSpan.getStringSet(), Arrays.asList( 1, 2 ));
        assertEquals( "Fret set",  arpeggio.getFretSet(), Arrays.asList( 1, 6 ));
    }

    @Test
    public void testBounds()
    {
    	LocationList bigSpan = 
    		new LocationList( new Location( 2, 1 ), new Location( 1, 6 ), new Location( 6, 10 ) );

        assertEquals("In Bounds", bigSpan.getInBounds( 1, 6 ), new LocationList(new Location( 2, 1 ), new Location( 1, 6 ) ));
        assertTrue("In Bounds count", 2 == bigSpan.getInBoundsCount( 1, 6 ) );
        assertEquals("Out Bounds", bigSpan.getOutBounds( 1, 6 ), new LocationList(new Location( 6, 10 ) ));
        assertTrue("Out Bounds count", 1 == bigSpan.getOutBoundsCount( 1, 6 ) );
    }

    @Test
    public void testMinMaxSpanFretString()
    {
    	LocationList fminVar = new LocationList(
    			new Location( 0, 8 ),
    			new Location( 1, 8 ),
    			new Location( 2, 6 ),
    			new Location( 3, 8 ) );

    	assertTrue( "Min string", fminVar.minString() == 0 );
    	assertTrue( "Max string", fminVar.maxString() == 3 );
    	assertTrue( "Min fret", fminVar.minFret() == 6 );
    	assertTrue( "Max fret", fminVar.maxFret() == 8 );
    	assertTrue( "String span", fminVar.stringSpan() == 3 );
    	assertTrue( "Fret span", fminVar.fretSpan() == 2 );
    }
    
    @Test
    public void testGetNoteList()
    {
    	LocationList fminVar = new LocationList(
    			new Location( 0, 8 ),
    			new Location( 1, 8 ),
    			new Location( 2, 6 ),
    			new Location( 3, 8 ) );

    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD );   	
    	NoteList fmin = fminVar.getNoteList( standard );
    	
    	NoteList expected = new NoteList(
    	   Note.plus( Note.GuitarLowE, 8 ),
    	   Note.plus( Note.GuitarA, 8 ),
    	   Note.plus( Note.GuitarD, 6 ),
    	   Note.plus( Note.GuitarG, 8 ));
    	
    	assertEquals( "Get NoteList", expected, fmin );   	
    }

    @Test
    public void testTranspose()
    {
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD );   	
    	LocationList fminVar = new LocationList(
    			new Location( 0, 8 ),
    			new Location( 1, 8 ),
    			new Location( 2, 6 ),
    			new Location( 3, 8 ) );    	
    	NoteList seventh = fminVar.getNoteList( standard );
    	// System.out.println( "Original notes=" + seventh );
    	
    	fminVar.transposeFrets( -5 );
    	seventh = fminVar.getNoteList( standard );
    	// System.out.println( "Transposed fret notes=" + seventh );

    	NoteList expected = new NoteList(
    	    	   Note.plus( Note.GuitarLowE, 3 ),
    	    	   Note.plus( Note.GuitarA, 3 ),
    	    	   Note.plus( Note.GuitarD, 1 ),
    	    	   Note.plus( Note.GuitarG, 3 ));
    	    	
    	assertEquals( "Transpose frets", expected, seventh );   	

    	fminVar.transposeStrings( 2 );
    	seventh = fminVar.getNoteList( standard );
    	// System.out.println( "Transposed string notes=" + seventh );

    	expected = new NoteList(
 	    	   Note.plus( Note.GuitarD, 3 ),
 	    	   Note.plus( Note.GuitarG, 3 ),
 	    	   Note.plus( Note.GuitarB, 1 ),
 	    	   Note.plus( Note.GuitarHighE, 3 ));
 	    	
 	    assertEquals( "Transpose strings", expected, seventh );   	
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTransposeFretboardFretException()
    {
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD );   	
    	LocationList fminVar = new LocationList(
    			new Location( 0, 8 ),
    			new Location( 1, 8 ),
    			new Location( 2, 6 ),
    			new Location( 3, 8 ) );    	
    	// NoteList seventh = fminVar.getNoteList( standard );
    	// System.out.println( "Original notes=" + seventh );
    	
    	fminVar.transposeFrets( standard, -1 );
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testTransposeFretboardStringException()
    {
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD );   	
    	LocationList fminVar = new LocationList(
    			new Location( 0, 8 ),
    			new Location( 1, 8 ),
    			new Location( 2, 6 ),
    			new Location( 3, 8 ) );    	
    	// NoteList seventh = fminVar.getNoteList( standard );
    	// System.out.println( "Original notes=" + seventh );
    	
    	fminVar.transposeStrings( standard, 22 );
    }
    
    @Test
    public void testVariationSorting()
    {
    	NoteList cRootsPos0 = new NoteList( 
			Note.plus( Note.GuitarB, Interval.half ),
			Note.plus( Note.GuitarA, Interval.wholehalf )
    	);
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD ); 

    	List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations( cRootsPos0 );
    	ChordRank ranker = new ChordRank( 0, 5 );
    	// ChordRank ranker = ChordRank.instance.getInstance( ChordRank.STANDARD );
    	List<LocationList> sortedVars = Fretboard.explodeAndSort( cRootsPos0Vars, ranker );

    	// int varCount = FilenameRegExFilter.getVariationCount( cRootsPos0Vars );
    	// System.out.println( "C Roots Variation Count=" + varCount );
    	// for( int i = 0; i < varCount; i ++ ) {
    	// 	// LocationList variation = FilenameRegExFilter.getVariation( cRootsPos0Vars, i );
    	// 	LocationList variation = sortedVars.get( i );
    	// 	System.out.println( "C Roots Variation " + i + 
        // 	" (span=" + LocationList.fretSpan( variation ) + 
        // 	", unique strings=" +  LocationList.uniqueStrings( variation ) + 
        // 	", in bounds [1,5]=" +  LocationList.getInBoundsCount( variation, 0, 5 ) + 
        // 	", score=" +  ranker.compositeScore( variation ) + 
        // 	"):" + nl + 
        // 	standard.toString( variation, 0, 18, displayOpts ) + Display.NL );
    	// }
    	
       	// Test for easiest C roots.
       	LocationList calcedEasiest = sortedVars.get( 0 );
       	// System.out.println( "Easiest C=" + calcedEasiest );
       	LocationList expectedEasiest = new LocationList( new Location( 1, 3 ), new Location( 3, 5 ));
       	assertEquals ( "Easiest C", expectedEasiest, calcedEasiest );

       	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.PLAIN;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.fretSpace = 1;
    	displayOpts.notPlayed = EnumSet.of( Display.NotPlayedLocation.HEAD );
    	displayOpts.notPlayedString = "x";
    	
       	// String emptyHead = "x||";
       	// String head = " ||";
       	// String note = "o|";
       	// String space = " |";
       	// String none = emptyHead + space + space + space + space + space;    	
    	// System.out.println( "C Roots 0 pos, righty, plain, easiest variation: " );
    	// System.out.println( standard.toString( easiest, 0, 6, displayOpts ));        	
        // assertEquals("FilenameRegExFilter horizontal plain righty, easiest C root",
        //   none + nl +
        //   none + nl +
        //   head + space + space + space + space + note + nl +
        //   none + nl +
        //   head + space + space + note + space + space + nl +
        //   none ,
        //   standard.toString( easiest, 0, 6, displayOpts ));        	

    }

    @Test
    public void testSeventhVariationSorting()
    {
    	NoteList d7 = new NoteList( 
			Note.GuitarD,
			Note.plus( Note.GuitarD, Interval.majorThird ),
			Note.plus( Note.GuitarD, Interval.perfectFifth ),
			Note.plus( Note.GuitarD, Interval.majorSeventh )
    	);
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD ); 

    	List<LocationList> d7Vars = standard.getEnharmonicVariations( d7 );
    	// ChordRank ranker = new ChordRank( 0, 12, 5, 10 );
    	ChordRank ranker = ChordRank.instance.getInstance( ChordRank.STANDARD );    	
    	List<LocationList> sortedVars = Fretboard.explodeAndSort( d7Vars, ranker );
		int varCount = Fretboard.getPermutationCount( d7Vars );

    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.fretSpace = 1;
    	displayOpts.notPlayed = EnumSet.of( Display.NotPlayedLocation.HEAD );
    	displayOpts.notPlayedString = "x";
    	
		System.out.println( "Dmaj7 notes=" + d7 + ", absolute intervals=" + d7.toStringIntervals() + ", relative intervals=" + d7.toStringRelativeIntervals() );
    	System.out.println( "Dmaj7 Variation Count=" + varCount );
    	int chordableCount = 0;
    	int lowScore = Integer.MAX_VALUE; int highScore = 0;
    	for( int i = 0; i < varCount; i ++ ) {
    		// LocationList variation = FilenameRegExFilter.getVariation( cRootsPos0Vars, i );
    		LocationList variation = sortedVars.get( i );
    		int [] score = ranker.compositeScore(variation);
    		if ( score[ 0 ] < lowScore ) lowScore = score[ 0 ];
    		if ( score[ 0 ] > highScore ) highScore = score[ 0 ];
    		// if (LocationList.uniqueStrings(variation)) {
       		if (variation.uniqueStrings() && (variation.fretSpan() <=4 )) {
				System.out.println("Dmaj7 variation " + i + 
 					   "   (" + ranker.getScoreString( variation ) + "):" + NL + 
					    standard.toString(variation, 0, 18, displayOpts));
				chordableCount++;
			}
    	}
    	System.out.println( "Dmaj7 Variation Chordable Count=" + chordableCount + 
    	    	   ", low/high score=" + lowScore + "/" + highScore );
    	
//       	String emptyHead = "x||";
//       	String head = " ||";
//       	String note = "o|";
//       	String space = " |";
//       	String none = emptyHead + space + space + space + space + space;
    	
//       	LocationList easiest = sortedVars.get( 0 );
//    	System.out.println( "C Roots 0 pos, righty, plain, easiest variation: " );
//    	System.out.println( standard.toString( easiest, 0, 6, displayOpts ));        	
//        assertEquals("FilenameRegExFilter horizontal plain righty, easy C roots",
//           none + nl +
//           none + nl +
//           head + space + space + space + space + note + nl +
//           none + nl +
//           head + space + space + note + space + space + nl +
//           none ,
//           standard.toString( easiest, 0, 6, displayOpts ));        	

    }

    @Test
    public void testSeventh1573VariationSorting()
    {
    	NoteList d7 = new NoteList( 
			Note.GuitarD,
			Note.plus( Note.GuitarD, Interval.majorThird ),
			Note.plus( Note.GuitarD, Interval.perfectFifth ),
			Note.plus( Note.GuitarD, Interval.majorSeventh )
    	);
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD ); 
    	String nl = Display.NL;

    	List<LocationList> d7Vars = standard.getOctaveVariations( d7 );
    	ChordRank ranker = new ChordRank( 0, 12, 5, 10 );
    	List<LocationList> sortedVars = Fretboard.explodeAndSort( d7Vars, ranker );
		int varCount = Fretboard.getPermutationCount( d7Vars );

    	Display displayOpts = new Display();
    	displayOpts.infoType = Display.InfoType.NAME;
    	displayOpts.hand = Hand.RIGHT;
    	displayOpts.orientation = Orientation.HORIZONTAL;
    	displayOpts.fretSpace = 1;
    	displayOpts.notPlayed = EnumSet.of( Display.NotPlayedLocation.HEAD );
    	displayOpts.notPlayedString = "x";
    	
		System.out.println( "Dmaj7 notes=" + d7 + ", absolute intervals=" + d7.toStringIntervals() + ", relative intervals=" + d7.toStringRelativeIntervals() );
		System.out.println( "Dmaj7 Variation (with octaves) count=" + varCount );
    	int chordableCount = 0;
    	int lowScore = Integer.MAX_VALUE; int highScore = 0;
    	for( int i = 0; i < varCount; i ++ ) {
    		LocationList variation = sortedVars.get( i );
    		int [] score = ranker.compositeScore(variation);
    		if ( score[ 0 ] < lowScore ) lowScore = score[ 0 ];
    		if ( score[ 0 ] > highScore ) highScore = score[ 0 ];
    		// if (variation.uniqueStrings()) {
    		if (variation.uniqueStrings() && (variation.fretSpan() <=4 )) {
				System.out.println("Dmaj7 octave variation " + i +
				   "   (" + ranker.getScoreString( variation ) + "):" + nl + 
				   standard.toString(variation, 0, 18, displayOpts));
				chordableCount++;
			}
    	}
    	System.out.println( "Dmaj7 1-5-7-3 Variation Chordable Count=" + chordableCount + 
    	   ", low/high score=" + lowScore + "/" + highScore );
    	
//       	String emptyHead = "x||";
//       	String head = " ||";
//       	String note = "o|";
//       	String space = " |";
//       	String none = emptyHead + space + space + space + space + space;
    	
//       	LocationList easiest = sortedVars.get( 0 );
//    	System.out.println( "C Roots 0 pos, righty, plain, easiest variation: " );
//    	System.out.println( standard.toString( easiest, 0, 6, displayOpts ));        	
//        assertEquals("FilenameRegExFilter horizontal plain righty, easy C roots",
//           none + nl +
//           none + nl +
//           head + space + space + space + space + note + nl +
//           none + nl +
//           head + space + space + note + space + space + nl +
//           none ,
//           standard.toString( easiest, 0, 6, displayOpts ));        	

    }
    
    @Test
    public void testFormulaVariations()
    {
    	Note lowF = Note.plus( Note.GuitarLowE, Interval.half );
    	NoteList fmin = new NoteList( lowF, "R-b3-5-b7" );
    	
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD ); 
    	List<LocationList> fminVars = standard.getVariations( fmin, Fretboard.ENHARMONICS );
		int varCount = Fretboard.getPermutationCount( fminVars );

		// There are 6 variations of fmin starting from lowF. 
		//   0 [[0,1], [0,4], [0,8], [0,11]]
	    //   1 [[0,1], [0,4], [0,8], [1,6]]
		//     ....
        //   5 [[0,1], [0,4], [1,3], [2,1]]		
		// Ensure all of them return "R-b3-5-b7"
		System.out.println( "Formula test variation count=" + varCount );   	
   	    assertEquals( "Fminor formula var count", 6, varCount );
    	for ( int i = 0; i < varCount; i++ ) {    	
    	   // LocationList variation = fminVars.get( i );
    	   LocationList variation = Fretboard.getPermutation( fminVars, i );
    	   String formula = variation.getFormula( standard, lowF );
    	   String notes = variation.getNotes( standard );
    	   System.out.println( "Fmin variation " + i + ", notes=" + notes + ", formula=" + formula + ", variation=" + variation );
    	   assertEquals( "Fminor formula", "R-b3-5-b7", formula );
    	}
    }

    @Test
    public void testFormulaInversions()
    {
    	Note lowF = Note.plus( Note.GuitarLowE, Interval.half );
    	NoteList fmin = new NoteList( lowF, "R-b3-5-b7" );
    	
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD );   	 
    	List<LocationList> fminVars = standard.getVariations( fmin, Fretboard.OCTAVES );
		int varCount = Fretboard.getPermutationCount( fminVars );
		System.out.println( "Formula test permutation count=" + varCount );
   	    assertEquals( "Formula inversion permutation count", 13310, varCount );
		
    	ChordRank ranker = ChordRank.instance.getInstance( ChordRank.STANDARD  );
    	List<LocationList> fminLocations = Fretboard.explodeAndSort( fminVars, ranker );
		// System.out.println( "Formula test variation count=" + fminLocations.size() );
		// Display displayOpts = Display.instance.getInstance( Display.HORIZONTAL_NAME );

    	// Test first 10 easiest playable variations
    	for ( int i = 0; i < 10; i++ ) {    	
    	   LocationList variation = fminLocations.get( i );
    	   // Variations tend to be note list order. Sort by location.
    	   Collections.sort( variation );
    	   // LocationList variation = Fretboard.getVariation( fminVars, i );
    	   String formula = variation.getFormula( standard, lowF );
    	   String notes = variation.getNotes( standard );
    	   System.out.println( "Fmin variation " + i + ", notes=" + notes + ", formula=" + formula + ", variation=" + variation );
           // System.out.println( "   octave variation " + i + NL + 
		   // standard.toString(variation, 0, 18, displayOpts));
    	   // Fmin variation 0, notes=D#³-G#³-C⁴-F⁴, formula=b7-b3-5-R, variation=[[2,1], [3,1], [4,1], [5,1]]
           // Fmin variation 1, notes=D#⁴-G#⁴-C⁵-F⁵, formula=b7-b3-5-R, variation=[[2,13], [3,13], [4,13], [5,13]]
    	   // Fmin variation 2, notes=D#³-G#³-C⁴-F⁴, formula=b7-b3-5-R, variation=[[0,11], [1,11], [2,10], [3,10]]
    	   // Fmin variation 3, notes=D#³-G#³-C⁴-F⁴, formula=b7-b3-5-R, variation=[[1,6], [2,6], [3,5], [4,6]]
    	   // Fmin variation 4, notes=F²-C³-D#³-G#³, formula=R-5-b7-b3, variation=[[0,1], [1,3], [2,1], [3,1]]
    	   // Fmin variation 5, notes=F³-C⁴-D#⁴-G#⁴, formula=R-5-b7-b3, variation=[[0,13], [1,15], [2,13], [3,13]]
    	   // Fmin variation 6, notes=C³-F³-G#³-D#⁴, formula=5-R-b3-b7, variation=[[0,8], [1,8], [2,6], [3,8]]
    	   // Fmin variation 7, notes=F³-C⁴-D#⁴-G#⁴, formula=R-5-b7-b3, variation=[[1,8], [2,10], [3,8], [4,9]]
    	   // Fmin variation 8, notes=F³-C⁴-D#⁴-G#⁴, formula=R-5-b7-b3, variation=[[2,3], [3,5], [4,4], [5,4]]
    	   // Fmin variation 9, notes=C⁴-F⁴-G#⁴-D#⁵, formula=5-R-b3-b7, variation=[[2,10], [3,10], [4,9], [5,11]]    	   
       	   switch( i ) {
       	   	  case 0: case 1: case 2: case 3:
    	         assertEquals( "Fminor variation" + i, "b7-b3-5-R", formula ); break;
       	   	  case 4: case 5: case 7: case 8:
    	         assertEquals( "Fminor variation" + i, "R-5-b7-b3", formula ); break;
       	   	  case 6: case 9:
    	         assertEquals( "Fminor variation" + i, "5-R-b3-b7", formula ); break;
      	   }
    	}
    }

    @Test
    public void testStringParse()
    {
    	LocationList emptyList = new LocationList();
        assertEquals("Location list parse 1", emptyList, LocationList.parseString( null ));
        assertEquals("Location list parse 2", emptyList, LocationList.parseString( "" ));
        assertEquals("Location list parse 3", emptyList, LocationList.parseString( " " ));
        assertEquals("Location list parse 4", emptyList, LocationList.parseString( "[]" ));
        assertEquals("Location list parse 5", new LocationList( new Location( 0, 0 ) ), LocationList.parseString( "0-0" ));
        assertEquals("Location list parse 6", new LocationList( new Location( 0, 0 ), new Location( 1, 2 ) ), LocationList.parseString( "0-0,1-2" ));
        assertEquals("Location list parse 6", new LocationList( new Location( 0, 0 ), new Location( 1, 2 ), new Location( 9, 13 )  ), LocationList.parseString( "0-0,1-2,9-13" ));
    }

    @Test
    public void testJson()  {
    	LocationList expected = LocationList.parseString( "0+0,1+2,5+4" );
    	String json = expected.toJSON();   	
    	System.out.println( "LocationList string=" + expected.toString() + ", json=" + json );
    	LocationList returned = LocationList.fromJSON( json );
    	assertEquals( "LocationList json", expected, returned );
    }

    
    @Test
    public void testLocationListFilter()
    {
		Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD );
		LocationList locations = standard.getLocations( Note.GuitarG );
       	// System.out.println( "Locations of G=" + locations );
        assertTrue("G locations", 4 == locations.size() );        	
       	
        assertEquals("Location 1", new Location( 0, 15 ), locations.get( 0 ) );        	
        assertEquals("Location 2", new Location( 1, 10 ), locations.get( 1 ) );        	
        assertEquals("Location 3", new Location( 2,  5 ), locations.get( 2 ) );        	
        assertEquals("Location 4", new Location( 3,  0 ), locations.get( 3 ) );        	

        // No filtering
        int delCount = locations.filter( new Location( 0, 0 ), new Location( 6, 20 ));
        assertTrue( "No filter", 0 == delCount );        	
        assertTrue( "No filter list count", 4 == locations.size() );        	

        // Filtering
        delCount = locations.filter( new Location( 0, 0 ), new Location( 6, 8 ));
        assertTrue( "Filter", 2 == delCount );        	
        assertTrue( "Filter list count", 2 == locations.size() );        	
    }


    @Test
    public void testLocationListProximity()
    {
    	// Given a list of fixed notes and a list of variable notes,
    	// find and rank all variations of the variable notes.
    	// Example, give a barre G chord, E form, fixed,
    	// now add in a 2/9 note, variable,
    	// find the G with 2/9 variations and rank them.
    	
		Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD );
		LocationList barreG = LocationList.parseString( "0+3,1+5,2+5,3+4,4+5,5+3" );
		
    	List<LocationList> Gadd9 = standard.getVariations( barreG, new NoteList( Note.parse( "B3") ), 0, standard.getMaxFret(), Fretboard.ENHARMONICS );
		int varCount = Fretboard.getPermutationCount( Gadd9 );
		System.out.println( "Gadd9 permutations count=" + varCount );

		Note lowG = Note.plus( Note.GuitarLowE, 3 );

		// There are 6 variations of fmin starting from lowF. 
		//   0 [0+1, 0+4, 0+8, 0+11]
	    //   1 [0+1, 0+4, 0+8, 1+6]
		//     ....
        //   5 [0+1, 0+4, 1+3, 2+1]		
		// Ensure all of them return "R-b3-5-b7"
    	for ( int i = 0; i < varCount; i++ ) {    	
    	   // LocationList variation = fminVars.get( i );
    	   LocationList variation = Fretboard.getPermutation( Gadd9, i );
    	   String formula = variation.getFormula( standard, lowG );
    	   String notes = variation.getNotes( standard );
    	   System.out.println( "Gadd9 variation " + i + ", notes=" + notes + ", formula=" + formula + ", variation=" + variation );
    	}
    }
}