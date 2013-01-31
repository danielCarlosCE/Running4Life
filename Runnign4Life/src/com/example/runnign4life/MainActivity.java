package com.example.runnign4life;

import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements LocationListener {

	public static final String FILENAME = "corridas";
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
		editText1 = (EditText) findViewById(R.id.editText1);
		editText2 = (EditText) findViewById(R.id.editText2);
		button1 = (Button) findViewById(R.id.button1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void iniciar(View view) {
		if (stop == -1) {
			Long tempoEmSegundos = (System.currentTimeMillis() - this.TEMPO_INICIAL) / 1000;
			editText2.setText(tempoEmSegundos + "s");
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
		String corrida = distanciaPercorrida/1000 + "km em " + tempoEmSegundos + "s ("
				+ new Date().toGMTString() + ")";
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
			editText1.setText(distanciaPercorrida/1000 + "km");

		} else {
			Toast.makeText(this, "n‹o conectou GPS!", Toast.LENGTH_LONG).show();
		}

	}

	public void historico(View view) {
		startActivity(new Intent(this, HistoryActivity.class));
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
