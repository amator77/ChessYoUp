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
import com.chessyoup.transport.xmpp.XMPPUser;
import com.chessyoup.transport.xmpp.XMPPStatus.MODE;

public class AccountStatusAdapter extends BaseAdapter {

	/**
	 * The Class ViewHolder.
	 */
	private static class ViewHolder {
		TextView accountName;
		TextView accountStatus;
	}

	/** The context. */
	private Context context;

	private AccountStatusModel model;

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
	public AccountStatusAdapter(Context context, AccountStatusModel model) {
		this.context = context;
		this.model = model;
		this.layoutInflater = LayoutInflater.from(this.context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		return model.getCount();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int id) {
		return model.getStatus(id);
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
					R.layout.account_status_list_item, parent, false);

			holder = new ViewHolder();
			holder.accountName = (TextView) convertView
					.findViewById(R.id.account_status_list_item_text_account_name);
			holder.accountStatus = (TextView) convertView
					.findViewById(R.id.account_status_list_item_text_account_status);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		MODE status = model.getStatus(position);

		holder.accountName.setText( " "+model.getAccount().substring(0,model.getAccount().indexOf("/")));
		holder.accountStatus.setText(status.toString());
		
		Drawable statusIcon = context.getResources().getDrawable(getIdForStatusType(status));
	
		holder.accountName.setCompoundDrawablesWithIntrinsicBounds(statusIcon,
				null, null, null);

		return convertView;
	}

	/**
	 * Gets the search roster model.
	 * 
	 * @return the search roster model
	 */
	public AccountStatusModel getModel() {
		return this.model;
	}

	/**
	 * Sets the search roster model.
	 * 
	 * @param searchRosterModel
	 *            the new search roster model
	 */
	public void setModel(AccountStatusModel model) {
		this.model = model;
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
