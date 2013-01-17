package com.chessyoup.transport.xmpp;

import java.util.Collection;

import com.chessyoup.transport.Message;

public class XMPPMessage implements Message {

	private org.jivesoftware.smack.packet.Message xmppPachet;

	public XMPPMessage(org.jivesoftware.smack.packet.Message xmppPachet) {
		this.xmppPachet = xmppPachet;
	}

	public String getHeader(String key) {
		return this.xmppPachet.getProperty(key) != null ? this.xmppPachet
				.getProperty(key).toString() : null;
	}

	@Override
	public String getBody() {
		return this.xmppPachet.getBody();
	}

	@Override
	public String getFrom() {
		return this.xmppPachet.getFrom();
	}

	@Override
	public String getTo() {
		return this.xmppPachet.getTo();
	}

	@Override
	public Collection<String> getHeadersKeys() {
		return this.xmppPachet.getPropertyNames();
	}
}
