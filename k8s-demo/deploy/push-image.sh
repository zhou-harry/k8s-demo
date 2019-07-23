#!/usr/bin/env bash
#定义变量
API_NAME="k8s-demo"
API_VERSION="0.0.1-SNAPSHOT"

#如果下面需要将镜像推送到镜像仓库，那么此处的镜像名称需要带上镜像仓库地址
IMAGE_NAME="harbor.harry.com:8015/harry/$API_NAME:$BUILD_NUMBER"

#进入target目录复制Dockerfile文件
cd $WORKSPACE/$API_NAME/target/docker

#构建docker镜像
docker build -t $IMAGE_NAME .

#登录Harbor镜像仓库（此处的仓库地址需要与上面IMAGE_NAME的仓库地址保持一致否则推送denied）
docker login -u admin -p Harbor12345 harbor.harry.com:8015

#推送docker镜像到Harbor镜像仓库
docker push $IMAGE_NAME