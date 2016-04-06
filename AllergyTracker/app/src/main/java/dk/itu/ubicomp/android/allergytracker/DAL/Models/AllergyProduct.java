package dk.itu.ubicomp.android.allergytracker.DAL.Models;

/**
 * Created by Eiler on 06/04/2016.
 */
public class AllergyProduct {

    public Long id;
    public String title;
    public String description;
    public String barcode;

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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Override
    public String toString() {
        return title;
    }
}
