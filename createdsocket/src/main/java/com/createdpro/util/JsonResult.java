package com.createdpro.util;

import java.io.Serializable;

public class JsonResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 86515311587238284L;
	private int status;
	private String message;
	private Object data;
	
	/**
	 * 对外方法
	 */
	
	/**
	 * 返回成功时的数据包
	 */
	public static JsonResult success(Object data) {
		return new JsonResult(data);
	}
	
	/**
	 * 返回成功时的数据包，同时携带消息
	 */
	public static JsonResult success(String message, Object data) {
		return new JsonResult(message, data);
	}
	
	/**
	 * 返回异常数据包
	 */
	public static JsonResult error(int state, Exception e) {
		return new JsonResult(state, e);
	}

	@Override
	public String toString() {
		return "JsonResult [status=" + status + ", message=" + message + ", data=" + data + "]";
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * 私有方法
	 */
	
	private JsonResult() {}
	
	/**
	 * 成功数据
	 * @param data
	 */
	private JsonResult(Object data) {
		super();
		this.data = data;
	}
	
	/**
	 * 成功数据需要带消息
	 */
	private JsonResult(String message, Object data) {
		super();
		this.message = message;
		this.data = data;
	}
	
	/**
	 * 错误数据
	 */
	private JsonResult(int state, Exception e) {
		super();
		this.status = state;
		this.message = e.getMessage();
	}
	
}
