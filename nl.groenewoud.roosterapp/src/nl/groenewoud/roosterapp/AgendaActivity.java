package nl.groenewoud.roosterapp;

/*
 * In deze activity worden de agenda en to-do lists weergegeven
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
 
public class AgendaActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agenda);
        
        /*De layouts ophalen*/
        TextView tv = (TextView)findViewById(R.id.textView1);
        TableLayout tl = (TableLayout)findViewById(R.id.TableLayout);
        TableRow tr = (TableRow)findViewById(R.id.tableRow1);
        
        /*Layoutparameters aanmaken*/
        LinearLayout.LayoutParams linearlayoutparams = (LinearLayout.LayoutParams)tv.getLayoutParams();
        linearlayoutparams.setMargins(20, 20, 50, 50);
        //tv.setLayoutParams(params);
        
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(55,10,0,0);
        
        Button agenda = new Button(this);
        agenda.setText("Test knop die geprogrameerd is");
        tl.addView(agenda, linearlayoutparams);
        
        TextView test = new TextView(this);
        test.setText("Test textview die geprogrameerd is");
        tr.addView(test, params);
        tr.setLayoutParams(params);
	}
}