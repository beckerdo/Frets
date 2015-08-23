package frets.main;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Encapsulates a group of locations.
 * Can be used as base for scales, chords, arpeggios, melodies.
 * Consider collections that have unique locations, sorted locations, repeated locations.
 *
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class LocationList implements List<Location>, Comparable<LocationList> {
	public final static String DELIM = ",";
	
	public LocationList( ) {
	}
	
	/** Make a new set, clone, without touching the original. */
	public LocationList( final LocationList other ) {
		// addAll( other );
		if ( null != other ) {
			for ( int i = 0; i < other.size(); i++ )
				list.add( new Location( other.get( i ))); // deep copy
		}
	}
	
	/** Make a new set, without touching the original. */
	public LocationList( final Collection<? extends Location> other ) {
		// addAll( other );
		if ( null != other ) {
			for ( Location loc : other ) {
				list.add( new Location( loc ) );
			}
		}
	}
	
	/** Make a new set, without touching the original. */
	public LocationList( final Location ... other ) {
		// addAll( Arrays.asList( other ) );
		if ( null != other ) {
			for ( int i = 0; i < other.length; i++ )
				list.add( new Location( other[ i ] ) ); // deep copy
		}
	}

	/** Make a new instance from a String produced by toString of this class. */
	public LocationList( String toString ) {
		this();
		this.set( LocationList.parseString( toString ) );
	}

	// Setters
	public LocationList set( final LocationList other ) {
		clear();
		if ( null != other ) {
			for ( int i = 0; i < other.size(); i++ )
				list.add( new Location( other.get( i ))); // deep copy
		}
		return this;
	}
	
	public LocationList set( final Collection<? extends Location> other ) {
		clear();
		if ( null != other ) {
			for ( Location loc : other ) {
				list.add( new Location( loc ) );
			}
		}
		return this;
	}
	
	public LocationList set( final Location ... other ) {
		clear();
		if ( null != other ) {
			for ( int i = 0; i < other.length; i++ )
				list.add( new Location( other[ i ] ) ); // deep copy
		}
		return this;
	}
	
	@Override
	public int hashCode() {
		int prime = 17;
		if ( null == list )
			return prime;
		return list.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationList other = (LocationList) obj;
		// internal test
		// Compare lists
		int compare = this.compareTo(other);
		return (compare == 0);
	}

	@Override
	@com.fasterxml.jackson.annotation.JsonValue
	public String toString() {
		return toString( DELIM );
	}
	
	public String toString( String delim ) {
		StringBuilder sb = new StringBuilder(  );
		int i = 0;
		for( Location location: list ) {
			sb.append( location );
			if ( i + 1 < list.size())
				sb.append( delim );
			i += 1;
		}
		return sb.toString();   		
	}
	
	/** Create a new location list from the given string */
	public static LocationList parseString( String parseString ) {
		// Tried various string representations. The last seemed most readble.
		// (0>7),(2>1),(1>7)
		// 0>7,2>1,1>7
		// 0+7,2+1,1+7
		// System.out.println( "LocationList.parseString string=\"" + parseString + "\"" );
		LocationList list = new LocationList();
   		if ((null==parseString) || (parseString.length() < 1 ))
   			return list; // empty list
   		// Whitespace plus delimiters, plus any ""
   		String [] values = parseString.split( "[\\s" + LocationList.DELIM  + "\"]" );
   		for ( String token: values ) {
   			if ( !token.isEmpty() ) {
   				// System.out.println( "   token=" + token );
   				try {
   				list.add( Location.parseString( token ) );
   				} catch ( Exception e) {
   					// Ignore garbage locations
   				}
   			}
   		}
   		return list;
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

	public static LocationList fromJSON( String json ) {
		// Json looks exactly like String
		// LocationList string="0+0,1+2,5+4", json="0+0,1+2,5+4"
		return LocationList.parseString( json );
		// LocationList value = null;
		// try {
		//	com.fasterxml.jackson.databind.ObjectMapper jsonMapper = 
		//		new com.fasterxml.jackson.databind.ObjectMapper();
		// 	value = jsonMapper.readValue(json, LocationList.class);
		// } catch (Exception e) { // JsonParseExcetpion, JsonMappingException, IOException
		// 	e.printStackTrace();
		// }
		// return value;
	}

	
	/** Locations should be of same order and length to be equal.
	 * First lower location is less than
	 * Shorter sequences are less than longer sequences
	 * Examples:
	 * A < B
	 * ABC < ABD
	 * AAA < BB
	 * AAA < AAAA
	 * ABC < ABCD
	 */
	public int compareTo(LocationList o) {
		if ((null == list) && (null == o.list))
			return 0;
		if ((null == list) && (null != o.list))
			return -1;
		if ((null != list) && (null == o.list))
			return 1;
		int shorter = list.size();
		if ( o.list.size() < shorter )
			shorter = o.list.size();
		// Location list should be of same size and order
		for ( int i = 0; i < shorter; i ++ ) {
		   Location a = list.get( i );
		   Location b = o.list.get( i );
		   int locationCompare = b.compareTo(a);
		   if (locationCompare != 0)
			   return locationCompare;
		}
        // Lists are equal for first N locations.
		if (list.size() < o.list.size())
			return -2;
		if (list.size() > o.list.size())
			return 2;
		return 0;
	}

	// Collection API
	public boolean add(Location o) {
		return list.add(o);
	}

	public boolean addAll(Collection<? extends Location> c) {
		return list.addAll(c);
	}

	public void clear() {
		list.clear();
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<Location> iterator() {
		return list.iterator();
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return list.removeAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public int size() {
		return list.size();
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	// List methods
	public void add(int index, Location element) {
		list.add(index, element);
	}

	public boolean addAll(int index, Collection<? extends Location> c) {
		return list.addAll(index, c);
	}

	public Location get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<Location> listIterator() {
		return list.listIterator();
	}


	public ListIterator<Location> listIterator(int index) {
		return list.listIterator(index);
	}

	public Location remove(int index) {
		return list.remove(index);
	}

	public Location set(int index, Location element) {
		return list.set(index, element);
	}

	public List<Location> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	// Information utilities
	/** Returns number of unique guitar strings these locations contain. */
	public int getStringCount() {
		Set<Integer> strings = new HashSet<Integer>();
		for ( Location location: list ) {
			strings.add( location.getString() );
		}
		return strings.size();				
	}
	
	/** Returns false if multiple locations on one string. Otherwise true. */
	public boolean uniqueStrings() {
		List<Integer> strings = new LinkedList<Integer>();
		for ( Location location: list ) {
			if ( strings.contains( location.getString() ))
				return false;
			strings.add(location.getString() );
		}
		return true;				
	}
	
	/** Returns the span of highest to lowest fret. */
	public int fretSpan() {
		int minFret = Integer.MAX_VALUE;
		int maxFret = Integer.MIN_VALUE;
		for ( Location location : list ) {
			if ( location.getFret() < minFret ) minFret = location.getFret();
			if ( location.getFret() > maxFret ) maxFret = location.getFret();
		}
		int span = maxFret - minFret;
		if (span < 0)
			throw new IllegalArgumentException( "Negative LocationList.fretSpan=" + span); 
		return span;				
	}
	
	/** Returns the span of highest to lowest string. */
	public int stringSpan() {
		int minstring = Integer.MAX_VALUE;
		int maxstring = Integer.MIN_VALUE;
		for ( Location location : list ) {
			if ( location.getString() < minstring ) minstring = location.getString();
			if ( location.getString() > maxstring ) maxstring = location.getString();
		}
		return Math.abs( maxstring - minstring );				
	}
	
	/** Returns the min fret of the list. */
	public int minFret() {
		int minFret = Integer.MAX_VALUE;
		for ( Location location : list ) {
			if ( location.getFret() < minFret ) minFret = location.getFret();
		}
		return minFret;				
	}
	
	/** Returns the max fret of the list. */
	public int maxFret() {
		int maxFret = Integer.MIN_VALUE;
		for ( Location location : list ) {
			if ( location.getFret() > maxFret ) maxFret = location.getFret();
		}
		return maxFret;				
	}
	
	/** Takes a location list and moves all locations up or down a given string interval. */
	public void transposeFrets( int offset ) {
		for ( Location location : list ) {
			location.setFret( location.getFret() + offset );
		}
	}
	
	/** Takes a location list and moves all locations up or down to a given spot on the fretboard. */
	public void transposeFrets( final Fretboard fretboard, int freti ) {
		int offset = freti -  minFret();
		for ( Location location : list ) {
			int newLocation = location.getFret() + offset;
			if ( newLocation < 0 ) {
				throw new IllegalArgumentException( "Location " + location + " moved to fret " + newLocation + ", below min fret." );
			}
			if ( newLocation > fretboard.getString( location.getString()).getMaxFret() ) {
				throw new IllegalArgumentException( "Location " + location + " moved to fret " + newLocation + ", above max fret." );
			}
			location.setFret( location.getFret() + offset );
		}
	}
	
	/** Takes a location list and moves all locations up or down a given string interval. */
	public void transposeStrings( int offset ) {
		for ( Location location : list ) {
			location.setString( location.getString() + offset );
		}
	}
	
	/** Takes a location list and moves all locations up or down to a given spot on the fretboard. */
	public void transposeStrings( final Fretboard fretboard, int stringi ) {
		int offset = stringi -  minString();
		for ( Location location : list ) {
			int newLocation = location.getString() + offset;
			if ( newLocation < 0 ) {
				throw new IllegalArgumentException( "Location " + location + " moved to string " + newLocation + ", below min string." );
			}
			if ( newLocation > fretboard.getStringCount() ) {
				throw new IllegalArgumentException( "Location " + location + " moved to string " + newLocation + ", above max string." );
			}
			location.setString( location.getString() + offset );
		}
	}
	
	/** Returns the min string of the list. */
	public int minString() {
		int minString = Integer.MAX_VALUE;
		for ( Location location : list ) {
			if ( location.getString() < minString ) minString = location.getString();
		}
		return minString;				
	}
	
	/** Returns the max string of the list. */
	public int maxString() {
		int maxString = Integer.MIN_VALUE;
		for ( Location location : list ) {
			if ( location.getString() > maxString ) maxString = location.getString();
		}
		return maxString;				
	}
	
	/** Returns a sorted list of strings of this location list. */
	public List<Integer> getStringSet() {
		List<Integer> strings = new LinkedList<Integer>();
		for ( Location location : list ) {
			if ( !strings.contains( location.getString() ))
   			   strings.add(location.getString() );
		}
		Collections.sort( strings );			
		return strings; 
	}
	
	/** Returns a sorted list of frets for this location list. */
	public List<Integer> getFretSet() {
		List<Integer> frets = new LinkedList<Integer>();
		for ( Location location : list ) {
			if ( !frets.contains( location.getFret() ))
   			   frets.add(location.getFret() );
		}
		Collections.sort( frets );			
		return frets; 
	}

	/** Returns the number of locations in the given bounds. */
	public int getInBoundsCount( int minFret, int maxFret ) {
		return getBoundedCount( minFret, maxFret, true );
	}
	
	/** Returns the number of locations out of the given bounds. */
	public int getOutBoundsCount( int minFret, int maxFret ) {
		return getBoundedCount( minFret, maxFret, false );
	}
	
	/** Returns the number of location in/out of the given range, inclusive. */
	public int getBoundedCount( int minFret, int maxFret, boolean inBounds ) {
		int count = 0;
		for ( Location location : list ) {
			int fret = location.getFret();
			if ( inBounds ) {
               if (( fret >= minFret ) && ( fret <= maxFret ))
            	   count += 1;
			} else { // outOfBounds
               if (( fret < minFret ) || ( fret > maxFret ))
	                count += 1;				
			}
		}
		return count; 
	}

	/** Returns the subset of location in of the given range, inclusive. */
	public LocationList getInBounds( int minFret, int maxFret ) {
		return getBounded( minFret, maxFret, true );
	}
	
	/** Returns the subset of location in of the given range, inclusive. */
	public LocationList getOutBounds( int minFret, int maxFret ) {
		return getBounded( minFret, maxFret, false );
	}
	
	/** Returns the subset of location in/out of the given range, inclusive. */
	public LocationList getBounded( int minFret, int maxFret, boolean inBounds ) {
		LocationList subList = new LocationList();
		for ( Location location : list ) {
			int fret = location.getFret();
			if ( inBounds ) {
               if (( fret >= minFret ) && ( fret <= maxFret ))
            	   subList.add( new Location( location ) );
			} else { // outOfBounds
               if (( fret < minFret ) || ( fret > maxFret ))
	                subList.add( new Location( location ) );				
			}
		}
		return subList; 
	}
	
	/** Returns number of strings skipped. Only counts interior skips, e.g. 
	 * XX0000, X0000X, 0000XX == 0, 
	 * X0X00X, 00X00X == 1, 
	 * 0XX000, 00XX00 == 2
	 * @param locations
	 * @return
	 */
	public int getSkippedStringCount() {
		Set<Integer> strings = new TreeSet<Integer>(); // ordered set
		for ( Location location : list ) {
			strings.add( location.getString() );
		}
		int score = 0;
		int previous = Integer.MIN_VALUE;
		for (Integer stringi : strings ) {
			if ( previous == Integer.MIN_VALUE )
				previous = stringi - 1;
			score += ( stringi - previous ) - 1;
			previous = stringi;
		}
		return score;
	}
	
	/** Returns a note list indicating the notes for these locations. */
	public NoteList getNoteList( final Fretboard fretboard ) {
		if ( null == fretboard )
			throw new IllegalArgumentException( "Fretboard must be provided to get note list." );
		NoteList noteList = new NoteList();
		for ( Location location : list ) {
			Note currentNote = location.getNote( fretboard );
			noteList.add( currentNote );
		}
		return noteList;
	}
	
	/** 
	 * Returns a string indicating the notes for these locations. 
	 */
	public String getNotes( final Fretboard fretboard, String space ) {
		return getNoteList( fretboard ).toString( space );
	}
	
	public String getNotes( final Fretboard fretboard ) {
		return getNoteList( fretboard ).toString( "," );
	}

	/** Returns a string indicating the formula related to the given root. 
	 * Examples:
	 *    list="F G# C D#" root="F" returns "R-b3-5-b7" 
	 *    list="D# G# C F" root="F" returns "b7-b3-5-R" 
	 *    list="D# G# C F" root="B" returns "2-5-7-8" 
	 */
	public String getFormula( final Fretboard fretboard, final Note root, String space ) {
		// These names are used rather than interval just to pick a preferred name.
		StringBuffer sb = new StringBuffer();
		
		for ( int i = 0; i < list.size(); i ++ ) {
			Location location = list.get( i );
			Note currentNote = location.getNote( fretboard );
   		    sb.append( currentNote.getQualityName( root ) );
			if ( i < list.size() - 1 ) {
				sb.append( space );							
			}
		}
		return sb.toString();
	}
	
	public String getFormula( final Fretboard fretboard, final Note root ) {
		return getFormula( fretboard, root, "-" );
	}

	/** Removes any notes from the list, not within the given min and max. Returns delete count. */
	public int filter( Location min, Location max ) {
		int delCount = 0;
		
		// Note. Cannot iterate and delete from the same list. ConcurrentModificationException.
		List<Location> removeList = new LinkedList<Location>();
		for ( Location location : list ) {
			if (( location.freti < min.freti ) || ( location.freti > max.freti) ||
				( location.stringi < min.stringi ) || ( location.stringi > max.stringi)) {
				removeList.add( location );
				delCount += 1;;
			}
		}
		for ( Location location : removeList ) {
			list.remove(location);
		}

		return delCount;
	}

	public synchronized void sort() {
		if ( null != list && list.size() > 0 ) {
			Location[] sortedList = new Location[ list.size() ];
			sortedList = list.toArray(sortedList);
			Arrays.sort(sortedList);
			this.set( sortedList );
		}
	}
	
	protected List<Location> list = new LinkedList<Location>();
}