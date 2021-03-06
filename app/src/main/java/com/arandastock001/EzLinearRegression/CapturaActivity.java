package com.arandastock001.EzLinearRegression;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class CapturaActivity extends AppCompatActivity {


    SurfaceView vistaDeCamara;
    TextView textoReconocido;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    private Button btnCapturarX, btnCapturarY;
    private Boolean xCapturada, yCapturada;
    private ArrayList<String> caracteresReconocidos;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults){

        switch (requestCode){

            case RequestCameraPermissionID:
            {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        return;
                    }
                    try {
                        cameraSource.start(vistaDeCamara.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vistaDeCamara = (SurfaceView)findViewById(R.id.surface_view);
        textoReconocido = (TextView)findViewById(R.id.text_view);
        btnCapturarX = (Button)findViewById(R.id.btnCapturarColumnaX);
        btnCapturarY = (Button)findViewById(R.id.btnCapturarColumnaY);

        xCapturada = false;
        yCapturada = false;

        caracteresReconocidos = new ArrayList<>();

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!textRecognizer.isOperational()){
            Log.w("MainActivity", "No disponible");
        }else {
            cameraSource = new CameraSource.Builder(getApplicationContext(),textRecognizer).
                    setFacing(CameraSource.CAMERA_FACING_BACK).
                    setRequestedPreviewSize(1280,1024).
                    setRequestedFps(2.0f).
                    setAutoFocusEnabled(true).
                    build();

            vistaDeCamara.getHolder().addCallback(new SurfaceHolder.Callback() {

                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                       try {
                           if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                               ActivityCompat.requestPermissions(CapturaActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);

                               return;
                           }


                           cameraSource.start(vistaDeCamara.getHolder());
                       } catch (IOException e) {
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
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if(items.size() != 0){
                        textoReconocido.post(new Runnable() {
                            @Override
                            public void run() {


                                StringBuilder st = new StringBuilder();
                                for(int i = 0; i<items.size(); i++){
                                    TextBlock item = items.valueAt(i);
                                    st.append(item.getValue());
                                    st.append("\n");

                                }
                                textoReconocido.setText(st.toString());


                            }
                        });
                    }

                }










            });
        }

        btnCapturarX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String caracteresReconocidosColumnaX = textoReconocido.getText().toString();

                //descomentar siguiente linea al terminar pruebas
                caracteresReconocidos.add(caracteresReconocidosColumnaX);


                xCapturada = true;

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Capturados los valores de la columna X",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);

                toast.show();

                if((xCapturada==true) && (yCapturada==true)){


                    startActivity(new Intent(CapturaActivity.this, ResumenDeResultados.class).putExtra("caracteresReconocidos", (Serializable) caracteresReconocidos));
                    finish();
                }


            }
        });


        btnCapturarY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String caracteresReconocidosColumnaY = textoReconocido.getText().toString();


                //descomentar siguiente linea esto al terminar pruebas
                caracteresReconocidos.add(caracteresReconocidosColumnaY);




                yCapturada = true;

                Toast toast = Toast.makeText(getApplicationContext(),
                        "Capturados los valores de la columna Y",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);

                toast.show();

                if((xCapturada==true) && (yCapturada==true)){

                    startActivity(new Intent(CapturaActivity.this, ResumenDeResultados.class).putExtra("caracteresReconocidos", (Serializable) caracteresReconocidos));
                    finish();
                }


            }
        });

    }
}
