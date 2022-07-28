package com.abstact.revisisister.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.abstact.revisisister.CartListActivity;
import com.abstact.revisisister.Database.SQLiteHelper;
import com.abstact.revisisister.Model.Cart;
import com.abstact.revisisister.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final ArrayList<Cart> list = new ArrayList<>();
    private final Activity activity;
    private final SQLiteHelper sqLiteHelper;

    public CartAdapter(Activity activity) {
        this.activity = activity;
        sqLiteHelper = new SQLiteHelper(activity);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(ArrayList<Cart> list) {
        if (list.size() > 0) {
            this.list.clear();
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        int priceDetail = Integer.parseInt(list.get(position).getPrice());
        String resultPriceDetailTv = formatRupiah((double) priceDetail);

        holder.nameTv.setText(list.get(position).getName());
        holder.quantityTv.setText(list.get(position).getQuantity());
        holder.priceTv.setText(resultPriceDetailTv);

        holder.itemCv.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            builder.setTitle("Konfirmasi Hapus");
            builder.setMessage("Apakah yakin akan dihapus?");

            builder.setPositiveButton("YA", (dialogInterface, i) -> {
                sqLiteHelper.deleteCartData(list.get(position).getId());
                Toast.makeText(activity, "Hapus berhasil!", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(activity, CartListActivity.class);
                activity.startActivity(myIntent);
                activity.finish();
            });

            builder.setNegativeButton("TIDAK", (dialog, which) -> dialog.dismiss());

            AlertDialog alert = builder.create();
            alert.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        final TextView nameTv, quantityTv, priceTv;
        final CardView itemCv;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTv = itemView.findViewById(R.id.txtName);
            quantityTv = itemView.findViewById(R.id.txtQuantity);
            priceTv = itemView.findViewById(R.id.txtPrice);

            itemCv = itemView.findViewById(R.id.cv_item_cart);
        }
    }

    private String formatRupiah(Double number) {
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        return formatRupiah.format(number);
    }
}
