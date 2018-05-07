package com.indomaret.klikindomaret.data;

import java.util.ArrayList;

/**
 * Created by USER on 11/18/2016.
 */
public class Items {
    private String pName;
    private ArrayList<SubCategory> mSubCategoryList;

    public Items(String pName, ArrayList<SubCategory> mSubCategoryList) {
        super();
        this.pName = pName;
        this.mSubCategoryList = mSubCategoryList;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public ArrayList<SubCategory> getmSubCategoryList() {
        return mSubCategoryList;
    }

    public void setmSubCategoryList(ArrayList<SubCategory> mSubCategoryList) {
        this.mSubCategoryList = mSubCategoryList;
    }

    /**
     *
     * second level item
     *
     */

    public static class SubCategory {
        private String pSubCatName;

        public SubCategory(String pSubCatName) {
            super();
            this.pSubCatName = pSubCatName;
        }

        public String getpSubCatName() {
            return pSubCatName;
        }

        public void setpSubCatName(String pSubCatName) {
            this.pSubCatName = pSubCatName;
        }
    }
}
