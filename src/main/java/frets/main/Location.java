package frets.main;

/**
 * Represents locations "string#-fret#" on a fretboard.
 * The strings are numbered 0 (lowest frequency string) to N.
 * The frets are numbered 0 (nut) to N. 
 * 
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class Location implements Comparable<Location> {
	public final static String DELIM = "-";
	
	public Location() {
		this.stringi = 0;
		this.freti = 0;
		this.hashi = absoluteValue();
	}

	public Location(int stringi, int freti) {
		this.stringi = stringi;
		this.freti = freti;
		this.hashi = absoluteValue();
	}
	
	/** Make a new location, clone, without touching the original. */
	public Location( final Location other ) {
		this.stringi = other.stringi;
		this.freti = other.freti;
		this.hashi = absoluteValue();
	}

	/** Make a new instance from a String produced by toString of this class. */
	public Location( String fromString ) {
		Location other = parseString( fromString );
		this.stringi = other.getString();
		this.freti = other.getFret();
		this.hashi = absoluteValue();
	}
	
	public int getString() {
		return stringi;
	}

	public void setString( int stringi ) {
		if ( getString() != stringi ) {
		   this.stringi = stringi;
		   this.hashi = absoluteValue();
		}
	}

	public int getFret() {
		return freti;
	}

	public void setFret( int freti ) {
		if ( getFret() != freti ) {
		   this.freti = freti;
		   this.hashi = absoluteValue();
		}
	}

	/**
	 * Return a note on the fretboard that corresponds to this location. 
	 * Returns null if the string or fret does not exist.
	 */
	public Note getNote(final Fretboard fretboard) {
		if (null == fretboard)
			return null;
		if ((0 > stringi) || (0 > freti))
			return null;
		if (stringi >= fretboard.getStringCount())
			return null;

		GuitarString guitarString = fretboard.getString(stringi);
		if (null == guitarString)
			return null;
		if (freti >= guitarString.getMaxFret())
			return null;
		return guitarString.getNote(freti);
	}

	@Override
	public int hashCode() {
		return hashi;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (null == object || !(getClass().isInstance(object)))
			return false;

		Location other = getClass().cast(object);
		return this.hashi == other.hashi;
	}

	
	@Override
	@com.fasterxml.jackson.annotation.JsonValue
	/** Return a String representation of the location. */
	public String toString() {
		// Tried various forms. The last seemed most readable in a big list.
		// (0,7)
		// (0>7),(2>1),(1>7)
		// 0>7,2>1,1>7
		// 0+7,2+1,1+7
		// 0|7,2|1,1|7
		// 0-7,2-1,1-7
		return stringi + DELIM + freti;
	}
	
	/** Make a new instance from a String produced by toString of this class. */
	public static Location parseString( String fromString ) {		
		if ((null==fromString) || (fromString.length() < 1 ))
			throw new IllegalArgumentException( "Bad input string=" + fromString );
		String [] values = fromString.split( "[\\s" + DELIM + "\"]" ); // Can return "" when these match 
		// System.out.println( "Location string=\"" + fromString + "\", value count=" + values.length );
		if ((null==values) || (values.length < 2))
			throw new IllegalArgumentException( "Bad parse of string=" + fromString );
		int params = 0;
		int stringi = 0;
		int freti = 0;
		for ( int i = 0; i < values.length; i++ ) {
			if ( !values[ i ].isEmpty() ) {
				if ( params == 0 ) {
			       stringi = Integer.parseInt(values[ i ]); 
			       params++;
				} else if (params == 1) {
			       freti = Integer.parseInt(values[ i ]);
			       params++;
				} else
				   // More than 2 params
                   throw new IllegalArgumentException( "Bad parse of string=" + fromString );				
			}
		}
		// System.out.println( "   param count=" + params );
		if (params < 2)
			throw new IllegalArgumentException( "Bad parse of string=" + fromString );
		
		return new Location( stringi, freti );
	}

	/**
	 * Returns a String which names the string and fret. 
	 * For low E string fret 3:
	 *   normal toString: "0-3"
	 *   this notation:   "E2-3"
	 * 
	 */
	public String toStringFret(final Fretboard fretboard) {
		if (null == fretboard)
			return null;
		if ((0 > stringi) || (0 > freti))
			return null;
		if (stringi >= fretboard.getStringCount())
			return null;

		GuitarString guitarString = fretboard.getString(stringi);
		if (null == guitarString)
			return null;
		if (freti >= guitarString.getMaxFret())
			return null;
		
		String openString = guitarString.getOpenNote().toString();
		return openString + DELIM + freti;
	}

	/** Make a new instance from a String produced by toString of this class. */
	public static Location parseStringFret( String fromString, final Fretboard fretboard ) {		
		if ((null==fromString) || (fromString.length() < 1 ))
			throw new IllegalArgumentException( "Bad input string=" + fromString );
		String [] values = fromString.split( "[\\s" + DELIM + "\"]" ); // Can return "" when these match 
		// System.out.println( "Location string=\"" + fromString + "\", value count=" + values.length );
		if ((null==values) || (values.length < 2))
			throw new IllegalArgumentException( "Bad parse of string=" + fromString );
		int params = 0;
		int stringi = 0;
		int freti = 0;
		for ( int i = 0; i < values.length; i++ ) {
			if ( !values[ i ].isEmpty() ) {
				if ( params == 0 ) {
         		   String stringString = values[ i ];
			       Note stringNote = Note.parse( stringString );
			       while ( stringi < fretboard.getStringCount() ) {
			    	   if ( stringNote.equals( fretboard.get( stringi ).getOpenNote() ))
			    	      break;			    	   
			       }
			       if ( stringi == fretboard.getStringCount() ) {
			    	   throw new IllegalArgumentException( "Could not match string=" + stringString + " of " + fromString + " to an open string." );
			       }
			       params++;
				} else if (params == 1) {
			       freti = Integer.parseInt(values[ i ]);
			       params++;
				} else
				   // More than 2 params
                   throw new IllegalArgumentException( "Bad parse of string=" + fromString );				
			}
		}
		// System.out.println( "   param count=" + params );
		if (params < 2)
			throw new IllegalArgumentException( "Bad parse of string=" + fromString );
		
		return new Location( stringi, freti );
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

	public static Location fromJSON( String json ) {
		Location value = null;
		try {
			com.fasterxml.jackson.databind.ObjectMapper jsonMapper = 
				new com.fasterxml.jackson.databind.ObjectMapper();
			value = jsonMapper.readValue(json, Location.class);
		} catch (Exception e) { // JsonParseExcetpion, JsonMappingException, IOException
			e.printStackTrace();
		}
		return value;
	}

	public int compareTo(Location o) {
		return this.hashi - o.hashi;
	}

	/* Provides a total ordering calculation. */
	public int absoluteValue() {
		return 101 * stringi + freti;
	}
	
	protected int stringi;
	protected int freti;
	protected int hashi;
}