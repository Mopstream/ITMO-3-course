package t3


data class Action(
    val type: Type,
    val description: String,
    var target: Any?
) {


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        other as Action
        return type == other.type
    }
}