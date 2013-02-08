package com.chessyoup.ui.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chessyoup.R;
import com.cyp.accounts.Account;
import com.cyp.transport.Contact;
import com.cyp.transport.Presence;
import com.cyp.transport.Presence.MODE;
import com.cyp.transport.Util;

public class RosterAdapter extends BaseExpandableListAdapter {

	private List<Account> accounts;
	
	private List<RosterGroup> groups; 
	
	private RosterGroup root;
	
	private LayoutInflater layoutInflater;

	private Context context;

	/**
	 * The Class ViewHolder.
	 */
	private static class GroupViewHolder {
		ImageView groupImage;
		TextView groupName;
	}

	/**
	 * The Class ViewHolder.
	 */
	private static class ChildViewHolder {
		ImageView contactAvatar;
		TextView contactName;
		TextView contactStatus;
	}

	public RosterAdapter(Context context) {
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
		this.accounts = new ArrayList<Account>();
		this.groups = new ArrayList<RosterAdapter.RosterGroup>();
		this.root = new RosterGroup("ChessYoUp", R.drawable.chessyoup, true);
	}
	
	public void refresh(){
		this.root.contacts.clear();
		this.groups.clear();
		this.groups.add(root);
		
		for(Account account : this.accounts ){
			RosterGroup group = new RosterGroup(account.getConnection().getAccountId(), Integer.parseInt(account.getIconTypeResource()), false);
			
			for(Contact contact : account.getRoster().getContacts() ){
				
				if( contact.isCompatible() ){
					this.root.contacts.add(new RosterContact(contact));
				}
				else{					
					group.contacts.add(new RosterContact(contact));
				}
			}
			
			Collections.sort(group.contacts);			
			Collections.sort(this.root.contacts);
			
			this.groups.add(group);			
		}
		
		Collections.sort(this.groups);		
		this.notifyDataSetChanged();
	}
	
	public void addAccount(Account account) {
		this.accounts.add(account);
		this.refresh();
	}

	@Override
	public Object getChild(int groupPosition, final int childPosition) {
		return this.groups.get(groupPosition).contacts.get(childPosition).contact;		
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return groupPosition + childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ChildViewHolder holder;

		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.view_roster_list_item, parent, false);

			holder = new ChildViewHolder();
			holder.contactAvatar = (ImageView) convertView
					.findViewById(R.id.roster_list_item_image_avatar);
			holder.contactName = (TextView) convertView
					.findViewById(R.id.roster_list_item_text_contact_name);
			holder.contactStatus = (TextView) convertView
					.findViewById(R.id.roster_list_item_text_contact_status);

			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}

		Contact contact = (Contact) getChild(groupPosition, childPosition);

		holder.contactName.setText(contact.getName() != null ? contact.getName() : Util.getContactFromId(contact.getId()) );
		
		if( contact.getAvatar() != null ){
			Bitmap bmp=BitmapFactory.decodeByteArray(contact.getAvatar(),0,contact.getAvatar().length);			
			holder.contactAvatar.setImageBitmap(bmp);
		}
		else{
			holder.contactAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.general_avatar_unknown));
		}
		
		holder.contactStatus.setText(contact.getPresence() != null ? contact.getPresence().getStatus() : "");

		holder.contactName.setCompoundDrawablesWithIntrinsicBounds( contact.getPresence() != null  ? getStatusIcon(contact.getPresence()) : context.getResources().getDrawable(
				R.drawable.general_status_offline) ,
				null, null, null);
				
		if( contact.getPresence().getMode() != MODE.OFFLINE ){
			holder.contactStatus.setCompoundDrawablesWithIntrinsicBounds( contact.isCompatible() ? context.getResources().getDrawable(R.drawable.chessyoup)  :  null , null, null, null);
		}
		else{
			holder.contactStatus.setCompoundDrawablesWithIntrinsicBounds(  null , null, null, null);
		}
				
		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		GroupViewHolder holder;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.view_roster_group,
					parent, false);

			holder = new GroupViewHolder();
			holder.groupImage = (ImageView) convertView
					.findViewById(R.id.roster_group_image);
			holder.groupName = (TextView) convertView
					.findViewById(R.id.roster_group__name);

			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}
		
		RosterGroup group = (RosterGroup)getGroup(groupPosition);
		holder.groupName.setText(group.groupName);
		holder.groupImage.setImageResource(group.imageId);
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {	
		return this.groups.get(groupPosition).contacts.size();				
	}

	@Override
	public Object getGroup(int groupPosition) {		
		return this.groups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this.groups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private Drawable getStatusIcon(Presence presence) {
		switch (presence.getMode()) {
		case ONLINE:
			return context.getResources().getDrawable(
					R.drawable.general_status_online);

		case AWAY:
			return context.getResources().getDrawable(
					R.drawable.general_status_away);

		case BUSY:
			return context.getResources().getDrawable(
					R.drawable.general_status_busy);

		case OFFLINE:
			return context.getResources().getDrawable(
					R.drawable.general_status_offline);

		default:
			return context.getResources().getDrawable(
					R.drawable.general_status_offline);
		}
	}
	
	class RosterContact implements Comparable<RosterContact>{
		
		Contact contact;
		
		RosterContact(Contact contact){
			this.contact = contact;
		}
		
		@Override
		public int compareTo(RosterContact another) {			
			return contact.getPresence().getMode().compareTo(another.contact.getPresence().getMode());						
		}		
	}
	
	class RosterGroup implements Comparable<RosterGroup>{
		
		String groupName;
		int imageId;
		List<RosterContact> contacts;
		boolean isRoot;
		
		public RosterGroup(String groupName,int imageId , boolean isRoot){
			this.groupName = groupName;
			this.imageId = imageId;
			this.contacts = new ArrayList<RosterAdapter.RosterContact>();
			this.isRoot= isRoot;		
		}							
		
		@Override
		public int compareTo(RosterGroup another) {			
			
			if( this.isRoot ){
				return 1;
			}
			else{
				if( this.countOnline() > another.countOnline() ){
					return 1;
				}
				else{
					return groupName.compareTo(another.groupName);
				}
			}
		}
		
		int countOnline(){
			int count = 0;
			
			for( RosterContact rContact : contacts ){
				if( rContact.contact.getPresence().getMode() == MODE.ONLINE ){
					count++;
				}
			}
			
			return count;
		}
	}
}
