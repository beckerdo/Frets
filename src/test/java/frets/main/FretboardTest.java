package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.EnumSet;
import java.util.List;

import static frets.main.Display.Orientation;
import static frets.main.Display.Hand;

/**
 * Unit tests to validate this class.
 * Tests fretboard features.
 * For character graphics tests, see FretboardCharGraphicTest.
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

	/**
	 * Note that the variation LocationList of any List position can be null or empty.
	 * This often happen when one Fretboard with high and low notes gets moved to a smaller fretboard.
	 * Thus you may see a List<LocationList> String that looks like "[]", "[, ]", or "[, , ]".  
	 */
	@Test
	public void testEmptyVariations() {
		// No notes == no variations
		List<LocationList> noVariations = standard.getEnharmonicVariations(null);
		assertTrue("No variations", null == noVariations);

		// No notes on fretboard == variations size 1 == variation.count of 0
		List<LocationList> emptyVariations = standard.getEnharmonicVariations(
			new NoteList(Note.plus(Note.GuitarHighE,100)));
		// System.out.println( "One note empty List<LocationList> string=" + emptyVariations.toString()); // List prints "[]"
		// System.out.println( "Empty variations size=" + emptyVariations.size());
		assertTrue("Empty variation size", 1 == emptyVariations.size());
		long numVariations = Fretboard.getPermutationCount(emptyVariations);
		// System.out.println( "Empty variations count=" + numVariations );
		assertTrue("Empty variation count", 0 == numVariations);

		// No notes on fretboard (100 below and 100 above) == variations size 2 == variation.count of 0
		List<LocationList> noneOnFretboardVariations = standard.getEnharmonicVariations(
			new NoteList(Note.minus(Note.GuitarLowE, -100), Note.plus(Note.GuitarHighE,100)));
		// System.out.println( "Two note empty List<LocationList> string=" + noneOnFretboardVariations.toString()); // List prints ugly "[, ]"
		// System.out.println( "None variations size=" + noneOnFretboardVariations.size());		
		assertTrue("None variation size", 2 == noneOnFretboardVariations.size());
		long numNoneVariations = Fretboard.getPermutationCount(noneOnFretboardVariations);
		System.out.println( "None variations count=" + numNoneVariations );
		assertTrue("None variation count", 0 == numNoneVariations);

		// No notes on fretboard (100 below and 100 above) == variations size 3 == variation.count of 0
		List<LocationList> noneOnFretboardVariations2 = standard.getEnharmonicVariations(
			new NoteList(Note.minus(Note.GuitarLowE, -100), Note.plus(Note.GuitarHighE,100), Note.plus(Note.GuitarHighE,1000)));
		// System.out.println( "Two note empty List<LocationList> string=" + noneOnFretboardVariations.toString()); // List prints ugly "[, ]"
		// System.out.println( "None variations size=" + noneOnFretboardVariations.size());		
		assertTrue("None variation size 2", 3 == noneOnFretboardVariations2.size());
		numNoneVariations = Fretboard.getPermutationCount(noneOnFretboardVariations2);
		System.out.println( "None variations count=" + numNoneVariations );
		assertTrue("None variation count 2", 0 == numNoneVariations);

	}

	/**
	 * Note that the variation LocationList of any List position can be null or empty.
	 * This often happen when one Fretboard with high and low notes gets moved to a smaller fretboard.
	 * Thus you may see a List<LocationList> String that looks like "[]", "[, ]", or "[, , ]".  
	 */
	@Test
	public void testPermutations() {
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
		long numVariations = Fretboard.getPermutationCount(variations);
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
	public void testPermutationNumber() {
		NoteList noteSet = new NoteList(Note.GuitarG, // 4 locations,
				Note.plus(Note.GuitarG, Interval.fourth), Note.plus(Note.GuitarG, Interval.fifth));

		// Test some happy paths
		List<LocationList> variations = standard.getEnharmonicVariations(noteSet);
		System.out.println("NoteList=" + noteSet + ", variations=" + variations);
		int digits = variations.size(); // 4 * 5 * 5
		assertEquals("NoteList variation digits", 3, digits);
		long permutations = Fretboard.getPermutationCount(variations);
		System.out.println( "NoteList=" + noteSet + ", variation count=" + permutations );
		assertEquals("NoteList variation count", 100, permutations);
		
		for ( long permi = 0; permi < permutations; permi++ ) {
			LocationList locations = Fretboard.getPermutation( variations, permi );
			// System.out.println( "LocationList variation " + permi + "=" + locations );
			assertEquals("LocationList variation " + permi, permi, Fretboard.getPermutationNumber( variations, locations ) );
		}

		// Test some nulls
		assertEquals("Null variations", -1 , Fretboard.getPermutationNumber( null, Fretboard.getPermutation( variations, 0 ) ) );
		assertEquals("Null variations", -1 , Fretboard.getPermutationNumber( variations, null ) );		
	}
	
	/**
	 * Test large sets of note/permutations to see that things don't blow up.
	 * As data, this uses the C major scale, the shape 1E, which ranges from
	 * B3 to D5.
	 */
	@Test
	public void testPermutationLongs() {
		NoteList notes = NoteList.parse(
			"C3,D3,E3,F3,G3,A4,B4," +
			"C4,D4,E4,F4,G4,A5,B5," +
			"C5"
		);
		
		// System.out.println( "CMaj first note=" + notes.get( 0 ) + ",last note=" + notes.get(notes.size()-1) + ",length=" + notes.size());
        List<LocationList> variations = standard.getEnharmonicVariations( notes );
	    long permutations = Fretboard.getPermutationCount( variations );
        // System.out.println( "FretboardTest.testLargePermutations variations=" + variations.size() + ", permutations=" + permutations);

        long expectedPermutations = 4 * 4 * 4 * 
        		5 * 5 * 5 * 5 * 5 * 5 *
        		4 * 4 * 3 * 3 * 3 * 2;
		// System.out.println("NoteList=" + notes + "\n   variations=" + variations);        
		assertEquals("Permutation count 15", expectedPermutations, permutations);
		
		notes.add( 0, Note.parse( "B3" ));
		notes.add( Note.parse( "D5" ));

		System.out.println( "CMaj first note=" + notes.get( 0 ) + ",last note=" + notes.get(notes.size()-1) + ",length=" + notes.size());
        variations = standard.getEnharmonicVariations( notes );
	    permutations = Fretboard.getPermutationCount( variations );
        System.out.println( "FretboardTest.testLargePermutations variations=" + variations.size() + ", permutations=" + permutations);
		// System.out.println("NoteList=" + notes + "\n   variations=" + variations);
		expectedPermutations *= 2 * 3;
		assertEquals("Permutation count 17", expectedPermutations, permutations);
		assertTrue("Testing long territory", permutations > Integer.MAX_VALUE );
		
		// Get first and last permutations
//		LocationList firstPerm = Fretboard.getPermutation( variations, 0 );
//		System.out.println( "First permutation=" + firstPerm);
//		LocationList penuPerm = Fretboard.getPermutation( variations, permutations - 2 );
//		System.out.println( "Penu permutation=" + penuPerm);
		LocationList lastPerm = Fretboard.getPermutation( variations, permutations - 1 );
		System.out.println( "Last permutation=" + lastPerm);

		System.out.println( "Last permutation string=" + Fretboard.getPermutationString(variations, permutations - 1));		
		// assertEquals("NoteList variation n - 1", "99/100 (344/455)", Fretboard.getPermutationString(variations, permutations - 1));
	}
	
	/**
	 * Note that the variation LocationList of any List position can be null or empty.
	 * This often happen when one Fretboard with high and low notes gets moved to a smaller fretboard.
	 * Thus you may see a List<LocationList> String that looks like "[]", "[, ]", or "[, , ]".  
	 */
	@Test
	public void testPermutationStrings() {
		NoteList noteSet = new NoteList(Note.GuitarG, // 4 locations,
				Note.plus(Note.GuitarG, Interval.fourth), Note.plus(Note.GuitarG, Interval.fifth));

		List<LocationList> variations = standard.getEnharmonicVariations(noteSet);
		System.out.println("NoteList=" + noteSet + ", variations=" + variations);
		int digits = variations.size(); // 4 * 5 * 5
		assertEquals("NoteList variation digits", 3, digits);
		long permutations = Fretboard.getPermutationCount(variations);
		System.out.println( "NoteList=" + noteSet + ", variation count=" + permutations );
		assertEquals("NoteList variation count", 100, permutations);

		// System.out.println( "NoteListTest null perm string=\"" +
		// Fretboard.getPermutationString( null, 0 ) + "\".");
		assertEquals("NoteList variation null", "ø", Fretboard.getPermutationString(null, 0));
		assertEquals("NoteList variation 0", "0/100 (000/455)", Fretboard.getPermutationString(variations, 0));
		// Need to reverse order here. Right now this is coming out backwards.
		assertEquals("NoteList variation 1", "1/100 (001/455)", Fretboard.getPermutationString(variations, 1));
		assertEquals("NoteList variation 15", "15/100 (030/455)", Fretboard.getPermutationString(variations, 15));
		// assertEquals("NoteList variation 16", "16/64 (100)",
		// Fretboard.getPermutationString( variations, 16 ));
		// assertEquals("NoteList variation 62", "62/64 (332)",
		// Fretboard.getPermutationString( variations, 62 ));
		assertEquals("NoteList variation 63", "63/100 (223/455)", Fretboard.getPermutationString(variations, 63));
		assertEquals("NoteList variation 99", "99/100 (344/455)", Fretboard.getPermutationString(variations, permutations - 1));

		// Get first and last permutations
		// LocationList firstPerm = Fretboard.getPermutation( variations, 0 );
		// System.out.println( "First permutation=" + firstPerm);
		// LocationList penuPerm = Fretboard.getPermutation( variations, permutations - 2 );
		// System.out.println( "Penu permutation=" + penuPerm);
		// LocationList lastPerm = Fretboard.getPermutation( variations, permutations - 1 );
		// System.out.println( "Last permutation=" + lastPerm);
	}

	@Test
	public void testPermutationStringParsing() {
		assertNull("Empty", Fretboard.getPermutationValues(null));

		assertNull("One", Fretboard.getPermutationValues("1"));

		assertNull("Delims", Fretboard.getPermutationValues("/ ()"));

		long[] two = Fretboard.getPermutationValues("8/100");
		assertTrue("Two", 3 == two.length);
		assertArrayEquals("Two values", new long[] { 8, 100, 0 }, two);

		long[] legit = Fretboard.getPermutationValues("15/64 (123/456)");
		assertTrue("Legit", 9 == legit.length);
		assertArrayEquals("Legit values", new long[] { 15, 64, 3, 1, 4, 2, 5, 3, 6 }, legit);

		long[] doubleit = Fretboard.getPermutationValues("15/64 (123/456) 10/20 (345/678)");
		assertNull("Doubleit", doubleit);
	}

	@Test
	public void testPermutationsWithOctaves() {
		// Expected variations = 4 * x * 1 * 2 = 8
		NoteList noteSet = new NoteList(Note.GuitarD // 4 locations,
		);

		List<LocationList> variations = standard.getEnharmonicVariations(noteSet);
		long numVariations = Fretboard.getPermutationCount(variations);
		System.out.println("NoteList=" + noteSet + ", variations=" + numVariations);
		System.out.println("NoteList=" + noteSet + ", variations=" + variations);
		assertEquals("NoteList variation count", 3, numVariations);

		List<LocationList> variationsWithOctaves = standard.getOctaveVariations(noteSet);
		numVariations = Fretboard.getPermutationCount(variationsWithOctaves);
		System.out.println("NoteList=" + noteSet + ", variation count=" + numVariations);
		System.out.println("NoteList=" + noteSet + ", variations=" + variationsWithOctaves);
		assertEquals("NoteList octave variation count", 12, numVariations);
	}

	@Test
	/** How to specify note/enharmonic variations that appear on a fretboard. */
	public void testToStrings() {
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
	
	@Test
	public void testSort() {
		Fretboard bari = new Fretboard(
				new GuitarString( Note.parse( "A3" )), 
				new GuitarString( Note.parse( "F#3" )), 
				new GuitarString( Note.parse( "B4" )), 
				new GuitarString( Note.parse( "D3" )), 
				new GuitarString( Note.parse( "B2" )), 
				new GuitarString( Note.parse( "E2" )));
		System.out.println( "Unsorted baritone strings=" + bari);
		bari.sortStrings();
		GuitarString prev = bari.getString( 0 );
		for ( int i = 1; i < bari.getStringCount(); i++ ) {
			GuitarString curr = bari.getString(i);
			assertTrue( "Sort string compare " + i, prev.compareTo( curr ) < 0 );			
		}
		System.out.println( "Sorted baritone strings=" + bari);
	}

	@Test
	public void testUkelele() {
		// Test that reentrant (out of order) strings are preserved.	
		String shortName = "fretboard.ukeleleSoprano.properties";
		Fretboard uke = Fretboard.getInstanceFromFileName(shortName);

		// This will test that the crazy order of ukelele strings is in order.
		Fretboard expected= new Fretboard(
				new GuitarString( Note.parse( "G4" ), 12, 18), 
				new GuitarString( Note.parse( "C4" ), 12, 18), 
				new GuitarString( Note.parse( "E4" ), 12, 18), 
				new GuitarString( Note.parse( "A4" ), 12, 18));
		
		for ( int i = 0; i < expected.getStringCount(); i++ ) {
			GuitarString eString = expected.getString(i);
			assertEquals( "Ukelele string " + i, eString, uke.getString( i ) );			
		}
	}
}