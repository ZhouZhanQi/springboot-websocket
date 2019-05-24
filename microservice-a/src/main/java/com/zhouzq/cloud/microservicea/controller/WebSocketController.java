package com.zhouzq.cloud.microservicea.controller;

import com.alibaba.fastjson.JSON;
import com.zhouzq.cloud.microservicea.entity.RequestMessage;
import com.zhouzq.cloud.microservicea.helper.IRedisSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouzq
 * @date 2019/5/24
 * @desc
 */
@RestController
@RequestMapping("/ws")
public class WebSocketController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketController.class);

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private IRedisSession redisSession;

    /**
     * 向执行用户发送请求
     * @param msg
     * @param name
     * @return
     */
    @RequestMapping(value = "send2user")
    public int sendMq2User(String msg, String name){
        // 根据用户名称获取用户对应的session id值
        String wsSessionId = redisSession.get(name);
        RequestMessage demoMQ = new RequestMessage();
        demoMQ.setName(msg);

        // 生成路由键值，生成规则如下: websocket订阅的目的地 + "-user" + websocket的sessionId值。生成值类似:
        String routingKey = getTopicRoutingKey("demo", wsSessionId);
        // 向amq.topi交换机发送消息，路由键为routingKey
        LOGGER.info("向用户[{}]sessionId=[{}]，发送消息[{}]，路由键[{}]", name, wsSessionId, wsSessionId, routingKey);
        amqpTemplate.convertAndSend("amq.topic", routingKey,  JSON.toJSONString(demoMQ));
        return 0;
    }

    /**
     * 获取Topic的生成的路由键
     *
     * @param actualDestination
     * @param sessionId
     * @return
     */
    private String getTopicRoutingKey(String actualDestination, String sessionId){
        return actualDestination + "-user" + sessionId;
    }
}
