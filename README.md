# Huatai Alluxio Proxy Server

## How to generate HTTPS certificates

```bash
$openssl genrsa -des3 -out server.key 2048
$openssl req -new -key server.key -out server.csr
$openssl x509 -req -days 365 -in server.csr -signkey server.key -out server.crt
$cat server.key > server.pem
$cat server.crt >> server.pem
$openssl pkcs12 -export -in server.pem -out keystore.pkcs12
$keytool -import -alias serverCert -file server.crt -keystore truststore_client
```
