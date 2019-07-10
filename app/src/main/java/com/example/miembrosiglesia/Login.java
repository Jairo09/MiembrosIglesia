package com.example.miembrosiglesia;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TextView registro = findViewById(R.id.tvRegistrarLogin);
        final EditText usuarioT = findViewById(R.id.etNombreLogin);
        final EditText contraseniaT = findViewById(R.id.etContraseniaLogin);

        Button btnLogin = findViewById(R.id.btnIngresarLogin);
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regist = new Intent(Login.this, Registro.class);
                Login.this.startActivity(regist);
                finish();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usuario = usuarioT.getText().toString();
                final String contrasenia = contraseniaT.getText().toString();
                Response.Listener<String> respuesta = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject JSONrespuesta = new JSONObject(response);
                            boolean ok = JSONrespuesta.getBoolean("success");
                            if(ok == true){
                                String nombreResp = JSONrespuesta.getString("nombre");

                                Intent bienvenido = new Intent(Login.this, Bienvenido.class);
                                bienvenido.putExtra("nombre", nombreResp);
                                Login.this.startActivity(bienvenido);
                                Login.this.finish();
                            }else{
                                AlertDialog.Builder alerta = new AlertDialog.Builder(Login.this);
                                alerta.setMessage("Falló el Login")
                                        .setNegativeButton("Reintentar", null).create().show();
                            }
                        }catch (JSONException e){
                            e.getMessage();
                        }
                    }
                };
                LoginRequest loguear = new LoginRequest(usuario, contrasenia, respuesta);
                RequestQueue cola = Volley.newRequestQueue(Login.this);
                cola.add(loguear);

            }
        });
    }
}
