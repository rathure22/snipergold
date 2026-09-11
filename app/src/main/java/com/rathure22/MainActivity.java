package com.rathure22;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {
    private TextView tvStatus, tvPrice, tvProfit;
    private int multiplier = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_main);
            
            tvStatus = findViewById(R.id.tvStatus);
            tvPrice = findViewById(R.id.tvPrice);
            tvProfit = findViewById(R.id.tvProfit);
            
            Button btnAuto = findViewById(R.id.btnAuto);
            Button btnLock = findViewById(R.id.btnLock);
            Button btnAlarm = findViewById(R.id.btnAlarm);
            Button btnX2 = findViewById(R.id.btnX2);
            Button btnX3 = findViewById(R.id.btnX3);
            Button btnX4 = findViewById(R.id.btnX4);
            Button btnStart = findViewById(R.id.btnStart);

            if(tvStatus != null) tvStatus.setText("Fixed v19 Ready");

            if(btnAuto != null) btnAuto.setOnClickListener(v -> {
                if(tvStatus != null) tvStatus.setText("AUTO Toggled");
                Toast.makeText(this, "AUTO", Toast.LENGTH_SHORT).show();
            });
            if(btnLock != null) btnLock.setOnClickListener(v -> {
                if(tvStatus != null) tvStatus.setText("LOCKED / UNLOCKED");
            });
            if(btnAlarm != null) btnAlarm.setOnClickListener(v -> {
                if(tvStatus != null) tvStatus.setText("ALARM Active");
            });
            if(btnX2 != null) btnX2.setOnClickListener(v -> {
                multiplier = 2;
                if(tvStatus != null) tvStatus.setText("x2 Selected");
            });
            if(btnX3 != null) btnX3.setOnClickListener(v -> {
                multiplier = 3;
                if(tvStatus != null) tvStatus.setText("x3 Selected");
            });
            if(btnX4 != null) btnX4.setOnClickListener(v -> {
                multiplier = 4;
                if(tvStatus != null) tvStatus.setText("x4 Selected");
            });
            if(btnStart != null) btnStart.setOnClickListener(v -> {
                if(tvStatus != null) tvStatus.setText("SNIPER RUNNING x" + multiplier);
                if(tvPrice != null) tvPrice.setText("Price: $2650.45");
                if(tvProfit != null) tvProfit.setText("Profit: $" + (multiplier * 15) + ".50");
                Toast.makeText(this, "Sniper v19 x" + multiplier + " Started - No Crash", Toast.LENGTH_LONG).show();
            });

        } catch (Exception e) {
            // Crash protection
            TextView errorView = new TextView(this);
            errorView.setText("Error: " + e.getMessage());
            errorView.setTextColor(0xFFFF0000);
            setContentView(errorView);
            Toast.makeText(this, "Fix: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
