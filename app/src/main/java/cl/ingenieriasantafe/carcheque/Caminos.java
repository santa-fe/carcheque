package cl.ingenieriasantafe.carcheque;

public class Caminos {

    String id;
    String obra;
    String nombre;
    String desde;
    String hasta;

    public Caminos(String id, String obra, String nombre, String desde, String hasta) {
        this.id = id;
        this.obra = obra;
        this.nombre = nombre;
        this.desde = desde;
        this.hasta = hasta;
    }

    public Caminos() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObra() {
        return obra;
    }

    public void setObra(String obra) {
        this.obra = obra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public String getHasta() {
        return hasta;
    }

    public void setHasta(String hasta) {
        this.hasta = hasta;
    }
}
