package t3

class Person(val name: String, var currentAction: Action?) {

    fun doAction(action: Action, target: Any) {
        currentAction = action
        action.target = target
        println("$name applies ${action.description} to $target")
    }
}