import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class Main extends JFrame {

    private String numeroSecreto;
    private List<String> historial = new ArrayList<>();
    private int intentos = 0;

    private JTextField txtEntrada;
    private JTextArea txtHistorial;
    private JButton btnValidar;


    public Main() {
        super("Code Braker");

        // Generamos el número secreto al iniciar el juego
        this.numeroSecreto = generarNumeroSecreto();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(650, 600);

        int AnchoIzquierdo = 250;
        int AnchoDerecho = 400;

        // Panel de juego
        JPanel PanelDerecho = new JPanel(new BorderLayout());

        JPanel PanelArriba = new JPanel();
        PanelArriba.add(new JLabel("Intenta adivinar"), BorderLayout.NORTH);

        txtEntrada = new JTextField(15);
        PanelArriba.add(txtEntrada);

        btnValidar = new JButton("Validar");
        PanelArriba.add(btnValidar, BorderLayout.SOUTH);

        Dimension DimUp = new Dimension(AnchoDerecho, 200);
        PanelArriba.setPreferredSize(DimUp);
        PanelArriba.setMinimumSize(DimUp);
        PanelArriba.setMaximumSize(DimUp);

        JPanel PanelAbajo = new JPanel(new BorderLayout());
        Dimension DimDown = new Dimension(AnchoDerecho, 400);
        PanelAbajo.setPreferredSize(DimDown);
        PanelAbajo.setMinimumSize(DimDown);
        PanelAbajo.setMaximumSize(DimDown);

        JTextArea Instrucciones = new JTextArea();
        Instrucciones.setText(
                "Pasos y Consejos para jugar CODE BRAKER:\n\n" +
                        "El codigo se compone por un conjunto de 4 numeros distintos.\n" +
                        "1.- Debes ingresar un conjunto de 4 numeros para adivinar el codigo oculto.\n" +
                        "2.- No puedes repetir números iguales al intentar adivinar el codigo (ej. 151 o 232).\n" +
                        "3.- Al acertar un número y su posición se mostrará un: * (asterisco). No se te dirá cuál es, solo que está bien alguno de los numeros \n" +
                        "Si aciertas el número pero no la posición se indicará con: - (guión).");

        Instrucciones.setLineWrap(true);
        Instrucciones.setWrapStyleWord(true);
        Instrucciones.setEditable(false);
        Instrucciones.setOpaque(false);
        Instrucciones.setFont(Instrucciones.getFont().deriveFont(14f));

        PanelAbajo.add(new JScrollPane(Instrucciones), BorderLayout.CENTER);
        PanelAbajo.add(Instrucciones, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, PanelArriba, PanelAbajo);

        PanelDerecho.setBackground(Color.WHITE);
        Dimension DimDer = new Dimension(AnchoDerecho, 0);
        PanelDerecho.setPreferredSize(DimDer);
        PanelDerecho.setMinimumSize(DimDer);
        PanelDerecho.setMaximumSize(DimDer);
        PanelDerecho.add(split);

        // Panel de intentos previos
        JPanel PanelIzquierdo = new JPanel(new BorderLayout());
        JLabel a = new JLabel("Intentos previos");

        PanelIzquierdo.setBackground(Color.LIGHT_GRAY);
        Dimension DimIzq = new Dimension(AnchoIzquierdo, 0);
        PanelIzquierdo.setPreferredSize(DimIzq);
        PanelIzquierdo.setMinimumSize(DimIzq);
        PanelIzquierdo.setMaximumSize(DimIzq);
        PanelIzquierdo.add(a, BorderLayout.NORTH);

        txtHistorial = new JTextArea();
        txtHistorial.setEditable(false);
        txtHistorial.setLineWrap(true);
        txtHistorial.setWrapStyleWord(true);
        PanelIzquierdo.add(new JScrollPane(txtHistorial), BorderLayout.CENTER);

        JSplitPane split1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, PanelIzquierdo, PanelDerecho);

        // Agregamos los paneles del juego al espacio principal (padre)
        add(split1);
        setLocationRelativeTo(null);

        // Listener del botón Validar (al final, cuando ya existen todos los campos)
        btnValidar.addActionListener(e -> {
            String entrada = txtEntrada.getText().trim();

            if (!esEntradaValida(entrada)) {
                return;
            }

            intentos++;
            String feedback = calcularRetroalimentacion(entrada);

            // Guardar el intento con su retroalimentación
            String registro = "Intento " + intentos + ": " + entrada + "  ->  " + feedback;
            historial.add(registro);

            // Mostrar en el historial
            txtHistorial.append(registro + "\n");

            txtEntrada.setText("");
            txtEntrada.requestFocus();

            // Verificar si ganó
            if (entrada.equals(numeroSecreto)) {
                JOptionPane.showMessageDialog(this,
                        "¡Felicidades! Adivinaste el número en " + intentos + " intentos.");
                btnValidar.setEnabled(false); // o reiniciar el juego
            }
        });

        setVisible(true);
        this.numeroSecreto = generarNumeroSecreto();
        System.out.println("DEBUG - Secreto: " + numeroSecreto); // quitar después
    }


    private String generarNumeroSecreto() {
        List<Integer> digitos = new ArrayList<>();
        for (int i = 0; i <= 9; i++) digitos.add(i);
        Collections.shuffle(digitos);
        if (digitos.get(0) == 0) Collections.swap(digitos, 0, 1);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 4; i++) sb.append(digitos.get(i));
        return sb.toString();
    }

    private boolean esEntradaValida(String entrada) {
        if (entrada.length() != 4) {
            JOptionPane.showMessageDialog(this, "Debes ingresar exactamente 4 dígitos.");
            return false;
        }
        if (!entrada.matches("\\d{4}")) {
            JOptionPane.showMessageDialog(this, "Solo se permiten números.");
            return false;
        }
        // Verificar que no haya dígitos repetidos
        Set<Character> unicos = new HashSet<>();
        for (char c : entrada.toCharArray()) {
            if (!unicos.add(c)) {
                JOptionPane.showMessageDialog(this, "No se permiten dígitos repetidos.");
                return false;
            }
        }
        return true;
    }

    private String calcularRetroalimentacion(String intento) {
        int aciertosPosicion = 0; // cuántos "*"
        int aciertosNumero = 0;   // cuántos "-"

        for (int i = 0; i < 4; i++) {
            char c = intento.charAt(i);
            if (c == numeroSecreto.charAt(i)) {
                aciertosPosicion++; // número y posición correctos
            } else if (numeroSecreto.indexOf(c) != -1) {
                aciertosNumero++; // número correcto, posición incorrecta
            }
        }

        // Primero se muestran todos los "*" y después todos los "-"
        StringBuilder resultado = new StringBuilder();
        for (int i = 0; i < aciertosPosicion; i++) resultado.append(" * ");
        for (int i = 0; i < aciertosNumero; i++) resultado.append(" - ");

        return resultado.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}