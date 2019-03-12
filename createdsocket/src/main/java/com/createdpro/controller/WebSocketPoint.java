package com.createdpro.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.createdpro.util.FormatText;
import com.createdpro.util.JsonResult;
import com.createdpro.util.MessageResult;

@ServerEndpoint(value = "/websocket")
@Component
public class WebSocketPoint {

	//静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketPoint> webSocketSet = new CopyOnWriteArraySet<WebSocketPoint>();

    private static CopyOnWriteArraySet<MessageResult> users = new CopyOnWriteArraySet<MessageResult>();
    
    private MessageResult user = new MessageResult();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
	
    /**
     * 	连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        try {
            sendMessage(JsonResult.success("101", "服务器连接成功！"));
        } catch (IOException e) {
            System.out.println("IO异常");
        }
    }
    
    /**
     * 	连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
    	System.out.println("即将移除的user：" + this.user);
    	boolean tip = false;
    	if(users.remove(this.user)) {
    		tip = true;
    	}
        webSocketSet.remove(this);  //从set中删除
        if(tip) {
        	sendAllMessage(JsonResult.success("102", this.user.getName()));
        }
        subOnlineCount();           //在线数减1
		synchroUser();
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 	收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
    	System.out.println("message:"+message);
        JSONObject json = JSONObject.parseObject(message);
        int order = json.getInteger("order");
        String name = json.getString("name");
        // 验证名称是否存在
        if(StringUtils.isEmpty(name)) {
        	System.out.println("角色名字不存在！");
        	return;
        }
        user.setName(FormatText.format(name.trim()));
        if(order == 1) {
        	for (MessageResult user : users) {
				if(user.getName().equals(this.user.getName())) {
					try {
						sendMessage(JsonResult.success("105", "账号已存在"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					return;
				}
			}
        	// 返回用户名称和秘钥
        	String userKey = UUID.randomUUID().toString().replace("-", "");
        	user.setUserKey(userKey);
        	try {
				sendMessage(JsonResult.success("106", user));
				sendAllMessage(JsonResult.success("103", user.getName()));
				users.add(user);
				synchroUser();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return;
        }
        
        // 搞事
        // 验证名字存在和秘钥正确
        user.setUserKey((json.getString("userKey")));
        if(users.remove(user)) {
        	user.setText(FormatText.format(json.getString("text")));
        	user.setSendDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        	users.add(user);
        } else {
        	System.out.println("角色名字不存在或者秘钥有误，发送失败");
        	try {
				sendMessage(JsonResult.success("189", "您的发送失败了，请刷新页面并重新进入。"));
			} catch (IOException e) {
				e.printStackTrace();
			}
        	return;
        }
        
        // 群发消息
		sendAllMessage(JsonResult.success("188", user));
        
    }
    
    /**
     * 	每次发生信息变动都将最新的数据群发给前端
     */
    public void synchroUser() {
		Set<String> set = new HashSet<String>();
    	for (MessageResult user : users) {
    		set.add(user.getName());
	    }
		sendAllMessage(JsonResult.success("180", set));
    }
    
    /**
     * 	发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    public void sendMessage(JsonResult result) throws IOException {
//		this.session.getBasicRemote().sendObject(result);
        this.session.getBasicRemote().sendText(JSON.toJSONString(result));
//        this.session.getAsyncRemote().sendText(message);
    }
    
    public void sendAllMessage(JsonResult result){
    	// 群发消息
        for (WebSocketPoint item : webSocketSet) {
            try {
                item.sendMessage(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
    	WebSocketPoint.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
    	WebSocketPoint.onlineCount--;
    }
}
