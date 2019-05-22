package krupski.aleksander.keepassdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import krupski.aleksander.keepassdroid.data.Password;
import krupski.aleksander.keepassdroid.data.User;
import krupski.aleksander.keepassdroid.utils.AES;

public class MainActivity extends AppCompatActivity {

    private Button btnAddNewPassword,btnShowAllPasswords,
            addPassword, signOut;

    private EditText  newEntry, newLogin, newPassword;
    private ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private List<Password> allPasswords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //final List<Password> allPasswords = new ArrayList<>();
        database.getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = getIntent().getStringExtra(LoginActivity.USERNAME);
                int index = name.indexOf('.');
                Map map = new HashMap<String, Password>();
                map = (HashMap<String, Password>)dataSnapshot.child("users").child(name.substring(0, index)).child("passwords").getValue();
                allPasswords = new ArrayList<Password>(map.values());
                System.out.println(allPasswords.toString());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final Intent intent = getIntent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };

        btnAddNewPassword = (Button) findViewById(R.id.changePassword);
        btnShowAllPasswords = (Button) findViewById(R.id.showAllPasswords);
        addPassword = (Button) findViewById(R.id.addNewPassword);
        signOut = (Button) findViewById(R.id.sign_out);

        newEntry = (EditText) findViewById(R.id.new_entry);
        newLogin = (EditText) findViewById(R.id.new_login);
        newPassword = (EditText) findViewById(R.id.new_password);


        newEntry.setVisibility(View.GONE);
        newLogin.setVisibility(View.GONE);
        newPassword.setVisibility(View.GONE);
        btnAddNewPassword.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

        addPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEntry.setVisibility(View.VISIBLE);
                newLogin.setVisibility(View.VISIBLE);
                newPassword.setVisibility(View.VISIBLE);
                btnAddNewPassword.setVisibility(View.VISIBLE);
            }
        });

        btnAddNewPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if (user != null && !newEntry.getText().toString().trim().equals("") && !newLogin.getText().toString().trim().equals("") && !newPassword.getText().toString().trim().equals("")) {
                    User user = new User();
                    user.setUsername(intent.getStringExtra(LoginActivity.USERNAME));
                    Password password = new Password(newEntry.getText().toString().trim(), newLogin.getText().toString().trim(), AES.encrypt(newPassword.getText().toString().trim(), (intent.getStringExtra(LoginActivity.PASSWORD))));
                    List<Password> passwords = new ArrayList<>();
                    passwords.add(password);
                    user.setPasswords(passwords);
                    DatabaseReference myref = database.getReference("users");
                    String name = intent.getStringExtra(LoginActivity.USERNAME);
                    int index = name.indexOf('.');
                    myref.child(name.substring(0, index)).child("passwords").push().setValue(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Password has been added!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to add password!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });

                } else if (newEntry.getText().toString().trim().equals("")) {
                    newEntry.setError("Enter new entry");
                    progressBar.setVisibility(View.GONE);
                }
                else if (newLogin.getText().toString().trim().equals("")) {
                    newEntry.setError("Enter new login");
                    progressBar.setVisibility(View.GONE);
                }
                else if (newPassword.getText().toString().trim().equals("")) {
                    newEntry.setError("Enter new password");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


        btnShowAllPasswords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newEntry.setVisibility(View.VISIBLE);
                newLogin.setVisibility(View.VISIBLE);
                newPassword.setVisibility(View.VISIBLE);
                btnAddNewPassword.setVisibility(View.VISIBLE);
                String name = intent.getStringExtra(LoginActivity.USERNAME);
                int index = name.indexOf('.');
                DatabaseReference newRef = database.getReference("users");
                String aaa = newRef.child(name.substring(0, index)).child("passwords").toString();
          //      DataSnapshot datasnapshot = database.getReference().
  //

            }
        });
//
//        changePassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                if (user != null && !newPassword.getText().toString().trim().equals("")) {
//                    if (newPassword.getText().toString().trim().length() < 6) {
//                        newPassword.setError("Password too short, enter minimum 6 characters");
//                        progressBar.setVisibility(View.GONE);
//                    } else {
//                        user.updatePassword(newPassword.getText().toString().trim())
//                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            Toast.makeText(MainActivity.this, "Password is updated, sign in with new password!", Toast.LENGTH_SHORT).show();
//                                            signOut();
//                                            progressBar.setVisibility(View.GONE);
//                                        } else {
//                                            Toast.makeText(MainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
//                                            progressBar.setVisibility(View.GONE);
//                                        }
//                                    }
//                                });
//                    }
//                } else if (newPassword.getText().toString().trim().equals("")) {
//                    newPassword.setError("Enter password");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        btnSendResetEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                oldEmail.setVisibility(View.VISIBLE);
//                newEmail.setVisibility(View.GONE);
//                password.setVisibility(View.GONE);
//                newPassword.setVisibility(View.GONE);
//                changeEmail.setVisibility(View.GONE);
//                changePassword.setVisibility(View.GONE);
//                sendEmail.setVisibility(View.VISIBLE);
//                remove.setVisibility(View.GONE);
//            }
//        });
//
//        sendEmail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                if (!oldEmail.getText().toString().trim().equals("")) {
//                    auth.sendPasswordResetEmail(oldEmail.getText().toString().trim())
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(MainActivity.this, "Reset password email is sent!", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    } else {
//                                        Toast.makeText(MainActivity.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                } else {
//                    oldEmail.setError("Enter email");
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        btnRemoveUser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                progressBar.setVisibility(View.VISIBLE);
//                if (user != null) {
//                    user.delete()
//                            .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
//                                        startActivity(new Intent(MainActivity.this, SignupActivity.class));
//                                        finish();
//                                        progressBar.setVisibility(View.GONE);
//                                    } else {
//                                        Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
//                                    }
//                                }
//                            });
//                }
//            }
//        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

    }

    //sign out method
    public void signOut() {
        auth.signOut();
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }
}