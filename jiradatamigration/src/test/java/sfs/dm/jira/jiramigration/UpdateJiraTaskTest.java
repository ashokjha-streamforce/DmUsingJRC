package sfs.dm.jira.jiramigration;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for UpdateJiraTask.
 */
public class UpdateJiraTaskTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public UpdateJiraTaskTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( UpdateJiraTaskTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testUpdateJiraTask()
    {
        assertTrue( true );
    }
}
