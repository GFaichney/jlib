// Uses Declarative syntax to run commands inside a container.
pipeline {
    agent {
        kubernetes {
            // Rather than inline YAML, in a multibranch Pipeline you could use: yamlFile 'jenkins-pod.yaml'
            // Or, to avoid YAML:
            // containerTemplate {
            //     name 'shell'
            //     image 'ubuntu'
            //     command 'sleep'
            //     args 'infinity'
            // }
yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
    - name: my-main-container
      image: ubuntu:latest
      command:
      - sleep
      args:
      - infinity
      env:
      - name: DOCKER_HOST
        value: tcp://localhost:2375
    - name: dind
      image: docker:18.05-dind
      securityContext:
        privileged: true
      volumeMounts:
        - name: dind-storage
          mountPath: /var/lib/docker
  volumes:
    - name: dind-storage
      emptyDir: {}
'''
            // Can also wrap individual steps:
            // container('shell') {
            //     sh 'hostname'
            // }
            defaultContainer 'shell'
        }
    }
    stages {
        stage('Main') {
            steps {
                container('dind'){
                  sh 'hostname'
                  sh '''
cat <<EOF > Dockerfile
FROM ubuntu:latest
EOF
                  '''
                  sh 'docker build .'
                }
            }
        }
    }
}



