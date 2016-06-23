package operacionais.sistemas.ui;

import java.util.Collection;

import operacionais.sistemas.SO;
import operacionais.sistemas.memorias.RAM;
import operacionais.sistemas.memorias.Virtual;
import operacionais.sistemas.memorias.Virtual.Pagina;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public final class Memorias extends Application {
	
	private static Memorias INSTANCIA;
	
	public static Memorias instancia() {
		return Memorias.INSTANCIA;
	}
	
	public static void main(String[] args) { 
		Application.launch(args);
	}

	private Stage window;
	
	public Memorias() {
		Memorias.INSTANCIA = this;
	}
	
	@Override
	public void start(Stage window) throws Exception {
		this.window = window;
		this.window.setTitle("UPE - Simulador de Paginação MRU [2016.1]");
		this.window.show();
		
		SO.instancia().preparar();
		this.atualizarTela();
	}
	
	private void atualizarTela() {
		Label labelMemVirtual = new Label("Memória Virtual");
		labelMemVirtual.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		
		Label labelMemRAM = new Label("Memória RAM");
		labelMemRAM.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		
		Label labelMemSWAP = new Label("Memória SWAP");
		labelMemSWAP.setFont(Font.font("Arial", FontWeight.BOLD, 12));
		
		Button atualizar = new Button("Atualizar");
		atualizar.setOnAction(e -> this.atualizarTela());

		FlowPane memoriaVirtual = this.prepararDisplayVirtual();
		FlowPane memoriaRAM = this.prepararDisplayMemoria(Virtual.LOCAL_RAM);
		FlowPane memoriaSWAP = this.prepararDisplayMemoria(Virtual.LOCAL_DISCO);
		
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(15, 10, 15, 10));
		layout.getChildren().addAll(atualizar, labelMemVirtual, memoriaVirtual, labelMemRAM, memoriaRAM, labelMemSWAP, memoriaSWAP);
		
		Scene scene = new Scene(layout, 800, 400);
		this.window.setScene(scene);
	}
	
	private FlowPane prepararDisplayVirtual() {
		FlowPane fp = new FlowPane();
		fp.setPadding(new Insets(5, 0, 5, 0));
		fp.setVgap(RAM.CAPACIDADE);
	    fp.setHgap(RAM.CAPACIDADE);
		
		Collection<Pagina> paginas = Virtual.instancia().getPaginas();
		for (Pagina pagina : paginas) {
			ImageView img = null;
			if ((pagina.isPresente())) {
				img = new ImageView(new Image(this.getClass().getResourceAsStream("img/ram.png")));
			} else if ((pagina.getEndereco() < 0)) {
				img = new ImageView(new Image(this.getClass().getResourceAsStream("img/sem-uso.png")));
			} else {
				img = new ImageView(new Image(this.getClass().getResourceAsStream("img/disco.png")));
			}
			
			Label status = new Label((pagina.isPresente() ? " [R] [" : " [D] [") + pagina.getEndereco() + "]");
			status.setGraphic(img);
			status.setMinWidth(60);
			status.setMaxWidth(60);
			
			fp.getChildren().add(status);
		}
		
		return fp;
	}
	
	private FlowPane prepararDisplayMemoria(int local) {
		FlowPane fp = new FlowPane();
		fp.setPadding(new Insets(5, 0, 5, 0));
		fp.setVgap(RAM.CAPACIDADE);
	    fp.setHgap(RAM.CAPACIDADE);
		
		for (int endereco = 0; (endereco < RAM.CAPACIDADE); endereco++) {
			Integer valorLido = Virtual.instancia().ler(endereco, local);
			ImageView img = null;
			Label status = null;
			
			if ((valorLido == null)) {
				status = new Label("-1");
				img = new ImageView(new Image(this.getClass().getResourceAsStream("img/sem-uso.png")));
			} else {
				status = new Label(valorLido.toString());
				img = new ImageView(new Image(this.getClass().getResourceAsStream("img/ram.png")));
			}
			
			status.setGraphic(img);
			status.setMinWidth(30);
			status.setMaxWidth(30);
			
			fp.getChildren().add(status);
		}
		
		return fp;
	}

}
