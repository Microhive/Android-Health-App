package dk.itu.ubicomp.android.allergytracker.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private Button takePictureButton;

    private static final int RC_BARCODE_CAPTURE = 9001;

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private static final String IMAGE_DIRECTORY_NAME = "HelloWorld";

    private static final String TAG = "BarcodeMain";
    private Bitmap bitmap;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


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
        takePictureButton = (Button) findViewById(R.id.take_image);

        if (readButton != null)
            readButton.setOnClickListener(this);
        if (saveButton != null)
            saveButton.setOnClickListener(this);
        if (takePictureButton != null)
            takePictureButton.setOnClickListener(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
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

        if (v.getId() == R.id.take_image)
        {


            // Check for the camera permission before accessing the camera.  If the
            // permission is not granted yet, request permission.
            int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            if (rc == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    captureImage();
                }

                ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                verifyStoragePermissions(this);
            } else {
                requestCameraPermission();
            }
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
                if (bitmap != null)
                {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    this.bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    byte[] bArray = bos.toByteArray();
                    item.setImage(bArray);
                }

                AllergyProductDb.getInstance(this).create(item);

                this.finish();
            }
        }
    }

    private static final int RC_HANDLE_CAMERA_PERM = 2;

    /**
     * Handles the requesting of the camera permission.
     * This includes why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };
    }

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
 * returning image / video
 */
    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        String folder_main = IMAGE_DIRECTORY_NAME;

        File f = new File(this.getExternalFilesDir("img"), folder_main);
        if (!f.exists()) {
            f.mkdirs();
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Capturing Camera Image will lauch camera app requrest image capture
     * */
    private void captureImage() {

        if (!isDeviceSupportCamera())
        {
            return;
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    barcodeTextView.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    barcodeExistsPopup(barcode.displayValue);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
//                statusMessage.setText(String.format(getString(R.string.barcode_error),
//                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            ImageView imgPreview = (ImageView) findViewById(R.id.img_thumbnail);

            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            imgPreview.setImageBitmap(bitmap);
            this.bitmap = bitmap;

        } catch (NullPointerException e) {
            e.printStackTrace();
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

    public boolean hasPermissionInManifest(Context context, String permissionName) {
        final String packageName = context.getPackageName();
        try {
            final PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            final String[] declaredPermisisons = packageInfo.requestedPermissions;
            if (declaredPermisisons != null && declaredPermisisons.length > 0) {
                for (String p : declaredPermisisons) {
                    if (p.equals(permissionName)) {
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return false;
    }

    private boolean barcodeExists(String barcodeString)
    {
        return AllergyProductDb.getInstance(this).barcodeExists(barcodeTextView.getText().toString());
    }
}
