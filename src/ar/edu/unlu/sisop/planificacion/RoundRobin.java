package ar.edu.unlu.sisop.planificacion;

import static ar.edu.unlu.sisop.planificacion.ANSIColors.GREEN;
import static ar.edu.unlu.sisop.planificacion.ANSIColors.RESET;

import java.util.LinkedList;
import java.util.List;

import ar.edu.unlu.sisop.planificacion.Main.Proceso;
import ar.edu.unlu.sisop.planificacion.Main.Resultado;

public class RoundRobin implements Scheduler {

    private boolean LOGS = true;
    private boolean STEP = false; // Para 
    private int QUANTUM = 4;
    private int TI = QUANTUM / 4;

    public RoundRobin() {
        
    }

    public RoundRobin(boolean logs, boolean step) {
        this.LOGS = logs;
        this.STEP = step;
    }

    public RoundRobin(boolean logs, boolean step, int quantum, int ti) {
        this.LOGS = logs;
        this.STEP = step;
        this.QUANTUM = quantum;
        this.TI = ti;
    }

    @Override
    public List<Resultado> procesar(List<Proceso> procesos) {
        log(" ----------\n");
        log("|" + GREEN + "QUANTUM" + RESET + ": %d|\n", this.QUANTUM);
        log("|" + GREEN + "TI" + RESET + ": %d     |\n", this.TI);
        log(" ----------\n");
        int reloj = 0;
        // pAnterior es el último proceso que se alocó y ejecutó
        // ("último" en el sentido de "más reciente")
        Proceso pAnterior = null;
        List<Resultado> resultados = new LinkedList<>();

        int sumaTs = calcularSumaTiempoServicio(procesos);
        while (sumaTs > 0) {
            // iProceso es el índice que uso para iterar sobre todos los procesos de la lista
            for (int iProceso = 0; iProceso < procesos.size(); iProceso++) {
                // pActual es el proceso que leo de la lista en esta iteración.
                // IMPORTANTE: No implica que el proceso se aloque y ejecute en esta iteración
                // Simplemente estoy guardando una referencia al proceso que está en ListaProcesos(indiceProceso)
                // en una variable por comodidad
                Proceso pActual = procesos.get(iProceso);
                if (pActual.getTs() > 0) { // Acá determinamos si el proceso se va a alocar
                    if (pAnterior != null && pActual != pAnterior) {
                        log("Quito proceso %d (TS restante = %d)\n", pAnterior.getPid(), pAnterior.getTs());
                        reloj += TI / 2;
                    }

                    if (pActual != pAnterior) {
                        log("Inserto proceso %d (TS = %d)\n", pActual.getPid(), pActual.getTs());
                        reloj += TI / 2;
                    }

                    if (pActual.getTs() > QUANTUM) {
                        pActual.setTs(pActual.getTs() - QUANTUM);
                        reloj += QUANTUM;
                        sumaTs -= QUANTUM;
                    } else {
                        reloj += pActual.getTs();
                        sumaTs -= pActual.getTs();
                        pActual.setTs(0);
                    }

                    log("Ejecuto proceso %d\n", pActual.getPid());

                    resultados.add(new Resultado(pActual.getPid(), reloj));
                    pAnterior = pActual;
                } else {
                    log(GREEN + "No se alocó el proceso %d porque su ts es cero\n", pActual.getPid()); 
                }
                log(GREEN + "Fin ronda - pAnterior: %d pActual %d\n" + RESET, pAnterior == null ? null : pAnterior.getPid(), pActual.getPid());
                //step();
            }
            // No hace falta volver a calcularla si voy restándolo en cada iteración
            // sumaTs = calcularSumaTiempoServicio(pHead);
        }
        return resultados;
    }

    private void log(String template, Object... params) {
        if (LOGS) {
            System.out.printf(template, params);
        }
    }

    /*
    private void step() {
        if (STEP) {
            int c = 13;
            try {
                c = System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (c != 13) { // Esperar teclar ENTER
                STEP = false;
            }
            System.out.printf("%d", c);
        }
    }
    */

    private int calcularSumaTiempoServicio(List<Proceso> procesos) {
        return procesos.stream().mapToInt(Proceso::getTs).sum();
    }
}
