package com.indomaret.klikindomaret.data;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

/**
 * Created by madearyawisnuwardana on 3/3/16.
 */

public final class SideMenuButtonAdapter extends BaseAdapter {
    private final List<Item> mItems = new ArrayList<Item>();
    private final LayoutInflater mInflater;
    public Context mContext;

    public SideMenuButtonAdapter(Context context, Boolean isHeader, Boolean hasLogin, String myName) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        if(isHeader) {
            mItems.add(new Item("Beranda", R.drawable.beranda));
            mItems.add(new Item((hasLogin) ? myName : "Masuk/Daftar", R.drawable.akun_saya));
            mItems.add(new Item("Daftar\nKeinginan", R.drawable.wish_list));
            mItems.add(new Item("Cakupan\nKode Pos", R.drawable.cakupan_kode_pos));
            mItems.add(new Item("Cara\nMemesan", R.drawable.cara_memesan));
            mItems.add(new Item("Status\nPemesanan", R.drawable.status_pesanan));
            mItems.add(new Item("Tanya\nJawab", R.drawable.tanya_jawab));
            mItems.add(new Item("Chat\nBantuan", R.drawable.chat_bantuan));
        } else {
            mItems.add(new Item("Kebijakan\nPrivasi", R.drawable.kebijakan));
            mItems.add(new Item("Layanan\nPelanggan", R.drawable.layanan_pelanggan));
            mItems.add(new Item("Tentang\nKami", R.drawable.tentang_kami));
            mItems.add(new Item("Semua\nMerek", R.drawable.mrek_pilihan));
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Item getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).drawableId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        ImageView picture;
        TextView name;

        if (v == null) {
            v = mInflater.inflate(R.layout.grid_item, viewGroup, false);
            v.setTag(R.id.picture, v.findViewById(R.id.picture));
            v.setTag(R.id.text, v.findViewById(R.id.text));
        }

        picture = (ImageView) v.getTag(R.id.picture);
        name = (AutofitTextView) v.getTag(R.id.text);

        Item item = getItem(i);

        picture.setImageResource(item.drawableId);
        name.setText(item.name);

        // Get the TextView LayoutParams
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) v.getLayoutParams();

        // Set the TextView height (GridView item/row equal height)
        params.height = convertDpToPixels(130, mContext);

        // Set the TextView layout parameters
        v.setLayoutParams(params);

        return v;
    }

    public static int convertDpToPixels(float dp, Context context){
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics()
        );
    }

    public void setItemAtId(int i, String newTitle) {
        Log.d("Ganti judul",newTitle);
//        Item itemMenu = (Item)mItems.get(i);
//        itemMenu.name = newTitle;
//        mItems.set(i, itemMenu);
    }

    private class Item {
        public String name;
        public final int drawableId;

        Item(String nameId, int drawableId) {
//            MainActivity mainActivity = (MainActivity) mContext;
//            String nameString = mainActivity.getResources().getString(nameId);
            this.name = nameId;
            this.drawableId = drawableId;
        }
    }
}