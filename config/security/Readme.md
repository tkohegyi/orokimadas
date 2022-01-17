This is only for testing/demo purposes.
Our keystore and jetty entry password is the same: Rf7856op

Steps:
1. Generate initial keystore (keytool):
keytool -genkey -alias server-alias -keyalg RSA -keypass Rf7856op -storepass Rf7856op -keystore adorApp.jks

2. Generate PKCS version  (keytool)
keytool -importkeystore -srckeystore adorApp.jks -destkeystore adorApp2.jks -deststoretype pkcs12

3. Generate crt file from keystore:
winpty openssl pkcs12 -in adorApp2.jks -nokeys -out adorApp2.crt

4. Open crt file in Windows Explorer - Export to file as DEC type adorApp.cer

5. Finalize comment
cp adorApp2.jks adorApp.keystore
and remove every files except adorApp.keystore and adorApp.cer

6. Search for jetty-util-*.jar file in Gradle's local lib cache.
Create OBF password to be placed in jetty setup java:
java -cp jetty-util-9.2.27.v20190403.jar org.eclipse.jetty.util.security.Password Rf7856op
place OBF part into WebAppServer.java
(+ build/release)

7. set property useHttps=true in xxx.conf.properties

+1. In case you are using local App server, you may instruct Chrome to accept incesure localhost calls:
chrome://flags/#allow-insecure-localhost

