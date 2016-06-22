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
		/*for (int i = 0; i < comandos.length; i++) {
			String instrucao = comandos[i];
		}*/
		
		
		for (String instrucao : comandos) {
			try {
				SO.instancia().interpretar(this.getProcesso(), instrucao);
				Thread.sleep(500l);
			} catch (InterruptedException error) {
				error.printStackTrace();
			} catch (Exception error) {
				error.printStackTrace();
			}
		}

		SO.instancia().kill(this.getProcesso());
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
