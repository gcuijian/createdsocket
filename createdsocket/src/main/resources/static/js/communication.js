var websocket = null;

//判断当前浏览器是否支持WebSocket
if('WebSocket' in window){
    websocket = new WebSocket("ws://localhost:8886/createdpro/websocket");
}else{
    alert('Not support websocket')
}

function getWebSocket(){
	
	//连接发生错误的回调方法
	websocket.onerror = function(){
	    alert("连接服务器失败！请刷新页面！");
	    location.reload();
	};

	//连接成功建立的回调方法
	websocket.onopen = function(event){
	    console.log("连接服务器成功！");
	}

	//接收到消息的回调方法
	websocket.onmessage = function(result){
		var local = JSON.parse(result.data);
		if(local.message == '188'){
			// 正常的发送消息
			setMessage(local.data);
		} else if(local.message == '180') {
			// 共享在线群友
			setFriends(local.data);
		} else if(local.message == '189') {
			// 如果userKey过期，发送失败
			setErrorNotice(local.data);
		} else if(local.message == '105') {
			// 账号已存在错误提示
			setErrorMessage(local.data);
		} else if(local.message == '106') {
			// 接受服务器发来的秘钥和名字
			saveMessageKey(local.data.name, local.data.userKey);
		} else if(local.message == '103') {
			// 聊天栏提示某某加入了聊天
			setInNotice(local.data);
		} else if(local.message == '102') {
			// 聊天栏提示某某退出了聊天
			setOutNotice(local.data);
		} else if(local.message == '101') {
			console.log("通讯成功！" + result.data);
		}
	}

	//连接关闭的回调方法
	websocket.onclose = function(){
		location.reload();
	}

	//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
	window.onbeforeunload = function(){
	    websocket.close();
	}
	
}

//关闭连接
function closeWebSocket(){
    websocket.close();
}

//发送消息
function send(order,name,key,text){
	var message = '{"order":' + order + ',"name":"' + name + '","key":"' + key + '","text":"'+ text +'"}';
    websocket.send(message);
}