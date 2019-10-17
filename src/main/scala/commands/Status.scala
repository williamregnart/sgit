package commands

import java.io.File

import files._

object Status {

  /**
    * function getNewFilesUntracked
    * @param repo_files : paths of files in repo
    * @param index_files : paths of files in index
    * @return path of file which are in repo but not in index
    */
  def getNewFilesUntracked(repo_files:List[String], index_files:List[String]):List[String]={

    @scala.annotation.tailrec
    def apply(files_in_repo:List[String], result:List[String]):List[String]={
      if(files_in_repo.isEmpty) result

      //if file is not in index, add it to result
      else if(!index_files.contains(files_in_repo.head)) apply(files_in_repo.tail,result:+files_in_repo.head)

      else apply(files_in_repo.tail,result)
    }
    apply(repo_files,List[String]())
  }

  /**
    * function getFilesDeletedUntracked
    * @param repo_files : paths of files in repo
    * @param index_files : paths of files in index
    * @return path of file which are in index but not in repo
    */
  def getFilesDeletedUntracked(repo_files:List[String], index_files:List[String]):List[String]={

    @scala.annotation.tailrec
    def apply(files_in_index:List[String], result:List[String]):List[String]={
      if(files_in_index.isEmpty) result
      //if file is not in repo
      else if(!repo_files.contains(files_in_index.head)) apply(files_in_index.tail,result:+files_in_index.head)
      else apply(files_in_index.tail,result)
    }
    apply(index_files,List[String]())
  }

  /**
    * function getFilesModifiedUntracked
    * @param repo_files : paths of files in repo
    * @param index_files_blobs : paths of files in index with their blob (content hashed)
    * @param actual_repo : sgit repo
    * @return path of file which are in index and repo but with different blobs
    */
  def getFilesModifiedUntracked(repo_files:List[String], index_files_blobs:Map[String,String], actual_repo:File):List[String]={

    @scala.annotation.tailrec
    def apply(files_in_repo:List[String], result:List[String]):List[String]={
      if(files_in_repo.isEmpty) result
      else{
        val file_in_repo = new FileHandler(new File(actual_repo.getPath+"/"+files_in_repo.head))
        val blob_file_index = index_files_blobs.get(files_in_repo.head)

        //if file is in repo and index but his hashed content is different, add it to result
        if(blob_file_index.isDefined){
          if(blob_file_index.get != file_in_repo.getUniqueKey) apply(files_in_repo.tail,result:+files_in_repo.head)
          else apply(files_in_repo.tail,result)
        }
        else apply(files_in_repo.tail,result)
      }
    }
    apply(repo_files,List[String]())
  }

  /**
    * function printElements
    * @param files : files list to print
    * @param status : status of file (added/deleted/modified)
    * @param added : true if files added and color will be green, else false and color will be red
    * @return true
    */
  def printElements(files:List[String],status:String,added:Boolean):Boolean = {
    if(files.isEmpty) true
    else{
      val to_print = status+" : "+files.head

      if(added) println(Console.GREEN + to_print)

      else println(Console.RED + to_print)
      printElements(files.tail,status,added)
    }
  }

  /**
    * function printUntrackedFiles
    * @param actual_directory : sgit repo
    */
  def printUntrackedFiles(actual_directory:File):Unit = {

    val directory = new DirectoryHandler(actual_directory)
    val repo_files = directory.getAllFilesPath
    val index_file = new IndexHandler(new File(actual_directory.getPath+"/.sgit/INDEX"))
    val index_files_paths = index_file.getAllFilesPath
    val index_files_blobs = index_file.getPathAndBlob

    val files_added_untracked = getNewFilesUntracked(repo_files,index_files_paths)
    val files_deleted_untracked = getFilesDeletedUntracked(repo_files,index_files_paths)
    val files_modified_untracked = getFilesModifiedUntracked(repo_files,index_files_blobs,actual_directory)

    //if there are untracked files deleted or modified
    if(files_deleted_untracked.nonEmpty || files_modified_untracked.nonEmpty){
      println("Changes not staged for commit (use \"sgit add/remove <file>...\" to update what will be committed)")
      printElements(files_modified_untracked,"modified",added = false)
      printElements(files_deleted_untracked,"deleted",added = false)
      print(Console.WHITE)
     }

    //if there are untracked files added
    if(files_added_untracked.nonEmpty){
      println("Untracked files: (use \"sgit add <file>...\" to include in what will be committed)")
      printElements(files_added_untracked,"new file",added = false)
      print(Console.WHITE)
    }
  }

  /**
    * function getAddedUncommittedFiles
    * @param actual_index : index of actual repo
    * @param old_index : index of tree of last commit
    * @return files path which are in actual index but not in old one
    */
  def getAddedUncommittedFiles(actual_index:IndexHandler, old_index:IndexHandler):List[String]={
    val actual_index_files = actual_index.getAllFilesPath
    val old_index_files = old_index.getAllFilesPath
    getNewFilesUntracked(actual_index_files,old_index_files)
  }

  /**
    * function getDeletedUncommittedFiles
    * @param actual_index : index of actual repo
    * @param old_index : index of tree of last commit
    * @return files path which are in old index but not in actual one
    */
  def getDeletedUncommittedFiles(actual_index:IndexHandler, old_index:IndexHandler):List[String]= {
    val actual_index_files = actual_index.getAllFilesPath
    val old_index_files = old_index.getAllFilesPath
    getFilesDeletedUntracked(actual_index_files, old_index_files)
  }

  /**
    * function getModifiedUncommittedFiles
    * @param actual_index : index of actual repo
    * @param old_index : index of tree of last commit
    * @return files path which are in actual and old indexes but with different blobs
    */
  def getModifiedUncommittedFiles(actual_index:IndexHandler, old_index:IndexHandler):List[String]= {
    val added_uncommitted_files = getAddedUncommittedFiles(actual_index,old_index)

    val changed_and_added_lines = getNewFilesUntracked(actual_index.getLinesList,old_index.getLinesList)

    changed_and_added_lines.filter(element => !added_uncommitted_files.contains(element.split(" ")(0)))
  }

  /**
    * function printUncommittedFiles
    * @param actual_directory : sgit repo
    * @return true if there are uncommitted files, else false
    */
  def printUncommittedFiles(actual_directory:File):Boolean = {
    //actual index
    val index_file = new IndexHandler(new File(actual_directory.getPath+"/.sgit/INDEX"))

    //-------to get the index of tree of last commit------
    val branch  = new FileHandler(new File(actual_directory.getPath+"/.sgit/HEAD")).getContent.replace("\n","")

    val commit_name = new FileHandler(new File(actual_directory.getPath+"/.sgit/refs/heads/"+branch)).getContent.replace("\n","")

    val commit_file = new CommitHandler(new File(actual_directory.getPath+"/.sgit/objects/commits/"+commit_name))

    val tree_file = new TreeHandler(new File(actual_directory.getPath+"/.sgit/objects/trees/"+commit_file.getTree))

    val index_last_commit = new IndexHandler(new File(actual_directory.getPath+"/.sgit/INDEX_LAST_COMMIT"))
    index_last_commit.createFile()
    index_last_commit.addContent(tree_file.getIndex("",actual_directory),appendContent = false)
    //--------------------------

    val get_added_uncommitted_files = getAddedUncommittedFiles(index_file,index_last_commit)
    val get_deleted_uncommitted_files = getDeletedUncommittedFiles(index_file,index_last_commit)
    val get_modified_uncommitted_files = getModifiedUncommittedFiles(index_file,index_last_commit)

    //if nothing to commit
    if(get_added_uncommitted_files.isEmpty && get_deleted_uncommitted_files.isEmpty && get_modified_uncommitted_files.isEmpty){
      println("No changes to be committed")
      print(Console.WHITE)
      false
    }
    else{
      //added = true for color green in console
      println("changes to be committed : ")
      printElements(get_added_uncommitted_files,"new file",added = true)
      printElements(get_deleted_uncommitted_files,"deleted",added = true)
      printElements(get_modified_uncommitted_files,"modified",added = true)
      print(Console.WHITE)
      true
    }
  }

}
