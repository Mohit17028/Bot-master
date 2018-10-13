package com.app.sample.fchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sample.fchat.ActivityChatDetails;
import com.app.sample.fchat.ActivityMain;
import com.app.sample.fchat.R;
import com.app.sample.fchat.RecordDialog;
import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.fragment.ChatsFragment;
import com.app.sample.fchat.model.ChatMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
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

		try {
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
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		holder.time.setText(msg.getReadableTime());

        if(msg.getReceiver().getId().equals(set.readSetting("myid"))){
            holder.lyt_parent.setPadding(5, 3, 90, 2);
            holder.lyt_parent.setGravity(Gravity.LEFT);
            if(holder.audio_layout.getVisibility()==View.VISIBLE) {
				holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_dark_24dp);
				holder.aud_name_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
			}
			if (holder.message.getVisibility()==View.VISIBLE) {
                holder.time.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
                holder.message.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
            }
            holder.lyt_thread.setBackgroundResource(R.drawable.left_chat_msg_box_style);
//            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
//            holder.profile_img.setImageResource(R.drawable.mascot_face);
            //holder.image_status.setImageResource(android.R.color.transparent);
        }else{
//        	when msg is audio txt_layout visibility will be gone; if text, audio layout visibility will be gone
//			if(holder.audio_layout.getVisibility()==View.VISIBLE) {}
        	holder.profile_img.setVisibility(View.GONE);
        	holder.lyt_thread.setBackgroundResource(R.drawable.right_chat_msg_box_style);
            holder.lyt_parent.setPadding(90, 3, 5, 2);
            holder.lyt_parent.setGravity(Gravity.RIGHT);
//            holder.lyt_thread.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDark));

        }

        final String audio_path = msg.getAudName();
        holder.audio_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //play audio code here

                System.out.println("Method play called" + audio_path);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                String fileName = audio_path;
//        fileName="/111433108964661785456/recording_1539375297988.mp3";
                String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS).getPath();

                StorageReference storageRef = storage.getReference();
                System.out.println("Filename :" + fileName.substring(1));
                StorageReference downloadRef = storageRef.child(fileName.substring(1));
                storageRef.child(fileName.substring(1)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(mContext,
                                "file found",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // File not found
                        Toast.makeText(mContext,
                                "file not found",
                                Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                String[] arr = audio_path.split("/");
                System.out.println("Array :" + arr);
                System.out.println("download ref : " + downloadRef.toString() + " " + downloadRef.getPath() + " " + downloadRef.getName());
                System.out.println("Pathname :" + DOWNLOAD_DIR + "/" + downloadRef.getName());
                File localFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS), arr[2]);
                System.out.println("local file :" + localFile);
                try {
                    localFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                downloadRef.getFile(localFile);
                final int[] flag = {0};
                File fileNameOnDevice = new File(DOWNLOAD_DIR + "/" + downloadRef.getName());
                System.out.println("File on device :" + fileNameOnDevice);

                downloadRef.getFile(fileNameOnDevice).addOnSuccessListener(
                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d("File Download", "downloaded the file");
                                Toast.makeText(mContext,
                                        "Downloaded the file",
                                        Toast.LENGTH_SHORT).show();
                                        flag[0] =1;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("File Download", "Failed to download the file");
                        Toast.makeText(mContext,
                                "Couldn't be downloaded",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                if (fileNameOnDevice.exists() || flag[0]==1) {
//                    RecordDialog recDialog = new RecordDialog();
//                    FragmentManager fm = ((ActivityChatDetails)mContext).getSupportFragmentManager();
//                    recDialog.show(fm,"Record Dialog");
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource(fileNameOnDevice.getPath());
                        mediaPlayer.prepare();
                        mediaPlayer.start();

                        Toast.makeText(mContext, "Playing Audio", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }



		});
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
