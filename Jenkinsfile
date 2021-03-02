class Constants {

    static final String MASTER_BRANCH = 'master'

    static final String QA_BUILD = 'Debug'
    static final String RELEASE_BUILD = 'Release'

    static final String INTERNAL_TRACK = 'internal'
    static final String RELEASE_TRACK = 'release'
}

def getBuildType() {
    switch (env.BRANCH_NAME) {
        case Constants.MASTER_BRANCH:
            return Constants.RELEASE_BUILD
        default:
            return Constants.QA_BUILD
    }
}

def getTrackType() {
    switch (env.BRANCH_NAME) {
        case Constants.MASTER_BRANCH:
            return Constants.RELEASE_TRACK
        default:
            return Constants.INTERNAL_TRACK
    }
}

def isDeployCandidate() {
    return ("${env.BRANCH_NAME}" =~ /(develop|master)/)
}

// -- Directory where the Platform Tools is located
def PLATFORM_TOOL_DIRECTORY = "$ANDROID_HOME/platform-tools/"

// -- Directory where the Android Emulator is located
def EMULATOR_DIRECTORY = "$ANDROID_HOME/emulator/"

def JOB_DEVICE_NAME = "@Pixel_4_API_29"

pipeline {
    agent any

    options {
        // Stop the build early in case of compile or test failures
        skipStagesAfterUnstable()
    }

    stages {
            stage('Clean Build') {
                  steps {
                      sh './gradlew clean'
                  }
            }

    stage('Compile') {
          steps {
            // Compile the app and its dependencies
            sh './gradlew compileDevDebugSources'
          }
        }

    stage('Unit test') {
          steps {
            // Compile and run the unit tests for the app and its dependencies
            sh './gradlew testDevDebugUnitTest'

            // Analyse the test results and update the build result as appropriate
            // junit '**/TEST-*.xml'
          }
        }

    stage('UI Tests') {

        steps {
          echo "Executing ADB Server"
          // -- First, you need to go to the Platform Tool Directory.
          // -- Then run the ADB Server
          script {
              try {
                  sh """
                      cd ${PLATFORM_TOOL_DIRECTORY}
                      ./adb start-server&
                     """
              } catch (err) {
                      echo "The ADB Server is not running"
              }
          }

          echo "Starting Emulator"
          // -- First, you need to go to the Emulator Directory.
          // -- Then launch the Emulator
          script {
              try {
                  sh """
                      cd ${EMULATOR_DIRECTORY}
                      ./emulator -avd ${JOB_DEVICE_NAME} -engine auto -wipe-data -no-cache -memory 3072 -no-snapshot-save&
                      sleep 60s
                     """
              } catch (err) {
                      echo "The emulator is not open"
              }

          }

          sh "./gradlew connectedDevDebugAndroidTest"

          sh script: '/var/lib/jenkins/kill-emu.sh'

        }
    }

    stage('Build APK') {
          steps {
            // Finish building and packaging the APK
            sh './gradlew assembleDebug'

            // Archive the APKs so that they can be downloaded from Jenkins
            archiveArtifacts artifacts: '**/*.apk', fingerprint: true, onlyIfSuccessful: true
          }
        }

    stage('Publish') {

          steps {
            appCenter apiToken: 'b69c74ec4d2a76f8b40c3fb9a827fd977522767e',
                    ownerName: 'mark.yang-mintpayments.com',
                    appName: 'Weather',
                    pathToApp: '**/*-dev-DEBUG.apk',
                    distributionGroups: 'Test'
          }
        }
    }
}