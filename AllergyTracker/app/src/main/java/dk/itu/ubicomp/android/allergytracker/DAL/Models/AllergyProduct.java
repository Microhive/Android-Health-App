package dk.itu.ubicomp.android.allergytracker.DAL.Models;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Eiler on 06/04/2016.
 */
public class AllergyProduct implements Serializable {

    private Long id;
    private String title;
    private String description;
    private String barcode;
    private byte[] image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBarcode() { return barcode; }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public byte[] getImage() { return image; }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return title;
    }

    public String getText()
    {
        return getTitle() + "," + getDescription() + "," + getBarcode();
    }
}
