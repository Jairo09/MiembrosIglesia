package com.example.miembrosiglesia;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class Bienvenido extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPageAdapter adapter;
    private ArrayList<Miembro> lista_de_seniores;

    private SQLiteHandler db;
    private SessionManager session;
    String usuario = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);



        //Reemplaza el toolbar original
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Implementacion de Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //Cambiar color titulo sección nav drawer
        NavigationView navView = (NavigationView)findViewById(R.id.nav_view);
        MenuItem tools =  navView.getMenu().findItem(R.id.opciones);
        SpannableString spanString = new SpannableString(tools.getTitle());
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,
                R.color.gris)), 0, spanString.length(), 0);
        tools.setTitle(spanString);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nuevo = new Intent(Bienvenido.this, IngresarMiembro.class);
                nuevo.putExtra("nombre", usuario);
                Bienvenido.this.startActivity(nuevo);
                Bienvenido.this.finish();
            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        //traer datos de sqlite
        ArrayList<Miembro> miembrosList = db.getMiembrosDetails();

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //////////LISTENER////////////////////////
        Response.Listener<String> respuesta = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Toast.makeText(getContext(), ""+response, Toast.LENGTH_LONG).show();
                    //JSONObject JSONrespuesta = new JSONObject(response);

                    JSONArray array = new JSONArray(response);

                    if (!response.isEmpty()) {
                        //Sincronizando db con SQLite
                        String nombreLite;
                        String apellidoLite;
                        String sociedadLite;

                        for(int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            nombreLite = obj.getString("nombre");
                            apellidoLite = obj.getString("apellido");
                            sociedadLite = obj.getString("sociedad");

                            // Inserting row in users table
                            db.addMiembro(nombreLite, apellidoLite, sociedadLite);

                        }


                        Toast.makeText(getApplicationContext(), "Lista de miembros actualizados", Toast.LENGTH_LONG).show();


                    } else {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(Bienvenido.this);
                        alerta.setMessage("No se pudieron actualizar los registros")
                                .setNegativeButton("OK", null).create().show();


                        Toast.makeText(getApplicationContext(), "Los datos no se han actualizado", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            }

        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Los datos no se han actualizado", Toast.LENGTH_LONG).show();
            }
        };


        AllRequest regist = new AllRequest(respuesta, errorListener);
        regist.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue cola = Volley.newRequestQueue(Bienvenido.this);
        cola.add(regist);
        /////////FIN LISTENER//////////////////////
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        tabLayout = findViewById(R.id.tabLayout_id);
        viewPager = findViewById(R.id.viewpager_id);
        adapter = new ViewPageAdapter(getSupportFragmentManager());

        //ADD FRAGMENT HERE
        adapter.addFragment(new FragmentSeniores(), "Señores");
        adapter.addFragment(new FragmentSenioras(), "Señoras");
        adapter.addFragment(new FragmentJovenes(), "Jóvenes");
        adapter.addFragment(new FragmentNinios(), "Niños");

        tabLayout.setTabTextColors(Color.parseColor("#9f9b9b"), Color.parseColor("#ffffff"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //iconColor(tab, "#D81B60");
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
               // iconColor(tab, "#ada7a7");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        /*
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_menu_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_menu_galery);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_menu_manage);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_menu_send);
         */
        lista_de_seniores = new ArrayList<>();
        llenarNombres();





        //Remove shadow from de action bar
       // ActionBar actionBar = getSupportActionBar();
        //actionBar.setElevation(0);

       /*
        final TextView mensaje = findViewById(R.id.tvMensaje);
        Intent i = this.getIntent();
        String nombre = i.getStringExtra("nombre");
        mensaje.setText("Bienvenido "+nombre);
        */
    }//Fin de onCreate




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } */

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {
              logoutUser();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void cargarSQLiteReg(){
        //Luego obtenemos los datos de SQLite para llenar las vistas
        ArrayList<Miembro> listaMiembros = db.getMiembrosDetails();

        /////// obtenerDatosRequest//////////
        Bundle argsSeniores = new Bundle();
        Bundle argsSenioras = new Bundle();
        Bundle argsJovenes = new Bundle();
        Bundle argsNinios = new Bundle();

        ArrayList<String> nombresSeniores = new ArrayList<>();
        ArrayList<String> apellidosSeniores = new ArrayList<>();
        ArrayList<String> sociedadesSeniores = new ArrayList<>();

        ArrayList<String> nombresSenioras = new ArrayList<>();
        ArrayList<String> apellidosSenioras = new ArrayList<>();
        ArrayList<String> sociedadesSenioras = new ArrayList<>();

        ArrayList<String> nombresJovenes = new ArrayList<>();
        ArrayList<String> apellidosJovenes = new ArrayList<>();
        ArrayList<String> sociedadesJovenes = new ArrayList<>();

        ArrayList<String> nombresNinios = new ArrayList<>();
        ArrayList<String> apellidosNinios = new ArrayList<>();
        ArrayList<String> sociedadesNinios = new ArrayList<>();

        String nombre;
        String apellido;
        String sociedad;

        for(int i = 0; i < listaMiembros.size(); i++) {


            if(listaMiembros.get(i).getSociedad().equals("Señores")){
                nombre = listaMiembros.get(i).getNombre();
                apellido = listaMiembros.get(i).getApellido();
                sociedad = listaMiembros.get(i).getSociedad();

                nombresSeniores.add(nombre);
                apellidosSeniores.add(apellido);
                sociedadesSeniores.add(sociedad);


            }else if(listaMiembros.get(i).getSociedad().equals("Señoras")){
                nombre = listaMiembros.get(i).getNombre();
                apellido = listaMiembros.get(i).getApellido();
                sociedad = listaMiembros.get(i).getSociedad();

                nombresSenioras.add(nombre);
                apellidosSenioras.add(apellido);
                sociedadesSenioras.add(sociedad);


            }else if(listaMiembros.get(i).getSociedad().equals("Jóvenes")){
                nombre = listaMiembros.get(i).getNombre();
                apellido = listaMiembros.get(i).getApellido();
                sociedad = listaMiembros.get(i).getSociedad();

                nombresJovenes.add(nombre);
                apellidosJovenes.add(apellido);
                sociedadesJovenes.add(sociedad);


            }else{
                nombre = listaMiembros.get(i).getNombre();
                apellido = listaMiembros.get(i).getApellido();
                sociedad = listaMiembros.get(i).getSociedad();

                nombresNinios.add(nombre);
                apellidosNinios.add(apellido);
                sociedadesNinios.add(sociedad);


            }

        }

        argsSeniores.putStringArrayList("nombres", nombresSeniores);
        argsSeniores.putStringArrayList("apellidos", apellidosSeniores);
        argsSeniores.putStringArrayList("sociedades", sociedadesSeniores);

        argsSenioras.putStringArrayList("nombres", nombresSenioras);
        argsSenioras.putStringArrayList("apellidos", apellidosSenioras);
        argsSenioras.putStringArrayList("sociedades", sociedadesSenioras);

        argsJovenes.putStringArrayList("nombres", nombresJovenes);
        argsJovenes.putStringArrayList("apellidos", apellidosJovenes);
        argsJovenes.putStringArrayList("sociedades", sociedadesJovenes);

        argsNinios.putStringArrayList("nombres", nombresNinios);
        argsNinios.putStringArrayList("apellidos", apellidosNinios);
        argsNinios.putStringArrayList("sociedades", sociedadesNinios);

        FragmentSeniores fragSeniores = new FragmentSeniores();
        fragSeniores.setArguments(argsSeniores);

        FragmentSenioras fragSenioras = new FragmentSenioras();
        fragSenioras.setArguments(argsSenioras);

        FragmentJovenes fragJovenes = new FragmentJovenes();
        fragJovenes.setArguments(argsJovenes);

        FragmentNinios fragNinios = new FragmentNinios();
        fragNinios.setArguments(argsNinios);

        //ADD FRAGMENT HERE
        adapter.addFragment(fragSeniores, "Señores");
        adapter.addFragment(fragSenioras, "Señoras");
        adapter.addFragment(fragJovenes, "Jóvenes");
        adapter.addFragment(fragNinios, "Niños");


        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        //////////////////////////FIN OBTENER DATOS/////////////////////////////
    }

    private void logoutUser() {
        session.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(Bienvenido.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void llenarNombres(){
        lista_de_seniores.add(new Miembro("ALISON", "JUAREZ", "JOVENES"));
        lista_de_seniores.add(new Miembro("MARCOS", "LOPEZ", "SEÑORES"));
        lista_de_seniores.add(new Miembro("HUGO", "FLORES", "JOVENES"));
        lista_de_seniores.add(new Miembro("SOFIA", "SALINAS", "NIÑOS"));
        lista_de_seniores.add(new Miembro("ALISON", "JUAREZ", "JOVENES"));
        lista_de_seniores.add(new Miembro("MARCOS", "LOPEZ", "SEÑORES"));
        lista_de_seniores.add(new Miembro("HUGO", "FLORES", "JOVENES"));
        lista_de_seniores.add(new Miembro("SOFIA", "SALINAS", "NIÑOS"));

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

    public void cargarListas(JSONArray array) throws JSONException {

        ArrayList<String> nombres = new ArrayList<>();
        ArrayList<String> apellidos = new ArrayList<>();
        ArrayList<String> sociedades = new ArrayList<>();

        for(int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);

            String nombre = obj.getString("nombre");
            String apellido = obj.getString("apellido");
            String sociedad = obj.getString("sociedad");
            Toast.makeText(this, ""+nombre, Toast.LENGTH_SHORT).show();
            nombres.add(nombre);
            apellidos.add(apellido);
            sociedades.add(sociedad);

            Miembro newMiembro = new Miembro(nombre, apellido, sociedad);

            //lstMiembros.add(newMiembro);
        }

        //argsSeniores.putStringArrayList("nombres", nombres);
        //argsSeniores.putStringArrayList("apellidos", apellidos);
        //argsSeniores.putStringArrayList("sociedades", sociedades);


    }

    public void obtenerDatosRequest(){
        Response.Listener<String> respuesta = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Toast.makeText(getContext(), ""+response, Toast.LENGTH_LONG).show();
                    //JSONObject JSONrespuesta = new JSONObject(response);

                    JSONArray array = new JSONArray(response);

                    if (!response.isEmpty()) {
                        cargarListas(array);
                    } else {
                        AlertDialog.Builder alerta = new AlertDialog.Builder(Bienvenido.this);
                        alerta.setMessage("No se pudieron cargar los registros")
                                .setNegativeButton("OK", null).create().show();
                    }
                } catch (JSONException e) {
                    e.getMessage();
                }
            }

        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Bienvenido.this, "Error Listener", Toast.LENGTH_SHORT).show();
            }
        };


        AllRequest regist = new AllRequest(respuesta, errorListener);
        RequestQueue cola = Volley.newRequestQueue(Bienvenido.this);
        cola.add(regist);


    }



}
