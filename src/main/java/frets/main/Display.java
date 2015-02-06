package frets.main;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Dimension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import frets.util.FilenameRegExFilter;

/**
 * Encapsulates display variables.
 * Stuff that might affect display.
 * <p>
 * Some instance vars are fretboard oriented: orientation, hand, display area, info type.
 * Some instance vars are character oriented: characters for strings, frets, spaces.
 * Some instance vars are raster oriented
 *
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class Display implements SimpleProperties<Display> {
	public static final String HORIZONTAL_NAME = "Basic horizontal";
	public static final String VERTICAL_NAME = "Basic vertical";
	
	// Fretboard oriented, common
	public enum Orientation {
		HORIZONTAL,
		VERTICAL
	}
	public Orientation orientation = Orientation.HORIZONTAL; 
	
	/** Used to align markers toward the top, center or bottom of the guitar/head/fret. */
	public enum HAlign {
		LEFT,
		CENTER,
		RIGHT
	}
	public enum VAlign {
		TOP,
		CENTER,
		BOTTOM
	}
	public VAlign headAlign = VAlign.BOTTOM;
	public VAlign fretAlign = VAlign.BOTTOM;
	
	public enum Hand {
		RIGHT,
		LEFT
	}
	public Hand hand = Hand.RIGHT;
	
	public enum InfoType {
		PLAIN,
		FINGERNUMBER,
		FINGERLATIN,
		INTERVAL,
		NAME;
	}
	public InfoType infoType = InfoType.PLAIN;
	public Note root; // for interval calculations
	public int handPosition; // for finger calculations
	
	// Markings for not played strings.
	// Use EnumSet to set one or more styles, e.g. EnumSet.of( NotPlayed.OPEN, NotPlayed.FIRST )	
	public enum NotPlayedLocation {
		HEAD,
		FIRST,
	}
	public Set<NotPlayedLocation> notPlayed = EnumSet.of( NotPlayedLocation.HEAD );
	public boolean openStringDisplay = false; // display string character above nut (otherwise space)	

	// Numberings for frets
	// Use EnumSet to set one or more styles, e.g. EnumSet.of( FIRSTLEFT, FIRSTRIGHT )	
	public enum FretNumbering {
		FIRSTLEFT,
		FIRSTRIGHT,
	}
	public Set<FretNumbering> fretNumbering = EnumSet.of( FretNumbering.FIRSTLEFT, FretNumbering.FIRSTRIGHT );
	public boolean fretNumberingDisplayOpen = false; // display fret number when 0?

	// Display area - determines region of fretboard to display.
	// May set to none, absolute, or fret location area (e.g. min and max notes)
	public enum DisplayAreaStyle {
		NONE, ABSOLUTE, MINAPERTURE, MAXAPERTURE, MAXLOCATION, MAXFRETBOARD
	}
	public DisplayAreaStyle displayAreaStyle = DisplayAreaStyle.NONE;
	public Location displayAreaMin;
	public Location displayAreaMax;
	
	/** Turns off any display window sizing. The full fretboard will be displayed. */
	public void setDisplayAreaStyleNone() {
		displayAreaStyle = DisplayAreaStyle.NONE;
		displayAreaMin = null;
		displayAreaMax = null;	   	
	}

	/** Sets the display window to the given min and max string and fret. */
	public void setDisplayAreaStyleAbsolute( final Location displayAreaMin, final Location displayAreaMax ) {
		displayAreaStyle = DisplayAreaStyle.ABSOLUTE;
		this.displayAreaMin = displayAreaMin;
		this.displayAreaMax = displayAreaMin;	   	
	}

	/** Sets the display window to the lowest fret in locations, min( minLocFret + aperture, maxFret ). */
	public void setDisplayAreaStyleMinAperture( final Fretboard fretboard, final LocationList locations, int fretAperture ) {
		// locations may be null
		displayAreaStyle = DisplayAreaStyle.MINAPERTURE;
		displayAreaMin = new Location();
		displayAreaMax = new Location();
		displayAreaMin.setString(0);
	    displayAreaMax.setString(fretboard.getStringCount() - 1);
	    displayAreaMin.setFret(0);
		if (( null != locations ) && ( locations.size() > 0)) 
			displayAreaMin.setFret( locations.minFret() );
		else
			displayAreaMin.setFret( 0 );
	    displayAreaMax.setFret( displayAreaMin.getFret() + fretAperture );
	    // Window maxes out at max fret.
	    if ( displayAreaMax.getFret() > fretboard.getMaxFret() ) {
		    displayAreaMin.setFret( fretboard.getMaxFret() - fretAperture );
		    displayAreaMax.setFret( fretboard.getMaxFret() );
	    }
	}

	/** Sets the display window to the highest fret in locations, max( maxLocFret - aperture, minFret). */
	public void setDisplayAreaStyleMaxAperture( final Fretboard fretboard, final LocationList locations, int fretAperture ) {
		displayAreaStyle = DisplayAreaStyle.MAXAPERTURE;
		displayAreaMin = new Location();
		displayAreaMax = new Location();
		displayAreaMin.setString(0);
	    displayAreaMax.setString(fretboard.getStringCount() - 1);
	    displayAreaMax.setFret( fretboard.getMaxFret() );
		if ( null != locations ) 
			displayAreaMax.setFret( locations.maxFret() );
	    displayAreaMin.setFret( displayAreaMax.getFret() - fretAperture );
	    // Window mins out at min fret.
	    if ( displayAreaMin.getFret() < 0 ) {
		    displayAreaMin.setFret( 0 );
		    displayAreaMax.setFret( fretAperture );
	    }
	}
	
	/** Sets the display window to the given min and max fretboard strings and location frets. */
	public void setDisplayAreaStyleMaxLocation( final Fretboard fretboard, final LocationList locations ) {
		displayAreaStyle = DisplayAreaStyle.MAXLOCATION;
		displayAreaMin = new Location();
		displayAreaMax = new Location();
		displayAreaMin.setString(0);
	    displayAreaMax.setString(fretboard.getStringCount() - 1);
	    displayAreaMin.setFret(0);
		if ( null != locations ) {
			displayAreaMin.setFret( locations.minFret() );
			displayAreaMax.setFret( locations.maxFret() );
		}
	}

	/** Sets the display window to the size of the fretboard. */
	public void setDisplayAreaStyleMaxFretboard( final Fretboard fretboard ) {
		displayAreaStyle = DisplayAreaStyle.MAXFRETBOARD;
		displayAreaMin = new Location();
		displayAreaMax = new Location();
		displayAreaMin.setString(0);
	    displayAreaMax.setString(fretboard.getStringCount() - 1);
	    displayAreaMin.setFret(0);
	    displayAreaMax.setFret(fretboard.getMaxFret());
	}

	/** Returns the string span of the display area. */
	public int getDisplayAreaStringAperture() {
		if (null==displayAreaMin) return 0;
		int minString = displayAreaMin.getString();
		if (null==displayAreaMax) return 0;
		int maxString = displayAreaMax.getString();
		return maxString - minString;		
	}
	
	/** Returns the fret span of the display area. */
	public int getDisplayAreaFretAperture() {
		if (null==displayAreaMin) return 0;
		int minFret = displayAreaMin.getFret();
		if (null==displayAreaMax) return 0;
		int maxFret = displayAreaMax.getFret();
		return maxFret - minFret;				
	}
	
	public boolean showEnharmonicVariations = false;
	public boolean showOctaveVariations = false;
	
	
	// Ascii graphics
	public static final String NL = System.getProperty( "line.separator" );
	public String spaceString = " ";
	public String plainNoteString = "o";
	public String stringString = ""; // Set to something (e.g. "-" or "|") to turn on
	public String fretString = "|"; // Set to something (e.g. "-" or "|") to turn on
	public String nutString = "||"; // Set to something (e.g. "||" or "=") to turn on
	// Extra space for notes/dots on a fret
	public int headSpace = 1; // space for head
	public int fretSpace = 1; // space for fret
	public int noteSpace = 1; // space for note
	public String notPlayedString = "x"; // Set to something (e.g. "X" or "x") to turn on
	

	// Raster graphics
	// Must decide if insets contain fret number and open string info.
	public Insets insets = new Insets( 15, 10, 8, 10  );  // top, left, bottom, right
	public Color backgroundColor = new Color( 245, 245, 220, 0x00 ); // support alpha transparency
	public Color fretboardColor = new Color( 210, 180, 140 );
	public Color nutColor = Color.DARK_GRAY;
	public int nutThickness = 6;
	public Color fretColor = Color.GRAY;
	public int fretThickness = 3;
	public Color stringColor = Color.DARK_GRAY;
	public int stringThickness = 2;
	public Color defaultNoteColor = Color.BLACK;
	public Color [] intervalColors = new Color [] {
	   new Color( 255,   0,   0 ), // root 
	   new Color( 255,  85,   0 ), // d2 
	   new Color( 255, 136,   0 ), // 2 
	   new Color( 255, 186,   0 ), // m
	   new Color( 255, 238,   0 ), // 3
	   new Color(   0, 170,   0 ), // 4
	   new Color(   0, 119, 119 ), // b5
	   new Color(   0,  80, 238 ), // 5
	   new Color(  53,   0, 171 ), // b6
	   new Color(  85,   0,  36 ), // 6
	   new Color( 153,   0, 136 ), // b7
	   new Color( 204,   0, 170 ), // 7
	};
	public Color defaultNoteTextColor = Color.WHITE;
	public Color [] intervalTextColors = new Color [] {
       new Color( 255, 255, 255 ), // root 
	   new Color( 255, 255, 255 ), // d2 
	   new Color( 255, 255, 255 ), // 2 
	   new Color( 255, 255, 255 ), // m
	   new Color( 255, 255, 255 ), // 3
	   new Color( 255, 255, 255 ), // 4
	   new Color( 255, 255, 255 ), // b5
	   new Color( 255, 255, 255 ), // 5
	   new Color( 255, 255, 255 ), // b6
	   new Color( 255, 255, 255 ), // 6
	   new Color( 255, 255, 255 ), // b7
	   new Color( 255, 255, 255 ), // 7
	};
	public Color noteShadowColor = Color.DARK_GRAY;
	public boolean noteShadows = true;
	public Color fretNumberColor = Color.DARK_GRAY;
	
	// Support ghosted enharmonics and octaves
	public Color enharmonicAlpha = new Color( 0x00, 0x00, 0x00, 0x55 );
	public Color octavesAlpha = new Color( 0x00, 0x00, 0x00, 0x20 );

	// Support non-rectangular fretboard
	public boolean wideningStrings = false; // nut to bridge widening
	public boolean narrowingFrets = false; // nut to bridge narrowing
	
	// All measurements from Fender American Deluxe Telecaster 2012 
	public Dimension nutSizeMM = new Dimension( 42, 3 ); 
	public int fretThicknessMM = 3;
	public int [] stringSpacingMM = new int [] { 35, 48, 53 }; // nut, fret 22, bridge (center to center)
	public int [] fretDistAbsMM = new int [] { // measured from nut center to fret center
		38,	72, 104, 135, 164, 191, 217, 241, 264, 285,
		305, 325, 343, 360, 376, 392, 406, 420, 433, 445,
		456, 467
	};
		
	/** Pad takes a value, space, alignment, handedness, and orientation to produce a string.
	 * Examples: (with spaces and strings)
	 * 1) Note A, width 3, TOP, RIGHT, HORI: A__    A--
	 * 1) Note A, width 3, BOT, RIGHT, HORI: _A_    -A-
	 * 1) Note A, width 3, TOP, RIGHT, VERT: A_ + __ + __   A_ + |_ + |_
	 * 1) Note A, width 3, BOT, RIGHT, VERT: __ + __ + A_   |_ + |_ + A_
	 */
	public static String pad( String value, int noteSpace, int space, VAlign vAlign, Hand hand, 
			Orientation orient, String stringString, String stringSpace ) {
		StringBuilder sb = new StringBuilder();
		String pad = stringSpace;
		if (( null != stringString ) && ( stringString.length() > 0 )) pad = stringString; 
		if ( orient == Orientation.HORIZONTAL ) {
			HAlign hAlign = HAlign.CENTER;
			if ( hand == Hand.RIGHT) {
				if ( vAlign == VAlign.TOP )
					hAlign = HAlign.LEFT;
				else if ( vAlign == VAlign.BOTTOM )
					hAlign = HAlign.RIGHT;
			} else if (hand == Hand.LEFT) {
				if ( vAlign == VAlign.TOP )
					hAlign = HAlign.RIGHT;
				else if ( vAlign == VAlign.BOTTOM )
					hAlign = HAlign.LEFT;				
			}
			// Pad notes/spaces horizontally.
			while ( value.length() < noteSpace )
				value += pad;
			sb.append(value);
			while( sb.length() < space ) {				
				if ( hAlign == HAlign.RIGHT ) { sb.insert( 0, pad ); }
				if ( hAlign == HAlign.CENTER ) { 
					sb.insert( 0, pad ); 
					if ( sb.length() < space ) sb.append( pad ); 
				}
				if ( hAlign == HAlign.LEFT ) { sb.append( pad ); }
			}
		} else if (orient == Orientation.VERTICAL ) {
			int height = 1;
			// Pad notes/spaces horizontally.
			String horiPad = " ";
			if ((null != stringSpace) && ( stringSpace.length() > 0 ))
			   horiPad = stringSpace;
			while ( value.length() < noteSpace )
				value += horiPad;
			value += NL;
			while ( pad.length() < noteSpace )
				pad += horiPad;
			pad += NL;
			sb.append( value );
			while ( height < space ) {
				if ( vAlign == VAlign.BOTTOM ) { sb.insert( 0, pad ); height++; }
				if ( vAlign == VAlign.CENTER ) { 
					sb.insert( 0, pad ); height++;
					if ( height < space ) { sb.append( pad ); height++; } 
				}
				if ( vAlign == VAlign.TOP ) { sb.append( pad ); height++; }
			}
		}
		return sb.toString();
	}

	// Constructors
	/** Create a display with default values. */
	public Display() {
	}

	/** Create a display populated with values from other. */
	public Display( final Display other ) {
		super();
		if ( null == other ) return;
		this.orientation = other.orientation;
		this.headAlign = other.headAlign;
		this.fretAlign = other.fretAlign;
		this.hand = other.hand;
		this.infoType = other.infoType;
		if ( null != other.root )
			this.root = new Note( other.root );
		this.handPosition= other.handPosition;
		this.notPlayed = EnumSet.copyOf( other.notPlayed );
		this.notPlayedString= other.notPlayedString;
		this.openStringDisplay = other.openStringDisplay;
		this.fretNumbering = EnumSet.copyOf( other.fretNumbering );
		this.fretNumberingDisplayOpen = other.fretNumberingDisplayOpen;
		this.displayAreaStyle = other.displayAreaStyle;
		if ( null != other.displayAreaMin )
			this.displayAreaMin = new Location( other.displayAreaMin );
		if ( null != other.displayAreaMax )
			this.displayAreaMax = new Location( other.displayAreaMax );
		// Ascii graphics
		this.spaceString = other.spaceString;
		this.plainNoteString = other.plainNoteString;
		this.stringString = other.stringString;
		this.fretString = other.fretString;
		this.nutString = other.nutString;
		// Extra space for notes/dots on a fret
		this.headSpace = other.headSpace;
		this.fretSpace = other.fretSpace;
		this.noteSpace = other.noteSpace;
		// Raster graphics
		if ( null != other.insets )
			this.insets = new Insets( other.insets.top,  other.insets.left,  other.insets.bottom,  other.insets.right );
		// Must decide if insets contain fret number and open string info.
		if ( null != other.backgroundColor )
			this.backgroundColor = new Color( other.backgroundColor.getRGB(), true ); // copy with alpha
		if ( null != other.fretboardColor )
			this.fretboardColor = new Color( other.fretboardColor.getRGB(), true );
		if ( null != other.nutColor )
			this.nutColor = new Color( other.nutColor.getRGB(), true );
		this.nutThickness = other.nutThickness;
		if ( null != other.fretColor )
			this.fretColor = new Color( other.fretColor.getRGB(), true );
		this.fretThickness = other.fretThickness;
		if ( null != other.stringColor )
			this.stringColor = new Color( other.stringColor.getRGB(), true );
		this.stringThickness = other.stringThickness;
		if (null != other.intervalColors) 
			this.intervalColors = other.intervalColors.clone();
		if (null != other.intervalTextColors) 
			this.intervalTextColors = other.intervalTextColors.clone();
		if ( null != noteShadowColor )
			this.noteShadowColor = new Color( other.noteShadowColor.getRGB(), true );
		this.noteShadows = other.noteShadows;
		if ( null != fretNumberColor )
			this.fretNumberColor = new Color( other.fretNumberColor.getRGB(), true );
	}

	// Object overrides
	@Override
	public int hashCode() {
		final int prime = 19;
		// Currently, this is a partial compare. Some fields are commented.
		int result = prime;
		result += prime * orientation.hashCode();
		result += prime * headAlign.hashCode();
		result += prime * hand.hashCode();
		result += prime * infoType.hashCode();
		result += prime * notPlayed.hashCode();
		result += prime * notPlayedString.hashCode();
		result += prime * fretNumbering.hashCode();
		result += prime * displayAreaStyle.hashCode();
		if ( null != displayAreaMin )
				result += prime * displayAreaMin.hashCode();
		if ( null != displayAreaMax )
		result += prime * displayAreaMax.hashCode();
		// Ascii graphics
		// public static final String NL = System.getProperty( "line.separator" );
		result += prime * spaceString.hashCode();
		// public String plainNoteString = "o";
		result += prime * stringString.hashCode();
		result += prime * fretString.hashCode();
		result += prime * nutString.hashCode();
		// public int headSpace = 1; // space for head
		// public int fretSpace = 1; // space for fret
		// public int noteSpace = 1; // space for note
		// Raster graphics
		// Must decide if insets contain fret number and open string info.
		result += prime * insets.hashCode();
		result += prime * backgroundColor.hashCode();
		result += prime * fretboardColor.hashCode();
		// public Color nutColor = Color.DARK_GRAY;
		// public int nutThickness = 5;
		// public Color fretColor = Color.GRAY;
		// public int fretThickness = 3;
		result += prime * stringColor.hashCode();
		// public int stringThickness = 2;
		// public Color [] intervalColors;
		// public Color [] intervalTextColors;
		// public Color noteShadowColor = Color.DARK_GRAY;
		// public boolean noteShadows = true;
		// public Color fretNumberColor = Color.DARK_GRAY;
		
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
		Display other = (Display) obj;
		// test fields
		if ( this.orientation != other.orientation ) return false;
		if ( this.headAlign != other.headAlign ) return false;
		if ( this.fretAlign != other.fretAlign ) return false;
		if ( this.hand != other.hand ) return false;
		if ( this.infoType != other.infoType ) return false;
		if (!nullSafeEquals( this.root, other.root )) return false;
		if ( this.handPosition != other.handPosition ) return false;
		if (!nullSafeEquals( this.notPlayed, other.notPlayed )) return false;
		if ( this.notPlayedString != other.notPlayedString ) return false;
		if ( this.openStringDisplay != other.openStringDisplay ) return false;
		if (!nullSafeEquals( this.fretNumbering, other.fretNumbering )) return false;
		if ( this.fretNumberingDisplayOpen != other.fretNumberingDisplayOpen ) return false;
		if ( this.displayAreaStyle != other.displayAreaStyle ) return false;
		if (!nullSafeEquals( this.displayAreaMin, other.displayAreaMin )) return false;
		if (!nullSafeEquals( this.displayAreaMax, other.displayAreaMax )) return false;
		// Ascii graphics
		if ( this.spaceString != other.spaceString ) return false;
		if ( this.plainNoteString != other.plainNoteString ) return false;
		if ( this.stringString != other.stringString ) return false;
		if ( this.fretString != other.fretString ) return false;
		if ( this.nutString != other.nutString ) return false;
		// Extra space for notes/dots on a fret
		if ( this.headSpace != other.headSpace ) return false;
		if ( this.fretSpace != other.fretSpace ) return false;
		if ( this.noteSpace != other.noteSpace ) return false;
		// Raster graphics
		if (!nullSafeEquals( this.insets, other.insets )) return false;
		// Must decide if insets contain fret number and open string info.
		if (!nullSafeEquals( this.backgroundColor,  other.backgroundColor )) return false;
		if (!nullSafeEquals( this.fretboardColor, other.fretboardColor )) return false;
		if (!nullSafeEquals( this.nutColor, other.nutColor )) return false;
		if ( this.nutThickness != other.nutThickness ) return false;
		if (!nullSafeEquals( this.fretColor, other.fretColor )) return false;
		if ( this.fretThickness != other.fretThickness ) return false;
		if (!nullSafeEquals( this.stringColor,  other.stringColor )) return false;
		if ( this.stringThickness != other.stringThickness ) return false;
		// this.intervalColors = other.intervalColors.clone();
		// this.intervalTextColors = other.intervalTextColors.clone();
		if (!nullSafeEquals( this.noteShadowColor, other.noteShadowColor )) return false;
		if ( this.noteShadows != other.noteShadows ) return false;
		if (!nullSafeEquals( this.fretNumberColor, other.fretNumberColor )) return false;
		return true;
	}

   public static boolean nullSafeEquals(Object o1, Object o2) {
      if (o1 == o2) return true;
      if (o1 == null || o2 == null) return false;
      if (o1.equals(o2)) return true;
      return false;
   }

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder( "Display[" );
		// Display field information
		sb.append( "orientation=" + orientation );
		sb.append( ",hand=" + hand );
		sb.append( ",infoType=" + infoType );
		sb.append( "]" );
		return ( sb.toString());   		
	}

	/** Higher string returns higher value compared to others. */
	public int compareTo(Display o) {
		return this.hashCode() - o.hashCode();
	}

	// SimpleProperties implementation
	protected String metaName = "Default display options";
	protected String metaDescription = "Default display options from code, not loaded from resource.";
	protected String metaLocation = "classpath:frets.main.Display";
	protected static Map<String,Display> propertiesMap; // static collection for all these objects.
	protected static Display instance = new Display();
	
	public String getMetaName() {
		return metaName;
	}
	public String getMetaDescription() {
		return metaDescription;
	}
	public String getMetaLocation() {
		return metaLocation;
	}
	public void populateFromFile( String fileName ) throws IOException {
		// Read properties file. 
		Properties properties = new Properties();
		properties.load(new FileInputStream( fileName ));

		// Convert from properties to Display bean
		for ( Iterator<Object> it = properties.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			// System.out.println( "Property key=" + key + ", value=\"" + properties.getProperty( key ) + "\"" );
			String value = properties.getProperty( key );
			if ( "display.space".equals( key ) ) {
				this.spaceString = value;	
			} else if ( "display.name".equals( key )) {
				this.metaName = value;
			} else if ( "display.description".equals( key )) {
				this.metaDescription = value;
			} else if ( "display.plainNote".equals( key )) {
				this.plainNoteString = value;
			} else if ( "display.orientation".equals( key ) ) {
				this.orientation = Display.Orientation.valueOf( value );	
			} else if ( "display.hand".equals( key ) ) {
				this.hand = Display.Hand.valueOf( value );	
			} else if ( "display.note".equals( key ) ) {
				this.infoType = InfoType.valueOf( value ); 
			} else if ( "display.string".equals( key )) {
				this.stringString = value;
			} else if ( "display.fret".equals( key )) {
				this.fretString = value;
			} else if ( "display.nut".equals( key )) {
				this.nutString = value;
			} else if ( "display.notPlayedLocation".equals( key )) {
				this.notPlayed = EnumSet.noneOf( NotPlayedLocation.class );
				StringTokenizer st = new StringTokenizer( value, ",");
				while( st.hasMoreTokens() ) {
					String token = st.nextToken();
					this.notPlayed.add( Display.NotPlayedLocation.valueOf( token ));
				}				
			} else if ( "display.notPlayedString".equals( key )) {
				this.notPlayedString = value;
			} else if ( "display.fretNumbering".equals( key )) {
				this.fretNumbering = EnumSet.noneOf( FretNumbering.class );
				StringTokenizer st = new StringTokenizer( value, ",");
				while( st.hasMoreTokens() ) {
					String token = st.nextToken();
					this.fretNumbering.add( Display.FretNumbering.valueOf( token ));
				}				
			} else if ( "display.fret.align".equals( key ) ) {
				this.fretAlign = VAlign.valueOf( value ); 
			} else if ("display.fret.space".equals( key )) { 
				this.fretSpace = Integer.parseInt( value );
			} else if ( "display.head.align".equals( key ) ) {
				this.headAlign = VAlign.valueOf( value ); 
			} else if ("display.head.space".equals( key )) { 
				this.headSpace = Integer.parseInt( value );
			} else if ("display.note.space".equals( key )) { 
				this.noteSpace = Integer.parseInt( value );
			} else {
				throw new IllegalArgumentException( "Key \"" + key + "\" not handled, value=\"" + value + "\"" );
			} 
		}
	}
	
	/** Opens a file at the given name, and reads all the properties into an object. */
	public Display readFromFile( String fileName ) throws IOException {
		// TODO Need to create factory or surface static reader.
		return Display.read( fileName );
	}

	/** Opens a file at the given name, and reads all the properties into an object. */
	public static Display read( String fileName ) throws IOException {
		Display mock = new Display();
		mock.metaLocation = fileName;
		mock.populateFromFile( fileName );		
		return mock;
	}

	/** Opens a path at the given name, attempts to read files from there.
	 *  Use optional filter as a java.io.FilenameFilter. */
	public List<Display> readFromPath( String pathName, String filterString ) throws IOException {
		File dir = new File( pathName );
		if (!dir.isDirectory() )
			throw new IOException( "Path must be directory, path=" + pathName );
		
		File [] files = null;
		if (null != filterString && (filterString.length() > 0)) {
			FilenameFilter filter = new FilenameRegExFilter( filterString );
			files = dir.listFiles( filter );
		} else {
			files = dir.listFiles();   			
		}
		
		List<Display> list = new LinkedList<Display>();    		
		for ( File file : files ) {
           list.add(  readFromFile(file.getPath()) );			
		}
		return list;
	}

	/** Returns an object fretboard that has been loaded from a
	 *  central repository or properties list. */
	public Display getInstance( String shortName ) {
		if ( null == propertiesMap ) {
			// Lazy instantiation
			try {
				propertiesMap = loadProperties("src/main/resources/frets/main/", "display.*.properties");
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return propertiesMap.get( shortName );
	}

	/** Performs the loading of configured objects from the given location. */
	public Map<String,Display> loadProperties(String pathName, String filterString) throws IOException {
		   Map<String,Display> propertiesMap = new HashMap<String,Display>();
		   List<Display> propertiesList = readFromPath(pathName,filterString);
		   for (Display display : propertiesList) {
			   propertiesMap.put(display.getMetaName(),display);
		   }
		   return propertiesMap;	       		
	}
}