FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install --yes python uwsgi uwsgi-plugin-python && \
    apt-get clean

ENV LORIS_COMMIT 8facc018a474835a3ab7ead2b631b8c6f3c7b311
ENV LORIS_GITHUB_USER wellcometrust

COPY requirements.txt /
COPY install_loris.sh /install_loris.sh
RUN /install_loris.sh

# This is an sRGB color profile downloaded from
# http://www.color.org/srgbprofiles.xalter
COPY sRGB2014.icc /usr/share/color/icc/colord/sRGB2014.icc

ENV LORIS_CONF_FILE /opt/loris/etc/loris2.conf

COPY loris2.conf /opt/loris/etc/loris2.conf
COPY loris2.wsgi /var/www/loris2/loris2.wsgi
COPY uwsgi.ini /etc/uwsgi

EXPOSE 8888

CMD ["/usr/bin/uwsgi", "--ini", "/etc/uwsgi/uwsgi.ini"]
