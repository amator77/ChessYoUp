package com.chessyoup.transport.xmpp;

import java.util.Collection;

public interface XMPPListener {

	public void newEntriesAdded(Collection<String> jabberIds);

	public void entriesDeleted(Collection<String> jabberIds);

	public void entriesUpdated(Collection<String> jabberIds);

	public void presenceChanged(String jabberId, XMPPStatus status);
}
