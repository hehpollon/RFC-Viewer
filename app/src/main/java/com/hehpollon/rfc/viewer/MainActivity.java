package com.hehpollon.rfc.viewer;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayDeque;
import java.util.Deque;

public class MainActivity extends AppCompatActivity {

    WebView mWebView;
    Button mButtonMode;
    String mUrl;
    Boolean isNight = false;
    Handler mHandler;
    TextView mTextViewTitle;
    ImageButton mImageButtonBookmark;
    ImageButton mImageButtonHome;
    ScrollView mScrollViewWebView;

    Bookmark mBookmark;

    Deque<String> urlStack;
    Boolean isBookmark = false;
    String htmlData;
    Document mDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebView = (WebView) findViewById(R.id.wb_main);
        mButtonMode = (Button) findViewById(R.id.btn_mode);
        mTextViewTitle = (TextView) findViewById(R.id.tv_rfcnum);
        mImageButtonHome = (ImageButton) findViewById(R.id.btn_home);
        mImageButtonBookmark = (ImageButton) findViewById(R.id.btn_bookmark);
        mScrollViewWebView = (ScrollView) findViewById(R.id.sv_webview);

        mBookmark = new Bookmark(this);

        urlStack = new ArrayDeque<String>();

        Intent intent = getIntent();
        String rfcNum = intent.getStringExtra(FrontActivity.RFC_NUM);



        setTitle(rfcNum);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(false);

        mHandler = new Handler();


        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url){

                int last = url.split("/").length - 1;
                String rfcPath = url.split("/")[last];

                String newUrl = "https://tools.ietf.org/html/" + rfcPath;
                mUrl = newUrl;

                loadURL(mUrl, isNight);

                setLoadingView(view);

                return false;
            }
        });

        mWebView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        mButtonMode.setOnClickListener(mListenerButtonMode);
        mImageButtonHome.setOnClickListener(mListenerImageButtonHome);
        mImageButtonBookmark.setOnClickListener(mListenerImageButtonBookmark);


        mUrl = "https://tools.ietf.org/html/rfc" + rfcNum;
        setLoadingView(mWebView);
        loadURL(mUrl, false);

        reloadBookmark();

    }

    View.OnClickListener mListenerButtonMode = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isNight) {
                setNightMode(false);
                isNight = false;
            }else{
                setNightMode(true);
                isNight = true;
            }
        }
    };

    View.OnClickListener mListenerImageButtonBookmark = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mBookmark == null) {
                return;
            }
            try {
                String url = urlStack.getLast();
                int last = url.split("/").length - 1;
                String rfcPath = url.split("/")[last];
                String rfcNum = rfcPath.substring(3);
                String rfcBookmark = "RFC #"+rfcNum;
                if (!isBookmark) {
                    mBookmark.insertBookmark(rfcBookmark);
                    mImageButtonBookmark.setImageResource(R.drawable.btn_bookmark_reverse_src);
                    isBookmark = true;
                } else {
                    mBookmark.removeBookmark(rfcBookmark);
                    mImageButtonBookmark.setImageResource(R.drawable.btn_bookmark_src);
                    isBookmark = false;
                }
            }catch(Exception e) {

            }
        }
    };

    View.OnClickListener mListenerImageButtonHome = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            MainActivity.this.finish();
        }
    };

    private void reloadBookmark(){
        String url = mUrl;
        int last = url.split("/").length - 1;
        String rfcPath = url.split("/")[last];
        String rfcNum = rfcPath.substring(3);
        String rfcBookmark = "RFC #"+rfcNum;
        isBookmark = mBookmark.isBookmark(rfcBookmark);
        if(isBookmark) {
            mImageButtonBookmark.setImageResource(R.drawable.btn_bookmark_reverse_src);
        }else{
            mImageButtonBookmark.setImageResource(R.drawable.btn_bookmark_src);
        }
    }

    private void setLoadingView(WebView wb){
        wb.loadUrl("file:///android_asset/progress.html");
    }

    private void setTitle(String rfcNum){
        mTextViewTitle.setText("RFC #"+rfcNum);
    }

    private void setNightMode(boolean isNight){
        String style;

        if(isNight) {
            style = "style_night.css";
        } else {
            style = "style.css";
        }

        if(mDocument == null) {
            return;
        }

        mDocument.head().select("link").remove();
        mDocument.head().appendElement("link")
                .attr("rel", "stylesheet")
                .attr("type", "text/css")
                .attr("href", style);

        htmlData = mDocument.outerHtml();
        mWebView.loadDataWithBaseURL("file:///android_asset/.",
                htmlData, "text/html", "UTF-8", null);
    }

    private void loadURL(final String url, final boolean isNight){

        int last = url.split("/").length - 1;
        String rfcPath = url.split("/")[last];
        String rfcNum = rfcPath.substring(3);
        setTitle(rfcNum);
        reloadBookmark();

        new Thread(new Runnable() {
            @Override public void run() {

                String style;

                if(isNight) {
                    style = "style_night.css";
                } else {
                    style = "style.css";
                }

                try {
                    mDocument = Jsoup.connect(url).get();
                    mDocument.head().appendElement("link")
                            .attr("rel", "stylesheet")
                            .attr("type", "text/css")
                            .attr("href", style);
                    htmlData = mDocument.outerHtml();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.loadDataWithBaseURL("file:///android_asset/.",
                                    htmlData, "text/html", "UTF-8", null);

                            urlStack.push(url);
                            mScrollViewWebView.fullScroll(ScrollView.FOCUS_UP);
                        }
                    });

                }catch(Exception e) {
                    Log.e("ERROR",e.toString());
                    mHandler.post(new Runnable() {
                        @Override public void run() {
                            Toast.makeText(MainActivity.this,"Invalid RFC Number",Toast.LENGTH_SHORT).show();
                        }
                    });

                    MainActivity.this.finish();
                }
            }
        }).start();
    }



    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            try {
                setLoadingView(mWebView);
                urlStack.pop();
                if (urlStack.isEmpty()) {
                    MainActivity.this.finish();
                } else {
                    mUrl = urlStack.pop();
                    loadURL(mUrl, isNight);
                }
            }catch(Exception e){
                Log.e("ERROR", e.toString());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
