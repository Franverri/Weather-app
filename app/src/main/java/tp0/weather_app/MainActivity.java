package tp0.weather_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    String title;
    String idCiudad;

    String dia3, dia4, dia5;
    int horas, minutos;

    final String urlAPI = "https://weather-tdp2.herokuapp.com/weather/";
    RequestQueue queue;
    ProgressDialog progress;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = Volley.newRequestQueue(this);

        //SharedPref para almacenar ciudad seleccionada por usuario
        sharedPref = getSharedPreferences(getString(R.string.saved_data), Context.MODE_PRIVATE);
        editorShared = sharedPref.edit();

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Get city name or put default as title name
        final String strCity = sharedPref.getString("ciudadSeleccionada", "ninguna");
        if(strCity.equals("ninguna")){
            title = "Buenos Aires";
            idCiudad = "3688357";
        } else {
            title = strCity;
            //Get City ID
            idCiudad = sharedPref.getString("idCiudad", "ninguna");
        }

        setContentView(R.layout.activity_main);

        //Obtengo dia y hora
        configurarDiaYHora();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress = ProgressDialog.show(MainActivity.this, "Actualizando clima",
                        "Recolectando datos...", true);
                actualizarClima(idCiudad);
            }
        });
    }

    private void configurarDiaYHora() {

        Calendar calendar = Calendar.getInstance();
        int calendarDay = calendar.get(Calendar.DAY_OF_WEEK);

        calendar.add(Calendar.DATE, 2);
        int calendarNumberDay3 = calendar.get(Calendar.DAY_OF_MONTH);
        int calendarMonth3 = calendar.get(Calendar.MONTH) + 1;

        calendar.add(Calendar.DATE, 1);
        int calendarNumberDay4 = calendar.get(Calendar.DAY_OF_MONTH);
        int calendarMonth4 = calendar.get(Calendar.MONTH) + 1;

        calendar.add(Calendar.DATE, 1);
        int calendarNumberDay5 = calendar.get(Calendar.DAY_OF_MONTH);
        int calendarMonth5 = calendar.get(Calendar.MONTH) + 1;

        switch (calendarDay) {
            case Calendar.MONDAY:
                dia3 = "Miércoles, " + calendarNumberDay3 + "/" + calendarMonth3;
                dia4 = "Jueves, " + calendarNumberDay4 + "/" + calendarMonth4;
                dia5 = "Viernes, " + calendarNumberDay5 + "/" + calendarMonth5;
                break;

            case Calendar.TUESDAY:
                dia3 = "Jueves, " + calendarNumberDay3 + "/" + calendarMonth3;
                dia4 = "Viernes, " + calendarNumberDay4 + "/" + calendarMonth4;
                dia5 = "Sábado, " + calendarNumberDay5 + "/" + calendarMonth5;
                break;

            case Calendar.WEDNESDAY:
                dia3 = "Viernes, " + calendarNumberDay3 + "/" + calendarMonth3;
                dia4 = "Sábado, " + calendarNumberDay4 + "/" + calendarMonth4;
                dia5 = "Domingo, " + calendarNumberDay5 + "/" + calendarMonth5;
                break;

            case Calendar.THURSDAY:
                dia3 = "Sábado, " + calendarNumberDay3 + "/" + calendarMonth3;
                dia4 = "Domingo, " + calendarNumberDay4 + "/" + calendarMonth4;
                dia5 = "Lunes, " + calendarNumberDay5 + "/" + calendarMonth5;
                break;

            case Calendar.FRIDAY:
                dia3 = "Domingo, " + calendarNumberDay3 + "/" + calendarMonth3;
                dia4 = "Lunes, " + calendarNumberDay4 + "/" + calendarMonth4;
                dia5 = "Martes, " + calendarNumberDay5 + "/" + calendarMonth5;
                break;

            case Calendar.SATURDAY:
                dia3 = "Lunes, " + calendarNumberDay3 + "/" + calendarMonth3;
                dia4 = "Martes, " + calendarNumberDay4 + "/" + calendarMonth4;
                dia5 = "Miércoles, " + calendarNumberDay5 + "/" + calendarMonth5;
                break;

            case Calendar.SUNDAY:
                dia3 = "Martes, " + calendarNumberDay3 + "/" + calendarMonth3;
                dia4 = "Miércoles, " + calendarNumberDay4 + "/" + calendarMonth4;
                dia5 = "Jueves, " + calendarNumberDay5 + "/" + calendarMonth5;
                break;
        }

        TextView tvDia3 = (TextView) findViewById(R.id.dia3);
        tvDia3.setText(dia3);

        TextView tvDia4 = (TextView) findViewById(R.id.dia4);
        tvDia4.setText(dia4);

        TextView tvDia5 = (TextView) findViewById(R.id.dia5);
        tvDia5.setText(dia5);
    }

    private void actualizarClima(String idCiudad) {
        String url = urlAPI + idCiudad;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                        progress.dismiss();
                        Toast.makeText(MainActivity.this, "Clima actualizado",
                                Toast.LENGTH_LONG).show();
                        actualizarTarjetas(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));
                        progress.dismiss();
                        Toast.makeText(MainActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest);


        /*
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, urlAPI, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("RESPUESTA","Response: " + response.toString());
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error.Response", String.valueOf(error));

                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);*/
    }

    private void actualizarTarjetas(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = response.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
            try {
                String numDia = String.valueOf(i + 1);
                String tempDia = "";
                if (!jsonobject.isNull("temp_diurna")){
                    tempDia = jsonobject.getString("temp_diurna") + "°C";
                }
                String tempNoche = jsonobject.getString("temp_nocturna") + "°C";
                Log.i("JSON","Temperatura Día: " + tempDia);
                Log.i("JSON","Temperatura Noche: " + tempNoche);
                actualizarTarjetaDia(numDia,tempDia,tempNoche);
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
    }

    private void actualizarTarjetaDia(String numDia, String tempDia, String tempNoche) {
        String id1 = "temperaturaDia" + numDia;
        int idDia = getResources().getIdentifier(id1,
                "id", getPackageName());
        String id2 = "temperaturaNoche" + numDia;
        int idNoche = getResources().getIdentifier(id2,
                "id", getPackageName());
        TextView tvDia = (TextView)findViewById(idDia);
        TextView tvNoche = (TextView)findViewById(idNoche);
        tvDia.setText(tempDia);
        tvNoche.setText(tempNoche);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void goCitiesActivity(MenuItem item) {
        Intent intent = new Intent(this, CitiesActivity.class);
        startActivity(intent);
    }
}
