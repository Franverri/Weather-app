package tp0.weather_app;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CitiesActivity extends AppCompatActivity {

    ListView listCities;
    ArrayAdapter<String> listAdapter;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editorShared;
    EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        addCities();

        //Handle EditText for search with keyboard click
        etSearch = (EditText) findViewById(R.id.etSearch);
        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String strCity = String.valueOf(etSearch.getText());
                    Log.i("Prueba", strCity);
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
                        return true;
                    }
                }
                return false;
            }
        });

        //Hanlde item of listview click
        listCities.setClickable(true);
        listCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

                //Object o = listView.getItemAtPosition(position);
                // Realiza lo que deseas, al recibir clic en el elemento de tu listView determinado por su posicion.
                Log.i("Click", "click en el elemento " + listAdapter.getItem(position) + " de mi ListView");
                editorShared.putString("ciudadSeleccionada", listAdapter.getItem(position));
                editorShared.apply();
                goMainActivity();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void addCities() {
        //Obtain cities
        String[] strCities = {
                "Buenos Aires",
                "Cordoba",
                "Santa Fe",
                "Chubut",
                "Tucuman"
        };

        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                strCities);

        listCities.setAdapter(listAdapter);
    }
}
