package com.yks.collegemedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    // Folder path for Firebase Storage.
    String Storage_Path = "newdata/";//"All_Image_Uploads/";

    // Root Database Name for Firebase Database.
    public static final String Database_Path = "newdata";//"All_Image_Uploads_Database";

    // Creating button.
    Button ChooseButton, UploadButton;

    // Creating EditText.
    EditText ImageName ;

    // Creating ImageView.
    ImageView SelectImage,imageremove,imagerotate;

    // Creating URI.
    Uri FilePathUri;

    // Creating StorageReference and DatabaseReference object.
    StorageReference storageReference;
    DatabaseReference databaseReference;

    // Image request code for onActivityResult() .
    int Image_Request_Code = 7;

    ProgressDialog progressDialog ;

    String TempImageName="";

    byte[] data2;
    float degree=0;

    DatabaseReference profiledatabaseReference;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private  String userphonenumber,username,verified;

    LinearLayout verifieslayout;
    AppCompatButton postbtn,eventbtn;


      NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;
    int notificationId = 1;
    String CHANNEL_ID1="1";

    String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpost);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user= mAuth.getCurrentUser();

        userphonenumber=user.getPhoneNumber();
        profiledatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(userphonenumber);

        username="";
        verified="null";
        type="0";
        loadprofile();




//
//        mAuth = FirebaseAuth.getInstance();
//        currentUser = mAuth.getCurrentUser();
//
//        if (currentUser == null){                                       //check if the user is new then signIn anonymously
//            mAuth.signInAnonymously().                                 //.signInAnonymously is a method provided by Firebase
//                    addOnCompleteListener(new OnCompleteListener<AuthResult>() {        //insert a Listener that listen
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task)          // insert a method that will executes when the process is completed
//                {
//                    if (task.isSuccessful())                    // check the required task is completed successfully
//                    {
//                       toast("loggin success");
//                    }
//                }
//            })
//                    .addOnFailureListener(new OnFailureListener() {         //if the signin failed
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                     //       Log.e("TAG",e.getMessage());
//                            //return error in logs
//                            toast("error ");
//                            toast(e.getMessage());
//                        }
//                    });
//        }
//        else                                            //check if the user is not new
//        {
//           toast("already login");
//        }



        // Assign FirebaseDatabase instance with root database name.

        //Assign ID'S to button.
        ChooseButton = (Button)findViewById(R.id.ButtonChooseImage);
        //  ChooseButton=findViewById(R.id.ButtonChooseImage);
        UploadButton = (Button)findViewById(R.id.ButtonUploadImage);


        // Assign ID's to EditText.
        ImageName = (EditText)findViewById(R.id.ImageNameEditText);

        // Assign ID'S to image view.
        SelectImage = (ImageView)findViewById(R.id.ShowImageView);
        imageremove = (ImageView)findViewById(R.id.imageremove);
        imagerotate = (ImageView)findViewById(R.id.imagerotate);

        verifieslayout = findViewById(R.id.layout_button2);
        postbtn = findViewById(R.id.postbtn);
        eventbtn = findViewById(R.id.eventbtn);

        verifieslayout.setVisibility(View.GONE);



        final int[] lastSpecialRequestsCursorPosition = {0};
        final String[] specialRequests = {""};
        ImageName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastSpecialRequestsCursorPosition[0] = ImageName.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ImageName.removeTextChangedListener(this);

                if (ImageName.getLineCount() > 3) {
                    ImageName.setText(specialRequests[0]);
                    ImageName.setSelection(lastSpecialRequestsCursorPosition[0]);
                }
                else
                    specialRequests[0] = ImageName.getText().toString();

                ImageName.addTextChangedListener(this);
            }
        });

        // Assigning Id to ProgressDialog.
        progressDialog = new ProgressDialog(MainActivity.this);

        // Adding click listener to Choose image button.
        ChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Creating intent.
                Intent intent = new Intent();

                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Please Select Image"), Image_Request_Code);

            }
        });

        imageremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilePathUri = null;
                ChooseButton.setText("SELECT");
                SelectImage.setVisibility(View.GONE);
                imageremove.setVisibility(View.GONE);
                imagerotate.setVisibility(View.GONE);
            }
        });

        // Adding click listener to Upload image button.
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Calling method to upload selected image on Firebase storage.
                if(username.trim().isEmpty()) {
                    toast("Please add username in your profile page");
                }else if(username.trim().equals(userphonenumber)){
                    toast("Please enter a valid username in profile page");
                }else {
                    UploadImageFileToFirebaseStorage();
                }
            }
        });


        imagerotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                degree= degree+90;
                  rotate(degree);

            }
        });
    }

    private void rotate(float degree){
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

            rotated.compress(Bitmap.CompressFormat.JPEG, 25, baos);

            data2 = baos.toByteArray();

          //  long lengthbmp = data2.length;

            Glide.with(this).load(data2).into(SelectImage);
          //  toast("new l : "+lengthbmp);
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Image_Request_Code && resultCode == RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {



                // Getting selected image into Bitmap.
                Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), FilePathUri);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                Matrix matrix = new Matrix();
                matrix.postRotate(0);
                Bitmap rotated=Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);

                rotated.compress(Bitmap.CompressFormat.JPEG,25,baos);

                 data2=baos.toByteArray();

                long lengthbmp = data2.length;

                if(lengthbmp> 325000L){
                    FilePathUri=null;
                    toast("Please upload image less than 4 MB");
                }else {
                    Glide.with(this).load(data2).into(SelectImage);
                    SelectImage.setVisibility(View.VISIBLE);
                    ChooseButton.setText("Selected");
                    imageremove.setVisibility(View.VISIBLE);
                    imagerotate.setVisibility(View.VISIBLE);
                    degree=0;
                }
           //     toast("l : "+lengthbmp);
                // Setting up bitmap selected image into ImageView.
               // SelectImage.setImageBitmap(bitmap); 3mb=263039 ,r= 7.64797 ,4mb =320780



            }
            catch (Exception e) {

                e.printStackTrace();
            }
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {


        createnotification();

        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);

        String ImageUploadId = databaseReference.push().getKey();
        TempImageName = ImageName.getText().toString().trim();

        // Checking whether FilePathUri Is empty or not.
        if (FilePathUri != null ) {


            // Setting progressDialog Title.
            progressDialog.setTitle("Post is Uploading...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);

            // Showing progressDialog.
            progressDialog.show();

            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());


            // Creating second StorageReference.

            StorageReference storageReference2nd = storageReference.child(Storage_Path  +ImageUploadId);

            // Adding addOnSuccessListener to second StorageReference.
         UploadTask uploadTask=   storageReference2nd.putBytes(data2);
         /*  putFile(FilePathUri)  putBytes(data2);
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Getting image name from EditText and store into string variable.

                            // Hiding the progressDialog after done uploading.
                            progressDialog.dismiss();

                            // Showing toast message after done uploading.
       //taskSnapshot.getMetadata().toString()
                            @SuppressWarnings("VisibleForTests")
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName,storageReference2nd.getDownloadUrl().toString() );

                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                            Toast.makeText(getApplicationContext(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();

                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Image is Uploading...");

                        }
                    });

          */

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference2nd.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();
                        ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName,downloadUri.toString(),username ,userphonenumber,"0",ImageUploadId,date,type);

                        // Getting image upload ID.

                        // Adding image upload id s child element into databaseReference.
                        databaseReference.child(ImageUploadId).setValue(imageUploadInfo);

                        progressDialog.dismiss();

                       // toast(downloadUri.toString());
                 //       ImageName.setText(downloadUri.toString());
                        toast("Post Uploaded Successfully");

                        builder.setContentText("Post Uploaded Successfully").setProgress(0,0,false);
                        notificationManager.notify(notificationId, builder.build());

                        Intent i = new Intent(MainActivity.this, DisplayImagesActivity.class);
                        startActivity(i);
                        finish();

                    } else {
                        progressDialog.dismiss();

                        toast("Post Upload Failed");
                        builder.setContentText("Post Upload Failed").setProgress(0,0,false);
                        notificationManager.notify(notificationId, builder.build());

                        // Handle failures
                        // ...
                    }
                }
            });
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded "+ (int)progress + "%");
                ///////notification////////
                      builder.setContentText(String.valueOf(progress)).setProgress(100, (int) progress, false);
                      notificationManager.notify(notificationId, builder.build());

                }
            }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {

                 //   progressDialog.setMessage("upload paused");
                    builder.setContentText("upload paused").setProgress(0, 0, false);
                    notificationManager.notify(notificationId, builder.build());


                }
            });


        }else  if(!TempImageName.isEmpty()){
            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(TempImageName,"null",username,userphonenumber,"0",ImageUploadId ,date,type);

            // Getting image upload ID.

            // Adding image upload id s child element into databaseReference.
            progressDialog.setTitle("Post is Uploading...");
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.setCancelable(true);

            // Showing progressDialog.
            progressDialog.show();

            databaseReference.child(ImageUploadId).setValue(imageUploadInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    progressDialog.dismiss();
                    toast("Post Uploaded successfully");


                    builder.setContentText("Post Uploaded Successfully").setProgress(0,0,false);
                    notificationManager.notify(notificationId, builder.build());


                    Intent i = new Intent(MainActivity.this, DisplayImagesActivity.class);
                    startActivity(i);
                    finish();


                }
            });


        }
        else {

            Toast.makeText(MainActivity.this, "Please Select Image or Add some thoughts...", Toast.LENGTH_LONG).show();

        }
    }

    @SuppressLint("NonConstantResourceId")
    public void buttonClicked(View view) {


        switch (view.getId()) {

            case R.id.postbtn:
                type = "0";
                deactivatedallyr(postbtn,eventbtn);
                break;
            case R.id.eventbtn:
                type = "1";
                deactivatedallyr(eventbtn, postbtn);
                break;
        }
    }


    private void deactivatedallyr(AppCompatButton except, AppCompatButton btn1){
        button(except,1);
        button(btn1,0);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private  void button(AppCompatButton button, int state) {
        if (state == 1) {
            button.setTextColor(Color.parseColor("#ffffff"));
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.selectbg));

        }else {

            button.setTextColor(Color.parseColor("#666666"));
            button.setBackgroundDrawable(getResources().getDrawable(R.drawable.selectbg2));
        }
    }

            private  void loadprofile(){
        profiledatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                DataModal user = snapshot.getValue(DataModal.class);
                if(user==null){
                    username="";
                }else {
                    username = user.getName();
                    verified=user.getverified();
                    if(!verified.equals("null")){
                        verifieslayout.setVisibility(View.VISIBLE);
                        type="0";

                    }else {
                        type="0";
                    }
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(MainActivity.this, "failed to load profile data", Toast.LENGTH_SHORT).show();

            }
        });


    }

    void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onStart() {

        super.onStart();


    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(MainActivity.this, DisplayImagesActivity.class);
        startActivity(i);
        finish();

    }

    public  void  createnotification(){

         notificationManager = NotificationManagerCompat.from(this);
         builder = new NotificationCompat.Builder(this,CHANNEL_ID1 );
        builder.setContentTitle("Post Uploading")
                .setContentText("Uploading in progress")
                .setSmallIcon(R.drawable.ic_baseline_school_24)
                .setPriority(NotificationCompat.PRIORITY_MAX);

// Issue the initial notification with zero progress
        int PROGRESS_MAX = 100;
        int PROGRESS_CURRENT = 0;
        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
        notificationManager.notify(notificationId, builder.build());

    }


}
