FROM maven:latest AS MAVEN_TOOL_CHAIN
ENV NODE_TLS_REJECT_UNAUTHORIZED 0
ENV DISPLAY 99
ENV CHROME_BIN /usr/bin/google-chrome
ENV HEADLESS true
ENV environment doc
ENV container true
ENV screen 0 1280x1024x24
COPY . /app
COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app/
RUN curl -o- https://raw.githubusercontent.com/creationix/nvm/v0.34.0/install.sh | bash
RUN apt-get clean
RUN apt-get update
RUN curl -sL https://deb.nodesource.com/setup_10.x | bash -
RUN apt-get install -y nodejs
RUN npm install -g pa11y --unsafe-perm=true --allow-root
RUN npm install -g pa11y pa11y-reporter-html
RUN apt-get install -y xvfb
RUN wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb
RUN dpkg -i google-chrome-stable_current_amd64.deb; apt-get -fy install
RUN Xvfb :99 -screen 0 1280x1024x24 &
RUN export DISPLAY=:99
RUN mvn clean verify -Dheadless=true -Denvironment=doc -DaccessibilityTest=true
