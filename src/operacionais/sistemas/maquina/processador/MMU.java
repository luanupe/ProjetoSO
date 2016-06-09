package operacionais.sistemas.maquina.processador;

import operacionais.sistemas.maquina.Maquina;
import operacionais.sistemas.maquina.memoria.Pagina;
import operacionais.sistemas.maquina.memoria.outras.Virtual.Endereco;

public final class MMU {

	public Pagina recuperar(int posicao, Integer[] limite) {
		Endereco endereco = Maquina.instancia().getMemoria().getVirtual().getEndereco(posicao);
		if ((endereco != null)) {
			if ((!(endereco.isPresente()))) {
				// Trazer do SWAP pra memória
			}
			return Maquina.instancia().getMemoria().getRam().recuperar(endereco.getPosicao());
		} else {
			// EXCEPTION: Acesso inválido à memória
		}
		return null;
	}

}
