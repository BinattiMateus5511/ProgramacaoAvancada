package jsonlib.test

import jsonlib.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Representa o estado civil de uma pessoa.
 */
enum class EstadoCivil { Solteiro, Casado }

/**
 * Classe de exemplo para testar inferência de data classes.
 */
data class Pessoa(
    val nome: String,
    val idade: Int,
    val altura: Double?,
    val ativo: Boolean,
    val estadoCivil: EstadoCivil
)

/**
 * Testes para a função inferJson.
 */
class JsonInferenceTest {

    /**
     * Testa tipos básicos: String, Int, Double, Boolean e null.
     */
    @Test
    fun testInferTiposSimples() {
        assertEquals(JSONString("Olá"), inferJson("Olá"))
        assertEquals(JSONNumber(42), inferJson(42))
        assertEquals(JSONNumber(3.14), inferJson(3.14))
        assertEquals(JSONBoolean(true), inferJson(true))
        assertEquals(JSONNull, inferJson(null))
    }

    /**
     * Testa inferência de listas com elementos mistos.
     */
    @Test
    fun testInferListas() {
        val list = listOf(1, "texto", null)
        val json = inferJson(list)
        assertTrue(json is JSONArray)
        assertEquals(3, (json as JSONArray).elements.size)
    }

    /**
     * Testa inferência de enums convertidos para string.
     */
    @Test
    fun testInferEnums() {
        val json = inferJson(EstadoCivil.Casado)
        assertEquals(JSONString("Casado"), json)
    }

    /**
     * Testa inferência de mapas com chaves String.
     */
    @Test
    fun testInferMapas() {
        val map = mapOf("nome" to "João", "idade" to 30)
        val json = inferJson(map)
        assertTrue(json is JSONObject)
        val obj = json as JSONObject
        assertEquals(JSONString("João"), obj.properties["nome"])
        assertEquals(JSONNumber(30), obj.properties["idade"])
    }

    /**
     * Testa inferência de data class com tipos diversos.
     */
    @Test
    fun testInferDataClass() {
        val pessoa = Pessoa("Ana", 28, 1.65, true, EstadoCivil.Solteiro)
        val json = inferJson(pessoa)
        assertTrue(json is JSONObject)
        val props = (json as JSONObject).properties
        assertEquals(JSONString("Ana"), props["nome"])
        assertEquals(JSONNumber(28), props["idade"])
        assertEquals(JSONNumber(1.65), props["altura"])
        assertEquals(JSONBoolean(true), props["ativo"])
        assertEquals(JSONString("Solteiro"), props["estadoCivil"])
    }

    /**
     * Testa estrutura completa aninhada: Map contendo lista de data classes.
     */
    @Test
    fun testInferCompleto() {
        val dados = mapOf(
            "alunos" to listOf(
                Pessoa("Miguel", 22, null, true, EstadoCivil.Solteiro),
                Pessoa("Inês", 23, 1.70, false, EstadoCivil.Casado)
            ),
            "curso" to "Engenharia Informática"
        )
        val json = inferJson(dados)
        assertTrue(json is JSONObject)
    }
}
