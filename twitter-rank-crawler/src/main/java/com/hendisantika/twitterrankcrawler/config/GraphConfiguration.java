package com.hendisantika.twitterrankcrawler.config;

import com.hendisantika.twitterrankcrawler.twitter.FollowsRepository;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.server.security.auth.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by IntelliJ IDEA.
 * Project : twitter-analytics-parent
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 25/01/18
 * Time: 06.39
 * To change this template use File | Settings | File Templates.
 */

@EnableNeo4jRepositories(basePackageClasses = {FollowsRepository.class, UserRepository.class})
@EnableTransactionManagement
@Configuration
public class GraphConfiguration extends Neo4jConfiguration {

    @Value("${spring.neo4j.host}")
    private String host;

    @Value("${spring.neo4j.port}")
    private String port;

    @Bean
    public Neo4jServer neo4jServer() {
        return new RemoteServer(String.format("http://%s:%s", host, port));
    }

    @Bean
    public SessionFactory getSessionFactory() {
        return new SessionFactory(UserRepository.class.getPackage().getName());
    }

}