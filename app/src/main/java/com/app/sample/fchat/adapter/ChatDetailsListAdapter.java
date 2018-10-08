package com.app.sample.fchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.sample.fchat.R;
import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.model.ChatMessage;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailsListAdapter extends BaseAdapter {
	
	private List<ChatMessage> mMessages;
	private Context mContext;
	SettingsAPI set;
	
	public ChatDetailsListAdapter(Context context, List<ChatMessage> messages) {
        super();
        this.mContext = context;
        this.mMessages = messages;
        set=new SettingsAPI(mContext);
	}
	
	@Override
	public int getCount() {
		return mMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMessage msg = (ChatMessage) getItem(position);
        ViewHolder holder;
        if(convertView == null){
        	holder 				= new ViewHolder();
        	convertView			= LayoutInflater.from(mContext).inflate(R.layout.row_chat_details, parent, false);
        	holder.time 		= (TextView) convertView.findViewById(R.id.text_time);
        	holder.message 		= (TextView) convertView.findViewById(R.id.text_content);
			holder.lyt_thread 	= (LinearLayout) convertView.findViewById(R.id.lyt_thread);
			holder.lyt_parent 	= (LinearLayout) convertView.findViewById(R.id.lyt_parent);
			holder.audio_layout = (LinearLayout) convertView.findViewById(R.id.audio_layout);
			holder.image_status	= (ImageView) convertView.findViewById(R.id.image_status);
			holder.profile_img  = (CircleImageView) convertView.findViewById(R.id.profile_img);
			holder.play_icon    = (ImageView) convertView.findViewById(R.id.imageViewPlay);
			holder.aud_name_tv  = (TextView) convertView.findViewById(R.id.tv_audio_name);
        	convertView.setTag(holder);	
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        if(msg.getIs_text().equals("1")) {
			holder.message.setVisibility(View.VISIBLE);
			holder.message.setText(msg.getText());
			holder.audio_layout.setVisibility(View.GONE);
		}
        else {
			holder.message.setVisibility(View.GONE);
			holder.audio_layout.setVisibility(View.VISIBLE);
			holder.aud_name_tv.setText(msg.getAudName());
		}

		holder.time.setText(msg.getReadableTime());

        if(msg.getReceiver().getId().equals(set.readSetting("myid"))){
            holder.lyt_parent.setPadding(5, 3, 50, 2);
            holder.lyt_parent.setGravity(Gravity.LEFT);
            if(holder.audio_layout.getVisibility()==View.VISIBLE) {
				holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_dark_24dp);
				holder.aud_name_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
			}
            holder.time.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
            holder.message.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
            holder.lyt_thread.setBackgroundResource(R.drawable.left_chat_msg_box_style);
//            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
//            holder.profile_img.setImageResource(R.drawable.mascot_face);
            //holder.image_status.setImageResource(android.R.color.transparent);
        }else{
//        	when msg is audio txt_layout visibility will be gone; if text, audio layout visibility will be gone
//			if(holder.audio_layout.getVisibility()==View.VISIBLE) {}
        	holder.profile_img.setVisibility(View.GONE);
        	holder.lyt_thread.setBackgroundResource(R.drawable.right_chat_msg_box_style);
            holder.lyt_parent.setPadding(50, 3, 5, 2);
            holder.lyt_parent.setGravity(Gravity.RIGHT);
//            holder.lyt_thread.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDark));
        }
        return convertView;
	}

	/**
	 * remove data item from messageAdapter
	 * 
	 **/
	public void remove(int position){
		mMessages.remove(position);
	}
	
	/**
	 * add data item to messageAdapter
	 * 
	 **/
	public void add(ChatMessage msg){
		mMessages.add(msg);
	}
	
	private static class ViewHolder{
		TextView time;
		TextView message;
		LinearLayout lyt_parent;
		LinearLayout lyt_thread;
		LinearLayout audio_layout;
		ImageView image_status;
		CircleImageView profile_img;
		ImageView play_icon;
		TextView aud_name_tv;
	}	
}
