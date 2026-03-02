# Contributing to Creamie

First off, thank you for considering contributing to Creamie! It's people like you that make Creamie such a great tool.

## Where do I go from here?

If you've noticed a bug or have a feature request, make sure to check our [Issues](https://github.com/rajatt04/Creamie/issues) to see if someone else has already created a ticket. If not, go ahead and [make one](https://github.com/rajatt04/Creamie/issues/new/choose)!

## Fork & create a branch

If this is something you think you can fix, then fork Creamie and create a branch with a descriptive name.

A good branch name would be (where issue #325 is the ticket you're working on):

```sh
git checkout -b 325-add-dark-mode
```

## Get the test suite running

Make sure you're using the latest version of Android Studio and have Java 17 installed.
1. Clone your fork.
2. Open the project in Android Studio.
3. Sync project with Gradle files.
4. Run the app to ensure everything works correctly.

## Implement your fix or feature

At this point, you're ready to make your changes. Feel free to ask for help; everyone is a beginner at first.

## Make a Pull Request

At this point, you should switch back to your master branch and make sure it's up to date with Creamie's master branch:

```sh
git remote add upstream git@github.com:rajatt04/Creamie.git
git checkout master
git pull upstream master
```

Then update your feature branch from your local copy of master, and push it!

```sh
git checkout 325-add-dark-mode
git rebase master
git push --set-upstream origin 325-add-dark-mode
```

Finally, go to GitHub and make a Pull Request.
