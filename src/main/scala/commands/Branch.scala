package commands

import java.io.File

import files.{DirectoryHandler, FileHandler}

object Branch {

  /**
    * function createBranch : create the new branch and add the last commit of actual branch in it content
    * @param new_branch_name : new_branch name
    * @param last_commit : actual_branch last commit
    * @param actual_directory_path : sgit repo
    */
  def createBranch(new_branch_name:String, last_commit:String, actual_directory_path:String):Unit = {



    //create the branch file and add the last commit of actual branch in it
    val new_branch_file = new FileHandler(new File(actual_directory_path+"/.sgit/refs/heads/"+new_branch_name))
    new_branch_file.createFile()
    new_branch_file.addContent(last_commit,appendContent = false)
  }

  /**
    * function executeBranchCommand : if not exists or called "detached", create a branch with the last commit of actual branch as last commit
    * @param new_branch_name : the name of the new branch to create
    * @param actual_directory_path : sgit repo
    */
  def executeBranchCommand(new_branch_name:String, actual_directory_path:String):Unit = {

    val new_branch_file = new FileHandler(new File(actual_directory_path+"/.sgit/refs/heads/"+new_branch_name))

    //if branch already exists
    if(new_branch_file.existFile())  println(Console.RED+"ERROR : branch "+new_branch_name+" already exists!")

    //if new branch name is detached, forbid the creation, detached branch is a special branch for checkout on commit
    else if(new_branch_name=="detached") println(Console.RED+"ERROR : create a branch \"detached\" is forbidden!")

    else{
      val head_file = new FileHandler(new File(actual_directory_path+"/.sgit/HEAD"))
      val actual_branch_name = head_file.getContent.replace("\n","")
      if(actual_branch_name!=""){
        val actual_branch_file = new FileHandler(new File(actual_directory_path+"/.sgit/refs/heads/"+actual_branch_name))
        //get the last commit of actual branch
        val last_commit = actual_branch_file.getContent.replace("\n","")
        if(last_commit!=""){
          createBranch(new_branch_name,last_commit,actual_directory_path)
          println("branch "+new_branch_name+" has been created with success (use \"sgit checkout "+new_branch_name+"\" to go on this branch)")
        }
        else println(Console.RED+"ERROR : You need to make first commit before creating a new branch"+Console.WHITE)
      }
    }
  }

  /**
    * function getActualBranch
    * @param actual_directory_path : sgit repo
    * @return the actual branch_name which is referenced in HEAD file
    */
  def getActualBranch(actual_directory_path:String):String = {
    val head_file = new FileHandler(new File(actual_directory_path+"/.sgit/HEAD"))
    head_file.getContent.replace("\n","")
  }

  def getAllBranches(actual_directory_path : String):List[String] = {
    val branches_directory = new DirectoryHandler(new File(actual_directory_path+"/.sgit/refs/heads"))
    branches_directory.getFilesName
  }

  @scala.annotation.tailrec
  def printAllBranches(branches : List[String]):Unit = {
    if(branches.nonEmpty){
      println(branches.head)
      printAllBranches(branches.tail)
    }
  }
}
