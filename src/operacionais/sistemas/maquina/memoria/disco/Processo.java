package operacionais.sistemas.maquina.memoria.disco;

public final class Processo extends Thread {

	private Object executor;
	private boolean executar;
	private int identificador;

	public Processo(int identificador) {
		this.setIdentificador(identificador);
		this.setExecutor(new Object());
		this.preparar();
	}

	private void preparar() {
		/*
		 * Modelo de "instru��o" pensado para grava��o "G"
		 * Exemplo -> G:5=1;G:8=2
		 * Explica��o 1 -> Gravar (Instru��o) : Inteiro 5 (Valor) = Posicao 1 (Endere�o)
		 * Explica��o 2 -> Gravar (Instru��o) : Inteiro 8 (Valor) = Posicao 2 (Endere�o)
		 * 
		 * Modelo de "instru��o" pensado para Leitura "L"
		 * Exemplo -> L:1;L:2
		 * Explica��o 1 -> Ler (Instru��o) : Posi��o 1 (Endere�o)
		 * Explica��o 2 -> Ler (Instru��o) : Posi��o 2 (Endere�o)
		 * 
		 * Modelo "completo" de instru��es executando
		 * "simultaneamente" leituras e grava��es
		 * 
		 * Exemplo -> G:7=1;G:5=2;L:1;G:1=1;L:1;L:2
		 */
	}

	@Override
	public void run() {
		while (this.isExecutar()) {
			// Ler instru��es e solicitar ao SO...
		}
	}

	public void bloquear() {
		try {
			this.getExecutor().wait();
		} catch (InterruptedException e) {
			// Fazer algo aqui... parar o processo???
		}
	}

	public void executar() {
		this.setExecutar(true);
		this.getExecutor().notify();
		if ((!(this.isAlive()))) {
			this.start();
		}
	}

	private Object getExecutor() {
		return this.executor;
	}

	private void setExecutor(Object executor) {
		this.executor = executor;
	}

	private boolean isExecutar() {
		return this.executar;
	}

	private void setExecutar(boolean executar) {
		this.executar = executar;
	}

	public int getIdentificador() {
		return this.identificador;
	}

	private void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

}
