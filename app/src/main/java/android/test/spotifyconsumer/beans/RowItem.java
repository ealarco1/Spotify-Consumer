package android.test.spotifyconsumer.beans;

import android.graphics.Bitmap;

public class RowItem {
    private String imageUrl;
    private String desc;

    public RowItem(String imageUrl, String desc) {
        this.imageUrl = imageUrl;
        this.desc = desc;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    @Override
    public String toString() {
        return desc;
    }
}