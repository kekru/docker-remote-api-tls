FROM nginx:alpine
MAINTAINER Kevin Krummenauer <kevin@whiledo.de>
COPY resources/nginx-cert.conf /etc/nginx/conf.d/nginx-cert.conf
RUN sed -i 's/user\s*nginx;/user root;/g' /etc/nginx/nginx.conf
