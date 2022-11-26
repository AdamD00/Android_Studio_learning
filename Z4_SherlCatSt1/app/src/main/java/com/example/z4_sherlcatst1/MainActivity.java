package com.example.z4_sherlcatst1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void Clear(View view)
    {
        EditText edit =  findViewById(R.id.editTextName);
        edit.setText("");
    }

    public void onClickSend(View view){
        EditText userEditText = findViewById(R.id.editTextName);
        EditText giftEditText = findViewById(R.id.editTextInfo);

        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        intent.putExtra("username",userEditText.getText().toString());
        intent.putExtra("gift",giftEditText.getText().toString());

        startActivity(intent);
    }
}