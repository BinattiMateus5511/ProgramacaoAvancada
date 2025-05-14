import framework.Controller
import framework.GetJson

fun main() {
    val app = GetJson(Controller::class)
    app.start(8080)
}
