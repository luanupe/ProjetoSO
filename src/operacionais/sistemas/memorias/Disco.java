package operacionais.sistemas.memorias;

/*
 * RAM e disco são semelhantes, então basta fazer uma e dar extends.
 * Escolhemos implementar a RAM e fazer o DISCO extends RAM para evitar repetição de código.
 */
public final class Disco extends RAM {

	public static final int PROCESSOS = 2;

	protected Disco() {
		super();
	}

}
