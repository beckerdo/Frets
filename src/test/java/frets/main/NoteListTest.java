package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit tests to validate this class.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class NoteListTest 
{
    @Before
    public void setup()
    {
    }
    
    @Test
    public void testEquals()
    {
    	NoteList empty1 = new NoteList();
    	NoteList empty2 = new NoteList();
    	NoteList a = new NoteList( new Note[]{ Note.A, Note.B, Note.C } );
    	List<Note> list = new LinkedList<Note>();
    	list.add( Note.A );
    	list.add( Note.B );
    	list.add( Note.C );
    	NoteList b = new NoteList( list );
    	NoteList c = new NoteList( a );
    	
        assertEquals("Identity 1", empty1, empty1 );        	
        assertTrue("Identity 2", a.equals( a ));        	
        assertTrue("Symmetry 1", empty1.equals( empty2 ));        	
        assertTrue("Symmetry 2", empty2.equals( empty1 ));        	
        assertTrue("Symmetry 3", a.equals( b ));        	
        assertTrue("Symmetry 4", b.equals( a ) );        	
        assertTrue("Transitivity", a.equals(b) && b.equals( c ) && a.equals( c ) );        	
    }

    @Test
    public void testComparable()
    {
    	NoteList empty = new NoteList();
    	NoteList a = new NoteList( new Note[]{ Note.A, Note.B, Note.C } );
    	NoteList b = new NoteList( new Note[]{ Note.A, Note.B, Note.C, Note.D } );
    	NoteList c = new NoteList( new Note[]{ Note.A, Note.B, Note.C, Note.E } );
    	
        assertTrue("Empty 1", empty.compareTo( a ) < 0 );        	
        assertTrue("Empty 2", a.compareTo( empty ) > 0 );        	
        assertTrue("Identity", a.compareTo( a ) == 0 );        	
        assertTrue("Same length 1", b.compareTo( c ) < 0 );        	
        assertTrue("Same length 2", c.compareTo( b ) > 0 );        	
        assertTrue("Different length 1", a.compareTo( b ) < 0 );        	
        assertTrue("Different length 2", b.compareTo( a ) > 0 );        	
    }

    @Test
    public void testListMethods()
    {
    	// Test some of the crud methods.
    	NoteList a = new NoteList(  );
    	assertTrue("Length 1", 0 == a.size());        	
    	a.addAll( Arrays.asList( new Note[]{ Note.A, Note.B, Note.C } ) );
    	assertTrue("Length 2", 3 == a.size());
    	NoteList b = new NoteList( a );
    	
    	a.add( Note.D );
    	assertTrue("Length 3", 4 == a.size());
    	
    	a.remove( 2 );
    	assertTrue("Length 4", 3 == a.size());    	
    	b.remove( Note.C );
    	assertTrue("Length 5", 2 == b.size());
    	
    	assertTrue( "Contains", a.contains( Note.D ));

    	b.clear();
    	assertTrue("Length 6", 0 == b.size());
    	assertTrue("IsEmpty", b.isEmpty());    	
    }

    @Test
    public void testSorting()
    {
    	NoteList bMajor = new NoteList(
        		new Note[]{ 
        				Note.plus( Note.GuitarB, Interval.seventh ),
        				Note.plus( Note.GuitarB, Interval.second ),
        				Note.plus( Note.GuitarB, Interval.octave ),
        				Note.plus( Note.GuitarB, Interval.fifth ),
        				Note.plus( Note.GuitarB, Interval.fourth ),
        				Note.GuitarB,
        				Note.plus( Note.GuitarB, Interval.sixth ),
        				Note.plus( Note.GuitarB, Interval.third ),
        		} );

    	Collections.sort( bMajor );
    	// System.out.println( "Sorted B major=" + bMajor );
    	assertEquals("First", Note.GuitarB, bMajor.get( 0 ));
    	assertEquals("Last", Note.plus( Note.GuitarB, Interval.octave ), bMajor.get( bMajor.size() - 1 ));
    }

    @Test
    public void testUpdateArithmetic()
    {
        NoteList standard = new NoteList( 
        	Note.GuitarLowE, Note.GuitarA, Note.GuitarD, Note.GuitarG, Note.GuitarB, Note.GuitarHighE );

        // Retune up a to G.
        standard.updateAbsolute( Note.plus( Note.GuitarLowE, Interval.minorThird ));
        // System.out.println( "Notes after=" + standard );
        assertEquals( "Note G", Note.plus( Note.GuitarLowE, Interval.minorThird ), standard.get( 0 ));
        assertEquals( "Note C", Note.plus( Note.GuitarA, Interval.minorThird ), standard.get( 1 ));
        assertEquals( "Note F", Note.plus( Note.GuitarD, Interval.minorThird ), standard.get( 2 ));
        assertEquals( "Note A#", Note.plus( Note.GuitarG, Interval.minorThird ), standard.get( 3 ));
        assertEquals( "Note D", Note.plus( Note.GuitarB, Interval.minorThird ), standard.get( 4 ));
        assertEquals( "Note G", Note.plus( Note.GuitarHighE, Interval.minorThird ), standard.get( 5 ));

        // Detune up a to G.
        standard.updateRelative( -Interval.fourth.getValue() );
        // System.out.println( "Notes after=" + standard );
        assertEquals( "Note D", Note.minus( Note.GuitarLowE, Interval.W ), standard.get( 0 ));
        assertEquals( "Note G", Note.minus( Note.GuitarA, Interval.W ), standard.get( 1 ));
        assertEquals( "Note C", Note.minus( Note.GuitarD, Interval.W ), standard.get( 2 ));
        assertEquals( "Note F", Note.minus( Note.GuitarG, Interval.W ), standard.get( 3 ));
        assertEquals( "Note A", Note.minus( Note.GuitarB, Interval.W ), standard.get( 4 ));
        assertEquals( "Note D", Note.minus( Note.GuitarHighE, Interval.W ), standard.get( 5 ));
    }

    @Test
    public void testUpdateFormulas()
    {
        NoteList major = new NoteList( Interval.root, Interval.third, Interval.fifth );  
        major.updateAbsolute( Note.C );
        //System.out.println( "C major=" + major );
        assertEquals( "C", Note.C, major.get( 0 ));
        assertEquals( "E", Note.E, major.get( 1 ));
        assertEquals( "G", Note.G, major.get( 2 ));
        major.updateAbsolute( Note.G );
        // System.out.println( "G major=" + major );
        assertEquals( "G", Note.G, major.get( 0 ));
        assertEquals( "B", Note.B, major.get( 1 ));
        assertEquals( "D", Note.D.getValue(), major.get( 2 ).getValue()); // next octave
        major.updateAbsolute( Note.E );
        // System.out.println( "E major=" + major );
        assertEquals( "E", Note.E, major.get( 0 ));
        assertEquals( "G#", Note.Gs, major.get( 1 ));
        assertEquals( "B", Note.B, major.get( 2 ));

        NoteList minor = new NoteList( Interval.root, Interval.minorThird, Interval.fifth );  
        minor.updateAbsolute( Note.Fs );
        // System.out.println( "F# minor=" + minor );
        assertEquals( "F#", Note.Fs, minor.get( 0 ));
        assertEquals( "A", Note.A, minor.get( 1 ));
        assertEquals( "C", Note.Cs.getValue(), minor.get( 2 ).getValue());

        NoteList aug = new NoteList( Interval.root, Interval.third, Interval.augmentedFifth );
        aug.updateAbsolute( Note.Bb );
        // System.out.println( "Bb aug=" + aug );
        assertEquals( "Bb", Note.Bb, aug.get( 0 ));
        assertEquals( "D", Note.D.getValue(), aug.get( 1 ).getValue());
        assertEquals( "Gb", Note.Gb.getValue(), aug.get( 2 ).getValue());
        NoteList dim = new NoteList( Interval.root, Interval.third, Interval.diminishedFifth );  
        dim.updateAbsolute( Note.A );
        // System.out.println( "A dim=" + dim );
        assertEquals( "A", Note.A, dim.get( 0 ));
        assertEquals( "Cs", Note.Cs.getValue(), dim.get( 1 ).getValue());
        assertEquals( "Ds", Note.Ds.getValue(), dim.get( 2 ).getValue());
        NoteList sus2 = new NoteList( Interval.root, Interval.second, Interval.fifth );
        sus2.updateAbsolute( Note.Cs );
        // System.out.println( "C# sus2=" + sus2 );
        assertEquals( "C#", Note.Cs, sus2.get( 0 ));
        assertEquals( "D#", Note.Ds, sus2.get( 1 ));
        assertEquals( "G#", Note.Gs, sus2.get( 2 ));
        NoteList sus4 = new NoteList( Interval.root, Interval.fourth, Interval.fifth );  
        sus4.updateAbsolute( Note.Ds );
        // System.out.println( "D# sus4=" + sus4 );
        assertEquals( "D#", Note.Ds, sus4.get( 0 ));
        assertEquals( "G#", Note.Gs, sus4.get( 1 ));
        assertEquals( "A#", Note.As, sus4.get( 2 ));
    }
    
    @Test
	public void testIntervals() {
		NoteList major = new NoteList(Note.C, Note.E, Note.G);
		// System.out.println("Major=" + major.toStringIntervals());

		assertTrue("R", Interval.root.getValue() == major.get(0).getValue() );
		assertTrue("3", Interval.third.getValue() == major.get(1).getValue() );
		assertTrue("5", Interval.fifth.getValue() == major.get(2).getValue() );
		
		NoteList FsMinor = new NoteList( Note.Fs, Note.plus( Note.Fs, Interval.minorThird ), Note.plus( Note.Fs, Interval.fifth ));
		NoteList minor = FsMinor.getAbsoluteIntervals();
		System.out.println( "Fs minor notes=" + FsMinor + ", absolute intervals=" + minor.toStringIntervals() + 
		   ", relative intervals=" + minor.toStringRelativeIntervals() );
  		assertTrue("R", Interval.root.getValue() == minor.get(0).getValue() );
  		assertTrue("b3", Interval.minorThird.getValue() == minor.get(1).getValue() );
  		assertTrue("5", Interval.fifth.getValue() == minor.get(2).getValue() );
  
		NoteList BbAug = new NoteList( Note.Bb, Note.plus( Note.Bb, Interval.third ), Note.plus( Note.Bb, Interval.augmentedFifth ) );
		NoteList aug = BbAug.getAbsoluteIntervals(); 
		System.out.println( "Bb aug notes=" + BbAug + ", absolute intervals=" + aug.toStringIntervals() + ", relative intervals=" + aug.toStringRelativeIntervals() );
  		assertTrue("R", Interval.root.getValue() == aug.get(0).getValue() );
  		assertTrue("3", Interval.third.getValue() == aug.get(1).getValue() );
  		assertTrue("#5", Interval.augmentedFifth.getValue() == aug.get(2).getValue() );

  		NoteList Adim = new NoteList( Note.A,  Note.plus( Note.A, Interval.third ), Note.plus( Note.A, Interval.diminishedFifth ) );
		System.out.println( "A dim notes=" + Adim + ", absolute intervals=" + Adim.toStringIntervals() + ", relative intervals=" + Adim.toStringRelativeIntervals() );
		int []  dim = Adim.getRelativeIntervals(); 
  		assertTrue("R", Interval.root.getValue() == dim[ 0 ] );
  		assertTrue("3", Interval.third.getValue() == dim[ 1 ] );
  		assertTrue("b5", Interval.whole.getValue() == dim[ 2 ] );

  		// Test intervals constructor
		NoteList Em = new NoteList( Note.E, Interval.r, Interval.m3, Interval.p5  );
		NoteList absInt = Em.getAbsoluteIntervals();
		System.out.println( "Em notes=" + Em + ", absolute intervals=" + Em.toStringIntervals() + 
		    ", relative intervals=" + Em.toStringRelativeIntervals() );
  		assertEquals("R", Interval.root.getValue(), absInt.get(0).getValue() );
  		assertEquals("b3", Interval.minorThird.getValue(), absInt.get(1).getValue() );
  		assertEquals("5", Interval.fifth.getValue(), absInt.get(2).getValue() );  
    }

    @Test
	public void testInversions() {
		NoteList emInv =  new NoteList( Note.E, Interval.m3, Interval.p5, Interval.r  );
		NoteList invAbsInt = emInv.getAbsoluteIntervals();
		int [] invRelInt = emInv.getRelativeIntervals();

		System.out.println( "Em 1stInv notes=" + emInv + ", absolute intervals=" + emInv.toStringIntervals() + 
		   ", relative intervals=" + emInv.toStringRelativeIntervals() );
  		assertEquals("1st inv rel Minor Third", Interval.root.getValue(), invRelInt[ 0 ] );
  		assertEquals("1st inv rel Fifth", Interval.third.getValue(), invRelInt[ 1 ] );
  		assertEquals("1st inv rel Root", -Interval.fifth.getValue(), invRelInt[ 2 ] );

		System.out.println( "Em 1stInv notes=" + invAbsInt + ", absolute intervals=" + invAbsInt.toStringIntervals() + 
			", relative intervals=" + invAbsInt.toStringRelativeIntervals() );
  		assertEquals("1st inv abs Minor Third", Interval.root.getValue(), invAbsInt.get(0).getAbsoluteValue() );
  		assertEquals("1st inv abs Fifth", Interval.third.getValue(), invAbsInt.get(1).getAbsoluteValue() );
  		assertEquals("1st inv abs Root", -Interval.minorThird.getValue(), invAbsInt.get(2).getAbsoluteValue() );

		NoteList em2Inv = new NoteList( Note.E, Interval.p5, Interval.r, Interval.m3  );
		NoteList inv2AbsInt = em2Inv.getAbsoluteIntervals();
		int [] inv2RelInt = em2Inv.getRelativeIntervals();

		System.out.println( "Em 2ndInv notes=" + em2Inv + ", absolute intervals=" + em2Inv.toStringIntervals() + 
		   ", relative intervals=" + em2Inv.toStringRelativeIntervals() );
  		assertEquals("2nd inv rel Fifth", Interval.root.getValue(), inv2RelInt[ 0 ] );
  		assertEquals("2nd inv rel Root", -Interval.fifth.getValue(), inv2RelInt[ 1 ] );
  		assertEquals("2nd inv rel Flat Third", Interval.minorThird.getValue(), inv2RelInt[ 2 ] );

		System.out.println( "Em 2ndInv notes=" + inv2AbsInt + ", absolute intervals=" + inv2AbsInt.toStringIntervals() + 
			", relative intervals=" + inv2AbsInt.toStringRelativeIntervals() );
  		assertEquals("2nd inv abs Fifth", Interval.root.getValue(), inv2AbsInt.get(0).getAbsoluteValue() );
  		assertEquals("2nd inv abs Root", -Interval.fifth.getValue(), inv2AbsInt.get(1).getAbsoluteValue() );
  		assertEquals("2nd inv abs Flat Third", -Interval.third.getValue(), inv2AbsInt.get(2).getAbsoluteValue() );
    }

    @Test
	public void testFormulas() {
		NoteList expectedMajor = new NoteList(Note.C, Note.E, Note.G);
		NoteList test = new NoteList();

		test.setRelative( Note.C, "root third fifth");
		assertEquals("Major named", expectedMajor, test );
		
		test.setRelative( Note.C, "R-3-5");
		assertEquals("Major numbered", expectedMajor, test );

		assertEquals("Major numbered cons", expectedMajor, new NoteList( Note.C, "R-3-5" ));
	}

    @Test
    public void testString() {
		NoteList expected = new NoteList(Note.C, Note.E, Note.G);
		String expectedString = expected.toString();
		// System.out.println( "NoteList toString=" + expectedString );
		
		NoteList produced = new NoteList( expectedString );
		String producedString = produced.toString();

        assertEquals("toString constructor", expected, produced );        	
        assertEquals("toString compare", expectedString, producedString );        	
    }

    @Test
    public void testJson() {
		NoteList expected = new NoteList(Note.C, Note.E, Note.Fs);
		String expectedString = expected.toString();
		
		String json = expected.toJSON();
		System.out.println( "NoteListtest.testJson expected=" + expectedString + ", json=" + json );
		
		NoteList produced =  NoteList.fromJSON( json );
        assertEquals("JSON round trip", expected, produced );        	
    }

    @Test
    public void testStringConstructor()
    {
        try { NoteList.parse( null );
        	assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        try { NoteList.parse( "" );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        try { NoteList.parse( " " );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        
        assertEquals("Constructor 2", "D#4,A,E", NoteList.parse( "D#4,A,   E" ).toString());
        assertEquals("Constructor 1", "G5", NoteList.parse( "g5" ).toString());
        assertEquals("Constructor 3", "F#", NoteList.parse( "   F s    " ).toString());
        assertEquals("Constructor 4", "C,E2,G", NoteList.parse( "   C   , E2, G" ).toString());

        assertEquals("Constructor 2", (new Note("   F    2   ")).toString(), Note.parse( "F2" ).toString());
    }
 

}