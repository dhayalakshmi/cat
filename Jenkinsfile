pipeline {
  agent any
  environment {
    KUBECONFIG = credentials('kubeconfig')
    KUBECTL = '"C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\kubectl.exe"'
    DOCKER  = '"C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe"'
    IMAGE = 'java-k8s-app'
  }
  stages {
    stage('Checkout') { steps { checkout scm } }
    stage('Build Image') {
      steps { bat '%DOCKER% build -t %IMAGE%:%BUILD_NUMBER% .' }
    }
    stage('Deploy') {
      steps {
        bat '%KUBECTL% apply -f k8s\\configmap.yaml'
        bat '%KUBECTL% apply -f k8s\\deployment-full.yaml'
        bat '%KUBECTL% apply -f k8s\\service.yaml'
        bat '%KUBECTL% set image deployment/java-app java-app=%IMAGE%:%BUILD_NUMBER%'
      }
    }
  }
}
