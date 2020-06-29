#!/bin/bash

if [ "$TRAVIS_BRANCH" = "master" ]; then
  ./gradlew publish
else 
  ./gradlew build
fi

ls -R app/build/outputs