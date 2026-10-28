pipeline {
  agent any
  options { skipDefaultCheckout(true) }
  environment {
    KUBECONFIG = credentials('kubeconfig')
    KUBECTL = '"C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\kubectl.exe"'
    DOCKER  = '"C:\\Users\\Admin\\AppData\\Local\\Programs\\DockerDesktop\\resources\\bin\\docker.exe"'
  }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Build and Unit Test') {
      steps { bat 'mvn -B clean test' }
      post {
        always { junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml' }
      }
    }

    stage('Docker Build') {
      steps {
        bat "%DOCKER% build -t admin/java-k8s-app:v${env.BUILD_NUMBER} ."
        bat '%DOCKER% images admin/java-k8s-app'
      }
    }

    stage('Deploy to Kubernetes') {
      steps {
        bat '%KUBECTL% apply -f k8s\\pvc.yaml'
        bat '%KUBECTL% apply -f k8s\\configmap.yaml'
        bat '%KUBECTL% apply -f k8s\\deployment-full.yaml'
        bat '%KUBECTL% apply -f k8s\\service.yaml'
        bat "%KUBECTL% set image deployment/java-app java-app=admin/java-k8s-app:v${env.BUILD_NUMBER}"
      }
    }

    stage('Verify Rollout') {
      steps {
        bat '%KUBECTL% rollout status deployment/java-app --timeout=120s'
        bat '%KUBECTL% get pods,svc'
      }
    }

    stage('Smoke Test') {
      steps {
        bat '%KUBECTL% delete pod smoke-test --ignore-not-found'
        bat '%KUBECTL% run smoke-test --restart=Never --image=curlimages/curl -- curl -s http://java-app-service/health'
        bat 'ping 127.0.0.1 -n 20 > nul'
        bat '%KUBECTL% logs smoke-test'
        bat '%KUBECTL% logs smoke-test | findstr OK'
        bat '%KUBECTL% delete pod smoke-test --ignore-not-found'
      }
    }
  }
  post {
    success { echo 'Pipeline succeeded - application deployed and healthy' }
    failure {
      echo 'Pipeline failed - rolling back'
      bat '%KUBECTL% rollout undo deployment/java-app || exit 0'
    }
  }
}
