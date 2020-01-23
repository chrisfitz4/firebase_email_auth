package com.illicitintelligence.android.mythoughts.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.illicitintelligence.android.mythoughts.R;
import com.illicitintelligence.android.mythoughts.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginDelegate {

    @BindView(R.id.main_frame)
    FrameLayout frameLayout;

    LoginFragment loginFragment;
    HomeFragment homeFragment;

    private final String TAG = "TAG_X";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        firebaseAuth = FirebaseAuth.getInstance();
        checkIfUserIsLoggedIn();
    }

    private void checkIfUserIsLoggedIn(){
        firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser==null) {
            Log.d(TAG, "checkIfUserIsLoggedIn: ");
            loginFragment = new LoginFragment(this);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frame, loginFragment)
                    .addToBackStack(loginFragment.getTag())
                    .commit();
        }else{
            startHomeFragment();
        }
    }

    private void startHomeFragment() {
        homeFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_frame, homeFragment)
                .addToBackStack(homeFragment.getTag())
                .commit();
        Log.d(TAG, "checkIfUserIsLoggedIn: user is logged in");
    }


    @Override
    public void signUpNewUser(User user) {
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(),
                user.getPassword())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        removeFragment();
                        startHomeFragment();
                    }else{
                        Toast.makeText(this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void loginUser(User user) {
        Log.d(TAG, "loginUser: "+user.toString());
        firebaseAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword())
                .addOnCompleteListener(
                        task->{
                            if(task.isSuccessful()){
                                Toast.makeText(this,"Login complete",Toast.LENGTH_SHORT).show();
                                removeFragment();
                                startHomeFragment();
                            }else{
                                Toast.makeText(this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "loginUser failed: "+task.getException().getMessage());
                            }
                        }
                );
    }

    private void removeFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(loginFragment)
                .commit();
    }

    @Override
    public void goBack() {
        loginFragment.hideLayout();
    }


    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()!=0&&loginFragment.layoutIsVisible()){
            goBack();
        }else{
            super.onBackPressed();
        }
    }
}
