package com.manager.appbanhangonline.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.adapter.ChatAdapter;
import com.manager.appbanhangonline.model.ChatMessage;
import com.manager.appbanhangonline.utils.Utils;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    EditText editText;
    ImageView imageView;
    FirebaseFirestore db;
    ChatAdapter adapter;
    List<ChatMessage> list;
    int iduser;
    String str_iduser;
    String username;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        iduser = getIntent().getIntExtra("id", 0);
        str_iduser = String.valueOf(iduser);
        initView();
        getSupportBar();
        initControl();
        listenMess();
    }
    private void initControl() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessToFirebase();
            }
        });
    }

    private void sendMessToFirebase() {
        String str_mess = editText.getText().toString().trim();
        if (TextUtils.isEmpty(str_mess)) {

        } else {
            HashMap<String, Object> message = new HashMap<>();
            message.put(Utils.IDSEND, String.valueOf(Utils.user_current.getId()));
            message.put(Utils.IDRECEIVE, str_iduser);
            message.put(Utils.MESS, str_mess);
            message.put(Utils.DATETIME, new Date());
            db.collection(Utils.PATH_CHAT).add(message);
            editText.setText("");
        }
    }

    private void listenMess() {
        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.IDSEND, String.valueOf(Utils.user_current.getId()))
                .whereEqualTo(Utils.IDRECEIVE, str_iduser)
                .addSnapshotListener(eventListener);

        db.collection(Utils.PATH_CHAT)
                .whereEqualTo(Utils.IDSEND, str_iduser)
                .whereEqualTo(Utils.IDRECEIVE, String.valueOf(Utils.user_current.getId()))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = list.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.sendid = documentChange.getDocument().getString(Utils.IDSEND);
                    chatMessage.receiveid = documentChange.getDocument().getString(Utils.IDRECEIVE);
                    chatMessage.mess = documentChange.getDocument().getString(Utils.MESS);
                    chatMessage.dateObj = documentChange.getDocument().getDate(Utils.DATETIME);
                    chatMessage.datetime = format_date(documentChange.getDocument().getDate(Utils.DATETIME));
                    list.add(chatMessage);
                }
            }
            Collections.sort(list, (obj1, obj2) -> obj1.dateObj.compareTo(obj2.dateObj));
            if (count == 0) {
                adapter.notifyDataSetChanged();
            } else {
                adapter.notifyItemRangeInserted(list.size(), list.size());
                recyclerView.smoothScrollToPosition(list.size()-1);
            }
        }
    };

    private String format_date(Date date) {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date);
    }

    private void getSupportBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        db = FirebaseFirestore.getInstance();
        toolbar = findViewById(R.id.toolbar_chat);
        recyclerView = findViewById(R.id.recycleview_chat);
        editText = findViewById(R.id.edittextBox);
        imageView = findViewById(R.id.imageSend);
        textView = findViewById(R.id.usernameMessage);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        list = new ArrayList<>();
        adapter = new ChatAdapter(getApplicationContext(), list, String.valueOf(Utils.user_current.getId()));
        recyclerView.setAdapter(adapter);
        username = getIntent().getStringExtra("username");
        textView.setText(username);
    }
}