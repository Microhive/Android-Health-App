package dk.itu.ubicomp.android.allergytracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProduct;
import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProductDb;

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

        findViewById(R.id.read_barcode).setOnClickListener(this);
        findViewById(R.id.save_button).setOnClickListener(this);
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
                titleTextView.setError("Title is required!");
                fieldsAreOK = false;
            }

            if (barcodeTextView.getText().toString().trim().equals(""))
            {
                barcodeTextView.setError("Barcode is required!");
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
    }

}
