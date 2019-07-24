#!/bin/bash
echo "ssh到k8s服务器"
ssh -tt -o ConnectTimeout=3 -o ConnectionAttempts=1 -o ServerAliveInterval=1 -o BatchMode=yes root@192.168.88.130
echo "进入部署目录"
cd /harry/deploy/project
pwd
#echo "发布"
#kubectl apply -f k8s-demo.yaml
echo "退出k8s服务器"
exit
