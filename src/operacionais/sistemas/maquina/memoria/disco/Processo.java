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
		 * Modelo de "instrução" pensado para gravação "G"
		 * Exemplo -> G:5=1;G:8=2
		 * Explicação 1 -> Gravar (Instrução) : Inteiro 5 (Valor) = Posicao 1 (Endereço)
		 * Explicação 2 -> Gravar (Instrução) : Inteiro 8 (Valor) = Posicao 2 (Endereço)
		 * 
		 * Modelo de "instrução" pensado para Leitura "L"
		 * Exemplo -> L:1;L:2
		 * Explicação 1 -> Ler (Instrução) : Posição 1 (Endereço)
		 * Explicação 2 -> Ler (Instrução) : Posição 2 (Endereço)
		 * 
		 * Modelo "completo" de instruções executando
		 * "simultaneamente" leituras e gravações
		 * 
		 * Exemplo -> G:7=1;G:5=2;L:1;G:1=1;L:1;L:2
		 */
	}

	@Override
	public void run() {
		while (this.isExecutar()) {
			// Ler instruções e solicitar ao SO...
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
