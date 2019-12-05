<h1>Sgit</h1>
<h3>A simple git in scala</h3>



<h3>Installation</h3>
<p>In order to install SGIT and use its command, you have to:
<ul>
<li>Download zip archive present at <a href="https://github.com/williamregnart/sgit/blob/master/target/universal/sgit-0.1.zip">this link</a></li>
<li>Unzip archive in the directory of your choice</li>
	<li>In the bin folder, make sgit executable by running command <i>chmod +x sgit</i></li>
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
<h4>init</h4>
<p>Takes no parameter, init a sgit repository in your actual directory</p>
<h4>add &lt;file_name&gt;</h4>
<p>add the &lt;file_name&gt; in parameter in unstaged files</p>
<h4>add .</h4>
<p>add all files in the directory to unstaged files</p>
<h4>remove &lt;file_name&gt;</h4>
<p>remove the &lt;file_name&gt; in parameter from unstaged files</p>
<h4>commit</h4>
<p>commit all the files unstaged</p>
<h4>log</h4>
<p>print logs of commit from last to first</p>
<h4>log -p</h4>
<p>print logs of commit from last to first with differences between files</p>
<h4>log --stat</h4>
<p>print logs of commit from last to first with stat differences</p>
<h4>tag &lt;tag_name&gt;</h4>
<p>add the tag <tag_name> to the last commit</p>
<h4>branch &lt;branch_name&gt;</h4>
<p>create a branch &lt;branch_name&lt; on the actual commit</p>
<h4>branch -av</h4>
<p>list all branches and tags</p>
<h4>checkout &lt;commit_ref/tag_name/branch_name&gt;</h4>
<p>switch on the commit referenced, or commit of the &lt;tag_name&gt;, or branch of the &lt;branch_name&lgt;</p>
