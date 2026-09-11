package com.rathure22;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private TextView tvStatus, tvPrice, tvProfit;
    private int multiplier = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (tvStatus != null) tvStatus.setText("v19 FIXED - No Crash - Black Gold!");
        btnAuto.setOnClickListener(v -> { tvStatus.setText("AUTO Toggled"); Toast.makeText(this, "AUTO", Toast.LENGTH_SHORT).show(); });
        btnLock.setOnClickListener(v -> tvStatus.setText("LOCKED"));
        btnAlarm.setOnClickListener(v -> tvStatus.setText("ALARM Active"));
        btnX2.setOnClickListener(v -> { multiplier = 2; tvStatus.setText("x2"); });
        btnX3.setOnClickListener(v -> { multiplier = 3; tvStatus.setText("x3"); });
        btnX4.setOnClickListener(v -> { multiplier = 4; tvStatus.setText("x4"); });
        btnStart.setOnClickListener(v -> {
            tvStatus.setText("SNIPER RUNNING x" + multiplier);
            tvPrice.setText("Price: $2650.45");
            tvProfit.setText("Profit: $" + (multiplier * 15) + ".50");
            Toast.makeText(this, "Sniper v19 x" + multiplier + " Started", Toast.LENGTH_LONG).show();
        });
    }
}
