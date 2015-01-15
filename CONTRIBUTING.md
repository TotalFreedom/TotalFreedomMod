# Contributing to TotalFreedomMod #
TotalFreedomMod is a CraftBukkit server plugin designed primarily to support the [Official TotalFreedom Minecraft Server](http://totalfreedom.me/). However, it can be used in a variety of other configurations with minimal fuss.

For those who wish to contribute, we encourage you to fork the repository and submit pull requests. Below you will find guidelines that explain this process in further detail.

## Quick Guide ##
1. Create or find an issue on our [Issue Tracker](https://github.com/TotalFreedom/TotalFreedomMod/issues).
2. Does your change fits TotalFreedomMod's goals?
3. Fork TotalFreedomMod if you haven't done so already.
4. Make a branch dedicated to your change.
5. Make your change.
6. Commit your change according to the [committing guidelines](#committing-your-changes).
7. Push your branch and submit a pull request.

## Getting Started ##
* Search the issue tracker for your bug report or feature request.
* If the issue does not exist already, create it.
  * Clearly describe the issue.
  * If your issue is a bug, describe the steps needed to reproduce it.
  * If your issue is a feature request, ensure it fits TotalFreedomMod's goals and describe your feature in detail.
* Fork the repository on GitHub.

## Does the change fit TotalFreedomMod's goals? ##
As a rough guideline, ask yourself the following questions to determine if your proposed change fits the TotalFreedomMod project's goals. Please remember that this is only a rough guideline and may or may not reflect the definitive answer to this question.

* Is the change in line with the principles of "Total Freedom"?
  TotalFreedom is a freedom-based server. We don't normally make changes that restrict players unless absolutely necessary.

* Is the change directed towards the TotalFreedom server?
  Changes must be directed towards the TotalFreedom server. Changes such as adding ranks, adding the name of other TotalFreedom-like servers will not likely be accepted.

* Does the change add a feature that involves micromanagement?
  Pull requests that involve adding a micromanagement feature will likely not be accepted. This includes shorthands for multiple commands.

* Is a similar feature already present?
  Features that have very similar alternatives will not be added. For example: A command that temporarily bans a player from the server is very similar to the _/tban_ command and thus will likely not be accepted.

## Making Changes ##
* Create a topic branch from where you want to base your work.
  * This is usually the master branch.
  * Name your branch something relevant to the change you are going to make.
  * To quickly create a topic branch based on master, use `git checkout master` followed by `git checkout -b <name>`. Avoid working directly on the `master` branch.
* Make sure your change meets our [code requirements](#code-requirements).

### Code requirements ###
* Code must be written in [Allman style](http://en.wikipedia.org/wiki/Indent_style#Allman_style), and that it follows [Java Code Conventions](http://www.oracle.com/technetwork/java/codeconventions-150003.pdf).__
* No tabs; use 4 spaces for indentation.
* No trailing whitespaces for code lines, comments or configuration files.
* No CRLF line endings, only LF is allowed.
  * For Windows-based machines, you can configure Git to do this for your by running `git config --global core.autocrlf true`.
  * If you're running a Linux or Mac OSX, you should run `git config --global core.autocrlf input` instead.
  * For more information about line feeds. See this [this article](http://adaptivepatchwork.com/2012/03/01/mind-the-end-of-your-line/).
* No 80 character line limit or 'weird' midstatement newlines.
* Additions should be compiled, complete and tested before committing.
* Avoid using `org.bukkit.Server.dispatchCommand()`. Commits that make use of it will likely be rejected.
* Files must always end with a newline.
* Avoid nested code structures.

## Committing your changes ##
* Check for unnecessary whitespace with `git diff --check` before committing.
* Describe your changes in the commit description.
* For a prolonged description, continue on a new line.
* The first description line should be once sentence and should not exceed 10 words.
* The first description line should contain either:
  * For a bug-related issue: "Resolves _#issue_".
  * For a feature request: "Fixes _#issue_".
  * "#issue" is the issue number number you based your work on.

#### Example commit message ####
```
Fixed bugs with /saconfig clear. Resolves #167
Admins can now use /saconfig clear [ip] to remove the designated IP (only their own IPs can be removed. 
They can also simply use /saconfig clear to remove all IPs but the current one. /saconfig clear is safe, 
meaning you can't remove your current IP or an IP that doesn't belong to you.
```

## Submitting Your Changes ##
* Push your changes to the topic branch in your fork of the repository.
* Submit a pull request to this repository.
  * Explain in detail what each one of your commits changes and point out any big changes.
* Wait as a developer evaluates your changes.
* Do not add any prefixes or tags.

## Tips - How To Get Your Pull Request Accepted ##
* Please make sure your changes are written such as other features would be. For example: Commands have their own class and extend TFM_Command.
* Do not increment the version number.
* If you want to add multiple changes, please make one pull request per change. This way, it's easier to accept your changes faster and won't block the other changes if there is an issue with a specific line of code.
* Please avoid having to add files in the main namespace where possible.
* Please refrain from using an excessive amount of commits. As few as possible is generally the best.
* Please do not spread your contribution over several pull-requests.

## Additional Resources ##
* [TotalFreedom information](http://totalfreedom.me)
* [TotalFreedom forums](http://totalfreedom.boards.net)
* [Bug tracker](https://github.com/TotalFreedom/TotalFreedomMod/issues)
* [General GitHub documentation](http://help.github.com/)
* [GitHub pull request documentation](http://help.github.com/send-pull-requests/)
