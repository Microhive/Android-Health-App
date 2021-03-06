package dk.itu.ubicomp.android.allergytracker.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import dk.itu.ubicomp.android.allergytracker.BarCode.BarcodeCaptureActivity;
import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProduct;
import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProductDb;
import dk.itu.ubicomp.android.allergytracker.R;

public class MainActivity extends AppCompatActivity implements AllergyProductItemFragment.OnListFragmentInteractionListener {

    private static final int RC_BARCODE_CAPTURE = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AllergyProductItemFragment fragment;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateAllergyActivity.class));
            }
        });

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new AllergyProductItemFragment();
        fragmentTransaction.add(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_barcode_scan) {
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
            intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(AllergyProduct item) {

        Intent intent = new Intent(MainActivity.this, MainCRUDActivity.class);
        intent.putExtra("ALLERGYITEM", item);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Log.d("SCANNING FROM MAIN", "Barcode read: " + barcode.displayValue);

                    AllergyProductItemFragment fragment = (AllergyProductItemFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
                    fragment.setQueryText(barcode.displayValue);

                    AllergyProduct duplicateEntry = AllergyProductDb.getInstance(this).getByBarcode(barcode.displayValue);
                    if (duplicateEntry != null)
                    {
                        Intent intent = new Intent(MainActivity.this, MainCRUDActivity.class);
                        intent.putExtra("ALLERGYITEM", duplicateEntry);
                        startActivity(intent);
                    }

                } else {
                    Log.d("SCANNING FROM MAIN", "No barcode captured, intent data is null");

                    AllergyProductItemFragment fragment = (AllergyProductItemFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
                }
            } else {

                AllergyProductItemFragment fragment = (AllergyProductItemFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
                fragment.setQueryText("ERROR");
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
