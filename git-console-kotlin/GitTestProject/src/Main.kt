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
        println("Please input the local repository .git object path: ")
        gitHandler.initPath = scanner.next()
        try {
            gitHandler.Clone()
            gitHandler.Create()
            gitHandler.Add()
            println("Your git local repository is modified.")
            println("Please input the commit message: ")
            gitHandler.commitMessage = scanner.next()
            gitHandler.Commit()
            gitHandler.Push()
            println("Push success.")
            println("Now,The program try to pull the remote repository")
            gitHandler.Pull()
            println("The git module is tested successfully.")
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
