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

public class updateDeletePage extends AppCompatActivity {

    Warehouse currentProduct;
    EditText nameProduct, typeProduct, countProduct, priceProduct;
    ImageView productImage;
    String encodedImage;
    ProgressBar loadingPB;
    Button updateButton, deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_delete_cars);
        currentProduct = MainActivity.currentProduct;
        nameProduct = findViewById(R.id.productName);
        typeProduct = findViewById(R.id.productType);
        countProduct = findViewById(R.id.productCount);
        priceProduct = findViewById(R.id.productPrice);
        productImage = findViewById(R.id.photoProduct);
        loadingPB = findViewById(R.id.idLoadingPB);


        nameProduct.setText(currentProduct.Name_Product);
        typeProduct.setText(currentProduct.Type_Product);
        countProduct.setText(String.valueOf(currentProduct.Count_Product));
        priceProduct.setText(String.valueOf(currentProduct.Price_Product));
        productImage.setImageBitmap(getImgBitmap(currentProduct.Photo_Product));
        encodedImage = currentProduct.Photo_Product;

        forUpdateButton();
        forDeleteButton();
        addImage();
    }

    public void forUpdateButton(){
        updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int idProduct = currentProduct.Code_Product;
                putData(idProduct, nameProduct.getText().toString(), typeProduct.getText().toString(), Integer.parseInt(countProduct.getText().toString()), Integer.parseInt(priceProduct.getText().toString()));
            }
        });
    }

    public void forDeleteButton(){
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int idProduct = currentProduct.Code_Product;
                deleteData(idProduct);
            }
        });
    }

    private void deleteData(int forCodeProduct) {

        loadingPB.setVisibility(View.VISIBLE);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ngknn.ru:5001/ngknn/сергеичевад/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WarehouseApiGET retrofitAPI = retrofit.create(WarehouseApiGET.class);

        Call<Warehouse> call = retrofitAPI.createDelete(forCodeProduct);

        call.enqueue(new Callback<Warehouse>() {
            @Override
            public void onResponse(Call<Warehouse> call, Response<Warehouse> response) {
                Toast.makeText(updateDeletePage.this, "Запись удалена!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(updateDeletePage.this, MainActivity.class);
                startActivity(intent);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<Warehouse> call, Throwable t) {
                Toast.makeText(updateDeletePage.this, "ОШИБКА!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void putData(int forCodeProduct, String forNameProduct, String forTypeProduct, int forCountProduct, int forPriceProduct) {

        loadingPB.setVisibility(View.VISIBLE);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://ngknn.ru:5001/ngknn/сергеичевад/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        WarehouseApiGET retrofitAPI = retrofit.create(WarehouseApiGET.class);

        Warehouse warehouse = new Warehouse(forCodeProduct, forNameProduct, forTypeProduct, forCountProduct, forPriceProduct, encodedImage);

        Call<Warehouse> call = retrofitAPI.createPut(forCodeProduct, warehouse);

        call.enqueue(new Callback<Warehouse>() {
            @Override
            public void onResponse(Call<Warehouse> call, Response<Warehouse> response) {
                Toast.makeText(updateDeletePage.this, "Запись изменена", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(updateDeletePage.this, MainActivity.class);
                startActivity(intent);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<Warehouse> call, Throwable t) {
                Toast.makeText(updateDeletePage.this, "ОШИБКА!", Toast.LENGTH_SHORT).show();
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

    private Bitmap getImgBitmap(String encodedImg) {
        if (!encodedImg.equals("null")) {
            byte[] bytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                bytes = Base64.getDecoder().decode(encodedImg);
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return BitmapFactory.decodeResource(updateDeletePage.this.getResources(),
                R.drawable.empty);
    }
}
