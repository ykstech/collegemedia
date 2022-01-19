package com.yks.collegemedia;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class loginActivity extends AppCompatActivity {
   private static Context cxt;

    private FirebaseAuth mAuth;

    // variable for our text input
    // field for phone and OTP.
    private EditText edtPhone;
    private PinView edtOTP;

 //   AppCompatImageButton profile;
  //  TextView userpoint;
  //  ImageView back;
    // buttons for generating OTP and verifying OTP
    private Button verifyOTPBtn, generateOTPBtn;
    private TextView resend;

    // string for storing our verification ID
    private String verificationId;
  //  private static final String KEY_VERIFICATION_ID = "key_verification_id";

    private PhoneAuthProvider.ForceResendingToken mResendToken;
    // private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
//
//        getSupportActionBar().setTitle("Aazma Luck");
//        ColorDrawable colorDrawable
//                = new ColorDrawable(Color.parseColor("#3f0c46"));
//
//        // Set BackgroundDrawable
//        getSupportActionBar().setBackgroundDrawable(colorDrawable);
//



//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        profile = findViewById(R.id.profile);
//        userpoint = findViewById(R.id.userpoint);
//        back = findViewById(R.id.back);
//        profile.setVisibility(View.GONE);
//        userpoint.setVisibility(View.GONE);
 //       back.setVisibility(View.GONE);

        cxt=this;
        mAuth = FirebaseAuth.getInstance();

        // initializing variables for button and Edittext.
        edtPhone = findViewById(R.id.idEdtPhoneNumber);
        edtOTP = findViewById(R.id.idEdtOtp);
        verifyOTPBtn = findViewById(R.id.idBtnVerify);
        generateOTPBtn = findViewById(R.id.idBtnGetOtp);
        resend = findViewById(R.id.resend);

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+91" + edtPhone.getText().toString().trim();

                resendVerificationCode(phone,mResendToken);
            }
        });
//        // setting onclick listener for generate OTP button.




        generateOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // below line is for checking weather the user
                // has entered his mobile number or not.
                if (TextUtils.isEmpty(edtPhone.getText().toString().trim())) {
                    // when mobile number text field is empty
                    // displaying a toast message.
                    //    Toast.makeText(this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
                    toast("Please enter a valid phone number.");

                } else {
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    String phone = "+91" + edtPhone.getText().toString().trim();
                    sendVerificationCode(phone);
                }
            }
        });

        // initializing on click listener
        // for verify otp button
        verifyOTPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // validating if the OTP text field is empty or not.
                if (TextUtils.isEmpty(edtOTP.getText().toString().trim()) ||TextUtils.isEmpty(edtPhone.getText().toString().trim()) ) {
                    // if the OTP text field is empty display
                    // a message to user to enter OTP
                    //   Toast.makeText(MainActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                    toast("Please enter OTP");
                } else {
                    // if OTP field is not empty calling
                    // method to verify the OTP.
                    verifyCode(edtOTP.getText().toString());
                }
            }
        });

//        if (verificationId == null && savedInstanceState != null) {
//            onRestoreInstanceState(savedInstanceState);
//        }
    }
//    @Override
//    protected void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(KEY_VERIFICATION_ID,verificationId);
//    }

//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        verificationId = savedInstanceState.getString(KEY_VERIFICATION_ID);
//    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            Intent i = new Intent(loginActivity.this, DisplayImagesActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            //    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            toast(task.getException().getMessage());
                        }
                    }
                });
    }


    private void sendVerificationCode(String number) {
        // this method is used for getting
        // OTP on user phone number.

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)            // Phone number to verify
                        .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    // callback method is called on Phone auth provider.
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks

            // initializing our callbacks for on
            // verification callback method.
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        // below method is used when
        // OTP is sent from Firebase
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            // when we receive the OTP it
            // contains a unique id which
            // we are storing in our string
            // which we have already created.
            verificationId = s;
            mResendToken = forceResendingToken;
        }

        // this method is called when user
        // receive OTP from Firebase.
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // below line is used for getting OTP code
            // which is sent in phone auth credentials.
            final String code = phoneAuthCredential.getSmsCode();

            // checking if the code
            // is null or not.
            if (code != null) {
                // if the code is not null then
                // we are setting that code to
                // our OTP edittext field.
                edtOTP.setText(code);

                // after setting this code
                // to OTP edittext field we
                // are calling our verifycode method.
                verifyCode(code);
            }
        }

        // this method is called when firebase doesn't
        // sends our OTP code due to any error or issue.
        @Override
        public void onVerificationFailed(FirebaseException e) {
            // displaying error message with firebase exception.
            //  Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            toast(e.getMessage());
        }
    };

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        // below line is used for getting getting
        // credentials from our verification id and code.
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

            signInWithCredential(credential);
        }catch (Exception e){
            toast("ERROR");
        }
        // after getting credential we are
        // calling sign in method.
    }

    public static void toast(String s){

        Toast.makeText(cxt, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {

        super.onStart();

        RelativeLayout mainprogress=findViewById(R.id.mainprogress);
        FirebaseDatabase.getInstance().getReference("maintanance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //     Log.e(TAG, "App title updated");

                String status = dataSnapshot.child("status").getValue(String.class).trim();
                String update = dataSnapshot.child("update").getValue(String.class).trim();
                String updatetxt = dataSnapshot.child("updatetxt").getValue(String.class).trim();

                int versionCode = BuildConfig.VERSION_CODE;
            //    String versionName = BuildConfig.VERSION_NAME;

                RelativeLayout main=findViewById(R.id.mainlogin);

               // RelativeLayout update=findViewById(R.id.updatelayout);

              //  TextView notify=findViewById(R.id.notify);
               //  toast("vc "+versionCode+" vn "+versionName);

                if(status.equals("active") && update.equals(String.valueOf(versionCode)) ){
                    FirebaseUser user= mAuth.getCurrentUser();
                    if(user!=null){
                      //  mainprogress.setVisibility(View.GONE);
                        Intent i = new Intent(loginActivity.this, DisplayImagesActivity.class);
                    //    i.putExtra("phonenumber", user.getPhoneNumber());
                        startActivity(i);
                        finish();
                    }else {

                        mainprogress.setVisibility(View.GONE);
                       // update.setVisibility(View.GONE);

                        main.setVisibility(View.VISIBLE);
                    }
                }else {

                  //  mainprogress.setVisibility(View.GONE);
                  //  main.setVisibility(View.GONE);
                   // update.setVisibility(View.VISIBLE);
                     String pass="";
                     if(!status.equals("active") && !update.equals(String.valueOf(versionCode))){
                         pass=status;
                     }
                     else if(!status.equals("active")){
                        pass=status;
                    }else if(!update.equals(String.valueOf(versionCode))){
                        pass=updatetxt;
                    }


                    Intent i = new Intent(loginActivity.this, update.class);
                    i.putExtra("key", pass);
                 //   i.putExtra("key2",updatetxt);
                    startActivity(i);
                    finish();


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                finish();
            }
        });


    }
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


}