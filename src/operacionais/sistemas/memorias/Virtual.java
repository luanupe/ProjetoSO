package operacionais.sistemas.memorias;

import java.util.HashMap;
import java.util.Map;

public final class Virtual {

	public final static int LOCAL_RAM = 0;
	public final static int LOCAL_DISCO = 1;
	public final static int CAPACIDADE = (RAM.CAPACIDADE * 2);

	private static Virtual INSTANCIA;

	public static Virtual instancia() {
		if ((Virtual.INSTANCIA == null)) {
			Virtual.INSTANCIA = new Virtual();
		}
		return Virtual.INSTANCIA;
	}

	private RAM ram;
	private Disco disco;
	private Map<Integer, Pagina> enderecos;

	private Virtual() {
		this.setRam(new RAM());
		this.setDisco(new Disco());
		this.setEnderecos(new HashMap<Integer, Pagina>());
	}

	public void preparar() {
		for (int endereco = 0; endereco < Virtual.CAPACIDADE; ++endereco) {
			Pagina pagina = new Pagina();
			this.getEnderecos().put(endereco, pagina);
		}
	}

	/*
	 * Nos métodos abaixo, aparentemente a memória virtual está acessando
	 * diretamente a RAM/DISCO, no entanto esse método é apenas para auxiliar e
	 * é chamado apenas quando o SO solicita em ler/gravar.
	 */

	public Integer ler(int endereco, int local) {
		if ((local == Virtual.LOCAL_RAM)) {
			return this.getRam().ler(endereco);
		}
		return this.getDisco().ler(endereco);
	}

	public void gravar(int endereco, int valor, int local) {
		if ((local == Virtual.LOCAL_RAM)) {
			this.getRam().gravar(endereco, valor);
		} else {
			this.getDisco().gravar(endereco, valor);
		}
	}

	public RAM getRam() {
		return this.ram;
	}

	private void setRam(RAM ram) {
		this.ram = ram;
	}

	public Disco getDisco() {
		return this.disco;
	}

	private void setDisco(Disco disco) {
		this.disco = disco;
	}

	public Pagina getPagina(int endereco) {
		return this.getEnderecos().get(endereco);
	}

	private Map<Integer, Pagina> getEnderecos() {
		return this.enderecos;
	}

	private void setEnderecos(Map<Integer, Pagina> enderecos) {
		this.enderecos = enderecos;
	}

	public class Pagina {

		private int contador;
		private int endereco; // -1 indica que está sem uso
		private boolean presente;
		private boolean modificada;
		private boolean referenciada;

		private Pagina() {
			this.setContador(0);
			this.setEndereco(-1);
			this.setPresente(false);
			this.setModificada(false);
			this.setReferenciada(false);
		}

		public void gravar(int endereco, boolean presente, boolean modificada, boolean referenciada) {
			this.setContador(0);
			this.setEndereco(endereco);
			this.setPresente(presente);
			this.setModificada(modificada);
			this.setReferenciada(referenciada);
		}

		public void clock() {
			++this.contador;
			if (((this.contador % 17) == 0)) {
				this.setContador(0);
				this.setReferenciada(false);
				// System.out.println("\t\t\t\t\t\t\t\t\t\t\t\t\t FIM DO CICLO DE CLOCK");
			}
		}

		public int getContador() {
			return this.contador;
		}

		private void setContador(int contador) {
			this.contador = contador;
		}

		public int getEndereco() {
			return this.endereco;
		}

		public void setEndereco(int endereco) {
			this.endereco = endereco;
		}

		public boolean isPresente() {
			return this.presente;
		}

		public void setPresente(boolean presente) {
			this.presente = presente;
		}

		public boolean isModificada() {
			return this.modificada;
		}

		public void setModificada(boolean modificada) {
			this.modificada = modificada;
		}

		public boolean isReferenciada() {
			return this.referenciada;
		}

		public void setReferenciada(boolean referenciada) {
			this.referenciada = referenciada;
		}

	}

}
