package com.app.sample.fchat;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import org.apache.commons.io.FileUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;

import com.google.cloud.speech.v1p1beta1.SpeechSettings;
import com.google.protobuf.ByteString;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.io.FileInputStream;
import java.util.List;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import android.os.*;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;



import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

public class RecordDialog extends AppCompatDialogFragment {

//    private Button play,stop,save;
    private Chronometer chrono;
    private MediaRecorder myAudioRecorder;
    private String opFile;
    private String outputFile;
    private static String urlLink = "http://192.168.2.71:9009/audioqa/";
    private RecordDialogListener listener;

    public interface RecordDialogListener{
        void applyTexts(String audName);
//        void downloadAudio(String audName);
    }
    public interface MyAlertDialogResultInterface {
        abstract void onButtonClicked(int button);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder buider = new AlertDialog.Builder(getActivity());
//        AlertDialog alert = buider.create();
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        String userid= pref.getString("userid",null);
        String username=pref.getString("username",null);
        System.out.println("test record dialog activity main :"+userid+"  "+username);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_record_dialog, null);

        buider.setView(view);

//        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.RECORD_AUDIO},
//                    10);
//        }
//        play = (Button)view.findViewById(R.id.play_btn);
//        stop = (Button)view.findViewById(R.id.stop_btn);
//        save = (Button)view.findViewById(R.id.save_btn);
        chrono = (Chronometer)view.findViewById(R.id.chronometer);
        buider.setCancelable(false);



//        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android.Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[] { android.Manifest.permission.RECORD_AUDIO },
//                    10);
//
//            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
//            opFile = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/recording_"+timestamp.getTime()+".3gp";
////        System.out.println("Record Dialog: filename :"+opFile);
//            outputFile="/"+userid+"/recording_"+timestamp.getTime()+".3gp";
//            myAudioRecorder = new MediaRecorder();
//            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//            myAudioRecorder.setOutputFile(opFile);
//            chrono.start();
//
//            startRecording();
//
//        } else {

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            opFile = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/recording_"+timestamp.getTime()+".mp3";
//        System.out.println("Record Dialog: filename :"+opFile);
            outputFile="/"+userid+"/recording_"+timestamp.getTime()+".mp3";
//            outputFile="/recording_"+timestamp.getTime()+"_"+userid+".mp3";
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            myAudioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            myAudioRecorder.setOutputFile(opFile);
            chrono.start();

            startRecording();
//        }

        buider.setPositiveButton("Stop & Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                try {
                    myAudioRecorder.stop();
                    myAudioRecorder.release();
                    chrono.stop();
                    myAudioRecorder = null;

                /*SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("Filename",opfile);*/

                    //                stop.setEnabled(false);
                    //                play.setEnabled(true);
//                }catch (IllegalStateException e){
////                    Log.e("RECORDING :: ",e.getMessage());
//                    e.printStackTrace();
//                }
                Toast.makeText(getContext(), "Question Saved :)", Toast.LENGTH_LONG).show();
                Uri uriAudio = Uri.fromFile(new File(opFile).getAbsoluteFile());
                System.out.println(uriAudio+"uriaudio");

                FirebaseStorage storage = FirebaseStorage.getInstance();
//                final StorageReference audioRef = ref.child("Education/audio").child(uriAudio.getLastPathSegment());
                StorageReference storageRef = storage.getReference(outputFile);

                // on success upload audio
                storageRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot audioSnapshot) {


                        @SuppressWarnings("VisibleForTests") Uri audioUrl = audioSnapshot.getDownloadUrl();

                    }
                });



                doFileUpload(opFile);

                listener.applyTexts(outputFile);
//                listener.downloadAudio(outputFile);
                //translateAudioToText();

//
            }
        });




//        stop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                myAudioRecorder.stop();
//                myAudioRecorder.release();
//                chrono.stop();
//                myAudioRecorder = null;
//                stop.setEnabled(false);
//                play.setEnabled(true);
//                Toast.makeText(getContext(), "Audio Recorded Successfully", Toast.LENGTH_LONG).show();
//            }
//        });

//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MediaPlayer mediaPlayer = new MediaPlayer();
//                try{
//                    mediaPlayer.setDataSource(opFile);
//                    mediaPlayer.prepare();
//                    mediaPlayer.start();
//
//                    Toast.makeText(getContext(), "Playing Audio", Toast.LENGTH_SHORT).show();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });

//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), "Question Saved :)", Toast.LENGTH_LONG).show();
//                Uri uriAudio = Uri.fromFile(new File(opFile).getAbsoluteFile());
//                System.out.println(uriAudio+"uriaudio");
//                FirebaseStorage storage = FirebaseStorage.getInstance();
////                final StorageReference audioRef = ref.child("Education/audio").child(uriAudio.getLastPathSegment());
//                StorageReference storageRef = storage.getReference(outputFile);
//
//                // on success upload audio
//                storageRef.putFile(uriAudio).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(final UploadTask.TaskSnapshot audioSnapshot) {
//
//
//                        @SuppressWarnings("VisibleForTests") Uri audioUrl = audioSnapshot.getDownloadUrl();
//
//                    }
//                });
//
//                doFileUpload(opFile);
//                //translateAudioToText();
//
//                //translateAudioToText();
//
//            }
//        });
        return buider.create();
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (RecordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"must implement RecordDialogListener");

        }
    }

    public static void doFileUpload(final String selectedPath){//}, final Handler handler) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpPost post = new HttpPost(urlLink);
                File file = new File(selectedPath);
                //String message = "This is a multipart post";
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                //builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addBinaryBody("qaudio", file, ContentType.DEFAULT_BINARY, selectedPath);

//
                HttpEntity entity = builder.build();
                post.setEntity(entity);
                HttpClient client = new DefaultHttpClient();
                try {
                    HttpResponse response = client.execute((HttpUriRequest) post);
                    System.out.println("RESPONSE  :::  "+response.getStatusLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        /*MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();
        File file = new File(selectedPath);
        multipartEntity.addBinaryBody("qaudio", file, ContentType.create("audio/mp3"), file.getName());
//Json string attaching
        String json = null;


        multipartEntity.addPart("qaudio", new StringBody(json, ContentType.MULTIPART_FORM_DATA));


        HttpPut put = new HttpPut("url");
        put.setEntity((HttpEntity) multipartEntity.build());
        HttpResponse response = client.execute(put);
        int statusCode = response.getStatusLine().getStatusCode();


        InputStreamEntity reqEntity = null;
        try {
            reqEntity = new InputStreamEntity(new FileInputStream(selectedPath), -1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        reqEntity.setContentType("application/octet-stream");
        reqEntity.setChunked(true);

        HttpPost httpPost = new HttpPost(urlLink);
        httpPost.setEntity(reqEntity);
        HttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse response = httpClient.execute((HttpUriRequest) httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        /*try{

            System.out.println("FILE UPLOAD");
            //String urlString = "http://twiliosms.ashrafnaim.com/imgUpload/audio.php";

            File file=new File(selectedPath);

            RequestParams params = new RequestParams();
            params.put("qaudio",selectedPath);
            AsyncHttpClient client = new AsyncHttpClient();
            client.post(urlLink, params,new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String s=new String(responseBody);
                    System.out.println(" SERVER RESPONSE  "+s+"  STATUS CODE : "+statusCode);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }


            });

        }
        catch (Exception e)
        {

        }*/

        /*new Thread(new Runnable() {
            @Override
            public void run() {


                System.out.println("PATH : "+selectedPath);
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                DataInputStream inStream = null;
                String lineEnd = "rn";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                System.out.println(" INITIAL BUFFER SIZE : "+maxBufferSize);
                String responseFromServer = "";
                try {
                    //------------------ CLIENT REQUEST

                    FileInputStream fileInputStream = new FileInputStream(new File(selectedPath));
                    System.out.println("File:"+fileInputStream.toString());
                    // open a URL connection to the Servlet
                    URL url = new URL(urlLink);
                    System.out.println(" URL : "+url);
                    // Open a HTTP connection to the URL
                    conn = (HttpURLConnection) url.openConnection();
                    // Allow Inputs
                    conn.setDoInput(true);
                    // Allow Outputs
                    conn.setDoOutput(true);
                    // Don't use a cached copy.
                    conn.setUseCaches(false);
                    // Use a post method.
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("qaudio",selectedPath);
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data;name=\"qaudio\";filename=\""
                            + selectedPath + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    System.out.println("FILE UPLOAD");

                    // create a buffer of maximum size
                    bytesAvailable = fileInputStream.available();
                    System.out.println("bytes available :"+bytesAvailable);
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    System.out.println("buffer size:"+bufferSize);
                    buffer = new byte[bufferSize];
                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    System.out.println("BYTES READ : "+bytesRead);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        System.out.println("test "+dos.size()+" ");
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    // close streams
                    Log.e("Debug", "File is written");
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                    System.out.println("FILE UPLOAD");
                    System.out.println("RESPONSE CODE "+conn.getResponseCode());

                } catch (MalformedURLException ex) {
                    Log.e("Debug", "error url: " + ex.getMessage(), ex);
                    //sendMessageBack(responseFromServer, 0, handler);
                    return;
                } catch (IOException ioe) {
                    Log.e("Debug", "error: " + ioe.getMessage(), ioe);
                    //sendMessageBack(responseFromServer, 0, handler);
                    return;
                }
                try {
                    System.out.println("CONNECTION :: "+conn.getResponseMessage() +" "+responseFromServer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                responseFromServer = processResponse(conn, responseFromServer);
                //sendMessageBack(responseFromServer, 1, handler);
            }
        }).start();*/

    }

    private static String processResponse(HttpURLConnection conn, String responseFromServer) {
        DataInputStream inStream;
        try {
            //System.out.println("CONNECTION :: "+conn.getResponseMessage() +" "+responseFromServer);
            inStream = new DataInputStream(conn.getInputStream());
            String str;

            while ((str = inStream.readLine()) != null) {
                responseFromServer = str;
            }
            inStream.close();

        } catch (IOException ioex) {
            Log.e("Debug", "error response:  " + ioex.getMessage(), ioex);
        }
        return responseFromServer;
    }
    static void sendMessageBack(String responseFromServer, int success, Handler handler) {
        Message message = new Message();
        message.obj = responseFromServer;
        message.arg1 = success;
        handler.sendMessage(message);
    }
    private void translateAudioToText() {
        // Instantiates a client
        System.out.println("translate audio to text called");
//        System.out.println(Environment.GetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS"));

        new AsyncTask<Void, Void, Void>() {


            @Override
            protected Void doInBackground(Void... params) {
                try {
//                    String filename="C:\\Users\\hp 1\\Desktop\\Chatbot 3-a4af3ee07b3f.json";
//                    System.out.println("in try");
//                    FileInputStream credentialsStream = new FileInputStream(filename);
//                    GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream);
//                    FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credentials);
//
//
//      SpeechSettings speechSettings =
//                            SpeechSettings.newBuilder()
//                                    .setCredentialsProvider(credentialsProvider)
//                                    .build();
//
//                    SpeechClient speechClient = SpeechClient.create(speechSettings);
                    SpeechClient speechClient = SpeechClient.create();
                    {
                        System.out.println(" SPEECH CLIENT");
                        // The path to the audio file to transcribe
                        String fileName = "./resources/audio.raw";
                        File file = new File(opFile).getAbsoluteFile();
                        // Reads the audio file into memory
//            Path path = Paths.get(fileName);
//            byte[] data = Files.readAllBytes(path);
                        System.out.println("file :" + file);
                        byte[] data = FileUtils.readFileToByteArray(file);
                        System.out.println("data :" + data.toString());
                        ByteString audioBytes = ByteString.copyFrom(data);
                        System.out.println("audio bytes :" + audioBytes.toString());
                        // Builds the sync recognize request
                        RecognitionConfig config = RecognitionConfig.newBuilder()
                                .setEncoding(AudioEncoding.LINEAR16)
                                .setSampleRateHertz(16000)
                                .setLanguageCode("en-US")
                                .build();
                        System.out.println("config :" + config.getLanguageCode());
                        RecognitionAudio audio = RecognitionAudio.newBuilder()
                                .setContent(audioBytes)
                                .build();

                        // Performs speech recognition on the audio file
                        RecognizeResponse response = speechClient.recognize(config, audio);
                        System.out.println("response :" + response);
                        List<SpeechRecognitionResult> results = response.getResultsList();
                        System.out.println("results :" + results);
                        for (SpeechRecognitionResult result : results) {
                            // There can be several alternative transcripts for a given chunk of speech. Just use the
                            // first (most likely) one here.
                            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                            System.out.printf("Transcription: %s%n", alternative.getTranscript());
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
return null;
            }
        }.execute();
    }

    private void startRecording() {
        try{
            myAudioRecorder.prepare();
            myAudioRecorder.start();
            chrono.start();
        }catch (IllegalStateException ise){
            ise.printStackTrace();
        }catch (IOException ioe){
            ioe.printStackTrace();
        }

//        stop.setEnabled(true);

        Toast.makeText(getContext(),"Recording Started", Toast.LENGTH_LONG).show();

    }


}
