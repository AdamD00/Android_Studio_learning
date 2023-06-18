package com.example.sklep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class OrderActivity extends AppCompatActivity {

    String[] addresses = {"mrokon00@gmail.com"};
    String subject = "Order from Music Shop";
    String emailText;

    String userName, goodsName;
    int quantity;
    double price, orderPrice;
    TextView orderTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        setTitle("Your Order");

        Intent receivedOrderIntent = getIntent();
        userName = receivedOrderIntent.getStringExtra("userNameForIntent");
        goodsName = receivedOrderIntent.getStringExtra("goodsName");
        quantity = receivedOrderIntent.getIntExtra("quantity", 0);
        price = receivedOrderIntent.getDoubleExtra("price",0.0);
        orderPrice = receivedOrderIntent.getDoubleExtra("orderPrice",0.0);
        orderTextView = findViewById(R.id.orderTextView);
        orderTextView.setText(userName+"\n"
                +goodsName+"\n"
                +quantity+"\n"
                +orderPrice);
        emailText= (String) orderTextView.getText();
    }
    public void submitOrder(View view)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL,addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT,emailText);
        if(intent.resolveActivity(getPackageManager())!=null){
            startActivity(intent);
        }
    }
}