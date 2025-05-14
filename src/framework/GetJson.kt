package framework

/**
 * Framework minimalista que simula um roteador HTTP GET semelhante ao Spring Boot.
 * Desenvolvido exclusivamente com recursos da linguagem Kotlin e da JDK padrão,
 * conforme os requisitos da Fase 3 do projeto.
 *
 * Requisitos atendidos:
 * - Permite mapear controladores e métodos via anotações personalizadas (@Mapping, @Param, @Path)
 * - Inicia um servidor HTTP local usando com.sun.net.httpserver.HttpServer (JDK padrão)
 * - Realiza o roteamento de requisições HTTP GET para os métodos Kotlin corretos
 * - Converte os argumentos de URL (path/query) para os tipos Kotlin esperados
 * - Usa inferJson (da Fase 2) para converter o retorno em JSON
 * - Retorna a resposta ao cliente como JSON puro
 *
 * Nenhuma biblioteca externa foi utilizada, exceto JUnit e OkHttp (exclusivamente em testes).
 */

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import jsonlib.JSONValue
import jsonlib.JSONObject
import jsonlib.JSONArray
import jsonlib.inferJson
import java.io.OutputStream
import java.net.InetSocketAddress
import kotlin.reflect.KClass
import kotlin.reflect.full.*

/**
 * Classe principal do framework GetJson.
 * Recebe uma ou mais classes controladoras e expõe seus métodos anotados como endpoints HTTP GET.
 *
 * @param controllers Lista de classes (via reflexão) que contêm os métodos anotados com @Mapping.
 */
class GetJson(vararg controllers: KClass<*>) {

    private val controllerInstances: List<Any> = controllers.map { it.createInstance() }

    /**
     * Inicia o servidor HTTP na porta especificada.
     * @param port Porta TCP onde o servidor deve escutar.
     */
    fun start(port: Int) {
        val server = HttpServer.create(InetSocketAddress(port), 0)

        server.createContext("/") { exchange ->
            handleRequest(exchange)
        }

        println("Servidor iniciado em http://localhost:$port")
        server.start()
    }

    /**
     * Lida com cada requisição HTTP recebida.
     * Interpreta o path, resolve o controlador e método correto, executa a função e retorna JSON.
     */
    private fun handleRequest(exchange: HttpExchange) {
        try {
            val requestMethod = exchange.requestMethod
            if (requestMethod != "GET") {
                sendResponse(exchange, 405, "Método não permitido")
                return
            }

            val path = exchange.requestURI.path
            val parsed = parseUrl(exchange.requestURI.toString())

            println("👉 Requisição recebida: $path")

            for (controller in controllerInstances) {
                println("🔎 Verificando controller: ${controller::class.simpleName}")

                val match = matchRoute(controller, path) ?: continue

                println("✅ Função encontrada: ${match.function.name}")

                val args = resolveArguments(match.function, parsed, match.pathVariables)
                println("🧩 Argumentos resolvidos: $args")
                val result = match.function.call(controller, *args.toTypedArray())

                val json: JSONValue = inferJson(result)
                sendResponse(exchange, 200, json.toJSONString())
                return
            }

            sendResponse(exchange, 404, "Rota nao encontrada")

        } catch (e: Exception) {
            sendResponse(exchange, 500, "Erro interno: ${e.message}")
        }
    }

    /**
     * Constrói a lista de argumentos corretos para passar ao metodo a partir da URL.
     * Trata parâmetros com anotações @Path e @Param, convertendo-os para os tipos esperados.
     */
    private fun resolveArguments(
        function: kotlin.reflect.KFunction<*>,
        parsed: ParsedUrl,
        pathVariables: Map<String, String>
    ): List<Any?> {
        return function.parameters
            .filter { it.kind.name == "VALUE" }
            .map { param ->
                val name = param.name ?: return@map null
                val pathAnn = param.findAnnotation<Path>()
                val paramAnn = param.findAnnotation<Param>()

                val value: String? = when {
                    pathAnn != null -> pathVariables[name]
                    paramAnn != null -> parsed.queryParams[name]
                    else -> null
                }

                convertToType(value, param.type.classifier as? KClass<*>)
            }
    }

    /**
     * Converte uma string recebida de URL para o tipo Kotlin apropriado (ex: Int, Boolean).
     */
    private fun convertToType(value: String?, type: KClass<*>?): Any? {
        return when (type) {
            String::class -> value
            Int::class -> value?.toIntOrNull()
            Double::class -> value?.toDoubleOrNull()
            Boolean::class -> value?.toBooleanStrictOrNull()
            else -> null
        }
    }

    /**
     * Envia a resposta HTTP com o código de status e corpo especificado.
     */
    private fun sendResponse(exchange: HttpExchange, status: Int, body: String) {
        exchange.sendResponseHeaders(status, body.toByteArray().size.toLong())
        val os: OutputStream = exchange.responseBody
        os.write(body.toByteArray())
        os.close()
    }
}
