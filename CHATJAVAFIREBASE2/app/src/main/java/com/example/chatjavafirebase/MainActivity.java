package com.example.chatjavafirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mfirebaseAuth;
    private FirebaseAuth.AuthStateListener mautListener;
    public static final int SIGN_IN=1;

    List<AuthUI.IdpConfig> providers= Arrays.asList(
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mfirebaseAuth=FirebaseAuth.getInstance();
        mautListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user!=null){
                    vamosahome();
                    Toast.makeText(MainActivity.this, "Login Con Exito :D !!", Toast.LENGTH_SHORT).show();
                }else{
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .setIsSmartLockEnabled(false)
                                    .build(),SIGN_IN
                    );
                }
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        mfirebaseAuth.addAuthStateListener(mautListener);
        onPause();
        mfirebaseAuth.removeAuthStateListener(mautListener);
    }

    private void vamosahome() {
        Intent i=new Intent(this,homeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
