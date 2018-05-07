package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.google.android.gms.analytics.ecommerce.Promotion;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CategoryActivity;
import com.indomaret.klikindomaret.activity.CategoryHeroActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.views.ProportionalImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ViewPagerAdapter extends PagerAdapter {
    private Activity activity;
    private JSONArray heroBanner;
    private JSONObject bannerObject;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private Intent intent;

    public ViewPagerAdapter(Activity activity, JSONArray heroBanner) {
        this.activity = activity;
        this.heroBanner = heroBanner;
    }

    @Override
    public int getCount() {
        return heroBanner.length();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.pager_item, container, false);

        ProportionalImageView imageView = (ProportionalImageView) itemView.findViewById(R.id.img_pager_item);
        ImageView imgGif = (ImageView) itemView.findViewById(R.id.img_gif);

        try {
            bannerObject = heroBanner.getJSONObject(position);
            if (bannerObject.getString("AssetImgUrl").replace(" ","%20").toLowerCase().contains(".gif")){
                imgGif.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);

                Glide.with(getApplicationContext()).load(API.getInstance().getAssetsUrl() + bannerObject.getString("AssetImgUrl").replace(" ","%20")).into(imgGif);
            }else{
                imgGif.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);

                imageView.setImageUrl(API.getInstance().getAssetsUrl() + bannerObject.getString("AssetImgUrl").replace(" ","%20"), imageLoader);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        imgGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject heroObject = heroBanner.getJSONObject(position);

                    Promotion promotion = new Promotion()
                            .setId(heroObject.getString("PromoID"))
                            .setName(heroObject.getString("MetaTitle"))
                            .setCreative("")
                            .setPosition(heroObject.getString("Position"));
                    ProductAction promoClickAction = new ProductAction(Promotion.ACTION_CLICK);
                    HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                            .addPromotion(promotion)
                            .setProductAction(promoClickAction)
                            .setCategory("Promotions")
                            .setAction("click hero")
                            .setLabel("Promo Hero");

                    AppController application = (AppController) activity.getApplication();
                    Tracker t = application.getDefaultTracker();
                    t.send(builder.build());

                    if (heroObject.getString("TargetUrl").contains("search")){
                        String[] urlSplit = heroObject.getString("TargetUrl").split("=");
                        intent = new Intent(activity, CategoryActivity.class);
                        intent.putExtra("cat", "search");
                        intent.putExtra("promo", urlSplit[1]);
                        intent.putExtra("object", heroObject.toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }else if(heroObject.getString("TargetUrl").contains("content")){
                        intent = new Intent(activity, CategoryHeroActivity.class);
                        intent.putExtra("promo", heroObject.toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }else if(heroObject.getString("TargetUrl").contains("promo")){
                        intent = new Intent(activity, CategoryActivity.class);
                        intent.putExtra("cat", "promo");
                        intent.putExtra("promo", heroObject.toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }else {
                        intent = new Intent(activity, CategoryActivity.class);
                        intent.putExtra("cat", "promo");
                        intent.putExtra("promo", heroObject.toString());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });

        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            JSONObject heroObject = heroBanner.getJSONObject(position);

                            Promotion promotion = new Promotion()
                                    .setId(heroObject.getString("PromoID"))
                                    .setName(heroObject.getString("MetaTitle"))
                                    .setCreative("")
                                    .setPosition(heroObject.getString("Position"));
                            ProductAction promoClickAction = new ProductAction(Promotion.ACTION_CLICK);
                            HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                                    .addPromotion(promotion)
                                    .setProductAction(promoClickAction)
                                    .setCategory("Promotions")
                                    .setAction("click hero")
                                    .setLabel("Promo Hero");

                            AppController application = (AppController) activity.getApplication();
                            Tracker t = application.getDefaultTracker();
                            t.send(builder.build());

                            if (heroObject.getString("TargetUrl").contains("search")){
                                String[] urlSplit = heroObject.getString("TargetUrl").split("=");
                                intent = new Intent(activity, CategoryActivity.class);
                                intent.putExtra("cat", "search");
                                intent.putExtra("promo", urlSplit[1]);
                                intent.putExtra("object", heroObject.toString());
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }else if(heroObject.getString("TargetUrl").contains("content")){
                                intent = new Intent(activity, CategoryHeroActivity.class);
                                intent.putExtra("promo", heroObject.toString());
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }else if(heroObject.getString("TargetUrl").contains("promo")){
                                intent = new Intent(activity, CategoryActivity.class);
                                intent.putExtra("cat", "promo");
                                intent.putExtra("promo", heroObject.toString());
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }else {
                                intent = new Intent(activity, CategoryActivity.class);
                                intent.putExtra("cat", "promo");
                                intent.putExtra("promo", heroObject.toString());
                                activity.startActivity(intent);
                                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }
        );

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}