package t2

object InsertionSort {
    fun sort(arr: IntArray?) {
        if (arr == null) {
            throw NullPointerException()
        }
        val n = arr.size
        for (i in 1 until n) {
            val key = arr[i]
            var j = i - 1
            while (j >= 0 && arr[j] > key) {
                arr[j + 1] = arr[j]
                j--
            }
            arr[j + 1] = key
        }
    }
}