package com.example.obrazek_z_tekstem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
private TextView aHelloTextView;
private EditText bNameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aHelloTextView = (TextView) findViewById(R.id.txtView);
        bNameEditText = (EditText) findViewById(R.id.editText);
    }

    public void onClick (View view){
        if(bNameEditText.getText().length()==0){
            aHelloTextView.setText("Hello Spider-Man!");
        }else{
            aHelloTextView.setText("Hi "+bNameEditText.getText()+"!");
        }
    }
}