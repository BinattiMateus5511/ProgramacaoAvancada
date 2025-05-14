package framework

import framework.Mapping
import framework.Param
import framework.Path

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
