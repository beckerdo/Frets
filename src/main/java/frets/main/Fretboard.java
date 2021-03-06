package frets.main;

import static frets.main.Display.NL;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import frets.main.Display.Orientation;
import frets.util.FilenameRegExFilter;

/**
 * Encapsulates a fretboard.
 * A fretboard is a list of 0 or more guitar strings.
 * <p>
 * Also includes some static factory methods for pulling standard fretboards
 * from a properties repository.
 * There is a singleton fretboard in memory which is the
 * last fretboard retrieved with {@link Fretboard#getInstance(String shortName)}
 *
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class Fretboard implements List<GuitarString>, SimpleProperties<Fretboard> {

	/** Standard, out-of-the-box fretboard tuning name. */
	public static final String STANDARD = "Guitar, Standard";
	/** Standard, out-of-the-box fretboard tuning name. */
	public static final String OPEN_D = "Guitar, Open D";
	/** Standard, out-of-the-box fretboard tuning name. */
	public static final String OPEN_G = "Guitar, Open G";

	/** Courtesy constant to make "getVariation" calls more readable. */
	public static final boolean ENHARMONICS = false;
	/** Courtesy constant to make "getVariation" calls more readable. */
	public static final boolean OCTAVES = true;

	public static final String PROP_PATH = "frets/main/fretboards/";
	public static final String ALL_FRETBOARD_PROPS = "fretboard[.].*[.]properties";

	public static final int DEFAULT_OCTAVEFRET = 12;
	public static final int DEFAULT_MAXFRET = 24;
	
	public Fretboard( final List<GuitarString> strings) {
		setStrings( strings );	
	}
	
	public Fretboard( final GuitarString ... strings) {
		setStrings( strings );
	}
		
	public List<GuitarString> getStrings() {
		return strings;
	}

	public void setStrings( final List<GuitarString> strings) {
		this.strings = strings;
		validateLowHigh();
	}
	public void setStrings( final GuitarString ... strings ) {
		if ( null != strings ) {
			this.strings = new LinkedList<GuitarString>();
			for ( int i = 0; i < strings.length; i++ )
   		       this.strings.add( strings[ i ] );
			validateLowHigh();
		}
	}
	
	public GuitarString getString( int i ) {
		return strings.get( i );
	}

	public GuitarString getLowString() {
		return lowString;
	}

	public GuitarString getHighString() {
		return highString;
	}

	protected void validateLowHigh() {
		lowString = null;
		highString = null;
		if ( null != strings ) {
			for ( int i = 0; i < strings.size(); i++ ) {
				GuitarString currString = strings.get( i ); 
				if ((null == lowString) || ( currString.compareTo( lowString ) < 0 ))
					lowString = currString;
				if ((null == highString) || ( currString.compareTo( highString ) > 0 ))
					highString = currString;
			}
		}		
	}
	
	public int getStringCount() {
		if ( null == strings ) return 0;
		return strings.size();
	}

	public void sortStrings() {
    	Collections.sort( strings );
	}

	/** Gets highest fret of any of the strings. If string have different frets, this can be weird. */
	public int getMaxFret() {
		if (( null == strings ) || ( strings.size() < 1 ))
			return 0;
		int maxFret = Integer.MIN_VALUE; 
		for ( GuitarString string: strings ) {
			if ( string.getMaxFret() > maxFret ) {
				maxFret = string.getMaxFret();
			}			
		}
		return maxFret;		
	}
	
	// Collection API
	public boolean add(GuitarString o) {
		boolean result = strings.add(o);
		validateLowHigh();
		return result;
	}

	public boolean addAll(Collection<? extends GuitarString> c) {
		boolean result = strings.addAll(c);
		validateLowHigh();
		return result;
	}

	public void clear() {
		strings.clear();
		lowString = null;
		highString = null;
	}

	public boolean contains(Object o) {
		return strings.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return strings.containsAll(c);
	}

	public boolean isEmpty() {
		return strings.isEmpty();
	}

	public Iterator<GuitarString> iterator() {
		return strings.iterator();
	}

	public boolean remove(Object o) {
		boolean result = strings.remove(o);
		validateLowHigh();
		return result;
	}

	public boolean removeAll(Collection<?> c) {
		lowString = null;
		highString = null;
		return strings.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return strings.retainAll(c);
	}

	public int size() {
		return strings.size();
	}

	public Object[] toArray() {
		return strings.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return strings.toArray(a);
	}

	// List methods
	public void add(int index, GuitarString element) {
		strings.add(index, element);
	}

	public boolean addAll(int index, Collection<? extends GuitarString> c) {
		return strings.addAll(index, c);
	}

	public GuitarString get(int index) {
		return strings.get(index);
	}

	public int indexOf(Object o) {
		return strings.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return strings.lastIndexOf(o);
	}

	public ListIterator<GuitarString> listIterator() {
		return strings.listIterator();
	}

	public ListIterator<GuitarString> listIterator(int index) {
		return strings.listIterator(index);
	}

	public GuitarString remove(int index) {
		return strings.remove(index);
	}

	public GuitarString set(int index, GuitarString element) {
		return strings.set(index, element);
	}

	public List<GuitarString> subList(int fromIndex, int toIndex) {
		return strings.subList(fromIndex, toIndex);
	}

	
	/** Get all locations where the Note may be played on this fretboard. 
	 * Returns null if note is null or this fretboard has no strings. */
	public LocationList getLocations( final Note note ) {
		return getLocations( note, 0, Integer.MAX_VALUE ); // will return NOFRET < 0 or >= maxFret
	}

	/** Get all locations where the Note may be played on this fretboard in this fret range. 
	 * Returns null if note is null or this fretboard has no strings. */
	public LocationList getLocations( final Note note, int minFret, int maxFret ) {
		if ( null == note ) return null;
		if (( null == strings ) || ( 0 == strings.size() )) return null;
		LocationList locations = new LocationList();		
		for ( int stringi = 0; stringi < strings.size(); stringi++ ) {
			GuitarString guitarString = strings.get( stringi );
			int freti = guitarString.getFret( note );
			if (( freti >= minFret ) && ( freti < maxFret )) 
				locations.add(  new Location( stringi, freti ));
		}
		return locations;
	}
	
	/** Returns a list or null for locations of each note in the NoteList in the fret range. 
	 * Returns null if notes is null or this fretboard has no strings. */
	public List<LocationList> getEnharmonicVariations( final NoteList notes ) {
		return getVariations( notes, 0, Integer.MAX_VALUE, ENHARMONICS );
	}

	/** Returns a list or null for locations of each note in the NoteList in the fret range. 
	 * Returns null if notes is null or this fretboard has no strings.
	 * includeOctave substitutes lower and higher notes of same value, different octave. */
	public List<LocationList> getOctaveVariations( final NoteList notes ) {
		return getVariations( notes, 0, Integer.MAX_VALUE, OCTAVES );
	}

	/** Returns a list or null for locations of each note in the NoteList in the fret range. 
	 * Returns null if notes is null or this fretboard has no strings.
	 * includeOctave substitutes lower and higher notes of same value, different octave. */
	public List<LocationList> getVariations( final NoteList notes, boolean includeOctaves ) {
		return getVariations( notes, 0, Integer.MAX_VALUE, includeOctaves );
	}

	/** Returns a list or null for locations of each note in the NoteList. */ 
	public List<LocationList> getVariations( final NoteList notes, int minFret, int maxFret, boolean includeOctaves ) {
		return getVariations( null, notes, minFret, maxFret, includeOctaves );
	}

	/** Returns a list or null for locations of each note in the NoteList. 
	 * Each element in the variation list contains a list of 1 or more enharmonics or octaves.
	 * For example, if a VariationList is constructed from the notes G, A, D, the elements
	 * in the list consist of the following enharmonic locations (string, fret):
	 * <pre>
	 *    note >       G2,       A2,         D3
	 *    element >    0,        1,          2
	 *    locations    [0,3]     [0,5]       [0,10]
	 *                           [1,0]       [1,5]
	 *                                       [2,0]
	 * </pre>
	 * In other words, each element is a list of enharmonics on the fretboard.
	 * <p>
	 * Using the APIs, one can get each variation or a variation count in the list.
	 * The count will be all possible permutations of the the enharmonics in the list,
	 * in this case 1 * 2 * 3 or 6 variations.
	 * <p>
	 * Additionally, a VariationList can contains octave variations, for example,
	 * replacing G2 with G3, G4, and other octaves notes on the fretboard. So for the G, A, D
	 * example, the variations mushroom to: 
	 * <pre>
	 *    note >       G*,       A*,         D*
	 *    element >    0,        1,          2
	 *    locations    [0,3]     [0,5]       [0,10]
	 *                 [0,15]    [1,0]       [1,5]
	 *                 [2,5]                 [2,0]
	 *                 [2,18]
	 *                 [3,0]
	 *                 ...
	 * </pre>
	 * <p>
	 * Supports a fixed list of locations that can be thrown into the variation mix. So, given
	 * the above variable notes, one can also find and rank variations close to a fixed location
	 * such as B on the low E string (location [0, 7]).
	 *                 fixed     vari        vari       vari 
	 *    note >       B3,       G2,         A2,        D3
	 *    element >    0,        1,          2          3,
	 *    locations    [0,7]     [0,3]       [0,5]      [0,10]
	 *                                       [1,0]      [1,5]
	 *                                                  [2,0]
	 * 
	 * Returns null if notes is null or this fretboard has no strings. 
	 * includeOctave substitutes lower and higher notes of same value, different octave.
	 * <p>
	 * Note that the variation LocationList of any List position can be null or empty.
	 * This often happen when one Fretboard with high and low notes gets moved to a smaller fretboard.
	 * Thus you may see a List<LocationList> String that looks like "[]", "[, ]", or "[, , ]".  
	 */
	public List<LocationList> getVariations( final LocationList fixed, final NoteList variableNotes, int minFret, int maxFret, boolean includeOctaves ) {
		if (( null == variableNotes ) || ( 0 == variableNotes.size() )) return null;
		if (( null == strings ) || ( 0 == strings.size() )) return null;
		
		List<LocationList> variations = new LinkedList<LocationList>();
		if ( null != fixed ) {
			for( Location location : fixed ) {
				variations.add( new LocationList( location ) );				
			}
		}
		for ( int notei = 0; notei < variableNotes.size(); notei++ ) {
			Note note = variableNotes.get( notei );
			LocationList locations = getLocations( note, minFret, maxFret ); 
			// What if note is not available on this fretboard? For example bass note on soprano ukelele
			
			if ( includeOctaves == OCTAVES ) {
		        // Get lower octave variations.
				Note lowerNote = Note.minus( note, Interval.octave );
				GuitarString lowString = this.getLowString();
				Note lowOpen = lowString.getOpenNote();
				while (( lowerNote.getAbsoluteValue() >=0 ) && (lowerNote.compareTo(lowOpen) >= 0)) {
				   // System.out.println( "Comparing with lower note=" + lowerNote );	
				   LocationList lowerLocations = getLocations( lowerNote, minFret, maxFret );
				   locations.addAll( lowerLocations );
				   lowerNote = lowerNote.minus( Interval.octave );				   
				}
				
				// Get higher octave variations.
				Note higherNote = Note.plus( note, Interval.octave );
				GuitarString highString = this.getHighString();
				Note highMax = highString.getHighNote();
				while (( higherNote.getAbsoluteValue() <= Integer.MAX_VALUE ) && (higherNote.compareTo(highMax) <= 0)) {
				   // System.out.println( "Comparing with higher note=" + higherNote );	
				   LocationList higherLocations = getLocations( higherNote, minFret, maxFret );
				   locations.addAll( higherLocations );
				   higherNote = higherNote.plus( Interval.octave );				   
				}
			}
			variations.add( locations );
		}
		Collections.sort( variations );
		return variations;
	}

	/** Returns a list or null for locations of each note in the NoteList. 
	 * Returns null if notes is null or this fretboard has no strings.
	 *  
	 * Warning, the semantics of the input and output are different here.
	 *    Inputs are lists of various enharmonic notes e.g. [[(0,4)],[(0,6),(1,1)]] (2 variation lists) 
	 *    Outputs are lists of locations e.g. [[(0,4),(0,6)],[(0,4),(1,1)]] (2 location lists) 
	 * In other words, use getpermutationCount and getPermutation for variation lists.
	 * Use size and get for location lists. 
	 * */
	public static List<LocationList> explodeAndSort( final List<LocationList> variations, final ChordRank ranker ) {
		long varCount = Fretboard.getPermutationCount( variations );
    	List<LocationList> sortedVars = new LinkedList<LocationList>();
    	for( int i = 0; i < varCount; i ++ ) {
    		sortedVars.add( Fretboard.getPermutation( variations, i ));
    	}
    	Collections.sort( sortedVars, ranker );
    	return sortedVars;
	}

	/** Returns a count of all variations. 
	 * The count is the total permutations of all the enharmonic and octave locations in the list.
	 * int blows up with > 2billion, (about 17 locations with variations)
	 * <p>
	 * Also see {@link getPermutationNumber} which returns the variation number of a given location list.
	 * Also see {@link getPermutation} which returns the location list of a given variation number.
	 */
	public static long getPermutationCount( final List<LocationList> variations  ) {
		if (( null == variations ) || ( 0 == variations.size() )) return 0;
		
		long count = 0;
		for ( int variationi = 0; variationi < variations.size(); variationi++ ) {
			LocationList enharmonics = variations.get( variationi );

			if ( count == 0 ) {
				if (( null != enharmonics ) && ( 0 < enharmonics.size() ))
					count = enharmonics.size();
			} else {
				if (( null != enharmonics ) && ( 0 < enharmonics.size() ))
					count *= enharmonics.size();				
			}
		}
		return count;
	}
	
	/** 
	 * Given a list of enharmonic notes, return the LocationList of variationi, one of the permutations.
	 * For example, let variations be a list of enharmonic locations for notes C3,D3.
	 * Variations[0] contains the LocationList for C3: 0-8, 1-2.
	 * Variations[1] contains the LocationList for D3: 0-10,1-5,2-0
	 * getPermutation( variations, 0 ) returns permutation 0: 0-8,0-10
	 * getPermutation( variations, 1 ) returns permutation 1: 0-8,1-5
	 * getPermutation( variations, 2 ) returns permutation 2: 0-8,2-0
	 * <p>
	 * Also see {@link getPermutationCount} which returns the total number of variations.
	 * The inverse of this is {@link getPermutationNumber} which returns the variation number of a given location list.
	 */
	public static LocationList getPermutation( final List<LocationList> variations, long variationi  ) {
		if (( null == variations ) || ( 0 == variations.size() )) return null;
		if ( 0 > variationi ) return null;
		long numVariations = getPermutationCount( variations );
		if ( variationi >= numVariations ) return null;
				
		LocationList locations = new LocationList();
		// Choose one location variation from each list.
		// for ( int listi = variations.size() - 1; listi >= 0; listi-- ) {
		for ( int listi = 0; listi < variations.size(); listi++ ) {
			LocationList enharmonics = variations.get( listi );
			// System.out.println( "Enharmonics " + listi + "=" + enharmonics );
			if (( null != enharmonics ) && ( 0 < enharmonics.size() )) {
			   locations.add( 0, enharmonics.get( (int) (variationi % (long) enharmonics.size()) ) );
			   variationi /= (long) enharmonics.size();
			}			
		}		
		return locations;
	}

	/** 
	 * Given a list of enharmonic notes, return the permutation number of the given locations
	 * For example, let variations be a list of enharmonic locations for notes C3,D3.
	 * Variations[0] contains the LocationList for C3: 0-8, 1-2.
	 * Variations[1] contains the LocationList for D3: 0-10,1-5,2-0
	 * getPermutationNumber( variations, [0-8,0-10] ) returns permutation number 0
	 * getPermutationNumber( variations, [0-8,1-5] ) returns permutation number 1
	 * getPermutationNumber( variations, [0-8,2-0] ) returns permutation 2
	 * <p>
	 * The number -1 is returns for null lists, empty lists, or locations not in the enharmonic list.
	 * <p>
	 * Also see {@link getPermutationCount} which returns the total number of variations.
	 * The inverse of this is {@link getPermutation} which returns the location list of a given variation number.
	 */
	public static long getPermutationNumber( final List<LocationList> variations, final LocationList locations ) {
		if ( null == variations || variations.size() < 1 ) return -1;
		if ( null == locations || locations.size() < 1 || locations.size() != variations.size() ) return -1;
		long permutation = 0;
		for ( int i = 0; i < locations.size(); i++ ) {
			Location location = locations.get( i );
			// System.out.println( "Location " + i + "=" + location );
			LocationList enharmonics = variations.get( locations.size() - 1 - i );
			// System.out.print( "   Enharmonics " + i + "=" + enharmonics );
			
			int indexOf = enharmonics.indexOf( location );
			if ( -1 != indexOf ) {
				permutation *= (long) enharmonics.size();
				permutation += (long) indexOf;
				// System.out.println( " indexOf " + location + "=" + indexOf + ", permutation=" + permutation );				
			} else {
				System.out.println( "Did not find location " + location + " in list " + enharmonics );
				return -1;
			}
		}
		return permutation;
	}

	/** 
	 * Given a location list with enharmonic variations, for example: 
	 * <pre>
	 *    note >       G2,       A2,         D3
	 *    element >    0,        1,          2
	 *    locations    [0,3]     [0,5]       [0,10]
	 *                           [1,0]       [1,5]
	 *                                       [2,0]
	 * </pre>
	 * This returns a string with the variation number and digits in parens.
	 *    decimal-variationi/decimal-variations (binaryi/binary count)
	 *    variationi == 0 ==> "0/6 (000/123)"  
	 *    variationi == 1 ==> "1/6 (001/123)"  
	 *    variationi == 2 ==> "2/6 (002/123)"  
	 *    variationi == 3 ==> "3/6 (010/123)"  
	 *    variationi == 6 ==> "6/6 (012/123)"  
	 * FYI, this notation might breakdown with more than 10 locations on a large fretboard.
	 */
	public static String getPermutationString( final List<LocationList> variations, long variationi  ) {
		String INVALID = "ø";
		if (( null == variations ) || ( 0 == variations.size() )) return INVALID;
		if ( 0 > variationi ) return INVALID + "(" + variationi + ")";
		
		long numVariations = getPermutationCount( variations );
		if ( variationi >= numVariations ) return INVALID + "(" + variationi + "/" + numVariations + ")";
				
		// Choose one location variation from each list.
		// for ( int listi = variations.size() - 1; listi >= 0; listi-- ) {
		StringBuffer binaryVar = new StringBuffer();
		StringBuffer binaryTot = new StringBuffer();
		long remainder = variationi;
		for ( int listi = 0; listi < variations.size(); listi++ ) {
			LocationList enharmonics = variations.get( listi );
			// System.out.println( "Enharmonic " + listi + "=" + enharmonics );
			if ( null != enharmonics ) {				
				long digitSize = enharmonics.size();
				if ( 0 < digitSize ) {
				   long digit = remainder % digitSize;
				   binaryVar.insert( 0, digit ); // prepend				   
   			       remainder /= digitSize;
				} else
 				   binaryVar.append( "?" );   			    
	    		binaryTot.insert( 0, digitSize ); // prepend
			}			
		}
		String varString = Long.toString( variationi ) + "/" + Long.toString( numVariations )
		   + " ("  + binaryVar.toString() + "/" + binaryTot.toString() + ")";
		return varString;
	}

	/** The inverse of getPermutationString.
	 * Provides integer values for each element of data in a permutation string.
	 * For example, given the following String "1/6 (001/123)", this 
	 * returns the following values from the string.  
	 *    decimal-variationi (1)
	 *    decimal-variations (6)
	 *    note count (3)
	 *    note 0 variation i (0)
	 *    note 0 variation n (1)
	 *    note 1 variation i (0)
	 *    note 1 variation n (2)
	 *    note 2 variation i (1)
	 *    note 2 variation n (3)
	 * FYI, this notation might breakdown with more than 10 locations on a large fretboard.
	 */
	public static long [] getPermutationValues( String permutationString ) {
		if (null == permutationString) return null;
		String delims = "[() /]+";
	    String [] tokens = permutationString.split( delims );
	    if (( null == tokens ) || ( tokens.length < 2))
	       return null;
	    if ( tokens.length == 2 )
	       return new long [] { Long.parseLong( tokens[ 0 ] ), Long.parseLong( tokens[ 1 ] ),  0 };
	    if ( tokens.length == 4 ) {
	    	long noteCount = tokens[ 2 ].length();
 	        long [] vals = new long[ 3 + 2 * (int)noteCount ];
 	        vals[ 0 ] = Long.parseLong( tokens[ 0 ] );
 	        vals[ 1 ] = Long.parseLong( tokens[ 1 ] );
 	        vals[ 2 ] = noteCount;
 	        for ( int i = 0; i < noteCount; i++ ) {
 	        	int vali = 3 + i*2; 	        	
 	        	vals[ vali ] = Long.parseLong( tokens[ 2 ].substring( i, i+1 )); // note i, variation i, variation N
 	        	vals[ vali + 1 ] = Long.parseLong( tokens[ 3 ].substring( i, i+1 ));
 	        }
 	        return vals;
	    }
		return null;		
	}
	
	/** Returns a sorted list of strings not played with this location list. */
	public List<Integer> getNotPlayedSet( LocationList locations ) {
		List<Integer> playedSet = null;
		if ( null != locations ) playedSet = locations.getStringSet();
		List<Integer> stringSet = new LinkedList<Integer>();
		for ( int stringi = 0; stringi < strings.size(); stringi++ ) {
			if ( (null == playedSet) || !playedSet.contains( stringi )) {
				stringSet.add( stringi );				
			}			
		}
		return stringSet; 
	}
	
	/** 
	 * Show variation on strings in ASCII from lowFret to highFret.
	 * @param lowFret
	 * @param highFret
	 * @return
	 */
	public String toString( final LocationList locations, int lowFret, int highFret, final Display displayOpts ) {
		if ( Orientation.HORIZONTAL == displayOpts.orientation )
			return toStringHori( locations, lowFret, highFret, displayOpts );
		else if ( Orientation.VERTICAL == displayOpts.orientation )
			return toStringVert( locations, lowFret, highFret, displayOpts );
		throw new IllegalArgumentException ( "Unknown orientation \"" + displayOpts.orientation + "\"" );
	}
	
	/** 
	 * Show notes on strings in ASCII from lowFret to highFret.
	 * Warning, enharmonic notes are displayed. Use variations version for uniqueness.
	 * @param lowFret
	 * @param highFret
	 * @return
	 */
	public String toString( final NoteList notes, int lowFret, int highFret, final Display displayOpts ) {
		if ( Orientation.HORIZONTAL == displayOpts.orientation )
			return toStringHori( notes, lowFret, highFret, displayOpts );
		else if ( Orientation.VERTICAL == displayOpts.orientation )
			return toStringVert( notes, lowFret, highFret, displayOpts );
		throw new IllegalArgumentException ( "Unknown orientation \"" + displayOpts.orientation + "\"" );
	}

	/** Filters out only locations that are on the given string. */
	public static List<Integer> getLocations( int stringi, final LocationList locations ) {
		if (null == locations) return null;
		List<Integer> filteredLocations = new LinkedList<Integer>();
		for ( Location location: locations ) {
			if ( stringi == location.getString() )
				filteredLocations.add( location.getFret() );
		}
		return filteredLocations;		
	}
	
	/** 
	 * Show locations on horizontal strings in ASCII from lowFret to highFret.
	 * @param lowFret
	 * @param highFret
	 * @return
	 */
	public String toStringHori( final LocationList locations, int lowFret, int highFret, final Display displayOpts ) {
		StringBuilder sb = new StringBuilder();
		String fretNumberString = null;
		if (( null != displayOpts.fretNumbering ) && 
			 ( displayOpts.fretNumbering.contains( Display.FretNumbering.FIRSTRIGHT ))) {
			fretNumberString = getFretNumberStringHori( lowFret, displayOpts );
			if (( fretNumberString != null ) && ( displayOpts.fretNumberingDisplayOpen )){ 
			   sb.append( fretNumberString );
			   sb.append( Display.NL );
			}
		}
		// Work from high string to low string.
		for ( int stringi = this.getStringCount() - 1; stringi >=0 ; stringi-- ) {
			GuitarString guitarString = this.getString( stringi );
			List<Integer> locationsThisString = Fretboard.getLocations( stringi, locations );
			sb.append( guitarString.toString( locationsThisString, lowFret, highFret, displayOpts ));
			if ( stringi > 0 ) sb.append( Display.NL );
		}		
		if (( null != displayOpts.fretNumbering ) && 
			( displayOpts.fretNumbering.contains( Display.FretNumbering.FIRSTLEFT ))) {
			if (( fretNumberString != null ) && ( displayOpts.fretNumberingDisplayOpen )){ 
   			   sb.append( Display.NL );
			   sb.append( fretNumberString );
			}
		}
		return sb.toString();
	}

	/** 
	 * Show notes on horizontal strings in ASCII from lowFret to highFret.
	 * @param lowFret
	 * @param highFret
	 * @return
	 */
	public String toStringHori( final NoteList notes, int lowFret, int highFret, final Display displayOpts ) {
		List<LocationList> variations = this.getEnharmonicVariations( notes );
		long variationCount = Fretboard.getPermutationCount( variations );
		if ( variationCount == 0 ) {			
			throw new IllegalArgumentException( "StringHori detects " + variationCount + " variations.");
		} else if (variationCount > 1 ) {
			StringBuilder sb = new StringBuilder( "FilenameRegExFilter.toStringHori detects " + variationCount + " variations:" + NL );
	    	for( int i = 0; i < variationCount; i ++ ) {
	    		LocationList variation = Fretboard.getPermutation( variations, i );	    		
	        	sb.append( "Variation " + i + 
	        		" (span=" + variation.fretSpan() + ", unique strings=" + variation.uniqueStrings() + "):" + NL + 
	        		this.toString( variation, 0, 18, displayOpts ) + NL );
	    	}
			throw new IllegalArgumentException( sb.toString() );
		} else if (variationCount < 0 ) { 
			throw new IllegalArgumentException( "StringHori detects " + variationCount + " variations.");
		}
		return toStringHori( Fretboard.getPermutation( variations, 0 ), lowFret, highFret, displayOpts );
	}

	public String getFretNumberStringHori( int lowFret, final Display displayOpts ) {
		StringBuilder sb = new StringBuilder();
		// Figure characters to use.
		String spaceString = " ";
		if (( null != displayOpts.spaceString ) && (displayOpts.spaceString.length() > 0))
			spaceString = displayOpts.spaceString;
		String plainNoteString = "o";
		if (( null != displayOpts.plainNoteString ))
			plainNoteString = displayOpts.plainNoteString;
		int noteSpace = plainNoteString.length();
		if (( noteSpace < 2 ) && ( displayOpts.infoType == Display.InfoType.NAME )) noteSpace = 2;  
		int fretSpace = displayOpts.fretSpace;
		if ( lowFret == 0 ) {
			String display = " ";
			if ( displayOpts.fretNumberingDisplayOpen ) {
				display = String.valueOf( lowFret );
			}
			while ( display.length() < noteSpace ) {
				if ( displayOpts.hand == Display.Hand.LEFT )
				   display += " ";
				else if ( displayOpts.hand == Display.Hand.RIGHT )
				   display = " " + display;
			}
			sb.append( display );
		} else {
			String display = Display.pad( String.valueOf( lowFret ), noteSpace, fretSpace, 
				displayOpts.fretAlign, displayOpts.hand, Orientation.HORIZONTAL, spaceString, spaceString);
			sb.append( display );
		}
		return sb.toString();
	}
	
	/** 
	 * Show vertical strings in ASCII from lowFret to highFret.
	 * @param lowFret
	 * @param highFret
	 * @return
	 */
	public String toStringVert( final LocationList locations, int lowFret, int highFret, final Display displayOpts ) {
		StringBuilder sb = new StringBuilder();
		String [] stringsVert = new String [  this.getStringCount() ];
		String fretNumberString = null;
		boolean fretNumberRight = ( null != displayOpts.fretNumbering ) && 
			( displayOpts.fretNumbering.contains( Display.FretNumbering.FIRSTRIGHT ));
		boolean fretNumberLeft = ( null != displayOpts.fretNumbering ) && 
			( displayOpts.fretNumbering.contains( Display.FretNumbering.FIRSTLEFT));
		if (( lowFret == 0 ) && ( !displayOpts.fretNumberingDisplayOpen )) {
			   fretNumberRight = false;
			   fretNumberLeft = false;
		}
		
		// Capture vertical strings from high string to low string.
		for ( int stringi = this.getStringCount() - 1; stringi >=0 ; stringi-- ) {
			GuitarString guitarString = this.getString( stringi );
			List<Integer> locationsThisString = Fretboard.getLocations( stringi, locations );
			stringsVert[ stringi ] = guitarString.toString( locationsThisString, lowFret, highFret, displayOpts );
		}		
		// Get the optional fret number string. Lengthen it if necessary.
		if ( fretNumberLeft || fretNumberRight ) { 
			fretNumberString = Integer.toString( lowFret );
			// fretNumberString = getFretNumberStringHori( lowFret, displayOpts );
			// int lengthDiff = stringsVert[ 0 ].length() - fretNumberString.length();
			// while ( lengthDiff-- > 0 )
			// 	fretNumberString += " ";
		}
		
		// Parse and assemble vertical strings.
		StringTokenizer sts [] = new StringTokenizer[ this.getStringCount() ];
		for ( int stringi = this.getStringCount() - 1; stringi >=0 ; stringi-- ) {
			// Delimit by new lines.
			sts[ stringi ] = new StringTokenizer( stringsVert[ stringi ], Display.NL );
		}
		// For each line
		int tokenCount = 0;
		while ( sts[ 0 ].hasMoreTokens() ) {
			if ( fretNumberLeft ) {
				if (tokenCount == 0) 
					sb.append( fretNumberString );
				else
					sb.append( " " );
			}

			if ( Display.Hand.RIGHT == displayOpts.hand ) {
				for ( int stringi = 0; stringi < this.getStringCount(); stringi++ ) {
					sb.append( sts[ stringi ].nextToken() );
				}
			} else {
				for ( int stringi = this.getStringCount() - 1; stringi >=0 ; stringi-- ) {
					sb.append( sts[ stringi ].nextToken() );
				}
			}
			if ( fretNumberRight ) {
				if (tokenCount == 0) 
					sb.append( fretNumberString );
				else
					sb.append( " " );
			}
			sb.append( Display.NL );
			tokenCount++;
		}

		return sb.toString();
	}
	
	/** 
	 * Show vertical strings in ASCII from lowFret to highFret.
	 * @param lowFret
	 * @param highFret
	 * @return
	 */
	public String toStringVert( final NoteList notes, int lowFret, int highFret, final Display displayOpts ) {
		List<LocationList> variations = this.getEnharmonicVariations( notes );
		long variationCount = Fretboard.getPermutationCount( variations );
		if ( variationCount == 0 ) {			
			throw new IllegalArgumentException( "StringVert detects " + variationCount + " variations." );
		} else if (variationCount > 1 ) {
			StringBuilder sb = new StringBuilder( "FilenameRegExFilter.toStringVert detects " + variationCount + " variations:" + NL );
	    	for( int i = 0; i < variationCount; i ++ ) {
	    		LocationList variation = Fretboard.getPermutation( variations, i );
	        	sb.append( "Variation " + i + 
	        		" (span=" + variation.fretSpan() + ", unique strings=" + variation.uniqueStrings() + "):" + NL + 
	        		this.toString( variation, 0, 18, displayOpts ) + NL );
	    	}
			throw new IllegalArgumentException( sb.toString() );
		} else if (variationCount < 0 ) { 
			throw new IllegalArgumentException( "StringVert detects " + variationCount + " variations." );
		}
		return toStringVert( Fretboard.getPermutation( variations, 0 ), lowFret, highFret, displayOpts );
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime;
		if ( null != strings ) {
			for ( int i = 0; i < strings.size(); i++ ) {
				result = prime * result + strings.get(i).hashCode();
			}
		}
		return result;
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
		Fretboard other = (Fretboard) obj;
		if (( null == strings ) && ( null == other.strings ))
			return true; // both null are same
		if (( null == strings ) || ( null == other.strings ))
			return false; // one null are different
		for ( int i = 0; i < strings.size(); i++ ) {
			if ( !strings.get(i).equals( other.strings.get( i ) ))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < strings.size(); i++ ) {
			if ( i > 0 ) sb.append( "-" );
			sb.append( strings.get(i).toString() );
		}
		return ( sb.toString());   		
	}

	// SimpleProperties interface
	protected String metaName;
	protected String metaDescription;
	protected String metaLocation; // url or file

	/** Key is fretboard name. Value is fretboard property file name. */
	// Did not want to keep Map<String, Fretboard> in memory.
	// So some of the SimpleProperties methods are inefficient and load lots of fretboards.
	protected static Map<String,String> propertiesMap = loadPropertiesNames( PROP_PATH, ALL_FRETBOARD_PROPS );
	
	public String getMetaName() {
		return metaName;
	}

	public String getMetaDescription() {
		return metaDescription;
	}

	public String getMetaLocation() {
		return metaLocation;
	}

	/** Opens a path at the given name, attempts to read files from there.
	 *  Use optional filter as a java.io.FilenameFilter. */
	public List<Fretboard> readFromPath( String pathName, String filterString ) throws IOException {
		List<Fretboard> fretboards = new LinkedList<Fretboard>();
		for( String fileName : propertiesMap.values()  ) {
			fretboards.add( getInstanceFromFileName( fileName ));			
		}
        return fretboards;
	}

	/** Performs the loading of configured objects from the given location. */
	public Map<String,Fretboard> loadProperties(String pathName, String filterString) throws IOException {
	   Map<String,Fretboard> fretMap = new HashMap< String, Fretboard>();
	   for( String simpleName : propertiesMap.keySet()  ) {
		   fretMap.put( simpleName, getInstanceFromFileName( propertiesMap.get( simpleName )));			
	   }
	   return fretMap;
	}

	/** Opens a file at the given name, and reads all the properties into an object. */
	public Fretboard readFromFile( String fileName ) throws IOException {
		return getInstanceFromFileName( fileName );
	}
	
	/** Returns all fretboard names available for selection. */
	public static String [] getFretboardNames() {
		Set<String> keys = propertiesMap.keySet();
		String [] names = keys.toArray( new String[0] );
		Arrays.sort( names );
		return names;
	}

	/** Returns a standard fretboard that has been loaded from a
	 *  central repository or properties list. */
	public Fretboard getInstance( String shortName ) {
		String fileName = propertiesMap.get( shortName );
  	    return getInstanceFromFileName( fileName );
	}

	/** Instantiates a map. Loads fretboard property files from resource repo. */
	public static Map<String,String> loadPropertiesNames( String pathName, String filterString ) {
	   Map<String, String> fretboardMap = new HashMap<String,String>();
	   try {
		   readNamesFromPath(fretboardMap, pathName, filterString);
	   } catch (Exception e) {
		   System.out.print( "Fretboard.loadPropertiesName e=" + e );
	   }
	   return fretboardMap;	   
	}

	/** 
	 * Opens a path at the given name, attempts to read file names from there.
	 * Use optional filter as a java.io.FilenameFilter. 
	 */
	public static void readNamesFromPath( Map<String,String> fretboardMap, String path, String regExFilter ) throws URISyntaxException, IOException {
		String [] shortNames = getResourceListing( path, regExFilter );
		
		// Build list of file paths from filter.
		for ( String shortName : shortNames ) {
		   // Somewhat wasteful to throw a fully populated fretboard away, but it keeps memory consumption down.
		   Fretboard fretboard = getInstanceFromFileName( shortName );
		   // System.out.println( "Fretboard.readNamesFromPath resource name=" + shortName + ", fretboard=" +fretboard);
		   if ( null != fretboard)
			   fretboardMap.put( fretboard.metaName, fretboard.getMetaLocation() );
		}
	}

   /** Resource load location for property files
    *  file: (file system) or jar: (JAR) protocols
    *  This value is set by {@link Fretboard#getResourceListing(String, String)}
    */
   public static File resourceLocation;
   
  /**
   * List directory contents for a resource folder. Not recursive.
   * Works for regular files and also JARs.
   * <p>
   * A side effect of this method is the resourceLocation is populated.
   * There are different resource locations for file: (file system) or jar: (JAR) protocols
   * 
   * @param path Path for resources. Should not start with "/" but must end with "/" so that JAR path prefixes are clipped properly.
   * @param filter a regular expression such as ".*properties" for filtering certain names.
   * @return Just the name of each member item, not the full paths.
   * @throws URISyntaxException for non-file or non-JAR protocol 
   * @throws IOException 
   */
   public static String [] getResourceListing(String path, String regExFilter) throws URISyntaxException, IOException {
	  // System.out.println( "Fretboard.getResourceListing path=" + path + ", regex=" + regExFilter );
	  @SuppressWarnings("rawtypes")
	  Class clazz = Fretboard.class;
      URL dirURL = clazz.getClassLoader().getResource(path);
      
      if (dirURL != null && dirURL.getProtocol().equals("file")) {
    	 resourceLocation = new File(dirURL.toURI());
    	 // System.out.println( "Fretboard.getResourceListing dir path=" + dir.getPath() );
    	 if ( !resourceLocation.isDirectory() )
    	     throw new IOException ( "Path \"" + path + "\" is not a directory." );
    	 if (( null != regExFilter ) && ( regExFilter.length() > 0 )) {
    		FilenameFilter filter = new FilenameRegExFilter( regExFilter );
    		String [] names = resourceLocation.list( filter );
      	    // System.out.println( "Fretboard.getResourceListing find count=" + names.length + ", example name=" + names[ 0 ]);
      	    return names;
    	 } else {
    		String [] names = resourceLocation.list();
      	    // System.out.println( "Fretboard.getResourceListing find count=" + names.length + ", example name=" + names[ 0 ]);
  	    	return names;
         } 
      }
      
      if (dirURL == null) {
         // In case of a jar file, we can't actually find a directory. Have to assume the same jar as clazz.
         String me = clazz.getName().replace(".", "/")+".class";
         dirURL = clazz.getClassLoader().getResource(me);
    	 resourceLocation = new File(dirURL.toURI()); // Will be null for JAR files
      }
      
	  Pattern pattern = Pattern.compile(regExFilter); 
      if (dirURL.getProtocol().equals("jar")) {
    	// System.out.println( "Fretboard.getResourceListing got a JAR" );
        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
    	System.out.println( "Fretboard.getResourceListing jar=" + jar.getName() );
    	
        Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
        Set<String> result = new HashSet<String>(); // avoid duplicates in case it is a subdirectory
        while(entries.hasMoreElements()) {
          String name = entries.nextElement().getName();
          if (name.startsWith(path)) { // filter according to the path
            // System.out.println( "Fretboard.getResourceListing name=" + name + ", starts with path?=" + name.startsWith(path) );
            String entry = name.substring(path.length());
            int checkSubdir = entry.indexOf( "/" ); // do not use File.separator. JARs always use "/".
            // System.out.println( "Fretboard.getResourceListing entry=" + entry + ", contains /=" + checkSubdir );
            if (checkSubdir >= 0) {
              // Recursion here? No, if it is a subdirectory, we just return the directory name. 
              entry = entry.substring(0, checkSubdir);
            }
  	        Matcher matcher = pattern.matcher(entry);
            // System.out.println( "Fretboard.getResourceListing entry=" + entry + ", matches " + regExFilter + "?=" + matcher.matches() );
  	        if ( matcher.matches() )
  	        	result.add(entry);
          }
        }
        jar.close();
        String [] names = result.toArray(new String[result.size()]); 
        System.out.println( "Fretboard.getResourceListing find count=" + names.length + ", example name=" + names[ 0 ]);
        return names;
      } 
        
      throw new UnsupportedOperationException("Cannot list files for URL " + dirURL);
   }
   
	/**
	 * Looks up the given short name, returns object with those properties.
	 */
	public static Fretboard getInstanceFromName( String shortName ) {
		String fileName = propertiesMap.get( shortName );
		return getInstanceFromFileName( fileName );
	}
	
	/**
	 * Opens a file at the given name, and reads all the properties into an object.
	 * @param shortName - the short listing name of the property file.
	 */
	public static Fretboard getInstanceFromFileName( String shortName ) {
		try {
			Fretboard test = new Fretboard();
			test.populateFromFile( resourceLocation, shortName );
			test.metaLocation = shortName;			
			return test;
		} catch (Exception e) {
			System.out.println( "Fretboard.getInstanceFromFileName e=" + e);
		}
		return null;
	}

	/** Fills all values of this fretboard from the given file properties.
	 * This also populates metaName, metaDescription, and metaLocation.
	 * @param resourceLocation - the target directory of file based loading. Null for JAR or classpath based loading.
	 * @param shortName - the path free name of the property file
	 * @throws IOException
	 */
	public void populateFromFile( File resourceLocation, String shortName ) throws IOException {
		// Read properties file. 
		Properties properties = new Properties();
		Set<String> foundKeys = new HashSet<String>(); // Will check for missing or duplicate names.

		// Some defaults for strings.
		int maxFret = 15;
		int octaveFret = 12;
		
		// Load properties
		if ( null != resourceLocation ) {
			// Relative to file system target for this app.
			File file = new File( resourceLocation, shortName );
			// System.out.println( "Fretboard.getInstanceFromFileName resourceLocation=" + resourceLocation + ", name=" + shortName );
			properties.load(new FileInputStream( file ));
		} else {
			// Relative to JAR file location.
			InputStream is = Fretboard.class.getResourceAsStream( "fretboards/" + shortName ); // relative to class package
			// System.out.println( "Fretboard.getInstanceFromFileName resourceLocation=" + is + ", name=" + shortName );
			properties.load( is );			
		}

		// First load common fret values
		String requiredKey = "fretboard.name";
		this.metaName = properties.getProperty( requiredKey );
		foundKeys.add( requiredKey );
		requiredKey = "fretboard.description";
		this.metaDescription = properties.getProperty( requiredKey );
		foundKeys.add( requiredKey );
		String optionalKey = "fretboard.maxFret";
		if ( properties.containsKey( optionalKey ) ) {
			maxFret = Integer.parseInt( properties.getProperty( optionalKey ));
			foundKeys.add( optionalKey );
		}
		optionalKey = "fretboard.octaveFret";
		if ( properties.containsKey( optionalKey ) ) {
			octaveFret = Integer.parseInt( properties.getProperty( optionalKey ));
			foundKeys.add( optionalKey );
		}
		
		// Load string values in order.
		String stringPrefix = "guitarString.openNote.";
		// Try to load max strings. Will complain later if more are missed.
		int STRING_MAX = 12;
		boolean [] foundString = new boolean [ STRING_MAX ]; // Will check for gaps.
		for ( int i = 0; i < STRING_MAX; i++ ) {
			foundString[ i ] = false;
			String key = stringPrefix + i;
			if ( properties.containsKey( key ) ) {
				String value = properties.getProperty( key );
				Note openNote = Note.parse( value );
				GuitarString guitarString = new GuitarString(openNote, octaveFret, maxFret);
				this.add( guitarString );
				foundKeys.add( key );
				foundString[ i ] = true;
			}
		}

		// Check for missed or duplicate keys. 
		for ( Iterator<Object> it = properties.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			if ( !foundKeys.contains( key ) )
				System.err.println( "WARNING: key \"" + key + "\" in property file " + shortName + " was not used." );
		}
		
		// Check for skipped strings.
		for ( int i = 0; i < STRING_MAX; i++ ) {
			if ( !foundString[ i ] ) {
				if ( i + 1 < STRING_MAX ) {
					if ( foundString[ i + 1 ] ) 
						System.err.println( "WARNING: It appears string " + i + " in property file " + shortName + " was skipped." );
				}
			}
		}
		
		// this.sortStrings(); // Do not sort. This will be bad for ukeleles and other reentrant tunings.
	}
	
	protected GuitarString lowString = null;
	protected GuitarString highString = null;
	protected List<GuitarString> strings = new LinkedList<GuitarString>();	
}