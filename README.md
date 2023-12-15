# A Hazelcast Streaming Payments Demo
The idea is to process strraming `pain.001` payments.

### Pre-requisites
1. [Service Account](https://developers.google.com/identity/protocols/oauth2/service-account#creatinganaccount) with 
permissions to create GKE. The service account must be downloaded as a JSON file and located in `~/.gcp/credentials.json`
2. Ansible installed in local machine
3. kubectl installed in local machine
4. Helm installed in local machine
5. Hazelcast CLC installed in local machine
6. Maven installed in local machine
7. Hazelcast License key present in file `~/hazelcast/hazelcast.license` in local machine
8. Google Auth plugin installed pip - `pip3 install google-auth` in local machine
9. Install `gloud` via `brew install google-cloud-sdk` and `gke-gcloud-auth-plugin` via `gcloud components install gke-gcloud-auth-plugin` in local machine
10. A repository in [GCP Artifact Registry](https://cloud.google.com/artifact-registry/docs/docker/store-docker-container-images#linux) with 
name mapping to variable `repository_id` in region `asia-south1`. Ansible will push the docker image to this repository. Various parameters can be modified via the var files. 

### Steps
1. Start Docker on the laptop and execute `ansible-playbook k8s/deploy.yaml`.
2. You can run individual tasks by using `tags`.
For example, following commands will create the GKE cluster, populate the database, configure ssl certificates, deploy Hazelcast clusters and start Management Center.
The next command will deploy the pipeline and the next one starts producing the transactions.
    1. `ansible-playbook k8s/deploy.yaml --tags="gke,init,postgres,ssl,hz-init,hz"`
    2. `ansible-playbook k8s/deploy.yaml --tags="pipeline"`
    3. `ansible-playbook k8s/deploy.yaml --tags="tx-prod"` 
3. You may check the cluster on MC on `http://<EXTERNAL-IP>:8080` where `EXTERNAL-IP` is the external IP of the service `hazelcast-mc` service. Run `kubectl get svc` to get the IP.
4. To shutdown the cluster execute `ansible-playbook k8s/undeploy.yaml`

### Undo
1. To delete kafka execute `ansible-playbook k8s/undeploy.yaml --tags="kafka"`

### References
1. https://piotrminkowski.com/2023/11/06/apache-kafka-on-kubernetes-with-strimzi/
