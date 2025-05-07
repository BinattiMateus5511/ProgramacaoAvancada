package framework

/**
 * Anotação para mapear classes ou métodos para uma rota HTTP específica.
 * Pode ser usada tanto em classes (para definir o prefixo) quanto em funções (para definir o endpoint).
 * @property value Caminho da URL a ser mapeado.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Mapping(val value: String)

/**
 * Anotação usada para indicar que um parâmetro da função deve ser obtido diretamente do caminho da URL.
 * Exemplo: /api/path/{variavel}
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Path

/**
 * Anotação usada para indicar que um parâmetro da função deve ser obtido da query string.
 * Exemplo: /api/args?n=2&text=olá
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class Param

/**
 * Classe controladora que representa um grupo de endpoints REST disponíveis sob o prefixo "/api".
 */
@Mapping("api")
class Controller {

    /**
     * Retorna uma lista de inteiros. Mapeado para GET /api/ints
     */
    @Mapping("ints")
    fun demo(): List<Int> = listOf(1, 2, 3)

    /**
     * Retorna um par de strings. Mapeado para GET /api/pair
     */
    @Mapping("pair")
    fun obj(): Pair<String, String> = Pair("um", "dois")

    /**
     * Retorna uma string baseada em um valor no caminho. Ex: /api/path/abc → "abc!"
     * @param pathvar valor capturado do caminho da URL.
     */
    @Mapping("path/{pathvar}")
    fun path(@Path pathvar: String): String = pathvar + "!"

    /**
     * Repete uma string n vezes e retorna um mapa com a string original como chave.
     * Ex: /api/args?n=2&text=oi → {"oi":"oioi"}
     * @param n número de repetições.
     * @param text texto a ser repetido.
     */
    @Mapping("args")
    fun args(@Param n: Int, @Param text: String): Map<String, String> =
        mapOf(text to text.repeat(n))
}
