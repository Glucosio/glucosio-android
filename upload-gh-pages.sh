#!/usr/bin/env bash
if [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  echo -e "Starting to update gh-pages\n"

  #copy data we're interested in to other place
  mkdir $HOME/android/
  cp -R app/build/outputs/apk/app-debug.apk $HOME/android/

  #go to home and setup git
  cd $HOME
  git config --global user.email "glucosioproject@gmail.com"
  git config --global user.name "Glucat"


  #clone gh-pages branch
  git clone --quiet --branch=master https://f070c9ac4abfa8450706e8dc28320da96ecba36e@github.com/Glucosio/glucosio.github.io.git  master > /dev/null

  #go into diractory and copy data we're interested in to that directory
  cd master
  cp -Rf $HOME/android/* .

  #add, commit and push files
  git add -f .
  git remote rm origin
  git remote add origin https://glucat:f070c9ac4abfa8450706e8dc28320da96ecba36e@github.com/Glucosio/glucosio.github.io.gi
  git add -f .
  git commit -m "Travis build $TRAVIS_BUILD_NUMBER pushed to gh-pages"
  git push -fq origin master > /dev/null

  echo -e "Done magic with coverage\n"
fi
