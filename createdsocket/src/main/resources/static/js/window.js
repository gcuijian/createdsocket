var NAME = '';
var USERKEY = '';

$(function(){
	$('#toStart').click(toStart);
});

function toStart(){
	
	getWebSocket();
	
	if(!initSocket()){
		return;
	}
	
	$('body').html('<div id="messagePanel">'+
						'<div id="messageLeft">'+
							'<div id="friendListTitle">'+
								'当前在线列表'+
							'</div>'+
							'<ul id="friendList">'+
								'<li>张三</li>'+
							'</ul>'+
						'</div>'+
						'<div id="messageRight">'+
							'<div id="messageList">'+
								'<h6>相聚又分别：（2019-02-25 14:45:34）</h6>'+
								'<p>'+
									'您吃了吗？'+
								'</p>'+
							'</div>'+
							'<div id="messageSend">'+
								'<hr />'+
								'<textarea placeholder="发表一下看法~"></textarea><br />'+
								'<a id="sendNotice">当前的身份：别输给这世界</a> '+
								'<a id="sendNoticeModify" href="javascript:void(0)">点此修改</a>'+
								'<input id="toSendMessage" type="button" value="发送" />'+
							'</div>'+
						'</div>'+
					'</div>');
					
	// 获取当前页面的高度，并进行设置
	var height = $(window).height();
	$('#messagePanel').css('height',height);
	
	initMessageList();
	
	$('#toSendMessage').click(sendToServer);
}

function initMessageList(){
	$('#messageList').scrollTop( $('#messageList')[0].scrollHeight);
}


function setInNotice(name){
	$('#messageList').append('<p class="messageNotice"> 欢迎<b> '+ name +' </b>加入房间~ </p>');
	initMessageList();
}

function setOutNotice(name){
	if(name != undefined && name != 'undefined'){
		$('#messageList').append('<p class="messageNotice"><b> '+ name +' </b>离开了房间~ </p>');
	}
	initMessageList();
}

function setErrorNotice(text){
	if(name != undefined && name != 'undefined'){
		$('#messageList').append('<p class="messageNotice"><b> '+ text +' </b> </p>');
	}
	initMessageList();
}

function setFriends(data){
	var list = '';
	for (var i = 0; i<data.length; i++) {
		list += '<li>'+ data[i] +'</li>';
	}
	$('#friendList').html(list);
}

function setMessage(data){
	$('#messageList').append(	'<h6>'+ data.name +'：（'+ data.sendDate +'）</h6>'+
								'<p>'+
									data.text +
								'</p>');
	initMessageList();
}

function setErrorMessage(message){
	alert(message);
	location.reload();
}

function saveMessageKey(name, userKey){
	NAME = name;
	USERKEY = userKey;
	$('#sendNotice').text('当前的身份：' + name);
	$('#sendNoticeModify').click(function(){
		location.reload();
	});
}

function initSocket(){
	// 取出名字，完成初始化
	var name = $('#name').val();
	if(name == null || name == '' || name == undefined || name.trim() == ''){
		$('#fuck').remove();
		$('#loadWindow').append('<p id="fuck" style="text-align:center;color:red;">Fuck you! Why don\'t you input you name?</p>');
		return 0;
	}
	send(1,name,null,null);
	return 1;
}

function sendToServer(){
	// 发出消息
	var text = $('#messageSend textarea').val();
	var name = NAME;
	var userKey = USERKEY;
	send(2,name,userKey,text);
	$('#messageSend textarea').val('');
}


