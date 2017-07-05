FROM nginx:alpine
MAINTAINER Kevin Krummenauer <kevin@whiledo.de>
COPY resources/nginx.conf /etc/nginx/nginx.conf
COPY resources/nginx-cert.conf /etc/nginx/conf.d/nginx-cert.conf
