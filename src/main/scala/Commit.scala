import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

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
  def generateCommitContent(tree:String,parent_tree:Option[String],date:String):String={
    if(parent_tree.isDefined) "tree "+tree+"\n"+"parentTree "+parent_tree.get+"\n"+date
    else "tree "+tree+"\n"+"parentTree None"+"\ndate "+date
  }

  /**
    * function createCommitFile : create the commit file in .sgit/objects/commits
    * @param tree : tree to be commit
    * @param parent_tree : tree of last commit
    * @param date : date of today
    * @param actual_directory : sgit repo
    */
  def createCommitFile(tree:String,parent_tree:Option[String],date:String,actual_directory:File):Unit={
    val content = generateCommitContent(tree,parent_tree,date)
    val commit_File = FileHandler(new File(actual_directory.getPath+"/.sgit/objects/commits/"+Encryption.sha1(content)))
    commit_File.createFile()
    commit_File.addContent(content,appendContent = false)
  }


  /**
    * function addCommitToBranch : write in file .sgit/refs/heads/<actual_branch> the commit ref
    * @param branch : branch where we want to commit
    * @param commit : commit we want to add to branch
    * @param actual_directory : sgit repo
    */
  def addCommitToBranch(branch:String,commit:String,actual_directory:File):Unit={
    val branch_File = FileHandler(new File(actual_directory.getPath+"/.sgit/refs/heads/" + branch))
    branch_File.addContent(commit,appendContent = false)
  }

  /**
    * function getBranchToCommit
    * @param actual_directory : sgit repo
    * @return the actual branch, specified in file .sgit/HEAD
    */
  def getBranchToCommit(actual_directory:File):String={
    val head_file = FileHandler(new File(actual_directory.getPath+"/.sgit/HEAD"))
    head_file.getContent.replace("\n","")
  }

  /**
    * function getLastCommitFromBranch
    * @param branch : the branch we want the last commit
    * @param actual_directory : sgit repo
    * @return the commit ref written in file sgit/refs/heads/<branch>
    */
  def getLastCommitFromBranch(branch:String,actual_directory:File):String={
    val branch_File = FileHandler(new File(actual_directory.getPath+"/.sgit/refs/heads/"+branch))
    branch_File.getContent.replace("\n","")
  }

  def getCommitFileByName(commit_name:String,actual_directory:File):CommitHandler={
    new CommitHandler(new File(actual_directory.getPath+"/.sgit/objects/commits/"+commit_name))
  }

  /**
    * function getCommit
    * @param commit_hash : the commit_name we want
    * @param actual_directory : sgit repo
    * @return the commit FileHandler, which has path .sgit/objects/commits/<commit_hash>
    */
  def getCommit(commit_hash:String,actual_directory:File):CommitHandler={
    new CommitHandler(new File(actual_directory.getPath+"/.sgit/objects/commits/"+commit_hash))
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
    * @param actual_directory : sgit repo
    */
  def commit(actual_directory:File):Unit={
    val index_file = new IndexHandler(new File(actual_directory.getPath+"/.sgit/INDEX"))
    val tree = index_file.getTree("",actual_directory)
    val branch = getBranchToCommit(actual_directory)
    val date = getDateToday

    //if first commit, create commit file without parent tree and add the commit to branch
    if(getLastCommitFromBranch(branch,actual_directory)==""){
      createCommitFile(tree,None,date,actual_directory)
      addCommitToBranch(branch,Encryption.sha1(generateCommitContent(tree,None,date)),actual_directory)
    }
      //else, create commit file with last commit ref as parent tree and add the commit to branch
    else {
      if (getCommitFileByName(getLastCommitFromBranch(branch,actual_directory), actual_directory).getTree != tree) {
        createCommitFile(tree, Some(getLastCommitFromBranch(branch, actual_directory)), date, actual_directory)
        addCommitToBranch(branch, Encryption.sha1(generateCommitContent(tree, Some(getLastCommitFromBranch(branch, actual_directory)), date)), actual_directory)

      }
    }

  }
}
