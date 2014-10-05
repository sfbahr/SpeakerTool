package co.speechtoolpro.speechtool;

import android.text.Html;
import org.apache.commons.lang3.StringUtils;
import android.annotation.TargetApi;
import android.app.TaskStackBuilder;
import android.support.v4.app.NavUtils;
import android.content.Intent;
import android.widget.TextView;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ScoreActivity
    extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        String transcript =
            intent.getStringExtra(RecordActivity.EXTRA_TRANSCRIPT);

        TextView textBack = (TextView)findViewById(R.id.lookAtMyText);
        //textBack.setText(AnalyzeTranscript.boldFiller(transcript));
        textBack.setText("Number of umms:");
        TextView scoreView = (TextView) findViewById(R.id.scoreView);
        scoreView.setText(((Integer)AnalyzeTranscript.getFillerCount(transcript)).toString());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
        // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.score, menu);
        return true;
    }
}
