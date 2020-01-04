package org.qpython.qpy.main.server.model;

/**
 * Created by Hmei on 2017-07-19.
 */

public class GooglePurchaseModel {

    /**
     * orderId : GPA.1234-5678-9012-34567
     * packageName : com.example.app
     * productId : exampleSku
     * purchaseTime : 1345678900000
     * purchaseState : 0
     * developerPayload : bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ
     * purchaseToken : opaque-token-up-to-1000-characters
     */

    private String orderId;
    private String packageName;
    private String productId;
    private long   purchaseTime;
    private int    purchaseState;
    private String developerPayload;
    private String purchaseToken;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }
}
