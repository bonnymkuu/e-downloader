package com.example.myvideo;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PasteLinkActivity extends AppCompatActivity {

    private EditText editTextLink;
    private Button buttonUpload;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste_link);

        editTextLink = findViewById(R.id.editTextLink);
        progressBar = findViewById(R.id.progressBar);
        buttonUpload = findViewById(R.id.buttonUpload);



    }

}
