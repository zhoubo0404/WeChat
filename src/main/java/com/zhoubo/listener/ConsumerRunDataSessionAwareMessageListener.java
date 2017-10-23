package com.zhoubo.listener;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Created by 80011350 on 2016/11/12.
 */
public class ConsumerRunDataSessionAwareMessageListener implements SessionAwareMessageListener<TextMessage> {
//    public void onMessage(Message message, Session session) throws JMSException {
//
//        MapMessage mapMessage = (MapMessage) message;
//        BeautyInstrumentRunData beautyInstrumentRunData = (BeautyInstrumentRunData) mapMessage.getObject("runData");
//        System.out.println("收到一条消息");
//        System.out.println("消息内容是：" + mapMessage.getObject("runData"));
//        System.out.println("消息内容是：" + beautyInstrumentRunData.getUserId());
//    }

//    public void onMessage(MapMessage message, Session session) throws JMSException {
////        BeautyInstrumentRunData beautyInstrumentRunData = (BeautyInstrumentRunData) message.getObject("runData");
////        Map runData = (Map) message.getObject("runData");
//        System.out.println("……………………………………………………………………");
//        System.out.println("收到一条运行数据");
////        System.out.println("userId：" + runData.get("userId"));
////        System.out.println("deviceType：" + message.getInt("deviceType"));
////        System.out.println("deviceSubType：" + message.getInt("deviceSubType"));
////        System.out.println("productId" + message.getInt("productId"));
//        System.out.println("runData：" + message.getString("runData"));
//
//
//        System.out.println("……………………………………………………………………");
//    }

    @Resource
    private JmsTemplate jmsTemplateConfigDataTopic;

    @Override
    public void onMessage(TextMessage message, Session session) throws JMSException {
        System.out.println("[currentThread = " + Thread.currentThread().getName() + "]");
        System.out.println("……………………………………………………………………");
        System.out.println("收到一条运行数据");
        System.out.println("<<<<<<<<<<<" + message.getText());
        System.out.println("……………………………………………………………………");
    }
/*
    @PostConstruct
    public void testPostConstruct() {
        System.out.println("testPostConstruct is running");

        jmsTemplateConfigDataTopic.send(new MessageCreator() {

            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage();
                textMessage.setText("amq test message");
                System.out.println("send message to amq");
                return textMessage;
            }
        });
    }*/
}
