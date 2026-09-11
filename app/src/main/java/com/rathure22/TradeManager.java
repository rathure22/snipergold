
package com.rathure22;

public class TradeManager {

    public enum Tier { MASTER, PRO, EXPERT }

    private Tier currentTier = Tier.MASTER;
    private double baseLot = 0.01;
    private double currentLot = 0.01;

    private int consecutiveLoss = 0;
    private int consecutiveWin = 0;
    private double currentProfit = 0;
    private double totalLossThisCycle = 0;
    private int winMultiplier = 1;
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
        if (lot == 0.40) {
            if (listener != null) listener.onAlarm("LOCK LOT", "0.40 is for locking profit only");
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
        if (currentLot > 0.40) currentLot = 0.40;
        if (listener != null) listener.onLotChanged(currentLot, winMultiplier);
    }

    public void onTradeClosed(double profit) {
        currentProfit += profit;
        if (profit < 0) {
            consecutiveLoss++;
            consecutiveWin = 0;
            totalLossThisCycle += Math.abs(profit);
            winMultiplier = 1;
            updateLot();
            if (consecutiveLoss >= 4 || totalLossThisCycle >= 3.0) {
                isLocked = true;
                if (listener != null) {
                    listener.onLockTriggered("LOSE -4 = $" + totalLossThisCycle + " loss - Auto LOCK");
                }
            }
        } else {
            consecutiveWin++;
            consecutiveLoss = 0;
            totalLossThisCycle = 0;
            if (profit >= 6.0 || currentProfit >= 6.0) {
                if (consecutiveWin == 1) winMultiplier = 2;
                else if (consecutiveWin == 2) winMultiplier = 3;
                else if (consecutiveWin >= 3) winMultiplier = 4;
                updateLot();
                if (currentProfit >= 6.0) lockProfit();
            }
        }
    }

    public void lockProfit() {
        isLocked = true;
        if (listener != null) {
            listener.onLockTriggered("WIN +6 LOCK - Profit secured: $" + currentProfit);
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
