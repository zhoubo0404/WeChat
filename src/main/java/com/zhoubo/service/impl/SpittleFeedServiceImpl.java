package com.zhoubo.service.impl;

import com.zhoubo.model.Shout;
import com.zhoubo.service.SpittleFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

/**
 * Created by zhoubo on 2017/7/26.
 */
@Service
public class SpittleFeedServiceImpl implements SpittleFeedService {

    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Autowired
    public SpittleFeedServiceImpl(SimpMessageSendingOperations simpMessageSendingOperations) {
        this.simpMessageSendingOperations = simpMessageSendingOperations;
    }

    @Override
    public void broadcastSpittle(Shout shout) {
        simpMessageSendingOperations.convertAndSend("/topic/spittlefeed1", shout);
    }
}
