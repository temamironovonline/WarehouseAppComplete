package com.example.warehouseapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.VectorEnabledTintResources;

import java.util.ArrayList;
import java.util.Base64;

public class WarehouseAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Warehouse> objects;

    WarehouseAdapter(Context context, ArrayList<Warehouse> objects){
        this.context = context;
        this.objects = objects;
        layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Bitmap getImgBitmap(String encodedImg) {
        if (!encodedImg.equals("null")) {
            byte[] bytes = new byte[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                bytes = Base64.getDecoder().decode(encodedImg);
            }
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.warehouse_list_item, parent, false);
        }
        Warehouse c = getWarehouse(position);

        ((TextView) view.findViewById(R.id.showNameProduct)).setText(c.Name_Product);
        ((TextView) view.findViewById(R.id.showTypeProduct)).setText(c.Type_Product);
        ((TextView) view.findViewById(R.id.showCountProduct)).setText(String.valueOf(c.Count_Product));
        ((TextView) view.findViewById(R.id.showPriceProduct)).setText(String.valueOf(c.Price_Product));
        ((ImageView) view.findViewById(R.id.imageView)).setImageBitmap(getImgBitmap(c.Photo_Product));

        return view;
    }

    Warehouse getWarehouse(int position){
        return ((Warehouse) getItem(position));
    }

}
