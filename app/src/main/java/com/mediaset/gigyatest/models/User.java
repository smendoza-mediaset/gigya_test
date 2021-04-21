package com.mediaset.gigyatest.models;

import com.gigya.android.sdk.account.models.GigyaAccount;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by yigitozen on 8/9/16.
 */
public class User extends GigyaAccount implements Serializable {

    @SerializedName("UID")
    private transient String uID;
    @SerializedName("UIDSignature")
    private transient String uIDSignature;
    @SerializedName("callId")
    private transient String callId;
    @SerializedName("created")
    private transient String created;
    @SerializedName("createdTimestamp")
    private transient long createdTimestamp;
    @SerializedName("data")
    private Data data;
    @SerializedName("errorCode")
    private transient int errorCode;
    @SerializedName("isActive")
    private transient boolean isActive;
    @SerializedName("isLockedOut")
    private transient boolean isLockedOut;
    @SerializedName("isRegistered")
    private transient boolean isRegistered;
    @SerializedName("isVerified")
    private transient boolean isVerified;
    @SerializedName("lastLogin")
    private transient String lastLogin;
    @SerializedName("lastLoginTimestamp")
    private transient long lastLoginTimestamp;
    @SerializedName("lastUpdated")
    private transient String lastUpdated;
    @SerializedName("lastUpdatedTimestamp")
    private transient long lastUpdatedTimestamp;
    @SerializedName("loginProvider")
    private transient String loginProvider;
    @SerializedName("oldestDataUpdated")
    private transient String oldestDataUpdated;
    @SerializedName("oldestDataUpdatedTimestamp")
    private transient long oldestDataUpdatedTimestamp;
    @SerializedName("Profile")
    private transient Profile profile;
    @SerializedName("registered")
    private transient String registered;
    @SerializedName("registeredTimestamp")
    private transient long registeredTimestamp;
    @SerializedName("signatureTimestamp")
    private transient String signatureTimestamp;
    @SerializedName("socialProviders")
    private transient String socialProviders;
    @SerializedName("statusCode")
    private transient int statusCode;
    @SerializedName("statusReason")
    private transient String statusReason;
    @SerializedName("time")
    private transient String time;
    @SerializedName("verified")
    private transient String verified;
    @SerializedName("verifiedTimestamp")
    private transient long verifiedTimestamp;

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public class Data implements Serializable {

        @SerializedName("allowMailing")
        boolean allowMailing;
        @SerializedName("allowNewsletter")
        boolean allowNewsletter;
        @SerializedName("allowTelemania")
        boolean allowTelemania;
        @SerializedName("terms")
        boolean terms;
        @SerializedName("zuoraId")
        String zuoraId;
        @SerializedName("privacyPolicyCurrentVersion")
        String privacyPolicyCurrentVersion;
        @SerializedName("privacyPolicyDate")
        String privacyPolicyDate;
        @SerializedName("newsletterDate")
        String newsletterDate;
        @SerializedName("mailingDate")
        String mailingDate;
        @SerializedName("salesforceUID")
        String salesforceUID;
        @SerializedName("isMiteleClub")
        boolean isMiteleClub;
        @SerializedName("hasPendingPayment")
        boolean hasPendingPayment;

        public boolean isOverdue() {

            return hasPendingPayment;
        }
    }

    public class Profile implements Serializable {

        @SerializedName("country")
        String country;
        @SerializedName("email")
        String email;
        @SerializedName("firstName")
        String firstName;
        @SerializedName("gender")
        String gender;
        @SerializedName("age")
        Integer age;
        @SerializedName("lastName")
        String lastName;
        @SerializedName("photoURL")
        String photoURL;
        @SerializedName("thumbnailURL")
        String thumbnailURL;

    }

    private String userName;

    public String getUID() {
        return super.getUID();
    }

    public String getUIDSignature() {
        return super.getUIDSignature();
    }

    public void setUIDSignature(String uIDSignature) {
        super.setUIDSignature(uIDSignature);
        this.uIDSignature = uIDSignature;
    }

    public String getCallId() {
        return callId;
    }

    public String getCreated() {
        return created;
    }

    //    public long getCreatedTimestamp() {
//        return createdTimestamp;
//    }


    public Data getData() {
        return data;
    }


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean getIsActive() {
        return super.isActive();
    }

    public boolean getIsLockedOut() {
        return isLockedOut;
    }

    public boolean getIsRegistered() {
        return isRegistered;
    }

    public boolean getIsVerified() {
        return isVerified;
    }

    public String getLastLogin() {
        return lastLogin;
    }

//    public long getLastLoginTimestamp() {
//        return lastLoginTimestamp;
//    }

    public String getLastUpdated() {
        return lastUpdated;
    }

//    public long getLastUpdatedTimestamp() {
//        return lastUpdatedTimestamp;
//    }

    public String getLoginProvider() {
        return loginProvider;
    }

    public String getOldestDataUpdated() {
        return oldestDataUpdated;
    }

//    public long getOldestDataUpdatedTimestamp() {
//        return oldestDataUpdatedTimestamp;
//    }
//

    public String getRegistered() {
        return registered;
    }

    //    public long getRegisteredTimestamp() {
//        return registeredTimestamp;
//    }
//
    public Long getSignatureTimestamp() {
        return super.getSignatureTimestamp();
    }

    public void setSignatureTimestamp(String signatureTimestamp) {
        this.signatureTimestamp = signatureTimestamp;
    }


    public String getSocialProviders() {
        return socialProviders;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVerified() {
        return verified;
    }

//    public long getVerifiedTimestamp() {
//        return verifiedTimestamp;
//    }

    public String getUserName() {
        return userName;
    }


    public String getuID() {
        return uID;
    }

    public String getuIDSignature() {
        return super.getUIDSignature();
    }

    public boolean isActive() {
        return super.isActive();
    }

    public boolean isLockedOut() {
        return isLockedOut;
    }

    public boolean isRegistered() {
        return isRegistered;
    }

    public boolean isVerified() {
        return isVerified;
    }

    public String getPrivacyPolicyCurrentVersion() {
        return data.privacyPolicyCurrentVersion;
    }

    public boolean hasTermsAccepted() {
        return data.terms;
    }

    public boolean isZuoraUser() {
        return data != null && data.zuoraId != null && !data.zuoraId.isEmpty();
    }

    public String getPrivacyPolicyDate() {
        return data.privacyPolicyDate;
    }

    public boolean getNewsletterAccepted() {
        if (data != null) {
            return data.allowNewsletter;
        } else {
            return false;
        }
    }

    public String getNewsletterDate() {
        return data.newsletterDate;
    }

    public boolean getMailingAccepted() {
        if (data != null) {
            return data.allowMailing;
        } else {
            return false;
        }
    }

    public boolean isMiteleClub() {
        if (data != null) {
            return data.isMiteleClub;
        } else {
            return false;
        }
    }

    public String getMailingDate() {
        return data.mailingDate;
    }

    public String getSalesforceId() {
        if (data == null) {
            return null;
        } else {
            return data.salesforceUID;
        }
    }
    public void setData(Data data) {
        this.data = data;
    }

    public boolean hasPendingPayment() {
        if (data != null) {
            return data.hasPendingPayment;
        } else {
            return false;
        }
    }

}

