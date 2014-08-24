package frets.main;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collection;

/**
 * An example of how to do parameterized tests.  
 * @author <a href="mailto:dan@danbecker.info">Dan Becker</a>
 */
@RunWith(value = Parameterized.class)
public class ParameterizedTest 
{
	private int testData;
	
    @Parameters
    public static Collection<Object []> data() {
    	// Object [] [] data = new Object [][] {
    	// 		{ new Note( 5, Note.Name.G.getValue()) },
    	// };
    	Object [] [] data = new Object [][] {
    			{ 1 }, { 2}, { 3 }, { 4 }
    	};
    	return Arrays.asList( data );
    }
    
    public ParameterizedTest ( int testData ) {
    	setTestData( testData );
    }
    
    public void setTestData( int testData ) {
    	this.testData = testData;
    }
    
    @Test
    public void testStringParseParameters()    
    {
    	// System.out.println( "Running test with data=" + testData );    
    	assertNotNull( "Test data", testData );
    }
}