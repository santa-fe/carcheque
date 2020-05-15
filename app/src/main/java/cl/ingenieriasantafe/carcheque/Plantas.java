package cl.ingenieriasantafe.carcheque;

public class Plantas {

    String id;
    String nombre;
    String obra;

    public Plantas(String id, String nombre, String planta) {
        this.id = id;
        this.nombre = nombre;
        this.obra = planta;
    }

    public Plantas() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPlanta() {
        return obra;
    }

    public void setPlanta(String planta) {
        this.obra = planta;
    }
}
