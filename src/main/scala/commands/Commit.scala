package commands

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

import files.{CommitHandler, FileHandler, IndexHandler}
import others.Encryption

object Commit{

  /**
    *function generateCommitContent
    * @param tree : tree to be commit
    * @param parent_tree : tree of last commit
    * @return the content the commit should have,
    *         format : tree <tree_name>
    *                  parentTree <<parentTree_name> or <None>>
    *                   <date>
    */
  def generateCommitContent(tree:String,parent_tree:Option[String],date:String,message:String):String={
    if(parent_tree.isDefined) "tree "+tree+"\n"+"parentTree "+parent_tree.get+"\n"+date+"\n "+message
    else "tree "+tree+"\n"+"parentTree None"+"\ndate "+date+"\n "+message
  }

  /**
    * function createCommitFile : create the commit file in .sgit/objects/commits
    * @param tree : tree to be commit
    * @param parent_tree : tree of last commit
    * @param date : date of today
    * @param actual_directory_path : sgit repo path
    */
  def createCommitFile(tree:String,parent_tree:Option[String],date:String,message:String,actual_directory_path:String):Unit={
    val content = generateCommitContent(tree,parent_tree,date,message)
    val commit_File = new FileHandler(new File(actual_directory_path+"/.sgit/objects/commits/"+Encryption.sha1(content)))
    commit_File.createFile()
    commit_File.addContent(content,appendContent = false)
  }

  /**
    * function addCommitToBranch : write in file .sgit/refs/heads/<actual_branch> the commit ref
    * @param branch : branch where we want to commit
    * @param commit : commit we want to add to branch
    * @param actual_directory_path : sgit repo path
    */
  def addCommitToBranch(branch:String,commit:String,actual_directory_path:String):Unit={
    val branch_File = new FileHandler(new File(actual_directory_path+"/.sgit/refs/heads/" + branch))
    branch_File.addContent(commit,appendContent = false)
  }

  /**
    * function getBranchToCommit
    * @param actual_directory_path : sgit repo path
    * @return the actual branch, specified in file .sgit/HEAD
    */
  def getBranchToCommit(actual_directory_path:String):String={
    val head_file = new FileHandler(new File(actual_directory_path+"/.sgit/HEAD"))
    head_file.getContent.replace("\n","")
  }

  /**
    * function getLastCommitFromBranch
    * @param branch : the branch we want the last commit
    * @param actual_directory_path : sgit repo path
    * @return the commit ref written in file sgit/refs/heads/<branch>
    */
  def getLastCommitFromBranch(branch:String,actual_directory_path:String):String={
    val branch_File = new FileHandler(new File(actual_directory_path+"/.sgit/refs/heads/"+branch))
    branch_File.getContent.replace("\n","")
  }

  def getCommitFileByName(commit_name:String, actual_directory_path:String):CommitHandler={
    new CommitHandler(new File(actual_directory_path+"/.sgit/objects/commits/"+commit_name))
  }

  /**
    * function getCommit
    * @param commit_hash : the commit_name we want
    * @param actual_directory_path : sgit repo path
    * @return the commit files.FileHandler, which has path .sgit/objects/commits/<commit_hash>
    */
  def getCommit(commit_hash:String, actual_directory_path:String):CommitHandler={
    new CommitHandler(new File(actual_directory_path+"/.sgit/objects/commits/"+commit_hash))
  }

  /**
    * function getDateToday
    * @return the string of date of today with format dd-MM-yyyy-hh-mm-ss
    */
  def getDateToday:String = {
    val sdf = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss")
    sdf.format(new Date)
  }

  /**
    * function commit : create the tree from index, create the commit from it and commit (change commit ref in the actual branch)
    * @param actual_directory_path : sgit repo
    */
  def commit(actual_directory_path:String,message:String):Unit={
    val index_file = new IndexHandler(new File(actual_directory_path+"/.sgit/INDEX"))
    val tree = index_file.getTree("",actual_directory_path)
    val branch = getBranchToCommit(actual_directory_path)
    val date = getDateToday

    //if first commit, create commit file without parent tree and add the commit to branch
    if(getLastCommitFromBranch(branch,actual_directory_path)==""){
      createCommitFile(tree,None,date,message,actual_directory_path)
      addCommitToBranch(branch,Encryption.sha1(generateCommitContent(tree,None,date,message)),actual_directory_path)
    }
      //else, create commit file with last commit ref as parent tree and add the commit to branch
    else {
      if (getCommitFileByName(getLastCommitFromBranch(branch,actual_directory_path), actual_directory_path).getTree != tree) {
        createCommitFile(tree, Some(getLastCommitFromBranch(branch, actual_directory_path)), date, message, actual_directory_path)
        addCommitToBranch(branch, Encryption.sha1(generateCommitContent(tree, Some(getLastCommitFromBranch(branch, actual_directory_path)), date,message)), actual_directory_path)

      }
    }

  }
}
