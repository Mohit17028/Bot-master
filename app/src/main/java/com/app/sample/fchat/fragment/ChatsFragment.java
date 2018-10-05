package com.app.sample.fchat.fragment;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.Firebase;

import com.app.sample.fchat.ActivityChatDetails;
import com.app.sample.fchat.ActivityMain;
import com.app.sample.fchat.R;
import com.app.sample.fchat.adapter.ChatsListAdapter;
import com.app.sample.fchat.data.ParseFirebaseData;
import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.model.ChatMessage;
import com.app.sample.fchat.widget.DividerItemDecoration;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatsFragment extends Fragment {

    public RecyclerView recyclerView;

    private LinearLayoutManager mLayoutManager;
    public ChatsListAdapter mAdapter;
    private ProgressBar progressBar;

    public static final String MESSAGE_CHILD = "messages";

    View view;

    ParseFirebaseData pfbd;
    SettingsAPI set;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_chat, container, false);
        pfbd = new ParseFirebaseData(getContext());
        set = new SettingsAPI(getContext());

        // activate fragment menu
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        Firebase.setAndroidContext(getContext());

        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
       String userid= pref.getString("userid",null);
       String username=pref.getString("username",null);
        System.out.println("test login :"+userid+"  "+username);
        // Get a reference to our posts

        Firebase ref2 = new Firebase("https://docs-examples.firebaseio.com/web/saving-data/fireblog/posts");
// Attach an listener to read the data at our posts reference
        ref2.addValueEventListener(new com.firebase.client.ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                System.out.println(snapshot.getValue());
//            }

            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                System.out.println("msging test :"+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("msging test : The read failed: " + firebaseError.getMessage());
            }
        });


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(MESSAGE_CHILD);
        System.out.println("testing "+ref.toString()+" "+ref.getKey()+" "+ref.getDatabase());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String totalData = "";
                if (dataSnapshot.getValue() != null)
                    totalData = dataSnapshot.getValue().toString();
                // TODO: 25-05-2017 if number of items is 0 then show something else
                mAdapter = new ChatsListAdapter(getContext(), pfbd.getLastMessageList(totalData));
                recyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new ChatsListAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(View v, ChatMessage obj, int position) {
                        if (obj.getReceiver().getId().equals(set.readSetting("myid"))){
                            ActivityChatDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj.getSender());
                            System.out.println("if called");
                            ChatMessage message = obj;

//                            if (!message.getSender().equals(currentUserId)) {


                                /*NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                                NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                System.out.println("chat 1:"+mNotificationManager.getActiveNotifications());
                            }
                            else {
                                System.out.println("chat 2:"+mNotificationManager.toString());
                            }

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                        .setContentTitle("New Message from " + message.getSender())
                                        .setContentText(message.getText())
                                        .setOnlyAlertOnce(true)
                                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                                mBuilder.setAutoCancel(true);
                                mBuilder.setLocalOnly(false);



                                mNotificationManager.notify(001, mBuilder.build());*/



                        }
                        else if (obj.getSender().getId().equals(set.readSetting("myid"))) {
                            ActivityChatDetails.navigate((ActivityMain) getActivity(), v.findViewById(R.id.lyt_parent), obj.getReceiver());
                            System.out.println("else 25if called");
                            ChatMessage message = obj;

//                            if (!message.getSender().equals(currentUserId)) {


                            /*NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                   NotificationManager mNotificationManager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                System.out.println("chat 3:"+mNotificationManager.getActiveNotifications().toString());
                            }
                            else {
                                System.out.println("chat 4:"+mNotificationManager.toString());
                            }

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
                                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                                    .setContentTitle("New Message from " + message.getSender().getName())
                                    .setContentText(message.getText())
                                    .setOnlyAlertOnce(true)
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                            mBuilder.setAutoCancel(true);
                            mBuilder.setLocalOnly(false);


                            mNotificationManager.notify(001, mBuilder.build());*/


                        }
                    }
                });

                bindView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (getView() != null)
                    Snackbar.make(getView(), "Could not connect", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public void bindView() {
        try {
            mAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        } catch (Exception e) {
        }

    }
}
