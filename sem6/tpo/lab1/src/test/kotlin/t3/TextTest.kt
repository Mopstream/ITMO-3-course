package t3

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class TextTest {

    private var ford: Person = Person("Ford", null)

    private val fordAction = Action(
        type = Type.AGGRESSIVE,
        description = "считать вслух",
        target = null
    )

    val theoreticAction = Action(
        type = Type.AGGRESSIVE,
        description = "медленно приближаться, повторяя \"умри умри умри\"",
        target = null
    )
    init {
        ford.doAction(fordAction, "comp")
    }

    @Test
    @DisplayName("Checking ford current action")
    fun testCurrAction() {
        assertEquals(ford.currentAction, fordAction)
    }

    @Test
    @DisplayName("Checking type of Ford action")
    fun testType() {
        assertEquals(ford.currentAction!!.type, Type.AGGRESSIVE)
    }

    @Test
    @DisplayName("Checking ford current action target")
    fun testTarget() {
        assertEquals(ford.currentAction!!.target, "comp")
    }

    @Test
    @DisplayName("Checking action equaity")
    fun testEquality() {
        assertEquals(fordAction, theoreticAction)
    }

}