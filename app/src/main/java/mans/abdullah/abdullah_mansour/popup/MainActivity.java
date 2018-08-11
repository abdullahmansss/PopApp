package mans.abdullah.abdullah_mansour.popup;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageView mPhotoPickerButton;
    private EditText mMessageEditText;
    private ImageView mSendButton;
    private LinearLayout main;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagedatabaseReference;
    private ChildEventListener mchildEventListener;

    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener mstateListener;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public static final int RC_SIGN_IN = 1;
    private static final int RC_PHOTO_PICKER = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mfirebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        mMessagedatabaseReference = mFirebaseDatabase.getReference().child("message");
        storageReference = firebaseStorage.getReference().child("chat_photos");

        // Initialize references to views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageView) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (ImageView) findViewById(R.id.sendButton);
        main = (LinearLayout) findViewById(R.id.mail_linear);

        // Initialize message ListView and its adapter
        List<ChatMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
            }
        });

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                if (mMessageEditText.getText().toString().length() != 0)
                {
                    mSendButton.setPressed(true);
                    FirebaseUser user = mfirebaseAuth.getCurrentUser();
                String username = user.getDisplayName();

                ChatMessage chatMessage = new ChatMessage(mMessageEditText.getText().toString(), username, null);
                mMessagedatabaseReference.push().setValue(chatMessage);

                // Clear input box
                mMessageEditText.setText("");
            } else
                {
                    mSendButton.setPressed(false);
                }
            }
        });

        attachreadlistner ();

        /*mstateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null)
                {
                    //user is signed in
                    onSignedin (user.getDisplayName());
                } else
                    {
                        //user is signed out
                        onSignedout();

                        List<AuthUI.IdpConfig> providers = Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build(),
                                new AuthUI.IdpConfig.PhoneBuilder().build());

                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setLogo(R.drawable.ic_launcher)      // Set logo drawable
                                        .setAvailableProviders(providers)
                                        .build(),
                                RC_SIGN_IN);
                    }
            }
        };*/
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            if (resultCode == RESULT_OK)
            {

            } else if (resultCode == RESULT_CANCELED)
            {
                finish();
            } else if (requestCode == RC_PHOTO_PICKER && resultCode == RC_SIGN_IN)
            {
                Uri selectedImage = data.getData();

                StorageReference photo = storageReference.child(selectedImage.getLastPathSegment());

                photo.putFile(selectedImage).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                    {
                        Uri download = taskSnapshot.getUploadSessionUri();
                        ChatMessage chatMessage2 = new ChatMessage(null, mUsername, download.toString());
                        mMessagedatabaseReference.push().setValue(chatMessage2);
                    }
                });
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int ID = item.getItemId();

        switch (ID)
        {
            case R.id.sign_out_menu :
                mfirebaseAuth.signOut();
                Intent n = new Intent(getApplicationContext(), EmailPasswordActivity.class);
                startActivity(n);
                break;
            default:
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mfirebaseAuth.addAuthStateListener(mstateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*mfirebaseAuth.removeAuthStateListener(mstateListener);

        deattachreadlistner ();
        mMessageAdapter.clear();*/
    }

    public  void onSignedin (String user)
    {
        mUsername = user;
        attachreadlistner();
    }

    public void onSignedout ()
    {
        mUsername = ANONYMOUS;
        mMessageAdapter.clear();
        deattachreadlistner ();
    }

    public void attachreadlistner ()
    {
            if (mchildEventListener == null)
            {
            mchildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    mMessageAdapter.add(chatMessage);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };
            mMessagedatabaseReference.addChildEventListener(mchildEventListener);
        }
    }

    public void deattachreadlistner ()
    {
        if (mchildEventListener != null)
        {
            mMessagedatabaseReference.removeEventListener(mchildEventListener);
            mchildEventListener = null;
        }
    }

    @Override
    public void onBackPressed() {

    }
}
