package com.yks.collegemedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class profileActivity extends AppCompatActivity {
    GridView coursesGV;
    ArrayList<ImageUploadInfo> dataModalArrayList;

    DatabaseReference databaseReference,profiledatabaseReference;
    TextView profilename2,likes,posts,branchtxt,yeartxt;
    EditText profilename,description;
    Spinner branch,year;
    String prevdes="";
    String prevname="";
    int prevbranch=0;
    int prevyear=0;
    AppCompatButton editprofile,logout;
    private  String userphonenumber;
    private String verifieds="null";
    ImageView verified2;
    RelativeLayout mainprofile;
    ProgressBar progressBar;
    Class aClass;

    String[] yearlist ={"First","Second","Third","Fourth"};
    String[] branchlist={"Computer Science","Information Technology","Electronics","Electrical","Mechanical"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser user= mAuth.getCurrentUser();

        editprofile = findViewById(R.id.editprofile);

        logout = findViewById(R.id.logout);
        verified2 = findViewById(R.id.verified2);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("key");

            String value2 = extras.getString("key2");

             if(value.equals("mainprofile")){
                 userphonenumber=user.getPhoneNumber();
                 editprofile.setVisibility(View.VISIBLE);
                 logout.setVisibility(View.VISIBLE);


             }else if(value.equals(user.getPhoneNumber())){
                 userphonenumber=user.getPhoneNumber();
                 editprofile.setVisibility(View.VISIBLE);
                 logout.setVisibility(View.VISIBLE);

             }else {
                 userphonenumber=value;
                 editprofile.setVisibility(View.GONE);
                 logout.setVisibility(View.GONE);

             }

             if(value2.equals("display")){
                 aClass=DisplayImagesActivity.class;
             }else if(value2.equals("search")){
                 aClass=searchActivity.class;
             }

            //The key argument here must match that used in the other activity
        }else {

            toast("ERROR");
        }

        coursesGV = findViewById(R.id.idGVCourses);

        profilename = findViewById(R.id.profilename);
        profilename2 = findViewById(R.id.profilename2);


        mainprofile = findViewById(R.id.mainprofile);
        progressBar = findViewById(R.id.progressbar);

          likes = findViewById(R.id.likes);
        posts = findViewById(R.id.posts);
        description = findViewById(R.id.description);
        branch = findViewById(R.id.branch);
        branchtxt = findViewById(R.id.branchtxt);
        year = findViewById(R.id.year);
        yeartxt = findViewById(R.id.yeartxt);
        logout = findViewById(R.id.logout);

        editdisabled(branch);
        editdisabled(year);



        year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                // TODO Auto-generated method stub
               String spin_val = yearlist[position];//saving the value selected
            //    toast(spin_val);
            //    yeartxt.setText(spin_val);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        ArrayAdapter<String> spin_adapter = new ArrayAdapter<String>(profileActivity.this, android.R.layout.simple_spinner_item,
                yearlist);
        year.setAdapter(spin_adapter);

        branch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int position, long id) {
                // TODO Auto-generated method stub
                String spin_val = branchlist[position];//saving the value selected
            //     branchtxt.setText(spin_val);
             //   toast(spin_val);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }

        });
        ArrayAdapter<String> spin_adapter2 = new ArrayAdapter<String>(profileActivity.this, android.R.layout.simple_spinner_item,
                branchlist);
        branch.setAdapter(spin_adapter2);





        dataModalArrayList = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference(MainActivity.Database_Path);
        profiledatabaseReference = FirebaseDatabase.getInstance().getReference("users").child(userphonenumber);
        loadprofile();
        loadDatainGridView();


        final int[] lastSpecialRequestsCursorPosition = {0};
        final String[] specialRequests = {""};
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                lastSpecialRequestsCursorPosition[0] = description.getSelectionStart();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                description.removeTextChangedListener(this);

                if (description.getLineCount() > 5) {
                    description.setText(specialRequests[0]);
                    description.setSelection(lastSpecialRequestsCursorPosition[0]);
                }
                else
                    specialRequests[0] = description.getText().toString();

                description.addTextChangedListener(this);
            }
        });

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editprofile.getText().toString().equals("Edit Profile")){

                    editenabled(description);

                    editenabled(profilename);

                    profilename.requestFocus();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(profilename, InputMethodManager.SHOW_IMPLICIT);

                    editenabled(branch);
                    editenabled(year);

                    editprofile.setText("Save");
                }else {

                    if(!prevdes.equals(description.getText().toString().trim()) || !prevname.equals(profilename.getText().toString().trim())
                            || prevbranch != branch.getSelectedItemPosition() || prevyear != year.getSelectedItemPosition()
                    ){

                        String pname=profilename.getText().toString().trim();
                        if(!pname.isEmpty()){

                            Pattern ps = Pattern.compile("^[a-z_]+$");
                            Matcher ms = ps.matcher(pname);
                            boolean bs = ms.matches();
                            if (bs) {
                                editprofile();

                                editdisabled(description);

                                editdisabled(profilename);
                                editdisabled(branch);
                                editdisabled(year);

                                editprofile.setText("Edit Profile");
                            }else {
                                toast("Please enter a valid username contains only a-z and _ ");

                            }


                        }else {
                            toast("Please enter username");

                        }
                    }else {

                        editdisabled(description);

                        editdisabled(profilename);
                        editdisabled(branch);
                        editdisabled(year);

                        editprofile.setText("Edit Profile");
                    }

                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(profileActivity.this, loginActivity.class);
                startActivity(i);
                finish();

            }
        });

    }

    private void  editenabled(View et){
        et.setEnabled(true);
        et.setActivated(true);

  }
    private void  editdisabled(View et){
        et.setEnabled(false);
        et.setActivated(false);
    }



    private void loadDatainGridView() {

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                dataModalArrayList.clear();
                   int i=0;
                   int likescount=0;
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    if (postSnapshot.child("phonenumber").getValue().equals(userphonenumber)) {
                //    toast("value "+postSnapshot.getValue());

                        ImageUploadInfo userpics = postSnapshot.getValue(ImageUploadInfo.class);

                        dataModalArrayList.add(userpics);
                 //   toast("value "+postSnapshot.child("phonenumber").getValue());

                    //      toast("l "+userpics.getlikes());
                        likescount = likescount + Integer.parseInt(userpics.getlikes());
                        i++;
                    }


                }


                posts.setText(""+i);
                likes.setText(""+likescount);


                CoursesGVAdapter adapter = new CoursesGVAdapter(profileActivity.this, dataModalArrayList);

                coursesGV.setAdapter(adapter);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                   Toast.makeText(profileActivity.this, "failed to load images", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private  void loadprofile(){
        profiledatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                    DataModal user = snapshot.getValue(DataModal.class);
                    if(user==null){
                        editprofile();
                      //  toast("null");
                    }else
                {
                    profilename.setText(user.getName());
                    profilename2.setText(user.getName().substring(0, 1).toUpperCase());
                    description.setText(user.getDescription());


                    verifieds=user.getverified();
                     if(verifieds.equals("null")){
                         verified2.setVisibility(View.GONE);
                     }else {
                         verified2.setVisibility(View.VISIBLE);
                     }

                    branch.setSelection(Integer.parseInt(user.getBranch()));
                    year.setSelection(Integer.parseInt(user.getYear()));

                    prevdes = user.getDescription();
                    prevname = user.getName();
                    prevbranch= Integer.parseInt(user.getBranch());
                    prevyear= Integer.parseInt(user.getYear());

                  //  branchtxt.setText(branch.getSelectedItem().toString());
                  //  yeartxt.setText(year.getSelectedItem().toString());

                    progressBar.setVisibility(View.GONE);
                    mainprofile.setVisibility(View.VISIBLE);

                  //  toast(prevdes);
                }
                //    toast(prevname);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(profileActivity.this, "failed to load profile", Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void editprofile(){

        String username=profilename.getText().toString().trim();
        String descriptionvalue=description.getText().toString().trim();
        String branchvalue= String.valueOf(branch.getSelectedItemPosition());
        String yearvalue= String.valueOf(year.getSelectedItemPosition());

        if(username.isEmpty()){
            username=userphonenumber;
        }

        DataModal userdata = new DataModal(username,descriptionvalue,userphonenumber,branchvalue,yearvalue,verifieds );

        profiledatabaseReference.setValue(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                toast("Profile Updated successfully");


            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(profileActivity.this, aClass);
        startActivity(i);
        finish();

    }




    void toast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


}