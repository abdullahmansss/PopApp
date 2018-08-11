package mans.abdullah.abdullah_mansour.popup;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateAccount extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText mail,pass,name;
    private Button sign_in,create_account;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mail = (EditText) findViewById(R.id.field_email);
        pass = (EditText) findViewById(R.id.field_password);
        name = (EditText) findViewById(R.id.field_name);

        bar = (ProgressBar) findViewById(R.id.bar);
        bar.setVisibility(View.INVISIBLE);

        sign_in = (Button) findViewById(R.id.email_sign_in_button);
        create_account = (Button) findViewById(R.id.email_create_account_button);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createaccount();
            }
        });

        mAuth = FirebaseAuth.getInstance();

    }

    public void updateUI ()
    {
        Intent n = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(n);
    }

    public void createaccount ()
    {
        String email = mail.getText().toString();
        String password = pass.getText().toString();
        final String username = name.getText().toString();

        if (email.length() > 0 && password.length() > 0 && username.length() > 0)
        {

        bar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                //Log.d(TAG, "User profile updated.");
                                            }
                                        }
                                    });

                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(CreateAccount.this, "This Email is Already Exists",
                                    Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        } else
        {
            Toast.makeText(getApplicationContext(), "Please Enter Valid Data", Toast.LENGTH_SHORT).show();
        }
    }
}
