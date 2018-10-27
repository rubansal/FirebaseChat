package com.codingblocks.firebase;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class chat extends AppCompatActivity {

    LinearLayout layout;
    ImageView sendButton;
    EditText messageArea;
    ScrollView scrollView;
    Firebase reference1,reference2;

    TextToSpeech t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        layout=findViewById(R.id.layout1);
        sendButton=findViewById(R.id.sendButton);
        messageArea=findViewById(R.id.messageArea);
        scrollView=findViewById(R.id.scrollView);

        t=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=TextToSpeech.ERROR)
                    t.setLanguage(Locale.US);
            }
        });

        Firebase.setAndroidContext(this);
        reference1=new Firebase("https://fir-c3a69.firebaseio.com/messages/"+UserDetails.username+"_"+UserDetails.chatWith);
        reference2=new Firebase("https://fir-c3a69.firebaseio.com/messages/"+UserDetails.chatWith+"_"+UserDetails.username);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText=messageArea.getText().toString();
                if(!messageText.equals("")){
                    Map<String,String> map=new HashMap<String,String>();
                    map.put("message",messageText);
                    map.put("user",UserDetails.username);
                    reference1.push().setValue(map);
                    reference2.push().setValue(map);
                }
            }
        });

        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Map map=dataSnapshot.getValue(Map.class);
                String message=map.get("message").toString();
                String userName=map.get("user").toString();

                if(userName.equals(UserDetails.username)){
                    addMessageBox("You:-\n"+message,1);
                }
                else {
                    addMessageBox(UserDetails.chatWith+":-\n"+message,2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addMessageBox(String message,int type){
        TextView textView=new TextView(chat.this);
        textView.setText(message);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0,0,0,10);
        textView.setLayoutParams(lp);
        if(type==1){
            textView.setBackgroundResource(R.drawable.rounded_corner1);
            //t.speak(message,TextToSpeech.QUEUE_FLUSH,null);
        }
        else{
            textView.setBackgroundResource(R.drawable.rounded_corner2);
            t.speak(message,TextToSpeech.QUEUE_FLUSH,null);
        }
        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);
    }
}
