package ar.edu.unlu.sisop.planificacion;

import java.util.List;

import ar.edu.unlu.sisop.planificacion.Main.Proceso;
import ar.edu.unlu.sisop.planificacion.Main.Resultado;

public interface Scheduler {

    public List<Resultado> procesar(List<Proceso> procesos);
}
