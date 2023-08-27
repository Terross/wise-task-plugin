FROM gradle:8.0.2-jdk19
COPY . .
EXPOSE 6565
CMD sh run.sh