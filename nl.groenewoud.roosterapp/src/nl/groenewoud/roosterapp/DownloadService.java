package nl.groenewoud.roosterapp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import nl.groenewoud.roosterapp.RoosterActivity.ResponseReceiver;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.loopj.android.http.PersistentCookieStore;

import android.app.IntentService;
import android.content.Intent;

/*
 * Deze service handeld het downloaden van de broncode en het omzetten naar een handelbaar formaat af. 
 * Dit alles gebeurt op de achtergrond, zodat de gebruiker er geen invloed op heeft.
 */

public class DownloadService extends IntentService {

	public DownloadService() {
		
		super("DownloadService");
		
	}
	
	/*
	 * Variabelen die terug gestuurd worden naar de voorgrond om daar gebruikt te worden
	 */
	
	private String tabelCode;
	private String errorRegel;
	private String gebruikersnaam;
	private String updatetijd;

	/*
	 * Deze void wordt opgeroepen als de service gestart wordt. 
	 * Dit gebeurt als de gebruiker het rooster wil updaten.
	 */
	
	@Override
	protected void onHandleIntent(Intent intent){
		/*
		 * Dit zijn de variabelen waarin de gegevens opgeslagen zijn die nodig zijn bij
		 * het downloaden van het rooster. Zoals gebruikersnaam, wachtwoord, weeknummer etc. 
		 */
		
		final String user = intent.getExtras().getString("user");
		final String paswoord = intent.getExtras().getString("paswoord");
		final String weeknummer = intent.getExtras().getString("weeknummer");
		
		/*
		 *  Omdat de service hergebruikt wordt bij een rooster update moeten de variabelen geleegd worden
		 *  om vervuiling te voorkomen
		 */
		
		tabelCode = "";
		errorRegel = "";
		gebruikersnaam = "";
		
		/*
		 * Hier wordt geprobeerd het rooster te downloaden. Als er tijdens het downloaden een
		 * fout met de verbinding plaats vindt (IOException, ClientProtocolException of een andere Exception),
		 * dan wordt die hier opgevangen en krijgt de gebruiker een berichtje dat er iets mis ging.
		 */
		
		try{
			downloadUrl("http://www.groenewoud.nl/infoweb/infoweb/index.php", user, paswoord, weeknummer);
			
			/*
			 * Nu het downloaden voltooid is willen we de tekst leesbaar maken.
			 */
			
			if (tabelCode.length() > 0){
				// Er is een rooster gevonden, we ontdoen de tabel van alle fratsen om zo de leesbaarheid te verhogen
				tabelCode = tabelCode.replaceAll("<a href=\'(.+?)\'>(.+?)</a>", "$2");
				tabelCode = tabelCode.replaceAll("TITLE=\"(.+?)\"", "");
				tabelCode = tabelCode.replaceAll("(<tr>)?<td class=\"pic( wijziging)?\">(.+?)</td>(</tr>)?", "");
				tabelCode = tabelCode.replaceAll("<td class=\"pic informatie\" >(.+?)</td>", "");
				tabelCode = tabelCode.replaceAll("<div class=\"toets\"(.+?)>", "<div class=\"toets\">");
				tabelCode = tabelCode.replaceAll("<div class=\"les\"(.+?)>", "<div class=\"les\">");
				
				SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
				updatetijd = sdf.format(new Date());
			}
			
		} catch (ClientProtocolException e){
			errorRegel = "Er gaat iets mis " + e.toString();
		} catch (IOException e) {
			errorRegel = "Er gaat iets mis: " + e.toString();
		} catch (Exception e){
			errorRegel = "Er gaat iets mis: " + e.toString();
		}
		
		/*
		 * Als het eenmaal wel of niet gelukt is dan wordt het resultaat naar de voorgrond
		 * terug gestuurd om daar aan de gebruiker getoond te worden.
		 */

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(ResponseReceiver.ACTION_RESP);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra("tabelCode", tabelCode);
		broadcastIntent.putExtra("errorRegel", errorRegel);
		broadcastIntent.putExtra("gebruikersnaam", gebruikersnaam);
		broadcastIntent.putExtra("updatetijd", updatetijd);
		sendBroadcast(broadcastIntent);
		
	}
	
	
	/*
	 * Hier worden de parameters voor de netwerkverbinding opgesteld, bijvoorbeeld de
	 * timeout tijd (10 seconden), de poort waarop er gedwonload moet worden (poort 80)
	 */
	
    private DefaultHttpClient createHttpClient() {
    	
    	HttpParams my_httpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(my_httpParams, 10000); //Timeout in milliseconden
    	SchemeRegistry registry = new SchemeRegistry();
    	registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
    	ThreadSafeClientConnManager multiThreadedConnectionManager = new ThreadSafeClientConnManager(my_httpParams, registry);
    	DefaultHttpClient httpclient = new DefaultHttpClient(multiThreadedConnectionManager, my_httpParams);
    	return httpclient;
    	
    }
	
    /*
     * In deze methode wordt de broncode gedownload en teruggestuurd naar de hoofd methode om omgezet
     * te worden naar handelbare code
     */
    
	private String downloadUrl(String infoweblink, String user, String paswoord, String weeknummer) throws IOException, ClientProtocolException, Exception {
		
		int statuscode;
		String broncode;
		
		/*
		 * Hier wordt de netwerkverbinding opgestart, eerst worden er variabelen gemaakt om
		 * de verbinding, het antwoord en de opslag van cookies in op te slaan. Daarna
		 * wordt de verbinding uitgevoerd, het antwoord en de http statuscode opgeslagen. 
		 */
		
		DefaultHttpClient httpclient;
		HttpResponse response;
		PersistentCookieStore cookieStore = new PersistentCookieStore(getBaseContext());
		httpclient = createHttpClient();
		httpclient.setCookieStore(cookieStore);
		HttpGet request = new HttpGet(infoweblink);
		response = httpclient.execute(request); // Hier wordt de verbinding uitgevoerd en het antwoord opgeslagen in de response variabele 
		statuscode = response.getStatusLine().getStatusCode(); // Hier wordt de http statuscode opgeslagen

		//Als de download succesvol was dan willen we inloggen of na gaan of er wel een rooster online staat
		
		if (statuscode == 200){
			
			/*
			 * De aanvraag is gelukt, nu moeten we kijken of er al ingelogd was op infoweb,
			 * of dat we nog in moeten loggen.
			 */
			broncode = EntityUtils.toString(response.getEntity());
			
			int weekPositie = broncode.indexOf("selected=\"selected\">");
			String gevondenWeek = broncode.substring(weekPositie + 20, weekPositie + 22);
			if (broncode.indexOf("<h1>Mijn rooster</h1>") >= 0 && Integer.parseInt(gevondenWeek) == Integer.parseInt(weeknummer)){
				
				//We hoeven niet meer in te loggen op infoweb
				int tabelPositie = broncode.indexOf("<table class=\"roosterdeel\"");
				
				if (tabelPositie >= 0){
					
					// Er staat een rooster op infoweb, haal het rooster uit de broncode en stuur deze terug voor analyse
					int tabelEinde = broncode.indexOf("<td id=\"linkerfooter\">");
					tabelCode = broncode.substring(tabelPositie, tabelEinde);
					
					int gebruikersnaamPositie = broncode.indexOf("<h2>Het rooster voor ");
					
					gebruikersnaam = broncode.substring(gebruikersnaamPositie + 21, broncode.indexOf("</h2>", gebruikersnaamPositie));
					return tabelCode;
				}
				
				else{
					
					//We zijn ingelogd maar er staat geen rooster voor deze week online
					errorRegel = "Er staat geen rooster op infoweb!";
					return errorRegel;
					
				}
				
			}
			
			else {
				
				//We zijn niet ingelogd op infoweb, laten we dit snel doen!
				HttpPost post = new HttpPost(infoweblink);
				int csrfPositie = broncode.indexOf("name=\"csrf\" value=\""); //csrf is een random code die ter beveiliging bij het inloggen wordt mee gegeven
				
				if (csrfPositie != -1){
					
					//De code was vindbaar en dus kunnen we inloggen op infoweb
					String csrfCode = broncode.substring(csrfPositie + 19, csrfPositie + 51);
					
					//De onderstaande lijst is de informatie die meegstuurd wordt om in te loggen op infoweb
					List<NameValuePair> postData = new ArrayList<NameValuePair>(1);
					  postData.add(new BasicNameValuePair("user", user));
					  postData.add(new BasicNameValuePair("paswoord", paswoord));
					  postData.add(new BasicNameValuePair("login", "loginform"));
					  postData.add(new BasicNameValuePair("csrf", csrfCode));
					  postData.add(new BasicNameValuePair("weeknummer", weeknummer));
					  
					//Hier wordt er ingelogd en wordt de broncode en statuscode ingeladen.
					post.setEntity(new UrlEncodedFormEntity(postData));
					response = httpclient.execute(post);
					statuscode = response.getStatusLine().getStatusCode();
					
					if (statuscode == 200){
						
						//De aanvraag is gelukt nu moet het rooster eruit knippen en terug sturen
						broncode = EntityUtils.toString(response.getEntity());
						int tabelPositie = broncode.indexOf("<table class=\"roosterdeel\">");
						
						if (tabelPositie >= 0){
							
							// Er staat een rooster op infoweb, haal het rooster uit de broncode en stuur deze terug voor analyse
							int tabelEinde = broncode.indexOf("<td id=\"linkerfooter\">");
							tabelCode = broncode.substring(tabelPositie, tabelEinde);
							
							int gebruikersnaamPositie = broncode.indexOf("<h2>Het rooster voor ");
							
							gebruikersnaam = broncode.substring(gebruikersnaamPositie + 21, broncode.indexOf("</h2>", gebruikersnaamPositie));
							
							return tabelCode;
						}
						
						else{
							
							//We zijn ingelogd maar er staat geen rooster voor deze week online
							errorRegel = "Er staat geen rooster op infoweb!";
							return errorRegel;
							
						}
						
					}
					
					//Als er iets misging dan willen we netjes aan de gebruiker laten weten wat er fout ging
					
					else if(statuscode == 404){
						
						// De pagina is niet gevonden
						errorRegel = "404 Pagina niet gevonden, Infoweb kon niet gevonden.";			
						return errorRegel;
						
					}
					
					else if(statuscode > 0){
						
						//Er ging iets anders mis, maar er is wel een verbinding gemaakt met de server
						errorRegel =  "Error " + statuscode + " er ging iets mis! Infoweb is niet gevonden.";
						return errorRegel;
						
					}
					
					else {
						
						//Er was geen antwoord van de server
						errorRegel = "Error 0 de server gaf geen antwoord en ligt hoogstwaarschijnlijk plat.";
						return errorRegel;
						
					}
					
				}
				
				else {

					errorRegel = "Er ging iets fout, ik kon de beveiliging van infoweb niet omzeilen";
					return errorRegel;
					
				}
				
			}
			
		}
		
		//Als er iets misging dan willen we netjes aan de gebruiker laten weten wat er fout ging
		
		else if(statuscode == 404){
			
			// De pagina is niet gevonden
			errorRegel = "404 Pagina niet gevonden, Infoweb kon niet gevonden.";			
			return errorRegel;
			
		}
		
		else if(statuscode > 0){
			
			//Er ging iets anders mis, maar er is wel een verbinding gemaakt met de server
			errorRegel =  "Error " + statuscode + " er ging iets mis! Infoweb is niet gevonden.";
			return errorRegel;
			
		}
		
		else {
			
			//Er was geen antwoord van de server
			errorRegel = "Error 0 de server gaf geen antwoord en ligt hoogstwaarschijnlijk plat.";
			return errorRegel;
		}
		
	}

}