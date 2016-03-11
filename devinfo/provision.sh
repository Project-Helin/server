#!/usr/bin/env bash
# update before the show begins
apt-get update

# Some basic tools
apt-get install -y vim 

# RabbitMQ

# Add RabbitMQ to source list
echo "deb http://www.rabbitmq.com/debian/ testing main" >> /etc/apt/sources.list
curl http://www.rabbitmq.com/rabbitmq-signing-key-public.asc | sudo apt-key add -

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

# TODO: Install PostgreSQL