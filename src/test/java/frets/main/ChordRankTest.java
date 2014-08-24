package frets.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests to validate this class.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
public class ChordRankTest 
{
    @Test
    public void testNullMock() 
    {
    	SimpleProperties<ChordRank> mock = ChordRank.instance.getInstance("");
    	assertNull( "Null instance", mock );
    }
    
    @Test
    public void testMock() 
    {
    	SimpleProperties<ChordRank> mock = ChordRank.instance.getInstance( ChordRank.STANDARD );
    	// System.out.println( "ChordRankTest name=\"" + mock.getName() + "\", description=\"" + mock.getDescription() + "\".");
    	assertEquals( "Name", "Standard",  mock.getMetaName() );
    	assertTrue( "Description", mock.getMetaDescription().startsWith("Ranks chords") );
    	assertEquals( "Location", "src\\main\\resources\\frets\\main\\chordrank.standard.properties",  mock.getMetaLocation() );
    }
}