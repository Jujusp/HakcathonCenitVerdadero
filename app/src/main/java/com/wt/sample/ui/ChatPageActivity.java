package com.wt.sample.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.wt.sample.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;



import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class ChatPageActivity extends AppCompatActivity {

    private TextView mTextView;
    ListView lvElChatView;
    ArrayList <String> listOfDiscussion = new ArrayList<String>();
    ArrayAdapter arrayAdpt;
    String UserName;


    FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dbr = database.getInstance().getReference().getRoot();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_page);

        lvElChatView = (ListView) findViewById(R.id.lvElChatView);
        arrayAdpt = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfDiscussion);
        lvElChatView.setAdapter(arrayAdpt);

        //Por ser la version beta le pediremos el nombre al usuario
        getUserName();
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = snapshot.getChildren().iterator();

                while (i.hasNext()){
                    set.add(((DataSnapshot)i.next()).getKey());
                }
                arrayAdpt.clear();
                arrayAdpt.addAll(set);
                arrayAdpt.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        lvElChatView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent i = new Intent (getApplicationContext(), DiscussionActivity.class);
                i.putExtra("selected_topic", ((TextView)view).getText().toString());
                i.putExtra("user_name", UserName);
                startActivity(i);
            }
        });



    }

    private void getUserName(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText userName = new EditText(this);
        userName.setHint("Ingrese su usuario");

        builder.setView(userName);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UserName = userName.getText().toString();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getUserName();
            }
        });
        builder.show();


    }
}