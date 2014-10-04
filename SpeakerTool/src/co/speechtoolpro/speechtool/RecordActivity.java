package co.speechtoolpro.speechtool;

import android.widget.ImageView;
import android.os.SystemClock;
import android.widget.Chronometer;
import java.util.ArrayList;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  Allows the user to record their speech and shows them how long they've been
 *  talking. After they stop recording it takes them to the results screen.
 *
 *  @author Me
 *  @version Oct 4, 2014
 */
public class RecordActivity
    extends Activity
{
    private static final int  REQUEST_CODE = 1234;
    private Chronometer       timer;
    private Button            Start;
    private TextView          Speech;
    private Dialog            match_text_dialog;
    private ListView          textlist;
    private ArrayList<String> matches_text;
    private boolean           recording;
    private ImageView         recordingDot;


    public void toggleRecording(View view)
    {
        Button toggleButton = (Button) view;
        if (recording)
        {
            timer.stop();
            toggleButton.setText(R.string.restart_recording);
            recordingDot.setImageResource(R.drawable.recording_dot_empty);
        }
        else
        {
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
            toggleButton.setText(R.string.stop_recording);
            recordingDot.setImageResource(R.drawable.blinking_recording_dot);
        }
        System.out.println("Please print me");
        recording = !recording;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        recording = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        Start = (Button)findViewById(R.id.start_reg);
        Speech = (TextView)findViewById(R.id.speech);
        timer = (Chronometer)findViewById(R.id.timer);
        recordingDot = (ImageView)findViewById(R.id.imageView1);
        Start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if (isConnected())
                {
                    Intent intent =
                        new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                else
                {
                    Toast.makeText(
                        getApplicationContext(),
                        "Plese Connect to Internet",
                        Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public boolean isConnected()
    {
        ConnectivityManager cm =
            (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = cm.getActiveNetworkInfo();
        if (net != null && net.isAvailable() && net.isConnected())
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            match_text_dialog = new Dialog(RecordActivity.this);
            match_text_dialog.setContentView(R.layout.fragment_record);
            match_text_dialog.setTitle("Select Matching Text");
            textlist = (ListView)match_text_dialog.findViewById(R.id.list);
            matches_text =
                data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    matches_text);
            textlist.setAdapter(adapter);
            textlist
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                        AdapterView<?> parent,
                        View view,
                        int position,
                        long id)
                    {
                        Speech.setText("You have said "
                            + matches_text.get(position));
                        match_text_dialog.hide();
                    }
                });
            match_text_dialog.show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
