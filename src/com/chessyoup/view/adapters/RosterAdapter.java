package com.chessyoup.view.adapters;

import org.jivesoftware.smack.packet.Presence;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chessyoup.R;
import com.chessyoup.xmpp.XMPPStatus.MODE;
import com.chessyoup.xmpp.XMPPUser;

/**
 * The Class SearchRosterAdapter.
 * 
 * @author tamas.makranczi
 */
public class RosterAdapter extends BaseAdapter {

	/**
	 * The Class ViewHolder.
	 */
	private static class ViewHolder {
		ImageView contactAvatar;
		TextView contactName;
		TextView contactStatus;
	}

	/** The context. */
	private Context context;

	/** The search roster model. */
	private RosterModel rosterModel;

	/** The layout inflater. */
	private LayoutInflater layoutInflater;

	/**
	 * Instantiates a new search roster adapter.
	 * 
	 * @param context
	 *            the context
	 * @param rosterModel
	 *            the roster model
	 */
	public RosterAdapter(Context context, RosterModel rosterModel) {
		this.context = context;
		this.rosterModel = rosterModel;
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return rosterModel.getCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int id) {
		return rosterModel.getItem(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.view_roster_list_item, parent, false);

			holder = new ViewHolder();
			holder.contactAvatar = (ImageView) convertView
					.findViewById(R.id.roster_list_item_image_avatar);
			holder.contactName = (TextView) convertView
					.findViewById(R.id.roster_list_item_text_contact_name);
			holder.contactStatus = (TextView) convertView
					.findViewById(R.id.roster_list_item_text_contact_status);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		XMPPUser searchedChild = rosterModel.getItem(position);

		// holder.contactAvatar.setImageBitmap(searchedChild.getAvatarBitmap(context));
		holder.contactName.setText(searchedChild.getUsername());

		if (searchedChild.getStatus().getStatus() != null
				&& searchedChild.getStatus().getStatus().trim().length() > 0) {
			holder.contactStatus.setText(searchedChild.getStatus().getStatus());
		} else {
			if (searchedChild.getStatus().getType() != null) {
				if (searchedChild.getStatus().getType() != Presence.Type.unavailable) {
					holder.contactStatus.setText(searchedChild.getStatus()
							.getType().toString());
				} else {
					holder.contactStatus.setText("offline");
				}
			}
		}

		Drawable statusIcon = context.getResources().getDrawable(
				getIdForStatusType(searchedChild.getStatus().getMode()));
		Drawable hasEventIcon = null;

		holder.contactName.setCompoundDrawablesWithIntrinsicBounds(statusIcon,
				null, hasEventIcon, null);

		holder.contactStatus.setCompoundDrawablesWithIntrinsicBounds(
				searchedChild.isChessYoUpUser() ? context.getResources()
						.getDrawable(R.drawable.chessyoup) : null, null,
				hasEventIcon, null);

		return convertView;
	}

	/**
	 * Gets the search roster model.
	 * 
	 * @return the search roster model
	 */
	public RosterModel getSearchRosterModel() {
		return this.rosterModel;
	}

	/**
	 * Sets the search roster model.
	 * 
	 * @param searchRosterModel
	 *            the new search roster model
	 */
	public void setSearchRosterModel(RosterModel rosterModel) {
		this.rosterModel = rosterModel;
	}

	public static int getIdForStatusType(MODE mode) {
		if (mode != null) {

			switch (mode) {
			case ONLINE:
				return R.drawable.general_status_online;
			case AWAY:
				return R.drawable.general_status_away;
			case BUSY:
				return R.drawable.general_status_busy;
			case OFFLINE:
				return R.drawable.general_status_offline;
			default:
				return R.drawable.general_status_offline;
			}

		} else {
			return R.drawable.general_status_offline;
		}
	}
}
