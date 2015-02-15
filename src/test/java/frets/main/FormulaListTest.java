package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import org.junit.Test;

/**
 * Unit tests to validate this class.
 * 
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class FormulaListTest {
	@Test
	public void testChord() {
		Formula expected1 = new Formula("maj", "Major", "1-3-5");
		Formula expected2 = new Formula("maj", "Major", "1-3-5");
		Formula unexpected = new Formula("sus4", "Suspended 4th", "1-4-5");

		assertEquals("Equals 1", expected1, expected2);
		assertEquals("Equals 2", expected2, expected1);

		assertFalse("Unequals", expected1.equals(unexpected));

		assertFalse("Hash", expected1.hashCode() == unexpected.hashCode());
	}

	@Test
	public void testChordListByName() {
		Formula expected = new Formula("maj", "Major", "1-3-5");
		assertEquals("Name", expected, FormulaList.get("maj"));

		expected = new Formula("sus4", "Suspended 4th", "1-4-5");
		assertEquals("Name 2", expected, FormulaList.get("sus4"));

		assertNull("Null", FormulaList.get("dummy"));
	}

	@Test
	public void testChordListByFormula() {
		Formula expected = new Formula("maj", "Major", "1-3-5");
		assertEquals("Formula", expected, FormulaList.get("1-3-5"));

		expected = new Formula("sus4", "Suspended 4th", "1-4-5");
		assertEquals("Formula 2", expected, FormulaList.get("1-4-5"));

		assertNull("Null", FormulaList.get("dummy"));
	}

	@Test
	public void testChordFormula() {
		Formula major = FormulaList.get("maj");
		// System.out.println( "Major formula=" + major.getFormula());

		NoteList expectedCMajor = new NoteList(Note.C, Note.E, Note.G);
		NoteList testCMajor = new NoteList();
		testCMajor.setRelative(Note.C, major.getFormula());

		assertEquals("Major Formula", expectedCMajor, testCMajor);

		Formula minor = FormulaList.get("m");
		// System.out.println( "Minor formula=" + minor.getFormula());

		NoteList expectedGMinor = new NoteList(Note.G, Note.parse("A#1"), Note.parse("D1"));
		NoteList testGMinor = new NoteList();
		testGMinor.setRelative(Note.G, minor.getFormula());

		assertEquals("Minor Formula", expectedGMinor, testGMinor);

	}

	@Test
	public void testScale() {
		Formula expected1 = new Formula("Lydian", "", "1;2;3;#4;5;6;7");
		Formula expected2 = new Formula("Lydian", "", "1;2;3;#4;5;6;7");
		Formula unexpected1 = new Formula("Phrygian", "", "1;b2;b3;4;5;b6;b7");
		Formula unexpected2 = new Formula("Enigmatic", "", "1;b2;3;#4;#5;#6;7");

		assertEquals("Equals 1", expected1, expected2);
		assertEquals("Equals 2", expected2, expected1);

		assertFalse("Unequals", expected1.equals(unexpected1));

		assertFalse("Hash 1", expected1.hashCode() == unexpected1.hashCode());
		assertFalse("Hash 2", unexpected1.hashCode() == unexpected2.hashCode());
	}

	@Test
	public void testScaleFormula() {
		Formula p = FormulaList.get("Phrygian");
		System.out.println("Phrygian formula=" + p.getFormula());

		NoteList expectedCPhrygian = new NoteList(Note.C, Note.Cs, Note.Ds, Note.F, Note.G, Note.Gs, Note.parse("A#1"));
		NoteList testCP = new NoteList();
		testCP.setRelative(Note.C, p.getFormula());

		System.out.println("C Phrygian=" + testCP);
		assertEquals("Phrygian Formula", expectedCPhrygian, testCP);

	}

}