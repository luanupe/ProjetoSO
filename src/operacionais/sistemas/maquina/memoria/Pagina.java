package operacionais.sistemas.maquina.memoria;

public final class Pagina {

	private int contador;
	private Integer valor;
	private boolean presente;
	private boolean modificado;
	private boolean referenciado;

	public Pagina() {
		this.setPresente(false);
		this.setModificado(false);
		this.setReferenciado(false);
		this.setValor(null);
	}

	public void clock() {
		++this.contador;
		if ((this.contador > 20)) {
			this.contador = 0;
			this.setReferenciado(false);
		}
	}

	public boolean isPresente() {
		return this.presente;
	}

	private void setPresente(boolean presente) {
		this.presente = presente;
	}

	public Integer getValor() {
		return this.valor;
	}

	private void setValor(Integer valor) {
		this.valor = valor;
	}

	public boolean isModificado() {
		return this.modificado;
	}

	private void setModificado(boolean modificado) {
		this.modificado = modificado;
	}

	public boolean isReferenciado() {
		return this.referenciado;
	}

	public void setReferenciado(boolean referenciado) {
		this.referenciado = referenciado;
	}

}
