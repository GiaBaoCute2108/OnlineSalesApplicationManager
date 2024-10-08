package com.manager.appbanhangonline.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.manager.appbanhangonline.R;
import com.manager.appbanhangonline.adapter.UserAdapter;
import com.manager.appbanhangonline.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<User> list;
    UserAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initView();
        getSupportBar();
        getUserFromFire();
    }

    private void getUserFromFire() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                User user = new User();
                                user.setUsername(documentSnapshot.getString("username"));
                                user.setId(Integer.parseInt(documentSnapshot.getId()));
                                list.add(user);
                            }
                            if (!list.isEmpty()) {
                                adapter = new UserAdapter(list, getApplicationContext());
                                recyclerView.setAdapter(adapter);
                            }
                        }
                    }
                });
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
        list = new ArrayList<>();
        toolbar = findViewById(R.id.userToolbar);
        recyclerView = findViewById(R.id.userChatRecycleView);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);

    }
}