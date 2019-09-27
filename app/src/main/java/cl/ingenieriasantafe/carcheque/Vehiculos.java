package cl.ingenieriasantafe.carcheque;

public class Vehiculos {

    String id;
    String patente;
    String estado;
    String propietario;
    String marca;
    String m3;

    public Vehiculos() {

    }

    public Vehiculos(String id, String patente, String estado, String propietario, String marca, String m3) {
        this.id = id;
        this.patente = patente;
        this.estado = estado;
        this.propietario = propietario;
        this.marca = marca;
        this.m3 = m3;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getM3() {
        return m3;
    }

    public void setM3(String m3) {
        this.m3 = m3;
    }
}
