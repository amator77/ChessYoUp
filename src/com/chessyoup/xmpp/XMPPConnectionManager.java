package com.chessyoup.xmpp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.proxy.ProxyInfo;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.provider.DelayInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.EventProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemProvider;
import org.jivesoftware.smackx.pubsub.provider.ItemsProvider;
import org.jivesoftware.smackx.pubsub.provider.PubSubProvider;

import android.util.Log;

public class XMPPConnectionManager implements ConnectionListener,
		RosterListener, ChatManagerListener, MessageListener {

	private static XMPPConnectionManager instance = new XMPPConnectionManager();

	private static final String TAG = "XMPPConnectionManager";

	private static final String GTALK_HOST = "talk.google.com";

	private static final int GTALK_PORT = 5222;

	private static final String GTALK_SERVICE = "gtalk";

	private static final String GTALK_RESOURCE = "chessyoup";

	private XMPPConnection xmppConnection;

	private List<XMPPListener> listeners;

	private String user;

	private ConnectionConfiguration configuration;

	private XMPPConnectionManager() {
		configuration = new ConnectionConfiguration(GTALK_HOST, GTALK_PORT,
				GTALK_SERVICE, ProxyInfo.forNoProxy());
		configuration.setSecurityMode(SecurityMode.required);
		configuration.setDebuggerEnabled(true);
		configuration.setSendPresence(true);
		Roster.setDefaultSubscriptionMode(SubscriptionMode.manual);
		this.configurePM(ProviderManager.getInstance());
		this.listeners = new ArrayList<XMPPListener>();
	}

	public boolean login(String username, String password) {

		if (this.xmppConnection == null) {
			xmppConnection = new XMPPConnection(configuration);
		}

		if (xmppConnection.isAuthenticated()) {
			return true;
		}

		if (!xmppConnection.isConnected()) {

			try {
				xmppConnection.connect();
				xmppConnection.addConnectionListener(this);
				xmppConnection.addPacketListener(new PingListener(),
						new PacketTypeFilter(PingExtension.class));
				xmppConnection.addPacketListener(new PresenceListener(),
						new PacketTypeFilter(Presence.class));
			} catch (XMPPException e) {
				Log.e(TAG, "Error on connection", e);
				return false;
			}
		}

		try {
			xmppConnection.login(username, password, GTALK_RESOURCE);
			this.user = xmppConnection.getUser();
			Log.d(TAG, "Success on login as :" + this.user);
			xmppConnection.getRoster().addRosterListener(this);
			xmppConnection.getChatManager().addChatListener(this);
		} catch (XMPPException e) {
			Log.e(TAG, "Error on login", e);
			return false;
		}

		return true;
	}

	public void logout() {
		if (this.xmppConnection != null && this.xmppConnection.isConnected()) {
			this.xmppConnection.disconnect();
			this.xmppConnection.removeConnectionListener(this);
			this.xmppConnection = null;
			this.user = null;
		}
	}

	public static XMPPConnectionManager getInstance() {
		return XMPPConnectionManager.instance;
	}

	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		Log.d(TAG, "chatCreated :" + chat.getParticipant() + " ,"
				+ createdLocally);

		if (!createdLocally) {
			chat.addMessageListener(this);
		}
	}

	@Override
	public void processMessage(Chat arg0, Message message) {
		Log.d(TAG, "processMessage from :" + arg0.getParticipant()
				+ " , message :" + message.toString());

		Map<String, String> header = new HashMap<String, String>();

		for (String property : message.getPropertyNames()) {
			header.put(property, message.getProperty(property).toString());
		}

		XMPPMessage xmppMessage = new XMPPMessage(message.getBody(), header);

		for (XMPPListener listener : this.listeners) {
			listener.messageReceived(message.getFrom(), xmppMessage);
		}
	}

	public Roster getRoster() {
		return this.xmppConnection.getRoster();
	}

	public String getLoggedUser() {
		return this.user;
	}

	@Override
	public void entriesAdded(Collection<String> arg0) {
		Log.d(TAG, "entriesAdded :" + arg0.toString());
		for (XMPPListener listener : this.listeners) {
			listener.newEntriesAdded(arg0);
		}
	}

	@Override
	public void entriesDeleted(Collection<String> arg0) {
		Log.d(TAG, "entriesDeleted :" + arg0.toString());
		for (XMPPListener listener : this.listeners) {
			listener.entriesDeleted(arg0);
		}
	}

	@Override
	public void entriesUpdated(Collection<String> arg0) {
		Log.d(TAG, "entriesUpdated :" + arg0.toString());
		for (XMPPListener listener : this.listeners) {
			listener.entriesUpdated(arg0);
		}
	}

	@Override
	public void presenceChanged(Presence arg0) {
		Log.d(TAG, "presenceChanged :" + arg0.toString());
	}

	@Override
	public void connectionClosed() {
		Log.d(TAG, "connectionClosed");

	}

	@Override
	public void connectionClosedOnError(Exception arg0) {
		Log.e(TAG, "connectionClosedOnError", arg0);

	}

	@Override
	public void reconnectingIn(int arg0) {
		Log.d(TAG, "reconnectingIn :" + arg0);
	}

	@Override
	public void reconnectionFailed(Exception arg0) {
		Log.e(TAG, "reconnectionFailed", arg0);
	}

	@Override
	public void reconnectionSuccessful() {
		Log.d(TAG, "reconnectionSuccessful");
	}

	public void addXMPPListener(XMPPListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeXMPPListener(XMPPListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}

	private void configurePM(ProviderManager pm) {
		Log.d(TAG, "configure ProviderManager");

		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		pm.addExtensionProvider("delay", "urn:xmpp:delay",
				new DelayInfoProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());
		ChatStateExtension.Provider chatState = new ChatStateExtension.Provider();
		pm.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates", chatState);
		pm.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates", chatState);
		pm.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates", chatState);
		pm.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates", chatState);
		pm.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates", chatState);
		pm.addIQProvider("pubsub", "http://jabber.org/protocol/pubsub",
				new PubSubProvider());
		pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub",
				new ItemsProvider());
		pm.addExtensionProvider("items", "http://jabber.org/protocol/pubsub",
				new ItemsProvider());
		pm.addExtensionProvider("item", "http://jabber.org/protocol/pubsub",
				new ItemProvider());
		pm.addExtensionProvider("items",
				"http://jabber.org/protocol/pubsub#event", new ItemsProvider());
		pm.addExtensionProvider("item",
				"http://jabber.org/protocol/pubsub#event", new ItemProvider());
		pm.addExtensionProvider("event",
				"http://jabber.org/protocol/pubsub#event", new EventProvider());
		pm.addIQProvider(PingExtension.ELEMENT, PingExtension.NAMESPACE,
				PingExtension.class);
	}

	public class PresenceListener implements PacketListener {
		public void processPacket(Packet packet) {
			if (!(packet instanceof Presence))
				return;
			Presence presence = (Presence) packet;
			Log.d(TAG, "new precence pachet :" + presence.getFrom());
			Log.d(TAG, "new precence pachet :" + presence.getStatus());
			for (XMPPListener listener : listeners) {
				listener.presenceChanged(presence.getFrom(), presence.getStatus());
			}
		}
	}

	private class PingListener implements PacketListener {

		@Override
		public void processPacket(Packet packet) {
			if (!(packet instanceof PingExtension))
				return;
			PingExtension p = (PingExtension) packet;
			if (p.getType() == IQ.Type.GET) {
				PingExtension pong = new PingExtension();
				pong.setType(IQ.Type.RESULT);
				pong.setTo(p.getFrom());
				pong.setPacketID(p.getPacketID());
				xmppConnection.sendPacket(pong);
			}
		}

	}
}
