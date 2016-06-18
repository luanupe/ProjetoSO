package operacionais.sistemas;

import java.util.Map;
import java.util.Random;
import java.util.HashMap;

import operacionais.sistemas.memorias.Disco;
import operacionais.sistemas.memorias.RAM;
import operacionais.sistemas.memorias.Virtual;
import operacionais.sistemas.memorias.Virtual.Pagina;

public class SO {

	private static SO INSTANCIA;

	public static SO instancia() {
		if ((SO.INSTANCIA == null)) {
			SO.INSTANCIA = new SO();
		}
		return SO.INSTANCIA;
	}

	private Random gerador;
	private FabricaDeEntradas entradas;
	private Map<Integer, Controle> processos;

	public SO() {
		this.setGerador(new Random());
		// this.setEntradas(new FabricaDeEntradas(RAM.PAGINAS * 2));
		this.setProcessos(new HashMap<Integer, Controle>());
	}

	public void preparar() {
		Virtual.instancia().preparar(); // Inicia os endereços da memória virtual
		for (int i = 1; i <= Disco.PROCESSOS; i++) {
			Processo processo = new Processo(i, (new FabricaDeEntradas(RAM.PAGINAS * 2)).getNewEntrada());
			Controle controle = new Controle(processo); // Controle dos limites da memória reservada
			controle.preparar();
			
			this.getProcessos().put(i, controle);
			processo.start();
		}
	}

	public void interpretar(int processo, String instrucao) {
		String[] comandos = instrucao.split("-");
		Controle controle = this.getProcessos().get(processo);

		// Encontra o endereço verdadeiro pelo ID do processo
		int endereco = Integer.parseInt(comandos[0]);
		int enderecoVirtual = (Integer.parseInt(comandos[0]) + controle.getLIVirtual());

		if ((comandos[1].equals("R"))) {
			System.out.println("[" + processo + "] > LER: endereco: '" + endereco + "', virtual: '" + enderecoVirtual + "'");
			int valor = this.ler(controle, enderecoVirtual);
		} else if ((comandos[1].equals("W"))) {
			int valor = this.getGerador().nextInt(100); // Gera um valor entre 0 e 99 pra salvar no endereço
			System.out.println("[" + processo + "] > GRAVAR: endereco: '" + endereco + "', virtual: '" + enderecoVirtual + "' -> VALOR '" + valor + "'");
			this.gravar(controle, enderecoVirtual, valor);
		}
	}

	private Pagina getMenosUtilizada(int limiteInferior, int limiteSuperior) {
		Pagina menosUtilizada = null;
		for (int endereco = limiteInferior; (endereco < limiteSuperior); ++endereco) {
			Pagina paginaTestada = Virtual.instancia().getPagina(endereco);
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
		if ((pagina.getEndereco() > 0)) {
			if ((pagina.isPresente())) {
				// return Virtual.instancia().ler(endereco, Virtual.LOCAL_RAM);
			}
			// return Virtual.instancia().ler(endereco, Virtual.LOCAL_DISCO);
		}
		return 0;
	}

	private int persistir(int local, int limiteInferior, int limiteSuperior) {
		for (int endereco = limiteInferior; (endereco < limiteSuperior); ++endereco) {
			Integer valorLido = Virtual.instancia().ler(endereco, local);
			if ((valorLido == null)) {
				return endereco;
			}
		}
		return -1;
	}

	private void gravar(Controle controle, int endereco, int valor) {
		Pagina pagina = Virtual.instancia().getPagina(endereco);
		if ((pagina.isPresente())) {
			System.err.println("[" + controle.getId() + "] Pagina presente!!!! Substituir valor.");
			// Página já está presenta na memória RAM, apenas sobreescrever o valor
			Virtual.instancia().gravar(pagina.getEndereco(), valor, Virtual.LOCAL_RAM);
		} else {
			// Se a página não estiver presente a memória RAM
			if ((pagina.getEndereco() < 0)) {
				int enderecoLivreRAM = this.persistir(Virtual.LOCAL_RAM, controle.getLIFisica(), controle.getLSFisica());
				if ((enderecoLivreRAM >= 0)) {
					System.err.println("[" + controle.getId() + "] Posição livre encontrada! Escrevendo valor na RAM.");
					Virtual.instancia().gravar(enderecoLivreRAM, valor, Virtual.LOCAL_RAM);
					pagina.setEndereco(enderecoLivreRAM);
					pagina.setReferenciada(true);
					pagina.setModificada(true);
					pagina.setPresente(true);
				} else {
					System.err.println("[" + controle.getId() + "] Page fault em NOVA PÁGINA... Encontrando página livre e substituindo.");
					// Page fault
					
					// Tratar a "antiga" página
					Pagina menosUtilizada = this.getMenosUtilizada(controle.getLIVirtual(), controle.getLSVirtual());
					int enderecoMenosUtilizada = menosUtilizada.getEndereco();
					int valorMenosUtilizada = Virtual.instancia().ler(enderecoMenosUtilizada, Virtual.LOCAL_RAM);
					
					int enderecoLivreDisco = this.persistir(Virtual.LOCAL_DISCO, controle.getLIFisica(), controle.getLSFisica());
					Virtual.instancia().gravar(enderecoLivreDisco, valorMenosUtilizada, Virtual.LOCAL_DISCO);
					
					menosUtilizada.setPresente(false);
					menosUtilizada.setEndereco(enderecoLivreDisco);
					
					// Tratar a "nova" página
					Virtual.instancia().gravar(enderecoMenosUtilizada, valor, Virtual.LOCAL_RAM);
					pagina.setPresente(true);
					pagina.setEndereco(enderecoMenosUtilizada);
					pagina.setReferenciada(true);
					pagina.setModificada(true);
				}
			} else {
				System.err.println("[" + controle.getId() + "] Page fault em PÁGINA EXISTENTE... Encontrando página livre e substituindo.");
				// Se já existir endereçamento, mas não está presente
				int enderecoLivreDisco = pagina.getContador();
				int valorRecuperadoDisco = Virtual.instancia().ler(enderecoLivreDisco, Virtual.LOCAL_DISCO);
				
				Pagina menosUtilizada = this.getMenosUtilizada(controle.getLIVirtual(), controle.getLSVirtual());
				int enderecoMenosUtilizada = menosUtilizada.getEndereco();
				int valorMenosUtilizada = Virtual.instancia().ler(enderecoMenosUtilizada, Virtual.LOCAL_RAM);
				
				Virtual.instancia().gravar(enderecoLivreDisco, valorMenosUtilizada, Virtual.LOCAL_DISCO);
				
				// Tratar a "novo" valor na página
				Virtual.instancia().gravar(enderecoMenosUtilizada, valor, Virtual.LOCAL_RAM);
				pagina.setPresente(true);
				pagina.setEndereco(enderecoMenosUtilizada);
				pagina.setReferenciada(true);
				pagina.setModificada(true);
			}
		}

		System.out.println("[" + controle.getId() + "] RAM: " + Virtual.instancia().getRam().toString());
		System.out.println("[" + controle.getId() + "] DISCO: " + Virtual.instancia().getDisco().toString());
	}

	private Random getGerador() {
		return this.gerador;
	}

	private void setGerador(Random gerador) {
		this.gerador = gerador;
	}

	private FabricaDeEntradas getEntradas() {
		return this.entradas;
	}

	private void setEntradas(FabricaDeEntradas entradas) {
		this.entradas = entradas;
	}

	private Map<Integer, Controle> getProcessos() {
		return this.processos;
	}

	private void setProcessos(Map<Integer, Controle> processos) {
		this.processos = processos;
	}

	private class Controle {

		private Processo processo;
		private int LIFisica;
		private int LSFisica;
		private int LIVirtual;
		private int LSVirtual;

		private Controle(Processo instancia) {
			this.setProcesso(instancia);
		}
		
		private void preparar() {
			int LIFisica = (RAM.PAGINAS * this.getId()) - RAM.PAGINAS;
			int LSFisica = (LIFisica + RAM.PAGINAS);
			
			this.setLIFisica(LIFisica);
			this.setLSFicia(LSFisica);
			this.setLIVirtual(this.getLIFisica() * 2);
			this.setLSVirtual(this.getLSFisica() * 2);
			
			System.out.println("[" + this.getId() + "] > LIF: '" + this.getLIFisica() + "', LSF: '" + this.getLSFisica() + "', LIV: '" + this.getLIVirtual() + "', LSV: '" + this.getLSVirtual() + "'");
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

		public int getLIFisica() {
			return this.LIFisica;
		}

		private void setLIFisica(int LIFisica) {
			this.LIFisica = LIFisica;
		}

		public int getLSFisica() {
			return this.LSFisica;
		}

		private void setLSFicia(int LSFisica) {
			this.LSFisica = LSFisica;
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
