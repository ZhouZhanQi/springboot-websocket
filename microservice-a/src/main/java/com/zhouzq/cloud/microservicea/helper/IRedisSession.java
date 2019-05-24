package com.zhouzq.cloud.microservicea.helper;

/**
 * @author zhouzq
 * @date 2019/5/24
 * @desc
 */
public interface IRedisSession {

    void add(String name, String wsSessionId);

    boolean del(String name);

    String get(String name);
}
