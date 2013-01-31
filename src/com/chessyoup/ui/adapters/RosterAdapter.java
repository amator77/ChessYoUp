package com.chessyoup.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chessyoup.R;
import com.cyp.accounts.Account;
import com.cyp.transport.Contact;

public class RosterAdapter extends BaseExpandableListAdapter {

	private List<Account> accounts;

	private List<Contact> gameContacts;

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
		this.gameContacts = new ArrayList<Contact>();
	}
	
	public void addAccount(Account account) {
		this.accounts.add(account);
		Log.d("addAccount",account.toString()+"");
		this.notifyDataSetChanged();
	}
	
	public void updateContact(Account account, Contact contact) {

	}

	@Override
	public Object getChild(int groupPosition, final int childPosition) {
		if (groupPosition == 0) {
			return this.gameContacts.get(childPosition);
		} else {
			return this.accounts.get(groupPosition-1).getRoster().getContacts()
					.get(childPosition);
		}
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

		holder.contactName.setText(contact.getName() != null ? contact
				.getName() : contact.getId());

		holder.contactStatus.setText("online");

		Drawable statusIcon = context.getResources().getDrawable(
				R.drawable.general_status_online);
		Drawable hasEventIcon = null;

		holder.contactName.setCompoundDrawablesWithIntrinsicBounds(statusIcon,
				null, hasEventIcon, null);

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

		if (groupPosition == 0) {
			holder.groupName.setText(getGroup(groupPosition).toString());
			holder.groupImage.setImageResource(R.drawable.chessyoup);
		} else {
			Account account = (Account) getGroup(groupPosition);
			holder.groupName.setText(account.getId());
			holder.groupImage.setImageResource( Integer.parseInt(account.getIconTypeResource()));
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		if (groupPosition == 0) {
			return this.gameContacts.size();
		} else {
			return this.accounts.get(groupPosition-1).getRoster().getContacts()
					.size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		
		
		if (groupPosition == 0) {
			return "Chessyoup";
		} else {
			Log.d("getGroup", groupPosition +" , accouns :"+this.accounts);
			return this.accounts.get(groupPosition-1);
		}
	}

	@Override
	public int getGroupCount() {
		Log.d("getGroupCount", 1 + this.accounts.size() +"");
		return 1 + this.accounts.size();
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
}
