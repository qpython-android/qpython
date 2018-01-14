/*
* @Author: c4dr01d
* @Classname: GitInterface
* The interface for git
*/
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.JGitInternalException
import java.io.IOException
interface GitInterface {
    @Throws(IOException::class,GitAPIException::class)
    fun Clone()
    @Throws(IOException::class)
    fun Create()
    @Throws(IOException::class,GitAPIException::class)
    fun Add()
    @Throws(IOException::class,GitAPIException::class,JGitInternalException::class)
    fun Commit();
    @Throws(IOException::class,GitAPIException::class)
    fun Pull()
    @Throws(IOException::class,GitAPIException::class,JGitInternalException::class)
    fun Push()
}