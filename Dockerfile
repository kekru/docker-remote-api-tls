FROM nginx:1.15.12-alpine
LABEL MAINTAINER="Kevin Krummenauer <kevin@whiledo.de>"
RUN apk add --no-cache openssl

COPY resources /script

RUN cp /script/nginx-cert.conf /etc/nginx/nginx.conf \
 && chmod +x /script/create-certs.sh /script/entrypoint.sh

ENV CREATE_CERTS_WITH_PW="" \
    CERTS_DIR=/data/certs \
    CERT_HOSTNAME="myserver.example.com"

ENTRYPOINT ["/script/entrypoint.sh"]
CMD ["nginx", "-g", "daemon off;"]