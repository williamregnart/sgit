package commands

import java.io.File

object Log {


  /**
    * function getLog
    * @param actual_directory_path : sgit repo path
    * @param p : boolean, true if we want log -p, else false
    * @param stat : boolean, true if we want log --stat, else false
    * @return list of lines on the logs
    */
  def getLog(actual_directory_path:String, p:Boolean, stat:Boolean): List[String] ={

    @scala.annotation.tailrec
    def apply(parentTree:Option[String], logs:List[String]):List[String] ={
      //if it is not the first commit
      if(parentTree.isDefined){
        //get the previous commit
        val commit = Commit.getCommitFileByName(parentTree.get,actual_directory_path)
        val commit_log = List[String]("commit "+commit.getName)++commit.getLinesList

        //if log -p and it is not the first commit, we can add the diff add the differences between commit and previous commit
        if(p && commit.getParentTree.isDefined){
            val previous_commit = Commit.getCommitFileByName(commit.getParentTree.get,actual_directory_path)
            val diff = Diff.getDiffBetweenCommits(commit,previous_commit,actual_directory_path,stat = false)
            apply(commit.getParentTree,logs++commit_log++diff)
        }
        else if(stat && commit.getParentTree.isDefined){
          val previous_commit = Commit.getCommitFileByName(commit.getParentTree.get,actual_directory_path)
          val diff = Diff.getDiffBetweenCommits(commit,previous_commit,actual_directory_path,stat = true)
          apply(commit.getParentTree,logs++commit_log++diff)
        }
        else apply(commit.getParentTree,logs++commit_log)
      }
      else logs
    }
    //get the last commit
    val branch = Commit.getBranchToCommit(actual_directory_path)
    val last_commit = Commit.getCommitFileByName(Commit.getLastCommitFromBranch(branch,actual_directory_path),actual_directory_path)

    val last_commit_log = List[String]("commit "+last_commit.getName)++last_commit.getLinesList

    if((p || stat) && last_commit.getParentTree.isDefined){
      val previous_commit = Commit.getCommitFileByName(last_commit.getParentTree.get,actual_directory_path)
      val diff = Diff.getDiffBetweenCommits(last_commit,previous_commit,actual_directory_path,stat)
      apply(last_commit.getParentTree,last_commit_log++diff)
    }
    else apply(last_commit.getParentTree,last_commit_log)
  }

  def printLog(actual_directory_path:String,p:Boolean,stat:Boolean):Unit = {
    @scala.annotation.tailrec
    def apply(logs:List[String]):Boolean = {
      if(logs.isEmpty) true
      else{
        println(logs.head)
        apply(logs.tail)
      }
    }
    apply(getLog(actual_directory_path,p,stat))
  }


}
