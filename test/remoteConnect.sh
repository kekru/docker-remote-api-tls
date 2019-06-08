#!/bin/sh

# call this file with source remoteConnect.sh

export DOCKER_HOST=abc.127.0.0.1.nip.io:8443
export DOCKER_TLS_VERIFY=1
export DOCKER_CERT_PATH=$(pwd)/certs/client
