package com.rathure22;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;

/**
 * MainActivity for snipergold dashboard
 * Handles: M1 M5 M15 M30, AUTO LOCK ALARM, SELL BUY, Master Pro Expert
 * Connects to TradeManager for -4 lose $3 / +6 win x2 x3 x4 logic
 */
public class MainActivity extends AppCompatActivity implements TradeManager.Listener {

    private TradeManager tradeManager;
    
    // UI
    private MaterialButton btnMaster, btnPro, btnExpert;
    private MaterialButton lot001, lot002, lot003, lot040;
    private MaterialButton tfM1, tfM5, tfM15, tfM30;
    private MaterialSwitch switchAuto, switchLock, switchAlarm;
    private MaterialButton btnSell, btnBuy;

    private String currentTimeframe = "M5";
    private boolean autoEnabled = false;
    private boolean alarmEnabled = false;

    // Signal Monitor 18 - simulated
    private int redCount = 5, yellowCount = 5, greenCount = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // your snipergold_dashboard.xml rename to activity_main

        tradeManager = new TradeManager(this);

        initViews();
        setupListeners();
        createNotificationChannel();

        // Default tier
        setTier(TradeManager.Tier.MASTER);
        selectTimeframe("M5");

        // Start realtime signal monitor
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
        // Tiers
        btnMaster.setOnClickListener(v -> setTier(TradeManager.Tier.MASTER));
        btnPro.setOnClickListener(v -> setTier(TradeManager.Tier.PRO));
        btnExpert.setOnClickListener(v -> setTier(TradeManager.Tier.EXPERT));

        // Lot sizes 0.01 0.02 0.03 0.40
        lot001.setOnClickListener(v -> tradeManager.setManualLot(0.01));
        lot002.setOnClickListener(v -> tradeManager.setManualLot(0.02));
        lot003.setOnClickListener(v -> tradeManager.setManualLot(0.03));
        lot040.setOnClickListener(v -> {
            Toast.makeText(this, "0.40 is LOCK only - hedging profit", Toast.LENGTH_SHORT).show();
            tradeManager.lockProfit();
        });

        // Timeframes M1 M5 M15 M30
        tfM1.setOnClickListener(v -> selectTimeframe("M1"));
        tfM5.setOnClickListener(v -> selectTimeframe("M5"));
        tfM15.setOnClickListener(v -> selectTimeframe("M15"));
        tfM30.setOnClickListener(v -> selectTimeframe("M30"));

        // Strategy AUTO LOCK ALARM
        switchAuto.setOnCheckedChangeListener((b, checked) -> {
            autoEnabled = checked;
            Toast.makeText(this, checked ? "AUTO Sniper ON" : "AUTO OFF", Toast.LENGTH_SHORT).show();
        });

        switchLock.setOnCheckedChangeListener((b, checked) -> {
            if (checked) {
                tradeManager.lockProfit();
            } else {
                tradeManager.unlock();
                Toast.makeText(this, "UNLOCKED - Trading resumed", Toast.LENGTH_SHORT).show();
            }
        });

        switchAlarm.setOnCheckedChangeListener((b, checked) -> {
            alarmEnabled = checked;
            Toast.makeText(this, checked ? "ALARM ON - Red/Green alert" : "ALARM OFF", Toast.LENGTH_SHORT).show();
        });

        // SELL BUY with cheatsheet SL 20 TP 50
        btnSell.setOnClickListener(v -> executeTrade("SELL", 20, 50));
        btnBuy.setOnClickListener(v -> executeTrade("BUY", 20, 50));
    }

    private void setTier(TradeManager.Tier tier) {
        tradeManager.setTier(tier);
        // UI update
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
        Toast.makeText(this, "Timeframe: " + tf, Toast.LENGTH_SHORT).show();
        // Here reload chart data for M1/M5/M15/M30 from API
    }

    private void executeTrade(String direction, int slPips, int tpPips) {
        if (tradeManager.isLocked()) {
            Toast.makeText(this, "LOCKED - Unlock first. Lose -4 = $3 protection active", Toast.LENGTH_LONG).show();
            return;
        }

        double lot = tradeManager.getCurrentLot();
        String msg = direction + " " + lot + " lot | " + currentTimeframe + " | SL " + slPips + " TP " + tpPips;
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        // TODO: Call your broker API here with lot, SL, TP
        // After trade closes, call tradeManager.onTradeClosed(profit)
        // Example: tradeManager.onTradeClosed(6.5); // win
    }

    // Realtime Monitor 18 signals - simulates red green yellow changes
    private void startRealtimeMonitor() {
        // In real app, fetch from API every 3 seconds
        // For demo, random simulation + alarm when green/red changes
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                    runOnUiThread(() -> {
                        if (!autoEnabled) return;
                        // Simulate signal change detection
                        // If green count increases -> BUY alarm
                        // If red increases -> SELL alarm
                        if (alarmEnabled && Math.random() > 0.7) {
                            boolean isBuy = Math.random() > 0.5;
                            sendAlarmNotification(
                                isBuy ? "🟢 BUY Signal " + currentTimeframe : "🔴 SELL Signal " + currentTimeframe,
                                "SniperGold M" + currentTimeframe + " - " + (isBuy ? "BUY" : "SELL") + " alert - Lot " + tradeManager.getCurrentLot()
                            );
                        }
                    });
                } catch (InterruptedException e) { break; }
            }
        }).start();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("sniper_alarm", "SniperGold Alarm", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Red Green Yellow signal alerts");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void sendAlarmNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "sniper_alarm")
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(new long[]{0, 500, 200, 500});

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    // TradeManager.Listener
    @Override
    public void onLockTriggered(String reason) {
        runOnUiThread(() -> {
            switchLock.setChecked(true);
            Toast.makeText(this, reason, Toast.LENGTH_LONG).show();
            sendAlarmNotification("🔒 AUTO LOCK", reason);
        });
    }

    @Override
    public void onLotChanged(double newLot, int multiplier) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Lot: " + newLot + " | Multiplier x" + multiplier, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAlarm(String title, String msg) {
        if (alarmEnabled) sendAlarmNotification(title, msg);
    }
}
