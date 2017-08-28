FROM java:8

WORKDIR /root
#WORKDIR c:/main/projects/glowing-palm-tree/target

COPY highload-cup-0.9-fat.jar /root/

EXPOSE 80

#CMD java -server -Xcomp -XX:CompileThreshold=20 -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+OptimizeStringConcat -XX:+EliminateAutoBox -jar /root/highload-cup-0.9-fat.jar
#CMD java -jar /root/highload-cup-0.9-fat.jar
CMD java -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+OptimizeStringConcat -XX:+EliminateAutoBox -server -jar /root/highload-cup-0.9-fat.jar
#CMD java -XX:+AggressiveOpts -XX:+DoEscapeAnalysis -XX:+OptimizeStringConcat -XX:+EliminateAutoBox -jar /root/highload-cup-0.9-fat.jar
