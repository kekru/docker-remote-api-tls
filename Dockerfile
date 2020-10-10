# Original Dockerfile: https://github.com/nginxinc/docker-nginx/tree/5488180ebdd45b12b45107694dfa92dc878a2795/stable/alpine
FROM nginx:1.18.0-alpine
LABEL MAINTAINER="Kevin Krummenauer <kevin@whiledo.de>"
RUN apk add --no-cache openssl

COPY resources/create-certs.sh /script/create-certs.sh
COPY resources/nginx-cert.conf /etc/nginx/nginx.conf
COPY resources/entrypoint.sh /docker-entrypoint.d/30_entrypoint.sh

RUN chmod +x /script/create-certs.sh /docker-entrypoint.d/30_entrypoint.sh

ENV CREATE_CERTS_WITH_PW="" \
    CERTS_DIR=/data/certs \
    CERT_HOSTNAME="abc.127.0.0.1.nip.io" \
    CERT_EXPIRATION_DAYS="365" \
    CA_EXPIRATION_DAYS="900"

HEALTHCHECK --start-period=1s \
            --interval=5s \
            --timeout=5s \
            --retries=12 \
            CMD nc -vz localhost 443 || exit 1
