package com.rathure22;

public class CandleExpertProDetector {
    public enum Mode { EXPERT, PRO }
    public static class Signal {
        public String pattern, type, accuracy, desc;
        public Mode mode;
        public double entry, sl, tp;
        public Signal(String p, String t, Mode m, String acc, double e, double s, double tp, String d){
            pattern=p; type=t; mode=m; accuracy=acc; entry=e; sl=s; this.tp=tp; desc=d;
        }
    }

    public Signal detectLatest18(double price, double ema50, double ema200, double rsi, String pattern, Mode mode){
        double support = 3996.7;
        double resistance = 4005.6;
        boolean up = ema50 > ema200 && price > ema50;
        boolean down = ema50 < ema200 && price < ema50;

        switch(pattern){
            case "HAMMER": if(rsi>=30&&rsi<=55&&price<=support+3) return new Signal(pattern,"BUY",mode,mode==Mode.PRO?"72%":"68%",price,support-4,resistance,"v39 Hammer Support EXPERT/PRO READY"); break;
            case "BULLISH_ENGULFING": if(price<=support+3) return new Signal(pattern,"BUY",mode,mode==Mode.PRO?"71%":"65%",price,support-3.5,resistance,"v39 Bullish Engulfing"); break;
            case "MORNING_STAR": return new Signal(pattern,"BUY",mode,mode==Mode.PRO?"74%":"69%",price,support-5,resistance+2,"v39 Morning Star STRONG BUY");
            case "SHOOTING_STAR": if(rsi>=55&&price>=resistance-3) return new Signal(pattern,"SELL",mode,mode==Mode.PRO?"73%":"67%",price,resistance+4,support,"v39 Shooting Star READY"); break;
            case "BEARISH_ENGULFING": if(price>=resistance-3) return new Signal(pattern,"SELL",mode,mode==Mode.PRO?"70%":"64%",price,resistance+3.5,support,"v39 Bearish Engulfing"); break;
            case "EVENING_STAR": return new Signal(pattern,"SELL",mode,mode==Mode.PRO?"75%":"70%",price,resistance+5,support-2,"v39 Evening Star STRONG SELL");
            case "THREE_WHITE_SOLDIERS": return new Signal(pattern,"BUY",mode,"70%",price,support-3,resistance,"v39 3 White Soldiers");
            case "THREE_BLACK_CROWS": return new Signal(pattern,"SELL",mode,"70%",price,resistance+3,support,"v39 3 Black Crows");
            case "DOJI": if(rsi<30) return new Signal(pattern,"BUY",mode,"62%",price,support-4,resistance,"v39 Doji BUY"); if(rsi>70) return new Signal(pattern,"SELL",mode,"62%",price,resistance+4,support,"v39 Doji SELL"); break;
        }
        return null;
    }
}
