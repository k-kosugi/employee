FROM mysql:latest
EXPOSE 3306:3306
ENV MYSQL_ROOT_PASSWORD=kenta
ENV MYSQL_USER=kenta
ENV MYSQL_PASSWORD=kosugi
ENV MYSQL_DATABASE=employee
ENV TZ=Asia/Tokyo