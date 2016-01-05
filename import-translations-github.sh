#!/usr/bin/env bash
if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Starting translation import\n"

  #go to home and setup git
  cd $HOME
  git config --global user.email "glucosioproject@gmail.com"
  git config --global user.name "Glucat"

  #clone gh-pages branch
  git clone --branch=develop https://$GITHUB_API_KEY@github.com/Glucosio/glucosio-android.git  develop > /dev/null

  cd develop/app/src/main/res/
  wget https://crowdin.com/downloads/crowdin-cli.jar
  java -jar crowdin-cli.jar download
  rm crowdin-cli.jar
  cp -r wear/* $HOME/develop/wear/src/main/res/
  cd $HOME/develop

  #add, commit and push files
  git add -f .
  git remote rm origin
  git remote add origin https://glucat:$GITHUB_API_KEY@github.com/Glucosio/glucosio-android.git
  git add -f .
  git commit -m "Automatic translation import (build $TRAVIS_BUILD_NUMBER)."
  git push -f origin develop > /dev/null

  echo -e "Done magic with coverage\n"
fi
