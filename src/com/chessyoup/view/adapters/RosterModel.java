package com.chessyoup.view.adapters;

import java.util.ArrayList;
import java.util.List;

import com.chessyoup.transport.Contact;

/**
 * The Class RosterModel.
 */
public class RosterModel {

	/** The contacts list. */
	private List<Contact> contactsList;
	
	/** The show online contacts. */
	private boolean showOnlineContacts;
	
	/** The show blocked contacts. */
	private boolean showBlockedContacts;
	
	/** The sorted by status. */
	private boolean sortedByStatus;
	
	/** The group names. */
	private List<String> groupNames;

	private CharSequence searchCriteria;
	
	public RosterModel()
	{
		this.groupNames = new ArrayList<String>();
		this.contactsList = new ArrayList<Contact>();
	}
	
	/**
	 * Gets the contacts list.
	 *
	 * @return the contacts list
	 */
	public List<Contact> getContactsList()
	{
		return contactsList;
	}

	/**
	 * Sets the contacts list.
	 *
	 * @param contactsList the new contacts list
	 */
	public void setContactsList(List<Contact> contactsList)
	{
		this.contactsList = contactsList;
	}

	/**
	 * Show online contacts.
	 *
	 * @return true, if successful
	 */
	public boolean showOnlineContacts()
	{
		return showOnlineContacts;
	}

	/**
	 * Sets the show online contacts.
	 *
	 * @param showOnlineContacts the new show online contacts
	 */
	public void setShowOnlineContacts(boolean showOnlineContacts, boolean ... isNotificationRequired)
	{
		this.showOnlineContacts = showOnlineContacts;		
	}
	
	public boolean showBlockedContacts() {
		return showBlockedContacts;
	}
	
	public void setShowBlockedContacts(boolean showBlockedContacts, boolean... isNotificationRequired)
	{
		this.showBlockedContacts = showBlockedContacts;		
	}

	/**
	 * Checks if is sorted by status.
	 *
	 * @return true, if is sorted by status
	 */
	public boolean isSortedByStatus()
	{
		return sortedByStatus;
	}

	/**
	 * Sets the sorted by status.
	 *
	 * @param sortedByStatus the new sorted by status
	 */
	public void setSortedByStatus(boolean sortedByStatus, boolean ... isNotificationRequired)
	{
		this.sortedByStatus = sortedByStatus;		
	}

	/**
	 * Gets the group names.
	 *
	 * @return the group names
	 */
	public List<String> getGroupNames()
	{
		return groupNames;
	}

	/**
	 * Sets the group names.
	 *
	 * @param groupNames the new group names
	 */
	public void setGroupNames(List<String> groupNames, boolean ... isNotificationRequired)
	{
		this.groupNames = groupNames;		
	}
	
	/**
	 * Gets the search criteria.
	 *
	 * @return the search criteria
	 */
	public CharSequence getSearchCriteria()
	{
		return searchCriteria;
	}

	/**
	 * Sets the search criteria.
	 *
	 * @param s the new search criteria
	 */
	public void setSearchCriteria(CharSequence s, boolean ... isNotificationRequired)
	{
		this.searchCriteria = s;		
	}
	

	public int getCount()
	{
		return contactsList.size();
	}

	public Contact getItem(int position)
	{
		return contactsList.get(position);
	}
}
