# Bobbycar Zone Configuration

Bobbycar zones are configured through Custom Resources.

```sh
git clone https://github.com/sa-mw-dach/bobbycar
```

You need cluster-admin privileges in order to install CRDs. The following command installs the BobbycarZones CRD and a sample Zone CR

```sh
oc apply -k config/
```

Check if you can read the CR (shortname for kind BobbycarZone is bz)

```sh
oc get bz bobbycar-ffm -o json
```

The CRD extends the Kubernetes API as follows:

**/apis/bobbycar.redhat.com/v1alpha1/namespaces/\*/zones/**

Test the API extension with:

```sh
curl -k -H "Authorization: Bearer yourtoken" "https://api.ocp3.stormshift.coe.muc.redhat.com:6443/apis/bobbycar.redhat.com/v1alpha1/namespaces/bobbycar/zones/"
```

or

```sh
curl -k -H "Authorization: Bearer yourtoken" "https://api.ocp3.stormshift.coe.muc.redhat.com:6443/apis/bobbycar.redhat.com/v1alpha1/namespaces/bobbycar/zones/bobbycar-ffm"
```

```shell
oc adm policy add-cluster-role-to-user cluster-monitoring-view -z infinispan-monitoring

oc serviceaccounts get-token infinispan-monitoring
```