package dk.itu.ubicomp.android.allergytracker.DAL.Models;

import java.util.List;

import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProduct;

/**
 * Created by Eiler on 06/04/2016.
 */
public interface IDAL <E>{
    AllergyProduct create(E item);
    void remove(Long id);
    void update(E item);
    List<E> getItems();
}
