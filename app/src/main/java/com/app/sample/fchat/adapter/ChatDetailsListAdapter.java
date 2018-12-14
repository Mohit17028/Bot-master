package com.app.sample.fchat.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.app.sample.fchat.R;
import com.app.sample.fchat.data.SettingsAPI;
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
import pl.droidsonroids.gif.GifImageView;

public class ChatDetailsListAdapter extends BaseAdapter {

    SettingsAPI set;
    private List<ChatMessage> mMessages;
    private Context mContext;
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
            holder.photo_iv = (ImageView) convertView.findViewById(R.id.photo_iv);
            holder.video_iv = (VideoView) convertView.findViewById(R.id.video_iv);
            holder.loading_gif_view = (GifImageView) convertView.findViewById(R.id.loading_gif);
            holder.photo_loading_gif = (GifImageView) convertView.findViewById(R.id.photo_loading_gif);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }


//            0=audio, 1=text, 2=photo, 3=video
        try {
            if (msg.getIs_text().equals("1")) {
                holder.message.setVisibility(View.VISIBLE);
                holder.message.setText(msg.getText());
                holder.audio_layout.setVisibility(View.GONE);
                holder.photo_iv.setVisibility(View.GONE);
                holder.video_iv.setVisibility(View.GONE);

            } else if (msg.getIs_text().equals("0")) {
                String aud_name[] = msg.getAudName().split("/");
                holder.message.setVisibility(View.GONE);
                holder.audio_layout.setVisibility(View.VISIBLE);
                holder.aud_name_tv.setText(aud_name[aud_name.length - 1]);
                holder.photo_iv.setVisibility(View.GONE);
                holder.video_iv.setVisibility(View.GONE);

            } else if (msg.getIs_text().equals("2")) {
//                String photoPath[] = msg.getPhotoPath().split("/");

                holder.message.setVisibility(View.GONE);
                holder.audio_layout.setVisibility(View.GONE);
                holder.photo_iv.setVisibility(View.VISIBLE);
                holder.video_iv.setVisibility(View.GONE);

//                set image
                try {
//                    final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
//                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    System.out.println("PHOTOPATH :::"+msg.getPhotoPath());
                    String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    String arr[] =msg.getPhotoPath().split("/");
                    String path=DOWNLOAD_DIR+"/"+arr[2];
                    System.out.println("PHOTO PATH ::"+path);
                    File fileNameOnDevice = new File(path);
                    if (fileNameOnDevice.exists())
                        System.out.println("PATH EXISTS");
                    else
                        System.out.println("PATH WRONG");
                    Uri val=Uri.fromFile(new File(path));
                    System.out.println("PHOTO :"+val);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), val);
                    holder.photo_iv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (msg.getIs_text().equals("3")) {
//                String photoPath[] = msg.getPhotoPath().split("/");

                holder.message.setVisibility(View.GONE);
                holder.audio_layout.setVisibility(View.GONE);
                holder.photo_iv.setVisibility(View.GONE);
                holder.video_iv.setVisibility(View.VISIBLE);


//                set video
                try {
//                    final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
//                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Uri.parse(msg.getPhotoPath()));
//                    holder.photo_iv.setImageBitmap(bitmap);

//                    holder.video_iv.setVideoURI(Uri.parse(path));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
//                    final int takeFlags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
//                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    System.out.println("VIDEOPATH :::" + msg.getVideoPath());
                    String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    String arr[] = msg.getVideoPath().split("/");
                    String path = DOWNLOAD_DIR + "/" + arr[2];
                    System.out.println("VIDEO PATH ::" + path);
                    File fileNameOnDevice = new File(path);
                    if (fileNameOnDevice.exists()) {
                        System.out.println("VIDEO PATH EXISTS");
//                    Uri uri = Uri.parse(URL); //Declare your url here.
//
//                    VideoView mVideoView  = (VideoView)findViewById(R.id.videoview)
//                    mVideoView.setMediaController(new MediaController(this));
//                    mVideoView.setVideoURI(uri);
//                    mVideoView.requestFocus();
//                    mVideoView.start();
                        final MediaController mediacontroller = new MediaController(mContext);
                        mediacontroller.setAnchorView(holder.video_iv);


                        holder.video_iv.setMediaController(mediacontroller);
                        holder.video_iv.setVideoURI(Uri.parse(path));
                        holder.video_iv.requestFocus();

                        holder.video_iv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                    @Override
                                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                        holder.video_iv.setMediaController(mediacontroller);
                                        mediacontroller.setAnchorView(holder.video_iv);

                                    }
                                });
                            }
                        });

                        holder.video_iv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                Toast.makeText(mContext, "Video over", Toast.LENGTH_SHORT).show();
//                                if (index++ == arrayList.size()) {
//                                    index = 0;
//                                    mp.release();
//                                    Toast.makeText(getApplicationContext(), "Videos completed", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    videoView.setVideoURI(Uri.parse(arrayList.get(index)));
//                                    videoView.start();
//                                }


                            }
                        });

                        holder.video_iv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                            @Override
                            public boolean onError(MediaPlayer mp, int what, int extra) {
                                Log.d("API123", "What " + what + " extra " + extra);
                                return false;
                            }
                        });
                    }



                    else
                        System.out.println("VIDEO PATH WRONG");
                    Uri val=Uri.fromFile(new File(path));
                    System.out.println("VIDEO :"+val);
                    holder.video_iv.setVideoURI(Uri.parse(path));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        } catch (NullPointerException e) {
            e.printStackTrace();
        }


		holder.time.setText(msg.getReadableTime());

        if(msg.getReceiver().getId().equals(set.readSetting("myid"))){  //left side
            holder.profile_img.setVisibility(View.VISIBLE);
//            holder.profile_img.setImageURI(Uri.parse(msg.getSender().getPhoto()));
            holder.lyt_parent.setPadding(5, 3, 90, 2);
            holder.lyt_parent.setGravity(Gravity.LEFT);
            if(holder.audio_layout.getVisibility()==View.VISIBLE) {
				holder.play_icon.setImageResource(R.drawable.ic_file_download_dark_24dp);
				holder.time.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
                holder.aud_name_tv.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDarkDarkDark));
                holder.download_iv.setVisibility(View.GONE);
			}
			//Added by Ruchika
//            if(holder.photo_iv.getVisibility()==View.VISIBLE) {
//                holder.photo_iv.setImageResource(R.drawable.ic_file_download_dark_24dp);
//
//                holder.download_iv.setVisibility(View.GONE);
//            }
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


//       Download on click IMAGE
        final String photoPath = msg.getPhotoPath();
        holder.photo_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.photo_iv.setVisibility(View.GONE);
                holder.photo_loading_gif.setVisibility(View.VISIBLE);

                System.out.println("Method photo called" + photoPath);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                String fileName = photoPath;
//        fileName="/111433108964661785456/recording_1539375297988.mp3";
                String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS).getPath();

                StorageReference storageRef = storage.getReference();
                System.out.println("Filename :" + fileName.substring(1));
                StorageReference downloadRef = storageRef.child(fileName.substring(1));
                storageRef.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                String[] arr = photoPath.split("/");
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
                                Log.d(" Photo Download", "downloaded the file");

                                holder.photo_loading_gif.setVisibility(View.GONE);
                                holder.photo_iv.setVisibility(View.VISIBLE);

                                Toast.makeText(mContext, R.string.download_done, Toast.LENGTH_SHORT).show();
                                flag[0] = 1;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("Photo Download", "Failed to download the file");
                        Toast.makeText(mContext,
                                R.string.download_failed,
                                Toast.LENGTH_SHORT).show();

                        holder.photo_loading_gif.setVisibility(View.GONE);
                        holder.photo_iv.setVisibility(View.VISIBLE);
                    }
                });

            }
        });

//        OnClick for VIDEO
        final String VideoPath = msg.getVideoPath();
        holder.video_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Method video called" + VideoPath);
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                String fileName = VideoPath;
////        fileName="/111433108964661785456/recording_1539375297988.mp3";
                String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS).getPath();
//
//                StorageReference storageRef = storage.getReference();
//                System.out.println("Filename :" + fileName.substring(1));
//                StorageReference downloadRef = storageRef.child(fileName.substring(1));
//                storageRef.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
////                        Toast.makeText(mContext, "file found",Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // File not found
//                        Toast.makeText(mContext,
//                                "file not found",
//                                Toast.LENGTH_SHORT).show();
//                        try {
//                            Thread.sleep(10000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//                String[] arr = VideoPath.split("/");
//                System.out.println("Array :" + arr);
//                System.out.println("download ref : " + downloadRef.toString() + " " + downloadRef.getPath() + " " + downloadRef.getName());
//                System.out.println("Pathname :" + DOWNLOAD_DIR + "/" + downloadRef.getName());
//                File localFile = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DOWNLOADS), arr[2]);
//                System.out.println("local file :" + localFile);
//                try {
//                    localFile.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                downloadRef.getFile(localFile);
//                final int[] flag = {0};
//                File fileNameOnDevice = new File(DOWNLOAD_DIR + "/" + downloadRef.getName());
//                System.out.println("File on device :" + fileNameOnDevice);
//
//                downloadRef.getFile(fileNameOnDevice).addOnSuccessListener(
//                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                Log.d(" Video Download", "downloaded the file");
//                                Toast.makeText(mContext, R.string.download_done, Toast.LENGTH_SHORT).show();
//                                flag[0] = 1;
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Log.d("Video Download", "Failed to download the file");
//                        Toast.makeText(mContext,
//                                R.string.download_failed,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });

                String arr1[] = msg.getVideoPath().split("/");
                String path = DOWNLOAD_DIR + "/" + arr1[2];

                File fileNameOnDevice = new File(DOWNLOAD_DIR + "/" + arr1[2]);
                if (fileNameOnDevice.exists()) {
                    System.out.println("VIDEO PATH EXISTS");
//                    Uri uri = Uri.parse(URL); //Declare your url here.
//
//                    VideoView mVideoView  = (VideoView)findViewById(R.id.videoview)
//                    mVideoView.setMediaController(new MediaController(this));
//                    mVideoView.setVideoURI(uri);
//                    mVideoView.requestFocus();
//                    mVideoView.start();
                    final MediaController mediacontroller = new MediaController(mContext);
                    mediacontroller.setAnchorView(holder.video_iv);

                    holder.video_iv.setMediaController(mediacontroller);
                    holder.video_iv.setVideoURI(Uri.parse(path));
                    holder.video_iv.requestFocus();

                    holder.video_iv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                @Override
                                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                    holder.video_iv.setMediaController(mediacontroller);
                                    mediacontroller.setAnchorView(holder.video_iv);

                                }
                            });
                        }
                    });

                    holder.video_iv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            Toast.makeText(mContext, "Video over", Toast.LENGTH_SHORT).show();
//                                if (index++ == arrayList.size()) {
//                                    index = 0;
//                                    mp.release();
//                                    Toast.makeText(getApplicationContext(), "Videos completed", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    videoView.setVideoURI(Uri.parse(arrayList.get(index)));
//                                    videoView.start();
//                                }


                        }
                    });

                    holder.video_iv.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                        @Override
                        public boolean onError(MediaPlayer mp, int what, int extra) {
                            Log.d("API123", "What " + what + " extra " + extra);
                            return false;
                        }
                    });
                }



                else
                    System.out.println("VIDEO PATH WRONG");
                Uri val=Uri.fromFile(new File(path));
                System.out.println("VIDEO :"+val);
                holder.video_iv.setVideoURI(Uri.parse(path));
                FirebaseStorage storage = FirebaseStorage.getInstance();
                String fileName = VideoPath;
//        fileName="/111433108964661785456/recording_1539375297988.mp3";
//                String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
//                        (Environment.DIRECTORY_DOWNLOADS).getPath();

                StorageReference storageRef = storage.getReference();
                System.out.println("Filename :" + fileName.substring(1));
                StorageReference downloadRef = storageRef.child(fileName.substring(1));
                storageRef.child(fileName).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
                String[] arr = VideoPath.split("/");
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
//                File fileNameOnDevice = new File(DOWNLOAD_DIR + "/" + downloadRef.getName());
                System.out.println("File on device :" + fileNameOnDevice);

                downloadRef.getFile(fileNameOnDevice).addOnSuccessListener(
                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Log.d(" Video Download", "downloaded the file");
                                // Changing loading icon to play icon after file is donloading

                                Toast.makeText(mContext, R.string.download_done, Toast.LENGTH_SHORT).show();
                                flag[0] = 1;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("Video Download", "Failed to download the file");
                        Toast.makeText(mContext,
                                R.string.download_failed,
                                Toast.LENGTH_SHORT).show();

                    }
                });


            }
        });

//       play on click AUDIO
        final String audio_path = msg.getAudName();
        final SeekBar seekBar_inner = holder.seekBar;
        holder.play_icon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //play audio code here
                holder.play_icon.setVisibility(View.GONE);
                holder.loading_gif_view.setVisibility(View.VISIBLE);

                seekBar_inner.setVisibility(View.VISIBLE);
                System.out.println("Method play called" + audio_path);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                String fileName = audio_path;
//        fileName="/111433108964661785456/recording_1539375297988.mp3";
                String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
                        (Environment.DIRECTORY_DOWNLOADS).getPath();





////                NEW CODE
//                String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
//                        (Environment.DIRECTORY_DOWNLOADS).getPath();
//                String arr1[] = msg.getVideoPath().split("/");
////                File fileNameOnDevice = new File(DOWNLOAD_DIR + "/" + arr1[2]);
////                if (fileNameOnDevice.exists() || flag[0]==1) {
////                    System.out.println("VIDEO PATH EXISTS");
////                }
//
//                StorageReference storageRef = storage.getReference();
//                System.out.println("Filename :" + fileName.substring(1));
//                StorageReference downloadRef = storageRef.child(fileName.substring(1));
//
//                String[] arr = audio_path.split("/");
//                System.out.println("Array :" + arr);
////                System.out.println("download ref : " + downloadRef.toString() + " " + downloadRef.getPath() + " " + downloadRef.getName());
////                System.out.println("Pathname :" + DOWNLOAD_DIR + "/" + downloadRef.getName());
//                File localFile = new File(Environment.getExternalStoragePublicDirectory(
//                        Environment.DIRECTORY_DOWNLOADS), arr[2]);
//                System.out.println("local file :" + localFile);
//                try {
//                    localFile.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                downloadRef.getFile(localFile);
//                final int[] flag = {0};
//                File fileNameOnDevice = new File(DOWNLOAD_DIR + "/" + downloadRef.getName());
//                System.out.println("File on device :" + fileNameOnDevice);
//
//
//                downloadRef.getFile(fileNameOnDevice).addOnSuccessListener(
//                        new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                            @Override
//                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                                Log.d("File Download", "downloaded the file");
////                                if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
////                                    holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_dark_24dp);
////                                }
////                                else {
////                                    holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
////                                }
////                                Toast.makeText(mContext, R.string.download_done, Toast.LENGTH_SHORT).show();
//                                flag[0] =1;
//
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Log.e("File Download", "Failed to download the file");
//                        if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
//                            holder.play_icon.setImageResource(R.drawable.ic_file_download_dark_24dp);
//                        }
//                        else {
//                            holder.play_icon.setImageResource(R.drawable.ic_file_download_white_24dp);
//                        }
//                        Toast.makeText(mContext,
//                                R.string.download_failed,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//                if (fileNameOnDevice.exists() || flag[0]==1) {
//                    if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
//                        holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_dark_24dp);
//                    } else {
//                        holder.play_icon.setImageResource(R.drawable.ic_play_circle_filled_white_24dp);
//                    }
//                    handler = new Handler();
//                    mediaPlayer = new MediaPlayer();
////                    try {
////                        mediaPlayer.setDataSource(fileNameOnDevice.getPath());
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    RecordDialog recDialog = new RecordDialog();
////                    FragmentManager fm = ((ActivityChatDetails)mContext).getSupportFragmentManager();
////                    recDialog.show(fm,"Record Dialog");
//                    if (mediaPlayer.isPlaying()) {
//                        mediaPlayer.pause();
//                        if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
//                            holder.play_icon.setImageResource(R.drawable.ic_pause_circle_filled_dark_24dp);
//                        } else {
//                            holder.play_icon.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
//                        }
//                    } else {
//
//                        try {
//
//                            mediaPlayer.setDataSource(fileNameOnDevice.getPath());
////                        Toast.makeText(mContext, "media player set", Toast.LENGTH_SHORT).show();
//                            mediaPlayer.prepare();
//                            mediaPlayer.start();
//                            if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
//                                holder.play_icon.setImageResource(R.drawable.ic_pause_circle_filled_dark_24dp);
//                            } else {
//                                holder.play_icon.setImageResource(R.drawable.ic_pause_circle_filled_white_24dp);
//                            }
//                            isPlaying = true;
//                            changeSeekbar();
////                        Toast.makeText(mContext, "Playing Audio", Toast.LENGTH_SHORT).show();
////                        seekBar_inner.setProgress(mediaPlayer.getCurrentPosition());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//




//              OLD CODE
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
                                Toast.makeText(mContext, R.string.download_done, Toast.LENGTH_SHORT).show();
                                flag[0] =1;
                                holder.loading_gif_view.setVisibility(View.GONE);
                                holder.play_icon.setVisibility(View.VISIBLE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.e("File Download", "Failed to download the file");
                        if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
                            holder.play_icon.setImageResource(R.drawable.ic_file_download_dark_24dp);
                        }
                        else {
                            holder.play_icon.setImageResource(R.drawable.ic_file_download_white_24dp);
                        }
                        Toast.makeText(mContext,
                                R.string.download_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                });

                if (fileNameOnDevice.exists() || flag[0]==1) {
                    if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
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
                        if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
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
                            if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
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
                        if (msg.getReceiver().getId().equals(set.readSetting("myid"))) {
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
        ImageView photo_iv;
        VideoView video_iv;
        GifImageView loading_gif_view;
        GifImageView photo_loading_gif;
//        BubbleLinearLayout bubbleView;
	}	
}
