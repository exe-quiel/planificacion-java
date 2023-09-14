package ar.edu.unlu.sisop.planificacion;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import ar.edu.unlu.sisop.planificacion.Main.Proceso;
import ar.edu.unlu.sisop.planificacion.Main.Resultado;

public class RoundRobin implements Scheduler {

    private boolean LOGS = false;
    private boolean STEP = false;
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
        int reloj = 0;
        Proceso pAnterior = null;
        List<Resultado> resultados = new LinkedList<>();

        int sumaTs = calcularSumaTiempoServicio(procesos);
        while (sumaTs > 0) {
            // System.out.println("SUMA: %d\n", sumaTs);
            for (int iProceso = 0; iProceso < procesos.size(); iProceso++) {
            //while (pActual != null) {
                Proceso pActual = procesos.get(iProceso);
                if (pActual.getTs() > 0) {
                    if (pAnterior != null && pActual != pAnterior) {
                        log("Quito proceso %d\n", pAnterior.getPid());
                        reloj += TI / 2;
                    }

                    if (pActual.getTs() > QUANTUM) {
                        if (pActual != pAnterior) {
                            log("Inserto proceso %d\n", pActual.getPid());
                            reloj += TI / 2;
                        }

                        log("Ejecuto proceso %d\n", pActual.getPid());
                        pActual.setTs(pActual.getTs() - QUANTUM);
                        reloj += QUANTUM;
                        sumaTs -= QUANTUM;
                    } else {
                        if (pActual != pAnterior) {
                            log("Inserto proceso %d\n", pActual.getPid());
                            reloj += TI / 2;
                        }

                        log("Ejecuto proceso %d\n", pActual.getPid());
                        reloj += pActual.getTs();
                        sumaTs -= pActual.getTs();
                        pActual.setTs(0);
                    }

                    resultados.add(new Resultado(pActual.getPid(), reloj));
                }
                pAnterior = pActual;
                step();
            }
            // sumaTs = calcularSumaTiempoServicio(pHead);
        }
        return resultados;
    }

    private void log(String template, Object... params) {
        if (LOGS) {
            System.out.printf(template, params);
        }
    }

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

    private int calcularSumaTiempoServicio(List<Proceso> procesos) {
        return procesos.stream().mapToInt(Proceso::getTs).sum();
    }
}
