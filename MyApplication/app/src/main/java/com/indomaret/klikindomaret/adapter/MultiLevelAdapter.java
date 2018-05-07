package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.data.BaseItem;
import com.indomaret.klikindomaret.data.DataProvider;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.LevelBeamView;

import org.json.JSONException;

import java.util.List;

import pl.openrnd.multilevellistview.ItemInfo;
import pl.openrnd.multilevellistview.MultiLevelListAdapter;

public class MultiLevelAdapter extends MultiLevelListAdapter {
    private Activity activity;
    private DataProvider dataProvider;
    private SessionManager sessionManager;

    public MultiLevelAdapter(Activity activity, DataProvider dataProvider){
        this.activity = activity;
        this.dataProvider = dataProvider;
        System.out.println(("Sysout" + dataProvider));

        sessionManager = new SessionManager(activity);
    }

    private class ViewHolder {
        TextView nameView;
        ImageView arrowView;
        LevelBeamView levelBeamView;
        RelativeLayout bgView;
    }

    @Override
    public List<?> getSubObjects(Object object) {
        return dataProvider.getSubItems((BaseItem) object);
    }

    @Override
    public boolean isExpandable(Object object) {
        return dataProvider.isExpandable((BaseItem) object);
    }

    @Override
    public View getViewForObject(final Object object, View convertView, ItemInfo itemInfo) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.data_item, null);

            viewHolder.nameView = (TextView) convertView.findViewById(R.id.dataItemName);
            viewHolder.arrowView = (ImageView) convertView.findViewById(R.id.dataItemArrow);
            viewHolder.levelBeamView = (LevelBeamView) convertView.findViewById(R.id.dataItemLevelBeam);
            viewHolder.bgView = (RelativeLayout) convertView.findViewById(R.id.bg_view);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameView.setText(((BaseItem) object).getName());

        if (itemInfo.isExpandable()) {
            viewHolder.arrowView.setVisibility(View.VISIBLE);
            viewHolder.arrowView.setImageResource(itemInfo.isExpanded() ?
                    R.drawable.arrow_up : R.drawable.arrow_down);
        } else {
            viewHolder.arrowView.setVisibility(View.GONE);
        }

        //set level background color
        viewHolder.levelBeamView.setLevel(itemInfo.getLevel());
        viewHolder.bgView.setBackgroundColor(getColor(getColorResIdForLevel(itemInfo.getLevel())));

        viewHolder.nameView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        sessionManager.saveSingleCategory(((BaseItem) object).getCategoryObject().toString());

                        try {
                            //((FilterActivity) activity).setCategoryId(((BaseItem) object).getCategoryObject().getString("ID"));
                            System.out.println("apakah " + ((BaseItem) object).getCategoryObject().getString("ID"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });

        return convertView;
    }

    private int getColor(int colorResId) {
        return ContextCompat.getColor(activity, colorResId);
    }

    private int getColorResIdForLevel(int level) {
        switch (level) {
            case 0:
                return R.color.level_0a;
            case 1:
                return R.color.level_1a;
            case 2:
                return R.color.level_2a;
            case 3:
                return R.color.level_3a;
            case 4:
                return R.color.level_4a;
            case 5:
                return R.color.level_5a;
            default:
                return R.color.level_defaulta;
        }
    }
}