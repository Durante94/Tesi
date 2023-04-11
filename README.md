# Tesi

## LOGIN AS ROOT USER!
```
su root
```
If you cannot acces, you must add a password for the root user:
```
passwd root
```
Then repeat the first command
## Installing k0s
k0s is a tool that help the user with the deploy of kubernetes objects
```
curl -sSLf https://get.k0s.hs | sudo sh
```
Create a cofniguration for k0s that use calico as a dns resolver
```
cd /etc/k0s/; 
touch k0s.yaml;
k0s config create > k0s.yaml;
nano k0s.yaml
```
Then alter the following properties
```
spec.network.provider: kuberouter -> calico
spec.extension.storage.type: standard -> openebs_local_storage
```
Then delete the following properties
```
spec.extension.storage.create_default_storage_class
```
Execute a clean installation of k0s
```
k0s stop; 
k0s reset;
k0s install controller --single --enable-worker;
```
- OPTIONAL: add alias for ```k0s kubectl``` command
    ```
    cd ~;
    nano .bashrc
    ````
    Add the following alias as:
    ```
    alias kc=k0s kubectl
    ````
Finally
```
kc config view -o yaml --raw > /k0s/config.yaml
```
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
## Uninstall Deployment 
```
helm uninstall <deploy name>
```