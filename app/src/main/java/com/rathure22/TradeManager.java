package com.rathure22;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

/**
 * TradeManager - handles your realtime pick logic:
 * LOSE -4 = $3 loss -> auto LOCK
 * WIN +6 OR MORE -> x2 x3 x4 multiplier
 * Lot sizes: 0.01 Master, 0.02 Pro, 0.03 Expert, 0.40 Lock only
 */
public class TradeManager {

    public enum Tier { MASTER, PRO, EXPERT }
    public enum Signal { RED_SELL, YELLOW_WAIT, GREEN_BUY }

    private Tier currentTier = Tier.MASTER;
    private double baseLot = 0.01;
    private double currentLot = 0.01;

    // Realtime Pick tracking
    private int consecutiveLoss = 0;
    private int consecutiveWin = 0;
    private double currentProfit = 0;
    private double totalLossThisCycle = 0;

    // Multiplier x2 x3 x4
    private int winMultiplier = 1; // 1,2,3,4

    private boolean isLocked = false;

    public interface Listener {
        void onLockTriggered(String reason);
        void onLotChanged(double newLot, int multiplier);
        void onAlarm(String title, String msg);
    }

    private Listener listener;

    public TradeManager(Listener listener) {
        this.listener = listener;
    }

    public void setTier(Tier tier) {
        this.currentTier = tier;
        switch (tier) {
            case MASTER: baseLot = 0.01; break;
            case PRO: baseLot = 0.02; break;
            case EXPERT: baseLot = 0.03; break;
        }
        resetMultiplier();
        updateLot();
    }

    public void setManualLot(double lot) {
        // 0.40 is reserved for LOCK only, not for entry
        if (lot == 0.40) {
            if (listener != null) listener.onAlarm("LOCK LOT", "0.40 is for locking profit only, not entry");
            return;
        }
        this.baseLot = lot;
        resetMultiplier();
        updateLot();
    }

    private void resetMultiplier() {
        winMultiplier = 1;
        currentLot = baseLot;
    }

    private void updateLot() {
        currentLot = baseLot * winMultiplier;
        // Cap max lot to 0.40 for safety
        if (currentLot > 0.40) currentLot = 0.40;
        if (listener != null) listener.onLotChanged(currentLot, winMultiplier);
    }

    // Called every time a trade closes - realtime monitor
    public void onTradeClosed(double profit) {
        currentProfit += profit;

        if (profit < 0) {
            // LOSE
            consecutiveLoss++;
            consecutiveWin = 0;
            totalLossThisCycle += Math.abs(profit);
            winMultiplier = 1; // reset x2 x3 x4
            updateLot();

            // Your rule: LOSE -4 = $3 loss -> LOCK
            if (consecutiveLoss >= 4 || totalLossThisCycle >= 3.0) {
                isLocked = true;
                if (listener != null) {
                    listener.onLockTriggered("LOSE -4 = $" + totalLossThisCycle + " loss - Auto LOCK");
                    listener.onAlarm("🔴 LOCKED", "4 straight losses / $3 loss reached. Trading paused.");
                }
            }
        } else {
            // WIN
            consecutiveWin++;
            consecutiveLoss = 0;
            totalLossThisCycle = 0;

            // Your rule: WIN +6 OR MORE -> x2 x3 x4
            if (profit >= 6.0 || currentProfit >= 6.0) {
                if (consecutiveWin == 1) winMultiplier = 2;
                else if (consecutiveWin == 2) winMultiplier = 3;
                else if (consecutiveWin >= 3) winMultiplier = 4;

                updateLot();
                if (listener != null) {
                    listener.onAlarm("🟢 WIN +" + profit + " x" + winMultiplier,
                            "Multiplier x" + winMultiplier + " activated. Next lot: " + currentLot);
                }

                // Auto lock 50% profit when +6 or more
                if (currentProfit >= 6.0) {
                    lockProfit();
                }
            }
        }
    }

    public void lockProfit() {
        isLocked = true;
        // Use 0.40 lot logic to hedge/lock
        if (listener != null) {
            listener.onLockTriggered("WIN +6 LOCK - Profit secured: $" + currentProfit + " Lot 0.40 hedge");
        }
    }

    public void unlock() {
        isLocked = false;
        consecutiveLoss = 0;
        consecutiveWin = 0;
        totalLossThisCycle = 0;
        currentProfit = 0;
        resetMultiplier();
    }

    public boolean isLocked() { return isLocked; }
    public double getCurrentLot() { return currentLot; }
    public int getWinMultiplier() { return winMultiplier; }
}
