#!/bin/bash
echo "ssh到k8s服务器"
ssh -tt root@192.168.88.130 "cd /harry/deploy/project; kubectl apply -f k8s-demo.yaml"
