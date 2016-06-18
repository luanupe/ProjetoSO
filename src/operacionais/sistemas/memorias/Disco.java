package operacionais.sistemas.memorias;

/*
 * RAM e disco s�o semelhantes, ent�o basta fazer uma e dar extends.
 * Escolhemos implementar a RAM e fazer o DISCO extends RAM para evitar repeti��o de c�digo.
 */
public final class Disco extends RAM {

	public static final int PROCESSOS = 2;

	protected Disco() {
		super();
	}

}
