package com.example.miembrosiglesia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegistroMiembroRequest extends StringRequest {
    private static final String ruta = "https://jairolistausuarios.000webhostapp.com/registroMiembro.php";
    private Map<String, String> parametros;

    public RegistroMiembroRequest(String nombre, String apellido, String sociedad, String imagen, String usuario , Response.Listener<String> listener){
        super(Request.Method.POST, ruta, listener, null);
        parametros = new HashMap<>();
        parametros.put("nombre", nombre+"");
        parametros.put("apellido", apellido+"");
        parametros.put("sociedad", sociedad+"");
        parametros.put("imagen", imagen+"");
        parametros.put("usuario", usuario+"");
    }
    @Override
    protected Map<String, String> getParams() {
        return parametros;
    }

}
