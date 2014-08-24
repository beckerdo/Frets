package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Unit tests to validate this class.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class NoteTest 
{
    @Before
    public void setup()
    {
    }
    
    @Test
    public void testEnum()
    {
        assertTrue("Natural name", "F".equals( Note.Name.F.getName()) );        	
        assertTrue("Flat name", "Bb".equals( Note.Name.Bb.getName()) );        	
        assertTrue("Sharp name", "C#".equals( Note.Name.Cs.getName()) );        	
        assertTrue("Natural Utf name", "C".equals( Note.Name.C.getUTFName()) );        	
        // System.out.println( "Gb UTF=\"" + Note.Name.Gb.getUTFName() + "\"");
        // assertTrue("Flat Utf name", "G♭".equals( Note.Name.Gb.getUTFName()) );        	
        // assertTrue("Sharp Utf name", "A♯".equals( Note.Name.As.getUTFName()) );        	
        assertTrue("Values", 3 == Note.Name.Ds.getValue() );        	
        assertTrue("Enharmonic values", Note.Name.Ds.getValue() == Note.Name.Eb.getValue() );        	
    }

    @Test
    public void testGettersSetters()
    {
    	Note test = new Note( 1, 4 );
        assertEquals("Get octave", 1, test.getOctave() );        	
        assertEquals("Get value", 4, test.getValue() );        	
    }

    @Test
    public void testEquals()
    {
    	Note test1 = new Note( 1, 4 );
    	Note test2 = new Note( 1, 4 );
    	Note test3 = new Note( 1, 4 );
    	Note test4 = new Note( 0, 6 );
    	
        assertEquals("Identity", test1, test1 );        	
        assertTrue("Symmetry 1", test1.equals( test2 ));        	
        assertTrue("Symmetry 2", test2.equals( test1 ));        	
        assertFalse("Symmetry 3", test1.equals( test4 ) );        	
        assertFalse("Symmetry 4", test4.equals( test1 ) );        	
        assertTrue("Transitivity", test1.equals(test2) && test2.equals( test3 ) && test1.equals( test3 ) );        	
        assertEquals("Clone", test1, new Note( test1 ));        	
    }

    @Test
    public void testComparable()
    {
    	Note test1 = new Note( 0, 4 );
    	Note test2 = new Note( 0, 10 );
    	Note test3 = new Note( 1, 3 );
    	Note test4 = new Note( 1, 9 );
    	Note test5 = new Note( 8, 0 );
    	Note test6 = new Note( 8, 11 );
    	
        assertTrue("Same octave 1", 0 > test1.compareTo(test2));        	
        assertTrue("Same octave 2", 0 < test2.compareTo(test1));        	
        assertTrue("Different octave 1", 0 > test2.compareTo(test3));        	
        assertTrue("Different octave 2", 0 < test4.compareTo(test2));        	
        assertTrue("Multi octave 1", 0 > test1.compareTo(test5));        	
        assertTrue("Multi octave 2", 0 < test6.compareTo(test2));        	
    }

    @Test
    public void testSorting()
    {
    	Note test6 = new Note( 7, 11 );
    	Note test2 = new Note( 0, 10 );
    	Note test3 = new Note( 1, 3 );
    	Note test5 = new Note( 9, 0 );
    	Note test1 = new Note( 0, 4 );
    	Note test4 = new Note( 1, 9 );

    	SortedSet<Note> noteSet = new TreeSet<Note>();
    	noteSet.add( test6 );     	// System.out.println( "Note=" + test6 );
    	noteSet.add( test2 );     	// System.out.println( "Note=" + test2 );
    	noteSet.add( test3 );     	// System.out.println( "Note=" + test3 );
    	noteSet.add( test5 );     	// System.out.println( "Note=" + test5 );
    	noteSet.add( test1 );     	// System.out.println( "Note=" + test1 );
    	noteSet.add( test4 );     	// System.out.println( "Note=" + test4 );

    	System.out.println( "Sorted note set=" + noteSet );
    	Note first = noteSet.first();
        assertTrue("Sorted set first", (new Note( 0, 4 )).equals( first ));        	
    	Note last = noteSet.last();
        assertTrue("Sorted set first", (new Note( 9, 0 )).equals( last ));        	
    }

    @Test
    public void testList()
    {
    	Note[] notes = new Note[] { Note.C, Note.D, Note.E, Note.F, Note.G, Note.A, Note.B };
    	List<Note> doremi = Arrays.asList( notes );
    	// Note[] notes1  = doremi.toArray( new Note[ doremi.size() ]);

        assertEquals("List first", Note.C, doremi.get( 0 ));        	
        assertEquals("List last", Note.B, doremi.get( doremi.size() - 1 ));        	
    }

    @Test
    public void testArithmetic()
    {
    	Note altered = new Note( Note.GuitarHighE );
    	   	
        assertEquals("Addition", new Note( Note.GuitarHighE.getOctave(), Note.Name.Fs.getValue()),
        		altered.plus( Interval.whole ));
        assertEquals("Addition static", new Note( Note.GuitarHighE.getOctave(), Note.Name.A.getValue()),
        		Note.plus( altered, Interval.wholehalf ));
        assertEquals("Addition interval", Note.GuitarA, Note.plus( Note.GuitarLowE , Interval.fourth ) );

        // Check string tuning via addition
        assertEquals("Guitar A", Note.GuitarA, Note.plus( Note.GuitarLowE, Interval.fourth ));
        assertEquals("Guitar D", Note.GuitarD, Note.plus( Note.plus( Note.GuitarLowE, Interval.fourth ), Interval.fourth ));
        assertEquals("Guitar G", Note.GuitarG, Note.plus( Note.plus( Note.plus( Note.GuitarLowE, Interval.fourth ), Interval.fourth ), Interval.fourth ));
        assertEquals("Guitar B", Note.GuitarB, Note.plus( Note.plus( Note.plus( Note.plus( Note.GuitarLowE, Interval.fourth ), Interval.fourth ), Interval.fourth ), Interval.third ));
        assertEquals("Guitar HighE", Note.GuitarHighE, Note.plus( Note.plus( Note.plus( Note.plus( Note.plus( Note.GuitarLowE, Interval.fourth ), Interval.fourth ), Interval.fourth ), Interval.third ), Interval.fourth ));
        
        altered = new Note( Note.GuitarHighE );
        assertEquals("Subtraction", Note.GuitarB, altered.minus( Interval.fourth ));
        assertEquals("Subtraction static", Note.GuitarB, Note.minus( Note.GuitarHighE, Interval.fourth ));
        assertEquals("Subtraction interval", Note.GuitarB, (new Note( Note.GuitarHighE )).minus( 5 ) );

        // Check string tuning via subtraction
        assertEquals("Guitar B", Note.GuitarB, Note.minus( Note.GuitarHighE, Interval.fourth ));
        assertEquals("Guitar G", Note.GuitarG, Note.minus( Note.minus( Note.GuitarHighE, Interval.fourth ), Interval.third ));
        assertEquals("Guitar D", Note.GuitarD, Note.minus( Note.minus( Note.minus( Note.GuitarHighE, Interval.fourth ), Interval.third ), Interval.fourth ));
        assertEquals("Guitar A", Note.GuitarA, Note.minus( Note.minus( Note.minus( Note.minus( Note.GuitarHighE, Interval.fourth ), Interval.third ), Interval.fourth ), Interval.fourth ));
        assertEquals("Guitar LowE", Note.GuitarLowE, Note.minus( Note.minus( Note.minus( Note.minus( Note.minus( Note.GuitarHighE, Interval.fourth ), Interval.third ), Interval.fourth ), Interval.fourth ), Interval.fourth ));
    }

    @Test
    public void testUnicode()
    {
    	// System.out.println( "HTML super scripts=a&sup1;, b&sup2;, c&sup3;" );
        // System.out.println( "Unicode message=\u7686\u3055\u3093\u3001\u3053\u3093\u306b\u3061\u306f" );
        // System.out.println( "Unicode superscripts constant=\u2070\u00b9\u00b2\u00b3\u2074\u2075\u2076\u2077\u2078\u2079" );        
        StringBuilder sb = new StringBuilder( "Unicode superscripts construct=" ); 
        for ( int i = 0; i <  Note.SUPERSCRIPT.length; i ++ )
        	sb.append( Note.SUPERSCRIPT[ i ]);
        // System.out.println( sb.toString());
        // System.out.println( "Unicode subscripts constant=\u2080\u2081\u2082\u2083\u2084\u2085\u2086\u2087\u2088\u2089" );        
        sb = new StringBuilder( "Unicode subscripts construct=" ); 
        for ( int i = 0; i <  Note.SUBSCRIPT.length; i ++ )
        	sb.append( Note.SUBSCRIPT[ i ]);
        // System.out.println( sb.toString());
        
        Note highOctave = new Note( 1026, Note.Name.C.getValue() );
        String highOctaveString = highOctave.toString();
        System.out.println( "Really high C (unicode)=" + highOctaveString + ", length=" + highOctaveString.length());
        assertTrue("Note with four digit octave", 5 == highOctaveString.length());
        assertTrue("Note characters not \"?\"", -1 == highOctaveString.indexOf("?"));
        
        // Charset conversion
        // showChartSets();
    
        // Create the encoder and decoder for target page
        // showCoders();
    }

    @Test
    public void testStringParse()
    {
    	String name = "Eb4";
    	Note expected = new Note( 4, Note.Name.Eb.getValue());
    	// System.out.println( name + " value=" + expected.getValue() + ", octave=" + expected.getOctave() );
    	assertEquals( name, expected, Note.parse( name ));

    	name = "g5";
    	expected = new Note( 5, Note.Name.G.getValue());
    	assertEquals( name, expected, Note.parse( name ));

    	name = "Fs";
    	expected = new Note( 0, Note.Name.Fs.getValue());
    	assertEquals( name, expected, Note.parse( name ));
    }

    @Test
    public void testStringParseParameters()
    {
    	String name = "Eb4";
    	Note expected = new Note( 4, Note.Name.Eb.getValue());
    	// System.out.println( name + " value=" + expected.getValue() + ", octave=" + expected.getOctave() );
    	assertEquals( name, expected, Note.parse( name ));

    	name = "g5";
    	expected = new Note( 5, Note.Name.G.getValue());
    	assertEquals( name, expected, Note.parse( name ));

    	name = "Fs";
    	expected = new Note( 0, Note.Name.Fs.getValue());
    	assertEquals( name, expected, Note.parse( name ));
    }

    @Test
    public void testStringConstructor()
    {
        try { Note.parse( null );
        	assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        try { Note.parse( "" );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        try { Note.parse( " " );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        try { Note.parse( "," );
    		assertFalse( "Exception not thrown", true );
        } catch ( Throwable e ) {
        	assertEquals( "IllegalArgumentException",  IllegalArgumentException.class, e.getClass() );
        }
        
        assertEquals("Constructor 2", "D#4", Note.parse( "D#4" ).toString());
        assertEquals("Constructor 1", "G5", Note.parse( "g5" ).toString());
        assertEquals("Constructor 3", "F#", Note.parse( "   F s    " ).toString());
        assertEquals("Constructor 4", "C", Note.parse( "   C   " ).toString());

        assertEquals("Constructor 2", (new Note("   F    2   ")).toString(), Note.parse( "F2" ).toString());
    }
 
    @Test
    public void testHasAccidentals()
    {
    	Note note = Note.parse( "Eb4" );
    	assertTrue( "Accidentals 1", note.hasAccidental() );

    	note = Note.parse( "Gs2" );
    	assertTrue( "Accidentals 2", note.hasAccidental() );

    	note = Note.parse( "C1" );
    	assertFalse( "Accidentals 3", note.hasAccidental() );
    }

    public void showCharsets() {
        // Available char sets
        // Map map = Charset.availableCharsets(); 
        // Iterator it = map.keySet().iterator(); 
        // while (it.hasNext()) { 
        	// String charsetName = (String)it.next(); 
        	// System.out.println ( charsetName ); 
        	// Charset charset = Charset.forName(charsetName);
        
        	// Big5 CESU-8 	COMPOUND_TEXT EUC-CN EUC-JP EUC-KR EUC-TW GB18030 GB2312 GBK hp-roman8
        	// IBM-037 IBM-1006 IBM-1025 .. IBM-971
        	// ISCII91 ISO-2022-CN ISO-2022-CN-GB ISO-2022-JP ISO-2022-KR 
        	// ISO-8859-1 ISO-8859-10 ISO-8859-13 ISO-8859-14 ISO-8859-15 ISO-8859-16 
        	// ISO-8859-2 ISO-8859-3 ISO-8859-4 ISO-8859-5 ISO-8859-6 ISO-8859-6S ISO-8859-7 ISO-8859-8 ISO-8859-9        	
        	// JIS0201 JIS0208 JIS0212 Johab KOI8-R KOI8-RU KOI8-U KSC5601
        	// MacArabic MacCentralEurope ... MacUkraine
        	// PTCP154 Shift_JIS TIS-620
        	// US-ASCII UTF-16 UTF-16BE UTF-16LE UTF-32 UTF-32BE UTF-32LE UTF-8 UTF-8J
        	// windows-1250 windows-1251 windows-1252 windows-1253 windows-1254 windows-1255 windows-1256
        	// windows-1256S windows-1257 windows-1258 windows-31j windows-874 windows-936 windows-949 windows-950
        	// X-UnicodeBig X-UnicodeLittle        	
        // }    	
    }
    public void showCoders() {
        // String targetPage = "ISO-8859-1";
        // Charset charset = Charset.forName("ISO-8859-1"); 
        // CharsetDecoder decoder = charset.newDecoder(); 
        // CharsetEncoder encoder = charset.newEncoder(); 
        // try { 
        	// Convert a string to bytes in a ByteBuffer 
        	// The new ByteBuffer is ready to be read. 
        	// CharBuffer cbuf = CharBuffer.wrap( highOctaveString );
        	// System.out.println( "Char buffer=" + cbuf + ", length=" + cbuf.length() );        	
        	// ByteBuffer bbuf = encoder.encode( cbuf );
        	// System.out.println( "Byte buffer=" + bbuf + ", length=" + bbuf.array().length );
        	// Convert bytes in a ByteBuffer to a character ByteBuffer and then to a string. 
        	// The new ByteBuffer is ready to be read. 
        	// cbuf = decoder.decode(bbuf); 
        	// String s = cbuf.toString(); 
            // System.out.println( "Really high C (" + targetPage + ")=" + s );
        // } catch (CharacterCodingException e) { 
        // 	System.out.println( "Encoding exception=" + e );
        // }   	
    }

    @Test
    public void testJson()  {
    	Note expected = new Note( 4, Note.Name.Eb.getValue());
    	String json = expected.toJSON();   	
    	// System.out.println( "Note=" + expected.toString() + ", json=" + json );
    	Note returned = Note.fromJSON( json );
    	assertEquals( "Note json", expected, returned );
    }

}