import java.io.File

object Log {

  /**
    * function getSimpleLog
    * @param actual_directory : sgit repo
    * @return : all the commits of the branch
    */
  def getSimpleLog(actual_directory:File): List[String] ={

    def apply(parentTree:Option[String],logs:List[String]):List[String] ={
      if(parentTree.isDefined){
        val previous_commit = Commit.getCommitFileByName(parentTree.get,actual_directory)
        val commit_log = "commit "+previous_commit.getName+"\n"+previous_commit.getContent
        apply(previous_commit.getParentTree,logs:+commit_log)
      }
      else logs
    }
    val branch = Commit.getBranchToCommit(actual_directory)
    val last_commit = Commit.getCommitFileByName(Commit.getLastCommitFromBranch(branch,actual_directory),actual_directory)
    val last_commit_log = "commit "+last_commit.getName+"\n"+last_commit.getContent
    apply(last_commit.getParentTree,List[String](last_commit_log))
  }
}
