package com.abstact.revisisister;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abstact.revisisister.Adapter.CartAdapter;
import com.abstact.revisisister.Database.SQLiteHelper;
import com.abstact.revisisister.Model.Cart;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class CartListActivity extends AppCompatActivity {
    RecyclerView daftarRv;
    public TextView totalPriceTv, totalKembalianTV;
    ArrayList<Cart> list = new ArrayList<>();
    private CartAdapter adapter = null;
    private String name = "", quantity, price = "", nameItem;
    private int stockMasuk = 0, stockKeluar = 0, sisaStock = 0;

    SQLiteHelper sqLiteHelper;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;
    InputStream inputStream;
    Thread thread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    TextView lblPrinterName;
    EditText uangET;
    Button btnConnect, btnDisconnect, printBtn;

    @SuppressLint({"Range", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_list);

        // Membuat tombol kembali
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        lblPrinterName = findViewById(R.id.lblPrinterName);

        Button btnOrder = findViewById(R.id.btnPesan);
        daftarRv = findViewById(R.id.keranjangRv);
        totalPriceTv = findViewById(R.id.total);
        int total = 0;

        list = new ArrayList<>();
        adapter = new CartAdapter(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        daftarRv.setLayoutManager(layoutManager);
        daftarRv.setAdapter(adapter);

        sqLiteHelper = new SQLiteHelper(this);
        sqLiteHelper.queryTransaction();

        // get the data from sqlite
        //---------;;;;;;;;;;;;;;;;;;;;;;
        list = sqLiteHelper.getAllData();
        adapter.setList(list);
        Cursor c = sqLiteHelper.getData();
        list.clear();

        nameItem = getIntent().getStringExtra("name_item");
        stockMasuk = getIntent().getIntExtra("stok_masuk", 0);
        stockKeluar = getIntent().getIntExtra("stok_keluar", 0);
        sisaStock = stockMasuk - stockKeluar;
        Log.i("Name Activity: ", "CartListActivity");
//        Log.i("name_item", nameItem);
        Log.i("stok_masuk", String.valueOf(stockMasuk));
        Log.i("stok_keluar", String.valueOf(stockKeluar));

        if (c.moveToFirst()) {
            do {
                Cart cart = new Cart(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));

                cart.setId(c.getInt(c.getColumnIndex("id_keranjang")));
                name = cart.setName(c.getString(c.getColumnIndex("nama_keranjang")));
                quantity = cart.setQuantity(c.getString(c.getColumnIndex("jml_barang")));
                price = cart.setPrice(c.getString(c.getColumnIndex("harga_satuan")));
                Log.e("price: ", cart.getPrice());

                total = total + Integer.parseInt(cart.getPrice());
                Log.e("pricetotal: ", String.valueOf(total));


                list.add(cart);
            } while (c.moveToNext());
        }

//        Log.i("stok", String.valueOf(sisaStock - Integer.parseInt(quantity))); // ini sisa stok yang berhasil dikirim dari ItemDetailActivity

        totalPriceTv.setText(String.valueOf(total));

        daftarRv.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final int position = 0;

            builder.setTitle("Hapus Item");
            builder.setMessage("Apakah anda ingin menghapus item ini?");
            builder.setPositiveButton("Ya", (dialogInterface, i1) -> {
                Log.e("CART ID =", list.get(position).toString());
                Cart cart = list.get(position);

                sqLiteHelper.deleteCartData(cart.getId());
                list.remove(position);
                adapter.notifyDataSetChanged();
                daftarRv.invalidate();
            });
            builder.setNegativeButton("Tidak", (dialogInterface, i12) -> {
            });
            builder.create();
            builder.show();
        });

        btnOrder.setOnClickListener(view -> {
            if (list.size() > 0)
                showAlertDialog();
            else
                Toast.makeText(getApplicationContext(), "Keranjang Belanja Kosong", Toast.LENGTH_SHORT).show();
        });

        daftarRv.setFocusable(true);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("Range")
    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_calculator, null);

        uangET = view.findViewById(R.id.bayarEt);
        final TextView totalPriceTV = view.findViewById(R.id.total_bayar_tv);
        totalKembalianTV = view.findViewById(R.id.kembalianTV);
        final Button hitungBtn = view.findViewById(R.id.hitungBtn);
        printBtn = view.findViewById(R.id.printBtn);
        btnConnect = view.findViewById(R.id.connectBtn);
        btnDisconnect = view.findViewById(R.id.disconnectBtn);

        list = sqLiteHelper.getAllData();
        adapter.setList(list);
        Cursor c = sqLiteHelper.getData();
        list.clear();
        int total = 0;

        if (c.moveToFirst()) {
            do {
                Cart cart = new Cart(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));

                cart.setPrice(c.getString(c.getColumnIndex("harga_satuan")));
                Log.i("price: ", cart.getPrice());

                total = total + Integer.parseInt(cart.getPrice());
                Log.i("pricetotal: ", String.valueOf(total));
                list.add(cart);
            } while (c.moveToNext());
        }
        totalPriceTV.setText(String.valueOf(total));

        int finalTotal = total;
        hitungBtn.setOnClickListener(views -> {
            int getResult;
            String jml_barang = "";
            getResult = Integer.parseInt(uangET.getText().toString()) - finalTotal;
            totalKembalianTV.setText(String.valueOf(getResult));
            Log.i("Kembalian: ", String.valueOf(getResult));

            if (c.moveToFirst()) {
                do {
                    Cart cart = new Cart(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));

                    jml_barang = cart.setQuantity(c.getString(c.getColumnIndex("jml_barang")));
                    Log.e("jumlah barang: ", cart.getQuantity());

                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                } while (c.moveToNext());
            }
//            int afterStock = sisaStock - Integer.parseInt(quantity);
//            Log.i("after_stock: ", String.valueOf(afterStock));
            sqLiteHelper.inputStock(String.valueOf(stockMasuk), jml_barang, nameItem); //bikin cara oper idnya juga
        });

        btnConnect.setOnClickListener(view2 -> {
            try {
                findBluetoothDevice();
                openBluetoothPrinter();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        btnDisconnect.setOnClickListener(view3 -> {
            try {
                disconnectBt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        printBtn.setOnClickListener(view1 -> {
            try {
                printData();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void findBluetoothDevice() {
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (bluetoothAdapter==null) {
                lblPrinterName.setText("No Bluetooth Adapter found");
            }

            if (bluetoothAdapter.isEnabled()) {
                Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBt, 0);
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice pairedDev:pairedDevices) {

                    if (pairedDev.getName().equals("RPP02N")) {
                        bluetoothDevice=pairedDev;
                        lblPrinterName.setText("Bluetooth Printer Attached: " + pairedDev.getName());
                        break;
                    }
                }
            }

            lblPrinterName.setText("Bluetooth Printer Attached");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openBluetoothPrinter() {
        try {
            UUID uuidString = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuidString);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            beginListenData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void beginListenData() {
        try {
            Handler handler = new Handler();
            byte delimiter = 10;
            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            thread = new Thread(() -> {
                while (!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int byteAvailable = inputStream.available();
                        if (byteAvailable > 0) {
                            byte[] packByte = new byte[byteAvailable];
                            inputStream.read(packByte);

                            for (int i = 0; i < byteAvailable; i++) {
                                byte b = packByte[i];
                                if (b == delimiter) {
                                    byte[] encodeByte = new byte[readBufferPosition];
                                    System.arraycopy(
                                            readBuffer, 0,
                                            encodeByte, 0,
                                            encodeByte.length
                                    );

                                    final String data = new String(encodeByte, StandardCharsets.US_ASCII);
                                    readBufferPosition = 0;
                                    handler.post(() -> lblPrinterName.setText(data));
                                } else {
                                    readBuffer[readBufferPosition++]=b;
                                }
                            }
                        }
                    } catch (IOException e) {
                        stopWorker = true;
                    }
                }
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"Range", "SimpleDateFormat"})
    private void printData() {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            Date date = new Date();
            String today = dateFormat.format(date);

            OutputStream outputStream = bluetoothSocket.getOutputStream();
            String BILL;
            BILL = "       TOKO JURAGAN LAMPU       \n" +
                    "Jl. Cipto mangunkusumo No. RT.08\n" +
                    ", Sengkotek, Kec. Loa Janan Ilir\n\n\n";

            BILL = BILL + "                " + today + "\n";
            BILL = BILL + "--------------------------------\n";

            BILL = BILL + String.format("%1$-4s %2$-18s %3$-8s", "Qty", "Item", "Total");
            BILL = BILL + "--------------------------------\n";

            int total = 0;
            Cursor c = (Cursor) sqLiteHelper.getAllDataCart();
            if (c.moveToFirst()) {
                do {
                    Cart cart = new Cart(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));

                    cart.setId(c.getInt(c.getColumnIndex("id_keranjang")));
                    cart.setName(c.getString(c.getColumnIndex("nama_keranjang")));
                    cart.setQuantity(c.getString(c.getColumnIndex("jml_barang")));
                    cart.setPrice(c.getString(c.getColumnIndex("harga_satuan")));

                    int satuan = Integer.parseInt(cart.getPrice()) / Integer.parseInt(cart.getQuantity());

                    BILL = BILL + String.format("%1$-4s %2$-18s %3$-10s", "" + quantity + "", "" + name + "", "" + price + "") + "\n";

                    Log.e("price: ", cart.getPrice());
                    total = total + Integer.parseInt(cart.getPrice());
                    list.add(cart);

                } while (c.moveToNext());
            }

            BILL = BILL + "--------------------------------";
            BILL = BILL + "\n \n";

            BILL = BILL + "                 Total" + "   " + total + "\n";
            BILL = BILL + "                 Bayar" + "   " + uangET.getText().toString() + "\n";
            BILL = BILL + "             Kembalian" + "   " + totalKembalianTV.getText().toString() + "\n";
            BILL = BILL + "\n\n";

            BILL = BILL + "        Kritik dan Saran:       ";
            BILL = BILL + "    putuaditya17800@gmail.com   ";
            BILL = BILL + "        Powered by SISTER       ";

            BILL = BILL + "\n \n \n";

            outputStream.write(BILL.getBytes(StandardCharsets.UTF_8));

            // Setting height
            int gs = 29;
            outputStream.write(intToByteArray(gs));
            int h = 104;
            outputStream.write(intToByteArray(h));
            int n = 152;
            outputStream.write(intToByteArray(n));

            // Setting Width
            int gs_width = 29;
            outputStream.write(intToByteArray(gs_width));
            int w = 119;
            outputStream.write(intToByteArray(w));
            int n_width = 2;
            outputStream.write(intToByteArray(n_width));

        } catch (Exception e) {
            Log.e("CartListActivity", "Exe", e);
        }
    }

    private byte intToByteArray(int n_width) {
        byte[] b = ByteBuffer.allocate(4).putInt(n_width).array();

        for (int k = 0; k < b.length; k++) {
            System.out.println("Adit [" + k + "] = " + "0x" + UnicodeFormatter.byteToHex(b[k]));
        }

        return b[3];
    }

    @SuppressLint("SetTextI18n")
    private void disconnectBt() {
        try {
            stopWorker = true;
            outputStream.close();
            inputStream.close();
            bluetoothSocket.close();
            lblPrinterName.setText("Printer Disconnected");
        } catch (Exception e) {
            e.printStackTrace();
        }
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