import java.util.Scanner
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val test = GitTest()
        val scanner = Scanner(System.`in`)
        println("Please input the Git repository url: ")
        test.remotePath = scanner.next()
        println("Please input the local path: ")
        test.localPath = scanner.next()
        println("Please input the init path: ")
        test.initPath = scanner.next()
        try {
            test.TestClone()
            test.TestCreate()
            test.TestAdd()
            test.TestCommit()
            test.TestPush()
            test.TestPull()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
