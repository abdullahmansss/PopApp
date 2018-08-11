package mans.abdullah.abdullah_mansour.popup;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.ui.phone.PhoneActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EmailPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText mail,pass;
    private Button sign_in,create_account,phone;
    private ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        mail = (EditText) findViewById(R.id.field_email);
        pass = (EditText) findViewById(R.id.field_password);


        bar = (ProgressBar) findViewById(R.id.bar);
        bar.setVisibility(View.INVISIBLE);

        sign_in = (Button) findViewById(R.id.email_sign_in_button);
        create_account = (Button) findViewById(R.id.email_create_account_button);

        create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent n = new Intent(getApplicationContext(), CreateAccount.class);
                startActivity(n);
            }
        });
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            updateUI();
        }
    }

    public void updateUI ()
    {
        Intent n = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(n);
    }

    public void signin ()
    {
        String email = mail.getText().toString();
        String password = pass.getText().toString();

        if (email.length() > 0 && password.length() > 0)
        {
        bar.setVisibility(View.VISIBLE);


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            //Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(EmailPasswordActivity.this, "Wrong Email or Password",
                                    Toast.LENGTH_SHORT).show();
                            bar.setVisibility(View.INVISIBLE);
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    } else
        {
            Toast.makeText(getApplicationContext(), "Please Enter Valid Email and Password", Toast.LENGTH_SHORT).show();
        }

    }
}
