mvn clean install -DskipDeb -DskipTests -DskipDockerTestDB -DskipDocker
cd structr-binaries
 docker build . --build-arg STRUCTR_VERSION=$(ls target/*.zip |awk -F- '{ print $2"-"$3"-"$4; }') -t structr:local
cd ..
notify-send -a Structr "Docker build finished." -t 3000
