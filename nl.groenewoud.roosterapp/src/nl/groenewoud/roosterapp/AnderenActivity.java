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
	// Declareer de variabelen en array's die bij de tabel horen
	TableLayout roosterTabel;
	TableRow[] rij;
	TextView[] uurCel;
	TextView[] maandagCel;
	TextView[] dinsdagCel;
	TextView[] woensdagCel;
	TextView[] donderdagCel;
	TextView[] vrijdagCel;
	
	/* 
	 * De classe definieren om het rooster in op te slaan, dit is een verouderde manier
	 * tegenwoordig gebruik ik de GlobalVars maar omdat deze activity niks doet heb
	 * ik het nog niet aangepast
	 */
	public class roosterClass {
		String maandag;
		String dinsdag;
		String woensdag;
		String donderdag;
		String vrijdag;
	}
	
	// Reserveer 9 blokken van het type roosterClass in het geheugen
	roosterClass[] rooster = new roosterClass[9];
	
	//Dit stuk code wordt uitgevoerd als de activity geladen wordt
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
        // Zolang er nog een rij niet gevuld is moet die gevuld worden
        for(int i=0; i<9; i++){
        	rij[i] = new TableRow(this);
        	Resources r = getResources();
        	
        	// Vul de array met een niew tekstveld
        	uurCel[i] = new TextView(this);
        	// Geef de uurcellen (helemaal links) als tekst hun uur nummer 
        	uurCel[i].setText(String.valueOf(i));
        	// Geef de cellen een hoogte van 50 pixels
           	uurCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, r.getDisplayMetrics()));
           	// De achtergrond is een vierkant blokje met oranje rand
        	uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
        	//De allerlaatste cel (links onder) moet een afwijkende achtergrond hebben, de linker beneden hoek moet namelijk afgerond zijn
        	if(i==8){
        		uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_links_onder));
        	}
        	//Voeg de cel aan de rij toe
        	rij[i].addView(uurCel[i]);
        	
        	//idem voor de maandagCellen t/m vrijdagcellen
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
        	// Ook hier moet de laatste cel (rechts beneden) een apparte achtergrond hebben om de rechts onder hoek rond te maken
        	if(i==8){
        		vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_rechts_onder));
        	}
        	rij[i].addView(vrijdagCel[i]);
        	
        	// Voeg de rij toe aan de tabel
        	roosterTabel.addView(rij[i]);
        }
        
        // Vul de array rooster met het object roosterClass
        for(int i=0; i<9; i++){
        	rooster[i] = new roosterClass();
        }
	}
}