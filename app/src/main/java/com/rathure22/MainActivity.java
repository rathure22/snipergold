package com.rathure22;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView tvStatus, tvPrice, tvProfit;
    private Button btnAuto, btnLock, btnAlarm, btnX2, btnX3, btnX4, btnStart;
    private boolean isAuto = false;
    private boolean isLocked = false;
    private int multiplier = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvStatus = findViewById(R.id.tvStatus);
        tvPrice = findViewById(R.id.tvPrice);
        tvProfit = findViewById(R.id.tvProfit);
        btnAuto = findViewById(R.id.btnAuto);
        btnLock = findViewById(R.id.btnLock);
        btnAlarm = findViewById(R.id.btnAlarm);
        btnX2 = findViewById(R.id.btnX2);
        btnX3 = findViewById(R.id.btnX3);
        btnX4 = findViewById(R.id.btnX4);
        btnStart = findViewById(R.id.btnStart);

        btnAuto.setOnClickListener(v -> {
            isAuto = !isAuto;
            tvStatus.setText(isAuto ? "AUTO: ON" : "AUTO: OFF");
            btnAuto.setText(isAuto ? "AUTO ON" : "AUTO");
        });
        btnLock.setOnClickListener(v -> {
            isLocked = !isLocked;
            tvStatus.setText(isLocked ? "LOCKED" : "UNLOCKED");
        });
        btnAlarm.setOnClickListener(v -> tvStatus.setText("ALARM: Active"));
        btnX2.setOnClickListener(v -> { multiplier = 2; tvStatus.setText("x2 Selected"); });
        btnX3.setOnClickListener(v -> { multiplier = 3; tvStatus.setText("x3 Selected"); });
        btnX4.setOnClickListener(v -> { multiplier = 4; tvStatus.setText("x4 Selected"); });
        btnStart.setOnClickListener(v -> {
            tvStatus.setText("SNIPER RUNNING x" + multiplier);
            tvPrice.setText("Price: $2650.45");
            tvProfit.setText("Profit: $" + (multiplier * 15) + ".50");
            Toast.makeText(this, "Sniper v19 x" + multiplier + " Started", Toast.LENGTH_LONG).show();
        });
    }
}
