package com.tchip.autoplayer;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView.OnEditorActionListener;

import com.tchip.autoplayer.model.Video;

public class VideoChooseActivity extends Activity {

    private static int height, width;
    private LinkedList<Video> videoList;
    private LayoutInflater mInflater;
    View root;
    private EditText urlInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.dialog);

        videoList = MainActivity.videoLinkedList;

        mInflater = getLayoutInflater();
        ImageButton iButton = (ImageButton) findViewById(R.id.cancel);
        iButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                VideoChooseActivity.this.finish();
            }

        });

        ListView myListView = (ListView) findViewById(R.id.list);
        myListView.setAdapter(new BaseAdapter() {

            @Override
            public int getCount() {
                return videoList.size();
            }

            @Override
            public Object getItem(int arg0) {
                return arg0;
            }

            @Override
            public long getItemId(int arg0) {
                return arg0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup arg2) {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.list, null);
                }
                TextView text = (TextView) convertView.findViewById(R.id.text);
                text.setText(videoList.get(position).getName());

                return convertView;
            }

        });

        myListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent = new Intent();
                intent.putExtra("CHOOSE", position);
                VideoChooseActivity.this.setResult(Activity.RESULT_OK, intent);
                VideoChooseActivity.this.finish();
            }
        });

        urlInput = (EditText) findViewById(R.id.url_input);
        urlInput.setText("http://");
        urlInput.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                Log.d("actionId", "" + actionId);
                Editable url = ((EditText) v).getEditableText();
                Intent intent = new Intent();
                intent.putExtra("CHOOSE_URL", url.toString());
                VideoChooseActivity.this.setResult(Activity.RESULT_OK, intent);
                VideoChooseActivity.this.finish();

                return false;
            }

        });

        myListView.requestFocus();
    }
}
