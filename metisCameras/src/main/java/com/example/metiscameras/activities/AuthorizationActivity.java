package com.example.metiscameras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.metiscameras.R;
import com.example.metiscameras.api.AuthorizationApi;

import org.apache.commons.codec.digest.DigestUtils;

public class AuthorizationActivity extends AppCompatActivity {
    private static final boolean DEBUG = true;    // FIXME set false when production
    private static final String TAG = "!";


    /**
     * подумать над тем, что при вылете из мейна как то туда опять заходить (возможно заного через авторизацию)
     */
    private boolean isAuthorized;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.i(TAG, "onCreate");

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_authorization);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button confirm = (Button) findViewById(R.id.auth_button);
        confirm.setOnClickListener(view -> {
            Intent main = new Intent(this, MainActivity.class);
            startActivity(main);

//            if (DEBUG) Log.i(TAG, "confirm.setOnClickListener");
//            String name = ((EditText) findViewById(R.id.auth_name_input)).getText().toString();
//            String surname = ((EditText) findViewById(R.id.auth_surname_input)).getText().toString();
//            String password = ((EditText) findViewById(R.id.auth_password_input)).getText().toString();
//
//            String login = DigestUtils.md5Hex(surname + name).toUpperCase();
//            AuthorizationApi.login(this, login, password);
        });
    }

    public void onLogin(boolean authorized, String msg){
        if (DEBUG) Log.i(TAG, "onLogin");
        if(!authorized){
            Toast.makeText(this, "Ошибка авторизации. Ответ сервера: " + msg, Toast.LENGTH_SHORT).show();
            // TODO вызов бригадира ?????
            return;
        }

        Intent main = new Intent(this, MainActivity.class);
        startActivity(main);
    }
}