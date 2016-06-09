package operacionais.sistemas.maquina.memoria.outras;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import operacionais.sistemas.maquina.Disco;
import operacionais.sistemas.maquina.memoria.Pagina;

public final class RAM {

	public static final int TAMANHO_PAGINA = 4; // Tamanho da página, após isso há o "chaveamento" com o disco
	public static final int CAPACIDADE = (RAM.TAMANHO_PAGINA * Disco.PROCESSOS); // Pra garantir memória suficiente pra todos os processos
	private Map<Integer, Pagina> enderecamentos;

	public RAM() {
		Map<Integer, Pagina> enderecamentos = new HashMap<Integer, Pagina>();
		this.setEnderecamentos(enderecamentos);
	}

	public Pagina recuperar(int endereco) {
		if ((endereco >= 0) && (endereco < RAM.CAPACIDADE)) {
			return this.getEnderecamentos().get(endereco);
		} else {
			// EXCEPTION: Acesso inválido à memória
		}
		return null;
	}

	public Collection<Pagina> getPaginas() {
		return this.getEnderecamentos().values();
	}

	private Map<Integer, Pagina> getEnderecamentos() {
		return this.enderecamentos;
	}

	private void setEnderecamentos(Map<Integer, Pagina> enderecamentos) {
		this.enderecamentos = enderecamentos;
	}

}
