package com.upiiz.dm_jphl_practica02.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class ApiErrorResponse {

    @SerializedName("status")
    private int status;

    @SerializedName("error")
    private String error;

    @SerializedName("mensajes")
    private Map<String, String> mensajes;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Map<String, String> getMensajes() {
        return mensajes;
    }

    public void setMensajes(Map<String, String> mensajes) {
        this.mensajes = mensajes;
    }

    public String getMensajeDetalle() {
        if (mensajes != null && mensajes.containsKey("mensaje")) {
            return mensajes.get("mensaje");
        }
        if (error != null && !error.isEmpty()) {
            return error;
        }
        return "No fue posible procesar la solicitud";
    }
}
