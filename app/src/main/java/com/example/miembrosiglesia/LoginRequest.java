package com.example.miembrosiglesia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest{
    private static final String ruta = "https://jairolistausuarios.000webhostapp.com/login.php";
    private Map<String, String> parametros;

    public LoginRequest(String nombre, String contrasenia, Response.Listener<String> listener, Response.ErrorListener errlistener){
        super(Request.Method.POST, ruta, listener, errlistener);
        parametros = new HashMap<>();
        parametros.put("nombre", nombre+"");
        parametros.put("contrasenia", contrasenia+"");
    }
    @Override
    protected Map<String, String> getParams() {
        return parametros;
    }

}
