FROM httpd:2.4-alpine
WORKDIR /

#RUN apt install apache2-utils
#RUN a2enmod proxy_http

COPY dist/ /usr/local/apache2/htdocs/
EXPOSE 80