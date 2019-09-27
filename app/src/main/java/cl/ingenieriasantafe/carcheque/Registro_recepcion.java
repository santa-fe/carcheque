package cl.ingenieriasantafe.carcheque;

public class Registro_recepcion {

    int id = 0;
    String planta;
    String patente;
    String tipomaterial;
    String m3;
    String km;
    String obra;
    String camino;
    String fecha;
    String hora;
    String username;
    String chofer;
    String estado;


    public Registro_recepcion() {

    }

    public Registro_recepcion(int id, String planta, String patente, String tipomaterial, String m3, String km, String obra, String camino, String fecha, String hora, String username, String chofer, String estado) {
        this.id = id;
        this.planta = planta;
        this.patente = patente;
        this.tipomaterial = tipomaterial;
        this.m3 = m3;
        this.km = km;
        this.obra = obra;
        this.camino = camino;
        this.fecha = fecha;
        this.hora = hora;
        this.username = username;
        this.chofer = chofer;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanta() {
        return planta;
    }

    public void setPlanta(String planta) {
        this.planta = planta;
    }

    public String getPatente() {
        return patente;
    }

    public void setPatente(String patente) {
        this.patente = patente;
    }

    public String getTipomaterial() {
        return tipomaterial;
    }

    public void setTipomaterial(String tipomaterial) {
        this.tipomaterial = tipomaterial;
    }

    public String getM3() {
        return m3;
    }

    public void setM3(String m3) {
        this.m3 = m3;
    }

    public String getKm() {
        return km;
    }

    public void setKm(String km) {
        this.km = km;
    }

    public String getObra() {
        return obra;
    }

    public void setObra(String obra) {
        this.obra = obra;
    }

    public String getCamino() {
        return camino;
    }

    public void setCamino(String camino) {
        this.camino = camino;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getChofer() {
        return chofer;
    }

    public void setChofer(String chofer) {
        this.chofer = chofer;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}