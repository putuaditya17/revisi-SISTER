package com.abstact.revisisister.Kasir;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abstact.revisisister.Adapter.ItemsAdapterAdmin;
import com.abstact.revisisister.Adapter.ItemsAdapterKasir;
import com.abstact.revisisister.CartListActivity;
import com.abstact.revisisister.Database.SQLiteHelper;
import com.abstact.revisisister.InputItemActivity;
import com.abstact.revisisister.LoginActivity;
import com.abstact.revisisister.Model.Item;
import com.abstact.revisisister.R;

import java.util.ArrayList;
import java.util.Objects;

public class KasirActivity extends AppCompatActivity {
    private ItemsAdapterKasir adapter;
    private ArrayList<Item> arrayList;
    private SQLiteHelper sqLiteHelper;
    private int stockMasuk, stockKeluar, sisaStock = 0;
    private String nameItem;
    private String gambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.rv_view);
        recyclerView.setHasFixedSize(true);
        adapter = new ItemsAdapterKasir(this);

        sqLiteHelper = new SQLiteHelper(this);

        arrayList = sqLiteHelper.getAllItem();
        adapter.setItemArrayList(arrayList);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setAdapter(adapter);

        nameItem = getIntent().getStringExtra("name_item");
        gambar = getIntent().getStringExtra("gambar");
        stockMasuk = getIntent().getIntExtra("stok_masuk", 0);
        stockKeluar = getIntent().getIntExtra("stok_keluar", 0);
        sisaStock = stockMasuk - stockKeluar;
        Log.e("FIRTS ACTIVITY ", "Main Activity");
        Log.i("stok_masuk", String.valueOf(stockMasuk));
        Log.i("stok_keluar", String.valueOf(stockKeluar));
        Log.i("stok", String.valueOf(sisaStock));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        arrayList = sqLiteHelper.getAllItem();
        adapter.setItemArrayList(arrayList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu, menu);

        // Menu Search
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.action_search){

        } else if (item.getItemId() == R.id.inputProduct) {
            Objects.requireNonNull(getSupportActionBar()).setTitle("Kelola Produk");
            Intent i = new Intent(this, InputItemActivity.class);
            startActivity(i);
        } else if (item.getItemId() == R.id.cart){
            Objects.requireNonNull(getSupportActionBar()).setTitle("Daftar Belanja");
            Intent j = new Intent(this, CartListActivity.class);

            j.putExtra("name_item", nameItem);
            j.putExtra("stok_masuk", stockMasuk);
            j.putExtra("stok_keluar", stockKeluar);
            startActivity(j);
        } else if (item.getItemId() == R.id.logOut) {
            Intent k = new Intent(this, LoginActivity.class);
            startActivity(k);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
