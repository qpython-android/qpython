/*
* @Author: c4dr01d
* @Classname: Main
* Test program for class GitInterface and GitModule
*/
import java.util.Scanner
object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val gitHandler = GitModule()
        val scanner = Scanner(System.`in`)
        println("Git login: ")
        gitHandler.username = scanner.next()
        println("Password: ")
        gitHandler.password = scanner.next()
        println("Please input the Git remote repository url: ")
        gitHandler.remotePath = scanner.next()
        println("Please input the Local repository path: ")
        gitHandler.localPath = scanner.next()
        gitHandler.initPath = gitHandler.localPath
        try {
            gitHandler.Clone()
            gitHandler.Create()
            gitHandler.Add()
            println("\nYour git local repository is modified.")
            println("Please input the commit message: ")
            gitHandler.commitMessage = scanner.next()
            gitHandler.Commit()
            gitHandler.Push()
            println("\nPush success.")
            println("\nNow,The program try to pull the remote repository")
            gitHandler.Pull()
            println("\nCheckout new remote branch.");
            gitHandler.Checkout("test")
            println("\nCheckout master branch.")
            gitHandler.Checkout("master")
            println("\nThe git module is tested successfully.")
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
