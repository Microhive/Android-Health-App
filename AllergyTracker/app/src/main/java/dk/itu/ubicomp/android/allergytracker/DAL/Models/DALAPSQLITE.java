package dk.itu.ubicomp.android.allergytracker.DAL.Models;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

import dk.itu.ubicomp.android.allergytracker.Activities.AllergyProductReaderDbHelper;

/**
 * Created by Eiler on 06/04/2016.
 */
public class DALAPSQLITE implements IDAL<AllergyProduct> {

    private SQLiteDatabase database;
    private AllergyProductReaderDbHelper dbHelper;
    private String[] allColumns = {
            AllergyProductReaderDbHelper.COLUMN_NAME_ENTRY_ID,
            AllergyProductReaderDbHelper.COLUMN_NAME_TITLE,
            AllergyProductReaderDbHelper.COLUMN_NAME_DESCRIPTION,
            AllergyProductReaderDbHelper.COLUMN_NAME_BARCODE };

    public DALAPSQLITE(Context context) {
        dbHelper = new AllergyProductReaderDbHelper(context);
        this.open();
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    @Override
    public AllergyProduct create(AllergyProduct item) {
        ContentValues values = new ContentValues();
        values.put(AllergyProductReaderDbHelper.COLUMN_NAME_TITLE, item.getTitle());
        values.put(AllergyProductReaderDbHelper.COLUMN_NAME_DESCRIPTION, item.getDescription());
        values.put(AllergyProductReaderDbHelper.COLUMN_NAME_BARCODE, item.getBarcode());
        long insertId = database.insert(AllergyProductReaderDbHelper.TABLE_ALLERGYPRODUCT, null, values);
        Cursor cursor = database.query(AllergyProductReaderDbHelper.TABLE_ALLERGYPRODUCT,
                allColumns, AllergyProductReaderDbHelper.COLUMN_NAME_ENTRY_ID + " = " + insertId, null,
                null, null, null);

        cursor.moveToFirst();
        AllergyProduct newAllergyProduct = cursorToAllergyProduct(cursor);
        cursor.close();
        return newAllergyProduct;
    }

    @Override
    public void remove(Long id) {
        database.delete(AllergyProductReaderDbHelper.TABLE_ALLERGYPRODUCT, AllergyProductReaderDbHelper.COLUMN_NAME_ENTRY_ID + "=" + id, null);
    }

    @Override
    public void update(AllergyProduct item) {
        ContentValues values = new ContentValues();
        values.put(AllergyProductReaderDbHelper.COLUMN_NAME_TITLE, item.getTitle());
        values.put(AllergyProductReaderDbHelper.COLUMN_NAME_DESCRIPTION, item.getDescription());
        values.put(AllergyProductReaderDbHelper.COLUMN_NAME_BARCODE, item.getBarcode());

        database.update(AllergyProductReaderDbHelper.TABLE_ALLERGYPRODUCT, values,
                AllergyProductReaderDbHelper.COLUMN_NAME_ENTRY_ID + "=" + item.getId(),
                null);
    }

    @Override
    public List getItems() {

        List<AllergyProduct> allergyproducts = new ArrayList<AllergyProduct>();

        Cursor cursor = database.query(AllergyProductReaderDbHelper.TABLE_ALLERGYPRODUCT,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AllergyProduct item = cursorToAllergyProduct(cursor);
            allergyproducts.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return allergyproducts;
    }

    private AllergyProduct cursorToAllergyProduct(Cursor cursor) {
        AllergyProduct item = new AllergyProduct();
        item.setId(cursor.getLong(0));
        item.setTitle(cursor.getString(1));
        item.setDescription(cursor.getString(2));
        item.setBarcode(cursor.getString(3));
        return item;
    }
}

