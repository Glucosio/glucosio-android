#!/usr/bin/env bash
if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Starting translation import\n"

  #copy data we're interested in to other place
  mkdir $HOME/android/
  cp -R app/src/main/res/* $HOME/android/

  #go to home and setup git
  cd $HOME
  git config --global user.email "glucosioproject@gmail.com"
  git config --global user.name "Glucat"

  #clone gh-pages branch
  git clone --quiet --branch=develop https://8ded5df0cdf373ca7b7662f00ef159f722601d54@github.com/Glucosio/android.git  develop > /dev/null

  #go into directory and copy data we're interested in to that directory
  cd develop
  cp -u $HOME/android/ app/src/main/res/

  #add, commit and push files
  git add -f .
  git remote rm origin
  git remote add origin https://glucat:$GITHUB_API_KEY@github.com/Glucosio/android.git
  git add -f .
  git commit -m "Done translation import for (build $TRAVIS_BUILD_NUMBER). [ci skip]"
  git push -fq origin develop > /dev/null

  echo -e "Done magic with coverage\n"
fi