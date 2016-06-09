package operacionais.sistemas.maquina.memoria.disco;

import java.util.HashMap;
import java.util.Map;

import operacionais.sistemas.maquina.memoria.Pagina;
import operacionais.sistemas.maquina.memoria.outras.RAM;

public final class SWAP {

	private Map<Integer, Pagina> enderecamentos;

	public SWAP() {
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

	private Map<Integer, Pagina> getEnderecamentos() {
		return this.enderecamentos;
	}

	private void setEnderecamentos(Map<Integer, Pagina> enderecamentos) {
		this.enderecamentos = enderecamentos;
	}

}
