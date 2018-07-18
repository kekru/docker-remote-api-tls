# Docker Remote API with TLS client authentication via container
This images makes you publish your Docker Remote API by a container.  
A client must authenticate with a client-TLS certificate.  
This is an alternative way, instead of configuring TLS on Docker directly.  

## Create CA, certificates and keys  
First you need a CA and certs and keys for your Docker server and the client.  
Create them as shown here [Protect the Docker daemon socket](https://docs.docker.com/engine/security/https/).  
Or create the files with this script [create-certs.sh](https://github.com/kekru/linux-utils/blob/master/cert-generate/create-certs.sh). Read [Create certificate files](https://gist.github.com/kekru/974e40bb1cd4b947a53cca5ba4b0bbe5#create-certificate-files) for information on how to use the script.

## Start Container  
Copy the following files in a directory. The directory will me mounted in the container. 
```bash
ca-cert.pem 
server-cert.pem 
server-key.pem
```

The files `cert.pem` and `key.pem` are certificate and key for the client. The client will also need the `ca-cert.pem`.  

Now run the container:  
`docker run --name remote-api-tls -d -p 2376:443 -v <local cert dir>:/data/certs:ro -v /var/run/docker.sock:/var/run/docker.sock:ro whiledo/docker-remote-api-tls`  

Your Docker Remote API is available on port 2376 via https. The client needs to authenticate via `cert.pem` and `key.pem`.
