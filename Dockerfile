FROM openjdk:17-jdk-slim

ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG JWT_SECRET_KEY

ARG KAKAO_CLIENT_ID
ARG KAKAO_CLIENT_SECRET
ARG KAKAO_REDIRECT_URI
ARG KAKAO_AUTHORIZATIONGRANT_TYPE
ARG KAKAO_CLIENT_AUTHENTICATION_METHOD
ARG KAKAO_SCOPE
ARG KAKAO_AUTHORIZATION_URI
ARG KAKAO_TOKEN_URI
ARG KAKAO_USER_INFO_URI
ARG KAKAO_USER_NAME_ATTRIBUTE

ARG NAVER_CLIENT_ID
ARG NAVER_CLIENT_SECRET
ARG NAVER_REDIRECT_URI
ARG NAVER_AUTHORIZATION_GRANT_TYPE
ARG NAVER_SCOPE
ARG NAVER_AUTHORIZATION_URI
ARG NAVER_TOKEN_URI
ARG NAVER_USER_INFO_URI
ARG NAVER_USER_NAME_ATTRIBUTE

ENV DB_URL=$DB_URL
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD
ENV JWT_SECRET_KEY=$JWT_SECRET_KEY

ENV KAKAO_CLIENT_ID=$KAKAO_CLIENT_ID
ENV KAKAO_CLIENT_SECRET=$KAKAO_CLIENT_SECRET
ENV KAKAO_REDIRECT_URI=$KAKAO_REDIRECT_URI
ENV KAKAO_AUTHORIZATIONGRANT_TYPE=$KAKAO_AUTHORIZATIONGRANT_TYPE
ENV KAKAO_CLIENT_AUTHENTICATION_METHOD=$KAKAO_CLIENT_AUTHENTICATION_METHOD
ENV KAKAO_SCOPE=$KAKAO_SCOPE
ENV KAKAO_AUTHORIZATION_URI=$KAKAO_AUTHORIZATION_URI
ENV KAKAO_TOKEN_URI=$KAKAO_TOKEN_URI
ENV KAKAO_USER_INFO_URI=$KAKAO_USER_INFO_URI
ENV KAKAO_USER_NAME_ATTRIBUTE=$KAKAO_USER_NAME_ATTRIBUTE

ENV NAVER_CLIENT_ID=$NAVER_CLIENT_ID
ENV NAVER_CLIENT_SECRET=$NAVER_CLIENT_SECRET
ENV NAVER_REDIRECT_URI=$NAVER_REDIRECT_URI
ENV NAVER_AUTHORIZATION_GRANT_TYPE=$NAVER_AUTHORIZATION_GRANT_TYPE
ENV NAVER_SCOPE=$NAVER_SCOPE
ENV NAVER_AUTHORIZATION_URI=$NAVER_AUTHORIZATION_URI
ENV NAVER_TOKEN_URI=$NAVER_TOKEN_URI
ENV NAVER_USER_INFO_URI=$NAVER_USER_INFO_URI
ENV NAVER_USER_NAME_ATTRIBUTE=$NAVER_USER_NAME_ATTRIBUTE

COPY build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
