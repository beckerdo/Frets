package frets.main;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.util.EnumSet;
import java.util.List;

import static frets.main.Display.Orientation;
import static frets.main.Display.Hand;

/**
 * Unit tests to validate this class.
 * 
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class FretboardCharGraphicTest {
	protected Fretboard standard;
	String nl = Display.NL;

	@Before
	public void setup() {
		standard = new Fretboard(
			new GuitarString(Note.GuitarLowE), 
			new GuitarString(Note.GuitarA), 
			new GuitarString(Note.GuitarD), 
			new GuitarString(Note.GuitarG), 
			new GuitarString(Note.GuitarB), 
			new GuitarString(Note.GuitarHighE)
		);
	}

	@Test
	public void testList() {
		assertEquals("Tuning 6", standard.getString(0), new GuitarString(Note.GuitarLowE));
		assertEquals("Tuning 5", standard.getString(1), new GuitarString(Note.GuitarA));
		assertEquals("Tuning 4", standard.getString(2), new GuitarString(Note.GuitarD));
		assertEquals("Tuning 3", standard.getString(3), new GuitarString(Note.GuitarG));
		assertEquals("Tuning 2", standard.getString(4), new GuitarString(Note.GuitarB));
		assertEquals("Tuning 1", standard.getString(5), new GuitarString(Note.GuitarHighE));
	}

	@Test
	public void testtoStringLocationsHorizontal() {
		NoteList cRootsPos0 = new NoteList(Note.plus(Note.GuitarB, Interval.half), Note.plus(Note.GuitarA,
				Interval.wholehalf));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations(cRootsPos0);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos0Vars, 7);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.PLAIN;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.orientation = Orientation.HORIZONTAL;
		displayOpts.fretSpace = 1;
		displayOpts.notPlayed = EnumSet.of(Display.NotPlayedLocation.HEAD);
		displayOpts.notPlayedString = "x";

		// int varCount = FilenameRegExFilter.getVariationCount( cRootsPos0Vars
		// );
		// System.out.println( "C Roots Variation Count=" + varCount );
		// for( int i = 0; i < varCount; i ++ ) {
		// LocationList variation = FilenameRegExFilter.getVariation(
		// cRootsPos0Vars, i );
		// System.out.println( "C Roots Variation " + i +
		// " (span=" + variation.fretSpan() +
		// ", unique strings=" + variation.uniqueStrings() +
		// ", in bounds [1,5]=" + variation.getInBoundsCount( 0, 5 ) +
		// "):" + nl +
		// standard.toString( variation, 0, 18, displayOpts ) + Display.NL );
		// }

		String emptyHead = "x||";
		String head = " ||";
		String note = "o|";
		String space = " |";
		String none = emptyHead + space + space + space + space;

		// System.out.println( "C roots 0 pos, righty, plain: " );
		// System.out.println( "loc=" + cLocations );
		// System.out.println( standard.toString( cLocations, 0, 5, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter horizontal plain righty", none + nl + head + note + space + space + space
				+ nl + none + nl + none + nl + head + space + space + note + space + nl + none,
				standard.toString(cLocations, 0, 5, displayOpts));

		displayOpts.infoType = Display.InfoType.NAME;
		displayOpts.hand = Hand.LEFT;
		head = "|| ";
		emptyHead = "||x";
		note = "|C ";
		space = "|  ";
		none = space + space + space + space + emptyHead;

		// System.out.println( "C roots 0 pos, lefty, named: " );
		// System.out.println( standard.toString( cRootsPos0, 0, 5, displayOpts
		// ));
		assertEquals("FilenameRegExFilter horizontal plain lefty", none + nl + space + space + space + note + head + nl
				+ none + nl + none + nl + space + note + space + space + head + nl + none,
				standard.toString(cLocations, 0, 5, displayOpts));
	}

	@Test
	public void testtoStringNotesHorizontal() {
		NoteList cRootsPos0 = new NoteList(Note.plus(Note.GuitarB, Interval.half), Note.plus(Note.GuitarA,
				Interval.wholehalf));
		// Pick a variation for display. Will change if order changes
		List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations(cRootsPos0);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos0Vars, 7);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.PLAIN;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.orientation = Orientation.HORIZONTAL;
		displayOpts.fretSpace = 1;

		String played = " ||";
		String notPlayed = "x||";
		String note = "o|";
		String space = " |";
		String none = notPlayed + space + space + space + space;

		// System.out.println( "C roots 0 pos, righty, plain: " );
		// System.out.println( standard.toString( cRootsPos0, 0, 5, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter horizontal plain righty", none + nl + played + note + space + space + space
				+ nl + none + nl + none + nl + played + space + space + note + space + nl + none,
				standard.toString(cLocations, 0, 5, displayOpts));

		displayOpts.infoType = Display.InfoType.NAME;
		displayOpts.hand = Hand.LEFT;
		played = "|| ";
		notPlayed = "||x";
		note = "|C ";
		space = "|  ";
		none = space + space + space + space + notPlayed;

		// System.out.println( "C roots 0 pos, lefty, named: " );
		// System.out.println( standard.toString( cRootsPos0, 0, 5, displayOpts
		// ));
		assertEquals("FilenameRegExFilter horizontal plain righty", none + nl + space + space + space + note + played
				+ nl + none + nl + none + nl + space + note + space + space + played + nl + none,
				standard.toString(cLocations, 0, 5, displayOpts));
	}

	@Test
	public void testtoStringHorizontalCompact() {
		NoteList cRootsPos0 = new NoteList(Note.plus(Note.GuitarB, Interval.half), Note.plus(Note.GuitarA,
				Interval.wholehalf));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations(cRootsPos0);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos0Vars, 7);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.PLAIN;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.nutString = "|";
		displayOpts.fretString = "";
		displayOpts.stringString = "-";
		displayOpts.fretSpace = 1;

		String played = " |";
		String notPlayed = "x|";
		String note = "o";
		String space = "-";
		String none = notPlayed + space + space + space + space;

		// System.out.println( "C roots 0 pos, righty, plain, compact: " );
		// System.out.println( standard.toString( cRootsPos0, 0, 5, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter horizontal plain righty", none + nl + played + note + space + space + space
				+ nl + none + nl + none + nl + played + space + space + note + space + nl + none,
				standard.toString(cLocations, 0, 5, displayOpts));

		displayOpts.infoType = Display.InfoType.NAME;
		displayOpts.hand = Hand.LEFT;
		displayOpts.openStringDisplay = false;

		played = "| ";
		notPlayed = "|x";
		note = "C-";
		space = "--";
		none = space + space + space + space + notPlayed;

		// System.out.println( "C roots 0 pos, lefty, named, compact: " );
		// System.out.println( standard.toString( cRootsPos0, 0, 5, displayOpts
		// ));
		assertEquals("FilenameRegExFilter horizontal named lefty", none + nl + space + space + space + note + played
				+ nl + none + nl + none + nl + space + note + space + space + played + nl + none,
				standard.toString(cLocations, 0, 5, displayOpts));
	}

	@Test
	public void testtoStringHorizontalCompactNotPlayed() {
		NoteList cRootsPos0 = new NoteList(Note.plus(Note.GuitarB, Interval.half), Note.plus(Note.GuitarA,
				Interval.wholehalf));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations(cRootsPos0);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos0Vars, 7);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.PLAIN;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.nutString = "|";
		displayOpts.fretString = "";
		displayOpts.stringString = "-";
		displayOpts.fretSpace = 1;
		displayOpts.notPlayed = EnumSet.of(Display.NotPlayedLocation.HEAD, Display.NotPlayedLocation.FIRST);
		displayOpts.notPlayedString = "x";
		displayOpts.openStringDisplay = true;

		String head = "x|";
		String space = "-";
		String none = head + space + space + space + space;
		String c2 = "-|o---";
		String c4 = "-|--o-";

		System.out.println("C roots 0 pos, righty, plain, compact not played: ");
		System.out.println(standard.toString(cLocations, 0, 10, displayOpts));
		assertEquals("FilenameRegExFilter horizontal plain righty compact not played", none + nl + c2 + nl + none + nl
				+ none + nl + c4 + nl + none, standard.toString(cLocations, 0, 5, displayOpts));
	}

	@Test
	public void testtoStringHorizontalCompactNotPlayedFret() {
		NoteList cRootsPos0 = new NoteList(Note.plus(Note.GuitarB, Interval.half), Note.plus(Note.GuitarA,
				Interval.wholehalf));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations(cRootsPos0);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos0Vars, 7);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.PLAIN;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.nutString = "|";
		displayOpts.fretString = "";
		displayOpts.stringString = "-";
		displayOpts.fretSpace = 1;
		displayOpts.notPlayed = EnumSet.of(Display.NotPlayedLocation.HEAD, Display.NotPlayedLocation.FIRST);
		displayOpts.notPlayedString = "x";
		displayOpts.openStringDisplay = false;

		String head = "x|";
		String space = "-";
		String none = head + space + space + space + space;
		String c2 = " |o---";
		String c4 = " |--o-";

		// System.out.println(
		// "C roots 0 pos, righty, plain, compact not played: " );
		// System.out.println( standard.toString( cRootsPos0, 0, 5, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter horizontal plain righty compact not played", none + nl + c2 + nl + none + nl
				+ none + nl + c4 + nl + none, standard.toString(cLocations, 0, 5, displayOpts));
	}

	@Test
	public void testtoStringHorizontalCompactNotPlayedFretNumbering() {
		NoteList cRootsPos0 = new NoteList(Note.plus(Note.GuitarB, Interval.half), Note.plus(Note.GuitarA,
				Interval.wholehalf));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations(cRootsPos0);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos0Vars, 7);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.PLAIN;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.nutString = "|";
		displayOpts.fretString = "";
		displayOpts.stringString = "-";
		displayOpts.fretSpace = 1;
		displayOpts.notPlayed = EnumSet.of(Display.NotPlayedLocation.HEAD, Display.NotPlayedLocation.FIRST);
		displayOpts.notPlayedString = "x";
		displayOpts.fretNumbering = EnumSet.of(Display.FretNumbering.FIRSTLEFT, Display.FretNumbering.FIRSTRIGHT);
		displayOpts.openStringDisplay = false;
		displayOpts.fretNumberingDisplayOpen = true;

		String fretNumber = "0";
		String head = "x|";
		String space = "-";
		String none = head + space + space + space + space;
		String c2 = " |o---";
		String c4 = " |--o-";

		displayOpts.fretNumberingDisplayOpen = false;
		// System.out.println(
		// "C roots 0 pos, righty, plain, compact not played, fret #s, Open: "
		// );
		// System.out.println( standard.toString( cRootsPos0, 0, 5, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter horizontal plain righty compact not played, open fret #s ", none + nl + c2
				+ nl + none + nl + none + nl + c4 + nl + none, standard.toString(cLocations, 0, 5, displayOpts));

		displayOpts.fretNumberingDisplayOpen = true;
		fretNumber = "0";
		assertEquals("FilenameRegExFilter horizontal plain righty compact not played, no open fret #s ", fretNumber
				+ nl + none + nl + c2 + nl + none + nl + none + nl + c4 + nl + none + nl + fretNumber,
				standard.toString(cLocations, 0, 5, displayOpts));

		fretNumber = "3";
		none = "x----";
		String c3 = "o----";
		// System.out.println(
		// "C roots 1 pos, righty, plain, compact not played, fret #s: " );
		// System.out.println( standard.toString( cRootsPos0, 3, 8, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter horizontal plain righty compact not played, fret #s ", fretNumber + nl + none
				+ nl + none + nl + none + nl + none + nl + c3 + nl + none + nl + fretNumber,
				standard.toString(cLocations, 3, 8, displayOpts));
	}

	@Test
	public void testtoStringVerticalCompact() {
		NoteList cRootsPos0 = new NoteList(Note.plus(Note.GuitarB, Interval.half), Note.plus(Note.GuitarA,
				Interval.wholehalf));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos0Vars = standard.getEnharmonicVariations(cRootsPos0);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos0Vars, 7);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.PLAIN;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.orientation = Orientation.VERTICAL;
		displayOpts.nutString = "-";
		displayOpts.fretString = "";
		displayOpts.stringString = "|";
		displayOpts.fretSpace = 1;
		displayOpts.openStringDisplay = true;
		displayOpts.fretNumberingDisplayOpen = false;

		String head = "x|xx|x";
		String nut = "------";
		String space = "||||||";
		String c1 = "||||o|";
		String c2 = "|o||||";

		// System.out.println(
		// "C roots 0 pos, vertical, righty, plain, compact: " );
		// System.out.println( standard.toString( cRootsPos0, 0, 5, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter vertical plain righty open strings", head + nl + nut + nl + c1 + nl + space
				+ nl + c2 + nl + space + nl, standard.toString(cLocations, 0, 5, displayOpts));

		displayOpts.openStringDisplay = false;
		head = "x xx x";
		assertEquals("FilenameRegExFilter vertical plain righty no open strings", head + nl + nut + nl + c1 + nl
				+ space + nl + c2 + nl + space + nl, standard.toString(cLocations, 0, 5, displayOpts));
	}

	@Test
	public void testtoStringVerticalNotesCompact() {
		NoteList cRootsPos1 = new NoteList(Note.plus(Note.GuitarA, Interval.wholehalf), Note.plus(Note.GuitarG,
				Interval.fourth));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos1Vars = standard.getEnharmonicVariations(cRootsPos1);
		// int varCount = FilenameRegExFilter.getVariationCount( cRootsPos1Vars
		// );
		LocationList cLocations = Fretboard.getPermutation(cRootsPos1Vars, 6);
		// for ( int i = 0; i < varCount; i++ )
		// System.out.println( "C locations variation " + i + ": " +
		// FilenameRegExFilter.getVariation( cRootsPos1Vars, i ) );

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.NAME;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.orientation = Orientation.VERTICAL;
		displayOpts.nutString = "-";
		displayOpts.fretString = "";
		displayOpts.stringString = "|";
		displayOpts.fretSpace = 1;
		displayOpts.openStringDisplay = true;
		displayOpts.fretNumberingDisplayOpen = false;

		String c2 = "3| C | | | | 3";
		String c3 = " | | | C | |  ";
		String space = " | | | | | |  ";

		// System.out.println(
		// "C roots pos 1, vertical, righty, named, compact: " );
		// System.out.println( standard.toStringVert( cRootsPos1, 3, 7,
		// displayOpts ) );
		assertEquals("FilenameRegExFilter vertical plain righty", c2 + nl + space + nl + c3 + nl + space + nl,
				standard.toString(cLocations, 3, 7, displayOpts));
	}

	@Test
	public void testtoStringVerticalNotesCompactNotPlayed() {
		NoteList cRootsPos1 = new NoteList(Note.plus(Note.GuitarA, Interval.wholehalf), Note.plus(Note.GuitarG,
				Interval.fourth));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos1Vars = standard.getEnharmonicVariations(cRootsPos1);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos1Vars, 6);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.NAME;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.orientation = Orientation.VERTICAL;
		displayOpts.nutString = "-";
		displayOpts.fretString = "";
		displayOpts.stringString = "|";
		displayOpts.fretSpace = 1;
		displayOpts.notPlayed = EnumSet.of(Display.NotPlayedLocation.HEAD, Display.NotPlayedLocation.FIRST);
		displayOpts.notPlayedString = "x";

		String c2 = "3x C x | x x 3";
		String space = " | | | | | |  ";
		String c3 = " | | | C | |  ";

		// System.out.println(
		// "C roots pos 1, vertical, righty, named, compact, not played: " );
		// System.out.println( standard.toString( cRootsPos1, 3, 6, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter vertical righty named compact not played", c2 + nl + space + nl + c3 + nl,
				standard.toString(cLocations, 3, 6, displayOpts));

	}

	@Test
	public void testtoStringVerticalNotesCompactNotPlayedFretNum() {
		NoteList cRootsPos1 = new NoteList(Note.plus(Note.GuitarA, Interval.wholehalf), Note.plus(Note.GuitarG,
				Interval.fourth));
		// Pick a variation for display. Will change if order changes.
		List<LocationList> cRootsPos1Vars = standard.getEnharmonicVariations(cRootsPos1);
		LocationList cLocations = Fretboard.getPermutation(cRootsPos1Vars, 6);

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.NAME;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.orientation = Orientation.VERTICAL;
		displayOpts.nutString = "-";
		displayOpts.fretString = "";
		displayOpts.stringString = "|";
		displayOpts.fretSpace = 1;
		displayOpts.notPlayed = EnumSet.of(Display.NotPlayedLocation.HEAD, Display.NotPlayedLocation.FIRST);
		displayOpts.notPlayedString = "x";
		displayOpts.fretNumbering = EnumSet.of(Display.FretNumbering.FIRSTLEFT, Display.FretNumbering.FIRSTRIGHT);
		displayOpts.openStringDisplay = true;

		String c2 = "3x C x | x x 3";
		String space = " | | | | | |  ";
		String c3 = " | | | C | |  ";

		// System.out.println(
		// "C roots pos 1, vertical, righty, named, compact, not played, fret num: "
		// );
		System.out.println( standard.toString( cRootsPos1, 3, 6, displayOpts ));
		assertEquals("FilenameRegExFilter vertical righty named compact not played fretnum, open string displayed", c2
				+ nl + space + nl + c3 + nl, standard.toString(cLocations, 3, 6, displayOpts));

		displayOpts.openStringDisplay = false; // no effect since
		assertEquals("FilenameRegExFilter vertical righty named compact not played fretnum, open string not displayed",
				c2 + nl + space + nl + c3 + nl, standard.toString(cLocations, 3, 6, displayOpts));
	}

}