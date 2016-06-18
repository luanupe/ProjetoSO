package operacionais.sistemas;

public class Processo extends Thread {

	private int processo;
	private String instrucoes;

	protected Processo(int processo, String instrucoes) {
		this.setProcesso(processo);
		this.setInstrucoes(instrucoes);
	}

	@Override
	public void run() {
		String comandos[] = this.getInstrucoes().split(",");
		for (String instrucao : comandos) {
			try {
				Thread.sleep(1000 * this.getPriority());
				SO.instancia().interpretar(this.getProcesso(), instrucao);
			} catch (InterruptedException error) {
				error.printStackTrace();
			} catch (Exception error) {
				error.printStackTrace();
			}
		}
	}

	public int getProcesso() {
		return this.processo;
	}

	private void setProcesso(int processo) {
		this.processo = processo;
	}

	private String getInstrucoes() {
		return this.instrucoes;
	}

	private void setInstrucoes(String instrucoes) {
		this.instrucoes = instrucoes;
	}

}
