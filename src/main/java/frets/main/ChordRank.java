package frets.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import frets.util.FilenameRegExFilter;

/** 
 * Ranks the playability of notes.
 * <p> 
 * Scores may be the composite of numerous gradings.
 * In such case, a  {@link compositeScore} of numerous gradings may
 * be returned as an array. The first element in the array is
 * the composite of all gradings. Individual gradings follow
 * the main composite score in the array. 
 * The {@link compositeScoreNames}method returns descriptions of the score.
 * <p>
 * This class scores the following attributes:
 *    - fret bounds (within given min and max)
 *    - fret span (lower is better)
 *    - string skip score (number of skipped strings, normally 1, can be 0 or greater)
 *    - same string (number of notes on same string, normally 1, can be 0 or greater)
 *    - composite ( add all above scores)
 * A lower score is more playable with 0 satisfying all constraints.
 * 
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class ChordRank implements Comparator<LocationList>, SimpleProperties<ChordRank> {
	public final static String ELEMENT_DELIM = ",";
	public final static String KEY_VAL_DELIM = "=";
	
	
	public final static String STANDARD = "Standard";
	
    public ChordRank() {
    }
	
    public ChordRank( int minFret, int maxFret ) {
        setMinFret( minFret );
        setMaxFret( maxFret );
     }
	
    public ChordRank( int minFret, int maxFret, int skipStringPenalty, int sameStringPenalty) {
        setMinFret( minFret );
        setMaxFret( maxFret );
        setSkipStringPenalty(skipStringPenalty);
        setSameStringPenalty(sameStringPenalty);
     }
	
    // Object role
	public int compare(final LocationList o1, final LocationList o2) {
	   int o1Score = compositeScore( o1 )[ 0 ];
	   int o2Score = compositeScore( o2 )[ 0 ];
	   return o1Score - o2Score;	   
   }
   public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ChordRank other = (ChordRank) obj;
		int compare =
		   ( this.minFret - other.minFret ) + 
		   ( this.maxFret - other.maxFret ) +
           ( this.sameStringPenalty - other.sameStringPenalty) + 			
           ( this.skipStringPenalty - other.skipStringPenalty); 			
		return (0 == compare);
	}

	public int getMinFret() {
		return minFret;
	}

	public void setMinFret(int minFret) {
		this.minFret = minFret;
	}

	public int getMaxFret() {
		return maxFret;
	}

	public void setMaxFret(int maxFret) {
		this.maxFret = maxFret;
	}

	public int getSkipStringPenalty() {
		return skipStringPenalty;
	}

	public void setSkipStringPenalty(int skipStringPenalty) {
		this.skipStringPenalty = skipStringPenalty;
	}

	public int getSameStringPenalty() {
		return sameStringPenalty;
	}

	public void setSameStringPenalty(int sameStringPenalty) {
		this.sameStringPenalty = sameStringPenalty;
	}

	/** Score 0 for fret in bounds, N distance for fret out of bounds. */
	public int fretBoundsScore( final LocationList locations ) {
		if ((null == locations) || ( locations.size() == 0 )) return 0;
		int score = 0;
		for ( Location location : locations ) {
			int fret = location.getFret();
			if (fret < minFret)
			   score += minFret - fret;
            if ( fret > maxFret )
               score += fret - maxFret;
		}
		return score;
	}
	
	/** 
	 * Fret span 	score
	 * 0-5			1 * fret span
	 * 6-10			2 * fret span
	 * 11+			4 * fret span
	 */
	public int fretSpanScore( final LocationList locations ) {
		int fretSpan = locations.fretSpan();
		if ( fretSpan < 6 ) return fretSpan; 
		if ( fretSpan < 11) return 2 * fretSpan;
		return 4 * fretSpan;
	}
	
	/** Score 0 for no skipped strings, N distance for fret out of bounds. */
	public int skipStringScore( final LocationList locations ) {
		if ((null == locations) || ( locations.size() == 0 )) return 0;
		
		int score = locations.getSkippedStringCount();
		score *= skipStringPenalty;
		return score;
	}
	
	/** Score 0 for all notes on different strings, N for each repeat. */ 
	public int sameStringScore( final LocationList locations ) {
		if ((null == locations) || ( locations.size() == 0 )) return 0;
		int score = locations.size() - locations.getStringCount();
		score *= sameStringPenalty;
		return score;
	}

	/** 
	 * Sum score of multiple components.
	 * fret bounds (0..N) + 
	 * fret span (0..N) + 
	 * skipped string score (0..N) +
	 * same string score (0..N).
	 *  Lower is better. */
	public int getSum( final LocationList locations ) { 
		int component1 = fretBoundsScore( locations );
		int component2 = fretSpanScore( locations );
		int component3 = skipStringScore( locations );
		int component4 = sameStringScore( locations );
		int sum = component1 + component2 + component3 + component4;
		return sum;
	}
	
	/** 
	 * Composite score of multiple components.
	 * The first element of a composite score is a composite,
	 * usually by adding individual elements.
	 * The following array entries are individual scores
	 * that make up the composite.
	 * sum 
	 * fret bounds (0..N) + 
	 * fret span (0..N) + 
	 * skipped string score (0..N) +
	 * same string score (0..N).
	 *  Lower is better. */
	public int [] compositeScore( final LocationList list ) {
		int component1 = fretBoundsScore( list );
		int component2 = fretSpanScore( list );
		int component3 = skipStringScore( list );
		int component4 = sameStringScore( list );
		int sum = component1 + component2 + component3 + component4;
		return new int [] { sum, component1, component2, component3, component4 };		
	}

	/**
	 * Provides a pretty printed string of the scores for this list.
	 */
	public String getScoreString( LocationList list ) {
		int [] scores = compositeScore( list );
		return toString( scores );
	}

	/**
	 * Provides a description of the composite scores.
	 */
	public static String [] compositeScoreNames() {
		return new String [] { "Sum", "Fret Bounds", "Fret Span", "Skip Strings", "Same String" };		
	}
	
	/** Provides a pretty printed string of the scores from the given array. */
	public String toString( int [] scores ) {
		String [] names = compositeScoreNames();
        StringBuilder sb = new StringBuilder( "Scores" );
        sb.append( " " + names[ 0 ].toLowerCase() + "=" + scores[ 0 ] );
        sb.append( ELEMENT_DELIM + " " + names[ 1 ].toLowerCase() + "[" + minFret + "," + maxFret + "]" + KEY_VAL_DELIM + scores[ 1 ] );
        sb.append( ELEMENT_DELIM + " " + names[ 2 ].toLowerCase() + KEY_VAL_DELIM + scores[ 2 ] );
        sb.append( ELEMENT_DELIM + " " + names[ 3 ].toLowerCase() + KEY_VAL_DELIM + scores[ 3 ] );
        sb.append( ELEMENT_DELIM + " " + names[ 4 ].toLowerCase() + KEY_VAL_DELIM + scores[ 4 ] );
      	return sb.toString();
	}

	/** Given a string from toString, produce an array of the scores. */
    public static int [] toScores( String scoreString ) {
        // "Scores sum=22, fret bounds[0,15]=0, fret span=7, skip strings=5, same string=10"
    	if (( null == scoreString ) || ( scoreString.length() < 1 )) return new int [] { 0 };
        StringTokenizer st = new StringTokenizer( scoreString, ELEMENT_DELIM + KEY_VAL_DELIM );
        st.nextToken();
        int sum = Integer.parseInt( st.nextToken() );
        // String sum = scanner.next();
        st.nextToken();
        st.nextToken();
        int fret = Integer.parseInt( st.nextToken() );
        st.nextToken();
        int span = Integer.parseInt( st.nextToken() );
        st.nextToken();
        int skip = Integer.parseInt( st.nextToken() );
        st.nextToken();
        int same = Integer.parseInt( st.nextToken() );
        int [] scores  = new int [] { sum, fret, span, skip, same }; 
        return scores;
    }
	
	protected int minFret = 0;
	protected int maxFret = 18;
	protected int skipStringPenalty = 1;
	protected int sameStringPenalty = 1;
	
	// SimpleProperties implementation
	protected String metaName;
	protected String metaDescription;
	protected String metaLocation;
	protected static Map<String,ChordRank> propertiesMap; // static collection for all these objects.
	public static ChordRank instance = new ChordRank();
	
	public String getMetaName() {
		return metaName;
	}
	public String getMetaDescription() {
		return metaDescription;
	}
	public String getMetaLocation() {
		return metaLocation;
	}
	
	/** Opens a file at the given name, and reads all the properties into an object. */
	public ChordRank readFromFile( String fileName ) throws IOException {
		ChordRank mock = new ChordRank();
		mock.metaLocation = fileName;
				
		// Read properties file. 
		Properties properties = new Properties();
		properties.load(new FileInputStream( fileName ));

		// Convert from properties to object
		for ( Iterator<Object> it = properties.keySet().iterator(); it.hasNext(); ) {
			String key = (String) it.next();
			String value = properties.getProperty( key );
			if ( "chordrank.name".equals( key ) ) {
				mock.metaName = value;
			} else if ( "chordrank.description".equals( key ) ) {
				mock.metaDescription = value;
			} else if ( "chordrank.minFret".equals( key ) ) {
				mock.minFret = Integer.parseInt( value );
			} else if ( "chordrank.maxFret".equals( key ) ) {
				mock.maxFret = Integer.parseInt( value );
			} else if ( "chordrank.skipStringPenalty".equals( key ) ) {
				mock.skipStringPenalty = Integer.parseInt( value );
			} else if ( "chordrank.sameStringPenalty".equals( key ) ) {
				mock.sameStringPenalty = Integer.parseInt( value );
			} else {
				throw new IllegalArgumentException( "Class=\"" + ChordRank.class.getName() + "\" key \"" + key + "\" not handled, value=\"" + value + "\"." );
			} 
		}
		return mock;
	}

	/** Opens a path at the given name, attempts to read files from there.
	 *  Use optional filter as a java.io.FilenameFilter. */
	public List<ChordRank> readFromPath( String pathName, String filterString ) throws IOException {
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
		
		List<ChordRank> list = new LinkedList<ChordRank>();    		
		for ( File file : files ) {
           list.add(  readFromFile(file.getPath()) );			
		}
		return list;
	}

	/** Returns an object fretboard that has been loaded from a
	 *  central repository or properties list. */
	public ChordRank getInstance( String shortName ) {
		if ( null == propertiesMap ) {
			// Lazy instantiation
			try {
				propertiesMap = loadProperties("src/main/resources/frets/main/", "chordrank.*.properties");
			} catch (IOException e) {
				System.out.println(e);
			}
		}
		return propertiesMap.get( shortName );
	}

	/** Performs the loading of configured objects from the given location. */
	public Map<String,ChordRank> loadProperties(String pathName, String filterString) throws IOException {
		   Map<String,ChordRank> propertiesMap = new HashMap<String,ChordRank>();
		   List<ChordRank> propertiesList = readFromPath(pathName,filterString);
		   for (ChordRank ChordRank : propertiesList) {
			   propertiesMap.put(ChordRank.getMetaName(),ChordRank);
		   }
		   return propertiesMap;	       		
	}	
}