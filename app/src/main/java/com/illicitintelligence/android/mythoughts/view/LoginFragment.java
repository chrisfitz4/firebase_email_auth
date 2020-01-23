package com.illicitintelligence.android.mythoughts.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;

import com.illicitintelligence.android.mythoughts.R;
import com.illicitintelligence.android.mythoughts.model.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginFragment extends Fragment {


    private String TAG = "TAG_X";
    @BindView(R.id.email_address_edittext)
    EditText email_address_singin;
    @BindView(R.id.signup_email_address_edittext)
    EditText email_address_signup;
    @BindView(R.id.password)
    EditText password_signin;
    @BindView(R.id.signup_password)
    EditText password_signup;
    @BindView(R.id.signup_password_verify)
    EditText password_verify_signup;
    @BindView(R.id.innerlayout_constraint)
    ConstraintLayout inner_layout_constraint;

    private boolean signingIn = false;
    private LoginDelegate delegate;

    interface LoginDelegate{
        void signUpNewUser(User user);
        void loginUser(User user);
        void goBack();
    }

    public LoginFragment(LoginDelegate delegate) {
        this.delegate = delegate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this,view);
    }


    private boolean badEmail(String email){
        return email.length()==0||!email.contains("@");
    }

    private boolean badPassword(String password){
        return password.length()==0;
    }

    private boolean comparePasswords(@NonNull String password1, String password2){
        return password1.equals(password2);
    }

    @OnClick(R.id.signup_button)
    public void signUp(View view){
        if(badEmail(email_address_signup.getText().toString().trim())){
            Toast.makeText(this.getContext(),"Input a valid email",Toast.LENGTH_SHORT).show();
        }else if(badPassword(password_signup.getText().toString())){
            Toast.makeText(this.getContext(),"Input a password",Toast.LENGTH_SHORT).show();
        }else if(!comparePasswords(password_signup.getText().toString(),password_verify_signup.getText().toString())){
            Toast.makeText(this.getContext(),"Password does not match the verification",Toast.LENGTH_SHORT).show();
        }else{
            delegate.signUpNewUser(new User(email_address_signup.getText().toString().trim(),
                    password_signup.getText().toString()));
        }
    }

    @OnClick(R.id.login_button)
    public void login(View view){
        if(badEmail(email_address_singin.getText().toString().trim())){
            Toast.makeText(this.getContext(),"Input a valid email",Toast.LENGTH_SHORT).show();
            signingIn = false;
        }else if(badPassword(password_signin.getText().toString())) {
            Toast.makeText(this.getContext(), "Input a password", Toast.LENGTH_SHORT).show();
            signingIn = false;
        }else if(signingIn){
            //do nothing
        }else{
            Log.d(TAG, "login: ");
            delegate.loginUser(new User(email_address_singin.getText().toString().trim(),
                    password_signin.getText().toString()));
            signingIn = true;
        }
    }

    @OnClick(R.id.signup_textview)
    public void showSignUp(View view){
        inner_layout_constraint.setVisibility(View.VISIBLE);
        signingIn = true;
    }

    public void hideLayout(){
        inner_layout_constraint.setVisibility(View.GONE);
    }

    public boolean layoutIsVisible(){
        return inner_layout_constraint.getVisibility()==View.VISIBLE;
    }


}
