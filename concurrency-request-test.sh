#!/bin/bash

curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points &
curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points &
curl -L -v -d '{"memberId": 1841104, "code" : "REWARD_ORDER", "amounts": 100}' \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --url http://localhost:8080/points
