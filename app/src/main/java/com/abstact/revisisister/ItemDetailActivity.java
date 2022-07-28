package com.abstact.revisisister;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.abstact.revisisister.Admin.MainActivity;
import com.abstact.revisisister.Database.SQLiteHelper;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;

public class ItemDetailActivity extends AppCompatActivity {
    private int item = 0, price = 0, stockMasuk, stockKeluar, sisaStock = 0;
    private String nameItem;
    private TextView sumTv, priceTotalTv, nameTxt, quantityTxt, priceTxt, stockTxt;
    private Button btnOrder;

    public static SQLiteHelper sqLiteHelper;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Membuat tombol kembali
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        init();
        sqLiteHelper = new SQLiteHelper(this);
        sqLiteHelper.queryData();
        nameItem = getIntent().getStringExtra("name_item");
        price = getIntent().getIntExtra("price", -1);
        stockMasuk = getIntent().getIntExtra("stok_masuk", 0);
        stockKeluar = getIntent().getIntExtra("stok_keluar", 0);
        sisaStock = stockMasuk - stockKeluar;
        Log.i("Name Item: ", nameItem);
        Log.i("stok_masuk", String.valueOf(stockMasuk));
        Log.i("stok_keluar", String.valueOf(stockKeluar));
        Log.i("stok", String.valueOf(sisaStock));

        TextView nameTv = findViewById(R.id.item_name_detail_tv);
        nameTv.setText(nameItem);
        String resultPriceDetailTv = formatRupiah((double) price);
        TextView priceTv = findViewById(R.id.price_detail_tv);
        priceTv.setText(resultPriceDetailTv);
        TextView stockTv = findViewById(R.id.item_stock_detail_tv);
        stockTv.setText(Integer.toString(sisaStock));

        ImageView itemsIv = findViewById(R.id.item_detail_iv);
        Cursor cursor = sqLiteHelper.getItemData();

        if (cursor.moveToFirst()) {
            byte[] blob = cursor.getBlob(cursor.getColumnIndexOrThrow("gambar"));
            Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
            cursor.close();
            itemsIv.setImageBitmap(bitmap);
        }

//        while (!cursor.isAfterLast()) {
//            cursor.moveToNext();
//        }

        sumTv = findViewById(R.id.sumTv);
        priceTotalTv = findViewById(R.id.price_total_detail_tv);

        btnOrder.setOnClickListener(view -> {
            if (item < 1) {
                Toast.makeText(getApplicationContext(), "Maaf tidak bisa memesan kurang dari 1 item", Toast.LENGTH_SHORT).show();
            }

            try {
                if (item >= 1) {
                    Log.i("TXTNAME", nameTxt.getText().toString());
                    sqLiteHelper.inputCartData(
                            nameTxt.getText().toString(),
                            quantityTxt.getText().toString(),
                            priceTxt.getText().toString()
                    );
                    Toast.makeText(getApplicationContext(), "Masuk Keranjang!", Toast.LENGTH_SHORT).show();
                }
                quantityTxt.setText("0");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("name_item", nameItem);
            intent.putExtra("stok_masuk", stockMasuk);
            intent.putExtra("stok_keluar", stockKeluar);
            startActivity(intent);
        });

        Button incrementBtn = findViewById(R.id.increment_button);
        incrementBtn.setOnClickListener(view -> increment());
        Button decrementBtn = findViewById(R.id.decrement_button);
        decrementBtn.setOnClickListener(view -> decrement());
    }

    @SuppressLint("SetTextI18n")
    private void increment() {
        Button decrementBtn = findViewById(R.id.decrement_button);
        decrementBtn.setEnabled(true);

        Button incrementBtn = findViewById(R.id.increment_button);

        item++;
        sumTv.setText(Integer.toString(item));

        priceTotalTv.setText(Integer.toString(sumOfProduct(price)));

        sisaStock--;
        stockTxt.setText(Integer.toString(sisaStock));

        if (Integer.parseInt(String.valueOf(sisaStock)) <= 0)
            incrementBtn.setEnabled(false);

    }

    @SuppressLint("SetTextI18n")
    private void decrement() {
        Button incrementBtn = findViewById(R.id.increment_button);
        incrementBtn.setEnabled(true);

        Button decrementBtn = findViewById(R.id.decrement_button);
        if (item <= 0) {
            Toast.makeText(getApplicationContext(), "item tidak boleh 0!", Toast.LENGTH_SHORT).show();
            decrementBtn.setEnabled(false);
            return;
        }

        decrementBtn.setEnabled(true);
        item = item-1;
        sumTv.setText(Integer.toString(item));
        priceTotalTv.setText(Integer.toString(sumOfProduct(price)));

        sisaStock = sisaStock+1;
        stockTxt.setText(Integer.toString(sisaStock));

    }

    private int sumOfProduct(int price) {
        return item * price;
    }

    private void init() {
        nameTxt = findViewById(R.id.item_name_detail_tv);
        quantityTxt = findViewById(R.id.sumTv);
        priceTxt = findViewById(R.id.price_total_detail_tv);
        stockTxt = findViewById(R.id.item_stock_detail_tv);

        //btnCart = findViewById(R.id.cartbtn);
        btnOrder = findViewById(R.id.orderbtn);
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }

    // Membuat tombol kembali
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}