package operacionais.sistemas.maquina.memoria.disco;

import java.util.Map;
import java.util.HashMap;
import operacionais.sistemas.maquina.Maquina;
import operacionais.sistemas.maquina.memoria.Pagina;
import operacionais.sistemas.maquina.memoria.outras.RAM;

public final class SO {

	public static final String NOME = "S.Oper.";

	private int time;
	private Map<Integer, Processo> processos;
	private Map<Processo, Integer[]> limites;

	public SO() {
		System.out.println("Carregando sistema operacional...");

		Map<Integer, Processo> processos = new HashMap<Integer, Processo>();
		Map<Processo, Integer[]> limites = new HashMap<Processo, Integer[]>();

		this.time = -1;
		this.setProcessos(processos);
		this.setLimites(limites);
	}

	public void iniciar() {
		// "Iniciar" o programa na memoria do disco e "cria" o processo
		for (Processo processo : Maquina.instancia().getDisco().getMemoria()) {
			this.getProcessos().put(processo.getIdentificador(), processo);

			// Cada processo tem uma area limitada de memória
			// Códigos abaixo apenas define o limite mínimo e máximo
			int limite0 = (RAM.TAMANHO_PAGINA * processo.getIdentificador()) - RAM.TAMANHO_PAGINA;
			int limite1 = (limite0 + RAM.TAMANHO_PAGINA);
			Integer[] limite = new Integer[] { limite0, (limite1-1) };
			this.getLimites().put(processo, limite);
			System.out.println("Processo " + processo.getIdentificador() + " -> Limite0: " + limite[0] + "; Limite1: " + limite[1]);
		}
		
		System.out.println(new StringBuilder().append(SO.NOME).append(" iniciado com sucesso.").toString());
	}

	public void ler(Processo processo, int endereco) {
		Pagina pagina = this.recuperar(processo, endereco);
		if ((pagina != null)) {
			pagina.setReferenciado(true);
			System.out.println(new StringBuilder().append("Processo '").append(processo.getIdentificador()).append("' leu posicao '").append(endereco).append("' e encontrou: ").append(pagina.getValor()).toString());
		} else {
			/*
			 * EXCEPTION: Erro na página...
			 * 
			 * Endereçamento não existe???
			 * Sem memória virtual suficiente????
			 * 
			 * Tratar esse "erro" com cuidado...
			 */
		}
	}

	// Para evitar repetição de código esse método irá tratar toda a parte de recuperação
	// de páginas, inclusive trocar com a SWAP/RAM caso necessário atrás da MMU
	private Pagina recuperar(Processo processo, int endereco) {
		if ((this.getLimites().containsKey(processo))) {
			Integer[] limite = this.getLimites().get(processo);
			endereco = (endereco * processo.getIdentificador()); // Pega o valor endereço verdadeiro
			if ((endereco >= limite[0]) && (endereco <= limite[1])) { // Checa se a posição acessada está dentro das posições reservadas para o processo
				return Maquina.instancia().getProcessador().getControle().recuperar(endereco, limite);
			} else {
				// EXCEPTION: Acesso inválido à memória reservada...
			}
		} else {
			// EXCEPTION: Programa não está em execução...
		}
		return null;
	}

	public void clock() {
		if ((this.getProcessos() != null) && (this.getProcessos().size() > 0)) {
			Maquina.instancia().getMemoria().clock();
			
			if ((this.ciclo())) {
				System.out.println(" > FINAL DO CICLO...");
				/*
				 * if ((processoAtual != null)) {
				 * 	processoAtual.bloquear();
				 * 	processoAtual = processoAtual.proximo()
				 * } else {
				 * 	processoAtual = this.getProcessos().get(1);
				 * }
				 * 
				 * processoAtual.executar();
				 */
			} else {
				System.out.println(" * CONTINUA O CONTEXTO...");
			}
		}
	}

	private boolean ciclo() {
		if ((this.time < 0) || (this.time > 2)) { // Um clico = 4 clocks -> 1º ciclo = clock é -1
			this.time = 0;
			return true;
		}

		++this.time;
		return false;
	}

	private Map<Integer, Processo> getProcessos() {
		return this.processos;
	}

	private void setProcessos(Map<Integer, Processo> processos) {
		this.processos = processos;
	}

	public Map<Processo, Integer[]> getLimites() {
		return this.limites;
	}

	private void setLimites(Map<Processo, Integer[]> limites) {
		this.limites = limites;
	}

}
