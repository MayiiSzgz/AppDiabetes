package com.example.appdiabetes;

import java.util.Date;

public class Glucemia {
    private double valor;
    private String unidad;
    private Date fechaHora;
    private String estado;

    public Glucemia(double valor, String unidad, Date fechaHora, String estado) {
        this.valor = valor;
        this.unidad = unidad;
        this.fechaHora = fechaHora;
        this.estado = estado;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Date getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(Date fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String analizarNivelGlucemia() {
        if (unidad.equals("mg/dL")) {
            if (valor < 70) {
                return "Bajo";
            } else if (valor >= 70 && valor <= 180) {
                return "Normal";
            } else {
                return "Alto";
            }
        } else if (unidad.equals("mmol/L")) {
            if (valor < 3.9) {
                return "Bajo";
            } else if (valor >= 3.9 && valor <= 10.0) {
                return "Normal";
            } else {
                return "Alto";
            }
        } else {
            return "Unidad de medida no válida";
        }
    }
}
