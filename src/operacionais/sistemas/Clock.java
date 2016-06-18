package operacionais.sistemas;

public final class Clock extends Thread {

	private int tempo;
	private boolean rodando;

	public Clock() {
		this.setTempo(0);
		this.setRodando(false);
	}

	public void run() {
		try {
			while (this.isRodando()) {
				SO.instancia().clock();
				Thread.sleep(500);
			}
		} catch (InterruptedException error) {
		}
	}

	public void iniciar() {
		if ((!(this.isRodando()))) {
			this.setRodando(true);
			this.start();
		}
	}

	public void parar() {
		if ((this.isRodando())) {
			this.setRodando(false);
		}
	}

	public int getTempo() {
		return this.tempo;
	}

	private void setTempo(int tempo) {
		this.tempo = tempo;
	}

	private boolean isRodando() {
		return this.rodando;
	}

	private void setRodando(boolean rodando) {
		this.rodando = rodando;
	}

}
