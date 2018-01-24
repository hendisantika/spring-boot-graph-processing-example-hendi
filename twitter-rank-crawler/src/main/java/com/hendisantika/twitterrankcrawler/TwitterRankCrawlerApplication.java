package com.hendisantika.twitterrankcrawler;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringCloudApplication
@EnableZuulProxy
@EnableScheduling
public class TwitterRankCrawlerApplication extends SpringBootServletInitializer {


    public static void main(String[] args) {
        new SpringApplicationBuilder(TwitterRankCrawlerApplication.class).web(true).run(args);
    }
}
