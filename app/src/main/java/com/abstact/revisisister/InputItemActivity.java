package com.abstact.revisisister;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.abstact.revisisister.Database.SQLiteHelper;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class InputItemActivity extends AppCompatActivity {
    private EditText edtName;
    private EditText edtCode;
    private EditText edtPrice;
    private EditText edtStock;
    private EditText edtStockOut;
    private ImageView ivBarang;

    final int kodeGallery = 100, kodeCamera = 99;

    SQLiteHelper sqLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_item);

        // Membuat tombol kembali
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sqLiteHelper = new SQLiteHelper(this);

        ivBarang = findViewById(R.id.input_barang_iv);
        edtName = findViewById(R.id.inputName);
        edtCode = findViewById(R.id.inputCode);
        edtPrice = findViewById(R.id.inputPrice);
        edtStock = findViewById(R.id.inputStock);
        edtStockOut = findViewById(R.id.inputStockOut);
        //edtAfterStock = findViewById(R.id.inputAfterStock);

        Button btnAddImage = findViewById(R.id.add_image_btn);
        Button btnSave = findViewById(R.id.btnSave);

        btnAddImage.setOnClickListener(view -> selectImage());

        btnSave.setOnClickListener(view -> {
            sqLiteHelper.inputItemData(
                    edtName.getText().toString(),
                    edtCode.getText().toString(),
                    edtPrice.getText().toString(),
                    imageViewToByte(ivBarang),
                    edtStock.getText().toString(),
                    edtStockOut.getText().toString(),
                    String.valueOf(Integer.parseInt(edtStock.getText().toString()) - Integer.parseInt(edtStockOut.getText().toString())) );
            edtName.setText("");
            edtCode.setText("");
            edtPrice.setText("");
            edtStock.setText("");
            edtStockOut.setText("");
            //edtAfterStock.setText("");
            ivBarang.setImageURI(Uri.parse(String.valueOf(R.color.cardview_dark_background)));
            Toast.makeText(getApplicationContext(), "Simpan Berhasil!", Toast.LENGTH_SHORT).show();

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
            Uri uri = data.getData();
            ivBarang.setImageURI(uri);
        } else if (requestCode == kodeCamera && resultCode == RESULT_OK) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ivBarang.setImageBitmap(bitmap);
        }

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