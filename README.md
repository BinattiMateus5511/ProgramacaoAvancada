# JSONLib

Uma biblioteca em Kotlin para modelar e manipular dados JSON em memória, desenvolvida para a disciplina de Programação Avançada do Mestrado em Engenharia Informática do ISCTE - 2024/2025.

## Funcionalidades

- Criação manual de objetos e arrays JSON
- Tipos suportados: String, Number, Boolean, Null, Array, Object
- Serialização para string JSON compatível com o padrão
- Operações `map` e `filter` para `JSONArray` e `JSONObject`
- Suporte ao padrão Visitor para validações estruturais
- Inferência automática de estruturas JSON a partir de objetos Kotlin (com reflexão)
- Micro-framework HTTP com rotas via anotações (`@Mapping`, `@Param`, `@Path`)

## Tutorial de Uso

### 1. Criação manual de JSON (Fase 1)

```kotlin
val pessoa = JSONObject(
    mapOf(
        "nome" to JSONString("Ana"),
        "idade" to JSONNumber(30),
        "ativo" to JSONBoolean(true),
        "skills" to JSONArray(
            listOf(JSONString("Kotlin"), JSONString("Testes"))
        )
    )
)

println(pessoa.toJSONString())
// Saída: {"nome":"Ana","idade":30,"ativo":true,"skills":["Kotlin","Testes"]}
```

### 2. Inferência automática com reflexão (Fase 2)

```kotlin
data class Pessoa(val nome: String, val idade: Int)

val dados = mapOf(
    "curso" to "Engenharia Informática",
    "alunos" to listOf(Pessoa("João", 20), Pessoa("Inês", 21))
)

val json = inferJson(dados)
println(json.toJSONString())

// Saída esperada:
// {"curso":"Engenharia Informática","alunos":[{"nome":"João","idade":20},{"nome":"Inês","idade":21}]}
```

### 3. Servidor HTTP com rotas via anotação (Fase 3)

```kotlin
@Mapping("api")
class Controller {
    @Mapping("hello")
    fun hello(): String = "Olá, mundo!"
}

fun main() {
    val app = GetJson(Controller::class)
    app.start(8080)
}
```

Após executar `main()`, acesse:
- `http://localhost:8080/api/hello`
- Resposta: `"Olá, mundo!"`

## Licença

Uso educacional exclusivo para o ISCTE — Mestrado em Engenharia Informática (2024/2025).

