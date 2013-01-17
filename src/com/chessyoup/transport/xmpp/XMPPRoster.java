package com.chessyoup.transport.xmpp;

import java.util.ArrayList;
import java.util.List;

import org.jivesoftware.smack.RosterEntry;

import com.chessyoup.transport.Roster;
import com.chessyoup.transport.RosterListener;

public class XMPPRoster implements Roster {

	private List<XMPPContact> contacts;

	private List<RosterListener> listeners;

	public XMPPRoster(org.jivesoftware.smack.Roster roster) {
		this.contacts = new ArrayList<XMPPContact>();

		for (RosterEntry entry : roster.getEntries()) {
			contacts.add(new XMPPContact(entry.getUser(), entry.getName()));
		}

		this.listeners = new ArrayList<RosterListener>();
	}

	@Override
	public List<XMPPContact> getContacts() {
		return this.contacts;
	}

	@Override
	public void addListener(RosterListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public List<RosterListener> getListeners() {
		return listeners;
	}
}
