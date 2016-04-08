# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANT_API = "2"

Vagrant.configure(VAGRANT_API) do |config|

  # Use debian as our box
  config.vm.box = "ubuntu/trusty64"

  # run this script at initial setup
  config.vm.provision :shell, :path => "devinfo/provision.sh"

  config.vm.provider "virtualbox" do |v|
     v.name = "project-helin"
     v.memory = 3024
     v.cpus = 4
  end
	 
  # RabbitMQ Default port
  config.vm.network :forwarded_port, host: 5672, guest: 5672

  # For Management UI
  config.vm.network :forwarded_port, host: 15672, guest: 15672

  # PostgreSQL
  config.vm.network :forwarded_port, host: 5432, guest: 5432

end
