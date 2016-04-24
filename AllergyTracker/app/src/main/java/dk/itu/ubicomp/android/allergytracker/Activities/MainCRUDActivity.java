package dk.itu.ubicomp.android.allergytracker.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProduct;
import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProductDb;
import dk.itu.ubicomp.android.allergytracker.R;

public class MainCRUDActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView barcodeTextView;

    private AllergyProduct mAllergyProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_crud);

        titleTextView = (TextView)findViewById(R.id.title);
        descriptionTextView = (TextView)findViewById(R.id.description);
        barcodeTextView = (TextView)findViewById(R.id.barcode);

        mAllergyProduct = (AllergyProduct)getIntent().getSerializableExtra("ALLERGYITEM");

        titleTextView.setText(mAllergyProduct.getTitle().toString());
        descriptionTextView.setText(mAllergyProduct.getDescription().toString());
        barcodeTextView.setText(mAllergyProduct.getBarcode().toString());

        findViewById(R.id.update_button).setOnClickListener(this);
        findViewById(R.id.delete_button).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.update_button)
        {
            Boolean fieldsAreOK = true;
            if (titleTextView.getText().toString().trim().equals(""))
            {
                titleTextView.setError(getString(R.string.activity_allergy_title_required));
                fieldsAreOK = false;
            }

            if (barcodeTextView.getText().toString().trim().equals(""))
            {
                barcodeTextView.setError(getString(R.string.activity_allergy_barcode_required));
                fieldsAreOK = false;
            }

            AllergyProduct temp = AllergyProductDb.getInstance(this).getByBarcode(barcodeTextView.getText().toString());
            if (temp != null && !temp.getId().equals(mAllergyProduct.getId()))
            {
                barcodeTextView.setError(getString(R.string.activity_allergy_barcode_is_duplicate_required));
                fieldsAreOK = false;
            }

            if (fieldsAreOK)
            {
                AllergyProduct item = new AllergyProduct();
                item.setId(mAllergyProduct.getId());
                item.setTitle(titleTextView.getText().toString());
                item.setDescription(descriptionTextView.getText().toString());
                item.setBarcode(barcodeTextView.getText().toString());
                AllergyProductDb.getInstance(this).update(item);
                this.finish();
            }
        }

        if (v.getId() == R.id.delete_button)
        {
            AllergyProductDb.getInstance(this).remove(mAllergyProduct.getId());
            this.finish();
        }
    }

}
