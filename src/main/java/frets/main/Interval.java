package frets.main;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Encapsulates absolute distance between notes.
 *
* @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public enum Interval {
	r( 0 ),
	root( 0 ),
	unison( 0 ),
	m2( 1 ),
	b2( 1 ),
	minorSecond( 1 ),
	half( 1 ),
	h( 1 ),
	M2( 2 ),
	second( 2 ),
	majorSecond( 2 ),
	whole( 2 ),
	W( 2 ),
	minorThird( 3 ),
	m3( 3 ),
	b3( 3 ),
	wholehalf( 3 ),
	Wh( 3 ),
	skip( 3 ),
	s( 3 ),
	third( 4 ),
	majorThird( 4 ),
	M3( 4 ),
	fourth( 5 ),
	perfectFourth( 5 ),
	p4( 5 ),
	diminishedFifth( 6 ),
	d5( 6 ),
	b5( 6 ),
	tritone( 6 ),
	fifth( 7 ),
	perfectFifth( 7 ),
	p5( 7 ),
	augmentedFifth( 8 ),
	a5( 8 ),
	minorSixth( 8 ),
	m6( 8 ),
	b6( 8 ),
	sixth( 9 ),
	majorSixth( 9 ),
	M6( 9 ),
	minorSeventh( 10 ),
	m7( 10 ),
	b7( 10 ),
	seventh( 11 ),
	majorSeventh( 11 ),
	M7( 11 ),
	eigth( 12 ),
	octave( 12 ),
	perfectOctave( 12 ),
	o( 12 ),
	ninth( 14 ),
	augmentedNinth( 15 ),
	tenth( 16 ),
	eleventh( 17 ),
	augmentedEleventh( 18 ),
	twelfth( 19 ),
	thirteenth( 20 ),
	doubleOctave( 24 );

	public static Map<String, Interval> commonNames = new HashMap<String, Interval>();
	static {
		commonNames.put( "R", root );
		commonNames.put( "1", root );
		commonNames.put( "2", second );
		commonNames.put( "3", third );
		commonNames.put( "4", fourth );
		commonNames.put( "5", fifth );
		commonNames.put( "#5", augmentedFifth );
		commonNames.put( "6", sixth );
		commonNames.put( "7", seventh );
		commonNames.put( "8", octave );
		commonNames.put( "9", ninth );
		commonNames.put( "#9", augmentedNinth );
		commonNames.put( "10", tenth );
		commonNames.put( "11", eleventh );
		commonNames.put( "#11", augmentedEleventh );
		commonNames.put( "12", twelfth );
		commonNames.put( "13", thirteenth );
	}
	
	private Interval(int value ) {
       this.value = value;
    }
	public int getValue() { return value; }
	public static Interval getInterval( String name ) {
		if (( name == null ) || ( name.length() < 1 ))
			throw new IllegalArgumentException( "Interval name illegal, name=" + name );
		
		for ( Interval interval : EnumSet.range( r, doubleOctave )) {
			if ( interval.toString().equals( name ) )
				return interval;
		}
        throw new IllegalArgumentException( "Interval name not found, name=" + name );
	}
	public static Interval getInterval( int value ) {
		if (( value < unison.getValue() ) || ( value > doubleOctave.getValue()))
		   throw new IllegalArgumentException( "Interval value out of range, value=" + value );
		for ( Interval interval : EnumSet.range( r, doubleOctave )) {
			if ( value == interval.getValue())
				return interval;
		}
        throw new IllegalArgumentException( "Interval not found, value=" + value );
	}

	/** Go from params C W-W-h to list of intervals.
	 *  Also works with any interval name e.g. C root third fifth
	 *  Also works with absolute values e.g. C 1 3 5 */
	public static Interval [] getIntervals( String formula ) {
	   String delimiter = " ";
	   if (formula.contains("-"))
	      delimiter = "-";
	   return getIntervals( formula, delimiter );
	}

	/** Go from params C W-W-h to list of intervals.
	 *  Also works with any interval name e.g. C root third fifth
	 *  Also works with absolute values e.g. C 1 3 5 */
	public static Interval [] getIntervals( String formula, String delimiter ) {
	   if (null == formula)
		   return new Interval [ 0 ];
	   List<Interval> intervals = new LinkedList<Interval>();

	   StringTokenizer st = new StringTokenizer( formula, delimiter );
	   while( st.hasMoreTokens() ) {
		   String token = st.nextToken();
		   Interval common = commonNames.get( token );
		   if ( null != common ) {
			   // Attempt to decode from common names.
			   intervals.add( common );
		   } else {
			   // Attempt to decode as name.
			   intervals.add( Interval.getInterval( token ) );			   
		   }
	   }

	   return intervals.toArray( new Interval[intervals.size()] );
	}


	private int value;
}