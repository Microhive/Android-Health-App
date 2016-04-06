package dk.itu.ubicomp.android.allergytracker.DAL.Models;

import android.content.Context;

import java.util.List;

/**
 * Created by Eiler on 06/04/2016.
 */

public class AllergyProductDb {

    private static AllergyProductDb mInstance = null;

    private IDAL<AllergyProduct> DALDb;

    private AllergyProductDb(Context context){
        DALDb = new DALAPSQLITE(context);
    }

    public static AllergyProductDb getInstance(Context context){
        if(mInstance == null)
        {
            mInstance = new AllergyProductDb(context);
        }
        return mInstance;
    }

    public IDAL<AllergyProduct> getDatabase(){
        return this.DALDb;
    }

    public AllergyProduct create(AllergyProduct item) {
        return DALDb.create(item);
    }

    public void remove(int id) {
        DALDb.remove(id);
    }

    public void update(AllergyProduct item) {
        DALDb.update(item);
    }

    public List<AllergyProduct> getItems() {
        return DALDb.getItems();
    }
}
