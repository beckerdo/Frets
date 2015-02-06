package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import frets.main.Display.Hand;
import frets.main.Display.NotPlayedLocation;
import frets.main.Display.FretNumbering;
import frets.main.Display.Orientation;
import frets.main.Display.VAlign;
import static frets.main.Display.NL;

/**
 * Unit tests to validate this class.
 * 
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class DisplayTest {
	@Before
	public void setup() {
	}

	@Test
	public void testDisplayPad() {
		// public static String pad( String value, int noteSpace, int space,
		// VAlign vAlign, Hand hand,
		// Orientation orient, String stringString, String stringSpace )
		// Horizontal Right hand
		assertEquals("Pad BOTTOM RIGHT HORI", "   X",
				Display.pad("X", 1, 4, VAlign.BOTTOM, Hand.RIGHT, Orientation.HORIZONTAL, "", " "));
		assertEquals("Pad TOP RIGHT HORI", "X   ",
				Display.pad("X", 1, 4, VAlign.TOP, Hand.RIGHT, Orientation.HORIZONTAL, "", " "));
		assertEquals("Pad BOTTOM RIGHT HORI STRING", "---X",
				Display.pad("X", 1, 4, VAlign.BOTTOM, Hand.RIGHT, Orientation.HORIZONTAL, "-", " "));
		assertEquals("Pad TOP RIGHT HORI STRING", "X---",
				Display.pad("X", 1, 4, VAlign.TOP, Hand.RIGHT, Orientation.HORIZONTAL, "-", " "));
		// Horizontal Left hand
		assertEquals("Pad BOTTOM LEFT HORI", "X  ",
				Display.pad("X", 1, 3, VAlign.BOTTOM, Hand.LEFT, Orientation.HORIZONTAL, "", " "));
		assertEquals("Pad TOP LEFT HORI", "  X",
				Display.pad("X", 1, 3, VAlign.TOP, Hand.LEFT, Orientation.HORIZONTAL, "", " "));
		assertEquals("Pad BOTTOM LEFT HORI STRING", "X--",
				Display.pad("X", 1, 3, VAlign.BOTTOM, Hand.LEFT, Orientation.HORIZONTAL, "-", " "));
		assertEquals("Pad TOP LEFT HORI STRING", "--X",
				Display.pad("X", 1, 3, VAlign.TOP, Hand.LEFT, Orientation.HORIZONTAL, "-", " "));
		// Vertical Right hand
		assertEquals("Pad BOTTOM RIGHT VERT", " " + NL + " " + NL + "X" + NL,
				Display.pad("X", 1, 3, VAlign.BOTTOM, Hand.RIGHT, Orientation.VERTICAL, "", " "));
		assertEquals("Pad TOP RIGHT VERT", "X " + NL + "  " + NL + "  " + NL,
				Display.pad("X", 2, 3, VAlign.TOP, Hand.RIGHT, Orientation.VERTICAL, "", " "));
		assertEquals("Pad BOTTOM RIGHT VERT STRING", "|" + NL + "X" + NL,
				Display.pad("X", 1, 2, VAlign.BOTTOM, Hand.RIGHT, Orientation.VERTICAL, "|", " "));
		assertEquals("Pad TOP RIGHT VERT STRING", "X " + NL + "| " + NL,
				Display.pad("X", 2, 2, VAlign.TOP, Hand.RIGHT, Orientation.VERTICAL, "|", " "));
		// Vertical LEFT hand
		assertEquals("Pad BOTTOM RIGHT VERT", " " + NL + " " + NL + "X" + NL,
				Display.pad("X", 1, 3, VAlign.BOTTOM, Hand.LEFT, Orientation.VERTICAL, "", " "));
		assertEquals("Pad TOP RIGHT VERT", "X " + NL + "  " + NL + "  " + NL,
				Display.pad("X", 2, 3, VAlign.TOP, Hand.LEFT, Orientation.VERTICAL, "", " "));
		assertEquals("Pad BOTTOM RIGHT VERT STRING", "|" + NL + "X" + NL,
				Display.pad("X", 1, 2, VAlign.BOTTOM, Hand.LEFT, Orientation.VERTICAL, "|", " "));
		assertEquals("Pad TOP RIGHT VERT STRING", "X " + NL + "| " + NL,
				Display.pad("X", 2, 2, VAlign.TOP, Hand.LEFT, Orientation.VERTICAL, "|", " "));
	}

	@Test
	public void testDisplayPropertiesDefault() {
		Display test = new Display();
		try {
			// test.populateFromFile( "displayDefault.properties" );
			// test.populateFromFile( "frets/main/displayDefault.properties" );
			// test.populateFromFile(
			// "classpath:frets/main/displayDefault.properties" );
			// test.populateFromFile(
			// "file:frets/main/displayDefault.properties" );
			test.populateFromFile("src/main/resources/frets/main/display.horizontal.properties");

			// assertEquals("Display hand", Display.Hand.RIGHT, test.hand );
			assertTrue("Display hand", Display.Hand.RIGHT == test.hand);
			assertTrue("Display note", Display.InfoType.NAME == test.infoType);
			assertEquals("Display string string", "-", test.stringString);
			assertEquals("Display fret string", "|", test.fretString);
			assertEquals("Display nut string", "||", test.nutString);

			Set<NotPlayedLocation> notPlayedLocation = EnumSet.of(Display.NotPlayedLocation.HEAD);
			assertEquals("Display not played set", notPlayedLocation, test.notPlayed);
			assertEquals("Display not played string", "X", test.notPlayedString);
			Set<FretNumbering> fretNumbering = EnumSet.of(Display.FretNumbering.FIRSTLEFT,
					Display.FretNumbering.FIRSTRIGHT);
			assertEquals("Display fret numbering set", fretNumbering, test.fretNumbering);
			assertTrue("Display head space", 1 == test.headSpace);
			assertTrue("Display head align", test.headAlign == VAlign.BOTTOM);
			assertTrue("Display fret space", 1 == test.fretSpace);
			assertTrue("Display fret align", test.fretAlign == VAlign.BOTTOM);

		} catch (IOException e) {
			assertNull("IOException=" + e, e);
		}
	}

	@Test
	public void testClone() {
		Display one = new Display();
		Display oneClone = new Display(one);
		assertTrue("Display clone of default", oneClone.equals(one));

		Display other = new Display();
		other.spaceString = "12345678";
		Display otherClone = new Display(other);
		assertTrue("Display clone of other", otherClone.equals(other));

		assertTrue("Display originals", !other.equals(one));
		assertTrue("Display clones", !otherClone.equals(oneClone));
		assertTrue("Display transitive", !otherClone.equals(one));
	}

	@Test
	public void testObjectOverrides() {
		Display one = new Display();
		Display oneClone = new Display(one);
		Display oneReference = one;
		Display other = new Display();
		other.spaceString = "12345678";
		Display otherReference = other;

		// Hash codes
		assertTrue("Display reference hashCode", oneReference.hashCode() == one.hashCode());
		assertTrue("Display clone hashCode", oneClone.hashCode() == one.hashCode());
		assertTrue("Display other hashCode", one.hashCode() != other.hashCode());
		assertTrue("Display other reference hashCode", oneReference.hashCode() != otherReference.hashCode());

		// Equals
		assertTrue("Display reference equals", oneReference.equals(one));
		assertTrue("Display clone equals", oneClone.equals(one));
		assertTrue("Display other equals", !one.equals(other));
		assertTrue("Display other reference equals", !oneReference.equals(otherReference));

		// CompareTo
		assertTrue("Display reference compareTo", 0 == oneReference.compareTo(one));
		assertTrue("Display clone compareTo", 0 == oneClone.compareTo(one));
		assertTrue("Display other compareTo", 0 != one.compareTo(other));
		assertTrue("Display other reference compareTo", 0 != oneReference.compareTo(otherReference));

		// String
		String oneString = one.toString();
		assertTrue("String null", oneString != null);
		assertTrue("String length", oneString.length() > 0);
		assertTrue("String begins", oneString.startsWith("Display"));
		assertTrue("String compare", oneString.equals(oneClone.toString()));
		System.out.println("DisplayTest example=" + one.toString());
	}

	@Test
	public void testDisplayApertures() {
		Display test = new Display();

		assertTrue("String getter null", 0 == test.getDisplayAreaStringAperture());
		assertTrue("Fret getter null", 0 == test.getDisplayAreaFretAperture());

		Fretboard fretboard = Fretboard.instance.getInstance(Fretboard.STANDARD); // max
																					// fret																					// 18
		LocationList locationsLow = LocationList.parseString("1-2,2-5,3-10");
		LocationList locationsHigh = LocationList.parseString("1-16,2-18,3-20");

		// Min style
		int aperture = 5;
		test.setDisplayAreaStyleMinAperture(fretboard, locationsLow, aperture);
		assertEquals("Fret span less than location span", aperture, test.getDisplayAreaFretAperture());
		assertEquals("Fret span min fret", locationsLow.minFret(), test.displayAreaMin.getFret());
		test.setDisplayAreaStyleMinAperture(fretboard, locationsHigh, aperture);
		assertEquals("Fret span greater than fretboard", aperture, test.getDisplayAreaFretAperture());
		// assertEquals("Fret span min fret", fretboard.getMaxFret() - 5,
		// test.displayAreaMin.getFret() );

		// Max style
		test.setDisplayAreaStyleMaxAperture(fretboard, locationsLow, aperture);
		assertEquals("Fret span less than location span", aperture, test.getDisplayAreaFretAperture());
		assertEquals("Fret span max fret", locationsLow.maxFret(), test.displayAreaMax.getFret());
		aperture = 12;
		test.setDisplayAreaStyleMaxAperture(fretboard, locationsLow, aperture);
		assertEquals("Fret span greater than fretboard", 12, test.getDisplayAreaFretAperture());
		// assertEquals("Fret span min fret", 0, test.displayAreaMin.getFret()
		// );

		// MaxLocation
		test.setDisplayAreaStyleMaxLocation(fretboard, locationsLow);
		assertEquals("Fret span max location", locationsLow.maxFret() - locationsLow.minFret(),
				test.getDisplayAreaFretAperture());
		assertEquals("Fret span min of max location", locationsLow.minFret(), test.displayAreaMin.getFret());

		// MaxFretboard
		test.setDisplayAreaStyleMaxFretboard(fretboard);
		assertEquals("Fret span max location", fretboard.getMaxFret(), test.getDisplayAreaFretAperture());
		assertEquals("Fret span min of max location", 0, test.displayAreaMin.getFret());
	}

	@Test
	public void testDisplayVariations() {
		Display test = new Display();
		test.showEnharmonicVariations = true;
		assertEquals("Enharmonic variations", true, test.showEnharmonicVariations);
		test.showOctaveVariations = true;
		assertEquals("Octave variations", true, test.showOctaveVariations);

		assertNotNull("Enharmonic alpha", test.enharmonicAlpha);
		assertNotNull("Octaves alpha", test.octavesAlpha);
	}
}