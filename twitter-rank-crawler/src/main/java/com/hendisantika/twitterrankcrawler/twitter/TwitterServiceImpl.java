package com.hendisantika.twitterrankcrawler.twitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Project : twitter-analytics-parent
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 25/01/18
 * Time: 06.47
 * To change this template use File | Settings | File Templates.
 */

@Service
public class TwitterServiceImpl implements TwitterService {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static final String QUEUE_NAME = "twitter.followers";
    // These two fields are constants that target users below follows/following thresholds
    private static final Integer MAX_FOLLOWS = 50000;
    private static final Integer MAX_FOLLOWERS = 50000;
    private final Log log = LogFactory.getLog(TwitterService.class);
    private final Twitter twitter;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public TwitterServiceImpl(Twitter twitter, UserRepository userRepository, RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.twitter = twitter;
        this.userRepository = userRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Discover a user on Twitter using only their screen name
     *
     * @param screenName is the screen name of the user on Twitter
     * @return a user that has been retrieved from the Twitter API and saved to Neo4j
     */
    public User discoverUserByScreenName(String screenName) {
        User user;

        user = Optional.of(twitter.userOperations().getUserProfile(screenName))
                .map(User::new)
                .get();

        // Set the user's default values
        user.setPagerank(0f);
        user.setImported(true);

        user = getUser(user);

        return user;
    }

    /**
     * Discover a user on Twitter using their profile ID
     *
     * @param profileId is the profile ID of the user on thw Twitter API
     * @return a user that has been retrieved from the Twitter API and saved to Neo4j
     */
    public User discoverUserByProfileId(Long profileId) {
        User user;

        user = Optional.of(twitter.userOperations().getUserProfile(profileId))
                .map(User::new)
                .get();

        user = getUser(user);

        log.info(String.format("Discover user: %s", user.getScreenName()));

        return user;
    }

    /**
     * Submit a job to crawl this user only if their follows/follower counts are within limits
     *
     * @param user is the {@link User} that is to potentially be requested for crawling
     * @return the saved {@link User} with full profile information now updated on the Neo4j node
     */
    private User getUser(User user) {
        Long userId = userRepository.getUserIdByProfileId(user.getProfileId());

        if (userId != null) {
            user.setId(userId);
        }

        user = userRepository.save(user, 0);

        try {
            // Only crawl users that have manageable follows/follower counts
            if (user.getFollowerCount() < MAX_FOLLOWERS && user.getFollowsCount() < MAX_FOLLOWS) {
                log.info("Discover user scheduled on follows graph " + dateFormat.format(new Date()));
                user.setDiscoveredTime(new Date().getTime());

                // Update discovery time
                userRepository.save(user, 0);

                // Update the discovery chain
                userRepository.updateDiscoveryChain();

                rabbitTemplate.convertAndSend(QUEUE_NAME, objectMapper.writeValueAsString(user));
            } else {
                // Retry
                User nextUserToCrawl = userRepository.findNextUserToCrawl();

                if (nextUserToCrawl != null) {
                    this.discoverUserByProfileId(nextUserToCrawl.getProfileId());
                }
            }
        } catch (JsonProcessingException e) {
            log.error(e);
        }
        return user;
    }
}