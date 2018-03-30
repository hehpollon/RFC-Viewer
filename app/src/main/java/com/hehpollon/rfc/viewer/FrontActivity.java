package com.hehpollon.rfc.viewer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Collections;
import java.util.List;

/**
 * Created by han on 2018-03-29.
 */

public class FrontActivity extends AppCompatActivity {

    Button mButtonGo;
    EditText mEditTextRfcNumber;
    Spinner mSpinnerBookmark;
    Bookmark mBookmark;
    List<String> list;
    ArrayAdapter<String> adapter;
    Boolean isSpinnerSelected = true;
    public static final String RFC_NUM = "RFC_NUM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);


        mSpinnerBookmark = (Spinner) findViewById(R.id.sp_bookmark);

        mButtonGo = (Button)findViewById(R.id.btn_go);
        mEditTextRfcNumber = (EditText) findViewById(R.id.et_rfcnum);

        mBookmark = new Bookmark(this);

        mButtonGo.setOnClickListener(mListenerButtonGo);

        mSpinnerBookmark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isSpinnerSelected || position == 0) {
                    isSpinnerSelected = false;
                    return;
                }
                String bookmark = parent.getItemAtPosition(position).toString();
                int last = bookmark.split("#").length - 1;
                String rfcNum = bookmark.split("#")[last];
                goRfcActivity(rfcNum);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void goRfcActivity(String rfc){
        Intent intent = new Intent(FrontActivity.this, MainActivity.class);
        intent.putExtra(RFC_NUM, rfc);
        startActivity(intent);
    }

    View.OnClickListener mListenerButtonGo = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            goRfcActivity(mEditTextRfcNumber.getText().toString());
        }
    };

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        isSpinnerSelected = true;

        list = mBookmark.getAllBookmark();
        list.add("My Bookmark");
        Collections.reverse(list);

        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerBookmark.setAdapter(adapter);
    }
}
