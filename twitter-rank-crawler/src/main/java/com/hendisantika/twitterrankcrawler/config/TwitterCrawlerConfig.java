package com.hendisantika.twitterrankcrawler.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.ogm.session.Neo4jSession;
import org.neo4j.ogm.session.transaction.Transaction;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;


/**
 * Created by IntelliJ IDEA.
 * Project : twitter-analytics-parent
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 25/01/18
 * Time: 06.55
 * To change this template use File | Settings | File Templates.
 */

@Configuration
public class TwitterCrawlerConfig {
    private final Log logger = LogFactory.getLog(TwitterCrawlerConfig.class);
    @Value("${spring.social.twitter.appId}")
    private String appId;
    @Value("${spring.social.twitter.appSecret}")
    private String appSecret;
    @Value("${spring.social.twitter.accessToken}")
    private String accessToken;
    @Value("${spring.social.twitter.accessTokenSecret}")
    private String accessTokenSecret;

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        return new TomcatEmbeddedServletContainerFactory();
    }

    @Bean
    Queue follows() {
        return new Queue("twitter.follows", true, false, false);
    }

    @Bean
    Queue followers() {
        return new Queue("twitter.followers", true, false, false);
    }

    @Bean
    Twitter twitter(final @Value("${spring.social.twitter.appId}") String appId,
                    final @Value("${spring.social.twitter.appSecret}") String appSecret,
                    final @Value("${spring.social.twitter.accessToken}") String accessToken,
                    final @Value("${spring.social.twitter.accessTokenSecret}") String accessTokenSecret) {
        return new TwitterTemplate(appId, appSecret, accessToken, accessTokenSecret);
    }


    Twitter twitters(String appId, String appSecret,
                     String accessToken, String accessTokenSecret) {
        return new TwitterTemplate(appId, appSecret, accessToken, accessTokenSecret);
    }

    @Bean
    CommandLineRunner commandLineRunner(Neo4jSession neo4jSession) {
        return (args) -> {
            // Make sure that a constraint is created on the Neo4j database
            try {
                // This constraint ensures that each profileId is unique per user node
                Transaction tx = neo4jSession.beginTransaction();
                neo4jSession.execute("CREATE CONSTRAINT ON (user:User) ASSERT user.profileId IS UNIQUE");
                tx.commit();
                tx.close();
            } catch (Exception ex) {
                // The constraint is already created or the database is not available
                logger.error(ex);
            }

        };
    }
}
