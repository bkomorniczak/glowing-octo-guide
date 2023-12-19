# Contributing guide lines
## How to set up your local env
### Clone the repository
Go to directory where you want to set up the project locally. Open terminal type:

`git clone git@github.com:bkomorniczak/glowing-octo-guide.git`

Or do it with your IDE.

### Add your changes

<strong> Before creating new branch, be sure your local main is up to date with remote main. </strong>

Just in case, before creating new branch type: `git pull`

Sometimes, someone makes changes to main when you are working on your changes. Than you need to rebase.

`git rebase main`
If it passes, you can proceed with your changes. 
The can be conflicts. You need to resolve them, and then `git rebase --continue`
Important: you will need to add -f flag when pushing: `git push -f`

In terminal in your IDE (or not in your IDE if you are feeling geek) type 

`git checkout -b <YOUR_BRANCH_NAME>`

Remember to change <YOUR_BRANCH_NAME> to something significant, so the branch name would suggest what your changes are all about.

Do your changes.

Now it is time to commit your changes.
`git add .`

`git commit -m "<YOUR_COMMIT_MSG>"`

Protip - after first `git add .` in your branch, you can merge this two commands to `git commit -am "<YOUR_COMMIT_MSG>"`

When you are done with your local changes to need to push. Remember to explain briefly what your changes do in your commit msg.

`git push --set-upstream origin <YOUR_BRANCH_NAME>`

Change <YOUR_BRANCH_NAME> to your branch name. You need to do it like this only for the first push. Later use `git push`

In terminal there will be log with link to PR creation. Click on it. Create PR.



### Pull requests

Pull requests needs to be approved by code owners. After you are ready to merge your PR let us know in Discord channel. 
Code owner will review your changes, add comments, or just approve. After approve, you can merge the PR.

The are also automatic code reviewers:
#### Super-Linter: used for checking code style. 
#### Trivy: used for scanning code for vulnerabilities

They need pass. Without it you won't be able to merge.
