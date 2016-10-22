package com.lopezreynau.flashlight;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    private boolean flashLightOn;
    private boolean screenLightOn;

    private Camera camera;
    private Camera.Parameters params;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageButton flashLight = (ImageButton) findViewById(R.id.flashLightButton);
        flashLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flashLightOn) {
                    turnOffFlash();
                    flashLightOn = false;
                    v.setSelected(screenLightOn);
                }
                else {
                    turnOnFlash();
                    flashLightOn = true;
                    v.setSelected(false);
                }
                v.setActivated(flashLightOn);
            }
        });

        final ImageButton screenLight = (ImageButton) findViewById(R.id.screenLightButton);
        screenLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View b = findViewById(R.id.flashLightButton);
                WindowManager.LayoutParams layout = getWindow().getAttributes();
                if (screenLightOn) {
                    layout.screenBrightness = -1f;
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                            .getColor(R.color.colorPrimary)));
                    screenLightOn = false;
                } else {
                    layout.screenBrightness = 1f;
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources()
                            .getColor(R.color.black_overlay)));
                    screenLightOn = true;
                }
                v.setSelected(screenLightOn);
                if (!flashLightOn) b.setSelected(screenLightOn);
                getWindow().setAttributes(layout);
            }
        });

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle("Error");
            alert.setMessage("Sorry, your device doesn't support flash light!");
            alert.show();
            return;
        }
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_help:
                Intent help = new Intent(this, Help.class);
                startActivity(help);
                break;
            case R.id.action_about:
                Intent about = new Intent(this, About.class);
                startActivity(about);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getCamera() {

        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            }catch (Exception e) {
                Log.e("getCamera", e.getMessage());
            }
        }
    }

    private void turnOnFlash() {
        if(camera == null || params == null)
            getCamera();
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
    }

    private void turnOffFlash() {
        if (camera == null || params == null)
            getCamera();
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
    }


}
