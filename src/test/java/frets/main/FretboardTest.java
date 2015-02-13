package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static frets.main.Display.Orientation;
import static frets.main.Display.Hand;

/**
 * Unit tests to validate this class.
 * 
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class FretboardTest {
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
	public void testCRUD() {
		GuitarString drone = new GuitarString(Note.plus(Note.GuitarG, Interval.octave));
		GuitarString worker = new GuitarString(Note.plus(Note.GuitarA, Interval.octave));
		GuitarString queen = new GuitarString(Note.plus(Note.GuitarHighE, Interval.octave));
		Fretboard test = new Fretboard();

		assertTrue("Create size", 0 == test.size());
		test.add(drone);
		assertTrue("Add size", 1 == test.size());
		test.add(0, worker);
		assertTrue("Two", 2 == test.size());
		assertEquals("First", test.get(0), worker);
		assertEquals("Second", test.get(1), drone);
		test.set(1, queen);
		assertTrue("Update size", 2 == test.size());
		assertEquals("Update", test.get(1), queen);
		test.remove(0);
		assertTrue("Delete size", 1 == test.size());
		assertEquals("First", test.get(0), queen);
		test.clear();
		assertTrue("Empty", test.isEmpty());
	}

	@Test
	public void testLowHigh() {
		GuitarString lowString = standard.getLowString();
		assertEquals("Low string standard", new GuitarString(Note.GuitarLowE), lowString);
		GuitarString highString = standard.getHighString();
		assertEquals("High string standard", new GuitarString(Note.GuitarHighE), highString);

		Fretboard crazy = new Fretboard(
			new GuitarString(Note.GuitarD), 
			new GuitarString(Note.GuitarHighE),
			new GuitarString(Note.GuitarA), 
			new GuitarString(Note.GuitarLowE), 
			new GuitarString(Note.GuitarHighE),
			new GuitarString(Note.GuitarG), 
			new GuitarString(Note.GuitarLowE), 
			new GuitarString(Note.GuitarB));
		lowString = crazy.getLowString();
		assertEquals("Low string non-standard", new GuitarString(Note.GuitarLowE), lowString);
		highString = crazy.getHighString();
		assertEquals("High string non-standard", new GuitarString(Note.GuitarHighE), highString);
	}

	@Test
	public void testVariations() {
		// No notes == no variations
		List<LocationList> noVariations = standard.getEnharmonicVariations(null);
		assertTrue("No variations", null == noVariations);

		// No notes on fretboard == variations size 1 == variation.count of 0
		List<LocationList> emptyVariations = standard.getEnharmonicVariations(
			new NoteList(Note.plus(Note.GuitarHighE,100)));
		// System.out.println( "One note empty List<LocationList> string=" + emptyVariations.toString()); // List prints "[]"
		// System.out.println( "Empty variations size=" + emptyVariations.size());
		assertTrue("Empty variation size", 1 == emptyVariations.size());
		int numVariations = Fretboard.getPermutationCount(emptyVariations);
		// System.out.println( "Empty variations count=" + numVariations );
		assertTrue("Empty variation count", 0 == numVariations);

		// No notes on fretboard (100 below and 100 above) == variations size 1 == variation.count of 0
		List<LocationList> noneOnFretboardVariations = standard.getEnharmonicVariations(
			new NoteList(Note.minus(Note.GuitarLowE, -100),   Note.plus(Note.GuitarHighE,100)));
		// System.out.println( "Two note empty List<LocationList> string=" + noneOnFretboardVariations.toString()); // List prints ugly "[, ]"
		// System.out.println( "None variations size=" + noneOnFretboardVariations.size());		
		assertTrue("None variation size", 2 == noneOnFretboardVariations.size());
		int numNoneVariations = Fretboard.getPermutationCount(noneOnFretboardVariations);
		System.out.println( "None variations count=" + numNoneVariations );
		assertTrue("None variation count", 0 == numNoneVariations);

		
		
		// Expected variations = 4 * x * 1 * 2 = 8
		NoteList noteSet = new NoteList(Note.GuitarG, // 4 locations,
				Note.plus(Note.GuitarHighE, 1001), // 0 locations
				Note.GuitarLowE, // 1 location
				Note.GuitarA // 2 locations
		);

		List<LocationList> variations = standard.getEnharmonicVariations(noteSet);
		// System.out.println( "NoteList=" + noteSet + ", variations=" +
		// variations );
		assertTrue("NoteList variation size", 4 == variations.size());
		numVariations = Fretboard.getPermutationCount(variations);
		// System.out.println( "NoteList=" + noteSet + ", variation count=" +
		// numVariations );
		assertTrue("NoteList variation count", 8 == numVariations);

		// Test all variations of NoteList.
		// for ( int i = 0; i < numVariations; i++ )
		// System.out.println( "Variation " + i + ", locations=" +
		// FilenameRegExFilter.getVariation( variations, i ));
		LocationList expected = new LocationList(new Location(0, 0), new Location(0, 5), new Location(0, 15));
		assertEquals("Variations 0", expected, Fretboard.getPermutation(variations, 0));
		expected = new LocationList(new Location(0, 0), new Location(0, 5), new Location(1, 10));
		assertEquals("Variations 1", expected, Fretboard.getPermutation(variations, 1));
		expected = new LocationList(new Location(0, 0), new Location(0, 5), new Location(2, 5));
		assertEquals("Variations 2", expected, Fretboard.getPermutation(variations, 2));
		expected = new LocationList(new Location(0, 0), new Location(1, 0), new Location(3, 0));
		assertEquals("Variations 7", expected, Fretboard.getPermutation(variations, 7));

		// Test with limited location frets.
		variations = standard.getVariations(noteSet, 0, 10, Fretboard.ENHARMONICS);
		// System.out.println( "NoteList=" + noteSet + ", variations=" +
		// variations );
		assertTrue("NoteList variation size", 4 == variations.size());
		numVariations = Fretboard.getPermutationCount(variations);
		// System.out.println( "NoteList=" + noteSet + ", variation count=" +
		// numVariations );
		assertTrue("NoteList variation count", 4 == numVariations);

		// Test all variations of NoteList.
		// for ( int i = 0; i < numVariations; i++ )
		// System.out.println( "Variation " + i + ", locations=" +
		// FilenameRegExFilter.getVariation( variations, i ));
		expected = new LocationList(new Location(0, 0), new Location(0, 5), new Location(2, 5));
		assertEquals("Variations 2-0", expected, Fretboard.getPermutation(variations, 0));
		expected = new LocationList(new Location(0, 0), new Location(0, 5), new Location(3, 0));
		assertEquals("Variations 2-1", expected, Fretboard.getPermutation(variations, 1));
		expected = new LocationList(new Location(0, 0), new Location(1, 0), new Location(2, 5));
		assertEquals("Variations 2-2", expected, Fretboard.getPermutation(variations, 2));
		expected = new LocationList(new Location(0, 0), new Location(1, 0), new Location(3, 0));
		assertEquals("Variations 2-3", expected, Fretboard.getPermutation(variations, 3));

		// Expected variations = 4
		noteSet = new NoteList(Note.GuitarG);
		// Test with limited location frets.
		variations = standard.getVariations(noteSet, 0, 18, Fretboard.ENHARMONICS);
		// System.out.println( "NoteList=" + noteSet + ", variations=" +
		// variations );
		assertTrue("NoteList variation size", 1 == variations.size());
		numVariations = Fretboard.getPermutationCount(variations);
		// System.out.println( "NoteList=" + noteSet + ", variation count=" +
		// numVariations );
		assertTrue("NoteList variation count", 4 == numVariations);

	}

	@Test
	public void testVariationStrings() {
		NoteList noteSet = new NoteList(Note.GuitarG, // 4 locations,
				Note.plus(Note.GuitarG, Interval.fourth), Note.plus(Note.GuitarG, Interval.fifth));

		List<LocationList> variations = standard.getEnharmonicVariations(noteSet);
		System.out.println("NoteList=" + noteSet + ", variations=" + variations);
		int digits = variations.size(); // 4 * 4 * 4
		assertEquals("NoteList variation digits", 3, digits);
		int numVariations = Fretboard.getPermutationCount(variations);
		// System.out.println( "NoteList=" + noteSet + ", variation count=" +
		// numVariations );
		assertEquals("NoteList variation count", 64, numVariations);

		// System.out.println( "NoteListTest null perm string=\"" +
		// Fretboard.getPermutationString( null, 0 ) + "\".");
		assertEquals("NoteList variation null", "ø", Fretboard.getPermutationString(null, 0));
		assertEquals("NoteList variation 0", "0/64 (000/444)", Fretboard.getPermutationString(variations, 0));
		// Need to reverse order here. Right now this is coming out backwards.
		assertEquals("NoteList variation 1", "1/64 (001/444)", Fretboard.getPermutationString(variations, 1));
		assertEquals("NoteList variation 15", "15/64 (033/444)", Fretboard.getPermutationString(variations, 15));
		// assertEquals("NoteList variation 16", "16/64 (100)",
		// Fretboard.getPermutationString( variations, 16 ));
		// assertEquals("NoteList variation 62", "62/64 (332)",
		// Fretboard.getPermutationString( variations, 62 ));
		assertEquals("NoteList variation 63", "63/64 (333/444)", Fretboard.getPermutationString(variations, 63));
	}

	@Test
	public void testVariationStringParsing() {
		assertNull("Empty", Fretboard.getPermutationValues(null));

		assertNull("One", Fretboard.getPermutationValues("1"));

		assertNull("Delims", Fretboard.getPermutationValues("/ ()"));

		int[] two = Fretboard.getPermutationValues("8/100");
		assertTrue("Two", 3 == two.length);
		assertArrayEquals("Two values", new int[] { 8, 100, 0 }, two);

		int[] legit = Fretboard.getPermutationValues("15/64 (123/456)");
		assertTrue("Legit", 9 == legit.length);
		assertArrayEquals("Legit values", new int[] { 15, 64, 3, 1, 4, 2, 5, 3, 6 }, legit);

		int[] doubleit = Fretboard.getPermutationValues("15/64 (123/456) 10/20 (345/678)");
		assertNull("Doubleit", doubleit);
	}

	@Test
	public void testVariationsWithOctaves() {
		// Expected variations = 4 * x * 1 * 2 = 8
		NoteList noteSet = new NoteList(Note.GuitarD // 4 locations,
		);

		List<LocationList> variations = standard.getEnharmonicVariations(noteSet);
		int numVariations = Fretboard.getPermutationCount(variations);
		System.out.println("NoteList=" + noteSet + ", variations=" + numVariations);
		System.out.println("NoteList=" + noteSet + ", variations=" + variations);
		assertEquals("NoteList variation count", 3, numVariations);

		List<LocationList> variationsWithOctaves = standard.getOctaveVariations(noteSet);
		numVariations = Fretboard.getPermutationCount(variationsWithOctaves);
		System.out.println("NoteList=" + noteSet + ", variation count=" + numVariations);
		System.out.println("NoteList=" + noteSet + ", variations=" + variationsWithOctaves);
		assertEquals("NoteList octave variation count", 9, numVariations);

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
		// System.out.println( standard.toString( cRootsPos1, 3, 6, displayOpts
		// ) );
		assertEquals("FilenameRegExFilter vertical righty named compact not played fretnum, open string displayed", c2
				+ nl + space + nl + c3 + nl, standard.toString(cLocations, 3, 6, displayOpts));

		displayOpts.openStringDisplay = false; // no effect since
		assertEquals("FilenameRegExFilter vertical righty named compact not played fretnum, open string not displayed",
				c2 + nl + space + nl + c3 + nl, standard.toString(cLocations, 3, 6, displayOpts));
	}

	@Test
	/** How to specify note/enharmonic variations that appear on a fretboard. */
	public void testtoStringVariations() {
		// Appears on 6/15, 5/10, 4/5, and 3/0.
		NoteList g3 = new NoteList(Note.GuitarG);
		List<LocationList> g3Variations = standard.getEnharmonicVariations(g3);
		LocationList g3Locations = Fretboard.getPermutation(g3Variations, 0);

		// List<Location> locations = standard.getLocations( Note.GuitarG, 0, 18
		// );
		// System.out.println( "G, string locations " + locations.size() +
		// " locations: " + locations );
		// List<LocationList> variations = standard.getVariations( g3, 0, 18 );
		// System.out.println( "G, string variations " +
		// FilenameRegExFilter.getVariationCount(variations) + " variations: " +
		// variations );
		// int variationCount =
		// FilenameRegExFilter.getVariationCount(variations);
		// List<Location> variation0 =
		// FilenameRegExFilter.getVariation(variations, 0);
		// System.out.println( "G variation 0: " + standard.toString(
		// variation0, 0, 18, displayOpts ) );

		Display displayOpts = new Display();
		displayOpts.infoType = Display.InfoType.NAME;
		displayOpts.hand = Hand.RIGHT;
		displayOpts.orientation = Orientation.HORIZONTAL;
		displayOpts.nutString = "|";
		displayOpts.fretString = "";
		displayOpts.stringString = "-";
		displayOpts.fretSpace = 1;
		displayOpts.notPlayed = EnumSet.of(Display.NotPlayedLocation.HEAD, Display.NotPlayedLocation.FIRST);
		displayOpts.notPlayedString = "x";
		displayOpts.fretNumbering = EnumSet.of(Display.FretNumbering.FIRSTRIGHT);
		displayOpts.fretNumberingDisplayOpen = true;
		displayOpts.openStringDisplay = true;

		String fret = " 0";
		String empty = "x-|----------------------------------";
		String gs6 = "--|----------------------------G-----";

		// System.out.println( standard.toString( g3, 0, 18, displayOpts ) );
		assertEquals("FilenameRegExFilter vertical righty named compact not played fretnum", fret + nl + empty + nl
				+ empty + nl + empty + nl + empty + nl + empty + nl + gs6,
				standard.toString(g3Locations, 0, 18, displayOpts));
	}

	@Test
	public void testPropertiesDefault() {
		String shortName = "fretboard.guitar.properties";
		Fretboard test = Fretboard.getInstanceFromFileName(shortName);

		// Test meta data
		assertEquals("Name", "Guitar, Standard", test.getMetaName());
		assertEquals("Description", "Guitar, Standard, E-A-D-G-B-E", test.getMetaDescription());
		assertEquals("Location", shortName, test.getMetaLocation());

		GuitarString first = test.getString(0);
		int maxFret = first.getMaxFret();
		int octaveFret = first.getOctaveFret();
		// for ( int i = 0; i < test.size(); i++ )
		// System.out.println( "String " + i + ": " + test.getString( i ));
		assertEquals("Tuning 6", new GuitarString(Note.GuitarLowE, octaveFret, maxFret), test.getString(0));
		assertEquals("Tuning 5", new GuitarString(Note.GuitarA, octaveFret, maxFret), test.getString(1));
		assertEquals("Tuning 4", new GuitarString(Note.GuitarD, octaveFret, maxFret), test.getString(2));
		assertEquals("Tuning 3", new GuitarString(Note.GuitarG, octaveFret, maxFret), test.getString(3));
		assertEquals("Tuning 2", new GuitarString(Note.GuitarB, octaveFret, maxFret), test.getString(4));
		assertEquals("Tuning 1", new GuitarString(Note.GuitarHighE, octaveFret, maxFret), test.getString(5));
	}

	@Test
	public void testPropertiesFromPath() {
		try {
			String location ="fretboard.guitar.properties";
			Fretboard instance = Fretboard.getInstanceFromFileName(location);
			String filter = "fretboard..*.properties";
			List<Fretboard> fretboards = instance.readFromPath(Fretboard.PROP_PATH, filter);

			assertEquals("FilenameRegExFilter prop file count", 19L, fretboards.size());

			System.out.println("Available fretboards:");
			for (Fretboard test : fretboards) {
				System.out.println("   " + test.getMetaName());
				// // Test meta data
				// assertEquals("Short name", "Standard", test.getShortName());
				// assertEquals("Long name", "Standard E-A-D-G-B-E",
				// test.getLongName());
				// assertEquals("Location", path, test.getLocation());
			}

			// The properties map is cached. 
			// filter = "fretboard.guitar*.properties";
			// fretboards = instance.readFromPath(path, filter);
			// assertEquals("FilenameRegExFilter prop file count", 7L, fretboards.size());
		} catch (IOException e) {
			assertNull("IOException=" + e, e);
		}
	}

	@Test
	public void testProperties() {
		String location = "fretboard.guitar.properties";
		Fretboard instance = Fretboard.getInstanceFromFileName(location);
		
		Fretboard test = instance.getInstance(Fretboard.STANDARD);
		assertEquals("Name", "Guitar, Standard", test.getMetaName());
		assertEquals("Description", "Guitar, Standard, E-A-D-G-B-E", test.getMetaDescription());

		test = instance.getInstance(Fretboard.OPEN_D);
		assertEquals("Name", "Guitar, Open D", test.getMetaName());
		assertEquals("Description", "Guitar, Open D, D-A-D-F#-A-D", test.getMetaDescription());

		test = instance.getInstance(Fretboard.OPEN_G);
		assertEquals("Name", "Guitar, Open G", test.getMetaName());
		assertEquals("Description", "Guitar, Open G, D-G-D-G-B-D", test.getMetaDescription());
	}

	@Test
	public void testGetResourceListing() {
		try {
			String[] gNames = Fretboard.getResourceListing(Fretboard.PROP_PATH, "fretboard[.]guitar.*[.]properties");
			assertEquals("Name", 7, gNames.length);
			String[] bassNames = Fretboard.getResourceListing(Fretboard.PROP_PATH, "fretboard[.]bass.*[.]properties");
			assertEquals("Name", 3, bassNames.length);
			String[] bariNames = Fretboard.getResourceListing(Fretboard.PROP_PATH, "fretboard[.]bari.*[.]properties");
			assertEquals("Name", 3, bariNames.length);
			String[] ukeNames = Fretboard.getResourceListing(Fretboard.PROP_PATH, "fretboard[.]uke.*[.]properties");
			assertEquals("Name", 6, ukeNames.length);
		} catch (Exception e) {
			assertNull("Exception=" + e, e);
		}

	}
}