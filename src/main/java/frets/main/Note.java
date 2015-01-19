package frets.main;

/**
 * Encapsulates a note.
 * <p>
 * Value represents 0-12 note of a chromatic scale.
 * Octave represents 0-n iOctave from lowest sound.
 * <p>
 * The toString method produces a note/accidental/octave string for a note.
 * See http://en.wikipedia.org/wiki/Scientific_pitch_notation.
 * 
* @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class Note implements Comparable<Note>{
    public final static String [] SUPERSCRIPT = 
    	{ "\u2070", "\u00b9", "\u00b2", "\u00b3", "\u2074", "\u2075", "\u2076", "\u2077", "\u2078", "\u2079" };
    public final static String [] SUBSCRIPT =
    	{ "\u2080", "\u2081", "\u2082", "\u2083", "\u2084", "\u2085", "\u2086", "\u2087", "\u2088", "\u2089" };
    	
	// Piano C0 is about 17Hz, E10 is about 20kHz.
	// Guitar low E is about 82 Hz (==E2 piano), highE (12th fret 1st string) is about 659Hz (==E5 on piano).
	public enum Name {
		C( "C", "n", 0 ),
		Cs( "C", "#", 1 ),
		Db( "D", "b", 1 ),
		D( "D", "n", 2 ),
		Ds( "D", "#", 3 ),
		Eb( "E", "b", 3 ),
		E( "E", "n", 4 ),
		F( "F", "n", 5 ),
		Fs( "F", "#", 6 ),
		Gb( "G", "b", 6 ),
		G( "G", "n", 7 ),
		Gs( "G", "#", 8 ),
		Ab( "A", "b", 8 ),
		A( "A", "n", 9 ),
		As( "A", "#", 10 ),
		Bb( "B", "b", 10 ),
		B( "B", "n", 11 );
		private Name(String baseName,String accidental, int value ) {
	    	this.baseName = baseName;
	        this.accidental = accidental;
	        this.value = value;
	    }
		public String getName() {
			if ( "n".equals( accidental ) )	return baseName;
			return baseName + accidental; 
		}
		public String getUTFName() { 
			if ( "n".equals( accidental ) )	return baseName;
			return baseName + getUTFAccidental(); 
		}
		public String getAccidental() { return accidental; }
		public String getUTFAccidental() { 
			if ( "n".equals( accidental ) ) return "♮";
			else if ( "b".equals( accidental ) ) return "♭";
			else if ( "#".equals( accidental ) ) return "♯";
			throw new IllegalStateException ( "Note accidental=" + accidental );
		}
		public boolean hasAccidental() { return !accidental.equals( "n" ); }

		public int getValue() { return value; }
		public String toString() { return getName(); }
		
		public static Name getName( int value ){
			int normalValue = value % 12;
			switch( normalValue ) {
			case 0 : return C;
			case 1 : return Cs;
			case 2 : return D;
			case 3 : return Ds;
			case 4 : return E;
			case 5 : return F;
			case 6 : return Fs;
			case 7 : return G;
			case 8 : return Gs;
			case 9 : return A;
			case 10 : return As;
			case 11 : return B;
			}
			return C;
		}

		private String baseName;		
	    private String accidental;
	    private int value;
	}
	
	public Note() {
		this.value = 0;
		this.iOctave = 0;
	}
	
	/** Make a note of a given octave and value. */
	public Note( int iOctave, int value) {
		this.value = value;
		this.iOctave = iOctave;
		normalize();
	}

	/** Make a note out of an interval or absolute value. */
	public Note( int interval ) {
		this.iOctave = 0;
		this.value = interval;
		normalize();
	}

	/** Make a note out of an interval or absolute value. */
	public Note( Interval interval ) {
		this.iOctave = 0;
		this.value = interval.getValue();
		normalize();
	}

	/** Make a new note, clone, without touching the original. */
	public Note( final Note other ) {
		if ( null == other ) return;
		this.iOctave = other.iOctave;
		this.value = other.value;
		normalize();
	}

	/** Return note based on formula [value][accidental]*[octave]*
	 * where value is A-Ga-g,
	 * accidental is b♭, n♮, s#♯,
	 * octave is 0-n 
	 * @param toString
	 * @return
	 */
	public Note( String toString ) {
		if ( toString == null ) 
			throw new IllegalArgumentException( "Bad constructor string=\"" + toString + "\"" );
		toString = toString.replaceAll( "\\s", "" );
		if ( toString.length() < 1 )
			throw new IllegalArgumentException( "Bad constructor string=\"" + toString + "\"" );
		String valueString = toString.substring( 0, 1 ).toLowerCase();
		this.value = "cXdXefXgXaXb".indexOf( valueString );
		if ( value == -1 )
			throw new IllegalArgumentException( "Bad note value=\"" + valueString + "\"" );
		if ( toString.length() > 1 ) {
			String accidental = toString.substring( 1, 2 ).toLowerCase();
			int beginIndex = 1;
			if ( -1 != "b♭".indexOf( accidental ) ) {
				value -=1;
				beginIndex++;
			} else if ( -1 != "s#♯".indexOf( accidental ) ) {
				value +=1;
				beginIndex++;
			} else if ( -1 != "n♮".indexOf( accidental ) ) {
				beginIndex++;
			}
			String octaveString = toString.substring( beginIndex );
			if (( null != octaveString ) && ( octaveString.length() > 0 )) {
				this.iOctave = Integer.parseInt( octaveString );
			}
		}
		normalize();
	}

	/** Return new note isntance based on new {@link Note( String toString )}
	 */
	public static Note parse( String noteString ) {
		return new Note( noteString );
	}


    // Default notes of octave 0.
	public static final Note C = new Note( Note.Name.C.getValue() );
	public static final Note Cs = new Note( Note.Name.Cs.getValue() );
	public static final Note Db = new Note( Note.Name.Db.getValue() );
	public static final Note D = new Note( Note.Name.D.getValue() );
	public static final Note Ds = new Note( Note.Name.Ds.getValue() );
	public static final Note Eb = new Note( Note.Name.Eb.getValue() );
	public static final Note E = new Note( Note.Name.E.getValue() );
	public static final Note F = new Note( Note.Name.F.getValue() );
	public static final Note Fs = new Note( Note.Name.Fs.getValue() );
	public static final Note Gb = new Note( Note.Name.Gb.getValue() );
	public static final Note G = new Note( Note.Name.G.getValue() );
	public static final Note Gs = new Note( Note.Name.Gs.getValue() );
	public static final Note Ab = new Note( Note.Name.Ab.getValue() );
	public static final Note A = new Note( Note.Name.A.getValue() );
	public static final Note As = new Note( Note.Name.As.getValue() );
	public static final Note Bb = new Note( Note.Name.Bb.getValue() );
	public static final Note B = new Note( Note.Name.B.getValue() );

	public static final Note GuitarLowE = new Note( 2, Note.Name.E.getValue() ); // guitar
	public static final Note GuitarA = Note.plus( GuitarLowE, Interval.fourth ); // guitar
	public static final Note GuitarD = Note.plus( GuitarA, Interval.fourth ); // guitar
	public static final Note GuitarG = Note.plus( GuitarD, Interval.fourth ); // guitar
	public static final Note GuitarB = Note.plus( GuitarG, Interval.third ); // guitar
	public static final Note GuitarHighE = new Note( 4, Note.Name.E.getValue() ); // guitar

	// Used for building intervals
	public Note plus( final Note other ) {
		if ( null == other )
			return this;
		this.iOctave += other.iOctave;
		this.value += other.value;
		normalize();
		return this;
	}
	
	public static Note plus( final Note one, final Note other ) {
		if ( null == other )
			return one;
		if ( null == one )
			return other;
		return new Note( one.iOctave + other.iOctave, one.value + other.value );
	}

	/** Adds given number of diatonic steps. */
	public Note plus( int interval ) {
		if ( 0 == interval ) return this;
		this.value += interval;
		normalize();
		return this;
	}

	/** Adds given number of diatonic steps. */
	public Note plus( Interval interval ) {
		return this.plus( interval.getValue() );
	}

	public static Note plus( final Note one, int interval ) {
		if ( null == one )
			return (new Note( 0, 0 )).plus( interval );
		return (new Note( one )).plus( interval );
	}

	public static Note plus( final Note one, Interval interval ) {
		return Note.plus( one, interval.getValue() );
	}

	public Note minus( final Note other ) {
		if ( null == other )
			return this;
		this.iOctave -= other.iOctave;
		this.value -= other.value;
		normalize();
		return this;
	}
	
	public static Note minus( final Note one, final Note other ) {
		if ( null == other )
			return one;
		if ( null == one )
			return other;
		return new Note( one.iOctave - other.iOctave, one.value - other.value );
	}

	/** Subtracts given number of diatonic steps. */
	public Note minus( int interval ) {
		if ( 0 == interval ) return this;
		this.value -= interval;
		normalize();
		return this;
	}

	/** Subtracts given number of diatonic steps. */
	public Note minus( Interval interval ) {
		return this.minus( interval.getValue() );
	}

	public static Note minus( final Note one, int interval ) {
		if ( null == one )
			return (new Note( 0, 0 )).minus( interval );
		return (new Note( one )).minus( interval );
	}

	public static Note minus( final Note one, Interval interval ) {
		return Note.minus( one, interval.getValue() );
	}

	public int getOctave() {
		return iOctave;
	}
	public void setOctave(int iOctave) {
		this.iOctave = iOctave;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	
	/** Returns number of steps from given root.
	 * Both notes are normalized so the interval is always positive.
	 */
	public int getQuality( final Note root ) {
		if ( null == root )
			throw new IllegalArgumentException( "Need a root to determine note quality." );
		int thisValue = getValue();
		int rootValue = root.getValue();
		while ( rootValue > thisValue ) rootValue -= 12;
		return thisValue - rootValue;		
	}
	
	public String getQualityName( final Note root ) {
		return Note.getQualityName( this, root );
	}
	
	/** Returns a string indicating the interval/quality of this note compared to the given root. 
	 * Examples:
	 *    note="G#" root="F" returns "b3" 
	 *    note="D#" root="F" returns "b7" 
	 *    note="D#" root="B" returns "2" 
	 *    note="F5" root="F2" returns "R" 
	 */
	public static String getQualityName( final Note currentNote, final Note root ) {
		// These names are used rather than interval just to pick a preferred name.
		final String [] intervalNames = { "R", "m2","2","b3","3","4","b5","5","m6","6","b7","7" };
		
		if (null == currentNote) 
			throw new IllegalArgumentException( "Need note to determine quality." );
		if (null == root) 
			throw new IllegalArgumentException( "Need root to determine quality." );
		
		int steps = currentNote.getValue() - root.getValue();
		// System.out.println( "Note=" + currentNote + ", steps=" + steps );
		while (steps < 0)
			steps += 12;
		while (steps >= intervalNames.length)
			steps -= 12;
   		return intervalNames[ steps ];
	}

	@Override
	public int hashCode() {
		return getAbsoluteValue();
	}
	
	@Override
	public boolean equals(Object obj) {
		// Should normalize both values before comparing.
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Note other = (Note) obj;
		if (iOctave != other.iOctave)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	@Override
	@com.fasterxml.jackson.annotation.JsonValue
	/** Return note name, accidental, octave. */
	public String toString() {
		StringBuilder sb = new StringBuilder( Name.getName( value ).toString() );
		if ( 0 != iOctave ) {
			// Add digits as superscript. 
			// String digitString = Integer.toString( iOctave ); // handle digits above 9.
			// for ( int i = 0; i < digitString.length(); i++ ) {
			// 	String digitOrOther = digitString.substring( i, i+1 );
			// 	if ( "-".equals( digitOrOther ))
			// 		sb.append( "-" );
			// 	else
			// 	    sb.append( SUPERSCRIPT[ Integer.parseInt( digitOrOther ) ] );
			// }
			sb.append( Integer.toString( iOctave ));
		}
       return ( sb.toString());   		
	}

	public String toJSON() {
		String json = null;
		try {
			com.fasterxml.jackson.databind.ObjectMapper jsonMapper = 
				new com.fasterxml.jackson.databind.ObjectMapper();
			json = jsonMapper.writeValueAsString(this);
		} catch (com.fasterxml.jackson.core.JsonProcessingException e) {
			// e.printStackTrace();
			json = e.getMessage();
		}
		return json;
	}

	public static Note fromJSON( String json ) {
		Note value = null;
		try {
			com.fasterxml.jackson.databind.ObjectMapper jsonMapper = 
				new com.fasterxml.jackson.databind.ObjectMapper();
			value = jsonMapper.readValue(json, Note.class);
		} catch (Exception e) { // JsonParseExcetpion, JsonMappingException, IOException
			e.printStackTrace();
		}
		return value;
	}

	/** Return note short name, no octave. */
	public String getName() {
		return Name.getName( value ).toString();
	}

	/** Return whether note is sharp/flat or not. */
	public boolean hasAccidental() {
		Name name = Name.getName( value );
		return name.hasAccidental();
	}

	/** Returns difference in diatonic steps. Other classes (e.g. GuitarString) depend on diatonic steps. */
	public int compareTo(Note o) {
		if ( null == o ) return this.getAbsoluteValue();
		return this.getAbsoluteValue() - o.getAbsoluteValue();
	}

	public int getAbsoluteValue() {
		return 12 * iOctave + value;
	}
	
	protected void normalize() {
		// Assures value in the range 0..11.
		while ( this.value >= 12 ) {
			this.iOctave += 1;
			this.value -= 12;			
		}
		while ( this.value < 0 ) {
			this.iOctave -= 1;
			this.value += 12;			
		}
	}
	
	protected int iOctave;
	protected int value;	
}