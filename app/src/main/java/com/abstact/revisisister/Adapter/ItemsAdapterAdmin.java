package com.abstact.revisisister.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abstact.revisisister.Database.SQLiteHelper;
import com.abstact.revisisister.EditItemActivity;
import com.abstact.revisisister.ItemDetailActivity;
import com.abstact.revisisister.Admin.MainActivity;
import com.abstact.revisisister.Model.Item;
import com.abstact.revisisister.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class ItemsAdapterAdmin extends RecyclerView.Adapter<ItemsAdapterAdmin.GridViewHolder> {
    private final ArrayList<Item> itemArrayList = new ArrayList<>();
    private final Activity activity;

    private final SQLiteHelper sqLiteHelper;

    public ItemsAdapterAdmin(Activity activity) {
        this.activity = activity;
        sqLiteHelper = new SQLiteHelper(activity);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItemArrayList(ArrayList<Item> itemArrayList) {
        if (itemArrayList.size() > 0) {
            this.itemArrayList.clear();
        }
        this.itemArrayList.addAll(itemArrayList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_admin, parent, false);
        return new GridViewHolder(view);
    }

    @SuppressLint({"CutPasteId", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        int stokMasuk = itemArrayList.get(position).getItemStock();
        int stokKeluar = itemArrayList.get(position).getItemStockOut();
        holder.ivItem.setImageBitmap(itemArrayList.get(position).getmImageResource());
        holder.tvName.setText(itemArrayList.get(position).getItemName());
        holder.tvStock.setText(String.valueOf(stokMasuk - stokKeluar));
        String resultRupiah = formatRupiah((double) itemArrayList.get(position).getItemPrice());
        holder.tvPrice.setText(resultRupiah);

        holder.cvItem.setOnLongClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setMessage("Apakah yakin akan menghapus item ini?");
            builder.setPositiveButton("YA", (dialogInterface, i) -> {
                Log.e("ITEM ID =", itemArrayList.get(position).toString());
                Item item = itemArrayList.get(position);

                sqLiteHelper.deleteItem(item.getItemName());
                itemArrayList.remove(position);
                Toast.makeText(activity, "Hapus berhasil!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(activity, MainActivity.class);
                activity.startActivity(myIntent);
                activity.finish();
            });

            builder.setNegativeButton("TIDAK", (dialog, which) -> dialog.dismiss());

            AlertDialog alert = builder.create();
            alert.show();

            return false;
        });

        holder.cvItem.setOnClickListener(view -> {
            Intent intent = new Intent(activity, ItemDetailActivity.class);

            Item item = itemArrayList.get(position);
            Log.e("Item Name", itemArrayList.get(position).getItemName());
            intent.putExtra("name_item", item.getItemName());
            intent.putExtra("gambar", item.getmImageResource());
            intent.putExtra("price", item.getItemPrice());
            intent.putExtra("stok_masuk", item.getItemStock());
            intent.putExtra("stok_keluar", item.getItemStockOut());
            intent.putExtra("sisa_stok", item.getItemStock() - item.getItemStockOut());

            Log.i("name", item.getItemName());
            Log.i("price", String.valueOf(item.getItemPrice()));
            Log.i("sisa_stok", String.valueOf(item.getItemStock() - item.getItemStockOut()));

            activity.startActivity(intent);
        });

        holder.btnEdit.setOnClickListener(view -> {
            Intent intent = new Intent(activity, EditItemActivity.class); // update

            Item item = itemArrayList.get(position);
            Log.e("Item Name", itemArrayList.get(position).getItemName());
            intent.putExtra("name", item.getItemName());
            intent.putExtra("code", item.getItemKd());
            intent.putExtra("gambar", item.getmImageResource());
            intent.putExtra("price", item.getItemPrice());
            intent.putExtra("stok_masuk", item.getItemStock());
            intent.putExtra("stok_keluar", item.getItemStockOut());
            activity.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return itemArrayList.size();
    }

    public static class GridViewHolder extends RecyclerView.ViewHolder {
        final CardView cvItem;
        final ImageView ivItem;
        final TextView tvName, tvPrice, tvStock;
        final Button btnEdit;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItem = itemView.findViewById(R.id.item_iv);
            cvItem = itemView.findViewById(R.id.item_cv);

            tvName = itemView.findViewById(R.id.item_name_tv);
            tvPrice = itemView.findViewById(R.id.item_price_tv);
            tvStock = itemView.findViewById(R.id.item_stock_tv);

            btnEdit = itemView.findViewById(R.id.btnEdit);
        }
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}
