package com.chessyoup.xmpp;

public class XMPPUser {

	private String username;

	private String domain;

	private String resource;

	private XMPPStatus status;

	public XMPPUser(String jabberId, XMPPStatus status) {
		this.setJabberId(jabberId);
		this.status = status;
	}

	public XMPPStatus getStatus() {
		return status;
	}

	public void setStatus(XMPPStatus status) {
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setJabberId(String jabberId) {
		String[] parts = jabberId.split("@");
		username = parts[0];
		parts = parts[1].split("/");

		if (parts.length == 2) {
			domain = parts[0];
			resource = parts[1];
		} else {
			domain = parts[0];
		}
	}

	public String getJabberId() {
		return new StringBuffer(this.username).append("@").append(domain)
				.append("/").append(resource).toString();
	}

	public boolean isChessYoUpUser() {
		
		if (this.resource != null && this.resource.trim().length() > 0) {
			if (this.resource.startsWith("chessyoup")) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return new StringBuffer(this.username).append("@").append(domain)
				.append("/").append(resource).append("(").append(status)
				.append(")").toString();
	}
}
