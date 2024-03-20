package com.affixstudio.whatsapptool.ads;

public class OurAds {

public String imageLink;
public String openingLink;

    public OurAds(String imageLink, String openingLink) {
        this.imageLink = imageLink;
        this.openingLink = openingLink;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getOpeningLink() {
        return openingLink;
    }

    public void setOpeningLink(String openingLink) {
        this.openingLink = openingLink;
    }
}
