package com.abstact.revisisister.Model;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Item implements Serializable {
    private int itemId, itemKd;
    private String itemName;
    private int itemPrice;
    private int itemStock, itemStockOut, itemAfterStock;
    private Bitmap mImageResource;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemKd() {
        return itemKd;
    }

    public void setItemKd(int itemKd) {
        this.itemKd = itemKd;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemStock() {
        return itemStock;
    }

    public void setItemStock(int itemStock) {
        this.itemStock = itemStock;
    }

    public Bitmap getmImageResource() {
        return mImageResource;
    }

    public void setmImageResource(Bitmap mImageResource) {
        this.mImageResource = mImageResource;
    }

    public int getItemStockOut() {
        return itemStockOut;
    }

    public void setItemStockOut(int itemStockOut) {
        this.itemStockOut = itemStockOut;
    }

    public int getItemAfterStock() {
        return itemAfterStock;
    }

    public void setItemAfterStock(int itemAfterStock) {
        this.itemAfterStock = itemAfterStock;
    }
}
