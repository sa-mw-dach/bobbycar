# Installing Bobbycar HDPP

1. Create a new OpenShift project

````shell
oc new-project bobbycar-hdpp
````

2. Install the Human Driving Perception platform

`````shell
oc apply -f config/hdpp/hdpp-deployment.yaml
````` 

3. Scale down the hdpp deployment

`````shell
oc scale deployment/ntt-in-vehicle-detectionapplication --replicas=0
`````

5. Create a basic deployment in order to copy the data to the PVC:

`````shell
oc new-app registry.redhat.io/openshift4/ose-hello-openshift-rhel8
`````

6. Mount the `vehicle-detection` PVC to the `ose-hello-openshift-rhel8 ` deployment.
   Example mount path: `/tmp/vhd`


7. And copy 3 data files to the PVC:
    - can_data.csv
    - front_cam_driver.avi
    - front_cam_road.avi

`````shell
oc rsync ./ ose-hello-openshift-rhel8-5464985fbd-nrpvk:/tmp/vhd
`````
8. Configure the exposed route for the `ntt-in-vehicle-detectionapplication` in the `vehicle-application` configMap -> `init.json` -> `stream_endpoint`

