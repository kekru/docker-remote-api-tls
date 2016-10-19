FROM nginx:alpine
MAINTAINER Kevin Krummenauer <kevin@whiledo.de>
ADD resources/nginx-cert.conf /etc/nginx/conf.d/nginx-cert.conf
