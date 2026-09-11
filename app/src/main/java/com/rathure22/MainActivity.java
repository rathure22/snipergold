
package com.rathure22;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.widget.SwitchCompat;

public class MainActivity extends AppCompatActivity implements TradeManager.Listener {

    private TradeManager tradeManager;
    private MaterialButton btnMaster, btnPro, btnExpert;
    private MaterialButton lot001, lot002, lot003, lot040;
    private MaterialButton tfM1, tfM5, tfM15, tfM30;
    private SwitchCompat switchAuto, switchLock, switchAlarm;
    private MaterialButton btnSell, btnBuy;
    private String currentTimeframe = "M5";
    private boolean autoEnabled = false;
    private boolean alarmEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tradeManager = new TradeManager(this);
        initViews();
        setupListeners();
        createNotificationChannel();
        setTier(TradeManager.Tier.MASTER);
        selectTimeframe("M5");
        startRealtimeMonitor();
    }

    private void initViews() {
        btnMaster = findViewById(R.id.btnMaster);
        btnPro = findViewById(R.id.btnPro);
        btnExpert = findViewById(R.id.btnExpert);
        lot001 = findViewById(R.id.lot001);
        lot002 = findViewById(R.id.lot002);
        lot003 = findViewById(R.id.lot003);
        lot040 = findViewById(R.id.lot040);
        tfM1 = findViewById(R.id.tfM1);
        tfM5 = findViewById(R.id.tfM5);
        tfM15 = findViewById(R.id.tfM15);
        tfM30 = findViewById(R.id.tfM30);
        switchAuto = findViewById(R.id.switchAuto);
        switchLock = findViewById(R.id.switchLock);
        switchAlarm = findViewById(R.id.switchAlarm);
        btnSell = findViewById(R.id.btnSell);
        btnBuy = findViewById(R.id.btnBuy);
    }

    private void setupListeners() {
        btnMaster.setOnClickListener(v -> setTier(TradeManager.Tier.MASTER));
        btnPro.setOnClickListener(v -> setTier(TradeManager.Tier.PRO));
        btnExpert.setOnClickListener(v -> setTier(TradeManager.Tier.EXPERT));
        lot001.setOnClickListener(v -> tradeManager.setManualLot(0.01));
        lot002.setOnClickListener(v -> tradeManager.setManualLot(0.02));
        lot003.setOnClickListener(v -> tradeManager.setManualLot(0.03));
        lot040.setOnClickListener(v -> tradeManager.lockProfit());
        tfM1.setOnClickListener(v -> selectTimeframe("M1"));
        tfM5.setOnClickListener(v -> selectTimeframe("M5"));
        tfM15.setOnClickListener(v -> selectTimeframe("M15"));
        tfM30.setOnClickListener(v -> selectTimeframe("M30"));
        switchAuto.setOnCheckedChangeListener((b, checked) -> {
            autoEnabled = checked;
            Toast.makeText(this, checked ? "AUTO ON" : "AUTO OFF", Toast.LENGTH_SHORT).show();
        });
        switchLock.setOnCheckedChangeListener((b, checked) -> {
            if (checked) tradeManager.lockProfit();
            else { tradeManager.unlock(); Toast.makeText(this, "UNLOCKED", Toast.LENGTH_SHORT).show(); }
        });
        switchAlarm.setOnCheckedChangeListener((b, checked) -> {
            alarmEnabled = checked;
            Toast.makeText(this, checked ? "ALARM ON" : "ALARM OFF", Toast.LENGTH_SHORT).show();
        });
        btnSell.setOnClickListener(v -> executeTrade("SELL"));
        btnBuy.setOnClickListener(v -> executeTrade("BUY"));
    }

    private void setTier(TradeManager.Tier tier) {
        tradeManager.setTier(tier);
        btnMaster.setAlpha(tier == TradeManager.Tier.MASTER ? 1f : 0.5f);
        btnPro.setAlpha(tier == TradeManager.Tier.PRO ? 1f : 0.5f);
        btnExpert.setAlpha(tier == TradeManager.Tier.EXPERT ? 1f : 0.5f);
    }

    private void selectTimeframe(String tf) {
        currentTimeframe = tf;
        tfM1.setAlpha(tf.equals("M1") ? 1f : 0.5f);
        tfM5.setAlpha(tf.equals("M5") ? 1f : 0.5f);
        tfM15.setAlpha(tf.equals("M15") ? 1f : 0.5f);
        tfM30.setAlpha(tf.equals("M30") ? 1f : 0.5f);
    }

    private void executeTrade(String direction) {
        if (tradeManager.isLocked()) {
            Toast.makeText(this, "LOCKED - Unlock first", Toast.LENGTH_LONG).show();
            return;
        }
        double lot = tradeManager.getCurrentLot();
        Toast.makeText(this, direction + " " + lot + " lot | " + currentTimeframe, Toast.LENGTH_SHORT).show();
    }

    private void startRealtimeMonitor() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(() -> {
                        if (!autoEnabled) return;
                        if (alarmEnabled && Math.random() > 0.7) {
                            boolean isBuy = Math.random() > 0.5;
                            sendAlarmNotification(isBuy ? "BUY Signal " + currentTimeframe : "SELL Signal " + currentTimeframe, "Lot " + tradeManager.getCurrentLot());
                        }
                    });
                } catch (InterruptedException e) { break; }
            }
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("sniper_alarm", "SniperGold Alarm", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void sendAlarmNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sniper_alarm")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @Override public void onLockTriggered(String reason) {
        runOnUiThread(() -> { switchLock.setChecked(true); Toast.makeText(this, reason, Toast.LENGTH_LONG).show(); });
    }
    @Override public void onLotChanged(double newLot, int multiplier) {
        runOnUiThread(() -> Toast.makeText(this, "Lot: " + newLot + " x" + multiplier, Toast.LENGTH_SHORT).show());
    }
    @Override public void onAlarm(String title, String msg) {
        if (alarmEnabled) sendAlarmNotification(title, msg);
    }
}
