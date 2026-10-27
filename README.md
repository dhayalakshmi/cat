# java-k8s-app  (Factorial + Greatest of three)

Endpoints (port 8080)
  /                      message (APP_MESSAGE env, default "Hello from Kubernetes! (v1)")
  /factorial/5           {"number":5,"factorial":120}
  /greatest?a=3&b=9&c=5  {"greatest":9}
  /health                OK

Local run
  mvn clean package
  java -jar target/app.jar

Docker
  docker build -t DOCKERHUB_USER/java-k8s-app:v1 .
  docker run -p 8080:8080 DOCKERHUB_USER/java-k8s-app:v1

Replace DOCKERHUB_USER in k8s/*.yaml and Jenkinsfile with your Docker Hub username.

Set -> file
  3  k8s/pod.yaml
  4  k8s/deployment.yaml
  5  k8s/service.yaml
  6  deployment.yaml with replicas: 3
  7  kubectl set image deployment/java-app java-app=DOCKERHUB_USER/java-k8s-app:v2
  8  k8s/configmap.yaml + k8s/deployment-full.yaml
  9  k8s/pvc.yaml + k8s/deployment-full.yaml
  10 readiness probe in k8s/deployment-full.yaml
  11,12,13,15  Jenkinsfile
  14 k8s/deployment-faulty.yaml
Jenkins credentials needed: dockerhub-creds (username/password), kubeconfig-cred (secret file).
