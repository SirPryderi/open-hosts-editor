#!/bin/bash

if [ "$TRAVIS_BRANCH" = "$DEPLOYMENT_BRANCH" ]; then
  ./gradlew publish
else 
  ./gradlew build
fi

ls -R app/build/outputs