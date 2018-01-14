package com.hendisantika.twitterrankweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@SpringBootApplication
@SpringCloudApplication
@EnableZuulProxy
public class TwitterRankWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterRankWebApplication.class, args);
    }
}
