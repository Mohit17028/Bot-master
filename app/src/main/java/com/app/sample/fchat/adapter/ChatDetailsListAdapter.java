package com.app.sample.fchat.adapter;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.sample.fchat.ActivityChatDetails;
import com.app.sample.fchat.ActivityMain;
import com.app.sample.fchat.R;
import com.app.sample.fchat.RecordDialog;
import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.fragment.ChatsFragment;
import com.app.sample.fchat.model.ChatMessage;
import com.github.library.bubbleview.BubbleLinearLayout;
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

    private MediaPlayer mediaPlayer;
    private Runnable runnable;
    private Handler handler;
    private boolean isPlaying = false;
	
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
	public View getView(int position, View convertView, final ViewGroup parent) {
		final ChatMessage msg = (ChatMessage) getItem(position);
        final ViewHolder holder;
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
//        	holder.bubbleView   = (BubbleLinearLayout) convertView.findViewById(R.id.bubble_view);
			holder.seekBar      = (SeekBar) convertView.findViewById(R.id.seekBar);
            holder.download_iv  = (ImageView) convertView.findViewById(R.id.download_btn);
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
			    String aud_name[] = msg.getAudName().split("/");
				holder.message.setVisibility(View.GONE);
				holder.audio_layout.setVisibility(View.VISIBLE);
				holder.aud_name_tv.setText(aud_name[aud_name.length-1]);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		holder.time.setText(msg.getReadableTime());

        if(msg.getReceiver().getId().equals(set.readSetting("myid"))){  //left side
            holder.profile_img.setVisibility(View.VISIBLE);
            holder.lyt_parent.setPadding(5, 3, 90, 2);
            holder.lyt_parent.setGravity(Gravity.LEFT);
            if(holder.audio_layout.getVisibility()==View.VISIBLE) {
				holder.play_icon.setImageResource(R.drawable.ic_file_download_dark_24dp);
				holder.time.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
                holder.aud_name_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
                holder.download_iv.setVisibility(View.GONE);
			}
			if (holder.message.getVisibility()==View.VISIBLE) {
//                holder.download_iv.setVisibility(View.GONE);
                holder.time.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
                holder.message.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
            }
//            holder.bubbleView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
//            holder.lyt_thread.setBackgroundResource(R.drawable.left_chat_msg_box_style);
            holder.lyt_thread.setBackgroundResource(R.drawable.left_chat_bubble);

//            holder.lyt_thread.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
//            holder.profile_img.setImageResource(R.drawable.mascot_face);
            //holder.image_status.setImageResource(android.R.color.transparent);
        }else{
        	holder.profile_img.setVisibility(View.GONE);
            holder.lyt_parent.setPadding(90, 3, 10, 2);
            holder.lyt_parent.setGravity(Gravity.RIGHT);
//            holder.bubbleView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDark));
//            holder.bubbleView.set;
//            holder.lyt_thread.setBackgroundResource(R.drawable.right_chat_msg_box_style);
            holder.lyt_thread.setBackgroundResource(R.drawable.right_chat_bubble);

            if(holder.audio_layout.getVisibility()==View.VISIBLE) {
                holder.time.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.play_icon.setImageResource(R.drawable.ic_file_download_white_24dp);
                holder.aud_name_tv.setTextColor(mContext.getResources().getColor(R.color.white));
            }
            if (holder.message.getVisibility()==View.VISIBLE) {
//                holder.download_iv.setVisibility(View.GONE);
                holder.time.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.message.setTextColor(mContext.getResources().getColor(R.color.white));
            }
//            holder.lyt_thread.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDark));

        }

//       Download on click
        holder.download_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


//       play on click
        final String audio_path = msg.getAudName();
        final SeekBar seekBar_inner = holder.seekBar;
        holder.play_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //play audio code here
                seekBar_inner.setVisibility(View.VISIBLE);
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
//                        Toast.makeText(mContext, "file found",Toast.LENGTH_SHORT).show();
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
//                                Toast.makeText(mContext, "Downloaded the file", Toast.LENGTH_SHORT).show();
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
                    if(msg.getReceiver().getId().equals(set.readSetting("myid"))){;
                        holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_dark_24dp);
                    }
                    else {
                        holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
                    }
                    handler = new Handler();
                    mediaPlayer = new MediaPlayer();
//                    try {
//                        mediaPlayer.setDataSource(fileNameOnDevice.getPath());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    RecordDialog recDialog = new RecordDialog();
//                    FragmentManager fm = ((ActivityChatDetails)mContext).getSupportFragmentManager();
//                    recDialog.show(fm,"Record Dialog");
                    if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        if(msg.getReceiver().getId().equals(set.readSetting("myid"))){;
                            holder.play_icon.setImageResource(R.drawable.ic_pause_circle_filled_dark_24dp);
                        }
                        else {
                            holder.play_icon.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
                        }
                    }
                    else {

                        try {

                            mediaPlayer.setDataSource(fileNameOnDevice.getPath());
//                        Toast.makeText(mContext, "media player set", Toast.LENGTH_SHORT).show();
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            if(msg.getReceiver().getId().equals(set.readSetting("myid"))){;
                                holder.play_icon.setImageResource(R.drawable.ic_pause_circle_filled_dark_24dp);
                            }
                            else {
                                holder.play_icon.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
                            }
                            isPlaying = true;
                            changeSeekbar();
//                        Toast.makeText(mContext, "Playing Audio", Toast.LENGTH_SHORT).show();
//                        seekBar_inner.setProgress(mediaPlayer.getCurrentPosition());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }



//


//                    seekBar_inner.setProgress(mediaPlayer.getCurrentPosition());

//                    seekBar_inner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                        @Override
//                        public void onProgressChanged(SeekBar seekBar, int progress, boolean input) {
//                            if(input){
//                                if( mediaPlayer!=null && input ){
//                                    mediaPlayer.seekTo(progress);
//                                }
//
//                            }
//                        }
//
//                        @Override
//                        public void onStartTrackingTouch(SeekBar seekBar) {
//
//                        }
//
//                        @Override
//                        public void onStopTrackingTouch(SeekBar seekBar) {
//
//                        }
//                    });

                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        seekBar_inner.setMax(mediaPlayer.getDuration());
                        try {

                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            seekBar_inner.setVisibility(View.VISIBLE);
                            changeSeekbar();
//                            Toast.makeText(mContext, "Playing Audio", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        seekBar_inner.setProgress(0);
                        if(msg.getReceiver().getId().equals(set.readSetting("myid"))){;
                            holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_dark_24dp);
                        }
                        else {
                            holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
                        }
                    }
                });
            }

            public void changeSeekbar(){
                holder.seekBar.setProgress(mediaPlayer.getCurrentPosition());
//                Toast.makeText(mContext, "inside changeSeekBar", Toast.LENGTH_SHORT).show();
//                seekBar_inner.setProgress(mediaPlayer.getCurrentPosition());
                if(mediaPlayer.isPlaying()){

                    holder.seekBar.setMax(mediaPlayer.getDuration());
                    holder.seekBar.setProgress(mediaPlayer.getCurrentPosition());

                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            changeSeekbar();
                        }
                    };
                    handler.postDelayed(runnable,100);
                }
            }

            public void manageSeekBar(ViewHolder holder){
                seekBar_inner.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if( mediaPlayer!=null && fromUser ){
                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
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
		SeekBar seekBar;
		ImageView download_iv;
//        BubbleLinearLayout bubbleView;
	}	
}
