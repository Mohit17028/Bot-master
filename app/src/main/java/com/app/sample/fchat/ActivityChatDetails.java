package com.app.sample.fchat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ListView;
import android.widget.TextView;

import com.app.sample.fchat.adapter.ChatDetailsListAdapter;
import com.app.sample.fchat.data.ParseFirebaseData;
import com.app.sample.fchat.data.SettingsAPI;
import com.app.sample.fchat.data.Tools;
import com.app.sample.fchat.model.ChatMessage;
import com.app.sample.fchat.model.Friend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityChatDetails extends AppCompatActivity implements RecordDialog.RecordDialogListener {
    public static final int RECORD_AUDIO = 0;
    public static final String MESSAGE_CHILD = "messages";
    public static String KEY_FRIEND = "FRIEND";
    public static ChatDetailsListAdapter mAdapter;
    static String uid = "";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    ParseFirebaseData pfbd;
    SettingsAPI set;
    String urlLink = "http://192.168.2.71:9009/answer/";
    String urlLink_hin = "https://api.dialogflow.com/v1/query";
    String chatNode, chatNode_1, chatNode_2;
    String ques = " ";
    String sender_email = "";
    DatabaseReference ref;


    private LinearLayout attachment_layout;
    private ImageView add_image, add_video,attachment_iv;

    private TextView feedback_pos,feedback_neg,feedback_neutral;
    private String feedback_str="";
    private Button btn_send;
    private EditText et_content;
    private ListView listview;
    private ActionBar actionBar;
    private Friend friend;
    private List<ChatMessage> items = new ArrayList<>();
//    String audName;
private View parent_view;
    private TextWatcher contentWatcher = new TextWatcher() {
        @Override

        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btn_send.setEnabled(false);
            } else {
                btn_send.setEnabled(true);
            }
            //draft.setContent(etd.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    // give preparation animation activity transition
    public static void navigate(AppCompatActivity activity, View transitionImage, Friend obj) {
        Intent intent = new Intent(activity, ActivityChatDetails.class);
        intent.putExtra(KEY_FRIEND, obj);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, KEY_FRIEND);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }

    public static void POST(String url, String query) {
        InputStream inputStream = null;
        //String result = "" ;
        JSONObject result = null;
        JSONObject jsonObj = null;
        String f_ids = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = " ";

            //Id object = new Id();
            //String id = object.getId();

            System.out.println("USER ID POST : " + uid);

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("question", query);
            jsonObject.accumulate("id", uid);

            System.out.println("USER ASKED  = " + query);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.d("json string ", json);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("question", query));
            params.add(new BasicNameValuePair("id", "jass"));
            httpPost.setEntity(new UrlEncodedFormEntity(params));


            //Log.d(" json entity ", se+" "+httpPost);
            System.out.println("ENTITY    =   " + httpPost.getEntity());

            HttpResponse httpResponse = httpclient.execute((httpPost));

            //httpResponse.setStatusCode();

            int status = httpResponse.getStatusLine().getStatusCode();

            System.out.print("STATUS == == == " + status);
            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();

            String line;
            try {

                br = new BufferedReader(new InputStreamReader(inputStream));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            String ss = "";
            ss = sb.toString();
            System.out.println(ss + "INPUTSTREAM");

            //System.out.println("ID  ID  = "+ss.charAt(13) + "  " + ss.charAt(16)+ "  "+ss.charAt(19));

        } catch (Exception e) {
            //Log.d("InputStream", e.getLocalizedMessage());
        }
        //Log.d("Post meth " , result);
        //return jsonObj;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_details);
//        Intent intent1 = getIntent();
//        String audName = intent1.getStringExtra("aud_name");
//        Toast.makeText(this,audName,Toast.LENGTH_SHORT).show();

//      remove back btn
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(false);
//        }

        add_image = (ImageView) findViewById(R.id.attach_img);
        add_video = (ImageView) findViewById(R.id.attach_video);
        attachment_iv = (ImageView) findViewById(R.id.attatchment_iv);
        attachment_layout = (LinearLayout) findViewById(R.id.attatchment_layout);

        feedback_pos = (TextView)findViewById(R.id.feedback_pos);
        feedback_neg = (TextView)findViewById(R.id.feedback_neg);
        feedback_neutral = (TextView)findViewById(R.id.feedback_nuetral);

        parent_view = findViewById(android.R.id.content);
        pfbd = new ParseFirebaseData(this);
        set = new SettingsAPI(this);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String userid= pref.getString("userid",null);
        String username=pref.getString("username",null);
        System.out.println("test chat details activity main :"+userid+"  "+username);

        String useremail=pref.getString("useremail",null);
        System.out.println("test login activity main :"+userid+"  "+useremail);
        sender_email = useremail;

        uid = useremail;
        // animation transition
        ViewCompat.setTransitionName(parent_view, KEY_FRIEND);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                10);

        // initialize conversation data
        Intent intent = getIntent();
        friend = (Friend) intent.getExtras().getSerializable(KEY_FRIEND);
        initToolbar();

        iniComponen();
        chatNode_1 = set.readSetting("myid") + "-" + friend.getId();
        chatNode_2 = friend.getId() + "-" + set.readSetting("myid");
        System.out.println("testing"+chatNode+" "+chatNode_1+" "+chatNode_2);

//      /* FEEDBACK */
        feedback_pos.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback_str = feedback_pos.getText().toString();
            }
        });
        feedback_neg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback_str = feedback_neg.getText().toString();
            }
        });
        feedback_neutral.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback_str = feedback_neutral.getText().toString();
            }
        });

        /*  ATTACHMENTS  */
        attachment_iv.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                if(attachment_layout.getVisibility() == View.GONE){
                    attachment_layout.setVisibility(View.VISIBLE);
                }
                else {
                    attachment_layout.setVisibility(View.GONE);
                }

            }
        });
        add_image.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                attachment_layout.setVisibility(View.GONE);
                // add code here.
            }
        });
        add_video.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                attachment_layout.setVisibility(View.GONE);
                // add code here.
            }
        });

        /*  MICROPHONE   */
//        ImageButton microphone = (ImageButton)findViewById(R.id.microphone);
        CircleImageView microphone = (CircleImageView)findViewById(R.id.microphone);
        microphone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(),Microphone.class);
                //startActivity(intent);
////                askSpeechInput();
                showRecordDialog();
                downloadAudioFile();
            }
        });




        ref = FirebaseDatabase.getInstance().getReference(MESSAGE_CHILD);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(chatNode_1)) {
                    chatNode = chatNode_1;

                } else if (dataSnapshot.hasChild(chatNode_2)) {
                    chatNode = chatNode_2;
                } else {
                    chatNode = chatNode_1;
                }
                String totalData = dataSnapshot.child(chatNode).toString();
                items.clear();
                System.out.println("TOTAL DATA :  "+totalData);
                items.addAll(pfbd.getMessageListForUser(totalData));
                mAdapter = new ChatDetailsListAdapter(ActivityChatDetails.this, items);
                listview.setAdapter(mAdapter);
                System.out.println("Listview : "+listview);
                listview.setSelectionFromTop(mAdapter.getCount(), 0);
                listview.requestFocus();

                registerForContextMenu(listview);
                bindView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(getWindow().getDecorView(), "Could not connect", Snackbar.LENGTH_LONG).show();
            }
        });

        // for system bar in lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Tools.systemBarLolipop(this);
        }
    }

    private void downloadAudioFile() {
        System.out.println("download audio file called");
    }

//    @Override
//    public void downloadAudio(final String audName)  {
//        System.out.println("Chat node :download audio called"+chatNode);
//        System.out.println("Start download called");
//        FirebaseStorage storage=FirebaseStorage.getInstance();
//        String fileName=audName;
////        fileName="/111433108964661785456/recording_1539375297988.mp3";
//        String DOWNLOAD_DIR = Environment.getExternalStoragePublicDirectory
//                (Environment.DIRECTORY_DOWNLOADS).getPath();
//
//        StorageReference storageRef = storage.getReference();
//        System.out.println("Filename :"+fileName.substring(1));
//        StorageReference downloadRef = storageRef.child(fileName.substring(1));
//        storageRef.child(fileName.substring(1)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                Toast.makeText(getApplicationContext(),
//                        "file found",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // File not found
//                Toast.makeText(getApplicationContext(),
//                        "file not found",
//                        Toast.LENGTH_SHORT).show();
//                try {
//                    Thread.sleep(10000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        String[] arr= audName.split("/");
//        System.out.println("Array :"+arr);
//        System.out.println("download ref : "+downloadRef.toString()+" "+downloadRef.getPath()+" "+downloadRef.getName());
//        System.out.println("Pathname :"+DOWNLOAD_DIR+"/"+downloadRef.getName());
//        File localFile  = new File(Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_DOWNLOADS), arr[2]);
//        System.out.println("local file :"+localFile);
//        try {
//            localFile .createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        downloadRef.getFile(localFile);
//        File fileNameOnDevice = new File(DOWNLOAD_DIR+"/"+downloadRef.getName());
//        System.out.println("File on device :"+fileNameOnDevice);
//        downloadRef.getFile(fileNameOnDevice).addOnSuccessListener(
//                new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        Log.d("File Download", "downloaded the file");
//                        Toast.makeText(getApplicationContext(),
//                                "Downloaded the file",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Log.e("File Download", "Failed to download the file");
//                Toast.makeText(getApplicationContext(),
//                        "Couldn't be downloaded",
//                        Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void showRecordDialog() {
        RecordDialog recDialog = new RecordDialog();
        recDialog.show(getSupportFragmentManager(), "Record Dialog");
    }

    @Override
    public void applyTexts(String audName) {
//        feedback_str has the feedback,  i have Put it in hashmap just make sure
//        it is parsed correctly in ParseFirebaseData file
        HashMap hm = new HashMap();
//        et_content.setText(null);
        hm.put("text", null);
        hm.put("timestamp", String.valueOf(System.currentTimeMillis()));
        hm.put("receiverid", friend.getId());
        hm.put("receivername", friend.getName());
        hm.put("receiverphoto", friend.getPhoto());
        hm.put("receiveremail", friend.getEmail());
        hm.put("senderid", set.readSetting("myid"));
        hm.put("sendername", set.readSetting("myname"));
        hm.put("senderphoto", set.readSetting("mydp"));
        hm.put("senderemail", sender_email);
        hm.put("audio_name", audName);
        hm.put("isText", "0");
        hm.put("feedback_string", feedback_str);
//        Log.d("hm1",audName);
        System.out.println("hm" + hm.entrySet());
        ref.child(chatNode).push().setValue(hm);
        String qy = String.valueOf(et_content.getText());

        MyFirebaseInstanceIdService ob = new MyFirebaseInstanceIdService();
        ob.onTokenRefresh();
    }

    public void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle(friend.getName());
    }

    public void bindView() {
        try {
            mAdapter.notifyDataSetChanged();
            listview.setSelectionFromTop(mAdapter.getCount(), 0);
        } catch (Exception e) {

        }
    }

    public void iniComponen() {
        listview = (ListView) findViewById(R.id.listview);
        btn_send = (Button) findViewById(R.id.btn_send);
        et_content = (EditText) findViewById(R.id.text_content);

        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                ChatMessage im=new ChatMessage(et_content.getText().toString(), String.valueOf(System.currentTimeMillis()),friend.getId(),friend.getName(),friend.getPhoto());

//                PUT IT HERE TOO IF NEEDED
                HashMap hm = new HashMap();
                //if(et_content.getText().toString()!="")

                    hm.put("text", et_content.getText().toString());

                /*else
                {
                    hm.put("text", ques);
                }*/
//                SharedPreferences pref = getApplicationContext().getSharedPreferences("Record_dialog_pref", 0); // 0 - for private mode
//                String audName= pref.getString("aud_name",null);

                hm.put("timestamp", String.valueOf(System.currentTimeMillis()));
                hm.put("receiverid", friend.getId());
                hm.put("receivername", friend.getName());
                hm.put("receiverphoto", friend.getPhoto());
                hm.put("receiveremail", friend.getEmail());
                hm.put("senderid", set.readSetting("myid"));
                hm.put("sendername", set.readSetting("myname"));
                hm.put("senderphoto", set.readSetting("mydp"));
                hm.put("senderemail", sender_email);
                hm.put("audio_name",null);
                hm.put("isText","1");
                hm.put("feedback_string",feedback_str);
                System.out.println("hm"+hm.entrySet());
                ref.child(chatNode).push().setValue(hm);
                String qy = String.valueOf(et_content.getText());

                MyFirebaseInstanceIdService ob = new MyFirebaseInstanceIdService();
                ob.onTokenRefresh();

                //sendNotificationToUser("puf", "Hi there puf!");



                //ActivitySplash object = new ActivitySplash();
                //String id = object.userId();

                //System.out.println("USER ID POST : "+id);

                //new HttpAsyncTask().execute(qy);

                String hin_question = "सगर्भावस्था के दौरान निप्पल की देखभाल के बारे में आपकी क्या सलाह है?";

                //new HttpAsyncTas().execute(hin_question);
                et_content.setText("");
                hideKeyboard();
            }
        });
        et_content.addTextChangedListener(contentWatcher);
        if (et_content.length() == 0) {
            btn_send.setEnabled(false);
        }
        System.out.println("content test :"+et_content.getText());
        hideKeyboard();
    }

    /*@Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }*/

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void askSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Hi speak something");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            //Intent intent_m = new Intent(getApplicationContext(),Answer.class);
            //intent_m.putExtra("message", "HIIIIII");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        ArrayList<String> result = null;
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    //ArrayList<String>
                    result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String question = result.get(0);
                    ques = question;
                    et_content.setText(ques);

                    //Toast.makeText(this,ques,Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... query) {
            POST(urlLink, query[0]);

            String q = query[0] + " SENT";
            //Toast.makeText(getBaseContext(),q,Toast.LENGTH_LONG).show();
            System.out.println("QUERYYYYYYY   =  " + q);

            return null;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(Void result) {

            JSONObject json = null;
            String statistics = null;

        }
    }
}
