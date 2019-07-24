#!/bin/bash
echo "ssh到k8s服务器"
ssh -tt root@192.168.88.130 "cd /harry/deploy/project; kubectl apply -f k8s-demo.yaml"
echo "部署完成"
#cd /harry/deploy/project
#echo "发布"
#kubectl apply -f k8s-demo.yaml
#echo "退出k8s服务器"
#exit
