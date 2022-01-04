package com.example.android.mini_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText username, email, pass, cpass;
    Button register;
    String userID;
    String fname, emailid, password, confpassword;
    FirebaseAuth FAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.Username);
        email = (EditText) findViewById(R.id.inputEmail);
        pass = (EditText) findViewById(R.id.inputPassword);
        cpass = (EditText) findViewById(R.id.ConformPassword);

        register = (Button) findViewById(R.id.btnregister);

        TextView btn = findViewById(R.id.alreadyhaveAccount);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        FAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fname = username.getText().toString().trim();
                emailid = email.getText().toString().trim();
                password = pass.getText().toString().trim();
                confpassword = cpass.getText().toString().trim();
                if (isValid()) {
                    final ProgressDialog mDialog = new ProgressDialog(RegisterActivity.this);
                    mDialog.setCancelable(false);
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.setMessage("Registration in progress please wait......");
                    mDialog.show();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FAuth.createUserWithEmailAndPassword(emailid, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "User Creted", Toast.LENGTH_SHORT).show();
                                userID = FAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = db.collection("users").document(userID);
                                HashMap<String, String> hashMap1 = new HashMap<>();
                                hashMap1.put("First Name", fname);
                                hashMap1.put("EmailId", emailid);
                                hashMap1.put("Password", password);
                                hashMap1.put("Confirm Password", confpassword);
                                documentReference.set(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
                                    }
                                });
                            } else {
                                Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }

                        }
                    });
                }
//
            }
        });

    }

    String emailpattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    public boolean isValid(){

        boolean isValid=false,isValidname=false,isValidemail=false,isValidpassword=false,isValidconfpassword=false;
        if(TextUtils.isEmpty(fname)){
            username.setError("Enter First Name");
        }else{
            isValidname = true;
        }
        if(TextUtils.isEmpty(emailid)){
            email.setError("Email Is Required");
        }else{
            if(emailid.matches(emailpattern)){
                isValidemail = true;
            }else{
                email.setError("Enter a Valid Email Id");
            }
        }
        if(TextUtils.isEmpty(password)){
            pass.setError("Enter Password");
        }else{
            if(password.length()<8){
                pass.setError("Password is Weak");
            }else{
                isValidpassword = true;
            }
        }
        if(TextUtils.isEmpty(confpassword)){
            cpass.setError("Enter Password Again");
        }else{
            if(!password.equals(confpassword)){
                cpass.setError("Password Dosen't Match");
            }else{
                isValidconfpassword = true;
            }
        }

        isValid = (isValidconfpassword && isValidpassword && isValidemail && isValidname) ? true : false;
        return isValid;


    }
}


