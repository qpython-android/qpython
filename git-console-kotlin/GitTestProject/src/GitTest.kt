import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.junit.Test
import java.io.File
import java.io.IOException
// Modified UsernamePasswordCredentialsProvider Constructors string to apply git function is work.
class GitTest {
    var remotePath: String? = null
    var localPath: String? = null
    var initPath: String? = null
    @Test
    @Throws(IOException::class, GitAPIException::class)
    fun TestClone() {
        val usernamePasswordCredentialsProvider = UsernamePasswordCredentialsProvider("username", "password")
        val cloneCommand = Git.cloneRepository()
        val git = cloneCommand.setURI(remotePath).setBranch("master").setDirectory(File(localPath!!)).setCredentialsProvider(usernamePasswordCredentialsProvider).call()
        print(git.tag())
    }

    @Test
    @Throws(IOException::class)
    fun TestCreate() {
        val newRepo = FileRepositoryBuilder.create(File(initPath!! + "/.git"))
        newRepo.create()
    }

    @Test
    @Throws(IOException::class, GitAPIException::class)
    fun TestAdd() {
        val myFile = File(localPath!! + "/myfile.txt")
        myFile.createNewFile()
        val git = Git(FileRepository(localPath!! + "/.git"))
        git.add().addFilepattern("myFile").call()
    }

    @Test
    @Throws(IOException::class, GitAPIException::class, JGitInternalException::class)
    fun TestCommit() {
        val git = Git(FileRepository(localPath!! + "/.git"))
        git.commit().setMessage("Test Kotlin version").call()
    }

    @Test
    @Throws(IOException::class, GitAPIException::class)
    fun TestPull() {
        val usernamePasswordCredentialsProvider = UsernamePasswordCredentialsProvider("username", "password")
        val git = Git(FileRepository(localPath!! + "/.git"))
        git.pull().setRemoteBranchName("master").setCredentialsProvider(usernamePasswordCredentialsProvider).call()
    }

    @Test
    @Throws(IOException::class, GitAPIException::class, JGitInternalException::class)
    fun TestPush() {
        val usernamePasswordCredentialsProvider = UsernamePasswordCredentialsProvider("username", "password")
        val git = Git(FileRepository(localPath!! + "/.git"))
        git.push().setRemote("origin").setCredentialsProvider(usernamePasswordCredentialsProvider).call()
    }
}
