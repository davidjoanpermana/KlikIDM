package com.indomaret.klikindomaret.model;

/**
 * Created by USER on 5/27/2016.
 */
public class BadgeTag {

    int BadgeType;
    String BadgeDesc, BadgeDetailTitle, BadgeDetailDesc, PromoCode, PromoDesc;

    public BadgeTag(){

    }

    public int getBadgeType() {
        return BadgeType;
    }

    public void setBadgeType(int badgeType) {
        BadgeType = badgeType;
    }

    public String getBadgeDesc() {
        return BadgeDesc;
    }

    public void setBadgeDesc(String badgeDesc) {
        BadgeDesc = badgeDesc;
    }

    public String getBadgeDetailTitle() {
        return BadgeDetailTitle;
    }

    public void setBadgeDetailTitle(String badgeDetailTitle) {
        BadgeDetailTitle = badgeDetailTitle;
    }

    public String getBadgeDetailDesc() {
        return BadgeDetailDesc;
    }

    public void setBadgeDetailDesc(String badgeDetailDesc) {
        BadgeDetailDesc = badgeDetailDesc;
    }

    public String getPromoCode() {
        return PromoCode;
    }

    public void setPromoCode(String promoCode) {
        PromoCode = promoCode;
    }

    public String getPromoDesc() {
        return PromoDesc;
    }

    public void setPromoDesc(String promoDesc) {
        PromoDesc = promoDesc;
    }
}
