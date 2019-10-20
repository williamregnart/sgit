package commands

import java.io.File

import files.{CommitHandler, DirectoryHandler, FileHandler, TreeHandler}

object CheckOut {

  /**
    * function changeBranch : go on the repo of branch in param
    * @param branch_name : branch where we go
    * @param actual_directory_path : sgit repo
    */
  def changeBranch(branch_name:String, actual_directory_path:String):Unit = {

    //overwrite on HEAD file the actual branch name by branch name where we checkout
    val head_file = new FileHandler(new File(actual_directory_path + "/.sgit/HEAD"))
    head_file.addContent(branch_name, appendContent = false)

    //get the commit of the branch where we checkout
    val branch_file = new FileHandler(new File(actual_directory_path + "/.sgit/refs/heads/" + branch_name))
    val last_commit_name = branch_file.getContent.replace("\n", "")
    //if commit exists
    if(last_commit_name!="") {
      val last_commit_file = new CommitHandler(new File(actual_directory_path + "/.sgit/objects/commits/" + last_commit_name))

      //get the tree of this commit
      val branch_tree_name = last_commit_file.getTree
      val branch_tree_file = new TreeHandler(new File(actual_directory_path + "/.sgit/objects/trees/" + branch_tree_name))

      //delete and recreate the repository from tree
      val actual_directory_path_handler = new DirectoryHandler(new File(actual_directory_path))
      actual_directory_path_handler.deleteFilesExcept(List[String](".sgit"))
      branch_tree_file.createDirectoryFromTree(actual_directory_path, actual_directory_path)

      //overwrite the index with the index of branch tree
      val index_branch_content = branch_tree_file.getIndex("", actual_directory_path)
      val index_file = new FileHandler(new File(actual_directory_path + "/.sgit/INDEX"))
      index_file.addContent(index_branch_content, appendContent = false)
    }
    else{
      val index_file = new FileHandler(new File(actual_directory_path + "/.sgit/INDEX"))
      index_file.cleanContent()
    }
  }

  /**
    * function executeCheckOutCommand : go on the repo of branch/commit in param
    * @param input : branch/tag/commit where we want to go
    * @param actual_directory_path : sgit repo
    */
  def executeCheckOutCommand(input:String,actual_directory_path:String):Unit = {
    val opt_input_type_and_ref = getTypeAndRefByInput(input,actual_directory_path)
    val last_commit = Commit.getLastCommitFromBranch(Branch.getActualBranch(actual_directory_path),actual_directory_path)

    //if the input does not match a branch, commit or tag print error
    if(opt_input_type_and_ref.isEmpty) println(Console.RED+"ERROR : branch/tag/commit " + input + " doesn't exist")

    //if files have been uncommitted (and if a commit exists), forbid the change

    else if(last_commit!="") if(Status.printUncommittedFiles(actual_directory_path)) println(Console.YELLOW+"Please commit files before checkout")

    else{
      //the checkout will be done, if we are on detached branch, we will delete it
      val detached_branch = new FileHandler(new File(actual_directory_path+"/.sgit/refs/heads/detached"))
      if(detached_branch.existFile()) detached_branch.deleteFile()

      val input_type_and_ref = opt_input_type_and_ref.get
      //i input is a branch name
      if(input_type_and_ref._1=="branch"){
        changeBranch(input_type_and_ref._2,actual_directory_path)
      }
      //if input is a commit
      else{
        //create a detached branch with the commit as ref and switch on this branch
        detached_branch.createFile()
        detached_branch.addContent(input_type_and_ref._2,appendContent = false)
        changeBranch("detached",actual_directory_path)
      }
    }
    print(Console.WHITE+"You are on branch "+ Branch.getActualBranch(actual_directory_path))
  }

  /**
    * function getTypeAndRefByInput
    * @param input : branch/commit/tag in input for checkout
    * @param actual_directory_path : sgit repo
    * @return the input type (branch/commit/tag) with it ref (in an Option), for tag, it is the ref of commit of the tag
    *         if input is not a branch/commit/tag, return None
    */
  def getTypeAndRefByInput(input:String,actual_directory_path:String):Option[(String,String)] = {
    val branch_file = new File(actual_directory_path+"/.sgit/refs/heads/"+input)
    if(branch_file.exists()) Option(("branch", input))
    else{
      val tag_file = new FileHandler(new File(actual_directory_path+"/.sgit/refs/tags/"+input))
      if(tag_file.existFile()) Option(("commit", tag_file.getContent.replace("\n", "")))

      else{
        val commit_file = new CommitHandler(new File(actual_directory_path+"/.sgit/objects/commits/"+input))
        if(commit_file.existFile()) Option(("commit", commit_file.getName))

        else{
          None
        }
      }
    }
  }
}
