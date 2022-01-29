FROM jetty:10.0.7-jre11-slim
COPY app/build/libs/app.war /var/lib/jetty/webapps/ROOT.war