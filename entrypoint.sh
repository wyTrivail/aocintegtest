#!/usr/bin/env bash

# run task
/app/aocintegtest/bin/aocintegtest $@

# get response
response=`cat task_response`

# write response back to github action
echo "::set-output name=task_response::${response}"
