package com.abstact.revisisister.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import com.abstact.revisisister.Model.Cart;
import com.abstact.revisisister.Model.Item;
import com.abstact.revisisister.Model.Transaction;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "sisterDB.sqlite";
    private static final int DATABASE_VERSION = 1;

    // Table Item Columns
    private static final String TABLE_ITEM = "barang";
    private static final String KEY_ITEM_ID = "id_barang";
    private static final String KEY_ITEM_KD = "kd_barang";
    private static final String KEY_ITEM_NAME = "nama_barang";
    private static final String KEY_ITEM_PRICE = "harga_barang";
    private static final String KEY_ITEM_STOCK = "stok_masuk";
    private static final String KEY_ITEM_STOCK_OUT = "stok_keluar";
    private static final String KEY_ITEM_AFTER_STOCK = "sisa_stok";
    private static final String KEY_IMAGE_RESOURCE = "gambar";
    private static final String KEY_DATE_ARRIVE = "tgl_masuk";
    private static final String CREATE_ITEM_SQL = "CREATE TABLE " + TABLE_ITEM +
            "(" + KEY_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_ITEM_KD + " TEXT, "
            + KEY_ITEM_NAME + " TEXT NOT NULL, "
            + KEY_ITEM_PRICE + " INTEGER, "
            + KEY_ITEM_STOCK + " INTEGER, "
            + KEY_ITEM_STOCK_OUT + " INTEGER, "
            + KEY_ITEM_AFTER_STOCK + " INTEGER,"
            + KEY_IMAGE_RESOURCE + " BLOB, "
            + KEY_DATE_ARRIVE + " TIMESTAMP);";

    // Table Cart Columns
    public static final String TABLE_CART = "keranjang";
    public static final String KEY_ID_CART = "id_keranjang";
    public static final String KEY_NAME_CART = "nama_keranjang";
    private static final String KEY_ID_TRANSACTION_CART = "id_transaksi";
    private static final String KEY_ITEM_AMOUNTS_CART = "jml_barang";
    private static final String KEY_UNIT_PRICE_CART = "harga_satuan";
    private static final String CREATE_CART_SQL = "CREATE TABLE " + TABLE_CART +
            "(" + KEY_ID_CART + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_NAME_CART + " INTEGER, "
            + KEY_ID_TRANSACTION_CART + " INTEGER, "
            + KEY_ITEM_AMOUNTS_CART + " INTEGER, "
            + KEY_UNIT_PRICE_CART + " INTEGER);";

    // table transaksi
    private static final String TABLE_TRANSACTION = "transaksi";
    private static final String KEY_ID_TRANSACTION = "id_transaksi";
    private static final String KEY_TRANSACTION_NAME = "nama_barang";
    private static final String KEY_TRANSACTION_ITEM_AMOUNT = "jml_barang";
    private static final String KEY_UNIT_PRICE_TRANSACTION = "harga_satuan";
    private static final String KEY_PAY_TRANSACTION = "dibayar";
    private static final String KEY_DATE_TRANSACTION = "tgl_transaksi";
    private static final String KEY_TOTAL_PAY_TRANSACTION = "total_pembayaran";
    private static final String CREATE_TRANSACTION_SQL = "CREATE TABLE " + TABLE_TRANSACTION +
            "(" + KEY_ID_TRANSACTION + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TRANSACTION_NAME + " TEXT, "
            + KEY_TRANSACTION_ITEM_AMOUNT + " INTEGER, "
            + KEY_UNIT_PRICE_TRANSACTION + " INTEGER, "
            + KEY_TOTAL_PAY_TRANSACTION + " INTEGER, "
            + KEY_PAY_TRANSACTION + " INTEGER, "
            + KEY_DATE_TRANSACTION + " DEFAULT CURRENT_TIMESTAMP);";

    String sql;

    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ITEM_SQL); // table item
        sqLiteDatabase.execSQL(CREATE_CART_SQL); // table cart
        sqLiteDatabase.execSQL(CREATE_TRANSACTION_SQL); // table transaction
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_ITEM + "'"); // table item
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_CART + "'"); // table cart
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS '" + TABLE_TRANSACTION + "'");
        onCreate(sqLiteDatabase);
    }

    // ITEM
    public long inputItemData(String name, String code, String price, Bitmap image, String stock, String stockOut, String afterStock) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(); // untuk memasukan gambar
        image.compress(Bitmap.CompressFormat.JPEG, 100, out); // untuk memasukan gambar
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_NAME, name);
        values.put(KEY_ITEM_KD, code);
        values.put(KEY_ITEM_PRICE, price);
        values.put(KEY_IMAGE_RESOURCE, out.toByteArray());
        values.put(KEY_ITEM_STOCK, stock);
        values.put(KEY_ITEM_STOCK_OUT, stockOut);
        values.put(KEY_ITEM_AFTER_STOCK, String.valueOf(Integer.parseInt(stock) - Integer.parseInt(stockOut)));


        long insert;
        insert = database.insert(TABLE_ITEM, null, values);
        return insert;
    }

    public void updateItemData(String name, String code, String price, Bitmap image, String stock, String stockOut) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(); // untuk memasukan gambar
        image.compress(Bitmap.CompressFormat.JPEG, 100, out); // untuk memasukan gambar
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ITEM_NAME, name);
        contentValues.put(KEY_ITEM_KD, code);
        contentValues.put(KEY_ITEM_PRICE, price);
        contentValues.put(KEY_IMAGE_RESOURCE, out.toByteArray());
        contentValues.put(KEY_ITEM_STOCK, stock);
        contentValues.put(KEY_ITEM_STOCK_OUT, stockOut);
        contentValues.put(KEY_ITEM_AFTER_STOCK, String.valueOf(Integer.parseInt(stock) - Integer.parseInt(stockOut)));

        database.update(TABLE_ITEM, contentValues, KEY_ITEM_NAME + " = ? ", new String[] {name});
    }

    // ITEM
    @SuppressLint("Range")
    public ArrayList<Item> getAllItem() {
        ArrayList<Item> itemArrayList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_ITEM;
        SQLiteDatabase database = this.getReadableDatabase();

        try (Cursor c = database.rawQuery(query, null)) {
            if (c.moveToFirst()) {
                do {
                    Item item = new Item();

                    item.setItemKd(c.getInt(c.getColumnIndex(KEY_ITEM_KD)));
                    item.setItemName(c.getString(c.getColumnIndex(KEY_ITEM_NAME)));
                    item.setItemPrice(c.getInt(c.getColumnIndex(KEY_ITEM_PRICE)));
                    item.setItemStock(c.getInt(c.getColumnIndex(KEY_ITEM_STOCK)));
                    item.setItemStockOut(c.getInt(c.getColumnIndex(KEY_ITEM_STOCK_OUT)));
                    item.setItemAfterStock(c.getInt(c.getColumnIndex(KEY_ITEM_STOCK)) - c.getInt(c.getColumnIndex(KEY_ITEM_STOCK_OUT)));

                    // untuk set gambar
                    byte[] blob = c.getBlob(c.getColumnIndex(KEY_IMAGE_RESOURCE));
                    Bitmap bmp = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                    Bitmap bmpc = Bitmap.createScaledBitmap(bmp, 200, 200, false);
                    item.setmImageResource(bmpc);

                    itemArrayList.add(item);
                } while (c.moveToNext());
            }
        }

        return itemArrayList;
    }

    // ITEM
    public Cursor getItemData() {
        SQLiteDatabase database = getReadableDatabase();

        sql = "SELECT " + KEY_ITEM_ID + ", "
                + KEY_ITEM_NAME + ", "
                + KEY_ITEM_PRICE + ", "
                + KEY_IMAGE_RESOURCE + ", "
                + KEY_ITEM_STOCK + ", "
                + KEY_ITEM_STOCK_OUT + " FROM " + TABLE_ITEM;

        return database.rawQuery(sql, null);
    }

    // ITEM
    public void deleteItem(String name) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_ITEM, KEY_ITEM_NAME + " = ?", new String[]{name});
    }

    public void inputStock(String stock, String stockOut, String name) {
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_ITEM_STOCK, stock);
        contentValues.put(KEY_ITEM_STOCK_OUT, stockOut);
        contentValues.put(KEY_ITEM_AFTER_STOCK, String.valueOf(Integer.parseInt(stock) - Integer.parseInt(stockOut)));

        database.update(TABLE_ITEM, contentValues, KEY_ITEM_NAME + " = ? ", new String[] {name});
        database.close();
    }

    // CART
    public long inputCartData(String name, String quantity, String price) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME_CART, name);
        values.put(KEY_ITEM_AMOUNTS_CART, quantity);
        values.put(KEY_UNIT_PRICE_CART, price);

        long insert;
        insert = database.insert(TABLE_CART, null, values);
        return insert;
    }

    @SuppressLint("Range")
    public ArrayList<Cart> getAllData() {
        ArrayList<Cart> cartArrayList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CART;
        @SuppressLint("Recycle") Cursor c = database.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Cart cart = new Cart(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));

                cart.setId(c.getInt(c.getColumnIndex(KEY_ID_CART)));
                cart.setName(c.getString(c.getColumnIndex(KEY_NAME_CART)));
                cart.setQuantity(c.getString(c.getColumnIndex(KEY_ITEM_AMOUNTS_CART)));
                cart.setPrice(c.getString(c.getColumnIndex(KEY_UNIT_PRICE_CART)));
                Log.e("price: ", cart.getPrice());

                int total = 0;
                total = total + Integer.parseInt(cart.getPrice());
                Log.e("pricetotal: ", String.valueOf(total));

                cartArrayList.add(cart);
            } while (c.moveToNext());
        }

        return cartArrayList;
    }

    public void deleteCartData(int id) {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(TABLE_CART, KEY_ID_CART + " = ?", new String[]{String.valueOf(id)});
    }

    public Cursor getData() {
        SQLiteDatabase database = getReadableDatabase();
        sql = "SELECT " + KEY_ID_CART + ", "
                + KEY_NAME_CART + ", "
                + KEY_ITEM_AMOUNTS_CART + ", "
                + KEY_UNIT_PRICE_CART + " FROM " + TABLE_CART;
        return database.rawQuery(sql, null);
    }

    @SuppressLint("Range")
    public ArrayList<Cart> getAllDataCart() {
        ArrayList<Cart> cartArrayList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_CART;
        @SuppressLint("Recycle") Cursor c = database.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Cart cart = new Cart(c.getInt(0), c.getString(1), c.getString(2), c.getString(3));

                cart.setId(c.getInt(c.getColumnIndex(KEY_ID_CART)));
                cart.setName(c.getString(c.getColumnIndex(KEY_NAME_CART)));
                cart.setQuantity(c.getString(c.getColumnIndex(KEY_ITEM_AMOUNTS_CART)));
                cart.setPrice(c.getString(c.getColumnIndex(KEY_UNIT_PRICE_CART)));
                Log.e("price: ", cart.getPrice());

                int total = 0;
                total = total + Integer.parseInt(cart.getPrice());
                Log.e("pricetotal: ", String.valueOf(total));

                cartArrayList.add(cart);
            } while (c.moveToNext());
        }

        return cartArrayList;
    }

    public void queryData() {
        String query;
        SQLiteDatabase database = getWritableDatabase();
        query = "CREATE TABLE IF NOT EXISTS " + TABLE_CART +
                "(" + KEY_ID_CART + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME_CART + " VARCHAR, "
                + KEY_ID_TRANSACTION_CART + " INTEGER, "
                + KEY_ITEM_PRICE + " INTEGER, "
                + KEY_ITEM_AMOUNTS_CART + " INTEGER, "
                + KEY_UNIT_PRICE_CART + " INTEGER)";
        database.execSQL(query);
    }

    // Transaction
    public long inputTransactionData(String name, String quantity, String price) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TRANSACTION_NAME, name);
        values.put(KEY_TRANSACTION_ITEM_AMOUNT, quantity);
        values.put(KEY_UNIT_PRICE_TRANSACTION, price);

        long insert;
        insert = database.insert(TABLE_TRANSACTION, null, values);
        return insert;
    }

    @SuppressLint({"Range", "Recycle"})
    public ArrayList<Transaction> getAllTransactionData() {
        ArrayList<Transaction> transactionArrayList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_TRANSACTION;
        Cursor c = database.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getString(4),
                        c.getString(5),
                        c.getInt(6));

                transaction.setIdTransaction(c.getInt(c.getColumnIndex(KEY_ID_TRANSACTION)));
                transaction.setItemDateTransaction(c.getString(c.getColumnIndex(KEY_DATE_TRANSACTION)));
                transaction.setNameTransaction(c.getString(c.getColumnIndex(KEY_TRANSACTION_NAME)));
                transaction.setAmountTransaction(c.getString(c.getColumnIndex(KEY_TRANSACTION_ITEM_AMOUNT)));
                transaction.setPayTransaction(c.getString(c.getColumnIndex(KEY_PAY_TRANSACTION)));
                transaction.setTotalPayTransaction(c.getString(c.getColumnIndex(KEY_TOTAL_PAY_TRANSACTION)));
                transaction.setPriceTransaction(c.getInt(c.getColumnIndex(KEY_UNIT_PRICE_TRANSACTION)));

                int total = 0;
                total = total + transaction.getPriceTransaction();
                Log.e("Total: ", String.valueOf(total));

                transactionArrayList.add(transaction);

            } while (c.moveToNext());
        }
        return transactionArrayList;
    }

    public Cursor getTransaction() {
        SQLiteDatabase database = getReadableDatabase();
        sql = "SELECT " + KEY_ID_TRANSACTION + ", "
                + KEY_TRANSACTION_NAME + ", "
                + KEY_TRANSACTION_ITEM_AMOUNT + ", "
                + KEY_UNIT_PRICE_TRANSACTION + ", "
                + KEY_TOTAL_PAY_TRANSACTION + ", "
                + KEY_PAY_TRANSACTION + ", "
                + KEY_DATE_TRANSACTION + " FROM " + TABLE_TRANSACTION;
        return database.rawQuery(sql, null);
    }

    public void queryTransaction() {
        String query;
        SQLiteDatabase database = getWritableDatabase();
        query = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTION +
                "(" + KEY_ID_TRANSACTION + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TRANSACTION_NAME + " TEXT, "
                + KEY_TRANSACTION_ITEM_AMOUNT + " INTEGER, "
                + KEY_UNIT_PRICE_TRANSACTION + " INTEGER, "
                + KEY_TOTAL_PAY_TRANSACTION + " INTEGER, "
                + KEY_PAY_TRANSACTION + " INTEGER, "
                + KEY_DATE_TRANSACTION + " DEFAULT CURRENT_TIMESTAMP);";
        database.execSQL(query);
    }

}
