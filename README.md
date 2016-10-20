# Docker Remote API with TLS client authentication via container
This images makes you publish your Docker Remote API by a container.  
A client must authenticate with a client-TLS certificate.  
This is an alternative way, instead of configuring TLS on Docker directly.  

## Create CA, certificates and keys  
First you need a CA and certs and keys for your Docker server and the client.  
Create them as shown here [Protect the Docker daemon socket](https://docs.docker.com/engine/security/https/).  

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

## 502 Bad Gateway or Permisson denied  
If the nginx shows a "502 Bad Gateway" exception and `docker logs remote-api-tls` contains "connect() to unix:/var/run/docker.sock failed (13: Permission denied)" you'll need to edit permissions for the Docker daemon socket:  
`chmod 666 /var/run/docker.sock`

## Works with Docker Maven Plugin  
The configuration works great with [fabric8io's Docker Maven Plugin](https://github.com/fabric8io/docker-maven-plugin).  

Here is a simple example configuration, where /home/me/my/cert/dir contains `cert.pem`, `key.pem` and `ca.pem` (= ca-cert.pem from above).
```xml
<build>
	<plugins>
		<plugin>
			<groupId>io.fabric8</groupId>
			<artifactId>docker-maven-plugin</artifactId>
			<version>0.16.8</version>
			<configuration>
				<images>
					<image>
						<alias>my-httpd</alias>
						<name>httpd</name>
					</image>
				</images>
				<dockerHost>https://my-website.org:2376</dockerHost>
				<certPath>/home/me/my/cert/dir</certPath>
				<useColor>true</useColor>
			</configuration>
		</plugin>
	</plugins>
</build>
```

`mvn docker:start` starts the httpd via the remote API.
