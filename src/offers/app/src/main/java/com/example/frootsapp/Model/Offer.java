package com.example.frootsapp.Model;

public class Offer {
    private String offername;
    private String promocode;
    private String offerdescription;
    private String offerimage;


    public Offer() {
    }

    public String getOffername() {
        return offername;
    }

    public void setOffername(String offername) {
        this.offername = offername;
    }

    public String getPromocode() {
        return promocode;
    }

    public void setPromocode(String promocode) {
        this.promocode = promocode;
    }

    public String getOfferdescription() {
        return offerdescription;
    }

    public void setOfferdescription(String offerdescription) {
        this.offerdescription = offerdescription;
    }

    public void setOfferimage(String offerimage) {
        this.offerimage = offerimage;
    }

    public String getOfferimage() {
        return offerimage;
    }


}

