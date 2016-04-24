package dk.itu.ubicomp.android.allergytracker.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.ByteArrayInputStream;

import dk.itu.ubicomp.android.allergytracker.BarCode.BarcodeCaptureActivity;
import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProduct;
import dk.itu.ubicomp.android.allergytracker.DAL.Models.AllergyProductDb;
import dk.itu.ubicomp.android.allergytracker.R;

public class CreateAllergyActivity extends AppCompatActivity implements View.OnClickListener{

    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private TextView titleTextView;
    private TextView descriptionTextView;
    private TextView barcodeTextView;
    private ImageView imageView;
    private Button readButton;
    private Button saveButton;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy_create);

        titleTextView = (TextView)findViewById(R.id.title_value);
        descriptionTextView = (TextView)findViewById(R.id.description_value);
        barcodeTextView = (TextView)findViewById(R.id.barcode_value);
        imageView = (ImageView)findViewById(R.id.img_thumbnail);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);

        readButton = (Button) findViewById(R.id.read_barcode);
        saveButton = (Button) findViewById(R.id.save_button);
        if (readButton != null)
            readButton.setOnClickListener(this);
        if (saveButton != null)
            saveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }

        if (v.getId() == R.id.save_button)
        {
            Boolean fieldsAreOK = true;
            if (titleTextView.getText().toString().trim().equals(""))
            {
                titleTextView.setError(getString(R.string.activity_allergy_title_required));
                fieldsAreOK = false;
            }

            if (barcodeTextView.getText().toString().equals(""))
            {
                barcodeTextView.setError(getString(R.string.activity_allergy_barcode_required));
                fieldsAreOK = false;
            }

            if (barcodeExists(barcodeTextView.getText().toString()))
            {
                barcodeTextView.setError(getString(R.string.activity_allergy_barcode_is_duplicate_required));
                fieldsAreOK = false;
            }

            if (fieldsAreOK)
            {
                AllergyProduct item = new AllergyProduct();
                item.setTitle(titleTextView.getText().toString());
                item.setDescription(descriptionTextView.getText().toString());
                item.setBarcode(barcodeTextView.getText().toString());

                AllergyProductDb.getInstance(this).create(item);

                this.finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    byte[] byteBitMap = data.getByteArrayExtra(BarcodeCaptureActivity.PictureObject);
                    barcodeTextView.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    barcodeExistsPopup(barcode.displayValue);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(byteBitMap);
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);

                    imageView.setImageBitmap(theImage);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
//                statusMessage.setText(String.format(getString(R.string.barcode_error),
//                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void barcodeExistsPopup(String barcodeString)
    {
        final AllergyProduct duplicateEntry = AllergyProductDb.getInstance(this).getByBarcode(barcodeString);

        if (duplicateEntry == null)
            return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.dialog_create_message)
                .setTitle(R.string.dialog_create_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(CreateAllergyActivity.this, MainCRUDActivity.class);
                intent.putExtra("ALLERGYITEM", duplicateEntry);
                startActivity(intent);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.create().show();
    }

    private boolean barcodeExists(String barcodeString)
    {
        return AllergyProductDb.getInstance(this).barcodeExists(barcodeTextView.getText().toString());
    }
}
