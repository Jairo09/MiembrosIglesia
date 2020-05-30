package com.example.miembrosiglesia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class IngresarMiembro extends AppCompatActivity {

    ImageView foto;
    Bitmap bitmap = null;
    String usuario = "JAIRO";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingresar_miembro);



        //Spinner
        String [] opciones = { "Señores", "Señoras", "Jóvenes", "Niños"};
        final Spinner spinner = findViewById(R.id.idspinner);
        // Creando un arrayAdapter y asignandolo al spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(IngresarMiembro.this, R.layout.spinner_item_configuracion, opciones);

        spinner.setAdapter(adapter);

        final EditText etNombre = findViewById(R.id.actNombre);
        final EditText etApellido = findViewById(R.id.actApellido);
        foto = findViewById(R.id.idImagen);

        FirebaseMessaging.getInstance().subscribeToTopic("enviaratodos2").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(IngresarMiembro.this, "Suscrito a enviar a todos", Toast.LENGTH_SHORT).show();
            }
        });


        Button btnIngresar = findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //verifica si hay conexion a internet
                if(estadoConect()==true){
                    final String nombre = etNombre.getText().toString().trim();
                    final String apellido = etApellido.getText().toString().trim();
                    final String sociedad = spinner.getSelectedItem().toString();
                    final String imagen = convertirImgString(bitmap);

                    final ProgressDialog progDiag = new ProgressDialog(IngresarMiembro.this);
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

                                    Toast.makeText(IngresarMiembro.this, "Registro realizado con exito",Toast.LENGTH_LONG).show();
                                    Intent bienvenido = new Intent(IngresarMiembro.this, Bienvenido.class);
                                    IngresarMiembro.this.startActivity(bienvenido);
                                    IngresarMiembro.this.finish();
                                }else{
                                    progDiag.dismiss();

                                    AlertDialog.Builder alerta = new AlertDialog.Builder(IngresarMiembro.this);
                                    alerta.setMessage("No se pudo hacer el registro")
                                            .setNegativeButton("OK", null).create().show();
                                }
                            }catch (JSONException e){
                                e.getMessage();
                            }
                        }
                    };
                    RegistroMiembroRequest regist = new RegistroMiembroRequest(nombre, apellido, sociedad, imagen, usuario, respuesta);
                    RequestQueue cola = Volley.newRequestQueue(IngresarMiembro.this);
                    cola.add(regist);
                }else{
                    AlertDialog.Builder alerta = new AlertDialog.Builder(IngresarMiembro.this);
                    alerta.setMessage("Sin conexion a internet, por favor verifica la conexion e intenta de nuevo")
                            .setNegativeButton("Reintentar", null).create().show();
                }

            }
        });

    }

    private void llamarTodos() {
        RequestQueue myRequest = Volley.newRequestQueue(getApplicationContext());
        JSONObject json = new JSONObject();

        // String token = "";

        try {
            json.put("to", "/topics/enviaratodos2");
            JSONObject notificacion = new JSONObject();
            notificacion.put("Titulo", "Nueva Notificacion");
            notificacion.put("Detalle", "Has recibido una nueva notificacion desde nuestra aplicacion, gracias por usarla. Te invitamos a suscribirte. Has recibido una nueva notificacion desde nuestra aplicacion, gracias por usarla. Te invitamos a suscribirte.");
            json.put("data", notificacion);
            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, json, null, null){
                @Override
                public Map<String, String> getHeaders()  {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAABT2Y8TU:APA91bEjsWXsLKv_Ym6q9j7or5lg5I0XC47gIseSIXBQu0-5s-gUt8CUnW_w2Px8CPWBqTphDG0aTtP_gaVfLXcSnzF3sjW30g5q1Ae9tMYMPXCd0mJOHUVRIFMrZxvRTfMrwbWGVcHn");
                    return header;
                }
            };
            myRequest.add(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String convertirImgString(Bitmap bitmap){

        ByteArrayOutputStream array = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, array);
        byte[] imagenByte = array.toByteArray();
        String imagenString = Base64.encodeToString(imagenByte, Base64.DEFAULT );

        return imagenString;
    }

    public void cargarImagen(){

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Seleccione la aplicación"),10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Uri path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);

                int width  = bitmap.getWidth();
                int height = bitmap.getHeight();
                int newWidth = (height > width) ? width : height;
                int newHeight = (height > width)? height - ( height - width) : height;
                int cropW = (width - height) / 2;
                cropW = (cropW < 0)? 0: cropW;
                int cropH = (height - width) / 2;
                cropH = (cropH < 0)? 0: cropH;
                Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
                RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(getResources(), cropImg);
                dr.setCircular(true);
                foto.setImageDrawable(dr);

            } catch (IOException e) {
                e.printStackTrace();
            }
            /*
            Bitmap src = BitmapFactory.decodeResource(getResources(), R.id.idImagen);

            */


            /*
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
                roundedBitmapDrawable.setCornerRadius(1500);
                foto.setImageDrawable(roundedBitmapDrawable);
            */


        }
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

    public void onclick(View view) {
        cargarImagen();
    }
}
