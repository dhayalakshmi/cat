pipeline {
  agent any
  environment {
    IMAGE = 'yourname/java-k8s-app'
    TAG   = "v${BUILD_NUMBER}"
    KUBECONFIG = credentials('kubeconfig')
    KUBECTL = '"C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\kubectl.exe"'
    DOCKER  = '"C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe"'
  }
  stages {
    stage('Checkout') { steps { checkout scm } }

    stage('Build & Test') {
      steps { bat 'mvn clean test' }
      post { always { junit 'target/surefire-reports/*.xml' } }
    }

    stage('Docker Build') {
      steps { bat '%DOCKER% build -t %IMAGE%:%TAG% .' }
    }

    stage('Deploy') {
      steps {
        bat '%KUBECTL% apply -f k8s\\deployment.yaml'
        bat '%KUBECTL% apply -f k8s\\service.yaml'
        bat '%KUBECTL% set image deployment/java-app java-app=%IMAGE%:%TAG%'
      }
    }

    stage('Verify Rollout') {
      steps {
        bat '%KUBECTL% rollout status deployment/java-app --timeout=120s'
        bat '%KUBECTL% rollout history deployment/java-app'
        bat '%KUBECTL% get pods,svc'
      }
    }
  }
}
