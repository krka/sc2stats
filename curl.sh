#!/bin/bash
access_token=$(cat accesstoken.json | jq -r .access_token)
curl -H "Authorization: Bearer $access_token" "$@"

