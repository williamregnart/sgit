package commands

import java.io.File

object Log {

  /**
    * function getSimpleLog
    * @param actual_directory : sgit repo
    * @return : all the commits of the branch
    */
  def getLog(actual_directory:File,p:Boolean,stat:Boolean): List[String] ={

    @scala.annotation.tailrec
    def apply(parentTree:Option[String], logs:List[String]):List[String] ={
      //if it is not the first commit
      if(parentTree.isDefined){
        //get the previous commit
        val commit = Commit.getCommitFileByName(parentTree.get,actual_directory)
        val commit_log = List[String]("commit "+commit.getName)++commit.getLinesList

        //if log -p and it is not the first commit, we can add the diff add the differences between commit and previous commit
        if(p && commit.getParentTree.isDefined){
            val previous_commit = Commit.getCommitFileByName(commit.getParentTree.get,actual_directory)
            val diff = Diff.getDiffBetweenCommits(commit,previous_commit,actual_directory,stat = false)
            apply(commit.getParentTree,logs++commit_log++diff)
        }
        else if(stat && commit.getParentTree.isDefined){
          val previous_commit = Commit.getCommitFileByName(commit.getParentTree.get,actual_directory)
          val diff = Diff.getDiffBetweenCommits(commit,previous_commit,actual_directory,stat = true)
          apply(commit.getParentTree,logs++commit_log++diff)
        }
        else apply(commit.getParentTree,logs++commit_log)
      }
      else logs
    }
    //get the last commit
    val branch = Commit.getBranchToCommit(actual_directory)
    val last_commit = Commit.getCommitFileByName(Commit.getLastCommitFromBranch(branch,actual_directory),actual_directory)

    val last_commit_log = List[String]("commit "+last_commit.getName)++last_commit.getLinesList

    if((p || stat) && last_commit.getParentTree.isDefined){
      val previous_commit = Commit.getCommitFileByName(last_commit.getParentTree.get,actual_directory)
      val diff = Diff.getDiffBetweenCommits(last_commit,previous_commit,actual_directory,stat)
      apply(last_commit.getParentTree,last_commit_log++diff)
    }
    else apply(last_commit.getParentTree,last_commit_log)
  }

  def printLog(actual_repository:File,p:Boolean,stat:Boolean):Unit = {
    def apply(logs:List[String]):Boolean = {
      if(logs.isEmpty) true
      else{
        println(logs.head)
        apply(logs.tail)
      }
    }
    apply(getLog(actual_repository,p,stat))
  }


}
