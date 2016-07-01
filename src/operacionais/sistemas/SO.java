package operacionais.sistemas;

import java.util.Map;
import java.util.HashMap;
import operacionais.sistemas.memorias.RAM;
import operacionais.sistemas.memorias.Disco;
import operacionais.sistemas.memorias.Virtual;
import operacionais.sistemas.memorias.Virtual.Pagina;

public class SO {

	public static final int CICLO = 20;
	private static SO INSTANCIA;

	public static SO instancia() {
		if ((SO.INSTANCIA == null)) {
			SO.INSTANCIA = new SO();
		}
		return SO.INSTANCIA;
	}

	private Clock clock;
	private Map<Integer, Controle> processos;

	public SO() {
		this.setClock(new Clock());
		this.setProcessos(new HashMap<Integer, Controle>());
	}

	public void preparar() {
		Virtual.instancia().preparar();
		this.getClock().iniciar();

		for (int i = 1; i <= Disco.PROCESSOS; i++) {
			Processo processo = new Processo(i, (new FabricaDeEntradas(RAM.PAGINAS_POR_PROCESSO * 2)).getNewEntrada());
			Controle controle = new Controle(processo);
			controle.preparar();

			this.getProcessos().put(i, controle);
			processo.start();
		}
	}

	/*
	 * Para o clock depois que toda a f�brica de entrada tiver sido consumida.
	 */
	public void kill(int processo) {
		this.getProcessos().remove(processo);
		if ((this.getProcessos().isEmpty())) {
			System.out.println("\n\n--- [ PROCESSOS FINALIZADOS! ] --------------------------");
			this.getClock().parar();
		}
	}

	public void clock() {
		for (int endereco = 0; (endereco < Virtual.CAPACIDADE); ++endereco) {
			Pagina pagina = Virtual.instancia().getPagina(endereco);
			
			/*if ((endereco == 0)) {
				if (((this.getClock().getTempo() % SO.CICLO) == 0)) {
					System.out.println("\t\t\t\t\t\t\t\t\t\t FIM DE CICLO");
				} else {
					System.out.println("\t\t\t\t\t\t\t\t\t\t [" + this.getClock().getTempo() + "] CLOCK");
				}
			}*/
			
			if ((pagina.isPresente())) {
				if (((this.getClock().getTempo() % SO.CICLO) == 0)) {
					pagina.resetar();
				} else {
					pagina.clock();
				}
			}
		}
	}

	public void interpretar(int idProcesso, String instrucao) {
		String[] comandos = instrucao.split("-");
		Controle controle = this.getProcessos().get(idProcesso);

		int endereco = Integer.parseInt(comandos[0]);
		// Encontra o verdadeiro endere�o pelo ID do processo
		int enderecoVirtual = endereco + controle.getLIVirtual();

		if ((comandos[1].equals("R"))) {
			// System.out.println("[" + processo + "] > LER: endereco: '" +
			// endereco + "', virtual: '" + enderecoVirtual + "'");
			int valorLido = this.ler(controle, enderecoVirtual);
			System.out.println("[" + idProcesso + "] > LEU: endereco: '" + endereco + "', virtual: '" + enderecoVirtual + "' -> VALOR '" + valorLido + "'");
		} else if ((comandos[1].equals("W"))) {
			// Valor aleat�rio para ser persistido
			// int valor = this.getGerador().nextInt(100);
			int valor = Integer.parseInt(comandos[2]);
			System.out.println("[" + idProcesso + "] > GRAVAR: endereco: '" + endereco + "', virtual: '" + enderecoVirtual + "' -> VALOR '" + valor + "'");
			this.gravar(controle, enderecoVirtual, valor);
		}
	}

	/**
	 * Em caso de *page fault* esse m�todo � capaz de selecionar a melhor p�gina
	 * para ser chaveada da RAM para o DISCO.
	 * 
	 * @param limiteInferior Come�o relativo da Thread na mem�ria virtual
	 * @param limiteSuperior Fim relativo da Thread na mem�ria virtual
	 * @return Pagina menos utilizada no ciclo atual
	 */
	private Pagina getMenosUtilizada(int limiteInferior, int limiteSuperior) {
		Pagina menosUtilizada = null;
		for (int endereco = limiteInferior; (endereco < limiteSuperior); ++endereco) {
			Pagina paginaTestada = Virtual.instancia().getPagina(endereco);
			/*
			 * Para um p�gina poder ser chaveada com o DISCO ela tem de estar
			 * presente na RAM e o endere�amento tem de ser v�lido para que as
			 * informa��es sejam persistidas sem perder de dados.
			 * 
			 * Se a p�gina bater com o requisito de cima ela passa por outra
			 * checagem onde, se nenhuma p�gina menos utilizada tiver sido
			 * selecionada em voltas anteriores do loop ela ser� a "escolhida"
			 * at� que em outros loops seja detectado algu�m com um contador
			 * maior, indicando estar a mais tempo sem utiliza��o, para assumir
			 * a posi��o.
			 */

			if ((paginaTestada.isPresente()) && (paginaTestada.getEndereco() >= 0)) {
				if ((menosUtilizada == null) || (paginaTestada.getContador() > menosUtilizada.getContador())) {
					menosUtilizada = paginaTestada;
				}
			}
		}
		return menosUtilizada;
	}

	private int ler(Controle controle, int endereco) {
		Pagina pagina = Virtual.instancia().getPagina(endereco);
		if ((!(pagina.isPresente()))) {
			if ((pagina.getEndereco() < 0)) {
				return -1;
			} else {
				Pagina menosUtilizada = this.getMenosUtilizada(controle.getLIVirtual(), controle.getLSVirtual());
				int enderecoMenosUtilizada = menosUtilizada.getEndereco();
				int valorMenosUtilizada = Virtual.instancia().ler(enderecoMenosUtilizada, Virtual.LOCAL_RAM);
				
				int valorNoDisco = Virtual.instancia().ler(pagina.getEndereco(), Virtual.LOCAL_DISCO);
				int enderecoNoDisco = pagina.getEndereco();
				
				Virtual.instancia().getRam().remover(enderecoMenosUtilizada);
				Virtual.instancia().getDisco().remover(enderecoNoDisco);
				
				Virtual.instancia().gravar(enderecoMenosUtilizada, valorNoDisco, Virtual.LOCAL_RAM);
				pagina.atualizar(enderecoMenosUtilizada, true, false, true);
				
				Virtual.instancia().gravar(enderecoNoDisco, valorMenosUtilizada, Virtual.LOCAL_DISCO);
				menosUtilizada.atualizar(enderecoNoDisco, false, false, true);
			}
		}
		
		return Virtual.instancia().ler(pagina.getEndereco(), Virtual.LOCAL_RAM);
	}

	private synchronized int persistir(int local) {
		for (int endereco = 0; (endereco < RAM.CAPACIDADE); endereco++) {
			Integer valorLido = Virtual.instancia().ler(endereco, local);
			if ((valorLido == null)) {
				return endereco;
			}
		}
		return -1;
	}

	/**
	 * Para evitar repeti��es de c�digo nos blocos "else" foi necess�rio
	 * utilizar esse m�todo para persistir o valor em alguns casos de page
	 * fault.
	 * 
	 * @param controle Informa��es de controle do SO sobre o processo.
	 * @param pagina Pagina recuperada anteriormente para ser persistida.
	 * @param enderecoLivreDisco Endere�o onde a p�gina menos utilizada ser�
	 *        persistida.
	 * @param valor Valor que ser� persistido
	 */
	private void gravarPraNaoRepetirOCodigo(Controle controle, Pagina pagina, int enderecoLivreDisco, int valor) {
		/*
		 * Encontra a p�gina menos utilizada na RAM e guarda as informa��es que
		 * ser�o salvas em disco.
		 */

		// Roda o algoritmo de sele��o de p�gina "livre"
		Pagina menosUtilizada = this.getMenosUtilizada(controle.getLIVirtual(), controle.getLSVirtual());

		// Salva o endere�o para poder ser utilizado mais na frente
		int enderecoMenosUtilizada = menosUtilizada.getEndereco();

		// Salva o valor para persistir no disco
		int valorMenosUtilizada = Virtual.instancia().ler(enderecoMenosUtilizada, Virtual.LOCAL_RAM);

		// Trata a p�gina menos utilizada
		Virtual.instancia().getRam().remover(enderecoMenosUtilizada);
		Virtual.instancia().getDisco().remover(enderecoLivreDisco);

		Virtual.instancia().gravar(enderecoLivreDisco, valorMenosUtilizada, Virtual.LOCAL_DISCO);
		menosUtilizada.atualizar(enderecoLivreDisco, false, false, true);

		// Trata a "nova" p�gina
		Virtual.instancia().gravar(enderecoMenosUtilizada, valor, Virtual.LOCAL_RAM);
		pagina.atualizar(enderecoMenosUtilizada, true, true, true);
	}

	private void gravar(Controle controle, int endereco, int valor) {
		Pagina pagina = Virtual.instancia().getPagina(endereco);
		if ((pagina.isPresente())) {
			System.out.println("[" + controle.getId() + "] Pagina presente!!!! Substituir valor.");
			Virtual.instancia().gravar(pagina.getEndereco(), valor, Virtual.LOCAL_RAM);
			pagina.atualizar(pagina.getEndereco(), true, true, true);
		} else {
			// Entra aqui se n�o estiver armazenado na mem�ria RAM
			if ((pagina.getEndereco() < 0)) {
				/*
				 * A p�gina n�o est� presente e n�o possui mapeamento, logo � um
				 * endere�o que nunca foi utilizado
				 * 
				 * TENTAR GRAVAR DIRETO NA RAM
				 * 
				 * 1 - SE **CONSEGUIR** GRAVAR DIRETO NA RAM
				 * 
				 * 1.1 - Atualiza os status(R, M) e endere�o da que foi gravada.
				 * 
				 * 2 - SE **N�O CONSEGUIR** GRAVAR NA RAM
				 * 
				 * 2.1 - Escolhe uma posi��o livre no disco;
				 * 
				 * 2.2 - Encontrar uma p�gina menos utilizada na RAM;
				 * 
				 * 2.3 - Armazena o endere�o dessa p�gina menos utilizada;
				 * 
				 * 2.4 - Armazena o valor da menos utilizada;
				 * 
				 * 2.5 - Remover da RAM o valor menos utilizado [2.4];
				 * 
				 * 2.6 - Salvar o valor da menos utilizada [2.4] no DISCO na
				 * posi��o escolhida em [2.1];
				 * 
				 * 2.7 - Atualiza os status(R, M) e endere�o da p�gina menos
				 * utilizada [2.2];
				 * 
				 * 2.8 - Gravar o novo valor na RAM na antiga posi��o da menos
				 * utilizada [2.3];
				 * 
				 * 2.9 - Atualiza os status(R, M) e endere�o da p�gina[endereco
				 * param].
				 */

				int enderecoLivreRAM = this.persistir(Virtual.LOCAL_RAM);
				if ((enderecoLivreRAM >= 0)) {
					// Concluir a etapa [1]
					System.out.println("[" + controle.getId() + "] Posi��o livre encontrada! Escrevendo valor na RAM.");
					Virtual.instancia().gravar(enderecoLivreRAM, valor, Virtual.LOCAL_RAM);
					pagina.atualizar(enderecoLivreRAM, true, true, true);
				} else {
					// Concluir a etapa [2]
					System.out.println("[" + controle.getId() + "] Page fault em NOVA P�GINA... Encontrando p�gina livre e substituindo.");
					int enderecoLivreDisco = this.persistir(Virtual.LOCAL_DISCO);

					// M�todo "gravar" respons�vel por realizar a etapa 2
					this.gravarPraNaoRepetirOCodigo(controle, pagina, enderecoLivreDisco, valor);
				}
			} else {
				System.out.println("[" + controle.getId() + "] Page fault em P�GINA EXISTENTE... Encontrando p�gina livre e substituindo.");

				/*
				 * A p�gina do endere�o[param] n�o est� presente, ou seja, est�
				 * no DISCO, e ela j� possui uma refer�ncia, logo, precisamos
				 * tirar ela do disco e por na RAM!
				 * 
				 * 1 - O endere�o atual da p�gina[endereco param] precisa ser
				 * salvo, � a posi��o que vai ficar livre no DISCO;
				 * 
				 * 2 - Encontrar uma p�gina menos utilizada na RAM;
				 * 
				 * 3 - Armazena o endere�o dessa p�gina menos utilizada;
				 * 
				 * 4 - Armazena o valor da p�gina menos utilizada;
				 * 
				 * 5 - Remover da RAM o valor menos utilizado [4];
				 * 
				 * 6 - Salvar o valor da menos utilizada [2.4] no DISCO na
				 * posi��o salva em [1];
				 * 
				 * 7 - Mudar os status(R, M) e endere�o da p�gina menos
				 * utilizada [2];
				 * 
				 * 8 - Gravar o novo valor na RAM na antiga posi��o da menos
				 * utilizada [3];
				 * 
				 * 9 - Mudar os status(R, M) e endere�o da p�gina[endereco
				 * param].
				 */

				int enderecoLivreDisco = pagina.getEndereco();

				// M�todo "gravar" respons�vel por realizar as etapas
				this.gravarPraNaoRepetirOCodigo(controle, pagina, enderecoLivreDisco, valor);
			}
		}

		System.out.println("[" + controle.getId() + "] RAM: " + Virtual.instancia().getRam().toString());
		System.out.println("[" + controle.getId() + "] DISCO: " + Virtual.instancia().getDisco().toString());
		System.out.println("[" + controle.getId() + "] VIRTUAL: " + Virtual.instancia().toString());
	}

	private Clock getClock() {
		return this.clock;
	}

	private void setClock(Clock clock) {
		this.clock = clock;
	}

	private Map<Integer, Controle> getProcessos() {
		return this.processos;
	}

	private void setProcessos(Map<Integer, Controle> processos) {
		this.processos = processos;
	}

	private class Controle {

		private Processo processo;
		private int LIVirtual;
		private int LSVirtual;

		private Controle(Processo instancia) {
			this.setProcesso(instancia);
		}

		private void preparar() {
			/*
			 * Thread1 solicitou '0-R' e Thread2 solicitou '0-W'
			 * 
			 * Para que o '0' da Thread1 seja diferente do '0' da Thread2 �
			 * necess�rio "dividir" a mem�ria.
			 * 
			 * Esse m�todo faz apenas definir os limites da mem�ria virtual e da
			 * "f�sica", quanto menor o ID mais pr�ximo do come�o da Array e
			 * quanto maior o ID mais distante do in�cio.
			 * 
			 * LI: Limite Inferior, LS: Limite Superior
			 */

			int LIVirtual = (RAM.PAGINAS_POR_PROCESSO * this.getId()) - RAM.PAGINAS_POR_PROCESSO;
			int LSVirtual = (LIVirtual + RAM.PAGINAS_POR_PROCESSO);

			this.setLIVirtual(LIVirtual * 2);
			this.setLSVirtual(LSVirtual * 2);
		}

		public int getId() {
			return this.getProcesso().getProcesso();
		}

		private Processo getProcesso() {
			return this.processo;
		}

		private void setProcesso(Processo processo) {
			this.processo = processo;
		}

		public int getLIVirtual() {
			return this.LIVirtual;
		}

		private void setLIVirtual(int lIVirtual) {
			this.LIVirtual = lIVirtual;
		}

		public int getLSVirtual() {
			return this.LSVirtual;
		}

		private void setLSVirtual(int lSVirtual) {
			this.LSVirtual = lSVirtual;
		}

	}

}
