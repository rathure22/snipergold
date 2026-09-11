package com.rathure22;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.JavascriptInterface;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    
    CandleExpertProDetector detector = new CandleExpertProDetector();
    TradeManager tradeManager;

    @Override 
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebView w=findViewById(R.id.webView);
        WebSettings s=w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        
        // v39 BRIDGE - Connect Java Detector to index.html
        w.addJavascriptInterface(new DetectorBridge(), "AndroidDetector");
        
        w.setWebViewClient(new WebViewClient());
        w.loadUrl("file:///android_asset/index.html");

        tradeManager = new TradeManager(new TradeManager.Listener() {
            @Override public void onLockTriggered(String reason){}
            @Override public void onLotChanged(double newLot, int m){}
            @Override public void onAlarm(String title, String msg){}
        });
    }

    // Ito ang tatawagin ng index.html mo
    public class DetectorBridge {
        @JavascriptInterface
        public String getSignal(double price, double ema50, double ema200, double rsi, String pattern, String modeStr) {
            CandleExpertProDetector.Mode mode = modeStr.equals("PRO") ? CandleExpertProDetector.Mode.PRO : CandleExpertProDetector.Mode.EXPERT;
            CandleExpertProDetector.Signal sig = detector.detectLatest18(price, ema50, ema200, rsi, pattern, mode);
            if(sig == null) return "WAIT - Di pa READY sa v39 Expert/Pro";
            
            // Auto set Tier
            if(sig.mode == CandleExpertProDetector.Mode.PRO) tradeManager.setTier(TradeManager.Tier.PRO);
            else tradeManager.setTier(TradeManager.Tier.EXPERT);
            
            return sig.type + "|" + sig.accuracy + "|" + sig.pattern + "|SL:" + sig.sl + " TP:" + sig.tp + "|" + sig.desc;
        }
    }
}
