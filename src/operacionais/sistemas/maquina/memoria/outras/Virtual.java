package operacionais.sistemas.maquina.memoria.outras;

import java.util.Map;
import java.util.HashMap;

import operacionais.sistemas.maquina.Maquina;

public final class Virtual {

	private Map<Integer, Endereco> enderecamentos;

	public Virtual() {
		Map<Integer, Endereco> enderecamentos = new HashMap<Integer, Endereco>();
		this.setEnderecamentos(enderecamentos);
	}

	public void iniciar() {
		for (int id = 0; (id < (RAM.CAPACIDADE * 2)); ++id) {
			Endereco endereco = new Endereco(id);
			this.getEnderecamentos().put(endereco.getId(), endereco);
		}
	}

	public Endereco getEndereco(int posicao) {
		return this.getEnderecamentos().get(posicao);
	}

	private Map<Integer, Endereco> getEnderecamentos() {
		return this.enderecamentos;
	}

	private void setEnderecamentos(Map<Integer, Endereco> enderecamentos) {
		this.enderecamentos = enderecamentos;
	}

	public class Endereco {

		private int id;
		private int posicao;
		private boolean presente;

		private Endereco(int id) {
			this.setId(id);
			this.setPosicao(-1);
			this.setPresente(false);
		}

		public int getId() {
			return this.id;
		}

		private void setId(int id) {
			this.id = id;
		}

		public int getPosicao() {
			return this.posicao;
		}

		private void setPosicao(int posicao) {
			this.posicao = posicao;
		}

		public boolean isPresente() {
			return this.presente;
		}

		private void setPresente(boolean presente) {
			this.presente = presente;
		}

	}

}
