package cl.ingenieriasantafe.carcheque;

public class Choferes {

    String id;
    String nombre;
    String apellido;

    public Choferes() {

    }

    public Choferes(String id, String nombre, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
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

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombreApellido(){
        return nombre+" "+apellido;
    }
}
