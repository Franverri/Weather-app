package tp0.weather_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CitiesActivity extends AppCompatActivity {

    ListView listCities;
    ArrayAdapter<String> listAdapter;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;
    EditText etSearch;
    List<String> strCodigos = new ArrayList<String>();
    List<String> strCities = new ArrayList<String>();
    boolean coincidencia = false;

    final String urlAPI = "https://weather-tdp2.herokuapp.com/cities";
    RequestQueue queue;
    ProgressDialog progress;


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

        //Title bar name
        getSupportActionBar().setTitle("Ciudades");

        setContentView(R.layout.activity_cities);

        //Instance of ListView
        listCities = (ListView) findViewById(R.id.listCities);

        //Handle EditText for search with keyboard click
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String strCity = String.valueOf(etSearch.getText());
                    Log.i("Prueba", strCity);
                    hideKeyboard(CitiesActivity.this);
                    buscarCiudades(strCity);
                    return true;
                }
                return false;
            }
        });

        //Handle EditText for search with screen click
        etSearch.setLongClickable(false);
        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        String strCity = String.valueOf(etSearch.getText());
                        Log.i("Prueba", strCity);
                        hideKeyboard(CitiesActivity.this);
                        buscarCiudades(strCity);
                        return true;
                    }
                }
                return false;
            }
        });

        //Carga inicial vacia
        strCities.add("Sin resultados");
        addCities();

        //Hanlde item of listview click
        listCities.setClickable(true);
        listCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            if(coincidencia == true){
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                //Log.i("Click", "click la ciudad " + listAdapter.getItem(position) + " de mi ListView");
                //Log.i("Click", "click en el codigo " + strCodigos.get(position) + " de mi ListView");
                editorShared.putString("ciudadSeleccionada", listAdapter.getItem(position));
                editorShared.putString("idCiudad", strCodigos.get(position));
                editorShared.apply();
                goMainActivity();
            }

        }
        });
    }

    private void buscarCiudades(String strCity) {
        strCity = cleanString(strCity);
        if(strCity.length() < 3){
            etSearch.setError("Debe ingresar al menos 3 caracteres");
        } else {
            strCodigos.clear();
            strCities.clear();
            String url = urlAPI + "?filter=" + strCity;
            progress = ProgressDialog.show(CitiesActivity.this, "Actualizando ciudades",
                    "Recolectando datos...", true);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            Log.i("RESPUESTA","Response: " + response.toString());
                            if(response.isNull(0)){
                                strCities.add("No existen coincidencias");
                                coincidencia = false;
                            } else {
                                coincidencia = true;
                            }
                            progress.dismiss();
                            Toast.makeText(CitiesActivity.this, "Ciudades actualizadas",
                                    Toast.LENGTH_LONG).show();
                            actualizarListaCiudades(response);
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("Error.Response", String.valueOf(error));
                            progress.dismiss();
                            Toast.makeText(CitiesActivity.this, "No fue posible conectarse al servidor, por favor intente más tarde",
                                    Toast.LENGTH_LONG).show();
                        }
                    });

            // Add the request to the RequestQueue.
            queue.add(jsonArrayRequest);
        }
    }

    private void actualizarListaCiudades(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            JSONObject jsonobject = null;
            try {
                jsonobject = response.getJSONObject(i);
            } catch (JSONException e) {
                Log.i("JSON","Error al parsear JSON");
            }
            try {
                String idCiudad = jsonobject.getString("id");
                String nombreCiudad = jsonobject.getString("nombre");
                Log.i("JSON","ID Ciudad    : " + idCiudad );
                Log.i("JSON","Nombre Ciudad: " + nombreCiudad);
                strCodigos.add(idCiudad);
                strCities.add(nombreCiudad);
            } catch (JSONException e) {
                Log.i("JSON","Error al obtener datos del JSON");
            }
        }
        addCities();
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void addCities() {
        //Log.i("LISTA", String.valueOf(strCities));
        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                strCities);

        listCities.setAdapter(listAdapter);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String cleanString(String texto) {
        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
        texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return texto;
    }
}
