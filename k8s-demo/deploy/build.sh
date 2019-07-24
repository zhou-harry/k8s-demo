#!/usr/bin/env bash
#定义变量
API_NAME="k8s-demo"
API_VERSION="0.0.1-SNAPSHOT"
TAG=dev-$(date +%m%d%H%M)-${BUILD_NUMBER}

#如果下面需要将镜像推送到镜像仓库，那么此处的镜像名称需要带上镜像仓库地址
IMAGE_NAME="harbor.harry.com:8015/harry/$API_NAME:$TAG"

#进入target目录复制Dockerfile文件
cd $WORKSPACE/$API_NAME/target/docker

#构建docker镜像
docker build -t $IMAGE_NAME .

#登录Harbor镜像仓库（此处的仓库地址需要与上面IMAGE_NAME的仓库地址保持一致否则推送denied）
docker login -u admin -p Harbor12345 harbor.harry.com:8015

#推送docker镜像到Harbor镜像仓库
docker push $IMAGE_NAME

#将部署文件发送到k8s服务器
cd $WORKSPACE/$API_NAME/deploy
scp template/$API_NAME.yaml .
sed -i "s@v1.0.0@$TAG@g" $API_NAME.yaml
scp $API_NAME.yaml deploy.sh 192.168.88.130:/harry/deploy/project