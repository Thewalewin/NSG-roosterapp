package nl.groenewoud.roosterapp;

/*
 * In deze activity wordt het rooster gedownload en weergegeven
 */

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
 
public class RoosterActivity extends Activity {
	
	// Declareer een paar variabelen om de tabel te maken
	private TableLayout roosterTabel;
	private TableRow[] rij;
	private TextView[] uurCel;
	private TextView[] maandagCel;
	private TextView[] dinsdagCel;
	private TextView[] woensdagCel;
	private TextView[] donderdagCel;
	private TextView[] vrijdagCel;
	
		
	// Variabelen aanmaken
	
	public class lesuur {
		int n;
		String lesinformatie;
		
		public lesuur(int dag, String les) {
			// TODO Auto-generated constructor stub
			n = dag;
			lesinformatie = les;
		}
	}

	private ArrayList<lesuur> rooster = new ArrayList<lesuur>(0);
	
	private Button updateKnop;
	private ImageButton vorigeweekKnop;
	private ImageButton volgendeweekKnop;
	private TextView gebruikerVeld;
	private TextView updatetijdVeld;
	private TextView weekVeld;
	
	private String tabelCode = "";
	private String errorRegel = "";
	private String gebruikersnaam = "";
	private String updatetijd = "";
	
	private String leerlingnummer = "";
	private String wachtwoord = "";
	private String weeknummer = "";
	
	private ProgressDialog progressDialog;
	private ResponseReceiver receiver;

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rooster);
        
        roosterTabel = (TableLayout) findViewById(R.id.roosterTabel);
        updateKnop = (Button) findViewById(R.id.updateKnop);
        vorigeweekKnop = (ImageButton) findViewById(R.id.vorigeweekKnop);
        volgendeweekKnop = (ImageButton) findViewById(R.id.volgendeweekKnop);
        gebruikerVeld = (TextView) findViewById(R.id.gebruikerVeld);
        updatetijdVeld = (TextView) findViewById(R.id.updatetijdVeld);
        weekVeld = (TextView) findViewById(R.id.weeknummerVeld);
        
        // Maak rijen een cellen voor de tabel 
        rij = new TableRow[9];
        
        uurCel = new TextView[9];
        maandagCel = new TextView[9];
        dinsdagCel = new TextView[9];
        woensdagCel = new TextView[9];
        donderdagCel = new TextView[9];
        vrijdagCel = new TextView[9];
        
        tabelLayout(true);
        
        if (rooster.isEmpty()){
        	roosterUpdate();
        }
        
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    super.onCreateOptionsMenu(menu);
	    menu.add(0, R.id.updateMenuKnop, 0, "Update");
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.updateMenuKnop:     roosterUpdate();
	                            break;
	        case R.id.optiesMenuknop:     startActivity(new Intent(this, VoorkeurActivity.class));
	                            break;
	    }
	    return true;
	}
	
	
	private void roosterUpdate() {
		if(isOnline()){
			// Haal de pagina op
			progressDialog = new ProgressDialog(RoosterActivity.this);
			progressDialog.setMessage("Bezig met het ophalen van gegevens…");
			progressDialog.show();
			
	        SharedPreferences voorkeuren = getSharedPreferences("Voorkeuren", Context.MODE_PRIVATE); 
	        boolean gewijzigd = voorkeuren.getBoolean("gewijzigd", false);
	        
	        if(gewijzigd){
			    leerlingnummer = voorkeuren.getString("leerlingnummer", "");
			    wachtwoord = voorkeuren.getString("wachtwoord", "");
			    SharedPreferences.Editor editor = voorkeuren.edit();
			    editor.putBoolean("gewijzigd", false);
			    editor.commit();
	        }

			Intent downloadIntent = new Intent(this, DownloadService.class);
			downloadIntent.putExtra("user", leerlingnummer);
			downloadIntent.putExtra("paswoord", wachtwoord);
			downloadIntent.putExtra("gewijzigd", gewijzigd);
			
			
			startService(downloadIntent);
			
			IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
			filter.addCategory(Intent.CATEGORY_DEFAULT);
			receiver = new ResponseReceiver();
			registerReceiver(receiver, filter);
		}
		
		else{
			// Laat een error zien: geen verbinding
			Toast.makeText(RoosterActivity.this, "Je telefoon is niet verbonden met het internet.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void roosterUpdateKnop(View view){
		if (view == updateKnop){
			roosterUpdate();
		}
	}

	public void vorigeweekKnop(View view){
		if (view == vorigeweekKnop){
			/*
	        Calendar calendar = Calendar.getInstance();
	        calendar.setFirstDayOfWeek(Calendar.MONDAY);
	        
	        if((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && calendar.get(Calendar.HOUR_OF_DAY) >= 17) || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
	        	weeknummer = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
	        }
	        
	        else{
	        	weeknummer = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) -1);
	        }
	        roosterUpdate();
	        */
		}
	}
	
	public void volgendeweekKnop(View view){
		if (view == volgendeweekKnop){
			/*
	        Calendar calendar = Calendar.getInstance();
	        calendar.setFirstDayOfWeek(Calendar.MONDAY);
	        
	        if((calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY && calendar.get(Calendar.HOUR_OF_DAY) >= 17) || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
	        	weeknummer = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) + 2);
	        }
	        
	        else{
	        	weeknummer = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR) +1);
	        }
	        roosterUpdate();
	        */
		}
	}
	
	private boolean isOnline() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}
	
	private void tabelLayout( boolean normaleweek){
		if(normaleweek){
			// Geef de cellen eigenschappen en voeg ze toe aan de tabel
	        for(int i=0; i<9; i++){
	           
	        	Resources r = getResources();
	        	TableRow.LayoutParams textviewParameters = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
	        	//TableLayout.LayoutParams rijParameters = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
	        	
	        	int margin1dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -1, r.getDisplayMetrics());
	        	int margin2dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -2, r.getDisplayMetrics());
	            
	        	rij[i] = new TableRow(this);
	        	//rij[i].setPadding(0, 0, 0, 0);
	        	
	        	uurCel[i] = new TextView(this);
	        	uurCel[i].setText(String.valueOf(i));
	           	uurCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	           	uurCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	           	uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	if(i==8){
	        		uurCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_links_onder));
	        	}
	        	textviewParameters.setMargins(margin2dp, margin2dp, margin1dp, 0);
	        	rij[i].addView(uurCel[i], textviewParameters);
	        	        	
	        	maandagCel[i] = new TextView(this);
	        	maandagCel[i].setText("");
	        	maandagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	maandagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	maandagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	textviewParameters.setMargins(0, margin2dp, margin1dp, 0);
	        	rij[i].addView(maandagCel[i], textviewParameters);
	        	
	        	dinsdagCel[i] = new TextView(this);
	
	        	dinsdagCel[i].setText("");
	        	dinsdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	dinsdagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	dinsdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	rij[i].addView(dinsdagCel[i], textviewParameters);
	        	
	        	woensdagCel[i] = new TextView(this);
	        	woensdagCel[i].setText("");
	        	woensdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	woensdagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	woensdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	rij[i].addView(woensdagCel[i], textviewParameters);
	        	        	
	        	donderdagCel[i] = new TextView(this);
	        	donderdagCel[i].setText("");
	        	donderdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	donderdagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	donderdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	rij[i].addView(donderdagCel[i], textviewParameters);
	        	
	        	vrijdagCel[i] = new TextView(this);
	        	vrijdagCel[i].setText("");
	        	vrijdagCel[i].setHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, r.getDisplayMetrics()));
	        	vrijdagCel[i].setGravity(Gravity.CENTER_HORIZONTAL);
	        	vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelcel));
	        	if(i==8){
	        		vrijdagCel[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.tabelhoek_rechts_onder));
	        	}
	        	textviewParameters.setMargins(0, margin2dp, margin2dp, 0);
	        	rij[i].addView(vrijdagCel[i], textviewParameters);
	        	
	        	roosterTabel.addView(rij[i]);
	        }
		}
		else {
			//Tabellayout voor uurgebonden roostern
		}
	}
	
	
	public class ResponseReceiver extends BroadcastReceiver {
		public static final String ACTION_RESP ="com.mamlambo.intent.action.MESSAGE_PROCESSED";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			tabelCode = intent.getStringExtra("tabelCode");
			errorRegel = intent.getStringExtra("errorRegel");
			gebruikersnaam = intent.getStringExtra("gebruikersnaam");
			updatetijd = intent.getStringExtra("updatetijd");
			weeknummer = intent.getStringExtra("weeknummer");

			
			if(tabelCode.length() > 0){
				//Hier moet de uitvoer naar de tabel plaats vinden
				String pwrooster = "<div class=\"reeelraster\"";
				if(tabelCode.indexOf(pwrooster) == -1){
					//Het is een normale week
					sorteren(true);
				}
				
				else {
					//proefwerkweek
					sorteren(false);
				}
				gebruikerVeld.setText("Dit is het rooster van " + gebruikersnaam);
				updatetijdVeld.setText(updatetijd);
				weekVeld.setText("Week : " + weeknummer);
			}
			else {
				Toast.makeText(RoosterActivity.this, errorRegel, Toast.LENGTH_SHORT).show();
			}
			progressDialog.dismiss();
		}
	}
	
	private void sorteren (boolean normaleweek){
		
		if(normaleweek){
			
			//Het gaat om een normale schoolweek
			int lesPositie;
			int vrijPositie;
			int uitvalPositie;
			
			String lesScheiding = "<span class=\"nobr\">";
			String vrijScheiding = "<td class=\"vrij\">";
			String uitvalScheiding = "<td class=\"vervallen\">";
			
			for (int uur=0; uur<9; uur++){
				for (int dag=0; dag<5; dag++){
					lesPositie = tabelCode.indexOf(lesScheiding);
					vrijPositie = tabelCode.indexOf(vrijScheiding);
					uitvalPositie = tabelCode.indexOf(uitvalScheiding);
					
					if((lesPositie < vrijPositie || vrijPositie == -1) && (lesPositie < uitvalPositie || uitvalPositie == -1) && lesPositie != -1){
						
						// Het eerst volgende item is een les
						String les;
						les = tabelCode.substring(lesPositie + 19, tabelCode.indexOf("</span>"));
						les = les.replaceAll("<br />", "\n");
						
						rooster.add(new lesuur(dag, les));
						tabelCode = tabelCode.substring(tabelCode.indexOf("</table> </td>") + 14);
						
					}
					
					else if ((vrijPositie < lesPositie || lesPositie == -1) && (vrijPositie < uitvalPositie || uitvalPositie == -1) && vrijPositie != -1){
						
						// Het eerst voorkomende item is een tussenuur
						rooster.add(new lesuur(dag, "vrij")); 
						tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
					
					}
					
					else {
						
						// Het volgende uur valt uit
						rooster.add(new lesuur(dag, "uitval"));
						tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
						
					}
					
				}
				
			}
			
			for (int i=0; i<9; i++){
				maandagCel[i].setText(rooster.get(0 + i*5).lesinformatie);
				dinsdagCel[i].setText(rooster.get(1 + i*5).lesinformatie);
				woensdagCel[i].setText(rooster.get(2 + i*5).lesinformatie);
				donderdagCel[i].setText(rooster.get(3 + i*5).lesinformatie);
				vrijdagCel[i].setText(rooster.get(4 + i*5).lesinformatie);
			}
			
		}
		
		else{
			//We hebben te maken met een proefwerkweek of een uurgebonden rooster i.p.v een lesuur gebonden rooster
			tabelCode = tabelCode.substring(tabelCode.indexOf("</td>") + 5);
			
			String toetsScheiding = "<div class=\"toets\">";
			String lesScheiding = "<div class=\"les\">";
			String dagScheiding = "</td>";
			
			int toetsPositie = tabelCode.indexOf(toetsScheiding);
			int lesPositie = tabelCode.indexOf(lesScheiding);
			int dagPositie = tabelCode.indexOf(dagScheiding);
			
			for (int i=0; i<5; i++){
				while ( (toetsPositie < dagPositie && toetsPositie != -1) || (lesPositie < dagPositie && lesPositie != -1)){
					//Er staat nog een toets of les in deze dag
					if(toetsPositie < dagPositie && toetsPositie != -1){
						//Er is een toets
						String toetseinde = "</div>";
						int toetseindePositie = tabelCode.indexOf(toetseinde, toetsPositie);
						
						String toets = tabelCode.substring(toetsPositie + 19, toetseindePositie);
						toets = toets.replaceAll("<br />", "\n");
						
						rooster.add(new lesuur(i, toets));
						tabelCode = tabelCode.substring(toetseindePositie + 6);
					}
					
					else{
						//Er is een les
						String leseinde = "</div>";
						int leseindePositie = tabelCode.indexOf(leseinde, lesPositie);
						
						String les = tabelCode.substring(lesPositie + 19, leseindePositie);
						les = les.replaceAll("<br />", "\n");
						
						rooster.add(new lesuur(i, les));
						tabelCode = tabelCode.substring(leseindePositie + 6);
					}
					
					toetsPositie = tabelCode.indexOf(toetsScheiding);
					lesPositie = tabelCode.indexOf(lesScheiding);
					dagPositie = tabelCode.indexOf(dagScheiding);
					
				}
			}
			
		}
		
	}
	
}