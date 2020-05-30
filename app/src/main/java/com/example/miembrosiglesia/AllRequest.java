package com.example.miembrosiglesia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class AllRequest extends StringRequest {
    private static final String ruta = "https://jairolistausuarios.000webhostapp.com/getAll.php";
    private Map<String, String> parametros;

    public AllRequest(Response.Listener<String> listener, Response.ErrorListener errorL){
        super(Request.Method.POST, ruta, listener, errorL);
        parametros = new HashMap<>();

    }
    @Override
    protected Map<String, String> getParams() {
        return parametros;
    }
}
