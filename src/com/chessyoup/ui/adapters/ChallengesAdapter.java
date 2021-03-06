package com.chessyoup.ui.adapters;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chessyoup.R;
import com.cyp.game.IChallenge;
import com.cyp.transport.Util;

public class ChallengesAdapter extends BaseAdapter {
	
	private List<IChallenge> challenges;
	
	private LayoutInflater layoutInflater;
	
	private Context context;
	
	/**
	 * The Class ViewHolder.
	 */
	private static class ItemViewHolder {
		ImageView statusImage;
		ImageView contactAvatar;
		TextView contactName;
		TextView challangeDetails;
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, kk:mm:ss", Locale.getDefault());		
	}
	
	public ChallengesAdapter(Context context){
		this.context = context;
		this.challenges = new ArrayList<IChallenge>();
		this.layoutInflater = LayoutInflater.from(context);
	}
	
	public void addChallenge(IChallenge challenge) {
		this.challenges.add(challenge);
		Log.d("addChallenge",challenge.toString()+"");
		this.notifyDataSetChanged();
	}
	
	public void removeChallenge(IChallenge challenge) {
		this.challenges.remove(challenge);
		Log.d("removeChallenge",challenge.toString()+"");
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return this.challenges.size();
	}

	@Override
	public Object getItem(int position) {
		return this.challenges.get(position);
	}

	@Override
	public long getItemId(int position) {
		return this.challenges.get(position).getTime();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ItemViewHolder holder;

		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.view_challenges_item, parent, false);

			holder = new ItemViewHolder();
			holder.contactAvatar = (ImageView) convertView
					.findViewById(R.id.challenge_item_image_avatar);
			holder.contactName = (TextView) convertView
					.findViewById(R.id.challenge_item_text_contact_name);
			holder.challangeDetails = (TextView) convertView
					.findViewById(R.id.challenge_item_text_details);
			holder.statusImage = (ImageView) convertView
					.findViewById(R.id.challenge_status_image);
			convertView.setTag(holder);
		} else {
			holder = (ItemViewHolder) convertView.getTag();
		}

		IChallenge challenge = (IChallenge)getItem(position);
		
		if( challenge.getRemoteContact().getAvatar() != null ){
			Bitmap bmp=BitmapFactory.decodeByteArray(challenge.getRemoteContact().getAvatar(),0,challenge.getRemoteContact().getAvatar().length);			
			holder.contactAvatar.setImageBitmap(bmp);
		}
		else{
			holder.contactAvatar.setImageDrawable(context.getResources().getDrawable(R.drawable.general_avatar_unknown));
		}
		
		holder.contactName.setText( Util.getContactFromId(challenge.getRemoteContact().getId()));				
		holder.challangeDetails.setText( (challenge.isReceived() ? "Received at :" : "Sended at :")+ holder.dateFormat.format(new Date(challenge.getTime())) );		
		holder.statusImage.setImageDrawable(context.getResources().getDrawable( challenge.isReceived() ? R.drawable.receive : R.drawable.send ));
		
		
		return convertView;
	}
}
