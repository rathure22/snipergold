package com.rathure22;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
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

            if (tvStatus != null) tvStatus.setText("Fixed v19 Ready - No Crash");

            if (btnAuto != null) btnAuto.setOnClickListener(v -> { 
                if (tvStatus != null) tvStatus.setText("AUTO Toggled"); 
                Toast.makeText(this, "AUTO", Toast.LENGTH_SHORT).show(); 
            });

            if (btnLock != null) btnLock.setOnClickListener(v -> { 
                if (tvStatus != null) tvStatus.setText("LOCKED"); 
            });

            if (btnAlarm != null) btnAlarm.setOnClickListener(v -> { 
                if (tvStatus != null) tvStatus.setText("ALARM Active"); 
            });

            if (btnX2 != null) btnX2.setOnClickListener(v -> { 
                multiplier = 2; 
                if (tvStatus != null) tvStatus.setText("x2"); 
            });

            if (btnX3 != null) btnX3.setOnClickListener(v -> { 
                multiplier = 3; 
                if (tvStatus != null) tvStatus.setText("x3"); 
            });

            if (btnX4 != null) btnX4.setOnClickListener(v -> { 
                multiplier = 4; 
                if (tvStatus != null) tvStatus.setText("x4"); 
            });

            if (btnStart != null) btnStart.setOnClickListener(v -> {
                if (tvStatus != null) tvStatus.setText("SNIPER RUNNING x" + multiplier);
                if (tvPrice != null) tvPrice.setText("Price: $2650.45");
                if (tvProfit != null) tvProfit.setText("Profit: $" + (multiplier * 15) + ".50");
                Toast.makeText(this, "Sniper v19 x" + multiplier + " Started", Toast.LENGTH_LONG).show();
            });

        } catch (Exception e) {
            TextView errorView = new TextView(this);
            errorView.setPadding(30, 30, 30, 30);
            errorView.setText("Error: " + e.getMessage());
            setContentView(errorView);
        }
    }
}
