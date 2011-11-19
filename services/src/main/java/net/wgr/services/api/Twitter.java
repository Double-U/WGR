/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wgr.services.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.wgr.settings.Settings;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 *
 * @author double-u
 */
public class Twitter {

    private static Map<String, Twitter> instances;

    public static Twitter getInstance() {
        Settings s = Settings.getInstance();
        return getTwitterWith(s.getString("Twitter.AccessToken"), s.getString("Twitter.AccessTokenSecret"));
    }

    public static Twitter getUnboundInstance() {
        Twitter t = new Twitter();
        return t;
    }

    public static Twitter getTwitterWith(String accessToken, String secret) {
        if (instances == null) {
            instances = new HashMap<>();
        }
        Twitter instance = null;
        if (!instances.containsKey(accessToken)) {
            instance = new Twitter(accessToken, secret);
            instances.put(accessToken, instance);
        } else {
            instance = instances.get(accessToken);
        }
        return instance;
    }
    private twitter4j.Twitter t;
    protected TwitterStream ts;

    private Twitter() {
        // Whoa, didn't know this existed!
        this("", "");
    }

    private Twitter(String accessToken, String secret) {
        Settings s = Settings.getInstance();
        ConfigurationBuilder cfgb = new ConfigurationBuilder();
        cfgb.setDebugEnabled(true);
        cfgb.setUseSSL(true);
        cfgb.setOAuthConsumerKey(s.getString("Twitter.ConsumerKey"));
        cfgb.setOAuthConsumerSecret(s.getString("Twitter.ConsumerSecret"));
        if (!accessToken.isEmpty() && !secret.isEmpty()) {
            cfgb.setOAuthAccessToken(accessToken);
            cfgb.setOAuthAccessTokenSecret(secret);
        }
        cfgb.setIncludeEntitiesEnabled(true);
        Configuration cfg = cfgb.build();
        t = new TwitterFactory(cfg).getInstance();
        ts = new TwitterStreamFactory(cfg).getInstance();
    }

    public TwitterStream getTwitterStream() {
        return ts;
    }

    public void tweet(StatusUpdate su) {
        try {
            t.updateStatus(su);
        } catch (TwitterException ex) {
            Logger.getLogger(getClass().getName()).log(Level.ERROR, "Tweet failed", ex);
        }
    }

    public List<Status> getLatestTweets(int count) throws TwitterException {
        t.verifyCredentials();
        ResponseList list = t.getUserTimeline("double2u");
        List<Status> subList = list.subList(0, count > 20 ? 20 : count);

        return subList;
    }

    public RequestToken requestToken() {
        try {
            return t.getOAuthRequestToken();
        } catch (TwitterException ex) {
            Logger.getLogger(getClass().getName()).log(Level.ERROR, "Unable to request access token", ex);
        }
        return null;
    }

    public AccessToken getAccessTokenFor(RequestToken token, String verifier) {
        try {
            AccessToken at = t.getOAuthAccessToken(token, verifier);
            // Register this instance
            instances.put(token.getToken(), this);
            System.out.println("Hey, I got an access token! token:" + at.getToken() + " secret: " + at.getTokenSecret());
            return at;
        } catch (TwitterException ex) {
            Logger.getLogger(getClass().getName()).log(Level.ERROR, "Unable to get access token", ex);
        }
        return null;
    }
}
