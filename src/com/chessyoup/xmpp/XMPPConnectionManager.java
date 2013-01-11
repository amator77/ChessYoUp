package com.chessyoup.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
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

public class XMPPConnectionManager implements PacketListener,
		ConnectionListener {

	private static XMPPConnectionManager instance = new XMPPConnectionManager();

	private static final String TAG = "XMPPConnectionManager";

	private static final String GTALK_HOST = "talk.google.com";

	private static final int GTALK_PORT = 5222;

	private static final String GTALK_SERVICE = "gtalk";

	private static final String GTALK_RESOURCE = "chessyoup";

	private XMPPConnection xmppConnection;

	private String user;

	private ConnectionConfiguration configuration;

	private XMPPConnectionManager() {
		configuration = new ConnectionConfiguration(GTALK_HOST, GTALK_PORT,
				GTALK_SERVICE, ProxyInfo.forNoProxy());
		configuration.setSecurityMode(SecurityMode.required);
		configuration.setDebuggerEnabled(true);
		configuration.setSendPresence(false);
		Roster.setDefaultSubscriptionMode(SubscriptionMode.manual);
		this.configurePM(ProviderManager.getInstance());
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
				xmppConnection.addPacketListener(this, new PacketTypeFilter(
						PingExtension.class));
			} catch (XMPPException e) {
				Log.e(TAG, "Error on connection", e);
				return false;
			}
		}

		try {
			xmppConnection.login(username, password, GTALK_RESOURCE);
			this.user = xmppConnection.getUser();
			Log.d(TAG, "Success on login as :" + this.user);

			xmppConnection.sendPacket(new IQ() {

				@Override
				public String getChildElementXML() {
					// TODO Auto-generated method stub
					return null;
				}
			});

			ChatManager chatmanager = xmppConnection.getChatManager();
			final Chat chat = chatmanager.createChat(
					"florea.leonard@gmail.com", new MessageListener() {

						@Override
						public void processMessage(Chat arg0, Message arg1) {
							Log.d(TAG, "Chat message :" + arg1.getBody());
							try {
								Message move = new Message();
								move.setProperty("move", "d2d4");
								move.setProperty("time", System.currentTimeMillis()+"");
								arg0.sendMessage(move);
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
						
			chat.sendMessage("start chat");
		} catch (XMPPException e) {
			Log.e(TAG, "Error on login", e);
			return false;
		}

		return false;
	}

	public void logout() {
		if (this.xmppConnection != null && this.xmppConnection.isConnected()) {
			this.xmppConnection.disconnect();
			this.xmppConnection.removeConnectionListener(this);
			this.xmppConnection.removePacketListener(this);
			this.xmppConnection = null;
			this.user = null;
		}
	}

	public static XMPPConnectionManager getInstance() {
		return XMPPConnectionManager.instance;
	}

	@Override
	public void processPacket(Packet arg0) {
		Log.d(TAG, "processPacket :" + arg0.toString());

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
}
