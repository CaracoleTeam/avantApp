package com.avant.joao.avant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.avant.joao.avant.entities.Gait;
import com.avant.joao.avant.utils.ParcelablePatient;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.LogRecord;

public class GraphActivity extends AppCompatActivity {

    ArrayList<Gait> mGaits;
    ParcelablePatient mParcelablePatient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        mGaits = (ArrayList<Gait>) getIntent().getSerializableExtra(GaitsActivity.GAITS_EXTRA);

        mParcelablePatient = (ParcelablePatient) getIntent().getSerializableExtra(GaitsActivity.PATIENT_EXTRA);

        getSupportActionBar().setTitle(R.string.graph_activity_title);
        GraphView graph = (GraphView) findViewById(R.id.graph);

        int size = mGaits.size();

        DataPoint[] cadencia = new DataPoint[size];

        for(int i = 0;i<size;i++){
            cadencia[i] = new DataPoint(i,mGaits.get(i).getCadence());
        }

        graph.getViewport().setScrollable(true);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(cadencia);

        series.isDrawDataPoints();
        series.setDrawDataPoints(true);

        series.setColor(Color.rgb(237,152,185));
        graph.addSeries(series);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.graph_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_gaits_item:
                saveGaits();
                Toast.makeText(getApplicationContext(),getString(R.string.data_saved),Toast.LENGTH_LONG).show();

        }
        return super.onOptionsItemSelected(item);
    }

    private void saveGaits()  {
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Avant"+"/Caminhadas"+ "/"+String.valueOf(mParcelablePatient.getPatientName()));

        boolean var = false;
        if (!folder.exists())
            var = folder.mkdirs();

        System.out.println("" + var);

        final String filename = folder.toString() + "/" + "Geral.csv";




        final Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {

            }
        };

        new Thread() {
            public void run() {
                try {

                    FileWriter fw = new FileWriter(filename);

                    fw.append("GaitId");
                    fw.append(",");

                    fw.append("Cadence");
                    fw.append(",");

                    fw.append("Time");
                    fw.append(",");

                    fw.append("LeftSteps");
                    fw.append(",");

                    fw.append("RightSteps");
                    fw.append(",");

                    fw.append("TotalSteps");
                    fw.append(",");

                    fw.append("Date");
                    fw.append(",");

                    fw.append("\n");

                    for (Gait gait: mGaits){
                        fw.append(String.valueOf(gait.getGid()));
                        fw.append(",");

                        fw.append(String.valueOf(gait.getCadence()));
                        fw.append(",");

                        fw.append(String.valueOf(gait.getTime()));
                        fw.append(",");

                        fw.append(String.valueOf(gait.getlSteps()));
                        fw.append(",");

                        fw.append(String.valueOf(gait.getrSteps()));
                        fw.append(",");

                        fw.append(String.valueOf(gait.getTotalSteps()));
                        fw.append(",");

                        fw.append(String.valueOf(gait.getGaitDay() + "/" + gait.getGaitMonth() + "/" +gait.getGaitYear()));
                        fw.append(",");

                        fw.append("\n");
                    }

                    fw.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);

            }
        }.start();
    }


}
