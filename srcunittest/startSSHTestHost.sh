#!/bin/bash
sudo docker stop EarlGridhost
sudo docker rm EarlGridhost
sudo docker build -t earlgrid_host_image .
sudo docker run -d -p 127.0.0.1:8022:22 --name=EarlGridhost earlgrid_host_image:latest
sudo docker ps
