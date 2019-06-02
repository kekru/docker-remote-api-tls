FROM nginx:1.16.0-alpine
LABEL MAINTAINER="Kevin Krummenauer <kevin@whiledo.de>"
RUN apk add --no-cache openssl

COPY resources /script

RUN cp /script/nginx-cert.conf /etc/nginx/conf.d/nginx-cert.conf \
 && chmod +x /script/create-certs.sh /script/entrypoint.sh \
 && sed -i 's/user\s*nginx;/user root;/g' /etc/nginx/nginx.conf

ENV CREATE_CERTS_WITH_PW="" \
    CERTS_DIR=/data/certs \
    CERT_HOSTNAME="myserver.example.com"

ENTRYPOINT ["/script/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]