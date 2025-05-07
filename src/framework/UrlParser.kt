package framework

/**
 * Representa o resultado do parsing de uma URL.
 *
 * @property pathSegments Lista de segmentos do caminho, separados por "/".
 * @property queryParams Mapa com os pares chave=valor da query string.
 */
data class ParsedUrl(
    val pathSegments: List<String>,
    val queryParams: Map<String, String>
)

/**
 * Função utilitária que analisa uma URL completa, separando os segmentos do caminho e os parâmetros da query string.
 *
 * Exemplo de entrada: "/api/args?n=3&text=PA"
 * Resultado:
 *   pathSegments = ["api", "args"]
 *   queryParams = {"n" to "3", "text" to "PA"}
 *
 * @param url A URL completa da requisição (path + query string).
 * @return Um objeto ParsedUrl contendo os segmentos e os parâmetros extraídos.
 */
fun parseUrl(url: String): ParsedUrl {
    val (path, query) = url.split("?", limit = 2).let {
        it.getOrElse(0) { "" } to it.getOrElse(1) { "" }
    }

    val pathSegments = path.trim('/')
        .split('/')
        .filter { it.isNotEmpty() }

    val queryParams = query.split('&')
        .filter { it.contains('=') }
        .associate {
            val (key, value) = it.split('=', limit = 2)
            key to value
        }

    return ParsedUrl(pathSegments, queryParams)
}