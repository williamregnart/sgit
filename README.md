<h1>Sgit</h1>
<h3>A simple git in scala</h3>



<h3>Installation</h3>
<p>In order to install SGIT and use its command, you have to:
<ul>
<li>Download zip archive present at <a href="https://github.com/williamregnart/sgit/blob/master/target/universal/sgit-0.1.zip">this link</a></li>
<li>Unzip archive in the directory of your choice</li>
<li>In your shell, add the sgit-0.1/bin directory path to your PATH</li>

</ul>
You can use sgit on your shell</p><br>

<h3>Commands</h3>
<ul>
	<li>init</li>
	<li>add</li>
	<li>remove</li>
	<li>commit</li>
	<li>log</li>
	<li>tag</li>
	<li>branch</li>
	<li>checkout</li>
</ul>
<br>
<h5>init</h5>
<p>Takes no parameter, init a sgit repository in your actual directory</p>
<h5>add <file_name></h5>
<p>add the file_name in parameter in unstaged files</p>
<h5>add .</h5>
<p>add all files in the directory to unstaged files</p>
<h5>remove <file_name></h5>
<p>remove the file_name in parameter from unstaged files</p>
<h5>commit</h5>
<p>commit all the files unstaged</p>
<h5>log</h5>
<p>print logs of commit from last to first</p>
<h5>log -p</h5>
<p>print logs of commit from last to first with differences between files</p>
<h5>log --stat</h5>
<p>print logs of commit from last to first with stat differences</p>
<h5>tag <tag_name></h5>
<p>add the tag <tag_name> to the last commit</p>
<h5>branch <branch_name></h5>
<p>create a branch <branch_name> on the actual commit</p>
<h5>branch -av</h5>
<p>list all branches and tags</p>
<h5>checkout <commit_ref/tag_name/branch_name></h5>
<p>switch on the commit referenced or commit of the tag_name or branch of the branch_name</p>