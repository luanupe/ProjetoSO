package operacionais.sistemas.maquina.processador;

import operacionais.sistemas.maquina.Maquina;

public final class Clock extends Thread {

	private boolean clockeando;
	
	public Clock() {
		this.setClockeando(false);
	}

	public void iniciar() {
		this.setClockeando(true);
		this.start();
	}

	@Override
	public void run() {
		try {
			while (this.isClockeando()) {
				Thread.sleep(500l);
				Maquina.instancia().getDisco().getSistema().clock();
			}
		} catch (InterruptedException error) {
			// EXCEPTION: Overheat no processador?!?!
		}
	}

	public boolean isClockeando() {
		return this.clockeando;
	}

	private void setClockeando(boolean clockeando) {
		this.clockeando = clockeando;
	}
	
}
