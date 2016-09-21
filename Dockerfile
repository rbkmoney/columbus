# @dockerfile.Template@

# base java service image
FROM dr.rbkmoney.com/rbkmoney/service-java:d688a72d5859177174f733a5b6e6f4c460ce8ef3
MAINTAINER Semenkov Alexey <a.semenkov@rbkmoney.com>
COPY @artifactId@-@version@.jar /opt/@artifactId@/@artifactId@-@version@.jar
CMD ["java", "-Xmx512m", "-jar","/opt/@artifactId@/@artifactId@-@version@.jar"]
EXPOSE 8022

LABEL com.rbkmoney.@artifactId@.parent=service_java \
    com.rbkmoney.@artifactId@.parent_tag=d688a72d5859177174f733a5b6e6f4c460ce8ef3 \
    com.rbkmoney.@artifactId@.build_img=build \
    com.rbkmoney.@artifactId@.build_img_tag=@dockerfile.build.container.tag@ \
    com.rbkmoney.@artifactId@.commit_id=@git.revision@ \
    com.rbkmoney.@artifactId@.commit_number=@git.commitsCount@ \
    com.rbkmoney.@artifactId@.branch=@git.branch@
WORKDIR /opt/@artifactId@
