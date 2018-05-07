package com.indomaret.klikindomaret.model;

/**
 * Created by USER on 4/14/2016.
 */
public class InformationPromo {

    String promoTitle;
    String promoContent;
    int promoImage;

    public InformationPromo(String promoTitle, String promoContent, int promoImage){
        this.promoTitle = promoTitle;
        this.promoContent = promoContent;
        this.promoImage = promoImage;
    }

    public String getPromoTitle() {
        return promoTitle;
    }

    public void setPromoTitle(String promoTitle) {
        this.promoTitle = promoTitle;
    }

    public String getPromoContent() {
        return promoContent;
    }

    public void setPromoContent(String promoContent) {
        this.promoContent = promoContent;
    }

    public int getPromoImage() {
        return promoImage;
    }

    public void setPromoImage(int promoImage) {
        this.promoImage = promoImage;
    }
}
