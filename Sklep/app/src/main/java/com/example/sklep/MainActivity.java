package com.example.sklep;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int quantity = 0;
    double price;
    HashMap goodsMap;
    String goodsName;
    Spinner spinner;
    ArrayList spinnerArrayList;
    ArrayAdapter spinnerAdapter;
    TextView quantityTextView;
    TextView priceTextView;
    EditText userNameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSpinner();
        createMap();
        quantityTextView = findViewById(R.id.quantityTextView);
        priceTextView = findViewById(R.id.priceTextView);
        userNameEditText = findViewById(R.id.nameEditText);

    }
    void createSpinner()
    {
        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinnerArrayList = new ArrayList();
        spinnerArrayList.add("guitar");
        spinnerArrayList.add("drums");
        spinnerArrayList.add("keyboard");
        spinnerArrayList.add("violin");
        spinnerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerArrayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

    }
    void createMap(){
        goodsMap = new HashMap<>();
        goodsMap.put("guitar", 500.0);
        goodsMap.put("drums", 1500.0);
        goodsMap.put("keyboard", 1000.0);
        goodsMap.put("violin", 2000.0);
    }

    public void increaseQuantity(View view)
    {
        quantity++;
        quantityTextView.setText(""+quantity);
        priceTextView.setText(""+quantity*price);

    }
    public void decreaseQuantity(View view)
    {
        if(quantity>0) {
            quantity--;
            quantityTextView.setText("" + quantity);
            priceTextView.setText("" + quantity * price);
        }
    }
    public void addToCart (View view){
        Order order = new Order();
        order.userName = userNameEditText.getText().toString();
        order.goodName = goodsName;
        order.quantity = quantity;
        order.price = price;
        order.orderPrice = quantity*price;
        Intent orderIntent = new Intent(MainActivity.this, OrderActivity.class);
        orderIntent.putExtra("userNameForIntent", order.userName);
        orderIntent.putExtra("goodsName", order.goodName);
        orderIntent.putExtra("quantity", order.quantity);
        orderIntent.putExtra("price", order.price);
        orderIntent.putExtra("orderPrice", order.orderPrice);
        startActivity(orderIntent);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        goodsName = spinner.getSelectedItem().toString();
        price = (double)goodsMap.get(goodsName);
        priceTextView.setText(""+quantity*price);
        //TODO ADD IMAGES
        switch (goodsName){
            case "guitar":
                break;
            case "drums":
                break;
            case "keyboard":
                break;
            case "violin":
                break;
            default:
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}