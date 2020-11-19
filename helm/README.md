# Bobbycar Helm Charts

## Set up Bobbycar from the Git repo

Clone the repo

```sh
git clone https://github.com/sa-mw-dach/bobbycar.git

cd bobbycar/helm/
```

Login to the OpnShift cluster and create a new namespace

```sh
oc new-project bobbycar
```

Create an OperatorGroup Custom Resource

```sh
oc apply -f operator-group.yaml
```


Dry run and check the template
```sh
helm template bobbycar-core-infra/ --debug
```