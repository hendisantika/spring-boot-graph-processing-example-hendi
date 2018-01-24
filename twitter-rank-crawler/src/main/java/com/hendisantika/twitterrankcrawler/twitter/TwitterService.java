package com.hendisantika.twitterrankcrawler.twitter;

/**
 * Created by IntelliJ IDEA.
 * Project : twitter-analytics-parent
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 25/01/18
 * Time: 06.46
 * To change this template use File | Settings | File Templates.
 */
public interface TwitterService {
    User discoverUserByScreenName(String screenName);

    User discoverUserByProfileId(Long profileId);
}
