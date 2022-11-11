package com.example.warehouseapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WarehouseAdapter warehouseAdapter;
    private final ArrayList<Warehouse> listWarehouse = new ArrayList<>();
    int currentPriceSort = 0;
    EditText searchDataChangeName;
    ListView warehouseProducts;
    String urlAddress = "https://ngknn.ru:5001/ngknn/сергеичевад/api/warehouses";
    public static Warehouse currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        warehouseProducts = findViewById(R.id.listProduct);
        warehouseAdapter = new WarehouseAdapter(MainActivity.this, listWarehouse);
        warehouseProducts.setAdapter(warehouseAdapter);
        forTextChanged();
        forClearButton(sortingDataByPriceOrCount());
        forAddButton();
        forSpinnerCountPriceChanged(sortingDataByPriceOrCount());
        getCurrentItem();
    }

    //Метод на изменение значения в спиннере цены и количества
    public void forSpinnerCountPriceChanged(Spinner forSortByCountOrPrice){
        forSortByCountOrPrice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                currentPriceSort = parentView.getSelectedItemPosition();
                urlAddress = String.format("https://ngknn.ru:5001/ngknn/сергеичевад/api/warehouses/sortByCountOrPrice?typeOfSort=%d&nameProduct=%s", currentPriceSort, searchDataChangeName.getText().toString());
                new GetProducts().execute();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    //Метод на создание спиннера для сортировки по цене и количеству
    public Spinner sortingDataByPriceOrCount(){
        String[] arraySpinnerPrice = new String[] {
                "Нет", "По возрастанию цены", "По убыванию цены", "По возрастанию количества", "По убыванию количества"
        };
        Spinner spinnerSortByPrice = (Spinner) findViewById(R.id.sortingCountOrColor);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinnerPrice);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSortByPrice.setAdapter(adapter);
        return spinnerSortByPrice;
    }

    //Метод на изменение текста в TextView поиска записей по названию товара
    public void forTextChanged()
    {
        searchDataChangeName = findViewById(R.id.search);
        searchDataChangeName.addTextChangedListener(new TextWatcher(){
            @SuppressLint("DefaultLocale")
            @Override
            public void afterTextChanged(Editable s) {
                urlAddress = String.format("https://ngknn.ru:5001/ngknn/сергеичевад/api/warehouses/sortByCountOrPrice?typeOfSort=%d&nameProduct=%s", currentPriceSort, searchDataChangeName.getText().toString());
                new GetProducts().execute();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }

    //Метод на кнопку очистки
    public void forClearButton(Spinner forSortByPriceOrCount){
        Button clearButton = findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                searchDataChangeName.setText("");
                forSortByPriceOrCount.setSelection(0);
            }
        });
    }

    //Метод на кнопку добавить
    public void forAddButton(){
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, addProduct.class);
                startActivity(intent);
            }
        });
    }

    public void getCurrentItem()
    {
        warehouseProducts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                currentProduct = (Warehouse) (warehouseProducts.getItemAtPosition(position));
                Intent intent = new Intent(MainActivity.this, updateDeletePage.class);
                startActivity(intent);
            }
        });
    }

    private class GetProducts extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids)
        {
            try{
                URL url = new URL(urlAddress);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line = "";

                while((line = reader.readLine()) != null){
                    result.append(line);
                }
                return result.toString();
            }
            catch (Exception exception)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);
            try{
                JSONArray tempArray = new JSONArray(s);
                listWarehouse.clear();
                for (int i = 0; i < tempArray.length();i++)
                {
                    JSONObject productJson = tempArray.getJSONObject(i);
                    Warehouse tempProduct = new Warehouse(
                            productJson.getInt("productId"),
                            productJson.getString("productName"),
                            productJson.getString("productType"),
                            productJson.getInt("productCount"),
                            productJson.getInt("productPrice"),
                            productJson.getString("productPhoto")
                    );
                    listWarehouse.add(tempProduct);
                    warehouseAdapter.notifyDataSetChanged();
                }
            }
            catch (Exception exception){

            }
        }
    }

}