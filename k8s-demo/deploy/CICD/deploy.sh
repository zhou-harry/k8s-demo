#!/bin/bash
#unable to recognize "k8s-demo.yml": Get http://localhost:8080/api?timeout=32s: dial tcp [::1]:8080: connect: connection refused

#mkdir -p $HOME/.kube
#sudo cp -i /etc/kubernetes/admin.conf $HOME/.kube/config
#sudo chown $(id -u):$(id -g) $HOME/.kube/config

cd $WORKSPACE/deploy
kubectl apply -f deploy.yaml