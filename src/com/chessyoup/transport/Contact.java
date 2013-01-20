package com.chessyoup.transport;

public interface Contact {

	public enum PLATFORM {
		DESKTOP_LINUX, DESKTOP_WINDOWS, DESKTOP_IOS, MOBILE_ANDROID, MOBILE_IPHONE, UNKNOWN
	}

	/**
	 * Contact unique ID
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Contact name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Contact presence status.
	 * 
	 * @return
	 */
	public Presence getPresence();

	/**
	 * Get the contact client running platform.
	 * 
	 * @return
	 */
	public PLATFORM getClientPlatform();

	/**
	 * The contact client version.
	 * 
	 * @return
	 */
	public String getClientVersion();

	/**
	 * The contact client type.
	 * 
	 * @return
	 */
	public String getClientType();
}
