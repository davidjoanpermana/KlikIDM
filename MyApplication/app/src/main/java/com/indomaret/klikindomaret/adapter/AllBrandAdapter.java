package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER on 5/20/2016.
 */
public class AllBrandAdapter extends BaseAdapter implements SectionIndexer{
    private LayoutInflater inflater;
    private Activity activity;
    private List<JSONObject> brandList;
    private JSONObject brandObject;
    private int[] mSectionIndices;
    private Character[] mSectionLetters;

    private TextView brandName;

    public AllBrandAdapter(Activity activity, List<JSONObject> brandList){
        this.activity = activity;
        this.brandList = brandList;
        mSectionIndices = getSectionIndices();
        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<>();
        brandObject = brandList.get(1);
        char lastFirstChar = '#';

        try {
            if(brandObject.getString("Name").length() > 0){
                char brandLastChar = brandObject.getString("Name").charAt(0);

                if(Character.isLetter(brandLastChar)){
                    lastFirstChar = brandLastChar;
                } else if(Character.isDigit(brandLastChar)){
                    lastFirstChar = '#';
                }

                sectionIndices.add(0);

                for (int i = 1; i < brandList.size(); i++) {
                    char lastChar = '#';

                    if(Character.isLetter(brandList.get(i).getString("Name").charAt(0))){
                        lastChar = brandList.get(i).getString("Name").charAt(0);
                    } else if(Character.isDigit(lastChar = brandList.get(i).getString("Name").charAt(0))){
                        lastChar = '#';
                    }

                    if (lastChar != lastFirstChar) {
                        lastFirstChar = lastChar;
                        sectionIndices.add(i);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }

        return sections;
    }

    private Character[] getSectionLetters() {
        Character[] letters = new Character[mSectionIndices.length];

        try{
            for (int i = 0; i < mSectionIndices.length; i++) {
                char thisChar = brandList.get(mSectionIndices[i]).getString("Name").charAt(0);

                if(Character.isLetter(thisChar)){
                    letters[i] = thisChar;
                } else if(Character.isDigit(thisChar)){
                    letters[i] = '#';
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return letters;
    }

    @Override
    public int getCount() {
        return brandList.size();
    }

    @Override
    public Object getItem(int position) {
        return brandList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.brand_single_item, null);

        brandName = (TextView) convertView.findViewById(R.id.brand_name);

        try {
            brandObject = brandList.get(position);
            brandName.setText(brandObject.getString("Name"));

            if(brandObject.getString("Name").length() == 1) {
                brandName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                brandName.setTextColor(Color.parseColor("#0079C2"));
                brandName.setTypeface(null, Typeface.BOLD);
            }else {
                brandName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
                brandName.setTextColor(Color.parseColor("#000000"));
                brandName.setTypeface(null, Typeface.NORMAL);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public Object[] getSections() {
        return mSectionLetters;
    }

    @Override
    public int getPositionForSection(int section) {
        if (mSectionIndices.length == 0) {
            return 0;
        }

        if (section >= mSectionIndices.length) {
            section = mSectionIndices.length - 1;
        } else if (section < 0) {
            section = 0;
        }

        return mSectionIndices[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        for (int i = 0; i < mSectionIndices.length; i++) {
            if (position < mSectionIndices[i]) {
                return i - 1;
            }
        }

        return mSectionIndices.length - 1;
    }
}
