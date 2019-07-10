package com.example.miembrosiglesia;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Registro extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        final TextView nombre = findViewById(R.id.etNombre);
        final TextView contrasenia = findViewById(R.id.etContrasenia);
        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nombreS = nombre.getText().toString().trim();
                final String contraseniaS = contrasenia.getText().toString().trim();


                Response.Listener<String> respuesta = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(Registro.this, "Registro agregado con exito", Toast.LENGTH_LONG).show();
                        try{
                            JSONObject JSONrespuesta = new JSONObject(response);
                            boolean ok = JSONrespuesta.getBoolean("success");
                            if(ok == true){

                                Intent intent = new Intent(Registro.this, Login.class);
                                Registro.this.startActivity(intent);
                                Registro.this.finish();
                            }else{

                                AlertDialog.Builder alerta = new AlertDialog.Builder(Registro.this);
                                alerta.setMessage("Falló el Registro")
                                        .setNegativeButton("Reintentar", null).create().show();
                            }
                        }catch (JSONException e){
                            e.getMessage();
                        }
                    }
                };

                RegistroRequest registrar = new RegistroRequest(nombreS, contraseniaS, respuesta);
                RequestQueue cola = Volley.newRequestQueue(Registro.this);
                cola.add(registrar);
            }
        });
    }
}
