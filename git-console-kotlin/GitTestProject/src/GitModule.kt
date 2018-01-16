/*
* @Author: c4dr01d
* @Classname: GitModule
* The template for Git Module for QPython
*/
import org.eclipse.jgit.api.CheckoutCommand
import org.eclipse.jgit.api.CreateBranchCommand
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.PullCommand
import org.eclipse.jgit.api.errors.CheckoutConflictException
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.internal.storage.file.FileRepository
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.jetbrains.annotations.Mutable
import java.io.File
import java.io.IOException
import java.util.logging.Logger

class GitModule : GitInterface {
    /*
    * Data Area,
    * remotePath: Remote git repository address
    * localPath: Repository at local store path
    * initPath: Local repository .git directory path
    * username: git username
    * password: git user password
    * commitMessage: Commit file message or comment
    */
    var remotePath: String? = null
    var localPath: String? = null
    var initPath: String? = null
    var username: String? = null
    var password: String? = null
    var commitMessage: String? = null
    /*
    * @Method: Clone
    * @TODO: Clone remote repository
    * @Throw: IOException,GitAPIException
    */
    @Throws(IOException::class, GitAPIException::class)
    override fun Clone(){
        val provider = UsernamePasswordCredentialsProvider(username,password)
        val clone = Git.cloneRepository()
        val git = clone.setURI(remotePath).setBranch("master").setDirectory(File(localPath!!)).setCredentialsProvider(provider).call()
        print(git.tag())
    }
    /*
    * @Method: Create
    * @TODO: Create local repository
    * @Throw: IOException
    */
    @Throws(IOException::class)
    override fun Create() {
        val newRepo = FileRepositoryBuilder.create(File(initPath!! + "/.git"))
        // TODO: if the repository directory have .git directory,do nothing,else,init repository
        if (File(initPath!! + "/.git").exists())
            print("\nThe repo is initialize.")
        else
            newRepo.create()
    }
    /*
    * @Method: Checkout
    * @TODO: Checkout branch
    * @Throw: GitAPIException
    */
    @Throws(GitAPIException::class)
    override fun Checkout(branch: String) {
        val git = Git(FileRepository(localPath!! + "/.git"))
        var checkout: CheckoutCommand = git.checkout().setCreateBranch(false).setName(branch)
        val refList: List<Ref> = git.branchList().call()
        if (!refList.stream().anyMatch { refList -> refList.name.replace("refs/heads/","").equals(branch) }) {
            checkout = checkout.setCreateBranch(true).setUpstreamMode(CreateBranchCommand.SetupUpstreamMode.TRACK).setStartPoint("origin/" + branch)
        }
        checkout.call()
    }
    /*
    * @Method: Add
    * @TODO: Add file to local repository
    * @Throw: IOException,GitAPIException
    */
    @Throws(IOException::class,GitAPIException::class)
    override fun Add() {
        val myFile = File(localPath!! + "/myfile.txt")
        myFile.createNewFile()
        val git = Git(FileRepository(localPath!! + "/.git"))
        git.add().addFilepattern("myFile").call()
    }
    /*
    * @Method: Commit
    * @TODO: Commit file to local repository
    * @Throw: IOException,GitAPIException,JGitInternalException
    */
    @Throws(IOException::class,GitAPIException::class, JGitInternalException::class)
    override fun Commit() {
        val git = Git(FileRepository(localPath!! + "/.git"))
        // TODO: Commit the file to local repository,here set commit message
        git.commit().setMessage(commitMessage).call()
    }
    /*
    * @Method: Pull
    * @TODO: Pull remote repository to localhost
    * @Throw: IOException,GitAPIException
    */
    @Throws(IOException::class,GitAPIException::class)
    override fun Pull() {
        val provider = UsernamePasswordCredentialsProvider(username,password)
        val git = Git(FileRepository(localPath!! + "/.git"))
        git.pull().setRemoteBranchName("master").setCredentialsProvider(provider).call()
    }
    /*
    * @Method: Push
    * @TODO: Push local repository to remote repository
    * @Throw: IOException,GitAPIException,JGitInternalException
    */
    @Throws(IOException::class,GitAPIException::class,JGitInternalException::class)
    override fun Push() {
        val provider = UsernamePasswordCredentialsProvider(username,password)
        val git = Git(FileRepository(localPath!! + "/.git"))
        git.push().setRemote("origin").setCredentialsProvider(provider).call()
    }
}