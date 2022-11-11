package com.example.warehouseapp;

import android.graphics.Bitmap;

public class Warehouse {
    int Code_Product;
    String Name_Product;
    String Type_Product;
    int Count_Product;
    int Price_Product;
    String Photo_Product;

    //Для считывания
    Warehouse(int idProduct, String nameProduct, String typeProduct, int countProduct, int priceProduct, String photoProduct){
        Code_Product = idProduct;
        Name_Product = nameProduct;
        Type_Product = typeProduct;
        Count_Product = countProduct;
        Price_Product = priceProduct;
        Photo_Product = photoProduct;
    }

    //Для добавления
    Warehouse(String nameProduct, String typeProduct, int countProduct, int priceProduct, String photoProduct){
        Name_Product = nameProduct;
        Type_Product = typeProduct;
        Count_Product = countProduct;
        Price_Product = priceProduct;
        Photo_Product = photoProduct;
    }
}

