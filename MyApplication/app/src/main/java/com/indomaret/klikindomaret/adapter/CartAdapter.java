package com.indomaret.klikindomaret.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.analytics.ecommerce.Product;
import com.google.android.gms.analytics.ecommerce.ProductAction;
import com.indomaret.klikindomaret.API;
import com.indomaret.klikindomaret.R;
import com.indomaret.klikindomaret.activity.CartActivity;
import com.indomaret.klikindomaret.app.AppController;
import com.indomaret.klikindomaret.helper.Month;
import com.indomaret.klikindomaret.helper.SessionManager;
import com.indomaret.klikindomaret.views.NetworkImagesView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by USER on 4/16/2016.
 */
public class CartAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private SessionManager sessionManager;
    private Activity activity;
    private JSONArray products;
    private JSONArray stock;
    private List<String> plu = new ArrayList<>();

    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    private DecimalFormat df = new DecimalFormat("#,###");
    private String[] splitDate;
    private String[] splitDate2;
    private Month month = new Month();

    public CartAdapter(Activity activity, JSONArray products){
        this.activity = activity;
        this.products = products;
    }

    public CartAdapter(Activity activity, JSONArray products, JSONArray stock){
        this.activity = activity;
        this.products = products;
        this.stock = stock;
    }

    public CartAdapter(Activity activity, JSONArray products, List<String> plu){
        this.activity = activity;
        this.products = products;
        this.plu = plu;
    }

    @Override
    public int getCount() {
        return products.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return products.getJSONObject(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        sessionManager = new SessionManager(activity);

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.activity_cart_single_product_listview, null);

        TextView cartProductName = (TextView) convertView.findViewById(R.id.cart_single_product_name);
        TextView cartProductDateTicket = (TextView) convertView.findViewById(R.id.cart_single_product_date_ticket);
        TextView sendFrom = (TextView) convertView.findViewById(R.id.cart_single_product_send_from);
        TextView discount = (TextView) convertView.findViewById(R.id.cart_single_product_discount);
        TextView productColor = (TextView) convertView.findViewById(R.id.cart_single_product_color);
        TextView installment = (TextView) convertView.findViewById(R.id.cart_single_product_installment);
        TextView productPrice = (TextView) convertView.findViewById(R.id.cart_single_product_price);
        TextView initialPrice = (TextView) convertView.findViewById(R.id.cart_single_product_initial_price);
        initialPrice.setPaintFlags(initialPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        TextView totalPrice = (TextView) convertView.findViewById(R.id.total_price);
        TextView productQty = (TextView) convertView.findViewById(R.id.product_qty_stock);
        TextView greetingText = (TextView) convertView.findViewById(R.id.cart_single_greeting);
        TextView messageError = (TextView) convertView.findViewById(R.id.message_error);
        TextView messageInfo = (TextView) convertView.findViewById(R.id.message_info);
        TextView messageValidationPromo = (TextView) convertView.findViewById(R.id.message_validation_promo);
        RecyclerView listCoupon = (RecyclerView) convertView.findViewById(R.id.list_coupon);

        NetworkImagesView productImage = (NetworkImagesView) convertView.findViewById(R.id.cart_single_product);
        ImageView deleteItem = (ImageView) convertView.findViewById(R.id.delete_item);
        final EditText qtyProduct = (EditText) convertView.findViewById(R.id.product_quantity);

        LinearLayout productQtyContainer = (LinearLayout) convertView.findViewById(R.id.product_qty);
        LinearLayout linearInstallment = (LinearLayout) convertView.findViewById(R.id.linear_installment);
        LinearLayout linearMessageInfo = (LinearLayout) convertView.findViewById(R.id.linear_message_info);
        LinearLayout linearDeleteProduct = (LinearLayout) convertView.findViewById(R.id.linear_delete_product);
        RelativeLayout plusImage = (RelativeLayout) convertView.findViewById(R.id.qty_plus);
        RelativeLayout minusImage = (RelativeLayout) convertView.findViewById(R.id.qty_minus);
        RelativeLayout relativeCart = (RelativeLayout) convertView.findViewById(R.id.relative_cart);
        RecyclerView messageCountdown = (RecyclerView) convertView.findViewById(R.id.message_countdown);

        try {
            if (products.getJSONObject(position).getJSONArray("Coupons").length() > 0){
                listCoupon.setVisibility(View.VISIBLE);
                listCoupon.setHasFixedSize(true);
                listCoupon.setLayoutManager(new LinearLayoutManager(activity));
                CouponCartAdapter couponAdapter = new CouponCartAdapter(activity, products.getJSONObject(position).getJSONArray("Coupons"));
                listCoupon.setAdapter(couponAdapter);
            }else{
                listCoupon.setVisibility(View.GONE);
            }

//            if (products.getJSONObject(position).getJSONArray("Coupons").length() > 0){
//                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                relativeParams.setMargins(0, 0, 0, 15);
//                relativeCart.setLayoutParams(relativeParams);
//            }else{
//                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//                relativeParams.setMargins(0, 0, 0, 5);
//                relativeCart.setLayoutParams(relativeParams);
//            }

            if (products.getJSONObject(position).getJSONObject("Product").getBoolean("IsInstallment")){
                linearInstallment.setVisibility(View.GONE);
            }else{
                linearInstallment.setVisibility(View.VISIBLE);
                messageError.setText("Produk ini tidak dapat menggunakan pembayaran cicilan");
            }

            if(products.getJSONObject(position).getBoolean("IsTicket")){
                productImage.setImageUrl(products.getJSONObject(position).getJSONObject("ProductTicket").getString("ImageDetailURL"), imageLoader);
            }else{
                productImage.setImageUrl(products.getJSONObject(position).getJSONObject("Product").getString("ImageThumb"), imageLoader);
            }

            if (products.getJSONObject(position).getJSONObject("Product").getBoolean("IsVirtual") == true){
                if (products.getJSONObject(position).getBoolean("IsTicket")){
                    cartProductName.setText(products.getJSONObject(position).getJSONObject("ProductTicket").getString("ShowName")
                            + " [" + products.getJSONObject(position).getJSONObject("ProductTicket").getString("Class") + "]");

                    if (products.getJSONObject(position).getJSONObject("ProductTicket").getString("ProductType").toLowerCase().equals("y")){
                        cartProductDateTicket.setVisibility(View.VISIBLE);

                        splitDate = products.getJSONObject(position).getString("ShippingDate").split("T");
                        splitDate2 = splitDate[0].split("-");
                        month = new Month();
                        String date = splitDate2[2] + " " + month.getMonth(splitDate2[1]) + " " + splitDate2[0];

                        productImage.setImageUrl(products.getJSONObject(position).getJSONObject("ProductTicket").getString("ImageDetailURL"), imageLoader);

                        cartProductDateTicket.setText("Kedatangan : " + date);
                    }else{
                        cartProductDateTicket.setVisibility(View.GONE);
                    }
                }else{
                    cartProductName.setText(products.getJSONObject(position).getJSONObject("Product").getString("Description"));
                }
            } else {
                if (products.getJSONObject(position).getJSONObject("Product").getString("Title").equals("null")
                        || products.getJSONObject(position).getJSONObject("Product").getString("Title").equals("")){
                    cartProductName.setText(products.getJSONObject(position).getJSONObject("Product").getString("Name"));
                } else {
                    cartProductName.setText(products.getJSONObject(position).getJSONObject("Product").getString("Title"));
                }
            }

            int qtyProducts = products.getJSONObject(position).getInt("Quantity");
            qtyProduct.setText(qtyProducts+"");

            if(products.getJSONObject(position).getBoolean("IsPair")||(products.getJSONObject(position).getBoolean("IsParcel")
                    && products.getJSONObject(position).getJSONArray("DetailParcel").length() == 0)){
                linearDeleteProduct.setVisibility(View.INVISIBLE);
                plusImage.setVisibility(View.INVISIBLE);
                minusImage.setVisibility(View.INVISIBLE);
                sendFrom.setVisibility(View.GONE);
                totalPrice.setVisibility(View.INVISIBLE);
                productPrice.setVisibility(View.INVISIBLE);
                initialPrice.setVisibility(View.INVISIBLE);
                discount.setVisibility(View.INVISIBLE);
                installment.setVisibility(View.INVISIBLE);
            } else {
                linearDeleteProduct.setVisibility(View.VISIBLE);
                plusImage.setVisibility(View.VISIBLE);
                minusImage.setVisibility(View.VISIBLE);
                sendFrom.setVisibility(View.GONE);
                totalPrice.setVisibility(View.VISIBLE);
                productPrice.setVisibility(View.VISIBLE);
            }

            if(products.getJSONObject(position).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("store")){
                sendFrom.setText("Dikirim dari Toko");
            } else if(products.getJSONObject(position).getJSONObject("Product").getString("ProductFlag").toLowerCase().equals("plaza")){
                if (products.getJSONObject(position).getBoolean("IsTicket")){
                    sendFrom.setText("Dikirim via Elektrik");
                }else{
                    if(products.getJSONObject(position).getJSONObject("Product").getString("Flag_Produk").substring(0,1).equals("B")){
                        sendFrom.setText("Dikirim dari Penjual");
                    } else if(products.getJSONObject(position).getJSONObject("Product").getString("Flag_Produk").substring(0,1).equals("D")){
                        sendFrom.setText("Dikirim dari Gudang");
                    }
                }
            }

            Double discountProduct = products.getJSONObject(position).getDouble("Discount");
            Double initialPrices = products.getJSONObject(position).getDouble("Price");
            Double subTotal = products.getJSONObject(position).getDouble("SubTotal");
            Double validPrice = products.getJSONObject(position).getDouble("PriceWithDiscount");
            String prodId = products.getJSONObject(position).getString("ProductID");

            for (int i = 0; i<products.length(); i++){
                if (products.getJSONObject(i).getBoolean("IsPair")){
                    if (products.getJSONObject(i).getString("ProductIDPairHeader").equals(prodId)){
                        validPrice = products.getJSONObject(position).getDouble("PriceWithDiscount")+products.getJSONObject(i).getDouble("PriceWithDiscount");
                    }
                }
            }

            if(discountProduct == 0.0){
                initialPrice.setVisibility(View.GONE);
            } else {
                initialPrice.setVisibility(View.VISIBLE);
            }

            productPrice.setText("Rp " + df.format(validPrice).replace(",","."));
            initialPrice.setText("Rp " + df.format(initialPrices).replace(",","."));
            totalPrice.setText("Rp " + df.format(subTotal).replace(",","."));

            if(!products.getJSONObject(position).getJSONObject("Product").getString("Color").equals("null")){
                productColor.setText("Warna : " + products.getJSONObject(position).getJSONObject("Product").getString("Color"));
                productColor.setVisibility(View.VISIBLE);
            } else {
                productColor.setVisibility(View.GONE);
            }

            if(stock != null){
                String plu = products.getJSONObject(position).getJSONObject("Product").getString("PLU");
                JSONArray listProduct = stock.getJSONObject(0).getJSONObject("TrxLogModel").getJSONArray("ListProduct");

                for (int i=0; i < listProduct.length(); i++){
                    if(listProduct.getJSONObject(i).getString("PLU").equals(plu)){
                        int qtyStock = listProduct.getJSONObject(i).getInt("QtyStok");
                        int qtyBeli = listProduct.getJSONObject(i).getInt("QtyBeli");

                        if(qtyStock == 0 || qtyBeli > qtyStock){
                            if(qtyStock < 0){
                                productQty.setText("Persediaan Kosong");
                            } else {
                                productQty.setText("Sisa stok " + qtyStock);
                            }

                            ((CartActivity) activity).setStockEmpty(true);
                            System.out.println("Is Stock Empty Stock Adapter = "+true);
                            productQtyContainer.setVisibility(View.VISIBLE);
                        } else {
                            productQtyContainer.setVisibility(View.GONE);
                        }
                    }
                }
            }

            if(plu.size() > 0){
                String plus = products.getJSONObject(position).getJSONObject("Product").getString("PLU");
                int qtyBuy = products.getJSONObject(position).getInt("Quantity");
                productQtyContainer.setVisibility(View.GONE);


                for (int i=0; i<plu.size(); i++){
                    String[] pluStock = plu.get(i).split("\\|");
                    System.out.println("position " + position+" ==> " +pluStock[0]+" vs "+plus);

                    if(pluStock[0].replace("[", "").replace("]", "").equals(plus)){
                        String sisaStock = pluStock[1].replace("\"","");

                        if(Integer.parseInt(sisaStock) == 0 || Integer.parseInt(sisaStock) < qtyBuy){
                            if(Integer.parseInt(sisaStock) <= 0){
                                productQty.setText("Persediaan Kosong");
                            } else {
                                productQty.setText("Sisa stok " + sisaStock);
                            }
                            ((CartActivity) activity).setStockEmpty(true);
                            System.out.println("Is Stock Empty PLU Adapter = " + true);
                            productQtyContainer.setVisibility(View.VISIBLE);
                        }
                    }else {
                        ((CartActivity) activity).setStock(true);
                        System.out.println("Is Stock Empty PLU Adapter = " + false);
                    }
                }
            }

            String stringBadgeTag = products.getJSONObject(position).getJSONObject("Product").getString("BadgeTag");
            boolean isVIsible = false;

            if (!stringBadgeTag.equals("") && stringBadgeTag != null){
                JSONArray badgeTag = new JSONArray(stringBadgeTag);

                if (badgeTag.length() > 0){
                    int badgeType = 100;

                    for(int i=0; i<badgeTag.length(); i++){
                        if (badgeTag.getJSONObject(i).getInt("BadgeType") < badgeType){
                            badgeType = badgeTag.getJSONObject(i).getInt("BadgeType");

                            if (badgeTag.getJSONObject(i).getString("BadgeDesc").toLowerCase().equals("tebus murah")){
                                discount.setVisibility(View.GONE);
                            }else{
                                if (products.getJSONObject(position).getBoolean("IsDiscount")){
                                    discount.setVisibility(View.VISIBLE);
                                    discount.setText(badgeTag.getJSONObject(i).getString("BadgeDesc"));
                                }else{
                                    discount.setVisibility(View.GONE);
                                    messageCountdown.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                } else {
                    discount.setVisibility(View.GONE);
                    messageCountdown.setVisibility(View.GONE);
                }
            }else {
                discount.setVisibility(View.GONE);
                messageCountdown.setVisibility(View.GONE);
            }

            if (products.getJSONObject(position).getString("ProductExpired") != null && products.getJSONObject(position).getString("ProductExpired").length() > 0){
                isVIsible = true;
            }else{
                isVIsible = false;
                messageCountdown.setVisibility(View.GONE);
            }

            if (products.getJSONObject(position).getString("ItemMessage") == null ||
                    products.getJSONObject(position).getString("ItemMessage").toLowerCase().equals("null") ||
                    products.getJSONObject(position).getString("ItemMessage").equals("")){
                messageValidationPromo.setVisibility(View.GONE);
                linearMessageInfo.setVisibility(View.GONE);
            }else{
                if (products.getJSONObject(position).getString("ItemMessage").toLowerCase().contains("maksimal")
                        || products.getJSONObject(position).getString("ItemMessage").toLowerCase().contains("kuota")){
                    messageValidationPromo.setVisibility(View.VISIBLE);
                    messageValidationPromo.setText(products.getJSONObject(position).getString("ItemMessage"));
                }else{
                    messageValidationPromo.setVisibility(View.GONE);
                    if (isVIsible){
                        linearMessageInfo.setVisibility(View.GONE);
                    }else{
                        linearMessageInfo.setVisibility(View.VISIBLE);
                        messageInfo.setText(products.getJSONObject(position).getString("ItemMessage"));
                    }
                }
            }

            if (isVIsible){
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final Date eventDate = dateFormat.parse(products.getJSONObject(position).getString("ProductExpired").replace("T", " "));
                    final Date currentDate = new Date();

                    if (!currentDate.after(eventDate)) {
                        messageCountdown.setVisibility(View.VISIBLE);

                        messageCountdown.setHasFixedSize(true);
                        messageCountdown.setLayoutManager(new LinearLayoutManager(activity));
                        messageCountdown.setAdapter(new CountDownAdapter(activity, products.getJSONObject(position).getString("ProductExpired").replace("T", " "), products.getJSONObject(position).getString("ID")));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if(products.getJSONObject(position).getBoolean("IsUseNote")){
                if(products.getJSONObject(position).getString("Notes").replace("|", "").length() == 0 ||
                        products.getJSONObject(position).getString("Notes").equals("null")){
                    greetingText.setText("Tulis Ucapan pada kartu");
                }else {
                    greetingText.setText("Tulis Ucapan pada kartu ✓");
                }

                greetingText.setVisibility(View.VISIBLE);
            }else{
                greetingText.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        plusImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CartActivity)activity).setButtonEnabled(true);
                        int qty = Integer.parseInt(qtyProduct.getText().toString());
                        qty = qty + 1;
                        qtyProduct.setText(""+ qty);
                        JSONObject item = new JSONObject();

                        try {
                            item.put("QtyLimit", 0);
                            item.put("QtyUpdated", qty);
                            item.put("PLU", products.getJSONObject(position).getJSONObject("Product").getString("PLU"));
                            item.put("CartItemId", products.getJSONObject(position).getString("ID"));
                            item.put("Promo", null);

                            ((CartActivity) activity).updateCartFromAdapter(products.getJSONObject(position).getJSONObject("Product").getString("PLU"), item, "plus");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        minusImage.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CartActivity)activity).setButtonEnabled(true);
                        int qty = Integer.parseInt(qtyProduct.getText().toString());
                        if (qty != 1){
                            qty = qty - 1;
                            qtyProduct.setText(""+ qty);
                        }

                        JSONObject item = new JSONObject();

                        try {
                            item.put("QtyLimit", 0);
                            item.put("QtyUpdated", qty);
                            item.put("PLU", products.getJSONObject(position).getJSONObject("Product").getString("PLU"));
                            item.put("CartItemId", products.getJSONObject(position).getString("ID"));
                            item.put("Promo", null);

                            ((CartActivity) activity).updateCartFromAdapter(products.getJSONObject(position).getJSONObject("Product").getString("PLU"), item, "minus");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        linearDeleteProduct.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String variant = "";

                        try {
                            if (products.getJSONObject(position).getJSONObject("Product").getString("Color").equals(null) ||
                                    products.getJSONObject(position).getJSONObject("Product").getString("Color").equals("null")){
                                variant = products.getJSONObject(position).getJSONObject("Product").getString("Flavour");
                            }else{
                                variant = products.getJSONObject(position).getJSONObject("Product").getString("Color");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                        alertDialogBuilder.setMessage("Apakah Anda ingin menghapus produk ini dari keranjang belanja Anda?");
                        alertDialogBuilder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {}
                        });

                        final String finalVariant = variant;
                        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                ((CartActivity) activity).setStock(true);
                                JSONObject deleteCartObject = new JSONObject();
                                int countProduct = 0;
                                int countQty = 0;

                                try {
                                    Product product =  new Product()
                                            .setId(products.getJSONObject(position).getJSONObject("Product").getString("ID"))
                                            .setName(products.getJSONObject(position).getJSONObject("Product").getString("Description"))
                                            .setBrand(products.getJSONObject(position).getJSONObject("Product").getJSONObject("ProductBrand").getString("Name"))
                                            .setVariant(finalVariant)
                                            .setPosition(1)
                                            .setCustomDimension(1, "Produk");

                                    ProductAction productAction = new ProductAction(ProductAction.ACTION_REMOVE)
                                            .setProductActionList("Cart produk");

                                    HitBuilders.ScreenViewBuilder builder = new HitBuilders.ScreenViewBuilder()
                                            .addProduct(product)
                                            .setProductAction(productAction);

                                    AppController application = (AppController) activity.getApplication();
                                    Tracker t = application.getDefaultTracker();
                                    t.setScreenName("delete product");
                                    t.send(builder.build());

                                    for (int i=0; i<products.length(); i++){
                                        if (products.getJSONObject(i).getString("ID").equals(products.getJSONObject(position).getString("ID"))){
                                            countProduct += 1;
                                            countQty += products.getJSONObject(i).getInt("Quantity");
                                        }
                                    }

                                    if (countProduct > 1){
                                        deleteCartObject.put("QtyLimit", 0);
                                        deleteCartObject.put("QtyUpdated", (countQty - Integer.parseInt(qtyProduct.getText().toString())));
                                        deleteCartObject.put("PLU", products.getJSONObject(position).getJSONObject("Product").getString("PLU"));
                                        deleteCartObject.put("CartItemId", products.getJSONObject(position).getString("ID"));
                                        deleteCartObject.put("Promo", null);

                                        ((CartActivity) activity).updateCartFromAdapter(products.getJSONObject(position).getJSONObject("Product").getString("PLU"), deleteCartObject, "delete");
                                    }else{
                                        deleteCartObject.put("CartId", sessionManager.getCartId());
                                        deleteCartObject.put("CartItemId", products.getJSONObject(position).getString("ID"));
                                        deleteCartObject.put("PackagePermalink", null);

                                        jsonPost(API.getInstance().getApiRemoveCartItem()+"?mfp_id="+sessionManager.getKeyMfpId()+"&regionID="+sessionManager.getRegionID(), deleteCartObject, "delete");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                }
        );

        greetingText.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Integer qtyText = 0;
                        String prodName = null;

                        try {
                            qtyText = products.getJSONObject(position).getInt("Quantity");

                            if(Integer.valueOf(qtyText) >= 1){
                                final JSONObject wordObject = new JSONObject();
                                LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                View writeReview = null;

                                if (writeReview == null) {
                                    writeReview = inflater.inflate(R.layout.activity_review_greeting_cards, null);
                                }

                                try {
                                    prodName = products.getJSONObject(position).getJSONObject("Product").getString("Name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                ScrollView scrollNotes = (ScrollView) writeReview.findViewById(R.id.scroll_view);
                                LinearLayout linearLayoutGreeting = (LinearLayout) writeReview.findViewById(R.id.LinearLayout_greeting);
                                LinearLayout linearLayout = (LinearLayout) writeReview.findViewById(R.id.linearlayout_text);
                                final TextView headerText = (TextView) writeReview.findViewById(R.id.header_review);
                                Button cancelReview = (Button) writeReview.findViewById(R.id.cancel_review);
                                Button sendReview = (Button) writeReview.findViewById(R.id.send_review);
                                EditText editText = null;
                                TextView titleText, maks;
                                ViewGroup.LayoutParams paramSroll = scrollNotes.getLayoutParams();
                                ViewGroup.LayoutParams paramLayout = linearLayoutGreeting.getLayoutParams();

                                final List<EditText> editList = new ArrayList<EditText>();
                                InputFilter[] fArray = new InputFilter[1];
                                fArray[0] = new InputFilter.LengthFilter(200);

                                headerText.setText("Tulis ucapan yang anda inginkan pada kartu ucapan");
//
//                                if (qtyText > 1){
//                                    paramLayout.height = 750;
//                                    paramSroll.height = 500;
//
//                                    linearLayoutGreeting.setLayoutParams(paramLayout);
//                                    scrollNotes.setLayoutParams(paramSroll);
//                                }

                                for(int i=0; i<qtyText; i++){
                                    titleText = new TextView(activity);
                                    titleText.setText(prodName + " "+ (i+1));
                                    titleText.setPadding(0, 20, 0, 0);
                                    titleText.setTypeface(Typeface.DEFAULT_BOLD);

                                    editText = new EditText(activity);
                                    editText.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                                    editText.setHeight(280);
                                    editText.setBackgroundResource(R.drawable.card_product_style_1);
                                    editText.setPadding(5, 5, 5, 5);
                                    editText.setFilters(fArray);
                                    editText.setHint("Tulis pesan disini");
                                    editText.setGravity(Gravity.START);

                                    final EditText finalEditText = editText;
                                    editText.addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                        @Override
                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                                            if (s.length() < 1 || start >= s.length() || start < 0)
                                                return;

                                            if (s.subSequence(start, start + 1).toString().equalsIgnoreCase("\n")) {
                                                String s_text = start > 0 ? s.subSequence(0, start).toString() : "";
                                                s_text += start < s.length() ? s.subSequence(start + 1, s.length()).toString() : "";
                                                finalEditText.setText(s_text);

                                                finalEditText.setSelection(s_text.length());
                                            }
                                        }

                                        @Override
                                        public void afterTextChanged(Editable s) {}
                                    });

                                    maks = new TextView(activity);
                                    maks.setText("maks. 200 karakter");
                                    maks.setPadding(0, 0, 0, 0);
                                    maks.setGravity(Gravity.RIGHT);
                                    maks.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));

                                    linearLayout.addView(titleText);
                                    linearLayout.addView(editText);
                                    linearLayout.addView(maks);
                                    editList.add(editText);
                                }

                                if(products.getJSONObject(position).getString("Notes").equals("null") || products.getJSONObject(position).getString("Notes").equals("")){
                                    editText.setText("");
                                }else {
                                    String[] splitText = products.getJSONObject(position).getString("Notes").split("\\|");
                                    for(int j=0; j<splitText.length; j++){
                                        for(int i=0; i<editList.size(); i++){
                                            if(i == j) editList.get(i).setText(splitText[j]);
                                        }
                                    }
                                }

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                alertDialogBuilder.setView(writeReview);
                                final AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();

                                cancelReview.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                alertDialog.hide();
                                            }
                                        }
                                );

                                final StringBuilder builder = new StringBuilder();
                                sendReview.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    wordObject.put("cartId", products.getJSONObject(position).getString("ID"));

                                                    for(int i=0; i<editList.size(); i++){
                                                        if(i == 0){
                                                            if(editList.get(i).getText().length() == 0) builder.append("");
                                                            else builder.append(editList.get(i).getText().toString());
                                                        }else if(i > 0){
                                                            if(editList.get(i).getText().length() == 0) builder.append("|" + "");
                                                            else builder.append("|" + editList.get(i).getText().toString());
                                                        }

                                                        String words = builder.toString();
                                                        wordObject.put("Notes", words);
                                                    }

                                                    String url = API.getInstance().getApiSaveNote()+"?mfp_id="+sessionManager.getKeyMfpId();
                                                    ((CartActivity) activity).jsonPost(url, wordObject, "word");
                                                    alertDialog.hide();
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                );
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        return convertView;
    }

    // POST OBJECT
    public void jsonPost(String urlJsonObj, JSONObject jsonObject, final String type){
        System.out.println("cart adapter url = " + urlJsonObj);
        System.out.println("cart adapter object = " + jsonObject);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, jsonObject,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        if (response == null || response.length() == 0){
                            Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
                        }else{
                            if (type.equals("delete")){
                                deleteCartResponse(response);
                            }else if (type.equals("update")){

                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void jsonArrayGet(String urlJsonObj, final String type){
        System.out.println("region " + urlJsonObj);

        JsonArrayRequest jsonArrayReq = new JsonArrayRequest(urlJsonObj,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            if(response.getInt(0) > 0){
                                ((CartActivity) activity).deleteItemCartResponse();
                            } else {
                                sessionManager.setCartId("00000000-0000-0000-0000-000000000000");

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                                alertDialogBuilder.setMessage("Keranjang Belanja Anda kosong.");
                                alertDialogBuilder.setNegativeButton("Mulai Belanja", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        activity.finish();
                                        activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                    }
                                });

                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.setCanceledOnTouchOutside(false);
                                alertDialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, "Gagal terkoneksi server", Toast.LENGTH_LONG).show();
            }
        }, activity);

        AppController.getInstance().addToRequestQueue(jsonArrayReq);
    }

    public void deleteCartResponse(JSONObject response){
        System.out.println("delete = " + response);

        try {
            if(response.getString("Message").equals("success")){
                String url = API.getInstance().getCartTotal()
                        +"?cartId=" + sessionManager.getCartId()
                        +"&customerId=" + sessionManager.getUserID()
                        +"&mfp_id=" + sessionManager.getKeyMfpId();

                jsonArrayGet(url, "cart");
            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(response.getString("Message"));
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
