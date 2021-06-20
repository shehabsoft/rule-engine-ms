#!/usr/bin/env groovy


node {


    stage('checkout') {
        checkout scm
    }

    gitlabCommitStatus('build') {
        stage('check java') {
            sh "java -version"
        }

        stage('clean') {
            sh "chmod +x gradlew"
            sh "./gradlew clean --no-daemon"
        }

        stage('nohttp') {
            sh "./gradlew checkstyleNohttp --no-daemon"
        }

//comment till set correct enviroment
//        stage('backend tests') {
//           try {
//                sh "./gradlew test integrationTest -PnodeInstall --no-daemon"
//            } catch(err) {
//                throw err
//            } finally {
//               junit '**/build/**/TEST-*.xml'
//            }
//        }

        stage('packaging') {
            sh "./gradlew -Pprod bootJar jibDockerBuild "
            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
        }

        stage('docker build') {
		    sh "./gradlew --stop"
            sh "./gradlew bootJar -x test -Pprod jibDockerBuild"
        }

        stage('docker push') {
            sh "docker image tag ruleenginems nexus.diracatom.com:18444/ruleenginems"
	    sh "docker login -u admin -p byszLQH9PWkFzag nexus.diracatom.com:18444"
            sh "docker push nexus.diracatom.com:18444/ruleenginems"
        }



    }
}
