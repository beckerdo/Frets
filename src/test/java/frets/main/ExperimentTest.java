package frets.main;

import static frets.main.Display.NL;
import static frets.main.Fretboard.OCTAVES;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * Unit tests to validate this class.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class ExperimentTest 
{
	Note lowF;
	
    @Before
    public void setup()
    {
    	lowF = Note.plus( Note.GuitarLowE, Interval.half );
    }
    
    @Ignore
    @Test
    public void testF() {
    	// Need to investigate variations that double up notes.
    	NoteList f = new NoteList( lowF, "R-3-5" );
    	testVariation( "Fmajor", f, true );
    }
    
    @Ignore
    @Test
    public void testFmaj7() {
    	NoteList f = new NoteList( lowF, "R-3-5-7" );
    	testVariation( "Fmaj7", f, OCTAVES );
    }
    
    @Ignore
    @Test
    public void testFmin7() {
    	NoteList f = new NoteList( lowF, "R-b3-5-b7" );
    	testVariation( "Fmin7", f, OCTAVES );
    }
    
    @Ignore
    @Test
    public void testF7() {
    	NoteList f = new NoteList( lowF, "R-3-5-b7" );
    	testVariation( "F7", f, OCTAVES );
    }
    
    @Ignore
    @Test
    public void testFm7b5() {
    	NoteList f = new NoteList( lowF, "R-b3-b5-b7" );
    	testVariation( "Fm7b5", f, OCTAVES );
    }

    @Test
    public void testTranspose() {
    	LocationList fminVar = new LocationList(
    			new Location( 0, 8 ),
    			new Location( 1, 8 ),
    			new Location( 2, 6 ),
    			new Location( 3, 8 ) );

    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD ); 
    	ChordRank ranker = ChordRank.instance.getInstance( ChordRank.STANDARD  );
		Display displayOpts = Display.instance.getInstance( Display.HORIZONTAL_NAME );
		displayOpts.infoType = Display.InfoType.INTERVAL;
		displayOpts.root = lowF;

		String name = "Fmin Variation";
        String formula = fminVar.getFormula( standard, lowF );
        
		System.out.println( name + 
		   ", formula=" + formula + NL + 
		   "   (" + ranker.getScoreString( fminVar ) + "):" + NL + 
		   standard.toString(fminVar, 0, 18, displayOpts));
    }
    

    public void testVariation( String name, NoteList noteList, boolean includeOctaves )
    {
    	Fretboard standard = Fretboard.getInstanceFromName( Fretboard.STANDARD ); 
 
    	List<LocationList> noteListVars = standard.getVariations( noteList, includeOctaves );
    	ChordRank ranker = ChordRank.instance.getInstance( ChordRank.STANDARD  );
    	List<LocationList> sortedVars = Fretboard.explodeAndSort( noteListVars, ranker );
		int varCount = Fretboard.getPermutationCount( noteListVars );

		Display displayOpts = Display.instance.getInstance( Display.HORIZONTAL_NAME );
		displayOpts.infoType = Display.InfoType.INTERVAL;
		displayOpts.root = lowF;
    	
		System.out.println( name + " notes=" + noteList + 
		   ", absolute intervals=" + noteList.toStringIntervals() + 
		   ", relative intervals=" + noteList.toStringRelativeIntervals() );
		System.out.println( name + " variation (with octaves) count=" + varCount );
    	int chordableCount = 0;
    	int lowScore = Integer.MAX_VALUE; int highScore = 0;
    	for( int i = 0; i < varCount; i ++ ) {
    		LocationList variation = sortedVars.get( i );
    		int [] score = ranker.compositeScore(variation);
    		if ( score[ 0 ] < lowScore ) lowScore = score[ 0 ];
    		if ( score[ 0 ] > highScore ) highScore = score[ 0 ];
    		if (variation.uniqueStrings() && (score[ 0 ] <=3)) {
    			Collections.sort( variation );
                String formula = variation.getFormula(standard, lowF );
				System.out.println( name + " octave variation " + i +
						", formula=" + formula + NL + 
						"   (scores span=" + variation.fretSpan() +
						", in bounds [0,12]=" + variation.getInBoundsCount(0, 12) + 
						", skip strings=" + variation.getSkippedStringCount() +
						", unique strings=" + variation.getStringCount() + 
						", total=" + score + "):" +	NL + 
						standard.toString(variation, 0, 18, displayOpts));
				chordableCount++;
			}
    	}
    	System.out.println( name + " variation chordable count=" + chordableCount + 
    	   ", low/high score=" + lowScore + "/" + highScore );
    }
}