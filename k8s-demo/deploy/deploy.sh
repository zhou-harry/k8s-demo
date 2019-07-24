#!/bin/bash
ssh root@192.168.88.130

cd /harry/deploy/project

kubectl apply -f k8s-demo.yaml
