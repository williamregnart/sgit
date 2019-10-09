import java.io.File

object Commi{

  val actualDirectory = new File(System.getProperty("user.dir"))
  val gitPath = actualDirectory.getPath+"/.sgit"
  val commitsPath=gitPath+"/objects/commits"
  val branchPath=gitPath+"refs/heads"

  def getCommitContent(tree:String,parent_tree:Option[String]):String={
    if(parent_tree.isDefined) "tree "+tree+"\n"+"parentTree "+parent_tree.get
    else "tree "+tree+"\n"+"parentTree None"
  }

  def createCommitFile(tree:String,parent_tree:Option[String]):Unit={
    val content = getCommitContent(tree,parent_tree)
    val commit_File = FileHandler(new File(commitsPath+"/"+Encryption.sha1(content)))
    commit_File.createFile()
    commit_File.addContent(content,appendContent = false)
  }

  def addCommitToBranch(branch:String,commit:String):Unit={
    val branch_File = FileHandler(new File(branchPath + "/" + branch))
    branch_File.addContent(commit,appendContent = false)
  }

  def getBranchToCommit:String={
    val head_file = FileHandler(new File(gitPath+"/HEAD"))
    head_file.getContent
  }
  def getLastCommitFromBranch(branch:String):String={
    val branch_File = FileHandler(new File(branchPath + "/" + branch))
    branch_File.getContent
  }

  def getCommit(commit_hash:String):FileHandler={
    FileHandler(new File(commitsPath+"/"+commit_hash))
  }

  def commit(branch:String):Unit={
    val tree = Tree.getTree("")

    if(getLastCommitFromBranch(branch)==""){
      createCommitFile(tree,None)
      addCommitToBranch(branch,Encryption.sha1(getCommitContent(tree,None)))
    }
    else{
      createCommitFile(tree,Some(getLastCommitFromBranch(branch)))
      addCommitToBranch(branch,Encryption.sha1(getCommitContent(tree,Some(getLastCommitFromBranch(branch)))))
    }

  }
}
