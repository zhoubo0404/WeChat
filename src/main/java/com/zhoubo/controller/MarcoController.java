package com.zhoubo.controller;

import com.zhoubo.model.Shout;
import com.zhoubo.service.SpittleFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/**
 * Created by zhoubo on 2017/7/26.
 */
@Controller
public class MarcoController {

    @Autowired
    private SpittleFeedService spittleFeedService;

    @MessageMapping("/marco")
    public void handleShout(Shout incoming) {
        System.out.println("Received message: " + incoming.getMessage());
    }

    @MessageMapping("/topicmarco")
    @SendTo("/topic/spittlefeed")
    public Shout handleTopicShout(Shout incoming) {
        System.out.println("Received message: " + incoming.getMessage());
        Shout outgoing = new Shout();
        outgoing.setMessage(" test test");
        incoming.setMessage(incoming.getMessage());
        spittleFeedService.broadcastSpittle(outgoing);
        return incoming;
    }
}
