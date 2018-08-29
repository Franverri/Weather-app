package tp0.weather_app;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CitiesActivity extends AppCompatActivity {

    ListView listCities;
    ArrayAdapter<String> listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove notification bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Title bar name
        getSupportActionBar().setTitle("Ciudades");

        setContentView(R.layout.activity_cities);

        //Instance of ListView
        listCities = (ListView) findViewById(R.id.listCities);
        addCities();
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
