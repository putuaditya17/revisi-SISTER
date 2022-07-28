package com.abstact.revisisister;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.abstact.revisisister.Database.SQLiteHelper;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class EditItemActivity extends AppCompatActivity {
    private EditText edtName, edtCode, edtPrice, edtStock, edtStockOut, edtAfterStock;
    private ImageView ivBarang;
    private Button btnAddImage, btnUpdate;
    private Uri uri;

    final int kodeGallery = 100, kodeCamera = 99;

    SQLiteHelper sqLiteHelper;

    @Override
    @SuppressLint("Range")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Membuat tombol kembali
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sqLiteHelper = new SQLiteHelper(this);
        init();

        String name = getIntent().getStringExtra("name");
        int price = getIntent().getIntExtra("price", 0);
        int stockIn = getIntent().getIntExtra("stok_masuk", 0);
        int stockOut = getIntent().getIntExtra("stok_keluar", 0);
        int codes = getIntent().getIntExtra("code", 0);

        Cursor c = sqLiteHelper.getItemData();
        if (c.moveToFirst()) {
            do {
                edtName.setText(name);
                edtCode.setText(String.valueOf(codes));
                edtPrice.setText(String.valueOf(price));
                edtStock.setText(String.valueOf(stockIn));
                edtStockOut.setText(String.valueOf(stockOut));
                edtAfterStock.setText(String.valueOf(stockIn - stockOut));

                @SuppressLint("Range") byte[] blob = c.getBlob(c.getColumnIndex("gambar"));
                Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                ivBarang.setImageBitmap(bmp);
                ivBarang.setVisibility(View.VISIBLE);
            } while (c.moveToPrevious());
        }

        btnAddImage.setOnClickListener(view -> selectImage());

        btnUpdate.setOnClickListener(view -> {
            sqLiteHelper.updateItemData(
                    edtName.getText().toString(),
                    edtCode.getText().toString(),
                    edtPrice.getText().toString(),
                    imageViewToByte(ivBarang),
                    edtStock.getText().toString(),
                    edtStockOut.getText().toString());
            Toast.makeText(getApplicationContext(), "Update Berhasil!", Toast.LENGTH_SHORT).show();

        });
    }

    private void selectImage() {
        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tambah Gambar!");
        builder.setItems(options, (dialogInterface, i) -> {
            if (options[i].equals("Camera")) {
                openCamera();
            } else if (options[i].equals("Gallery")) {
                openGallery();
            } else if (options[i].equals("Cancel")) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, kodeCamera);
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, kodeGallery);
    }

    private Bitmap imageViewToByte(ImageView ivBarang) {
        Bitmap bitmap = ((BitmapDrawable) ivBarang.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == kodeGallery) {
            assert data != null;
            uri = data.getData();
            ivBarang.setImageURI(uri);
        } else if (requestCode == kodeCamera && resultCode == RESULT_OK) {
            assert data != null;
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ivBarang.setImageBitmap(bitmap);
        }

    }

    private void init() {
        ivBarang = findViewById(R.id.edit_barang_iv);
        edtName = findViewById(R.id.editName);
        edtCode = findViewById(R.id.editCode);
        edtPrice = findViewById(R.id.editPrice);
        edtStock = findViewById(R.id.editStock);
        edtStockOut = findViewById(R.id.editStockOut);
        edtAfterStock = findViewById(R.id.editAfterStock);

        btnAddImage = findViewById(R.id.update_image_btn);
        btnUpdate = findViewById(R.id.btnUpdate);
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