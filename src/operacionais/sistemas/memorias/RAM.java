package operacionais.sistemas.memorias;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RAM {

	public final static int PAGINAS = 5; // Quantidade de páginas por processo
	public final static int CAPACIDADE = (RAM.PAGINAS * Disco.PROCESSOS);

	private Map<Integer, Integer> enderecos;

	protected RAM() {
		this.setEnderecos(new ConcurrentHashMap<Integer, Integer>());
	}

	public Integer ler(int endereco) {
		if ((endereco < 0) || (endereco >= RAM.CAPACIDADE)) { 
			// EXCEPTION: Violação de memória
		}
		return this.getEnderecos().get(endereco);
	}

	public void gravar(int endereco, int valor) {
		if ((endereco < 0) || (endereco >= RAM.CAPACIDADE)) {
			// EXCEPTION: Violação de memória
		}
		this.getEnderecos().put(endereco, valor);
	}

	public void remover(int endereco) {
		this.getEnderecos().remove(endereco);
	}

	public String toString() {
		String string = "";
		for (Map.Entry<Integer, Integer> endereco : this.getEnderecos().entrySet()) {
			string += "[" + endereco.getKey() + "] = '" + endereco.getValue() + "'; ";
		}
		return string;
	}

	private Map<Integer, Integer> getEnderecos() {
		return this.enderecos;
	}

	private void setEnderecos(Map<Integer, Integer> enderecos) {
		this.enderecos = enderecos;
	}

}
