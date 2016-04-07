package com.uniform_imperials.herald.activities;

import android.os.Bundle;
import android.util.Log;

import com.google.zxing.Result;
import com.uniform_imperials.herald.BaseActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Sean Johnson on 4/7/2016.
 */
public class CodeScannerActivity extends BaseActivity
        implements ZXingScannerView.ResultHandler {

    /**
     * ZXing scanner instance.
     */
    private ZXingScannerView mScanner;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // Instantiate a scanner view.
        this.mScanner = new ZXingScannerView(this);

        // Set the content view and
        this.setContentView(this.mScanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mScanner.setResultHandler(this);
        this.mScanner.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.mScanner.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // TODO: Read the channel data out of the result, write to config db.
        Log.v("CHAN_QR", rawResult.getText());
        Log.v("CHAN_QR", rawResult.getBarcodeFormat().toString());

        this.mScanner.resumeCameraPreview(this);
    }
}
