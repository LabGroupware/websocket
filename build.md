# 手動Build手順

``` sh
./gradlew bootBuildImage --imageName=ablankz/nova-ws-service:1.0.3
docker push ablankz/nova-ws-service:1.0.3
```