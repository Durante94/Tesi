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
## Kubernetes install
- Debian OS:
    ```
    snap install kubectl --classic
    ```
- Fedora OS:
    ```
    sudo dnf install kubectl
    ```
## Installing k0s
k0s is a lightweight, certified, and all-inclusive Kubernetes distribution that help the user with the deploy of kubernetes objects
```
curl -sSLf https://get.k0s.sh | sudo sh
```
Create a configuration for k0s that use calico as a dns resolver
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
If the above command retrieve a empty config file, use this one as fallback:
```
k0s kubeconfig admin > /k0s/config.yaml
```
Last configuration command:
```
export KUBECONFIG=/k0s/config.yaml
```
## Installing k9s
k9s is a tool to monitor the the status of the deployment and pods for kubernetes objects
```
curl -sS https://webinstall.dev/k9s | bash
source /root/.config/envman/PATH.env
```
## Helm install
- Debian OS:
    ```
    snap install helm --classic
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
## Deploy InfluxDb
Namespace creation, if there are not deployment in the db namespace yet
```
kc apply -f databases/templates/influxdb/influx.yaml -n influx
```
## Deploy Telegraf
Follow the [official guide](https://github.com/influxdata/helm-charts/tree/master/charts/telegraf)
## Restart pod per modifiche
Quando modifichiamo config-map o pvc, riavviamo i pod
```
kc delete po <pod-name> -n <namespace>
```
