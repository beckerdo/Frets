package frets.main;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Encapsulates a group of notes.
 * Can be used as base for scales, chords, arpeggios, melodies.
 * Consider collections that have unique notes, sorted notes, repeated notes.
 *
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class NoteList implements List<Note>, Comparable<NoteList> {
	public static final String DELIM = ",";
	
	public NoteList( ) {
	}
	
	/** Make a new set, clone, without touching the original. */
	public NoteList( final NoteList other ) {
		// addAll( other );
		if ( null != other ) {
			for ( int i = 0; i < other.size(); i++ )
				list.add( new Note( other.get( i ))); // deep copy
		}
	}
	
	/** Make a new set, without touching the original. */
	public NoteList( final Collection<? extends Note> other ) {
		// addAll( other );
		if ( null != other ) {
			for ( Note notei : other ) {
				list.add( new Note( notei ) );
			}
		}
	}
	
	/** Make a new set, without touching the original. */
	// Note: Why have this constructor when we have NoteList( final Note ... other ) ?
	// Older versions of Java find the constructors ambiguous when creating a 
	// NoteList with just one note. That constructor also matches
	// NoteList( final Note root, final Interval ... other )
	public NoteList( final Note other ) {
		// addAll( Arrays.asList( other ) );
		if ( null != other ) {
			list.add( new Note( other ) ); // deep copy
		}
	}

	/** Make a new set, without touching the original. */
	public NoteList( final Note ... other ) {
		// addAll( Arrays.asList( other ) );
		if ( null != other ) {
			for ( int i = 0; i < other.length; i++ )
				list.add( new Note( other[ i ] ) ); // deep copy
		}
	}

	/** Make a new set, out of intervals or absolute values.. */
	public NoteList( final int ... other ) {
		if ( null != other ) {
			for ( int i = 0; i < other.length; i++ )
				list.add( new Note( other[ i ] ) ); // deep copy
		}
	}

	/** Make a new set, out of intervals or absolute values.. */
	public NoteList( final Interval ... other ) {
		if ( null != other ) {
			for ( int i = 0; i < other.length; i++ )
				list.add( new Note( other[ i ].getValue() ) ); // deep copy
		}
	}

	/** Make a new note list, out of intervals or absolute values.. */
	public NoteList( final Note rootNote, final Interval ... intervals ) {		
		if ( null != rootNote ) {
			for (Interval interval : intervals) {
				list.add(Note.plus(rootNote, interval.getValue()));
			}
		}
	}

	/** Make a note list from the given root and String formula. */
	public NoteList( final Note root, String formula ) {
		this();
		this.setRelative( root, formula);
	}

	/** Make a new instance from a String produced by toString of this class. */
	public NoteList( String toString ) {
		this();
		if ( toString == null ) 
			throw new IllegalArgumentException( "Bad constructor string=\"" + toString + "\"" );
		toString = toString.replaceAll( "\\s", "" );
		if ( toString.length() < 1 )
			throw new IllegalArgumentException( "Bad constructor string=\"" + toString + "\"" );
		String [] notes = toString.split( DELIM );
		// System.out.println( "NoteList toString=" + Arrays.asList( notes ));
		for ( String note : notes ) {
			list.add( new Note( note ));
		}
	}
	
	/** Make a new instance from a String base on the {@link NoteList} constructor of this class. */
	public static NoteList parse( String toString ) {
		return new NoteList( toString );
	}

	// Setters
	public NoteList set( final NoteList other ) {
		clear();
		if ( null != other ) {
			for ( int i = 0; i < other.size(); i++ )
				list.add( new Note( other.get( i ))); // deep copy
		}
		return this;
	}
	
	public NoteList set( final Collection<? extends Note> other ) {
		clear();
		if ( null != other ) {
			for ( Note notei : other ) {
				list.add( new Note( notei ) );
			}
		}
		return this;
	}
	
	public NoteList set( final Note ... other ) {
		clear();
		if ( null != other ) {
			for ( int i = 0; i < other.length; i++ )
				list.add( new Note( other[ i ] ) ); // deep copy
		}
		return this;
	}
	
	/** Go from params C W-W-h to list of notes.
	 *  Also works with any interval name e.g. C root third fifth
	 *  Also works with absolute values e.g. C 1 3 5 */
	public NoteList setRelative( final Note first, String formula ) {
		clear();
		if ( null != first ) {
			// add( first );
			Interval [] intervals = Interval.getIntervals(formula);
			for ( Interval interval : intervals ) {
				add( Note.plus( first, interval.getValue() ));
			}
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
		NoteList other = (NoteList) obj;
		// internal test
		// Compare lists
		int compare = this.compareTo(other);
		return (compare == 0);
	}

	@Override
	public String toString() {
		return toString( DELIM );
	}
	
	public String toString( String delim ) {
		StringBuilder sb = new StringBuilder(  );
		for( int i = 0; i < list.size(); i++ ) {
			Note note = list.get( i );
			sb.append(note.toString());
			if ( i + 1 < list.size())
			   sb.append( delim );			
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

	public static NoteList fromJSON( String json ) {
		NoteList value = null;
		try {
			com.fasterxml.jackson.databind.ObjectMapper jsonMapper = 
				new com.fasterxml.jackson.databind.ObjectMapper();
			value = jsonMapper.readValue(json, NoteList.class);
		} catch (Exception e) { // JsonParseExcetpion, JsonMappingException, IOException
			e.printStackTrace();
		}
		return value;
	}


	/** Notes should be of same order and length to be equal.
	 * First lower note is less than
	 * Shorter sequences are less than longer sequences
	 * Examples:
	 * A < B
	 * ABC < ABD
	 * AAA < BB
	 * AAA < AAAA
	 * ABC < ABCD
	 */
	public int compareTo(NoteList o) {
		if ((null == list) && (null == o.list))
			return 0;
		if ((null == list) && (null != o.list))
			return -1;
		if ((null != list) && (null == o.list))
			return 1;
		int shorter = list.size();
		if ( o.list.size() < shorter )
			shorter = o.list.size();
		// Note list should be of same size and order
		for ( int i = 0; i < shorter; i ++ ) {
		   Note a = list.get( i );
		   Note b = o.list.get( i );
		   int noteCompare = a.compareTo(b);
		   if ( noteCompare != 0)
			   return noteCompare;
		}
        // Lists are equal for first N notes.
		if (list.size() < o.list.size())
			return -2;
		if (list.size() > o.list.size())
			return 2;
		return 0;
	}

	// Collection API
	public boolean add(Note o) {
		return list.add(o);
	}

	public boolean addAll(Collection<? extends Note> c) {
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

	public Iterator<Note> iterator() {
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
	public void add(int index, Note element) {
		list.add(index, element);
	}

	public boolean addAll(int index, Collection<? extends Note> c) {
		return list.addAll(index, c);
	}

	public Note get(int index) {
		return list.get(index);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<Note> listIterator() {
		return list.listIterator();
	}

	public ListIterator<Note> listIterator(int index) {
		return list.listIterator(index);
	}

	public Note remove(int index) {
		return list.remove(index);
	}

	public Note set(int index, Note element) {
		return list.set(index, element);
	}

	public List<Note> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	/** Adjust all notes by the absolute value given. 
	 * The first note of this set is adjusted to the given note.
	 * The remaining notes of this set are moved by that distance.
	 */
	public NoteList updateAbsolute( final Note root ) {
		if (( null == list ) || (0 == this.size()) || ( null == root ))
			return this;
		Note first = list.get( 0 );
		int distance = root.compareTo( first );
		updateRelative( distance );
		return this;
	}
	
	/** Adjust all notes by the relative interval given. */
	public NoteList updateRelative( final Note interval ) {
		if (( null == list ) || (0 == this.size()) || ( null == interval ))
			return this;
		return updateRelative( interval.getAbsoluteValue() );
	}

	/** Adjust all notes by the relative interval given. */
	public NoteList updateRelative( int interval ) {
		if (( null == list ) || (0 == this.size()) || ( 0 == interval ))
			return this;
		for ( int i = 0; i < this.size(); i++ ) {
			Note notei = list.get( i );
			notei.plus( interval );
		}
		return this;
	}

	/** Adjust all notes by the relative interval given. */
	public NoteList updateRelative( Interval interval ) {
		return this.updateRelative( interval.getValue() );
	}

	/** Get all note intervals relative to the first note. */
	public NoteList getAbsoluteIntervals() {
		if (( null == list ) || (0 == list.size()))
			return null;
		NoteList intervals = new NoteList( this );
		int adjustment = -this.get(0).getAbsoluteValue();
		intervals.updateRelative( adjustment );
		return intervals;
	}
	
	public String toStringIntervals() {
		StringBuilder sb = new StringBuilder( "[" );
		if ( null != list ) {
		   int first = list.get( 0 ).getAbsoluteValue();
		   for ( int i = 0; i < list.size(); i++ ) {
			   if ( i > 0 ) sb.append("," );
			   sb.append( list.get( i ).getAbsoluteValue() - first );
		   }
		}
		sb.append( "]" );
		return sb.toString();
	}

	/** Get all note intervals relative to the previous note. */
	public int [] getRelativeIntervals() {
		if ( null == list) return null;
		int [] intervals = new int[ list.size() ];
	    int previous = list.get( 0 ).getAbsoluteValue();
		   for ( int i = 0; i < list.size(); i++ ) {
			   intervals[ i ] = list.get( i ).getAbsoluteValue() - previous;
			   previous = list.get( i ).getAbsoluteValue();
		   }
		return intervals;
	}
	
	public String toStringRelativeIntervals() {
		StringBuilder sb = new StringBuilder( "[" );
		if ( null != list ) {
		   int previous = list.get( 0 ).getAbsoluteValue();
		   for ( int i = 0; i < list.size(); i++ ) {
			   if ( i > 0 ) sb.append("," );
			   sb.append( list.get( i ).getAbsoluteValue() - previous );
			   previous = list.get( i ).getAbsoluteValue();
		   }
		}
		sb.append( "]" );
		return sb.toString();
	}

	// Fields
	protected List<Note> list = new LinkedList<Note>();
}