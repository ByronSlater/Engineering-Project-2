#!/usr/bin/env bash

set -euo pipefail

if [[ -f .env ]]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

DOMAIN="${OKTA_ISSUER#https://}"
DOMAIN="${DOMAIN%/}"

TOKEN=$(curl --fail-with-body --silent --show-error --request POST \
  --url "https://${DOMAIN}/oauth/token" \
  --header 'content-type: application/json' \
  --data "$(jq -n \
    --arg client_id "$MANAGEMENT_API_CLIENT_ID" \
    --arg client_secret "$MANAGEMENT_API_CLIENT_SECRET" \
    --arg audience "https://${DOMAIN}/api/v2/" \
    '{client_id: $client_id, client_secret: $client_secret, audience: $audience, grant_type: "client_credentials", scope: "read:users"}' \
  )" | jq -er '.access_token')

USERS_RESPONSE=$(curl --silent --show-error --write-out $'\n%{http_code}' \
  --url "https://${DOMAIN}/api/v2/users?per_page=100" \
  --header "authorization: Bearer ${TOKEN}")
STATUS="${USERS_RESPONSE##*$'\n'}"
USERS="${USERS_RESPONSE%$'\n'*}"

if [[ "$STATUS" != 2* ]]; then
  echo "Auth0 user request failed (HTTP ${STATUS}). Authorize this client for the Management API with read:users." >&2
  exit 1
fi

SQL=$(jq -r '.[] | select(.name != null and .name != "") | "INSERT INTO users (username, enabled) VALUES (\u0027" + (.name | gsub("\u0027"; "\u0027\u0027")) + "\u0027, \u0027t\u0027) ON CONFLICT (username) DO NOTHING;"' <<< "$USERS")

printf '%s\n' "$SQL" | psql \
  --dbname=acebook_springboot_development \
  --set=ON_ERROR_STOP=1 > /dev/null

printf '%s\n' "$SQL" | psql \
  --dbname=acebook_springboot_test \
  --set=ON_ERROR_STOP=1 > /dev/null
