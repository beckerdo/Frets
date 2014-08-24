package frets.main;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import frets.main.Display.Orientation;

import static frets.main.Display.Hand;
import static frets.main.Display.VAlign;
import static frets.main.Display.NotPlayedLocation;

/**
 * Encapsulates a guitar string tied to a fret board.
 *
* @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class GuitarString implements Comparable<GuitarString> {

	public static final int NOFRET = -1;
	
	public GuitarString(Note openNote ) {
		this( openNote, null, 12, 18 );
	}
	
	public GuitarString(Note openNote, int octaveFret, int maxFret) {
		this( openNote, null, octaveFret, maxFret );
	}
	
	public GuitarString(Note openNote, List<Note> slaves, int octaveFret, int maxFret) {
		super();
		this.openNote = openNote;
		this.slaves = slaves;
		this.octaveFret = octaveFret;
		this.maxFret = maxFret;
	}

	public Note getOpenNote() {
		return openNote;
	}

	public Note getHighNote() {
		return Note.plus( openNote, maxFret );
	}

	public void setOpenNote(Note openNote) {
		this.openNote = openNote;
	}

	public List<Note> getSlaves() {
		return slaves;
	}

	public void setSlaves(List<Note> slaves) {
		this.slaves = slaves;
	}

	public int getOctaveFret() {
		return octaveFret;
	}

	public void setOctaveFret(int octaveFret) {
		this.octaveFret = octaveFret;
	}

	public int getMaxFret() {
		return maxFret;
	}

	public void setMaxFret(int maxFret) {
		this.maxFret = maxFret;
	}

	/** Returns fret of given note. Returns NOFRET if Note is null, < open note, or fret > maxFret. */
	public int getFret( Note note ) {
		if ( null == note ) return NOFRET;
		int compare = note.compareTo( openNote );
		if ( compare < 0 )
			return NOFRET;
		if ( compare >= maxFret )
			return NOFRET;
		return compare;		
	}
	
	/** Returns note of given fret. */
	public Note getNote( int fret ) {
		if ( fret < 0 )
			throw new IllegalArgumentException(  "Fret \"" + fret + "\" is less than 0.");
		if ( fret > maxFret )
			throw new IllegalArgumentException(  "Fret \"" + fret + "\" is greater than max fret " + maxFret + ".");
		return Note.plus( openNote, fret );
	}
	
	/** Returns number of notes on this string. Accepts null Note set. */
	public int getCountThisString( NoteList notes ) {
		int count = 0;
		if ( null != notes ) {
			for ( int i = 0; i < notes.size(); i++ ) {
				int freti = getFret( notes.get( i ) );
				if ( -1 != freti )
					count++;
			}
		}
		return count;		
	}
	
	/** Returns number of notes on this string range. Accepts null Note set. */
	public int getCountThisRange( final NoteList notes, int lowFret, int highFret ) {
		int count = 0;
		if ( null != notes ) {
			for ( int i = 0; i < notes.size(); i++ ) {
				int freti = getFret( notes.get( i ) );
				if (( -1 != freti ) && ( freti >= lowFret ) && ( freti < highFret ))
					count++;
			}
		}
		return count;		
	}

	/** Returns number of locations on this string range. Accepts null locations set. */
	public int getCountThisRange( final List<Integer> locations, int lowFret, int highFret ) {
		int count = 0;
		if ( null != locations ) {
			for ( int i = 0; i < locations.size(); i++ ) {
				int freti = locations.get( i );
				if (( freti >= lowFret ) && ( freti < highFret ))
					count++;
			}
		}
		return count;		
	}

	/** Convert list of frets to set of notes. */
	public NoteList getNoteList( final List<Integer> locations ) {
		NoteList notes = new NoteList();
		for ( int freti : locations ) {
			if (( freti >= 0 ) && (freti < maxFret))
				notes.add( this.getNote(freti) );
		}
		return notes;
	}
	
	/** Convert list of frets in range to set of notes. */
	public NoteList getNoteList( final List<Integer> locations, int lowFret, int highFret ) {
		NoteList notes = new NoteList();
		for ( int freti : locations ) {
			if (( freti >= lowFret ) && (freti < highFret))
				notes.add( this.getNote(freti) );
		}
		return notes;
	}
	
	/** Convert set of notes to list of frets. */
	public List<Integer> getLocations( final NoteList notes ) {
		List<Integer> locations = new LinkedList<Integer>();
		for ( Note note : notes) {
			int freti = this.getFret( note );
			if ( NOFRET != freti )
				locations.add( freti );
		}
		return locations;
	}
	
	/** Convert set of notes in range to list of frets. */
	public List<Integer> getLocations( final NoteList notes, int lowFret, int highFret ) {
		List<Integer> locations = new LinkedList<Integer>();
		for ( Note note : notes) {
			int freti = this.getFret( note );
			if (( NOFRET != freti ) && ( freti >= lowFret ) && (freti < highFret ))
				locations.add( freti );
		}
		return locations;
	}
	
	/** 
	 * Show note on strings in ASCII from fret to fret.
	 * @param lowFret
	 * @param highFret
	 * @return
	 */
	public String toString( final NoteList notes, int lowFret, int highFret, Display displayOpts ) {
		if ( lowFret < 0 )
			throw new IllegalArgumentException ( "Low fret \"" + lowFret + "\" is less than 0.");
		if ( highFret > maxFret )
			throw new IllegalArgumentException ( "High fret \"" + highFret + "\" is greater than max fret \"" + maxFret + "\"." );
		if ( lowFret >= highFret )
			throw new IllegalArgumentException ( "Low fret \"" + lowFret + "\" is greater than or equal to high fret \"" + highFret + "\".");
		return toString( this.getLocations( notes, lowFret, highFret ), lowFret, highFret, displayOpts );
	}


	/** 
	 * Show strings in ASCII from fret to fret.
	 * @param lowFret
	 * @param highFret
	 * @return
	 */
	public String toString( final List<Integer> locations, int lowFret, int highFret, Display displayOpts ) {
		if ( lowFret < 0 )
			throw new IllegalArgumentException ( "Low fret \"" + lowFret + "\" is less than 0.");
		if ( highFret > maxFret )
			throw new IllegalArgumentException ( "High fret \"" + highFret + "\" is greater than max fret \"" + maxFret + "\"." );
		if ( lowFret >= highFret )
			throw new IllegalArgumentException ( "Low fret \"" + lowFret + "\" is greater than or equal to high fret \"" + highFret + "\".");
		StringBuilder sb = new StringBuilder();
		// Figure characters to use.
		String spaceString = " ";
		if (( null != displayOpts.spaceString ) && (displayOpts.spaceString.length() > 0))
			spaceString = displayOpts.spaceString;
		String stringString = "";
		if (( null != displayOpts.stringString ))
			stringString = displayOpts.stringString;
		String plainNoteString = "o";
		if (( null != displayOpts.plainNoteString ))
			plainNoteString = displayOpts.plainNoteString;
		int noteSpace = plainNoteString.length();
		// Note names and intervals take 2 spaces, so pump up the minimum
		if (( noteSpace < 2 ) && ( EnumSet.of( Display.InfoType.NAME, Display.InfoType.INTERVAL ).contains( displayOpts.infoType ))) noteSpace = 2;
		
		int fretSpace = displayOpts.fretSpace;
		int headSpace = displayOpts.headSpace;
		String nutString = "||"; 
		if ( null != displayOpts.nutString )
			nutString = displayOpts.nutString;
		if ( displayOpts.orientation == Orientation.VERTICAL ) {
			// Length nut String if needed.
			if ( nutString.length() > 0 ) {
				while ( nutString.length() < noteSpace )
					nutString += nutString;
			}
			nutString += Display.NL;
		}
		String fretString = null; 
		if ( null != displayOpts.fretString )
			fretString = displayOpts.fretString;
		if ( displayOpts.orientation == Orientation.VERTICAL ) {
			// Lengthen fret String if needed.
			if (( null != fretString ) && ( fretString.length() > 0 )) {
				while ( fretString.length() < noteSpace )
					fretString += fretString;
				fretString += Display.NL;
			}
		}
		int noteCountThisRange = this.getCountThisRange( locations, lowFret, highFret ); 
		
		// Example: open plus whole on 0 to 5 righty is "o|| |o| | |"
		if ( displayOpts.hand == Hand.RIGHT ) {
			for ( int freti = lowFret; freti < highFret; freti++ ) {
				VAlign align = displayOpts.fretAlign;
				int space = fretSpace;
				String padString = spaceString;
				if ( stringString.length() > 0 ) padString = stringString;
				if ( 0 == freti ) {
					// Above the nut
					align = displayOpts.headAlign;
					space = headSpace;
					if ( displayOpts.openStringDisplay == false ) { 
						padString = spaceString;
						while ( noteSpace > padString.length()) 
							padString += spaceString;
					}
				}
				// Create a no note space.
				String display = Display.pad( padString, noteSpace, space, align,
					displayOpts.hand, displayOpts.orientation, stringString, spaceString );

				// Open string check.
				if ( 0 == noteCountThisRange ) {
					if (( 0 == freti ) && ( null != displayOpts.notPlayed ) && 
						( displayOpts.notPlayed.contains( NotPlayedLocation.HEAD ))) {
						// Open string, above nut. 
						display = Display.pad( displayOpts.notPlayedString, noteSpace, headSpace, displayOpts.headAlign, 
							displayOpts.hand, displayOpts.orientation, stringString, spaceString );
					} else if (
						( lowFret == freti ) && ( null != displayOpts.notPlayed ) && 
						( displayOpts.notPlayed.contains( NotPlayedLocation.FIRST ))) {
						// Open string, first fret.
						display = Display.pad( displayOpts.notPlayedString, noteSpace, fretSpace, displayOpts.headAlign, 
							displayOpts.hand, displayOpts.orientation, stringString, spaceString );
					}
				} else if (( null != locations ) && locations.contains( freti )) {
				    String value = plainNoteString;
					switch ( displayOpts.infoType ) {
						case PLAIN: break;
						case FINGERNUMBER: value = String.valueOf( freti - lowFret + 1 );  break;
						case FINGERLATIN: value = Finger.getFinger(freti - displayOpts.handPosition + 1).getShortLatin(); break;
						case INTERVAL: value = String.valueOf( this.getNote( freti ).getQualityName( displayOpts.root )); break;
						case NAME: value = this.getNote( freti ).getName();	break;
					}
					display = Display.pad( value, noteSpace, space, align, 
						displayOpts.hand, displayOpts.orientation, stringString, spaceString );
				}
				sb.append( display );
				// Trailing nut or fret.
				if ( 0 == freti ) {
					if ( null != nutString )
						sb.append(nutString);
				} else {
					if ( null != fretString )
						sb.append(fretString);
				}					
			}
		} else { // lefty
			for ( int freti = highFret - 1; freti >= lowFret; freti-- ) {
				// Convert vertical alignment meanings to lefty alignments.
				VAlign align = displayOpts.fretAlign;
				int space = fretSpace;
				String padString = spaceString;
				if ( stringString.length() > 0 ) padString = stringString;
				if ( 0 == freti ) {
					// Above the nut
					align = displayOpts.headAlign;
					space = noteSpace;
					if ( displayOpts.openStringDisplay == false ) {
						padString = spaceString;
						while ( headSpace > padString.length()) 
							padString += spaceString;
					}
				} 
				// Create a no note space.
				String display = padString;
				if ( 0 != freti )
				   display = Display.pad( padString, noteSpace, space, align,
						displayOpts.hand, displayOpts.orientation, stringString, spaceString );
	            // Open string check.
				if ( 0 == noteCountThisRange ) {
					if (( 0 == freti ) && ( null != displayOpts.notPlayed ) && 
						( displayOpts.notPlayed.contains( NotPlayedLocation.HEAD ))) {
						// Open string, above nut. 
						display = Display.pad( displayOpts.notPlayedString, headSpace, headSpace, displayOpts.headAlign, 
						   displayOpts.hand, displayOpts.orientation, stringString, spaceString );
					} else if (
						( lowFret == freti ) && ( null != displayOpts.notPlayed ) && 
						( displayOpts.notPlayed.contains( NotPlayedLocation.FIRST ))) {
						// Open string, first fret.
						display = Display.pad( displayOpts.notPlayedString, noteSpace, fretSpace, displayOpts.headAlign, 
								displayOpts.hand, displayOpts.orientation, stringString, spaceString );
					}
				} else if (( null != locations ) && locations.contains( freti )) {
				    String value = plainNoteString;
					switch ( displayOpts.infoType ) {
						case PLAIN: break;
						case FINGERNUMBER: value = String.valueOf( freti - lowFret + 1 );  break;
						case FINGERLATIN: value = Finger.getFinger(freti - displayOpts.handPosition + 1).getShortLatin(); break;
						case INTERVAL: value = String.valueOf( this.getNote( freti ).getQuality( displayOpts.root )); break;
						case NAME: value = this.getNote( freti ).getName();	break;
					}
					display = Display.pad( value, noteSpace, space, align, 
						displayOpts.hand, displayOpts.orientation, stringString, spaceString );
				}
				// Leading nut or fret.
				if ( 0 == freti ) {
					if ( null != nutString )
						sb.append(nutString);
				} else {
					if ( null != fretString )
						sb.append(fretString);
				}					
				sb.append( display );
			}
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + openNote.hashCode();
		result = prime * result + octaveFret;
		result = prime * result + maxFret;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GuitarString other = (GuitarString) obj;
		if (!openNote.equals(other.openNote))
			return false;
		if (octaveFret != other.octaveFret)
			return false;
		if (maxFret != other.maxFret)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder( openNote.toString() );
		// add in octave and max frets
		// add in slaves
		return ( sb.toString());   		
	}

	/** Higher string returns higher value compared to others. */
	public int compareTo(GuitarString o) {
		int value = 1009 * openNote.compareTo( o.openNote );
		value += 101 * ( octaveFret - o.octaveFret );
		value += maxFret - o.maxFret;
		return value;
	}

	protected Note openNote;
	protected List<Note> slaves; // emulation of multiString guitars;
	protected int octaveFret;
	protected int maxFret;
}