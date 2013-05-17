package com.example.runnign4life;

import java.text.DecimalFormat;
import java.util.Date;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.runnign4life.dao.CorridaDAO;
import com.example.runnign4life.fragments.CorridaFragment;
import com.example.runnign4life.fragments.HistoricoFragment;
import com.example.runnign4life.fragments.TabListener;

public class MainActivity extends Activity implements LocationListener {

	
	private Location lastLocation = null;
	private LocationManager locationmanager;
	private Double distanciaPercorrida = 0.0;
	private Long TEMPO_INICIAL;
	private EditText editText1;
	private EditText editText2;
	private Button button1;
	private int stop = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		
		Tab tab = actionBar.newTab()
	            .setText("Corridas")
	            .setTabListener(new TabListener<CorridaFragment>(
                    this, "corridas", CorridaFragment.class));
	    actionBar.addTab(tab);
	    
	    tab = actionBar.newTab()
	            .setText("Hist—rico")
	            .setTabListener(new TabListener<HistoricoFragment>(
                    this, "historico", HistoricoFragment.class));
	    actionBar.addTab(tab);
	    
		
	}

	public void iniciar(View view) {
		
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		button1 = (Button) findViewById(R.id.button1);
		if (stop == -1) {
			Long tempoEmSegundos = (System.currentTimeMillis() - this.TEMPO_INICIAL) / 1000;
			lastLocation = null;
			button1.setText("Iniciar Corrida");
			salvarCorrida(tempoEmSegundos);
			stop *= -1;
		} else {
			editText1.setText("0");
			editText2.setText("0");
			distanciaPercorrida = 0.0;
			button1.setText("Parar Corrida");
			this.locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			this.locationmanager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 5000, 7, this);
			TEMPO_INICIAL = System.currentTimeMillis();
			stop *= -1;
		}
	}

	private void salvarCorrida(Long tempoEmSegundos) {
		
		DecimalFormat df = new DecimalFormat("#,###.00");  
		String corrida = df.format(distanciaPercorrida / 1000 ) + "km em "
				+ tempoEmSegundos + "s (" + new Date().toGMTString() + ")";
		Toast.makeText(this, corrida, 900).show();
		new CorridaDAO().corridas.add(corrida);

	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null) {

			if (this.lastLocation != null) {
				float result[] = new float[1];

				Location.distanceBetween(lastLocation.getLatitude(),
						lastLocation.getLongitude(), location.getLatitude(),
						location.getLongitude(), result);

				this.distanciaPercorrida = this.distanciaPercorrida + result[0];

			}
			this.lastLocation = location;

			System.out.println("DISTANCIA PERCORRIDA: " + distanciaPercorrida);
			DecimalFormat df = new DecimalFormat("#,###.00");
			editText1.setText(df.format(distanciaPercorrida / 1000 ) + "km");
			Long tempoEmSegundos = (System.currentTimeMillis() - this.TEMPO_INICIAL) / 1000;
			editText2.setText(tempoEmSegundos+"s");

		} else {
			Toast.makeText(this, "n‹o conectou GPS!", Toast.LENGTH_LONG).show();
		}

	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}
}
