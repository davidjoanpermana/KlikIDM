package com.indomaret.klikindomaret.model;

/**
 * Created by indomaretitsd7 on 6/13/16.
 */
public class Profile {

    String ID, FBID, FName, LName, Email, Mobile, Password, IPAddress, Phone, DateOfBirth, CustomerAddress, DefaultStoreID, CreatedBy, UpdatedBy, Gender;
    String LastUpdate, FullName, ConfirmPassword, NewEmail, ConfirmEmail, NewPassword, DateOfBirthStringFormatted, OTPCode, OTPCodeCreatedDate, ValidateOTPDate;
    String DateOfBirthExists, FaspayToken, Day, Month, Year;
    boolean IsConfirmed, IsSubscribed, AllowSMS, IsNewsLetterSubscriber, IsUpload, IsActivated, MobileVerified, OTPValidationExpired;
    boolean IsFromOtherSystem;
    int TypePushEmail, OTPCount, OTPAvailable;
    Address[] Address;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFBID() {
        return FBID;
    }

    public void setFBID(String FBID) {
        this.FBID = FBID;
    }

    public String getFName() {
        return FName;
    }

    public void setFName(String FName) {
        this.FName = FName;
    }

    public String getLName() {
        return LName;
    }

    public void setLName(String LName) {
        this.LName = LName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public String getCustomerAddress() {
        return CustomerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        CustomerAddress = customerAddress;
    }

    public String getDefaultStoreID() {
        return DefaultStoreID;
    }

    public void setDefaultStoreID(String defaultStoreID) {
        DefaultStoreID = defaultStoreID;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getUpdatedBy() {
        return UpdatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        UpdatedBy = updatedBy;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        LastUpdate = lastUpdate;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getConfirmPassword() {
        return ConfirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        ConfirmPassword = confirmPassword;
    }

    public String getNewEmail() {
        return NewEmail;
    }

    public void setNewEmail(String newEmail) {
        NewEmail = newEmail;
    }

    public String getConfirmEmail() {
        return ConfirmEmail;
    }

    public void setConfirmEmail(String confirmEmail) {
        ConfirmEmail = confirmEmail;
    }

    public String getNewPassword() {
        return NewPassword;
    }

    public void setNewPassword(String newPassword) {
        NewPassword = newPassword;
    }

    public String getDateOfBirthStringFormatted() {
        return DateOfBirthStringFormatted;
    }

    public void setDateOfBirthStringFormatted(String dateOfBirthStringFormatted) {
        DateOfBirthStringFormatted = dateOfBirthStringFormatted;
    }

    public String getOTPCode() {
        return OTPCode;
    }

    public void setOTPCode(String OTPCode) {
        this.OTPCode = OTPCode;
    }

    public String getOTPCodeCreatedDate() {
        return OTPCodeCreatedDate;
    }

    public void setOTPCodeCreatedDate(String OTPCodeCreatedDate) {
        this.OTPCodeCreatedDate = OTPCodeCreatedDate;
    }

    public String getValidateOTPDate() {
        return ValidateOTPDate;
    }

    public void setValidateOTPDate(String validateOTPDate) {
        ValidateOTPDate = validateOTPDate;
    }

    public String getDateOfBirthExists() {
        return DateOfBirthExists;
    }

    public void setDateOfBirthExists(String dateOfBirthExists) {
        DateOfBirthExists = dateOfBirthExists;
    }

    public String getFaspayToken() {
        return FaspayToken;
    }

    public void setFaspayToken(String faspayToken) {
        FaspayToken = faspayToken;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }

    public boolean isConfirmed() {
        return IsConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        IsConfirmed = confirmed;
    }

    public boolean isSubscribed() {
        return IsSubscribed;
    }

    public void setSubscribed(boolean subscribed) {
        IsSubscribed = subscribed;
    }

    public boolean isAllowSMS() {
        return AllowSMS;
    }

    public void setAllowSMS(boolean allowSMS) {
        AllowSMS = allowSMS;
    }

    public boolean isNewsLetterSubscriber() {
        return IsNewsLetterSubscriber;
    }

    public void setNewsLetterSubscriber(boolean newsLetterSubscriber) {
        IsNewsLetterSubscriber = newsLetterSubscriber;
    }

    public boolean isUpload() {
        return IsUpload;
    }

    public void setUpload(boolean upload) {
        IsUpload = upload;
    }

    public boolean isActivated() {
        return IsActivated;
    }

    public void setActivated(boolean activated) {
        IsActivated = activated;
    }

    public boolean isMobileVerified() {
        return MobileVerified;
    }

    public void setMobileVerified(boolean mobileVerified) {
        MobileVerified = mobileVerified;
    }

    public boolean isOTPValidationExpired() {
        return OTPValidationExpired;
    }

    public void setOTPValidationExpired(boolean OTPValidationExpired) {
        this.OTPValidationExpired = OTPValidationExpired;
    }

    public boolean isFromOtherSystem() {
        return IsFromOtherSystem;
    }

    public void setFromOtherSystem(boolean fromOtherSystem) {
        IsFromOtherSystem = fromOtherSystem;
    }

    public int getTypePushEmail() {
        return TypePushEmail;
    }

    public void setTypePushEmail(int typePushEmail) {
        TypePushEmail = typePushEmail;
    }

    public int getOTPCount() {
        return OTPCount;
    }

    public void setOTPCount(int OTPCount) {
        this.OTPCount = OTPCount;
    }

    public int getOTPAvailable() {
        return OTPAvailable;
    }

    public void setOTPAvailable(int OTPAvailable) {
        this.OTPAvailable = OTPAvailable;
    }

    public com.indomaret.klikindomaret.model.Address[] getAddress() {
        return Address;
    }

    public void setAddress(com.indomaret.klikindomaret.model.Address[] address) {
        Address = address;
    }
}
