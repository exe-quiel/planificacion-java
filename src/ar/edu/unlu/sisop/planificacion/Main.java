package ar.edu.unlu.sisop.planificacion;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Main {

    private static final String RESET = "\u001B[0m";
    private static final String BLACK = "\u001B[30m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE = "\u001B[37m";

    private static List<Proceso> crearProcesos() {
        List<Proceso> procesos = new LinkedList<>();
        for (int pid = 1; pid <= 10; pid++) {
            int ts = ThreadLocalRandom.current().nextInt(1, 11);
            procesos.add(new Proceso(pid, ts));
        }
        return procesos;
    }

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            List<Proceso> procesos = crearProcesos();
            imprimirProcesos(procesos);
            // Necesitamos una copia porque RoundRobin.procesar() modifica el tiempo de servicio de los procesos
            List<Proceso> procesosCopia = procesos.stream().map(Proceso::clonar).collect(Collectors.toList());
            List<Resultado> resultados = new RoundRobin().procesar(procesosCopia);
            imprimirResultados(resultados);
            calcularPromedios(resultados, procesos);
        }
    }

    private static void imprimirProcesos(List<Proceso> procesos) {
        System.out.println(" ------PROCESOS------");
        procesos.forEach(proceso -> {
            final int pid = proceso.getPid();
            final int ts = proceso.getTs();
            System.out.printf("| " + GREEN  + "PID" + RESET + ": %3d | " + GREEN + "TS" + RESET + ": %3d |\n", pid, ts);
        });
        System.out.println(" --------------------");
    }

    private static void imprimirResultados(List<Resultado> resultados) {
        System.out.println(" -----RESULTADOS-----");
        resultados.forEach(resultado -> {
            final int pid = resultado.getPid();
            final int reloj = resultado.getReloj();
            System.out.printf("| " + GREEN  + "PID" + RESET + ": %3d | " + GREEN + "TS" + RESET + ": %3d |\n", pid, reloj);
        });
        System.out.println(" --------------------");
    }

    private static void calcularPromedios(List<Resultado> resultados, List<Proceso> procesos) {
        Set<Integer> pids = new HashSet<>();
        ListIterator<Resultado> listIterator = resultados.listIterator(resultados.size());
        int accRetorno = 0;
        int accEspera = 0;
        while (listIterator.hasPrevious() && pids.size() < resultados.size()) {
            Resultado resultado = listIterator.previous();
            if (!pids.contains(resultado.getPid())) {
                Proceso proceso = obtenerProceso(procesos, resultado.getPid());
                accRetorno += resultado.getReloj();
                accEspera += resultado.getReloj() - proceso.getTs();
                pids.add(resultado.getPid());
            }
        }
        System.out.printf("Tiempo promedio de retorno: %3.2f\n", (float) accRetorno / resultados.size());
        System.out.printf("Tiempo promedio de espera: %3.2f\n", (float) accEspera / resultados.size());
    }

    private static Proceso obtenerProceso(List<Proceso> procesos, int pid) {
        return procesos.stream().filter(proceso -> proceso.getPid() == pid).findFirst().get();
    }

    public static class Proceso {
        private int pid;
        private int ts;

        public Proceso(int pid, int ts) {
            this.pid = pid;
            this.ts = ts;
        }

        public int getPid() {
            return pid;
        }

        public int getTs() {
            return ts;
        }

        public void setTs(int ts) {
            this.ts = ts;
        }

        public Proceso clonar() {
            return new Proceso(pid, ts);
        }
    }

    public static class Resultado {
        private int pid;
        private int reloj;

        public Resultado(int pid, int reloj) {
            this.pid = pid;
            this.reloj = reloj;
        }

        public int getPid() {
            return pid;
        }

        public int getReloj() {
            return reloj;
        }
    }
}
