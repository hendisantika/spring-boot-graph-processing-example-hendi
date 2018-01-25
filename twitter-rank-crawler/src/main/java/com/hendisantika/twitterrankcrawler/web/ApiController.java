package com.hendisantika.twitterrankcrawler.web;

import com.hendisantika.twitterrankcrawler.twitter.TwitterService;
import com.hendisantika.twitterrankcrawler.twitter.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


/**
 * Created by IntelliJ IDEA.
 * Project : twitter-analytics-parent
 * User: hendisantika
 * Email: hendisantika@gmail.com
 * Telegram : @hendisantika34
 * Date: 25/01/18
 * Time: 06.58
 * To change this template use File | Settings | File Templates.
 */

@RestController
@RequestMapping("v1")
public class ApiController {
    private final TwitterService twitterService;

    @Autowired
    public ApiController(TwitterService twitterService) {
        this.twitterService = twitterService;
    }

    @RequestMapping(path = "user/{screenName}", method = RequestMethod.GET)
    public ResponseEntity<User> discoverProfileByScreenName(@PathVariable("screenName") String screenName) {
        return Optional.of(ResponseEntity.ok(twitterService.discoverUserByScreenName(screenName)))
                .or(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
