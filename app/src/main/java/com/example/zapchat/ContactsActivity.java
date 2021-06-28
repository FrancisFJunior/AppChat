package com.example.zapchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private GroupAdapter adapter;
    private EditText mEditAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_contacts);

        Button mBtnAdd = findViewById(R.id.btn_Add);
        mEditAdd = findViewById(R.id.edit_Add);
        RecyclerView rv = findViewById(R.id.recycler);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ContactsActivity.this,"Ainda sem função!", Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull @NotNull Item item, @NonNull @NotNull View view) {
                Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);

                UserItem userItem = (UserItem) item;
                intent.putExtra("user", userItem.user);

                startActivity(intent);
            }
        });

        fetUsers();
    }
    private void fetUsers() {
        FirebaseFirestore.getInstance().collection("/users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot queryDocumentSnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Teste", e.getMessage(), e);
                            return;
                        }

                        List<DocumentSnapshot> docs = queryDocumentSnapshot.getDocuments();
                        for (DocumentSnapshot doc:docs){
                            User user = doc.toObject(User.class);

                            if(!FirebaseAuth.getInstance().getUid().equals(user.getUuid())){
                                Log.d("Teste", user.getUserName());
                                Log.d("Teste", FirebaseAuth.getInstance().getUid());
                                Log.d("Teste", user.getUuid());
                                adapter.add(new UserItem(user));
                            }
                        }
                    }
                });

    }

    private class UserItem extends Item<GroupieViewHolder> {

        private final User user;

        private UserItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull @NotNull GroupieViewHolder viewHolder, int position) {

            TextView txtUserName = viewHolder.itemView.findViewById(R.id.textView);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageView);

            txtUserName.setText(user.getUserName());

            Picasso.get()
                    .load(user.getProfileUrl())
                    .into(imgPhoto);



        }

        @Override
        public int getLayout() {
            return R.layout.item_user;
        }
    }
}