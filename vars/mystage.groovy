def call(){        
  stage('Echo') {
    container('dind'){
      sh 'echo Hello'
    }
  }
}

