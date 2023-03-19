# Tesi

## Installing k9s
k9s is a tool to monitor the the status of the deployment and pods for kubernetes objects
```
curl -sS https://webinstall.dev/k9s | bash
```
## Helm install
- Debian OS:
    ```
    sudo apt install helm
    ```
- Fedora OS:
    ```
    sudo dnf install helm
    ```

## Strimzi, Kafka and Zookeper preparation via Helm
```
helm repo add strimzi https://strimzi.io/charts/
```
## Deploy Strimzi, Kafka and Zookeper
```
helm install -f /path/to/deploy/file1.yaml -f /path/to/deploy/file2.yaml <deploy name> /deploy/folder
```
```
helm install strimzi strimzi/strimzi-kafka-operator --version=<version>  -n <namespace>
```
## Update Deployment 
Use the same path file as `helm install` command
```
helm upgrade -f /path/to/deploy/file1.yaml -f /path/to/deploy/file2.yaml <deploy name> /deploy/folder
```