package com.example.warehouseapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Base64;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class addProduct extends AppCompatActivity {

    private EditText nameProduct, typeProduct, countProduct, priceProduct;
    ImageView productImage;
    String encodedImage;
    ProgressBar loadingPB;
    Button addButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_page);

        nameProduct = findViewById(R.id.productName);
        typeProduct = findViewById(R.id.productType);
        countProduct = findViewById(R.id.productCount);
        priceProduct = findViewById(R.id.productPrice);
        productImage = findViewById(R.id.productImage);
        productImage.setImageResource(R.drawable.empty);
        loadingPB = findViewById(R.id.idLoadingPB);
        forAddButton();
        forBackButton();
        addImage();
    }

    //Метод на кнопку добавить
    public void forAddButton(){
        addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameProduct.getText().toString().isEmpty() || typeProduct.getText().toString().isEmpty() || countProduct.getText().toString().isEmpty() || priceProduct.getText().toString().isEmpty()) {
                    Toast.makeText(addProduct.this, "Please enter all the values", Toast.LENGTH_SHORT).show();
                }
                else postData(nameProduct.getText().toString(), typeProduct.getText().toString(), Integer.valueOf(countProduct.getText().toString()), Integer.valueOf(priceProduct.getText().toString()));

            }
        });
    }

    //Метод на кнопку добавить
    public void forBackButton(){
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addProduct.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void postData(String forNameProduct, String forTypeProduct, int forCountProduct, int forPriceProduct) {

        // below line is for displaying our progress bar.
        loadingPB.setVisibility(View.VISIBLE);
        addButton.setEnabled(false);
        backButton.setEnabled(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ngknn.ru:5001/ngknn/сергеичевад/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WarehouseApiGET retrofitAPI = retrofit.create(WarehouseApiGET.class);

        Warehouse warehouse = new Warehouse(forNameProduct, forTypeProduct, forCountProduct, forPriceProduct, encodedImage);

        Call<Warehouse> call = retrofitAPI.createPost(warehouse);

        call.enqueue(new Callback<Warehouse>() {
            @Override
            public void onResponse(Call<Warehouse> call, Response<Warehouse> response) {
                Toast.makeText(addProduct.this, "Запись добавлена", Toast.LENGTH_SHORT).show();
                addButton.setEnabled(true);
                backButton.setEnabled(true);
                loadingPB.setVisibility(View.GONE);

                // on below line we are setting empty text
                // to our both edit text.
                productImage.setImageResource(R.drawable.empty);
                nameProduct.setText("");
                typeProduct.setText("");
                countProduct.setText("");
                priceProduct.setText("");

            }

            @Override
            public void onFailure(Call<Warehouse> call, Throwable t) {
                Toast.makeText(addProduct.this, "ОШИБКА!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Метод для события при нажатии на изображение, чтобы его изменить
    public void addImage(){

            productImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImg.launch(intent);
        });
    }

    private final ActivityResultLauncher<Intent> pickImg = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                Uri uri = result.getData().getData();
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    productImage.setImageBitmap(bitmap);
                    encodedImage = encodeImage(bitmap);
                } catch (Exception e) {

                }
            }
        }
    });

    private String encodeImage(Bitmap bitmap) {
        int prevW = 150;
        int prevH = bitmap.getHeight() * prevW / bitmap.getWidth();
        Bitmap b = Bitmap.createScaledBitmap(bitmap, prevW, prevH, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(bytes);
        }
        return null;
    }
}
