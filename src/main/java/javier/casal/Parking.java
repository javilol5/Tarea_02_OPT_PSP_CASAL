package javier.casal;

// Un parking tiene 3 plazas y llegan 8 coches.
// Cada coche entra si hay sitio, está aparcado un tiempo aleatorio y sale.
// Si el parking está lleno, espera a que salga otro.
//
// Muestra cada entrada y salida con las plazas libres que quedan

public class Parking {

    //Colorinchos para que cada coche tenga un color distinto
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String GRAY = "\u001B[37m";
    public static final String WHITE = "\u001B[38m";

    //array de colorinchos para que cada coche tenga un color distinto
    public static final String[] COLORES = {
            RED, GREEN, YELLOW, BLUE, MAGENTA, CYAN, GRAY, WHITE
    };

    /*
     * Clase que actúa como recurso compartido (Monitor).
     * Administra el número de plazas disponibles y sincroniza el acceso de los coches (hilos).
     */
    static class Aparcar {

        // Variable compartida que representa el número total de plazas disponibles.
        private int plazasLibres = 3;

        /*
         * Permite que un coche intente aparcar.
         * Si el parking está lleno, el hilo del coche se bloquea con wait() hasta que quede una plaza libre.
         */
        public synchronized void entrar(int coche, String color) throws InterruptedException {

            // Mientras no haya plazas, el coche espera.
            while (plazasLibres == 0) {
                System.out.println(color + "Coche " + coche + " espera. Parking lleno." + RESET);
                wait(); //pone los hilos a esperar (los duerme)
            }

            // Ocupa una plaza
            plazasLibres--;

            System.out.println(color + "Coche " + coche + " entra. Plazas libres: " + RESET + plazasLibres);
        }

        /*
         * Permite que un coche abandone el parking.
         * Incrementa el número de plazas libres y notifica a los hilos en espera mediante notifyAll().
         */
        public synchronized void salir(int coche, String color) {

            // Libera una plaza
            plazasLibres++;

            System.out.println(color + "Coche " + coche + " sale. Plazas libres: " + RESET + plazasLibres);

            //notifica a todos los hilos (los despierta)
            notifyAll();
        }
    }

    /**
     * Clase que representa la tarea de cada coche en un hilo independiente.
     * Implementa Runnable para definir el comportamiento concurrente.
     */
    static class Coche implements Runnable {

        private Aparcar aparcar; // Referencia al parking compartido
        private int numero;      // Número identificador del coche

        //Consturcor de coche
        public Coche(Aparcar aparcar, int numero) {
            this.aparcar = aparcar;
            this.numero = numero;
        }

        /**
         * Metodo principal
         * Intenta entrar al parking, simula estar aparcado y se va del parking.
         */
        @Override
        public void run() {

            try {
                //asigna un color a cada coche según su número
                String color = COLORES[numero-1 % COLORES.length];

                // Intenta entrar al parking
                aparcar.entrar(numero, color);

                // Simula el tiempo que el coche está aparcado (entre 1 y 5 segundos)
                int tiempo = (int) (Math.random() * 5001) + 1000;

                Thread.sleep(tiempo);

                // Sale del parking
                aparcar.salir(numero, color);

            } catch (InterruptedException e) {
                System.out.println("Coche " + numero + " interrumpido.");
                Thread.currentThread().interrupt(); //restablece la interrupcion de los hilos
            }
        }
    }

    public static void main(String[] args) {

        // Se crea el parking compartido con 3 plazas
        Aparcar aparcar = new Aparcar();

        // Se crean y arrancan 8 hilos (coches) de forma concurrente
        for (int i = 1; i <= 8; i++) {
            Coche coche = new Coche(aparcar, i);
            Thread hilo = new Thread(coche);
            hilo.start();
        }
    }
}