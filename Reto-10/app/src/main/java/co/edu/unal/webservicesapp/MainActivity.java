package co.edu.unal.webservicesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private String url = "https://www.datos.gov.co/resource/4w3i-wxax.json?$$app_token=TOKEN";
    ArrayList<Pair<String, String>> data;
    ArrayAdapter defaultAdapter;

    private ListView obj;
    private SearchView byName;
    private SearchView byType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        data = new ArrayList<>();

        StringRequest getRequest = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    data.add(new Pair<>(object.getString("sorteo"), object.getString("fecha").substring(0,10)));
                }

                defaultAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, data){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);

                        text1.setText("Lottery Number: "+data.get(position).first);
                        text2.setText(data.get(position).second);
                        return view;
                    }
                };

                obj = findViewById(R.id.listView1);
                obj.setAdapter(defaultAdapter);
                obj.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    String id_To_Search = data.get(arg2).first;

                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("id", id_To_Search);

                    Intent intent = new Intent(getApplicationContext(),FocusActivity.class);

                    intent.putExtras(dataBundle);
                    startActivity(intent);
                });
            } catch (JSONException e) { e.printStackTrace(); }
        }, error -> { });

        Volley.newRequestQueue(this).add(getRequest);

        byName = findViewById(R.id.searchByName);
        byType = findViewById(R.id.searchByType);
        byName.setOnQueryTextListener(this);
        byType.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(byName.getQuery().toString().equals("") && byType.getQuery().toString().equals("")) return false;

        String finalUrl = url;
        if(!byName.getQuery().toString().equals("")) finalUrl += "&sorteo="+byName.getQuery().toString();
        if(!byType.getQuery().toString().equals("")) finalUrl += "&fecha="+byType.getQuery().toString()+"T00:00:00.000";

        ArrayList<Pair<String, String>> provData = new ArrayList<>();
        StringRequest getRequest = new StringRequest(Request.Method.GET, finalUrl, response -> {
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    provData.add(new Pair<>(object.getString("sorteo"), object.getString("fecha").substring(0,10)));
                }

                ArrayAdapter provAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_2, android.R.id.text1, provData){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text1 = view.findViewById(android.R.id.text1);
                        TextView text2 = view.findViewById(android.R.id.text2);

                        text1.setText("Lottery Number: "+provData.get(position).first);
                        text2.setText(provData.get(position).second);
                        return view;
                    }
                };

                obj.setAdapter(provAdapter);
                obj.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                    String id_To_Search = provData.get(arg2).first;
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("id", id_To_Search);
                    Intent intent = new Intent(getApplicationContext(),FocusActivity.class);
                    intent.putExtras(dataBundle);
                    startActivity(intent);
                });
            } catch (JSONException e) { e.printStackTrace(); }
        }, error -> { });

        Volley.newRequestQueue(this).add(getRequest);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(byName.getQuery().toString().equals("") && byType.getQuery().toString().equals("")){
            obj.setAdapter(defaultAdapter);
            obj.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                Bundle dataBundle = new Bundle();
                dataBundle.putString("id", data.get(arg2).first);
                Intent intent = new Intent(getApplicationContext(),FocusActivity.class);
                intent.putExtras(dataBundle);
                startActivity(intent);
            });
        }

        return false;
    }

    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }

}