package com.app.sample.fchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.app.sample.fchat.adapter.ChatsListAdapter;
import com.app.sample.fchat.adapter.FriendsListAdapter;
import com.app.sample.fchat.data.ParseFirebaseData;
import com.app.sample.fchat.data.Tools;
import com.app.sample.fchat.model.ChatMessage;
import com.app.sample.fchat.model.Friend;
import com.app.sample.fchat.widget.DividerItemDecoration;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.grpc.netty.shaded.io.netty.util.internal.SocketUtils;

public class ActivitySelectFriend extends AppCompatActivity {

    private Toolbar toolbar;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private FriendsListAdapter mAdapter;
    List<Friend> friendList;

    public static final String USERS_CHILD = "users";
    ParseFirebaseData pfbd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar();
        initComponent();
        friendList=new ArrayList<>();
        pfbd = new ParseFirebaseData(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String userid= pref.getString("userid",null);
        String username=pref.getString("username",null);
        String useremail=pref.getString("useremail",null);
        System.out.println("test login select friend :"+userid+"  "+username+" "+useremail);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(USERS_CHILD);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String totalData = dataSnapshot.getValue().toString();
                System.out.println("FRIEND SELECTED  :::  "+totalData);
                totalData = "{101303631882520175868={name=Start a conversation, photo=https://lh5.googleusercontent.com/-aB7ra_oRRdo/AAAAAAAAAAI/AAAAAAAAAT0/YV97n4P2WC8/s96-c/photo.jpg, id=101303631882520175868}}";
                // TODO: 25-05-2017 if number of items is 0 then show something else
                mAdapter = new FriendsListAdapter(ActivitySelectFriend.this, pfbd.getUserList(totalData));
                recyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new FriendsListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Friend obj, int position) {
                        System.out.println("OBJECT ------  "+obj);
                        String bot = obj.getId();
                        obj.setId("101303631882520175868");
                        obj.setName(getApplicationContext().getResources().getString(R.string.bot_name));
                        obj.setPhoto("\"https://lh5.googleusercontent.com/-aB7ra_oRRdo/AAAAAAAAAAI/AAAAAAAAAT0/YV97n4P2WC8/s96-c/photo.jpg\"");
                        obj.setEmail(getApplicationContext().getResources().getString(R.string.bot_name));
                        ActivityChatDetails.navigate((ActivitySelectFriend) ActivitySelectFriend.this, findViewById(R.id.lyt_parent), obj);
                    }
                });

                bindView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                    Snackbar.make(getWindow().getDecorView(), "Could not connect", Snackbar.LENGTH_LONG).show();
                //Snackbar.make(getWindow().getDecorView(), (CharSequence) databaseError, Snackbar.LENGTH_LONG).show();
            }
        });

        // for system bar in lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Tools.systemBarLolipop(this);
        }
    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        
        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    public void initToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
//        actionBar.setSubtitle(Constant.getFriendsData(this).size()+" friends");
    }

    private void prepareActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout: {
                Intent logoutIntent=new Intent(this,ActivitySplash.class);
                logoutIntent.putExtra("mode","logout");
                startActivity(logoutIntent);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void bindView() {
        try {
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }
}
