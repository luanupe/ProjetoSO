package operacionais.sistemas.maquina;

import operacionais.sistemas.maquina.memoria.Pagina;
import operacionais.sistemas.maquina.memoria.outras.RAM;
import operacionais.sistemas.maquina.memoria.outras.Virtual;

public final class Memoria {

	private RAM ram;
	private Virtual virtual;

	protected Memoria() {
		System.out.println("Checando o memoria...");

		this.setRam(new RAM());
		this.setVirtual(new Virtual());
	}

	public void clock() {
		for (Pagina pagina : this.getRam().getPaginas()) {
			pagina.clock();
		}
	}

	public RAM getRam() {
		return this.ram;
	}

	private void setRam(RAM ram) {
		this.ram = ram;
	}

	public Virtual getVirtual() {
		return this.virtual;
	}

	private void setVirtual(Virtual virtual) {
		this.virtual = virtual;
	}

}
