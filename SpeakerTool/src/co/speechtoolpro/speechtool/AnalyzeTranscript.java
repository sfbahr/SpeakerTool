package co.speechtoolpro.speechtool;
import java.util.Scanner;
import android.text.Html;
import org.apache.commons.lang3.StringUtils;
import android.text.Spanned;


public class AnalyzeTranscript
{
    private static int fillerCount = -1;

    public static Spanned boldFiller(String transcript)
    {
        StringBuilder highlightedTranscript = new StringBuilder();
        Scanner transcriptScanner = new Scanner(transcript);
        String filler = "umm";
        fillerCount = 0;
        while (transcriptScanner.hasNext())
        {
            String nextWord = transcriptScanner.next();
            if (nextWord.toLowerCase().startsWith(filler))
            {
                highlightedTranscript.append("<b>" + nextWord.substring(0,
                    filler.length()) + "</b>" +
                    nextWord.substring(filler.length()) + " ");
                fillerCount++;
            }
            else
            {
                highlightedTranscript.append(nextWord + " ");
            }
        }

        return Html.fromHtml(highlightedTranscript.toString());
    }

    /**
     * Must be ran after boldFiller
     */
    public static int getFillerCount()
    {
        return fillerCount;
    }
}
