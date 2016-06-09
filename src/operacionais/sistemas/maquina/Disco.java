package operacionais.sistemas.maquina;

import java.util.List;
import java.util.ArrayList;
import operacionais.sistemas.maquina.memoria.disco.SO;
import operacionais.sistemas.maquina.memoria.disco.SWAP;
import operacionais.sistemas.maquina.memoria.disco.Processo;

public final class Disco {

	public static final int PROCESSOS = 2; // Número de processos... vai servir para outras partes do código

	private SWAP swap; // Area realmente reservada para troca que terá o tamanho da RAM
	private SO sistema; // Para um acesso privilegeado o SO está numa area "separada" do disco
	private List<Processo> memoria; // Aqui seria o que está armazenado no disco

	protected Disco() {
		System.out.println("Checando o disco...");

		SO so = new SO();
		SWAP swap = new SWAP();
		List<Processo> memoria = new ArrayList<Processo>();

		this.setSistema(so);
		this.setSwap(swap);
		this.setMemoria(memoria);
	}

	public void ligar() {
		// Carregando os "programas" salvos em disco...
		for (int id = 1; (id <= Disco.PROCESSOS); ++id) {
			Processo processo = new Processo(id);
			this.getMemoria().add(processo);
		}
	}

	public SWAP getSwap() {
		return this.swap;
	}

	private void setSwap(SWAP swap) {
		this.swap = swap;
	}

	public SO getSistema() {
		return this.sistema;
	}

	private void setSistema(SO sistema) {
		this.sistema = sistema;
	}

	public List<Processo> getMemoria() {
		return this.memoria;
	}

	private void setMemoria(List<Processo> memoria) {
		this.memoria = memoria;
	}

}
