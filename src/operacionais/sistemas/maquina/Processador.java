package operacionais.sistemas.maquina;

import operacionais.sistemas.maquina.processador.MMU;
import operacionais.sistemas.maquina.processador.Clock;

public final class Processador {

	private Clock clock;
	private MMU controle;

	protected Processador() {
		System.out.println("Checando processador...");

		Clock clock = new Clock();
		MMU controle = new MMU();

		this.setClock(clock);
		this.setControle(controle);
	}

	public void iniciar() {
		this.getClock().iniciar();
	}

	public Clock getClock() {
		return this.clock;
	}

	private void setClock(Clock clock) {
		this.clock = clock;
	}

	public MMU getControle() {
		return this.controle;
	}

	private void setControle(MMU controle) {
		this.controle = controle;
	}

}
