#!/bin/sh

if [ "${TRAVIS_PULL_REQUEST}" = "false" ]; then
  mv target/TotalFreedomMod.jar target/TotalFreedomMod-${TRAVIS_COMMIT}.jar
  export SSHPASS=${SFTP_PASSWORD}
  sshpass -e sftp -oStrictHostKeyChecking=no -oUserKnownHostsFile=/dev/null -oLogLevel=ERROR -oBatchMode=no -b - ${SFTP_USER}@${SFTP_HOST}:${SFTP_PATH} << !
    put target/TotalFreedomMod-${TRAVIS_COMMIT}.jar
    bye
!
  echo "Artifact upload status: "$?
else
  echo "Skipping artifact upload for pull request build."
fi
