package operacionais.sistemas.maquina;

public final class Maquina {

	private static Maquina INSTANCIA;

	public static Maquina instancia() {
		if ((Maquina.INSTANCIA == null)) {
			Maquina.INSTANCIA = new Maquina();
		}
		return Maquina.INSTANCIA;
	}

	private Processador processador;
	private Memoria memoria;
	private Disco disco;

	private Maquina() {
		/* 
		 * Manter m�todo private, padr�o de projeto singleton para
		 * evitar ter sempre de passar o objeto da class Maquina
		 * via par�metros.
		 * 
		 * Manter privado para n�o criar outras int�ncias desse objeto
		 * em lugares indevidos...
		 */
	}

	// Inicia os componentes "hardware" da m�quina
	public void ligar() {
		System.out.println("Iniciando a m�quina...");

		Processador processador = new Processador();
		Memoria memoria = new Memoria();
		Disco disco = new Disco();

		this.setProcessador(processador);
		this.setMemoria(memoria);
		this.setDisco(disco);

		this.iniciar();
	}

	// Inicia tudo que precisa pro sistema rodar
	private void iniciar() {
		this.getProcessador().iniciar();
		this.getDisco().ligar();
		this.getDisco().getSistema().iniciar();
	}

	public Processador getProcessador() {
		return this.processador;
	}

	private void setProcessador(Processador processador) {
		this.processador = processador;
	}

	public Memoria getMemoria() {
		return this.memoria;
	}

	private void setMemoria(Memoria memoria) {
		this.memoria = memoria;
	}

	public Disco getDisco() {
		return this.disco;
	}

	private void setDisco(Disco disco) {
		this.disco = disco;
	}

}
