package be.vives.loic.shopandcook.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import be.vives.loic.shopandcook.R;
import be.vives.loic.shopandcook.models.User;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private SignInButton mSignInButton;

    private GoogleSignInClient mGoogleSignInClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;

    public static User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        EdgeToEdge.apply(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        findViewById(R.id.homeFab).setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), HomeActivity.class)));

        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Firebase persists the signed-in session to disk, so restore it here instead of
        // forcing the user to sign in again every time the app is relaunched.
        if (user == null && mFirebaseAuth.getCurrentUser() != null) {
            user = toUser(mFirebaseAuth.getCurrentUser());
        }

        // Assign fields
        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(this);
        TextView signInBtnText = (TextView) mSignInButton.getChildAt(0);

        // user already connected
        if (user != null) {
            TextView mTextView = (TextView) findViewById(R.id.currentUsername);
            mTextView.setText(user.getName() + "\n" + user.getEmail());
            ImageView mImageView = (ImageView) findViewById((R.id.userPicture));
            mImageView.setImageBitmap(user.getPicture());
            signInBtnText.setText("Log out");
        } else {
            signInBtnText.setText("Log in");
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private static User toUser(FirebaseUser firebaseUser) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String photoUrl = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;
        return new User(firebaseUser.getEmail(), firebaseUser.getDisplayName(), photoUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        int id = item.getItemId();
        if (id == R.id.action_home) {
            i = new Intent(getApplicationContext(), HomeActivity.class);
        } else if (id == R.id.action_signout) {
            i = new Intent(getApplicationContext(), SignInActivity.class);
        }
        startActivity(i);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (user != null) {
            signOut();
        } else {
            signIn();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        mFirebaseAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user = null; // reset user credidentials
                Intent refresh = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(refresh);
                Toast.makeText(getApplicationContext(), "Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed
                Log.e(TAG, "Google Sign In failed.", e);
                Toast.makeText(this, "Authentification failed, please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Log.d(TAG, "firebaseAuthWithGooogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            user = toUser(mFirebaseAuth.getCurrentUser());
                            startActivity(new Intent(SignInActivity.this, HomeActivity.class));
                            finish();
                        }
                    }
                });
    }
}
