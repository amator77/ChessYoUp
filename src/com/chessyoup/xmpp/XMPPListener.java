package com.chessyoup.xmpp;

import java.util.Collection;

public interface XMPPListener {

	public void newEntriesAdded(Collection<String> jabberIds);

	public void entriesDeleted(Collection<String> jabberIds);

	public void entriesUpdated(Collection<String> jabberIds);

	public void presenceChanged(String jabberId, String status);

	public void messageReceived(String jabberId, XMPPMessage message);

	public void chatStarted(String participant);
}
