package co.edu.unal.webservicesapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FocusActivity extends AppCompatActivity {
    private String url = "https://www.datos.gov.co/resource/4w3i-wxax.json?$$app_token=TOKEN&sorteo=";

    TextView number;
    TextView date;
    TextView win;
    TextView set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus);

        number = findViewById(R.id.editTextName);
        date = findViewById(R.id.editTextURL);
        win = findViewById(R.id.editTextPhone);
        set = findViewById(R.id.editTextEmail);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            String Value = extras.getString("id");

            StringRequest getRequest = new StringRequest(Request.Method.GET, url+Value, response -> {
                try {
                    JSONArray array = new JSONArray(response);
                    JSONObject object = array.getJSONObject(0);

                    number.setText(object.getString("sorteo"));
                    date.setText(object.getString("fecha").substring(0,10));
                    win.setText(object.getString("n_mero"));
                    set.setText(object.getString("serie"));
                } catch (JSONException e) { e.printStackTrace(); }
            }, error -> { });

            Volley.newRequestQueue(this).add(getRequest);
        }
    }
}