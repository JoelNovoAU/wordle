package com.example.wordle;

import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.*;

public class AbecedarioController implements Initializable {

    @FXML private HBox fila1, fila2, fila3;
    @FXML private Label lblPuntaje;
    @FXML private TextField txtIntento;
    @FXML private Button btnProbar;
    @FXML private GridPane gridIntentos;
    @FXML private Label lblAciertos;
    @FXML private Label lblFallos;
    @FXML private Rectangle alertOverlay;


    @FXML private StackPane alertPopup;
    @FXML private Label lblAlertMensaje;



    private List<String> palabras = List.of(
            "miedo","zombi","matar","negro","grime","sasha","glenn",
            "daryl","carol","moral","virus","sangre","tumba","heroe",
            "arena","radio","rifle","grupo","ruina","civil","torre",
            "carga","mando","rango","vivos"
    );

    private String palabraObjetivo;
    private int palabrasAcertadas = 0;
    private int palabrasFalladas = 0;
    private int filaActual = 0;
    private static final int MAX_INTENTOS = 5;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        iniciarNuevaPartida();
        btnProbar.setOnAction(e -> verificarIntento());
        alertPopup.setVisible(false);
        alertOverlay.widthProperty().bind(rootPane.widthProperty());
        alertOverlay.heightProperty().bind(rootPane.heightProperty());
    }

    private void crearFila(HBox fila, String letras) {
        for (char c : letras.toCharArray()) {
            Label label = new Label(String.valueOf(c));
            label.setStyle("-fx-min-width: 40; -fx-min-height: 40; -fx-background-color: white; "
                    + "-fx-border-color: #ccc; -fx-alignment: center; -fx-font-size: 18; -fx-font-weight: bold;");
            DropShadow shadow = new DropShadow();
            shadow.setColor(Color.GRAY);
            shadow.setRadius(3);
            label.setEffect(shadow);
            fila.getChildren().add(label);
        }
    }

    private void iniciarNuevaPartida() {
        gridIntentos.getChildren().clear();
        txtIntento.clear();
        filaActual = 0;

        palabraObjetivo = palabras.get(new Random().nextInt(palabras.size())).toUpperCase();
        crearFilasDeIntentos(palabraObjetivo.length());
        reiniciarColoresAbecedario();
    }

    private void crearFilasDeIntentos(int largo) {
        for (int i = 0; i < MAX_INTENTOS; i++) {
            for (int j = 0; j < largo; j++) {
                Label l = new Label("");
                l.setStyle("-fx-pref-width: 50; -fx-pref-height: 50; "
                        + "-fx-min-width: 50; -fx-min-height: 50; "
                        + "-fx-max-width: 50; -fx-max-height: 50; "
                        + "-fx-background-color: white; "
                        + "-fx-border-color: #bbb; -fx-border-radius: 5; -fx-background-radius: 5; "
                        + "-fx-alignment: center; -fx-font-size: 20; -fx-font-weight: bold;");
                gridIntentos.add(l, j, i);
            }
        }
    }

    private void verificarIntento() {
        String intento = txtIntento.getText().toUpperCase().trim();
        if (intento.length() != palabraObjetivo.length()) {
            alert("Debe tener " + palabraObjetivo.length() + " letras.");
            return;
        }

        List<Node> celdasFila = new ArrayList<>();
        for (Node n : gridIntentos.getChildren()) {
            if (GridPane.getRowIndex(n) == filaActual) celdasFila.add(n);
        }
        celdasFila.sort(Comparator.comparingInt(n -> GridPane.getColumnIndex(n)));

        char[] letrasPalabra = palabraObjetivo.toCharArray();
        char[] letrasIntento = intento.toCharArray();

        for (int i = 0; i < letrasIntento.length; i++) {
            Label celda = (Label) celdasFila.get(i);
            String letra = String.valueOf(letrasIntento[i]);

            if (letrasIntento[i] == letrasPalabra[i]) {
                celda.setStyle("-fx-pref-width: 50; -fx-pref-height: 50; "
                        + "-fx-background-color: #6aaa64; -fx-text-fill: white; "
                        + "-fx-border-color: #bbb; -fx-border-radius: 5; -fx-background-radius: 5; "
                        + "-fx-alignment: center; -fx-font-size: 20; -fx-font-weight: bold;");
                pintarLetraAbecedario(letra, "#6aaa64");
            } else {
                celda.setStyle("-fx-pref-width: 50; -fx-pref-height: 50; "
                        + "-fx-background-color: #d32f2f; -fx-text-fill: white; "
                        + "-fx-border-color: #bbb; -fx-border-radius: 5; -fx-background-radius: 5; "
                        + "-fx-alignment: center; -fx-font-size: 20; -fx-font-weight: bold;");
                pintarLetraAbecedario(letra, "#d32f2f");
            }

            celda.setText(letra);
        }

        if (intento.equals(palabraObjetivo)) {
            palabrasAcertadas++;
            actualizarMarcador();
            alert("¡Correcto! Era " + palabraObjetivo + " 🎉");
            iniciarNuevaPartida();
        } else {
            filaActual++;
            if (filaActual >= MAX_INTENTOS) {
                palabrasFalladas++;
                actualizarMarcador();
                alert("Perdiste 😔 La palabra era: " + palabraObjetivo);
                iniciarNuevaPartida();
            }
        }

        txtIntento.clear();
    }

    private void actualizarMarcador() {
        lblAciertos.setText("✅ Aciertos: " + palabrasAcertadas);
        lblFallos.setText("❌ Fallos: " + palabrasFalladas);
    }

    private void pintarLetraAbecedario(String letra, String colorHex) {
        List<HBox> filas = List.of(fila1, fila2, fila3);
        for (HBox fila : filas) {
            for (Node n : fila.getChildren()) {
                Label l = (Label) n;
                if (l.getText().equalsIgnoreCase(letra)) {
                    String base = l.getStyle();
                    l.setStyle(base + "; -fx-background-color: " + colorHex + "; -fx-text-fill: white;");
                }
            }
        }
    }

    private void reiniciarColoresAbecedario() {
        List<HBox> filas = List.of(fila1, fila2, fila3);
        for (HBox fila : filas) {
            for (Node n : fila.getChildren()) {
                Label l = (Label) n;
                l.setStyle("-fx-min-width: 45; -fx-min-height: 45; "
                        + "-fx-background-color: #444; "
                        + "-fx-border-color: #d32f2f; "
                        + "-fx-border-radius: 5; -fx-background-radius: 5; "
                        + "-fx-alignment: center; "
                        + "-fx-font-size: 18; -fx-font-weight: bold; "
                        + "-fx-text-fill: white; "
                        + "-fx-effect: dropshadow(gaussian, #222, 3, 0.5, 0, 0);");
            }
        }
    }


    private void alert(String mensaje) {
        lblAlertMensaje.setText(mensaje);
        alertOverlay.setVisible(true);
        alertPopup.setVisible(true);
    }


    @FXML
    private StackPane rootPane;


    @FXML
    private void cerrarAlert() {
        alertPopup.setVisible(false);
        alertOverlay.setVisible(false);
    }

}
