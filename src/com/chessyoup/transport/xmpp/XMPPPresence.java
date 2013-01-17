package com.chessyoup.transport.xmpp;

import org.jivesoftware.smack.packet.Presence.Mode;
import org.jivesoftware.smack.packet.Presence.Type;

import com.chessyoup.transport.Presence;

public class XMPPPresence implements Presence {

	private org.jivesoftware.smack.packet.Presence xmppPresence;

	public XMPPPresence(org.jivesoftware.smack.packet.Presence xmppPresence) {
		this.xmppPresence = xmppPresence;
	}

	@Override
	public String getContactId() {
		return xmppPresence.getFrom();
	}

	@Override
	public MODE getMode() {
		return this.convertMode(xmppPresence.getType(), xmppPresence.getMode());
	}

	@Override
	public String getStatus() {
		return this.xmppPresence.getStatus();
	}

	private MODE convertMode(Type type, Mode mode) {
		if (mode != null) {

			switch (mode) {
			case available:
				return MODE.ONLINE;
			case away:
				return MODE.AWAY;
			case dnd:
				return MODE.BUSY;
			case chat:
				return MODE.ONLINE;
			case xa:
				return MODE.AWAY;

			default:
				return MODE.OFFLINE;
			}
		} else {
			if (type != null) {
				switch (type) {
				case available:
					return MODE.ONLINE;
				default:
					return MODE.OFFLINE;
				}
			} else {
				return MODE.OFFLINE;
			}
		}
	}

}
