package com.indomaret.klikindomaret.model;

import java.util.List;

/**
 * Created by USER on 4/5/2016.
 */
public class Address {

    String $id, ID, CustomerID, Phone, Street, District, ZipCode, ProvinceId, Street2, Region, CityLabel, Street3, ProvinceName, RegionName, ReceiverName, ReceiverPhone, DefaultAddressID;
    boolean IsDefault;
    int CityId, DistrictId;
    Province[] Province;

    public String get$id() {
        return $id;
    }

    public void set$id(String $id) {
        this.$id = $id;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(String customerID) {
        CustomerID = customerID;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getDistrict() {
        return District;
    }

    public void setDistrict(String district) {
        District = district;
    }

    public String getZipCode() {
        return ZipCode;
    }

    public void setZipCode(String zipCode) {
        ZipCode = zipCode;
    }

    public String getProvinceId() {
        return ProvinceId;
    }

    public void setProvinceId(String provinceId) {
        ProvinceId = provinceId;
    }

    public String getStreet2() {
        return Street2;
    }

    public void setStreet2(String street2) {
        Street2 = street2;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getCityLabel() {
        return CityLabel;
    }

    public void setCityLabel(String cityLabel) {
        CityLabel = cityLabel;
    }

    public String getStreet3() {
        return Street3;
    }

    public void setStreet3(String street3) {
        Street3 = street3;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }

    public String getRegionName() {
        return RegionName;
    }

    public void setRegionName(String regionName) {
        RegionName = regionName;
    }

    public String getReceiverName() {
        return ReceiverName;
    }

    public void setReceiverName(String receiverName) {
        ReceiverName = receiverName;
    }

    public String getReceiverPhone() {
        return ReceiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        ReceiverPhone = receiverPhone;
    }

    public String getDefaultAddressID() {
        return DefaultAddressID;
    }

    public void setDefaultAddressID(String defaultAddressID) {
        DefaultAddressID = defaultAddressID;
    }

    public boolean isDefault() {
        return IsDefault;
    }

    public void setDefault(boolean aDefault) {
        IsDefault = aDefault;
    }

    public int getCityId() {
        return CityId;
    }

    public void setCityId(int cityId) {
        CityId = cityId;
    }

    public int getDistrictId() {
        return DistrictId;
    }

    public void setDistrictId(int districtId) {
        DistrictId = districtId;
    }

    public com.indomaret.klikindomaret.model.Province[] getProvince() {
        return Province;
    }

    public void setProvince(com.indomaret.klikindomaret.model.Province[] province) {
        Province = province;
    }
}
