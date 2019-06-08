# Docker Remote API with TLS client authentication via container

This images makes you publish your Docker Remote API by a container.  
A client must authenticate with a client-TLS certificate.  
This is an alternative way, instead of [configuring TLS on Docker directly](https://gist.github.com/kekru/974e40bb1cd4b947a53cca5ba4b0bbe5).  

## Remote Api with external CA, certificates and key

First you need a CA and certs and keys for your Docker server and the client.  

Create them as shown here [Protect the Docker daemon socket](https://docs.docker.com/engine/security/https/).  
Or create the files with this script [create-certs.sh](https://github.com/kekru/linux-utils/blob/master/cert-generate/create-certs.sh). Read [Create certificate files](https://gist.github.com/kekru/974e40bb1cd4b947a53cca5ba4b0bbe5#create-certificate-files) for information on how to use the script.

Copy the following files in a directory. The directory will me mounted in the container.  

```bash
ca-cert.pem
server-cert.pem
server-key.pem
```

The files `cert.pem` and `key.pem` are certificate and key for the client. The client will also need the `ca-cert.pem`.  

Create a docker-compose.yml file:

```yml
version: "3.4"
services:
  remote-api:
    image: kekru/docker-remote-api-tls:v0.2.0
    ports:
     - 2376:443
    volumes:
     - <local cert dir>:/data/certs:ro
     - /var/run/docker.sock:/var/run/docker.sock:ro
```

Now run the container with `docker-compose up -d` or `docker stack deploy --compose-file=docker-compose.yml remoteapi`.  
Your Docker Remote API is available on port 2376 via https. The client needs to authenticate via `cert.pem` and `key.pem`.

## Remote Api with auto generating CA, certificates and keys

The docker-remote-api image can generate CA, certificates and keys for you automatically.  
Create a docker-compose.yml file, specifying a password and the hostname, on which the remote api will be accessible later on. The hostname will be written to the server's certificate.

```yml
version: "3.4"
services:
  remote-api:
    image: kekru/docker-remote-api-tls:v0.2.0
    ports:
     - 2376:443
    environment:
     - CREATE_CERTS_WITH_PW=supersecret
     - CERT_HOSTNAME=remote-api.example.com
    volumes:
     - <local cert dir>:/data/certs
     - /var/run/docker.sock:/var/run/docker.sock:ro
```

Now run the container with `docker-compose up -d` or `docker stack deploy --compose-file=docker-compose.yml remoteapi`.  
Certificates will be creates in `<local cert dir>`.  
You will find the client-certs in `<local cert dir>/client/`. The files are `ca.pem`, `cert.pem` and `key.pem`.  

## Setup client

See [Run commands on remote Docker host](https://gist.github.com/kekru/4e6d49b4290a4eebc7b597c07eaf61f2) for instructions how to setup a client to communicate with the remote api.
