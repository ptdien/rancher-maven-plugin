# rancher-maven-plugin 
Upgrade a single service in specific environment
## 1. Installation
```xml
<plugin>
    <groupId>com.github.ptdien</groupId>
    <artifactId>rancher-maven-plugin</artifactId>
    <version>0.0.4</version>
</plugin>
```

## 2. Usage:

```text
mvn -Drancher.root=string 
    -Dservice.upgrade.timeout=long 
    -Drancher.username=string 
    -Ddocker.image=string 
    -Drancher.password=string 
    rancher:run
```

## 3. What are those above?
```text
    rancher.root: Rancher service url (no protocol). Ex: 0.0.0.0:8080\v2-beta\projects\<projectId>\services\<serviceId>?action=.
    service.upgrade.timeout: Timeout if upgrading task takes too long. Ex: 60000 (ms).
    rancher.username: Your environment accessKey. (API -> Keys -> Add evironment API key).
    rancher.password: Your environment secretKey
    docker.image: Which docker image is used for upgrading this service? Ex: 0.0.0.0:9000/dockerimage:1.0.0
```
