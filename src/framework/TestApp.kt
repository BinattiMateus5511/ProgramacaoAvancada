import framework.parseUrl
import framework.matchRoute

fun main() {
    val url = "/api/args?n=3&text=PA"
    val parsed = parseUrl(url)

    println("Segments: ${parsed.pathSegments}")
    println("Query Params: ${parsed.queryParams}")

}