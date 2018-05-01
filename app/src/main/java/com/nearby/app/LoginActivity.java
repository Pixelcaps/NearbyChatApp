package com.nearby.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewSwitcher;

/**
 * Created by Tiberiu Visan on 5/1/2018.
 * Project: NearbyChatApp
 */
public class LoginActivity extends Activity {

    ViewSwitcher editTextSwitcher;
    EditText emailEditText;
    EditText passwordEditText;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.LoginActivityTheme);
        setContentView(R.layout.activity_new_login);
        initViews();
    }

    private void initViews() {
        editTextSwitcher = findViewById(R.id.view_switcher);
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        editTextSwitcher.setInAnimation(in);
        editTextSwitcher.setOutAnimation(out);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
    }

    public void continueClicked(View view) {
        if (!emailEditText.getText().toString().isEmpty()) {
            editTextSwitcher.showNext();
        }
    }

}
