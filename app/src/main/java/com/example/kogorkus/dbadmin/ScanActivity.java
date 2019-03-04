package com.example.kogorkus.dbadmin;

import android.Manifest;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class ScanActivity extends Activity {

    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;


    //private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        surfaceView = findViewById(R.id.cameraprewiev);
        textView = findViewById(R.id.mTextView);

        //dbManager = DBManager.getInstance(this);

        barcodeDetector = new BarcodeDetector.Builder(this).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(640, 480)
                .setAutoFocusEnabled(true).build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ScanActivity.this, new String[] {Manifest.permission.CAMERA}, 100);
                    return;
                }
                try
                {
                    cameraSource.start(holder);
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size() != 0)
                {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(barcodes.valueAt(0).displayValue);
                        }
                    });
                }
            }
        });
    }

    public void AddBarcode(View view) {
        //dbManager.addBarcode(textView.getText().toString(), "name");

        Intent intent = new Intent(this, AddActivity.class);

        intent.putExtra("code", textView.getText().toString());
        //setResult(RESULT_OK, intent);

        startActivity(intent);
        finish();

    }
}
