package com.zhoubo.listener;

import com.zhoubo.model.Shout;
import com.zhoubo.service.SpittleFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Created by 80011350 on 2016/11/12.
 */
public class ConsumerConfigDataSessionAwareMessageListener implements SessionAwareMessageListener<TextMessage> {

    @Autowired
    private SpittleFeedService spittleFeedService;

//    public void onMessage(MapMessage message, Session session) throws JMSException {
////        Map runData = (Map) message.getObject("configData");
//
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//        System.out.println("收到一条控制数据");
//        System.out.println("configData：" + message.getString("configData"));
//
//        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
//    }

    @Override
    public void onMessage(TextMessage message, Session session) throws JMSException {

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
        System.out.println("收到一条控制数据");
        System.out.println("<<<<<<<<<<<" + message.getText());
        Shout shout = new Shout();
        shout.setMessage(message.getText());
        spittleFeedService.broadcastSpittle(shout);
        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
}
