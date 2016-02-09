FROM alpine:3.3
MAINTAINER Philippe Marchesseault <pmarches@gmail.com>

RUN apk --update add openjdk8-jre

RUN apk add openssh
RUN mkdir /var/run/sshd
#RUN echo 'root:screencast' | chpasswd
#RUN sed -i 's/PermitRootLogin without-password/PermitRootLogin yes/' /etc/ssh/sshd_config

# SSH login fix. Otherwise user is kicked off after login
#RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd

#RUN /usr/bin/ssh-keygen -A
COPY dockerTestHostSSHKey.rsa /etc/ssh/ssh_host_rsa_key
COPY dockerTestHostSSHKey.rsa.pub /etc/ssh/ssh_host_rsa_key.pub
RUN chmod 600 /etc/ssh/ssh_host_rsa_key

ENV NOTVISIBLE "in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile

EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]

RUN mkdir -m 700 /root/.ssh
COPY testSSHKey_rsa.pub /root/.ssh/authorized_keys
RUN chmod 600 /root/.ssh/authorized_keys
