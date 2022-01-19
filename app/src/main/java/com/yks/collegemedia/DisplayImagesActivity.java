package com.yks.collegemedia;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DisplayImagesActivity extends AppCompatActivity {

    // Creating DatabaseReference.
   // DatabaseReference databaseReference;

    // Creating RecyclerView.
    RecyclerView recyclerView;

    // Creating RecyclerView.Adapter.
    RecyclerViewAdapter adapter ;

    // Creating Progress dialog
    ProgressDialog progressDialog;

    // Creating List of ImageUploadInfo class.
    List<ImageUploadInfo> list = new ArrayList<>();

    ImageView profileintent,searchintent;

   public static Context ctx;

    private BottomAppBar bottomAppBar;

    AppCompatButton alldata,postm,eventm,mostlikedm,toppostm;
    String searchy="-1";
    String prevsearch="-1";

    String CHANNEL_ID1="1";
    String CHANNEL_ID2="2";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);
        ctx=this;
        setUpBottomAppBar();

      //  Intent intentBackgroundservice = new Intent(this,Firebasenotification.class);
      //  startService(intentBackgroundservice);

        //click event over FAB
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DisplayImagesActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        // Assign id to RecyclerView.
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        profileintent =  findViewById(R.id.profileintent);
        searchintent =  findViewById(R.id.searchintent);

        // Setting RecyclerView size true.
        recyclerView.setHasFixedSize(true);

        // Setting RecyclerView layout as LinearLayout.
     //   recyclerView.setLayoutManager(new LinearLayoutManager(com.yks.collegemedia.DisplayImagesActivity.this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(true);

        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);

        alldata =  findViewById(R.id.alldata);
        postm =  findViewById(R.id.postm);
        eventm =  findViewById(R.id.eventm);
        mostlikedm =  findViewById(R.id.mostlikedm);
        toppostm =  findViewById(R.id.toppostm);

        createNotificationChannel1();
        createNotificationChannel2();


        // Assign activity this to progress dialog.
        progressDialog = new ProgressDialog(com.yks.collegemedia.DisplayImagesActivity.this);

        // Setting up message in Progress dialog.
        progressDialog.setMessage("Loading Post.");

        // Showing progress dialog.

      filter(searchy);

        profileintent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayImagesActivity.this, profileActivity.class);
                i.putExtra("key", "mainprofile");
                i.putExtra("key2", "display");

                startActivity(i);
                        finish();
            }
        });

        searchintent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DisplayImagesActivity.this, searchActivity.class);
                startActivity(i);
                finish();
            }
        });


        adapter = new RecyclerViewAdapter(getApplicationContext(), list);
        recyclerView.setAdapter(adapter);

    }

    @SuppressLint("NonConstantResourceId")
    public void buttonClicked(View view) {


        switch (view.getId()){

            case R.id.alldata :    searchy="-1";       deactivatedallyr(alldata,postm,eventm,mostlikedm,toppostm);break;
            case R.id.postm  :     searchy="0";        deactivatedallyr(postm,alldata,eventm,mostlikedm,toppostm);break;
            case R.id.eventm  :    searchy="1";        deactivatedallyr(eventm,postm,alldata,mostlikedm,toppostm);break;
            case R.id.mostlikedm  :searchy="2";        deactivatedallyr(mostlikedm,postm,eventm,alldata,toppostm);break;
            case R.id.toppostm  :  searchy="3";        deactivatedallyr(toppostm,postm,eventm,mostlikedm,alldata);break;



        }

        if(!prevsearch.equals(searchy)){
            filter(searchy);
            prevsearch=searchy;
        }


      }


    private void deactivatedallyr(AppCompatButton except,AppCompatButton btn1,AppCompatButton btn2, AppCompatButton btn3,AppCompatButton btn4){
        button(except,1);
        button(btn1,0);
        button(btn2,0);
        button(btn3,0);
        button(btn4,0);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private  void button(AppCompatButton button, int state) {
        if (state == 1) {
            button.setTextColor(Color.parseColor("#ffffff"));
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.searchfilter2));

        }else {

            button.setTextColor(Color.parseColor("#666666"));
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.searchfilter));
        }
    }

    @Override
    public void onBackPressed() {

        //  finishAndRemoveTask();
       // finishAffinity();
//        finish();
//        System.exit(0);

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);


    }


      private void filter(String f){
        //  progressDialog.show();

          DatabaseReference  databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.Database_Path);


          // Adding Add Value Event Listener to databaseReference.
          databaseReference.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot snapshot) {
                  list.clear();
                  for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                      ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);

                      if(f.equals("-1") || f.equals("2")){

                          list.add(imageUploadInfo);

                      }else if(f.equals("0") || f.equals("1") || f.equals("3")) {
                          if (imageUploadInfo.gettype().equals(f)) {

                              list.add(imageUploadInfo);

                          }
                      }


                  }

                  adapter.notifyDataSetChanged();
                  if(f.equals("2")){
                      list.sort(new likecomparator());
                  }

               //   progressDialog.dismiss();
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

                  // Hiding the progress dialog.
               //   progressDialog.dismiss();

              }
          });


      }


    class likecomparator implements Comparator<ImageUploadInfo> {

        // override the compare() method
        public int compare(ImageUploadInfo s1, ImageUploadInfo s2)
        {
            return Integer.compare(Integer.parseInt(s1.getlikes()), Integer.parseInt(s2.getlikes()));
        }
    }



    public void createNotificationChannel2() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "post delete";
            String description ="show progress on post delete";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID2, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createNotificationChannel1() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "post upload";
            String description ="show progress on post upload";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID1, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void toast(String s){
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    private void setUpBottomAppBar() {
        //find id
        bottomAppBar = findViewById(R.id.bar);

        //set bottom bar to Action bar as it is similar like Toolbar
        setSupportActionBar(bottomAppBar);




        //click event over Bottom bar menu item
//        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.profilepage:
//                     //   Toast.makeText(DisplayImagesActivity.this, "profile clicked.", Toast.LENGTH_SHORT).show();
//                        Intent i = new Intent(DisplayImagesActivity.this, profileActivity.class);
//                        startActivity(i);
//                        finish();
//                        break;
//                }
//                return false;
//            }
//        });

//        //click event over navigation menu like back arrow or hamburger icon
//        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //open bottom sheet
//
//            }
//        });
    }

    //Inflate menu to bottom bar
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.profilepage:
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * method to toggle fab mode
     *
     * @param view
     */
//    public void toggleFabMode(View view) {
//        //check the fab alignment mode and toggle accordingly
//        if (bottomAppBar.getFabAlignmentMode() == BottomAppBar.FAB_ALIGNMENT_MODE_END) {
//            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
//        } else {
//            bottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
//        }
//    }

}



// 8f:0b:fa:31:cb:9b:ee:1e:c2:f7:b1:5f:48:56:85:e6:7a:53:26:21
// c6:9e:80:81:84:17:b4:f9:69:06:5b:6f:5a:88:12:af:b4:b3:0c:f5:e7:a3:db:ba:ad:89:a7:4b:2a:ba:ea:16
