package com.abstact.revisisister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.abstact.revisisister.Admin.MainActivity;
import com.abstact.revisisister.Kasir.KasirActivity;

public class LoginActivity extends AppCompatActivity {
    EditText username, password;
    Button btn_login;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        btn_login.setOnClickListener(view -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();

            if (user.equals("admin")){
                if (pass.equals("admin")) {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    password.setError("Please enter valid password!");
                }
            } else if (user.equals("kasir")) {
                if (pass.equals("12345")) {
                    Intent intent = new Intent(this, KasirActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    password.setError("Please enter valid password!");
                }
            }else {
                username.setError("Please enter valid email!");
            }

        });

    }

    private void init() {
        username = findViewById(R.id.etUsername);
        password = findViewById(R.id.etPassword);
        btn_login = findViewById(R.id.btnSignIn);
        tv = findViewById(R.id.tvFormLogin);
    }
}