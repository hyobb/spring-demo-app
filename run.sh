
(sleep 30; gradle buildAndReload --continuous -PskipDownload=true -x Test)&
# src/main/resources/application-development.yml을 실행
gradle bootRun -PskipDownload=true -Dspring.profiles.active=development