package com.example.hellokitty_200668;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private Button btnGuardar;
    private Button btnActualizar;
    private Button btnBuscar;
    private Button btnEliminar;
    private EditText etNumero;

    private EditText etNombre;

    private EditText etCumpleaños;

    private EditText etEstatura;

    private EditText etPeso;

    private EditText etComidafav;

    private ListView lvPersonajes;
    private RequestQueue colaPeticiones;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<String> origenDatos=new ArrayList<String>();
    private ArrayAdapter<String>adapter;

    private String url="http://10.10.62.3:3000/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGuardar= findViewById(R.id.btnGuardar);
        btnActualizar= findViewById(R.id.btnActualizar);
        btnBuscar= findViewById(R.id.btnBuscar);
        btnEliminar= findViewById(R.id.btnEliminar);
        etNumero= findViewById(R.id.etNumero);
        etNombre= findViewById(R.id.etNombre);
        etCumpleaños= findViewById(R.id.etCumpleaños);
        etEstatura= findViewById(R.id.etEstatura);
        etPeso= findViewById(R.id.etPeso);
        etComidafav= findViewById(R.id.etComidafav);
        lvPersonajes= findViewById(R.id.lvPersonajes);
        colaPeticiones= Volley.newRequestQueue(this);
        listarProductos();
        

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etNumero.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Ingrese el código de barras", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.DELETE,
                            url + "borrar/" + etNumero.getText().toString(),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Personaje eliminado")) {
                                            Toast.makeText(MainActivity.this, "Producto Eliminado con EXITO!", Toast.LENGTH_SHORT).show();
                                            etNumero.setText("");
                                            etNombre.setText("");
                                            etCumpleaños.setText("");
                                            etEstatura.setText("");
                                            etPeso.setText("");
                                            etComidafav.setText("");
                                            adapter.clear();
                                            lvPersonajes.setAdapter(adapter);
                                            listarProductos();
                                        } else if (response.getString("status").equals("Not Found")) {
                                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    colaPeticiones.add(jsonObjectRequest);
                }
            }
        });




        //Button update
        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNumero.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Primero use el BOTÓN BUSCAR!", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject productos = new JSONObject();
                    try {
                        productos.put("numero", etNumero.getText().toString());
                        if (!etNombre.getText().toString().isEmpty()) {
                            productos.put("nombre", etNombre.getText().toString());
                        }

                        if (!etCumpleaños.getText().toString().isEmpty()) {
                            productos.put("cumpleaños", etCumpleaños.getText().toString());
                        }

                        if (!etEstatura.getText().toString().isEmpty()) {
                            productos.put("estatura", etEstatura.getText().toString());
                        }

                        if (!etPeso.getText().toString().isEmpty()) {
                            productos.put("peso",etPeso.getText().toString());
                        }

                        if (!etComidafav.getText().toString().isEmpty()) {
                            productos.put("comidafav",etComidafav.getText().toString());
                        }

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    JsonObjectRequest actualizar = new JsonObjectRequest(
                            Request.Method.PUT,
                            url + "actualizar/" + etNumero.getText().toString(),
                            productos,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Personaje actualizado")) {
                                            Toast.makeText(MainActivity.this, "Producto actualizado!", Toast.LENGTH_SHORT).show();
                                            etNumero.setText("");
                                            etNombre.setText("");
                                            etCumpleaños.setText("");
                                            etEstatura.setText("");
                                            etPeso.setText("");
                                            etComidafav.setText("");
                                            adapter.clear();
                                            lvPersonajes.setAdapter(adapter);
                                            listarProductos();
                                        } else if (response.getString("status").equals("No encontrado")) {
                                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    colaPeticiones.add(actualizar);
                }
            }
        });

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest peticion=new JsonObjectRequest(
                        Request.Method.GET,
                        url + etNumero.getText().toString(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if(response.has("status"))
                                    Toast.makeText(MainActivity.this, "producto no encontrado", Toast.LENGTH_SHORT).show();
                                else {
                                    try {
                                        etNombre.setText(response.getString("nombre"));
                                        etCumpleaños.setText(response.getString("cumpleaños"));
                                        etEstatura.setText(response.getString("estatura"));
                                        etPeso.setText(response.getString("peso"));
                                        etComidafav.setText(response.getString("comidafav"));
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(peticion);
            }
        });





        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject producto = new JSONObject();
                try {
                    producto.put("numero",etNumero.getText().toString());
                    producto.put("nombre",etNombre.getText().toString());
                    producto.put("cumpleaños",etCumpleaños.getText().toString());
                    producto.put("estatura",etEstatura.getText().toString());
                    producto.put("peso",etPeso.getText().toString());
                    producto.put("comidafav",etComidafav.getText().toString());
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url +"insert",
                        producto,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("Personaje insertado")) {
                                        Toast.makeText(MainActivity.this, "Producto insertado con EXITO!", Toast.LENGTH_SHORT).show();
                                            etNumero.setText("");
                                            etNombre.setText("");
                                            etCumpleaños.setText("");
                                            etEstatura.setText("");
                                            etPeso.setText("");
                                            etComidafav.setText("");
                                            adapter.clear();
                                            lvPersonajes.setAdapter(adapter);
                                        listarProductos();
                                    }
                                }catch (JSONException e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(jsonObjectRequest);
            }
        });
    }
    protected void listarProductos(){
        String url="http://10.10.62.3:3000/";
        jsonArrayRequest= new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i=0;i<response.length();i++){
                            try {
                                String numero=response.getJSONObject(i).getString("numero");
                                String nombre=response.getJSONObject(i).getString("nombre");
                                String cumpleaños=response.getJSONObject(i).getString("cumpleaños");
                                origenDatos.add(numero + ": " +nombre+ ": " +cumpleaños);
                            } catch (JSONException e) {

                            }
                        }
                        adapter=new ArrayAdapter<String>(MainActivity.this,
                                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                                origenDatos);
                        lvPersonajes.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        colaPeticiones.add(jsonArrayRequest);
    }
}
