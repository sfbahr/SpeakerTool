package co.speechtoolpro.speechtool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.os.SystemClock;
import android.widget.Chronometer;
import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

/**
 *  Allows the user to record their speech and shows them how long they've been
 *  talking. After they stop recording it takes them to the results screen.
 *
 *  @author Me
 *  @version Oct 4, 2014
 */
public class RecordActivity extends Activity implements RecognitionListener
{
    public final static String EXTRA_TRANSCRIPT =
        "com.example.myfirstapp.TRANSCRIPT";
    private static final int  REQUEST_CODE = 1234;
    private Chronometer       timer;
    private static final String KWS_SEARCH = "wakeup";

    private Dialog            match_text_dialog;
    private ListView          textlist;
    private ArrayList<String> matches_text;
    private boolean           recording;
    private ImageView         recordingDot;

    private SpeechRecognizer recognizer;

    private static String recognizerKey = "recogKey";
    private String fillerCountList = "";
    private String PreviousFillerList = "";
    private long previousTimeMs;
    private String fillerWord = "umm";

    public void toggleRecording(View view)
    {
        Button toggleButton = (Button) view;
        if (recording)
        {
            timer.stop();
            toggleButton.setText(R.string.restart_recording);
            recordingDot.setImageResource(R.drawable.recording_dot_empty);
            Intent scoreIntent = new Intent(this, ScoreActivity.class);
            EditText input = (EditText) findViewById(R.id.testEditText);
            //String transcript = input.getText().toString();
            scoreIntent.putExtra(EXTRA_TRANSCRIPT, fillerCountList);//transcript);
            //System.out.println("transcript to score: " + transcript);
            startActivity(scoreIntent);

            recognizer.stop();

        }
        else
        {
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
            toggleButton.setText(R.string.stop_recording);
            recordingDot.setImageResource(R.drawable.blinking_recording_dot);

            AnalyzeTranscript.resetFillerCount();
            fillerCountList = "";
            startSpeechRecognition();

        }
        System.out.println("Please print me");
        recording = !recording;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        recording = false;
        super.onCreate(savedInstanceState);
        System.out.println("The program is starting");

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        setContentView(R.layout.activity_record);
        timer = (Chronometer)findViewById(R.id.timer);
        recordingDot = (ImageView)findViewById(R.id.recordingIndicator);
        getActionBar().setDisplayHomeAsUpEnabled(false);

    }

	@Override
	public void onBeginningOfSpeech() {

	}


	@Override
	public void onEndOfSpeech() {
    	System.out.println("End of speech println");
        recognizer.stop();
        recognizer.startListening("default");
	}


	@Override
	public void onPartialResult(Hypothesis hypothesis) {
    	if (hypothesis != null)
    		if(SystemClock.elapsedRealtime() - previousTimeMs>1000) {
    			if(!hypothesis.getHypstr().equals(PreviousFillerList)) {
    				previousTimeMs = SystemClock.elapsedRealtime();
    				PreviousFillerList = hypothesis.getHypstr();
    				fillerCountList = fillerCountList + fillerWord+" ";
    				System.out.println("on Partial Result  println "+fillerCountList);
    			}
    		}
	}


    @Override
    public void onResult(Hypothesis hypothesis) {

    }

    private void startSpeechRecognition() {
        try {
        	previousTimeMs=SystemClock.elapsedRealtime();
            Assets assets = new Assets(RecordActivity.this);
            File assetDir = assets.syncAssets();
            File modelsDir = new File(assetDir, "models");
            recognizer = defaultSetup()
                    .setAcousticModel(new File(modelsDir, "hmm/en-us-semi"))
                    .setDictionary(new File(modelsDir, "dict/cmu07a.dic"))
                    .setRawLogDir(assetDir).setKeywordThreshold(1e-2f)
                    .getRecognizer();
            recognizer.addListener(this);
            recognizer.stop();
            recognizer.addKeyphraseSearch("default", fillerWord);
            recognizer.startListening("default");
        } catch (IOException e) {
        	System.out.println("There was an error onCreate println");
            return;
        }
    }

}
