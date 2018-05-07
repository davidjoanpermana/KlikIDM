package com.indomaret.klikindomaret;

/**
 * Created by madearyawisnuwardana on 3/1/16.
 */
public class API {
    private static API sInstance = null;
    private final String API_BASE_URL;
    private final String API_BASE_KAI_URL;
    private final String ASSETS_BASE_URL;
    //    private final String API_BASE_URL_STAGING;
    private final String WEB_BASE_URL;
    private final String CARA_MEMESAN_URL;
    private final String STATUS_PESANAN;
    private final String CATEGORY_URL;
    private final String CAKUPAN_KODE_POS_URL;
    private final String TANYA_JAWAB_URL;
    private final String TENTANG_KAMI_URL;
    private final String KEBIJAKAN_URL;
    private final String MEREK_PILIHAN_URL;
    private final String PROMO_BULAN_INI_URL;
    private final String LAYANAN_PELANGGAN;
    private final String KERANJANG_URL;
    private final String LOGIN_URL;
    private final String AKUN_URL;
    private final String KEBIJAKAN_PENGEMBALIAN_URL;
    private final String SYARAT_DAN_KETENTUAN;
    private final String SITEMAP_URL;
    private final String WISHLIST;
    private final String CHAT_BANTUAN;
    private final String PAYMENT;
    private final String API_GET_VIRTUAL;
    private final String API_GET_PAGGING_NO_CACHE;
    private final String API_GET_PAGING_NO_CACHE;
    private final String INFO_PAYMENT_URL;
    private final String URL_YAHOO;

    //    private final String API_BASE_LOCAL;
    private final String API_LOGIN;
    private final String API_REGISTRATION_MOBILE;
    private final String API_FORGOT_PASSWORD_MOBILE;
    private final String API_UPDATE_PASSWORD_MOBILE;
    private final String API_GET_PROFILE;
    private final String API_UPDATE_PROFILE;
    private final String API_GET_HOME_CONTENT;
    private final String API_ALL_BRANDS;
    private final String API_GET_CATEGORIES;
    private final String API_GET_BRAND_BY_CATEGORY;
    private final String API_GET_BRAND_BY_SEARCH_KEY;
    private final String API_GET_BRAND_BY_BANNER_ID;
    private final String API_GET_VOUCHER_GAME_ONLINE;
    private final String API_CHECK_PULSA_NUMBER;
    private final String API_AUTOCOMPLETE_ZIPCODE;
    private final String API_STORE_COVERAGE;
    private final String API_SEARCH_AUTOCOMPLETE;
    private final String API_GET_PRODUCT_BY_PERMALINK;
    private final String API_GET_PRODUCT_GROUP_BY_URL;
    private final String API_GET_RELATED_PRODUCT;
    private final String API_GET_AVAILABLE_COLOR;
    private final String API_GET_AVAILABLE_SIZE;
    private final String API_GET_AVAILABLE_SIZE_FROM_COLOR;
    private final String API_SEARCH_BY_CATEGORY;
    private final String API_CATEGORY_BY_BRAND;
    private final String API_SEARCH_BY_BARCODE;
    private final String API_MFP_ID;
    private final String API_LOGIN_MOBILE;
    private final String API_GET_PROVINCE;
    private final String API_GET_REGION_BY_PROVINCE;
    private final String API_GET_DISTRICT_BY_CITY;
    private final String API_GET_SUBDISTRICT_BY_DISTRICT;
    private final String API_GET_ZIPCODE_BY_SUBDISTRICT;
    private final String API_CHANGE_PHONE_NUMBER;
    private final String API_SEND_OTP;
    private final String API_SEND_OTP_BANK;
    private final String API_GET_PROFILE_ID;
    private final String API_VALIDATION_OTP_CODE;
    private final String API_VALIDATION_OTP_CODE_BANK;
    private final String API_ADD_ADDRESS;
    private final String API_DELETE_ADDRESS;
    private final String API_SET_SEFAULT_ADDRESS;
    private final String API_GET_ALL_REGION;
    private final String API_UPDATE_ADDRESS;
    private final String API_GET_SALES_ORDER_HEADER;
    private final String API_GET_THREE_SALES_ORDER;
    private final String API_SEARCH_STATUS_ORDER;
    private final String API_GET_SALES_ORDER_BY_ID;
    private final String API_GET_STORE_BY_ID;
    private final String API_GET_LASTEST_CART;
    private final String API_MODIFY_CART;
    private final String API_CART_TOTAL;
    private final String API_ADD_WISHLIST;
    private final String API_CHECK_MERGE;
    private final String API_GET_MERGE;
    private final String API_UPDATE_CUSTOMER;
    private final String API_GET_CART;
    private final String API_CHECK_STOCK_ONLINE;
    private final String API_DELETE_CART;
    private final String API_REMOVE_CART_ITEM;
    private final String API_UPDATE_CART_ITEM;
    private final String API_SET_SELECT_PROMO;
    private final String API_GET_STORE_BY_REGION;
    private final String API_CHECKOUT;
    private final String API_PAYMENT;
    private final String API_GET_REVIEW;
    private final String API_ADD_REVIEW;
    private final String API_ADD_IKUPON;
    private final String API_POST_PAYMENT;
    private final String API_DETAIL_PAYMENT;
    private final String API_CANCEL_COUPON;
    private final String API_ADD_VOUCHER;
    private final String API_CANCEL_VOUCHER;
    private final String API_REORDER;
    private final String API_PAYMENT_TYPE_INSTALLMENT;
    private final String API_GET_SHIPPING_METHOD;
    private final String API_GET_FACEBOOKCREDIT;
    private final String API_GET_GOOGLEPLAY;
    private final String API_GET_PAKET_INTERNET;
    private final String API_CATEGORY_GET;
    private final String API_GET_MINIMUM_TRANSACTION;
    private final String API_GET_STOREBYREGIONANDKEY;
    private final String API_UPDATECARTBYREGIONID;
    private final String API_GETBANNERBYURL;
    private final String API_GET_URL_BY_BANNER;
    private final String API_GETPRODGROUPBYURL;
    private final String API_GETBYALIASURL;
    private final String API_GETSERVERDATE;
    private final String API_GETSERVERTIME;
    private final String API_SAVENOTE;
    private final String API_FILTER_CATEGORY_BY_PRODUCT_GROUP;
    private final String API_FILTER_CATEGORY_BY_CATEGORY_ID;
    private final String API_FILTER_CATEGORY_BY_BANNER;
    private final String API_FILTER_CATEGORY_BY_SEARCH_KEY;
    private final String API_ACTIVATION;
    private final String API_RESEND_ACTIVATION;
    private final String API_GET_INITIAL_ADDRESS;
    private final String API_IPP_GET_REGION;
    private final String API_IPP_GET_STORE;
    private final String API_IPP_GET_STORE_BY_REGION;
    private final String API_IPP_GET_STORE_BY_REGION_STORE;
    private final String API_IPP_GET_STORE_BY_CODE_STORE;
    private final String API_IPP_GET_STORE_BY_CODE_STORE_IPP_STORE;
    private final String API_IPP_STORE_GET_STORE_BY_CODE_STORE_IPP_STORE;
    private final String API_IPP_GET_LIST_STORE;
    private final String API_IPP_STORE_GET_LIST_STORE;
    private final String API_BANNER_VIRTUAL;
    private final String API_BANNER_CONTENT;
    private final String API_STORE_ZONE_SLOT;

    private final String API_GET_ALL_DISPLAY;
    private final String API_GET_BOOKING;
    private final String API_GET_BOOKING_BOOKING;
    private final String API_GET_BOOKING_SEATMAP;
    private final String API_GET_PAYMENT_CHECKOUT_SALES_ORDERID;
    private final String API_GET_PAYMENT_CHECKOUT;
    private final String API_GET_GET_SUMMARY_KAI;
    private final String API_GET_PASSENGER_BY_CUST_ID;
    private final String API_ADD_PASSENGER;
    private final String API_EDIT_PASSENGERI;
    private final String API_DELETE_PASSENGER;
    private final String API_GET_SALES_BY_CUST_ID;
    private final String API_CHANGE_SEAT;
    private final String API_TICKET_BY_EMAIL_BOOKINGCODE;
    private final String API_GET_SUBSCRIBE;
    private final String API_SEND_TICKET;
    private final String API_GET_REVERSAL;

    //Revamp
    private final String API_GET_PAYMENT_SECTION;
    private final String API_GET_PAYMENT_PROMO;
    private final String API_GET_SEARCH_HISTORY;
    private final String API_INSERT_SEARCH_HISTORY;
    private final String API_POS_BOOKING_VOUCHER_COUPON;
    private final String API_GET_NOTIF_COUNT;
    private final String API_POS_SET_NOTIF;
    private final String API_POS_PUSH_NOTIF;
    private final String API_GET_FPPB;
    private final String API_UPDATE_FPPB;
    private final String API_GET_REFUND;
    private final String API_GET_EMAIL;

    private API() {
        ASSETS_BASE_URL = "http://assets.klikindomaret.com/";

//        production
        API_BASE_URL = "https://api.klikindomaret.com/api/";
        WEB_BASE_URL = "https://www.klikindomaret.com/";
        API_BASE_KAI_URL = "https://travel.klikindomaret.com/api/api/";

//        API_BASE_URL = "http://192.168.72.16:3001/api/";
//        WEB_BASE_URL = "http://192.168.72.16:3000/";

//        revamp
//        API_BASE_URL = "http://stageapi.klikindomaret.com/api/";
//        WEB_BASE_URL = "http://staging.klikindomaret.com/";
//        API_BASE_URL = "http://192.168.83.64:2445/api/";
//        API_BASE_URL = "http://172.31.2.34:8086/api/";
//        API_BASE_URL = "http://192.168.83.64:7676/api/";

//        API_BASE_URL = "http://172.31.2.75:9086/api/";
//        WEB_BASE_URL = "http://172.31.2.75:2017/api/";

//        API_BASE_URL = "http://172.31.2.34:7000/api/";;
//        WEB_BASE_URL = "http://staging.klikindomaret.com/";

//        WEB_BASE_URL = "http://172.31.2.31:7000/";

//        staging
//        API_BASE_URL = "http://172.31.2.34:8082/api/";
//        WEB_BASE_URL = "http://staging.klikindomaret.com/";

//        API_BASE_URL = "http://192.168.83.64:2445/api/";
//        API_BASE_URL = "http://apibeta.klikindomaret.com/api/";
//        WEB_BASE_URL = "http://beta.klikindomaret.com/";

//        API_BASE_URL = "http://stageapi.klikindomaret.com/api/";
//        WEB_BASE_URL = "http://staging.klikindomaret.com/";

//        staging 2
//        API_BASE_URL = "http://172.31.2.34:7000/api/";
//        WEB_BASE_URL = "http://staging.klikindomaret.com/";

//        staging
//        API_BASE_URL = "http://apibeta.klikindomaret.com/api/";
//        WEB_BASE_URL = "http://beta.klikindomaret.com/";

//        development
//        API_BASE_URL = "http://172.31.2.34:8000/api/";
//        WEB_BASE_URL = "http://172.31.2.31:8000/";

//        DEV 01
//        API_BASE_URL = "http://dev01api.klikindomaret.com/api/";
//        WEB_BASE_URL = "http://dev01.klikindomaret.com/";

//        KAI
//        API_BASE_KAI_URL = "http://192.168.83.64:8990/api/";
//        API_BASE_KAI_URL = "http://dev01.klikindomaret.com:8089/keretawebapi/api/";
//        API_BASE_KAI_URL = "http://192.168.83.64:5102/api/";

        CATEGORY_URL = "category/";
        CARA_MEMESAN_URL = "content/index/how-to-order";//http://172.31.2.75:2018/content/index/how-to-order
        STATUS_PESANAN = "order/status";
        CAKUPAN_KODE_POS_URL = "search/coveragezipcode";
        TANYA_JAWAB_URL = "content/index/faq";
        INFO_PAYMENT_URL = "content/index/cara-pembayaran";//content/index/cara-pembayaran
        TENTANG_KAMI_URL = "content/index/about-klikindomaret";
        KEBIJAKAN_URL = "content/index/privacy-policy";
        MEREK_PILIHAN_URL = "brand";
        PROMO_BULAN_INI_URL = "http://klikindomaret.com/promo-bulan-maret";
        LAYANAN_PELANGGAN = "tel:1500280";
        KERANJANG_URL = "cart/mycart";
        LOGIN_URL = "customer/login";
        AKUN_URL = "customer/account";
        KEBIJAKAN_PENGEMBALIAN_URL = "content/index/return-policy";
        SYARAT_DAN_KETENTUAN = "content/index/syarat-dan-ketentuan";
        SITEMAP_URL = "content/sitemap";
        WISHLIST = "wishlist/MyWishListMobileApps/";
        CHAT_BANTUAN = "widget/livechat";
        PAYMENT = "Content/Index/cara-pembayaran";
        URL_YAHOO = "Customer/RedirectFromYahoo";

        API_LOGIN = "customer/login";
        API_REGISTRATION_MOBILE = "Customer/RegisterViaMobileApps";
        API_LOGIN_MOBILE = "Customer/LoginViaMobileApps";
        API_FORGOT_PASSWORD_MOBILE = "customer/ForgotPasswordViaMobileApps";
        API_UPDATE_PASSWORD_MOBILE = "Customer/UpdatePasswordViaMobileApps";
        API_GET_PROFILE = "customer/account"; //sign
        API_UPDATE_PROFILE = "Customer/UpdateProfile";
        API_GET_HOME_CONTENT = "BannerPosition/GetBannerListByPositionGroup";
        API_ALL_BRANDS = "brand/get";
        API_GET_CATEGORIES = "Product/GetPaging";
        API_GET_PAGING_NO_CACHE = "Product/GetPagingNoCache";
        API_GET_BRAND_BY_CATEGORY = "Product/GetProductBrandByCategory";
        API_GET_BRAND_BY_SEARCH_KEY  = "Product/GetProductBrandBySearchKey";
        API_GET_BRAND_BY_BANNER_ID = "Product/GetProductBrandByPLU";
        API_GET_VOUCHER_GAME_ONLINE = "Product/GetProductVoucherGameOnline";
        API_CHECK_PULSA_NUMBER = "ShoppingCart/CheckNomorPulsa";
        API_AUTOCOMPLETE_ZIPCODE = "Store/AUTOCOMPLETE_ZIPCODE";
        API_STORE_COVERAGE = "Store/GetAllStoreZipCodeList";
        API_SEARCH_AUTOCOMPLETE = "Product/AUTOCOMPLETE_PRODUCT";
        API_GET_PRODUCT_BY_PERMALINK = "Product/Get";
        API_GET_PRODUCT_GROUP_BY_URL = "ProductGroup/GetByUrl";
        API_GET_RELATED_PRODUCT = "Product/GetRelatedProducts";
        API_GET_AVAILABLE_COLOR = "Product/GetAvailableColor";
        API_GET_AVAILABLE_SIZE = "Product/GetAvailableSize";
        API_GET_AVAILABLE_SIZE_FROM_COLOR = "Product/GetAvailableSizeFromColor";
        API_SEARCH_BY_CATEGORY = "Category/GetCategory";
        API_CATEGORY_BY_BRAND = "Brand/GetBrand";
        API_CATEGORY_GET = "Category/Get";
        API_SEARCH_BY_BARCODE = "Product/GetProductByBarcode";
        API_MFP_ID = "MobileAppsVersion/getDevice_Token";
        API_GET_PROVINCE = "CustomerAddress/GetProvince";
        API_GET_REGION_BY_PROVINCE = "CustomerAddress/GetRegionWithZipCodeByProvince";
        API_GET_DISTRICT_BY_CITY = "CustomerAddress/GetCityByRegion";
        API_GET_SUBDISTRICT_BY_DISTRICT = "CustomerAddress/GetDistrictByCityId";
        API_GET_ZIPCODE_BY_SUBDISTRICT = "CustomerAddress/GetZipCodeById";
        API_GET_INITIAL_ADDRESS = "CustomerAddress/GetByCustomerId";
        API_IPP_GET_REGION = "IPP/GetIPPRegionByCityId";
        API_IPP_GET_STORE = "IPP/GetIPPStoreByRegion";
        API_IPP_GET_STORE_BY_REGION = "IPP/GetByAreaWilayahIPPStore";
        API_IPP_GET_STORE_BY_REGION_STORE = "Store/GetListByAreaWilayahStore";
        API_IPP_GET_STORE_BY_CODE_STORE = "IPP/GetListIPPStore";
        API_IPP_GET_STORE_BY_CODE_STORE_IPP_STORE = "IPP/GetByStoreCodeIPPStore";
        API_IPP_STORE_GET_STORE_BY_CODE_STORE_IPP_STORE = "Store/GetListByStoreCode";
        API_IPP_GET_LIST_STORE = "IPP/GetStoreCodeListIPPStore";
        API_IPP_STORE_GET_LIST_STORE = "Store/GetListAutoComplete";
        API_BANNER_VIRTUAL = "Banner/Get?Position=VIRTUAL_PRODUCT";
        API_BANNER_CONTENT = "Content/Get";
        API_STORE_ZONE_SLOT = "ShoppingCart/StoreZoneSlotCalculate";
        API_CHANGE_PHONE_NUMBER = "Customer/ChangeMobilePhone";
        API_SEND_OTP = "Customer/SendOTP";
        API_SEND_OTP_BANK = "Customer/SendOTPAccountBank";
        API_GET_PROFILE_ID = "Customer/getbyid";
        API_VALIDATION_OTP_CODE = "Customer/ValidationOTPCode";
        API_VALIDATION_OTP_CODE_BANK = "Customer/ValidationAccountOTPCode";
        API_ADD_ADDRESS = "CustomerAddress/InsertAddress";
        API_DELETE_ADDRESS = "Customer/DeleteAddress";
        API_UPDATE_ADDRESS = "CustomerAddress/UpdateAddress";
        API_SET_SEFAULT_ADDRESS = "CustomerAddress/SetDefaultAddress";
        API_GET_ALL_REGION = "customeraddress/getregionall";
        API_GET_SALES_ORDER_HEADER = "SalesOrder/GetSalesOrderHeaderListByCustomerIdAndSalesOrderNo/";
        API_GET_THREE_SALES_ORDER = "SalesOrder/GetThreeSalesOrderByCustomerID";
        API_SEARCH_STATUS_ORDER = "SalesOrder/GetSalesOrderHeaderListByCustomerIdAndSalesOrderNo/";
        API_GET_SALES_ORDER_BY_ID = "SalesOrder/GetSalesOrderBySalesOrderId";
        API_GET_STORE_BY_ID = "store/getbyid/";
        API_GET_LASTEST_CART = "ShoppingCart/GetLatestShoppingCartId";
        API_MODIFY_CART = "ShoppingCart/ModifyCart";
        API_CART_TOTAL = "ShoppingCart/GetMyCartCount";
        API_ADD_WISHLIST = "WishList/ModifyWishList";
        API_CHECK_MERGE = "ShoppingCart/CheckMerge";
        API_GET_MERGE = "ShoppingCart/GetCartForMerge";
        API_UPDATE_CUSTOMER = "ShoppingCart/UpdateCustomerID";
        API_GET_CART = "ShoppingCart/MyCart";
        API_CHECK_STOCK_ONLINE = "ShoppingCart/CheckStockOnline";
        API_DELETE_CART = "ShoppingCart/DeleteCart";
        API_REMOVE_CART_ITEM = "ShoppingCart/RemoveCartItem";
        API_UPDATE_CART_ITEM = "ShoppingCart/UpdateItem";
        API_UPDATECARTBYREGIONID = "ShoppingCart/UpdateCartItemByRegion";
        API_SET_SELECT_PROMO = "ShoppingCart/SetPromoSelect";
        API_GET_STORE_BY_REGION = "Store/GetStoresByRegion";
        API_CHECKOUT = "ShoppingCart/Checkout";
        API_PAYMENT = "Payment/GetPayment";
        API_GET_VIRTUAL = "VirtualProduct/GetVirtualProductList";
        API_GET_PAGGING_NO_CACHE = "Product/GetPagingNoCache";
        API_DETAIL_PAYMENT = "Payment/DetailPayment";
        API_GET_REVIEW = "Product/GetReview";
        API_ADD_REVIEW = "Product/WriteReview";
        API_ADD_IKUPON = "coupon/AddCoupon";
        API_POST_PAYMENT = "Payment/PostInsertPayment";
        API_CANCEL_COUPON = "Coupon/CancelCoupon";
        API_ADD_VOUCHER = "SalesOrder/BookVBK";
        API_CANCEL_VOUCHER = "Voucher/ReversalVoucherCoupon";
        API_REORDER = "ShoppingCart/CreateReOrderBySalesOrderId";
        API_PAYMENT_TYPE_INSTALLMENT = "PaymentType/GetPaymentTypeInstallment";
        API_GET_SHIPPING_METHOD = "Shoppingcart/GetShippingMethod";
        API_GET_FACEBOOKCREDIT = "Product/GetProductVoucherFacebook";
        API_GET_GOOGLEPLAY = "Product/GetProductVoucherGoogleCard";
        API_GET_PAKET_INTERNET = "/ShoppingCart/CheckNomorData";
        API_GET_MINIMUM_TRANSACTION = "SalesOrder/GetMinimumTransactionByNominal";
        API_GET_STOREBYREGIONANDKEY = "Store/GetStoresByRegionAndKey";
        API_GETBANNERBYURL = "Banner/GetByUrl";
        API_GET_URL_BY_BANNER = "Banner/GetByUrlBanner";
        API_GETPRODGROUPBYURL = "ProductGroup/GetByUrl";
        API_GETBYALIASURL = "AliasURL/GetByAlias";
        API_GETSERVERDATE = "setting/GetServerDateTime";
        API_GETSERVERTIME = "setting/Get?SettingKey=STORE_PREPARATION_TIME";
        API_SAVENOTE = "ShoppingCart/SaveNote";
        API_FILTER_CATEGORY_BY_PRODUCT_GROUP = "ProductGroup/GetByUrlLink";
        API_FILTER_CATEGORY_BY_CATEGORY_ID = "/Category/GetAllCategoriesByCategoryID";
        API_FILTER_CATEGORY_BY_BANNER = "Banner/GetByUrlBanner";
        API_FILTER_CATEGORY_BY_SEARCH_KEY = "Category/GetAllCategoriesBySearchKey";
        API_ACTIVATION = "Customer/ActivationWithPINCode";
        API_RESEND_ACTIVATION = "Customer/ResendActivationViaMobile";
        API_GET_ALL_DISPLAY = "Station/GetAllDisplay";
        API_GET_BOOKING = "Booking/Schedule";
        API_GET_BOOKING_BOOKING = "Booking/Booking";
        API_GET_BOOKING_SEATMAP = "Booking/GetBookingSeatMap";
        API_GET_PAYMENT_CHECKOUT_SALES_ORDERID = "SalesOrder/GetSummaryBySalesOrderHeaderID";
        API_GET_PAYMENT_CHECKOUT = "Payment/PaymentCheckout";
        API_GET_GET_SUMMARY_KAI = "Payment/GetSummaryByTransactionCode";
        API_GET_PASSENGER_BY_CUST_ID = "Passenger/GetPassengerByCustomerID";
        API_ADD_PASSENGER = "Passenger/AddPassenger";
        API_EDIT_PASSENGERI = "Passenger/EditPassenger";
        API_DELETE_PASSENGER = "Passenger/GetByIDAndThenDelete";
        API_GET_SALES_BY_CUST_ID = "SalesOrder/GetSalesOrderByCustomerID";
        API_CHANGE_SEAT = "Booking/ChangeSeat";
        API_TICKET_BY_EMAIL_BOOKINGCODE = "SalesOrder/SOByBookingCodeAndEmail";
        API_GET_SUBSCRIBE = "Subscriber/GetSubscriber";
        API_SEND_TICKET = "Booking/SendETicket";
        API_GET_REVERSAL = "Booking/Cancel";
        API_GET_PAYMENT_SECTION = "Payment/GetPaymentSection";
        API_GET_PAYMENT_PROMO = "Payment/GetPaymentPromo";
        API_GET_SEARCH_HISTORY = "SearchHistory/GetAllSearchHistory";
        API_INSERT_SEARCH_HISTORY = "SearchHistory/GetInsertSearchHistoryCategory";
        API_POS_BOOKING_VOUCHER_COUPON = "Voucher/BookingVoucherCoupon";
        API_GET_NOTIF_COUNT = "Customer/GetNotifCount";
        API_POS_SET_NOTIF = "Customer/SetNotificationReaded";
        API_POS_PUSH_NOTIF = "Payment/PushNotification";
        API_GET_FPPB = "ReturProcess/GetFppbHeaderByNo";
        API_UPDATE_FPPB = "ReturProcess/UpdateFppb";
        API_GET_REFUND = "RefundPlaza/GetRefundFromFppbDetailId";
        API_GET_EMAIL = "Customer/GetbyEmail";
    }

    public static API getInstance() {
        if (sInstance == null) {
            sInstance = new API();
        }
        return sInstance;
    }

    public String getWebBaseUrl() { return WEB_BASE_URL;}
    public String getAPIBaseUrl() { return API_BASE_URL;}
    public String getWebCategoryUrl() { return WEB_BASE_URL+CATEGORY_URL;}
    public String getCaraMemesanUrl() { return WEB_BASE_URL+CARA_MEMESAN_URL;}
    public String getStatusPesananUrl() { return WEB_BASE_URL+STATUS_PESANAN;}
    public String getCakupanKodePosUrl() { return WEB_BASE_URL+CAKUPAN_KODE_POS_URL;}
    public String getTanyaJawabUrl() { return WEB_BASE_URL+TANYA_JAWAB_URL;}
    public String getInfoPaymentUrl() { return WEB_BASE_URL+INFO_PAYMENT_URL;}
    public String getTentangKamiUrl() { return WEB_BASE_URL+TENTANG_KAMI_URL;}
    public String getKebijakanUrl() { return WEB_BASE_URL+KEBIJAKAN_URL;}
    public String getMerekPilihanUrl() { return WEB_BASE_URL+MEREK_PILIHAN_URL;}
    public String getLayananPelangganUrl() {return LAYANAN_PELANGGAN;}
    public String getKeranjangUrl() {return WEB_BASE_URL+KERANJANG_URL;}
    public String getAkunUrl() {return WEB_BASE_URL+AKUN_URL;}
    public String getKebijakanPengembalianUrl() {return WEB_BASE_URL+KEBIJAKAN_PENGEMBALIAN_URL;}
    public String getSyaratDanKetentuanUrl() {return WEB_BASE_URL+SYARAT_DAN_KETENTUAN;}
    public String getSitemapUrl() {return WEB_BASE_URL+SITEMAP_URL;}
    public String getWishlistUrl() {return WEB_BASE_URL+WISHLIST;}
    public String getChatBantuanUrl() {return WEB_BASE_URL+CHAT_BANTUAN;}
    public String getPaymentUrl() {return WEB_BASE_URL + PAYMENT;}
    public String getYahooUrl() {return WEB_BASE_URL + URL_YAHOO;}

    public String getAssetsUrl(){return ASSETS_BASE_URL;}
    public String getApiLogin() {return API_BASE_URL + API_LOGIN;}
    public String getApiLoginMobile() {return API_BASE_URL + API_LOGIN_MOBILE;}
    public String getApiRegistrationMobile() {return API_BASE_URL + API_REGISTRATION_MOBILE; }
    public String getApiForgotPasswordMobile() {return API_BASE_URL + API_FORGOT_PASSWORD_MOBILE;}
    public String getApiUpdatePasswordMobile() {return API_BASE_URL + API_UPDATE_PASSWORD_MOBILE;}
    public String getApiGetProfile() {return API_BASE_URL + API_GET_PROFILE;}
    public String getApiUpdateProfile() {return API_BASE_URL + API_UPDATE_PROFILE;}
    public String getApiHomeContent(){return API_BASE_URL + API_GET_HOME_CONTENT;}
    public String getApiAllBrands(){return API_BASE_URL + API_ALL_BRANDS;}
    public String getApiCategories(){return API_BASE_URL + API_GET_CATEGORIES;}
    public String getApiPagingNoCache(){return API_BASE_URL + API_GET_PAGING_NO_CACHE;}
    public String getApiBrandByCategory(){return API_BASE_URL + API_GET_BRAND_BY_CATEGORY;}
    public String getApiBrandBySearchKey(){return API_BASE_URL + API_GET_BRAND_BY_SEARCH_KEY;}
    public String getApiBrandByBannerId() {return API_BASE_URL + API_GET_BRAND_BY_BANNER_ID;}
    public String getApiVoucherGameOnline() {return API_BASE_URL + API_GET_VOUCHER_GAME_ONLINE;}
    public String getApiCheckPulsaNumber() {return API_BASE_URL + API_CHECK_PULSA_NUMBER;}
    public String getApiAutoCompleteZipcode() {return API_BASE_URL + API_AUTOCOMPLETE_ZIPCODE;}
    public String getApiStoreCoverage() {return API_BASE_URL + API_STORE_COVERAGE;}
    public String getApiSearchAutoComplete() {return API_BASE_URL + API_SEARCH_AUTOCOMPLETE;}
    public String getApiProductByPermalink() {return API_BASE_URL + API_GET_PRODUCT_BY_PERMALINK;}
    public String getApiProductGroupByUrl() {return API_BASE_URL + API_GET_PRODUCT_GROUP_BY_URL;}
    public String getApiRelatedProduct() {return API_BASE_URL + API_GET_RELATED_PRODUCT;}
    public String getAPiAvailableColor() {return API_BASE_URL + API_GET_AVAILABLE_COLOR;}
    public String getApiAvailableSize() {return API_BASE_URL + API_GET_AVAILABLE_SIZE;}
    public String getApiAvailableSizeFromColor() {return API_BASE_URL + API_GET_AVAILABLE_SIZE_FROM_COLOR;}
    public String getSearchByCategory() {return API_BASE_URL + API_SEARCH_BY_CATEGORY;}
    public String getSearchByCategoryByBrand() {return API_BASE_URL + API_CATEGORY_BY_BRAND;}
    public String getSearchByBarcode() {return API_BASE_URL + API_SEARCH_BY_BARCODE;}
    public String getMfpId() {return API_BASE_URL + API_MFP_ID;}
    public String getApiGetProvince() {return API_BASE_URL + API_GET_PROVINCE;}
    public String getApiGetRegionByProvince() {return API_BASE_URL + API_GET_REGION_BY_PROVINCE;}
    public String getApiGetDsitrictByCity() {return API_BASE_URL + API_GET_DISTRICT_BY_CITY;}
    public String getApiGetSubdistrictByDistrict() {return API_BASE_URL + API_GET_SUBDISTRICT_BY_DISTRICT;}
    public String getApiGetZipcodebySubdistrict() {return API_BASE_URL + API_GET_ZIPCODE_BY_SUBDISTRICT;}
    public String getApiGetInitialAddress() {return API_BASE_URL + API_GET_INITIAL_ADDRESS;}
    public String getApiIPPGetRegion() {return API_BASE_URL + API_IPP_GET_REGION;}
    public String getApiIPPGetStore() {return API_BASE_URL + API_IPP_GET_STORE;}
    public String getApiIPPGetStoreByRegion() {return API_BASE_URL + API_IPP_GET_STORE_BY_REGION;}
    public String getApiIPPStoreGetStoreByRegion() {return API_BASE_URL + API_IPP_GET_STORE_BY_REGION_STORE;}
    public String getApiIPPGetStoreByStoreCode() {return API_BASE_URL + API_IPP_GET_STORE_BY_CODE_STORE;}
    public String getApiIPPGetStoreByStoreCodeIppStore() {return API_BASE_URL + API_IPP_GET_STORE_BY_CODE_STORE_IPP_STORE;}
    public String getApiIPPStoreGetStoreByStoreCodeIppStore() {return API_BASE_URL + API_IPP_STORE_GET_STORE_BY_CODE_STORE_IPP_STORE;}
    public String getApiIPPGetListStore() {return API_BASE_URL + API_IPP_GET_LIST_STORE;}
    public String getApiIPPStoreGetListStore() {return API_BASE_URL + API_IPP_STORE_GET_LIST_STORE;}
    public String getApiBannerVirtual() {return API_BASE_URL + API_BANNER_VIRTUAL;}
    public String getApiBannerContent() {return API_BASE_URL + API_BANNER_CONTENT;}
    public String getApiStoreZoneSlot() {return API_BASE_URL + API_STORE_ZONE_SLOT;}
    public String getApiChangePhoneNumber() {return API_BASE_URL + API_CHANGE_PHONE_NUMBER;}
    public String getSendOtp() {return API_BASE_URL + API_SEND_OTP; }
    public String getSendOtpBank() {return API_BASE_URL + API_SEND_OTP_BANK; }
    public String getProfileId() {return API_BASE_URL + API_GET_PROFILE_ID; }
    public String getApiValidationOtpCode() {return API_BASE_URL + API_VALIDATION_OTP_CODE; }
    public String getApiValidationOtpCodeBank() {return API_BASE_URL + API_VALIDATION_OTP_CODE_BANK; }
    public String getApiAddAddress() {return API_BASE_URL + API_ADD_ADDRESS; }
    public String getApiDeleteAddress() {return API_BASE_URL + API_DELETE_ADDRESS; }
    public String getApiSetDefaultAddress() {return API_BASE_URL + API_SET_SEFAULT_ADDRESS;}
    public String getApiAllRegion() {return API_BASE_URL + API_GET_ALL_REGION;}
    public String getApiUpdateAddress() {return API_BASE_URL + API_UPDATE_ADDRESS;}
    public String getApiSalesOrderHeader() {return API_BASE_URL + API_GET_SALES_ORDER_HEADER; }
    public String getApiThreeSalesOrder(){return API_BASE_URL + API_GET_THREE_SALES_ORDER; }
    public String getApiSearchStatusOrder() {return API_BASE_URL + API_SEARCH_STATUS_ORDER; }
    public String getApiStatusOrderById() {return API_BASE_URL + API_GET_SALES_ORDER_BY_ID; }
    public String getApiStoreById() {return  API_BASE_URL + API_GET_STORE_BY_ID;}
    public String getLastestShoppingCartId() {return API_BASE_URL + API_GET_LASTEST_CART;}
    public String getApiModifyCart() {return API_BASE_URL + API_MODIFY_CART;}
    public String getCartTotal() {return API_BASE_URL + API_CART_TOTAL;}
    public String getApiAddWishlist() {return API_BASE_URL + API_ADD_WISHLIST;}
    public String getApiCheckMerge() {return API_BASE_URL + API_CHECK_MERGE;}
    public String getApiGetMerge() {return API_BASE_URL + API_GET_MERGE;}
    public String getApiUpdateCustomer() {return  API_BASE_URL + API_UPDATE_CUSTOMER;}
    public String getApiGetCart() {return API_BASE_URL + API_GET_CART;}
    public String getApiCheckStockOnline() {return  API_BASE_URL + API_CHECK_STOCK_ONLINE;}
    public String getApiDeleteCart() {return  API_BASE_URL + API_DELETE_CART;}
    public String getApiRemoveCartItem() {return API_BASE_URL + API_REMOVE_CART_ITEM;}
    public String getApiUpdateCartItem() {return API_BASE_URL + API_UPDATE_CART_ITEM;}
    public String getAPiSetSelectPromo() {return API_BASE_URL + API_SET_SELECT_PROMO;}
    public String getApiStoreByRegion() {return API_BASE_URL + API_GET_STORE_BY_REGION;}
    public String getApiCheckout() {return API_BASE_URL + API_CHECKOUT;}
    public String getApiPayment() {return API_BASE_URL + API_PAYMENT;}
    public String getApiDetailPayment() {return API_BASE_URL + API_DETAIL_PAYMENT;}
    public String getApiGetReview() {return API_BASE_URL + API_GET_REVIEW;}
    public String getApiWriteReview() {return API_BASE_URL + API_ADD_REVIEW;}
    public String getApiPostInsertPayment() {return API_BASE_URL + API_POST_PAYMENT;}
    public String getApiAddCoupon() {return API_BASE_URL + API_ADD_IKUPON;}
    public String getApiCancelCoupon() {return API_BASE_URL + API_CANCEL_COUPON;}
    public String getApiAddVoucher() {return API_BASE_URL + API_ADD_VOUCHER;}
    public String getApiCancelVoucher() {return API_BASE_URL + API_CANCEL_VOUCHER;}
    public String getApiReorder() {return API_BASE_URL + API_REORDER;}
    public String getApiPaymentTypeInstallment() {return API_BASE_URL + API_PAYMENT_TYPE_INSTALLMENT;}
    public String getApiShippingMethod() {return API_BASE_URL + API_GET_SHIPPING_METHOD;}
    public String getApiGetVoucherFacebook(){return API_BASE_URL + API_GET_FACEBOOKCREDIT;}
    public String getApiGetGooglePlay(){return API_BASE_URL + API_GET_GOOGLEPLAY;}
    public String getApiGetPaketInternet(){return  API_BASE_URL + API_GET_PAKET_INTERNET;}
    public String getApiCategoryGet(){return  API_BASE_URL + API_CATEGORY_GET;}
    public String getApiMinimumTransaction(){return  API_BASE_URL + API_GET_MINIMUM_TRANSACTION;}
    public String getApiGetStoreByRegionAndKey(){return API_BASE_URL + API_GET_STOREBYREGIONANDKEY;}
    public String updateCartItemByRegion(){return API_BASE_URL + API_UPDATECARTBYREGIONID;}
    public String getApiBannerByUrl(){return  API_BASE_URL + API_GETBANNERBYURL;}
    public String getApiUrlByBanner(){return  API_BASE_URL + API_GET_URL_BY_BANNER;}
    public String getApiProdGroupByUrl(){return API_BASE_URL + API_GETPRODGROUPBYURL;}
    public String getApiByAliasUrl(){return API_BASE_URL + API_GETBYALIASURL;}
    public String getApiServerDate(){return API_BASE_URL + API_GETSERVERDATE;}
    public String getApiServerTime(){return API_BASE_URL + API_GETSERVERTIME;}
    public String getApiSaveNote(){return API_BASE_URL + API_SAVENOTE;}
    public String getApiFilterCategoryByProductGroup(){return API_BASE_URL + API_FILTER_CATEGORY_BY_PRODUCT_GROUP;}
    public String getApiFilterCategoryByCategoruId(){return API_BASE_URL + API_FILTER_CATEGORY_BY_CATEGORY_ID;}
    public String getApiFilterCategoryByBanner(){return API_BASE_URL + API_FILTER_CATEGORY_BY_BANNER;}
    public String getApiFilterCategoryBySearchKey(){return API_BASE_URL + API_FILTER_CATEGORY_BY_SEARCH_KEY;}
    public String getApiActivation(){return API_BASE_URL + API_ACTIVATION;}
    public String getApiResendActivation(){return API_BASE_URL + API_RESEND_ACTIVATION;}

    public String getApiAllDisplay(){return API_BASE_KAI_URL + API_GET_ALL_DISPLAY;}
    public String getApiBooking(){return API_BASE_KAI_URL + API_GET_BOOKING;}
    public String getApiBookingBooking(){return API_BASE_KAI_URL + API_GET_BOOKING_BOOKING;}
    public String getApiBookingSeatMap(){return API_BASE_KAI_URL + API_GET_BOOKING_SEATMAP;}
    public String getApiSalesOrderId(){return API_BASE_KAI_URL + API_GET_PAYMENT_CHECKOUT_SALES_ORDERID;}
    public String getApiPaymentCheckout(){return API_BASE_KAI_URL + API_GET_PAYMENT_CHECKOUT;}
    public String getApiGetSummaryKAI(){return API_BASE_KAI_URL + API_GET_GET_SUMMARY_KAI;}
    public String getApiGetPassenger(){return API_BASE_KAI_URL + API_GET_PASSENGER_BY_CUST_ID;}
    public String getApiEditPassenger(){return API_BASE_KAI_URL + API_EDIT_PASSENGERI;}
    public String getApiAddPassenger(){return API_BASE_KAI_URL + API_ADD_PASSENGER;}
    public String getApiDeletePassenger(){return API_BASE_KAI_URL + API_DELETE_PASSENGER;}
    public String getApiSalesByCustId(){return API_BASE_KAI_URL + API_GET_SALES_BY_CUST_ID;}
    public String getApiChangeSeat(){return API_BASE_KAI_URL + API_CHANGE_SEAT;}
    public String getApiTicketByEmailBooingCode(){return API_BASE_KAI_URL + API_TICKET_BY_EMAIL_BOOKINGCODE;}
    public String getApiGetSubScribe(){return API_BASE_URL + API_GET_SUBSCRIBE;}
    public String getApiSendTicket(){return API_BASE_KAI_URL + API_SEND_TICKET;}
    public String getApiGetReversal(){return API_BASE_KAI_URL + API_GET_REVERSAL;}
    public String getApiPaymentSection(){return API_BASE_URL + API_GET_PAYMENT_SECTION;}
    public String getApiPaymentPromo(){return API_BASE_URL + API_GET_PAYMENT_PROMO;}
    public String getApiSearchHistory(){return API_BASE_URL + API_GET_SEARCH_HISTORY;}
    public String insertApiSearchHistory(){return API_BASE_URL + API_INSERT_SEARCH_HISTORY;}
    public String getApiBookingVoucherCoupon(){return API_BASE_URL + API_POS_BOOKING_VOUCHER_COUPON;}
    public String getApiNotifCount(){return API_BASE_URL + API_GET_NOTIF_COUNT;}
    public String getApiSetNotif(){return API_BASE_URL + API_POS_SET_NOTIF;}
    public String getApiPushNotif(){return API_BASE_URL + API_POS_PUSH_NOTIF;}
    public String getApiFppb(){return API_BASE_URL + API_GET_FPPB;}
    public String getApiUpdateFppb(){return API_BASE_URL + API_UPDATE_FPPB;}
    public String getApiRefund(){return API_BASE_URL + API_GET_REFUND;}
    public String getApiEmail(){return API_BASE_URL + API_GET_EMAIL;}
    public String getApiVirtual() {return API_BASE_URL + API_GET_VIRTUAL;}
    public String getApiPaggingNoCache(){return API_BASE_URL + API_GET_PAGGING_NO_CACHE;}
}

