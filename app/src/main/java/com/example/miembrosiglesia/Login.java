package com.example.miembrosiglesia;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.miembrosiglesia.sesionadmin.SessionManager;
import com.example.miembrosiglesia.sqlite.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Login extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;


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

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(Login.this, Bienvenido.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifica si hay conexion a internet
                if(estadoConect()==true){
                    final String usuario = usuarioT.getText().toString();
                    final String contrasenia = contraseniaT.getText().toString();

                    final ProgressDialog progDiag = new ProgressDialog(Login.this);
                    progDiag.setMessage("Iniciando proceso...");
                    progDiag.setTitle("por favor espere...");
                    progDiag.setMax(8000);
                    progDiag.setCancelable(false);
                    progDiag.show();

                    Response.Listener<String> respuesta = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject JSONrespuesta = new JSONObject(response);
                                boolean ok = JSONrespuesta.getBoolean("success");
                                if(ok == true){

                                    progDiag.dismiss();
                                    String nombreResp = JSONrespuesta.getString("nombre");

                                    session.setLogin(true);


                                    Intent bienvenido = new Intent(Login.this, Bienvenido.class);
                                    bienvenido.putExtra("nombre", nombreResp);
                                    Login.this.startActivity(bienvenido);
                                    Login.this.finish();
                                }else{
                                    progDiag.dismiss();
                                    AlertDialog.Builder alerta = new AlertDialog.Builder(Login.this);
                                    alerta.setMessage("Falló el Login")
                                            .setNegativeButton("Reintentar", null).create().show();
                                }
                            }catch (JSONException e){
                                e.getMessage();
                            }
                        }
                    };

                    Response.ErrorListener errorListener = new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progDiag.dismiss();

                            AlertDialog.Builder alerta = new AlertDialog.Builder(Login.this);
                            alerta.setMessage("Falló conexion a internet, favor revisar tu red wifi o datos móviles e intentar de nuevo")
                                    .setNegativeButton("OK", null).create().show();

                        }
                    };

                    LoginRequest loguear = new LoginRequest(usuario, contrasenia, respuesta, errorListener);
                    RequestQueue cola = Volley.newRequestQueue(Login.this);
                    cola.add(loguear);
                }else{
                    AlertDialog.Builder alerta = new AlertDialog.Builder(Login.this);
                    alerta.setMessage("Sin conexion a internet, por favor verifica la conexion e intenta de nuevo")
                            .setNegativeButton("Reintentar", null).create().show();
                }

            }
        });


    }


    public Boolean estadoConect(){

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


}
