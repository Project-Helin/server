#!/usr/bin/env bash
export DB_USER=helin
export DB_USER_TEST=test

# update before the show begins
apt-get update

# Some basic tools
apt-get install -y vim htop

# install some compile tools
apt-get install -y autoconf build-essential cmake docbook-mathml docbook-xsl libboost-dev libboost-thread-dev libboost-filesystem-dev libboost-system-dev libboost-iostreams-dev libboost-program-options-dev libboost-timer-dev libcunit1-dev libgdal-dev libgeos++-dev libgeotiff-dev libgmp-dev libjson0-dev libjson-c-dev liblas-dev libmpfr-dev libopenscenegraph-dev libpq-dev libproj-dev libxml2-dev xsltproc git build-essential wget


# RabbitMQ
# Add RabbitMQ to source list
echo "deb http://www.rabbitmq.com/debian/ testing main" >> /etc/apt/sources.list
curl http://www.rabbitmq.com/rabbitmq-signing-key-public.asc | sudo apt-key add -

# Add PostgreSQL 9.5 to source list
echo "deb http://apt.postgresql.org/pub/repos/apt trusty-pgdg main" >> /etc/apt/sources.list
curl http://apt.postgresql.org/pub/repos/apt/ACCC4CF8.asc | sudo apt-key add -

# update APT
apt-get update
# Install RabbitMQ
apt-get install rabbitmq-server -y

# Enable Management Console:
# Connect to port 15672 and you'll be provided with an UI to manager RabbitMQ
rabbitmq-plugins enable rabbitmq_management

# Add new user 'admin' with password 'helin'
sudo rabbitmqctl add_user admin helin
sudo rabbitmqctl set_user_tags admin administrator
sudo rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"

# Install PostgreSQL
sudo apt-get install -y postgresql-9.5 postgresql-server-dev-9.5 postgresql-contrib-9.5

# Install Postgis 2.2 with SFCGAL Support
# Download and compile CGAL
wget https://gforge.inria.fr/frs/download.php/file/32994/CGAL-4.3.tar.gz &&\
tar -xzf CGAL-4.3.tar.gz &&\
cd CGAL-4.3 &&\
mkdir build &&\
cd build &&\
cmake .. &&\
sudo make
sudo make install
cd ..
cd ..
# don't remove CGAL here because it is needed for later installations!
rm CGAL-4.3.tar.gz

# download and compile SFCGAL
git clone https://github.com/Oslandia/SFCGAL.git &&\
cd SFCGAL &&\
cmake .
sudo make -j 2
sudo make install
cd ..
# cleanup
# no ZIP File to be removed!

# download and install GEOS 3.5
wget http://download.osgeo.org/geos/geos-3.5.0.tar.bz2 &&\
tar -xjf geos-3.5.0.tar.bz2 &&\
cd geos-3.5.0 && \
./configure
sudo make
sudo make install
cd ..
# cleanup
rm geos-3.5.0.tar.bz2

# update ld - configure dynamic linker run-time bindings
sudo ldconfig

# Download and compile PostGIS
wget http://download.osgeo.org/postgis/source/postgis-2.2.0.tar.gz &&\
tar -xzf postgis-2.2.0.tar.gz &&\
cd postgis-2.2.0 &&\
./configure --with-sfcgal=/usr/local/bin/sfcgal-config --with-geos=/usr/local/bin/geos-config
sudo make
sudo make install
cd ..
# cleanup
rm -Rf postgis-2.2.0.tar.gz

# update ld
sudo ldconfig

# signInPost new user and DB
sudo -u postgres createdb $DB_USER
sudo -u postgres createuser $DB_USER -s       # -s for superuser
# change password
sudo -u postgres psql -c "alter user $DB_USER with password '$DB_USER';"
sudo -u postgres psql -d $DB_USER -c "CREATE EXTENSION postgis;"
sudo -u postgres psql -d $DB_USER -c "CREATE EXTENSION postgis_sfcgal;"
sudo -u postgres psql -d $DB_USER -c "CREATE EXTENSION \"uuid-ossp\";"

# Create test user
sudo -u postgres createdb $DB_USER_TEST
sudo -u postgres createuser $DB_USER_TEST -s       # -s for superuser
sudo -u postgres psql -c "alter user $DB_USER_TEST with password '$DB_USER_TEST';"
sudo -u postgres psql -d $DB_USER_TEST -c "CREATE EXTENSION postgis;"
sudo -u postgres psql -d $DB_USER_TEST -c "CREATE EXTENSION postgis_sfcgal;"
sudo -u postgres psql -d $DB_USER_TEST -c "CREATE EXTENSION \"uuid-ossp\";"

# We need to alter vagrant configuration, so that we can connect from
# outside of the virtual machine to the db
# From: http://jamie.ideasasylum.com/2012/09/connecting-navicat-to-postgresql-on-vagrant/
# Another source: http://www.bentedder.com/use-pgadmin-access-postgres-database-within-vagrant-box/

sudo sh -c  "echo 'host all all all password' >> /etc/postgresql/9.5/main/pg_hba.conf"
sudo sh -c  "echo listen_addresses = \'*\' >> /etc/postgresql/9.5/main/postgresql.conf"

# restart postgres so that all changes get activated
sudo service postgresql restart

# Clean up all compile packages
# all -dev packages
sudo apt-get remove -y --purge autotools-dev libgeos-dev libgif-dev libgl1-mesa-dev libglu1-mesa-dev libgnutls-dev libgpg-error-dev libhdf4-alt-dev libhdf5-dev libicu-dev libidn11-dev libjasper-dev libjbig-dev libjpeg8-dev libjpeg-dev libjpeg-turbo8-dev libkrb5-dev libldap2-dev libltdl-dev liblzma-dev libmysqlclient-dev libnetcdf-dev libopenthreads-dev libp11-kit-dev libpng12-dev libpthread-stubs0-dev librtmp-dev libspatialite-dev libsqlite3-dev libssl-dev libstdc++-4.8-dev libtasn1-6-dev libtiff5-dev libwebp-dev libx11-dev libx11-xcb-dev libxau-dev libxcb1-dev libxcb-dri2-0-dev libxcb-dri3-dev libxcb-glx0-dev libxcb-present-dev libxcb-randr0-dev libxcb-render0-dev libxcb-shape0-dev libxcb-sync-dev libxcb-xfixes0-dev libxdamage-dev libxdmcp-dev libxerces-c-dev libxext-dev libxfixes-dev libxshmfence-dev libxxf86vm-dev linux-libc-dev manpages-dev mesa-common-dev libgcrypt11-dev unixodbc-dev uuid-dev x11proto-core-dev x11proto-damage-dev x11proto-dri2-dev x11proto-fixes-dev x11proto-gl-dev x11proto-input-dev x11proto-kb-dev x11proto-xext-dev x11proto-xf86vidmode-dev xtrans-dev zlib1g-dev
# installed packages
sudo apt-get remove -y --purge autoconf build-essential cmake docbook-mathml docbook-xsl libboost-dev libboost-filesystem-dev libboost-timer-dev libcgal-dev libcunit1-dev libgdal-dev libgeos++-dev libgeotiff-dev libgmp-dev libjson0-dev libjson-c-dev liblas-dev libmpfr-dev libopenscenegraph-dev libpq-dev libproj-dev libxml2-dev postgresql-server-dev-9.5 xsltproc git build-essential wget
# additional compilation packages
sudo apt-get remove -y --purge m4
# - DONT EVER TRY TO DO AUTOREMOVE!!! -

# Check if everything is fine!
sudo -u postgres psql -d $DB_USER -c "SELECT POSTGIS_FULL_VERSION();"
sudo -u postgres psql -d $DB_USER_TEST -c "SELECT POSTGIS_FULL_VERSION();"
