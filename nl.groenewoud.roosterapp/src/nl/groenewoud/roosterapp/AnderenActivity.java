package nl.groenewoud.roosterapp;

/*
 * In deze activity wordt het weergeven van roosters van anderen afgehandeld
 */

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
 
public class AnderenActivity extends Activity {
	// Declareer een paar variabelen om de tabel te maken
	TableLayout roosterTabel;
	TableRow[] rij;
	TextView[] uurCel;
	TextView[] maandagCel;
	TextView[] dinsdagCel;
	TextView[] woensdagCel;
	TextView[] donderdagCel;
	TextView[] vrijdagCel;
	
	// De classe definieren om het rooster in op te slaan
	public class roosterClass {
		String maandag;
		String dinsdag;
		String woensdag;
		String donderdag;
		String vrijdag;
	}
	
	// Variabelen aanmaken
	roosterClass[] rooster = new roosterClass[9];
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.anderen);
        
        roosterTabel = (TableLayout) findViewById(R.id.roostertabel);

        // Maak rijen een cellen voor de tabel 
        rij = new TableRow[9];
        
        uurCel = new TextView[9];
        maandagCel = new TextView[9];
        dinsdagCel = new TextView[9];
        woensdagCel = new TextView[9];
        donderdagCel = new TextView[9];
        vrijdagCel = new TextView[9];
        
        // Geef de cellen eigenschappen en voeg ze toe aan de tabel
        for(int i=0; i<9; i++){
        	rij[i] = new TableRow(this);
        	Resources r = getResources();
        	
        	uurCel[i] = new TextView(this);
        	uurCel[i].setText(String.valueOf(i+1));
           	uurCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	if(i==8){
        		uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_links_onder));
        	}
        	rij[i].addView(uurCel[i]);
        	
        	maandagCel[i] = new TextView(this);
        	maandagCel[i].setText("");
        	maandagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	maandagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	rij[i].addView(maandagCel[i]);
        	
        	dinsdagCel[i] = new TextView(this);
        	dinsdagCel[i].setText("");
        	dinsdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	dinsdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	rij[i].addView(dinsdagCel[i]);
        	
        	woensdagCel[i] = new TextView(this);
        	woensdagCel[i].setText("");
        	woensdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	woensdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	rij[i].addView(woensdagCel[i]);
        	
        	donderdagCel[i] = new TextView(this);
        	donderdagCel[i].setText("");
        	donderdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	donderdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	rij[i].addView(donderdagCel[i]);
        	
        	vrijdagCel[i] = new TextView(this);
        	vrijdagCel[i].setText("");
        	vrijdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
        	vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	if(i==8){
        		vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_rechts_onder));
        	}
        	rij[i].addView(vrijdagCel[i]);
        	
        	roosterTabel.addView(rij[i]);
        }
        
        //Array voor Rooster maken
        for(int i=0; i<9; i++){
        	rooster[i] = new roosterClass();
        }
	}
}