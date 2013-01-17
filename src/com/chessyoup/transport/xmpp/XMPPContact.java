package com.chessyoup.transport.xmpp;

import com.chessyoup.transport.Contact;
import com.chessyoup.transport.Presence;
import com.chessyoup.transport.Util;

public class XMPPContact implements Contact {

	private String id;

	private String name;

	private String resource;

	private XMPPPresence presense;

	public XMPPContact(String id, String name, XMPPPresence presense) {
		this.id = id;
		this.name = name;
		this.presense = presense;
	}

	public XMPPContact(String id, String name) {
		this(id, name, new XMPPPresence(null));
	}

	public XMPPContact(String id) {
		this(id, null, new XMPPPresence(null));
	}

	@Override
	public Presence getPresence() {
		return this.presense;
	}

	public void setPresense(XMPPPresence presense) {
		this.presense = presense;
	}

	public String getName() {
		return name;
	}

	public void updateResource(String resource) {
		this.resource = resource;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public PLATFORM getClientPlatform() {
		return Util.getPlatform(this.resource);
	}

	@Override
	public String getClientVersion() {
		return Util.getClientVersion(this.resource);
	}

	@Override
	public String getClientType() {
		return Util.getClientType(this.resource);
	}
}