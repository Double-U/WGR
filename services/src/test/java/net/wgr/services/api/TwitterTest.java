/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.services.api;



import junit.framework.TestCase;
import twitter4j.Status;
import twitter4j.TwitterException;

/**
 *
 * @author double-u
 */
public class TwitterTest extends TestCase {
    
    public TwitterTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getLatestTweets method, of class Twitter.
     */
    public void testGetLatestTweets() {
        /*try {
            for (Status st : Twitter.getInstance().getLatestTweets(20)) {
                System.out.println(st.getText());
            }
        } catch (TwitterException ex) {
            fail("Failed with: " + ex.getErrorMessage());
            Logger.getLogger(TwitterTest.class.getName()).log(Level.ERROR, null, ex);
        }*/
    }
}
