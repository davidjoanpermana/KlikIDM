/******************************************************************************
 *
 *  2016 (C) Copyright Open-RnD Sp. z o.o.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package com.indomaret.klikindomaret.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DataProvider {
    /**
     * Do not confuse with MultiLevelListView levels.
     * Following variables refer only to data generation process.
     * For instance, if ITEMS_PER_LEVEL = 2 and MAX_LEVELS = 3,
     * list should look like this:
     *      + 1
     *      | + 1.1
     *      | - - 1.1.1
     *      | - - 1.1.2
     *      | + 1.2
     *      | - - 1.2.1
     *      | - - 1.2.2
     *      | - - 1.2.3
     *      + 2
     *      | + 2.1
     *      | - - 2.1.1
     *      | - - 2.1.2
     *      | + 2.2
     *      | - - 2.2.1
     *      | - - 2.2.2
     */
    public JSONArray categories;
    String type;

    private static final int ITEMS_PER_LEVEL = 4;
    private static final int MAX_LEVELS = 6;

    public DataProvider(JSONArray categories, String type) {
//        this.categories = categories;
        this.categories = modifyCategories(categories);
        this.type = type;
    }

    private JSONArray modifyCategories(JSONArray categories){
        JSONArray result = new JSONArray();

        try {

            if(categories != null){
                if(categories.length()>0){
                    for(int i = 0; i< categories.length(); i++){
                        JSONObject jsonObject = categories.getJSONObject(i);

                        if(jsonObject != null){
                            if (jsonObject.getString("ID").equals("home")
                                    || jsonObject.getString("ID").equals("kategoryBelanja")
                                    || jsonObject.getString("ID").equals("notifikasi")
                                    || jsonObject.getString("ID").equals("riwayatBelanja")
                                    || jsonObject.getString("ID").equals("daftarBelanja")
                                    || jsonObject.getString("ID").equals("merchantCenter")
                                    || jsonObject.getString("ID").equals("bantuan")
                                    || jsonObject.getString("ID").equals("bantuanAboutIndomaret")
                                    || jsonObject.getString("ID").equals("hotelIndomaret")
                                    || jsonObject.getString("ID").equals("bantuanShopping")
                                    || jsonObject.getString("ID").equals("bantuanPayment")
                                    || jsonObject.getString("ID").equals("bantuanFAQ")
                                    || jsonObject.getString("ID").equals("bantuanKebijakan")
                                    || jsonObject.getString("ID").equals("bantuanLayanan")
                                    || jsonObject.getString("ID").equals("bantuanKodePos")
                                    || jsonObject.getString("ID").equals("bantuanKebijakanPrivasi")
                                    || jsonObject.getString("ID").equals("bantuanPersyaratan")){

                            }else{
                                if(jsonObject.getInt("Level") == 1){
                                    jsonObject.put("Level", jsonObject.getInt("Level") + 1);
                                    jsonObject.put("ParentID","kategoryBelanja");

                                }else if(jsonObject.getInt("Level") > 1){
                                    jsonObject.put("Level", jsonObject.getInt("Level") + 1);
                                }
                                else if(jsonObject.getInt("Level") == 0){
                                     jsonObject.put("Level", jsonObject.getInt("Level"));
                                }
                            }

                            result.put(jsonObject);
                        }
                    }
                }
            }
        }catch (Exception e){

        }

        return result;
    }

    public List<BaseItem> getInitialItems() {
        return getSubItems(new GroupItem("root"));
    }

    public List<BaseItem> getSubItems(BaseItem baseItem) {
        if (!(baseItem instanceof GroupItem)) {
            throw new IllegalArgumentException("GroupItem required");
        }

        GroupItem groupItem = (GroupItem) baseItem;
        List<BaseItem> result = new ArrayList<>();

        String categoryID = null;
        boolean isRoot = true;

        if(!baseItem.getName().equals("root")) {
            isRoot = false;

            try {
                categoryID = baseItem.dataObj.getString("ID");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < this.categories.length(); i++) {
            int level = 1;

            try {
                BaseItem item = null;
                JSONObject itemObj = this.categories.getJSONObject(i);
                level = itemObj.getInt("Level");
                int productTotal = itemObj.getInt("ProductTotal");
                Boolean isPackage = itemObj.getBoolean("IsPackage");

                if(productTotal > 0 || isPackage) {
                    if (isRoot && level == 1) {
                        item = newGroupOrItem(itemObj);
                    }
                    else if (!isRoot && level > 1) {
                        String parentID = itemObj.getString("ParentID");

                        if ((parentID.equals(categoryID) && level == groupItem.getLevel() + 1)) {
                            item = newGroupOrItem(itemObj);
                        }
                    }
                }else{
                    if (isRoot && level == 0) {
                        item = newGroupOrItem(itemObj);
                    }
                }

                if(item != null) result.add(item);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static boolean isExpandable(BaseItem baseItem) {
        return baseItem instanceof GroupItem;
    }

    private BaseItem newGroupOrItem(JSONObject itemObj) {
        BaseItem item = null;
        String itemID;

        try {
            itemID = itemObj.getString("ID");
            int level = itemObj.getInt("Level");

            if (hasChildren(itemID)) {
                item = new GroupItem(itemObj.getString("Name"));
                ((GroupItem) item).setLevel(level);
            } else {
                item = new Item(itemObj.getString("Name"));
            }

            item.dataObj = itemObj;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }

    private Boolean hasChildren(String itemID) {
        for (int j = 0; j < this.categories.length(); j++) {
            String parentID = null;

            try {
                JSONObject objChild = this.categories.getJSONObject(j);
                parentID = objChild.getString("ParentID");
                int productTotal = objChild.getInt("ProductTotal");
                Boolean isPackage = objChild.getBoolean("IsPackage");

                if(parentID.equals(itemID) && (productTotal > 0 || isPackage)) {
                    return true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}
