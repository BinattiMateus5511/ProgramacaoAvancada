package framework.test

import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals


/**
 * Classe de teste responsável por validar o comportamento da API REST.
 * Utiliza a biblioteca OkHttp para enviar requisições HTTP locais.
 */
class GetJsonTest {

    private val client = OkHttpClient()

    /**
     * Testa se o endpoint /api/ints retorna corretamente a lista [1,2,3].
     */
    @Test
    fun testIntsEndpoint() {
        val response = get("http://localhost:8080/api/ints")
        assertEquals("[1,2,3]", response)
    }

    /**
     * Testa se o endpoint /api/pair retorna corretamente um objeto JSON com duas strings.
     */
    @Test
    fun testPairEndpoint() {
        val response = get("http://localhost:8080/api/pair")
        assertEquals("""{"first":"um","second":"dois"}""", response)
    }

    /**
     * Testa se o endpoint /api/path/{valor} retorna o valor seguido de "!".
     */
    @Test
    fun testPathEndpoint() {
        val response = get("http://localhost:8080/api/path/abc")
        assertEquals("\"abc!\"", response)
    }

    /**
     * Testa se o endpoint /api/args retorna o mapa com a string repetida corretamente.
     */
    @Test
    fun testArgsEndpoint() {
        val response = get("http://localhost:8080/api/args?n=3&text=PA")
        assertEquals("""{"PA":"PAPAPA"}""", response)
    }

    /**
     * Envia uma requisição GET para a URL especificada e retorna o conteúdo da resposta como string.
     * @param url Endereço completo da requisição HTTP GET.
     * @return Corpo da resposta, ou null se não houver.
     */
    private fun get(url: String): String? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        return response.body?.string()
    }
}
