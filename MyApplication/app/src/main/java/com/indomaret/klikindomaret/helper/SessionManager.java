package com.indomaret.klikindomaret.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.List;

public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();
    SharedPreferences pref;

    Editor editor;
    Context context;

    private static final String PREF_NAME = "com.indomaret.klikindomaret";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_LAST_USER_NAME = "last_user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_NOTIFICATIONS = "notifications";
    private static final String KEY_DEVICE_TOKEN = "device_token";
    private static final String KEY_MFP_ID = "mfp_id";
    private static final String KEY_INTRO = "intro";
    private static final String KEY_RESPONSE_ID = "response_id";
    private static final String KEY_REGION = "region_10";
    private static final String KEY_REGION_NAME = "region_name_10";
    private static final String KEY_CART_ID = "cartd_id";
    private static final String KEY_EMPTY_PRODUCT = "empty_prod";
    private static final String KEY_TICKET_KAI = "ticket_kai";
    private static final String KEY_CURRENT_SPINNER = "current_spinner";
    private static final String KEY_EDIT_SPINNER = "edit_spinner";
    private static final String KEY_WISHLIST = "wishlist";
    private static final String KEY_USERDATA_KAI = "userdata_ticket_kai";
    private static final String KEY_PLAZA_ADDRESS_ID = "key_plaza_address_id";
    private static final String KEY_STORE_PLAZA = "key_store_plaza";
    private static final String KEY_ADDRESS_ID_PLAZA = "key_address_id_plaza";
    private static final String KEY_PLAZA_SHIPPING = "key_plaza_shipping";
    private static final String KEY_DEFAULT_ADDRESS_PLAZA = "key_default_address_plaza";
    private static final String KEY_STORE_CODE_PLAZA = "key_store_code_plaza";
    private static final String KEY_IPP_COVER = "key_ipp_cover";
    private static final String KEY_NEW_STORE_PLAZA = "key_new_store_plaza";
    private static final String KEY_LAST_STORE = "key_last_store";
    private static final String KEY_LAST_STORE_PLAZA = "key_last_store_plaza";
    private static final String KEY_TOKEN_COOKIE = "key_token_cookie";
    private static final String KEY_STOCK = "key_stok";
    private static final String KEY_TOTAL_TRANSACTION = "key_total_transaction";
    private static final String KEY_TOTAL_PRICE = "key_total_price";
    private static final String KEY_TOTAL_VOUCHER = "key_total_voucher";
    private static final String KEY_TOTAL_COUPON = "key_total_coupon";
    private static final String KEY_TOTAL_DISCOUNT = "key_total_discount";
    private static final String KEY_TOTAL_SHIPPING_COST = "key_total_shipping_cost";
    private static final String KEY_PREPARATION_TIME = "key_preparation_time";
    private static final String KEY_COUPON_LIST = "key_coupon_list";

    private static final String KEY_DATE = "key_date";
    private static final String KEY_TIME = "key_time";
    private static final String KEY_EXPIRED_DATE = "key_expired_date";
    private static final String KEY_STORE = "key_store";
    private static final String KEY_SHIPPING = "key_shipping";
    private static final String KEY_SHIPPING_PLAZA = "key_shipping_plaza";

    private static final String KEY_CATEGORIES = "com.indomaret.klikindomaret.categories";
    private static final String KEY_SINGLE_CATEGORIES = "com.indomaret.klikindomaret.singlecategory";

    private static final String KEY_COUNT_PASSENGER = "passenger";
    private static final String KEY_COUNT_NOTIF = "notif";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public void setUserID(String userID){
        editor.putString(KEY_USER_ID, userID);
        editor.commit();
    }

    public String getUserID(){
        return pref.getString(KEY_USER_ID, null);
    }

    public void setResponseId(String responseID){
        editor.putString(KEY_RESPONSE_ID, responseID);
        editor.commit();
    }

    public String getResponseId(){
        return pref.getString(KEY_RESPONSE_ID, null);
    }

    public void setLastUsername(String username){
        editor.putString(KEY_LAST_USER_NAME, username);
        editor.commit();
    }

    public String getLastUsername(){
        return pref.getString(KEY_LAST_USER_NAME, null);
    }

    public void setUsername(String username){
        editor.putString(KEY_USER_NAME, username);
        editor.commit();
    }

    public String getUsername(){
        return pref.getString(KEY_USER_NAME, null);
    }

    public void setUserEmail(String email){
        editor.putString(KEY_USER_EMAIL, null);
        editor.commit();
    }

    public String getUserEmail(){
        return pref.getString(KEY_USER_EMAIL, null);
    }

    public void setDeviceToken(String deviceToken){
        editor.putString(KEY_DEVICE_TOKEN, deviceToken);
        editor.commit();
    }

    public String getDeviceToken(){
        return pref.getString(KEY_DEVICE_TOKEN, null);
    }

    public void setKeyMfpId (String mfpID){
        editor.putString(KEY_MFP_ID, mfpID);
        editor.commit();
    }

    public String getKeyMfpId(){
        return pref.getString(KEY_MFP_ID, null);
    }

    public void setIntro(){
        editor.putBoolean(KEY_INTRO, true);
        editor.commit();
    }

    public boolean getIntro(){
        return  pref.getBoolean(KEY_INTRO, false);
    }

    public void addNotification(String notification) {
        String oldNotifications = getNotifications();
        oldNotifications = notification;

        editor.putString(KEY_NOTIFICATIONS, oldNotifications);
        editor.commit();
    }

    public String getNotifications() {
        return pref.getString(KEY_NOTIFICATIONS, null);
    }

    public void saveCategories(String categories){
        editor.putString(KEY_CATEGORIES, categories);
        editor.commit();
    }

    public String getCategories(){
        return pref.getString(KEY_CATEGORIES, null);
    }

    public void saveSingleCategory(String category){
        editor.putString(KEY_SINGLE_CATEGORIES, category);
        editor.commit();
    }

    public String getSingleCategory(){
        return pref.getString(KEY_SINGLE_CATEGORIES, null);
    }

    public void setRegionID(String regionID){
        editor.putString(KEY_REGION, regionID);
        editor.commit();
    }

    public String getRegionID(){
        return pref.getString(KEY_REGION, null);
    }

    public void setRegionName(String regionName){
        editor.putString(KEY_REGION_NAME, regionName);
        editor.commit();
    }

    public String getRegionName(){
        return pref.getString(KEY_REGION_NAME, null);
    }

    public void setCartId(String cartId){
        editor.putString(KEY_CART_ID, cartId);
        editor.commit();
    }

    public String getCartId(){
        return pref.getString(KEY_CART_ID, null);
    }

    public void setEmptyProd(String emptyProd){
        editor.putString(KEY_EMPTY_PRODUCT, emptyProd);
        editor.commit();
    }

    public String getEmptyProd(){
        return pref.getString(KEY_EMPTY_PRODUCT, null);
    }


    public void setkeyDate(String date){
        editor.putString(KEY_DATE, date);
        editor.commit();
    }

    public String getKeyDate(){
        return pref.getString(KEY_DATE, null);
    }

    public void setKeyTime(String time){
        editor.putString(KEY_TIME, time);
        editor.commit();
    }

    public String getKeyTime(){
        return pref.getString(KEY_TIME, null);
    }

    public void setKeyExpiredDate(String time){
        editor.putString(KEY_EXPIRED_DATE, time);
        editor.commit();
    }

    public String getKeyExpiredDate(){
        return pref.getString(KEY_EXPIRED_DATE, "");
    }

    public void setkeyStore(String store){
        editor.putString(KEY_STORE, store);
        editor.commit();
    }

    public String getKeyStore(){
        return pref.getString(KEY_STORE, null);
    }


    public void setKeyShippingPlaza(Integer shipping){
        editor.putInt(KEY_SHIPPING_PLAZA, shipping);
        editor.commit();
    }

    public Integer getKeyShippingPlaza(){
        return pref.getInt(KEY_SHIPPING_PLAZA, 0);
    }

    public void setKeyCuurentSpinner(Integer position){
        editor.putInt(KEY_CURRENT_SPINNER, position);
        editor.commit();
    }

    public Integer getKeyCuurentSpinner(){
        return pref.getInt(KEY_CURRENT_SPINNER, 0);
    }

    public void setKeyShipping(boolean shipping){
        editor.putBoolean(KEY_SHIPPING, shipping);
        editor.commit();
    }

    public boolean getKeyShipping(){
        return pref.getBoolean(KEY_SHIPPING, false);
    }

    public void setKeyEditSpinner(boolean edit){
        editor.putBoolean(KEY_EDIT_SPINNER, edit);
        editor.commit();
    }

    public boolean getKeyEditSpinner(){
        return pref.getBoolean(KEY_EDIT_SPINNER, false);
    }

    public void setKeyWishlist(String edit){
        editor.putString(KEY_WISHLIST, edit);
        editor.commit();
    }

    public String getKeyWishlist(){
        return pref.getString(KEY_WISHLIST, "");
    }

    public void setKeyTicketKai(String ticketKai){
        editor.putString(KEY_TICKET_KAI, ticketKai);
        editor.commit();
    }

    public String getKeyTicketKai(){ return pref.getString(KEY_TICKET_KAI, null);}

    public void setKeyCountPassenger(String passenger){
        editor.putString(KEY_COUNT_PASSENGER, passenger);
        editor.commit();
    }

    public String getKeyCountPassenger(){
        return pref.getString(KEY_COUNT_PASSENGER, null);
    }

    public void setKeyCountNotif(String passenger){
        editor.putString(KEY_COUNT_NOTIF, passenger);
        editor.commit();
    }

    public String getKeyCountNotif(){
        return pref.getString(KEY_COUNT_NOTIF, null);
    }

    public void setKeyUserDataKai(String ticketKai){
        editor.putString(KEY_USERDATA_KAI, ticketKai);
        editor.commit();
    }

    public String getKeyUserDataKai(){ return pref.getString(KEY_USERDATA_KAI, null);}

    public void setKeyPlazaAddressId(String searchKey){
        editor.putString(KEY_PLAZA_ADDRESS_ID, searchKey);
        editor.commit();
    }

    public String getKeyPlazaAddressId(){ return pref.getString(KEY_PLAZA_ADDRESS_ID, null);}

    public void setKeyStorePlaza(String searchKey){
        editor.putString(KEY_STORE_PLAZA, searchKey);
        editor.commit();
    }

    public String getKeyStorePlaza(){ return pref.getString(KEY_STORE_PLAZA, null);}

    public void setKeyAddressIdPlaza(String searchKey){
        editor.putString(KEY_ADDRESS_ID_PLAZA, searchKey);
        editor.commit();
    }

    public String getKeyAddressIdPlaza(){ return pref.getString(KEY_ADDRESS_ID_PLAZA, null);}

    public void setKeyPlazaShipping(boolean edit){
        editor.putBoolean(KEY_PLAZA_SHIPPING, edit);
        editor.commit();
    }

    public boolean getKeyPlazaShipping(){
        return pref.getBoolean(KEY_PLAZA_SHIPPING, false);
    }

    public void setKeyDefaultAddressPlaza(String searchKey){
        editor.putString(KEY_DEFAULT_ADDRESS_PLAZA, searchKey);
        editor.commit();
    }

    public String getKeyDefaultAddressPlaza(){ return pref.getString(KEY_DEFAULT_ADDRESS_PLAZA, "");}

    public void setKeyStoreCodePlaza(String searchKey){
        editor.putString(KEY_STORE_CODE_PLAZA, searchKey);
        editor.commit();
    }

    public String getKeyStoreCodePlaza(){ return pref.getString(KEY_STORE_CODE_PLAZA, "");}

    public void setKeyIppCover(String edit){
        editor.putString(KEY_IPP_COVER, edit);
        editor.commit();
    }

    public String getKeyIppCover(){
        return pref.getString(KEY_IPP_COVER, "");
    }

    public void setKeyLastStorePlaza(String edit){
        editor.putString(KEY_LAST_STORE_PLAZA, edit);
        editor.commit();
    }

    public String getKeyLastStorePlaza(){
        return pref.getString(KEY_LAST_STORE_PLAZA, "");
    }

    public void setKeyTokenCookie(String edit){
        editor.putString(KEY_TOKEN_COOKIE, edit);
        editor.commit();
    }

    public String getKeyTokenCookie(){
        return pref.getString(KEY_TOKEN_COOKIE, "");
    }

    public void setKeyStock(boolean stock) {
        editor.putBoolean(KEY_STOCK, stock);
        editor.commit();
    }

    public boolean getKeyStock(){
        return pref.getBoolean(KEY_STOCK, false);
    }

    public void setKeyTotalTransaction(Integer total) {
        editor.putInt(KEY_TOTAL_TRANSACTION, total);
        editor.commit();
    }

    public Integer getKeyTotalTransaction(){
        return pref.getInt(KEY_TOTAL_TRANSACTION, 0);
    }

    public void setKeyTotalPrice(Integer total) {
        editor.putInt(KEY_TOTAL_PRICE, total);
        editor.commit();
    }

    public Integer getKeyTotalPrice(){
        return pref.getInt(KEY_TOTAL_PRICE, 0);
    }

    public void setKeyTotalVoucher(Integer total) {
        editor.putInt(KEY_TOTAL_VOUCHER, total);
        editor.commit();
    }

    public Integer getKeyTotalVoucher(){
        return pref.getInt(KEY_TOTAL_VOUCHER, 0);
    }

    public void setKeyTotalCoupon(Integer total) {
        editor.putInt(KEY_TOTAL_COUPON, total);
        editor.commit();
    }

    public Integer getKeyTotalCoupon(){
        return pref.getInt(KEY_TOTAL_COUPON, 0);
    }

    public void setKeyTotalDiscount(Integer total) {
        editor.putInt(KEY_TOTAL_DISCOUNT, total);
        editor.commit();
    }

    public Integer getKeyTotalDiscount(){
        return pref.getInt(KEY_TOTAL_DISCOUNT, 0);
    }

    public void setKeyTotalShippingCost(Integer total) {
        editor.putInt(KEY_TOTAL_SHIPPING_COST, total);
        editor.commit();
    }

    public Integer getKeyTotalShippingCost(){
        return pref.getInt(KEY_TOTAL_SHIPPING_COST, 0);
    }

    public void setKeyPreparationTime(int total) {
        editor.putInt(KEY_PREPARATION_TIME, total);
        editor.commit();
    }

    public Integer getKeyPreparationTime(){
        return pref.getInt(KEY_PREPARATION_TIME, 0);
    }

    public void setKeyCouponList(String total) {
        editor.putString(KEY_COUPON_LIST, total);
        editor.commit();
    }

    public String getKeyCouponList(){
        return pref.getString(KEY_COUPON_LIST, "");
    }

    public void setKeyLastStore(String edit){
        editor.putString(KEY_LAST_STORE, edit);
        editor.commit();
    }

    public String getKeyLastStore(){
        return pref.getString(KEY_LAST_STORE, "");
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}
