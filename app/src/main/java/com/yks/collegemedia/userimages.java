package com.yks.collegemedia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class userimages extends AppCompatActivity {

    DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerViewAdapter2 adapter;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();

    ImageView profileintent, searchintent;

    private  String userphonenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userimages);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
             userphonenumber = extras.getString("key");

            String value2 = extras.getString("key2");

        }else{
            toast("ERROR");
        }

//        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        FirebaseUser user= mAuth.getCurrentUser();
//        userphonenumber=user.getPhoneNumber();

        recyclerView = (RecyclerView) findViewById(R.id.userrecyclerView);

        profileintent = findViewById(R.id.profileintent);
        searchintent = findViewById(R.id.searchintent);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
        //   recyclerView.setLayoutManager(new LinearLayoutManager(com.yks.collegemedia.DisplayImagesActivity.this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(true);

        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);


        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(userimages.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Images From Firebase.");

        // Showing progress dialog.
        progressDialog.show();


        databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.Database_Path);


        // Adding Add Value Event Listener to databaseReference.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    //   for(DataSnapshot i : postSnapshot.getChildren()) {

                    if (postSnapshot.child("phonenumber").getValue().equals(userphonenumber)) {
                        ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                        list.add(imageUploadInfo);

                        adapter.notifyDataSetChanged();
                    }
                    //   }
                    //   Toast.makeText(DisplayImagesActivity.this, "url "+imageUploadInfo.getImageURL(), Toast.LENGTH_SHORT).show();

                }

                // Hiding the progress dialog.
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                // Hiding the progress dialog.
                progressDialog.dismiss();

            }
        });

        adapter = new RecyclerViewAdapter2(getApplicationContext(), list);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(userimages.this, profileActivity.class);
        i.putExtra("key", userphonenumber);
        i.putExtra("key2", "display");

        startActivity(i);
        finish();

    }
    void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

}