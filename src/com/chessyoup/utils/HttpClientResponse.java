package com.chessyoup.utils;


public class HttpClientResponse {

	private int status;

	private String body;

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public HttpClientResponse() {

	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "HttpClientResponse [status=" + status + ", body=" + body + "]";
	}
}
